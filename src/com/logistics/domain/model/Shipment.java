package com.logistics.domain.model;

import com.logistics.domain.vo.GeoCoordinate;
import com.logistics.domain.vo.TrafficVolume;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a single shipment moving cargo between two locations.
 *
 * Shipments contribute to the traffic volume data used by the barycenter engine:
 * the engine aggregates completed and active shipments per consumption site to
 * derive the tonnage weights. The {@code TrafficVolume} field represents the
 * actual cargo weight for this shipment.
 *
 * Event log:
 *   Each status transition appends a {@link ShipmentEvent} to the immutable
 *   event log. This provides the full audit trail displayed in SCR-007 and
 *   feeds the activity feed on the Dashboard.
 *
 * Lifecycle:
 *   PENDING → IN_TRANSIT → DELIVERED
 *   PENDING → CANCELLED
 *   IN_TRANSIT → OVERDUE  (set by a scheduled check)
 *   OVERDUE  → DELIVERED  (late delivery still counts)
 */
public class Shipment {

    // -------------------------------------------------------------------------
    // Status — mirrors StatusBadge.ShipmentStatus
    // -------------------------------------------------------------------------

    public enum Status {
        PENDING,
        IN_TRANSIT,
        DELIVERED,
        OVERDUE,
        CANCELLED
    }

    // -------------------------------------------------------------------------
    // Nested event record
    // -------------------------------------------------------------------------

    /**
     * Immutable log entry appended on every status transition.
     * Drives the event log table in the Shipment Detail screen (SCR-007).
     */
    public record ShipmentEvent(
            Instant timestamp,
            String  description,
            String  locationName,
            Status  newStatus) {}

    // -------------------------------------------------------------------------
    // Identity
    // -------------------------------------------------------------------------

    private final String id;          // e.g. "SHP-1823"
    private final String companyId;

    // -------------------------------------------------------------------------
    // Route
    // -------------------------------------------------------------------------

    private final String        originLocationId;
    private final String        destinationLocationId;
    private final GeoCoordinate originCoordinate;
    private final GeoCoordinate destinationCoordinate;
    private final String        originName;
    private final String        destinationName;

    // -------------------------------------------------------------------------
    // Cargo
    // -------------------------------------------------------------------------

    private final TrafficVolume cargoWeight;   // contribution to traffic volume
    private       double        volumeM3;
    private       String        cargoCategory;
    private       double        declaredValueUsd;
    private       boolean       insured;

    // -------------------------------------------------------------------------
    // Scheduling
    // -------------------------------------------------------------------------

    private final Instant scheduledPickup;
    private       Instant eta;
    private       Instant actualDelivery;

    // -------------------------------------------------------------------------
    // Status and audit
    // -------------------------------------------------------------------------

    private Status             status;
    private final List<ShipmentEvent> eventLog = new ArrayList<>();
    private final Instant             createdAt;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public Shipment(String id,
                    String companyId,
                    String originLocationId,
                    String destinationLocationId,
                    GeoCoordinate originCoordinate,
                    GeoCoordinate destinationCoordinate,
                    String originName,
                    String destinationName,
                    TrafficVolume cargoWeight,
                    Instant scheduledPickup) {
        this.id                    = id != null ? id : "SHP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.companyId             = Objects.requireNonNull(companyId);
        this.originLocationId      = Objects.requireNonNull(originLocationId);
        this.destinationLocationId = Objects.requireNonNull(destinationLocationId);
        this.originCoordinate      = Objects.requireNonNull(originCoordinate);
        this.destinationCoordinate = Objects.requireNonNull(destinationCoordinate);
        this.originName            = Objects.requireNonNull(originName);
        this.destinationName       = Objects.requireNonNull(destinationName);
        this.cargoWeight           = Objects.requireNonNull(cargoWeight);
        this.scheduledPickup       = Objects.requireNonNull(scheduledPickup);
        this.status                = Status.PENDING;
        this.createdAt             = Instant.now();

        recordEvent("Shipment created and queued for pickup.", originName, Status.PENDING);
    }

    // -------------------------------------------------------------------------
    // Domain behaviour — lifecycle transitions
    // -------------------------------------------------------------------------

    /** Marks shipment as picked up and in transit. */
    public void dispatch(String locationName) {
        requireStatus(Status.PENDING, "dispatch");
        status = Status.IN_TRANSIT;
        recordEvent("Picked up / Departed", locationName, Status.IN_TRANSIT);
    }

