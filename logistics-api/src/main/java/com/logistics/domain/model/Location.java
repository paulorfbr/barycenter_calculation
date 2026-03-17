package com.logistics.domain.model;

import com.logistics.domain.vo.GeoCoordinate;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a physical logistics location (warehouse, hub,
 * airport cargo terminal, seaport, depot, or a logistics center).
 *
 * This entity backs the LocationListScreen table and map view. The view-model
 * mapper converts it to {@link com.logistics.ui.screens.LocationListScreen.LocationRow}
 * for display, and to a Leaflet pin specification for the map.
 *
 * Type taxonomy matches the filter options in LocationListScreen:
 *   WAREHOUSE, HUB, AIRPORT, PORT, DEPOT, LOGISTICS_CENTER
 *
 * Status values match the StatusBadge options in the same screen:
 *   ACTIVE, INACTIVE, MAINTENANCE
 */
public class Location {

    // -------------------------------------------------------------------------
    // Type
    // -------------------------------------------------------------------------

    public enum Type {
        WAREHOUSE,
        HUB,
        AIRPORT,
        PORT,
        DEPOT,
        LOGISTICS_CENTER    // result of a barycenter calculation
    }

    // -------------------------------------------------------------------------
    // Status — matches LocationListScreen filter and StatusBadge.EntityStatus
    // -------------------------------------------------------------------------

    public enum Status {
        ACTIVE,
        INACTIVE,
        MAINTENANCE
    }

    // -------------------------------------------------------------------------
    // Identity
    // -------------------------------------------------------------------------

    private final String id;
    private final String companyId;

    // -------------------------------------------------------------------------
    // Descriptive fields
    // -------------------------------------------------------------------------

    private String name;
    private Type   type;
    private Status status;

    // -------------------------------------------------------------------------
    // Geographic data
    // -------------------------------------------------------------------------

    private GeoCoordinate coordinate;
    private String        address;
    private String        city;
    private String        stateRegion;
    private String        country;

    // -------------------------------------------------------------------------
    // Operational metadata
    // -------------------------------------------------------------------------

    private String operatingHours;
    private String notes;

    /**
     * If this location represents an approved logistics center result,
     * this field references the source LogisticsCenter entity.
     */
    private String logisticsCenterId;

    // -------------------------------------------------------------------------
    // Audit
    // -------------------------------------------------------------------------

    private final Instant createdAt;
    private Instant       updatedAt;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public Location(String id,
                    String companyId,
                    String name,
                    Type type,
                    GeoCoordinate coordinate) {
        this.id         = id != null ? id : "LOC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.companyId  = Objects.requireNonNull(companyId, "companyId must not be null");
        this.name       = requireNonBlank(name, "Location name");
        this.type       = Objects.requireNonNull(type, "Location type must not be null");
        this.coordinate = Objects.requireNonNull(coordinate, "coordinate must not be null");
        this.status     = Status.ACTIVE;
        this.createdAt  = Instant.now();
        this.updatedAt  = this.createdAt;
    }

    // -------------------------------------------------------------------------
    // Domain behaviour
    // -------------------------------------------------------------------------

    public void activate() {
        status    = Status.ACTIVE;
        updatedAt = Instant.now();
    }

    public void deactivate() {
        status    = Status.INACTIVE;
        updatedAt = Instant.now();
    }

    public void setUnderMaintenance() {
        status    = Status.MAINTENANCE;
        updatedAt = Instant.now();
    }

    public void relocate(GeoCoordinate newCoordinate) {
        this.coordinate = Objects.requireNonNull(newCoordinate);
        this.updatedAt  = Instant.now();
    }

    // -------------------------------------------------------------------------
    // Accessors / mutators
    // -------------------------------------------------------------------------

    public String        getId()                { return id; }
    public String        getCompanyId()         { return companyId; }
    public String        getName()              { return name; }
    public Type          getType()              { return type; }
    public Status        getStatus()            { return status; }
    public GeoCoordinate getCoordinate()        { return coordinate; }
    public String        getAddress()           { return address; }
    public String        getCity()              { return city; }
    public String        getStateRegion()       { return stateRegion; }
    public String        getCountry()           { return country; }
    public String        getOperatingHours()    { return operatingHours; }
    public String        getNotes()             { return notes; }
    public String        getLogisticsCenterId() { return logisticsCenterId; }
    public Instant       getCreatedAt()         { return createdAt; }
    public Instant       getUpdatedAt()         { return updatedAt; }

    public void setName(String name)                          { this.name            = requireNonBlank(name, "Location name"); updatedAt = Instant.now(); }
    public void setType(Type type)                            { this.type            = Objects.requireNonNull(type); updatedAt = Instant.now(); }
    public void setAddress(String address)                    { this.address         = address; updatedAt = Instant.now(); }
    public void setCity(String city)                          { this.city            = city; updatedAt = Instant.now(); }
    public void setStateRegion(String stateRegion)            { this.stateRegion     = stateRegion; updatedAt = Instant.now(); }
    public void setCountry(String country)                    { this.country         = country; updatedAt = Instant.now(); }
    public void setOperatingHours(String operatingHours)      { this.operatingHours  = operatingHours; updatedAt = Instant.now(); }
    public void setNotes(String notes)                        { this.notes           = notes; updatedAt = Instant.now(); }
    public void setLogisticsCenterId(String centerId)         { this.logisticsCenterId = centerId; updatedAt = Instant.now(); }

    // -------------------------------------------------------------------------
    // equals / hashCode
    // -------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location l)) return false;
        return id.equals(l.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }

    @Override
    public String toString() {
        return "Location{id='" + id + "', name='" + name + "', type=" + type
                + ", status=" + status + ", coord=" + coordinate + "}";
    }

    private static String requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value.trim();
    }
}
