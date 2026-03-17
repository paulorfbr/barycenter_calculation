package com.logistics.domain.model;

import com.logistics.domain.vo.GeoCoordinate;
import com.logistics.domain.vo.TrafficVolume;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a site that consumes goods shipped through the
 * logistics network.
 *
 * A ConsumptionSite is the primary input to the barycenter calculation: each
 * site contributes its geographic position weighted by its traffic volume
 * (tons of goods shipped per period).
 *
 * Invariants:
 *   - name is never null or blank.
 *   - coordinate is always valid (enforced by GeoCoordinate constructor).
 *   - trafficVolume is always non-negative (enforced by TrafficVolume constructor).
 *   - companyId is set exactly once via assignToCompany() after the site is added
 *     to a Company aggregate.
 *
 * This entity is intentionally free of JavaFX observable properties so that it
 * can be used in service threads without Platform.runLater() concerns. The
 * ConsumptionSiteViewModelMapper converts it to a JavaFX-observable view-model
 * when the UI layer needs it.
 */
public class ConsumptionSite {

    // -------------------------------------------------------------------------
    // Identity
    // -------------------------------------------------------------------------

    private final String id;
    private String       companyId;      // set once by Company.addConsumptionSite()

    // -------------------------------------------------------------------------
    // Descriptive fields
    // -------------------------------------------------------------------------

    private String name;
    private String description;
    private String address;
    private String city;
    private String country;

    // -------------------------------------------------------------------------
    // Core logistics data
    // -------------------------------------------------------------------------

    private GeoCoordinate coordinate;
    private TrafficVolume trafficVolume;

    // -------------------------------------------------------------------------
    // Status
    // -------------------------------------------------------------------------

    public enum Status { ACTIVE, INACTIVE }

    private Status status;

    // -------------------------------------------------------------------------
    // Audit
    // -------------------------------------------------------------------------

    private final Instant createdAt;
    private Instant       updatedAt;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Creates a new ConsumptionSite.
     *
     * @param id            unique identifier; pass null to auto-generate
     * @param name          human-readable site name, must not be blank
     * @param coordinate    geographic position of this site
     * @param trafficVolume annual (or periodic) tonnage routed through this site
     */
    public ConsumptionSite(String id,
                           String name,
                           GeoCoordinate coordinate,
                           TrafficVolume trafficVolume) {
        this.id            = id != null ? id : UUID.randomUUID().toString();
        this.name          = requireNonBlank(name, "ConsumptionSite name");
        this.coordinate    = Objects.requireNonNull(coordinate, "coordinate must not be null");
        this.trafficVolume = Objects.requireNonNull(trafficVolume, "trafficVolume must not be null");
        this.status        = Status.ACTIVE;
        this.createdAt     = Instant.now();
        this.updatedAt     = this.createdAt;
    }

    // -------------------------------------------------------------------------
    // Package-visible lifecycle — called by Company aggregate only
    // -------------------------------------------------------------------------

    /**
     * Binds this site to an owning company. May be called only once.
     *
     * @param companyId the owning company's id
     * @throws IllegalStateException if the site is already assigned
     */
    void assignToCompany(String companyId) {
        if (this.companyId != null) {
            throw new IllegalStateException(
                    "ConsumptionSite '" + id + "' is already assigned to company '" + this.companyId + "'");
        }
        this.companyId = companyId;
    }

    // -------------------------------------------------------------------------
    // Domain behaviour
    // -------------------------------------------------------------------------

    /**
     * Updates the geographic position of this site.
     * Recalculating barycenter should be triggered after this change.
     *
     * @param newCoordinate the new position
     */
    public void relocate(GeoCoordinate newCoordinate) {
        this.coordinate = Objects.requireNonNull(newCoordinate);
        this.updatedAt  = Instant.now();
    }

    /**
     * Updates the traffic volume for this site.
     *
     * @param volume new tonnage
     */
    public void updateTrafficVolume(TrafficVolume volume) {
        this.trafficVolume = Objects.requireNonNull(volume);
        this.updatedAt     = Instant.now();
    }

    public void activate() {
        status    = Status.ACTIVE;
        updatedAt = Instant.now();
    }

    public void deactivate() {
        status    = Status.INACTIVE;
        updatedAt = Instant.now();
    }

    // -------------------------------------------------------------------------
    // Update helpers
    // -------------------------------------------------------------------------

    public void updateName(String name) {
        this.name      = requireNonBlank(name, "ConsumptionSite name");
        this.updatedAt = Instant.now();
    }

    public void updateAddress(String address, String city, String country) {
        this.address   = address;
        this.city      = city;
        this.country   = country;
        this.updatedAt = Instant.now();
    }

    public void updateDescription(String description) {
        this.description = description;
        this.updatedAt   = Instant.now();
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public String        getId()            { return id; }
    public String        getCompanyId()     { return companyId; }
    public String        getName()          { return name; }
    public String        getDescription()   { return description; }
    public String        getAddress()       { return address; }
    public String        getCity()          { return city; }
    public String        getCountry()       { return country; }
    public GeoCoordinate getCoordinate()    { return coordinate; }
    public TrafficVolume getTrafficVolume() { return trafficVolume; }
    public Status        getStatus()        { return status; }
    public Instant       getCreatedAt()     { return createdAt; }
    public Instant       getUpdatedAt()     { return updatedAt; }

    // -------------------------------------------------------------------------
    // equals / hashCode — identity based on id
    // -------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConsumptionSite s)) return false;
        return id.equals(s.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }

    @Override
    public String toString() {
        return "ConsumptionSite{id='" + id + "', name='" + name
                + "', tons=" + trafficVolume.tons() + "}";
    }

    // -------------------------------------------------------------------------
    // Guard
    // -------------------------------------------------------------------------

    private static String requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value.trim();
    }
}