    /** Records arrival at an intermediate waypoint without completing delivery. */
    public void arriveAtWaypoint(String locationName) {
        requireStatus(Status.IN_TRANSIT, "waypoint arrival");
        recordEvent("Arrived at transit", locationName, Status.IN_TRANSIT);
    }

    /** Records departure from an intermediate waypoint. */
    public void departWaypoint(String locationName) {
        requireStatus(Status.IN_TRANSIT, "waypoint departure");
        recordEvent("Departed transit", locationName, Status.IN_TRANSIT);
    }

    /** Marks the shipment as delivered at its final destination. */
    public void deliver(String locationName) {
        if (status != Status.IN_TRANSIT && status != Status.OVERDUE) {
            throw new IllegalStateException(
                    "Cannot deliver a shipment in status " + status);
        }
        status          = Status.DELIVERED;
        actualDelivery  = Instant.now();
        recordEvent("Delivered", locationName, Status.DELIVERED);
    }

    /** Flags the shipment as overdue (called by a scheduled monitoring job). */
    public void markOverdue() {
        requireStatus(Status.IN_TRANSIT, "mark overdue");
        status = Status.OVERDUE;
        recordEvent("Shipment is overdue — ETA exceeded.", destinationName, Status.OVERDUE);
    }

    /** Cancels the shipment. */
    public void cancel(String reason) {
        if (status == Status.DELIVERED) {
            throw new IllegalStateException("Cannot cancel a delivered shipment.");
        }
        status = Status.CANCELLED;
        recordEvent("Cancelled: " + reason, originName, Status.CANCELLED);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void requireStatus(Status required, String action) {
        if (status != required) {
            throw new IllegalStateException(
                    "Cannot perform '" + action + "' when status is " + status
                    + " (expected " + required + ")");
        }
    }

    private void recordEvent(String description, String locationName, Status newStatus) {
        eventLog.add(new ShipmentEvent(Instant.now(), description, locationName, newStatus));
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public String              getId()                    { return id; }
    public String              getCompanyId()             { return companyId; }
    public String              getOriginLocationId()      { return originLocationId; }
    public String              getDestinationLocationId() { return destinationLocationId; }
    public GeoCoordinate       getOriginCoordinate()      { return originCoordinate; }
    public GeoCoordinate       getDestinationCoordinate() { return destinationCoordinate; }
    public String              getOriginName()            { return originName; }
    public String              getDestinationName()       { return destinationName; }
    public TrafficVolume       getCargoWeight()           { return cargoWeight; }
    public double              getVolumeM3()              { return volumeM3; }
    public String              getCargoCategory()         { return cargoCategory; }
    public double              getDeclaredValueUsd()      { return declaredValueUsd; }
    public boolean             isInsured()                { return insured; }
    public Instant             getScheduledPickup()       { return scheduledPickup; }
    public Instant             getEta()                   { return eta; }
    public Instant             getActualDelivery()        { return actualDelivery; }
    public Status              getStatus()                { return status; }
    public Instant             getCreatedAt()             { return createdAt; }
    public List<ShipmentEvent> getEventLog()              { return Collections.unmodifiableList(eventLog); }

    /** True if the shipment was delivered after its ETA. */
    public boolean isLate() {
        return status == Status.DELIVERED && eta != null && actualDelivery != null
                && actualDelivery.isAfter(eta);
    }

    // -------------------------------------------------------------------------
    // Setters for supplementary fields
    // -------------------------------------------------------------------------

    public void setVolumeM3(double volumeM3)                    { this.volumeM3       = volumeM3; }
    public void setCargoCategory(String cargoCategory)          { this.cargoCategory  = cargoCategory; }
    public void setDeclaredValueUsd(double declaredValueUsd)    { this.declaredValueUsd = declaredValueUsd; }
    public void setInsured(boolean insured)                     { this.insured        = insured; }
    public void setEta(Instant eta)                             { this.eta            = eta; }

    // -------------------------------------------------------------------------
    // equals / hashCode
    // -------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shipment s)) return false;
        return id.equals(s.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }

    @Override
    public String toString() {
        return "Shipment{id='" + id + "', " + originName + " -> " + destinationName
                + ", status=" + status + "}";
    }
}
