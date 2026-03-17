package com.logistics.domain.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate root representing a logistics company.
 *
 * A Company owns a set of ConsumptionSite objects and may have one or more
 * LogisticsCenter candidates calculated via the barycenter engine.
 *
 * Domain invariants enforced here:
 *   - name is never null or blank.
 *   - id is immutable once assigned.
 *   - consumption sites are owned by exactly one company.
 *
 * Status lifecycle:
 *   PENDING -> ACTIVE -> INACTIVE
 *          -> INACTIVE (direct deactivation from any non-deleted state)
 *
 * Corresponds to the view-model {@link com.logistics.ui.screens.CompanyListScreen.CompanyRow}
 * — the CompanyViewModelMapper translates between the two.
 */
public class Company {

    // -------------------------------------------------------------------------
    // Status enum
    // -------------------------------------------------------------------------

    /**
     * Mirrors {@link com.logistics.ui.components.StatusBadge.EntityStatus} values
     * for company lifecycle management.
     */
    public enum Status {
        ACTIVE,
        INACTIVE,
        PENDING
    }

    /**
     * Company classification from the perspective of the logistics network —
     * matches the filter options in CompanyListScreen.
     */
    public enum Type {
        SHIPPER,
        CARRIER,
        BOTH
    }

    // -------------------------------------------------------------------------
    // Identity and scalar fields
    // -------------------------------------------------------------------------

    private final String id;
    private String       name;
    private Type         type;
    private Status       status;
    private String       taxId;

    // -------------------------------------------------------------------------
    // Contact information
    // -------------------------------------------------------------------------

    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private String notes;

    // -------------------------------------------------------------------------
    // Audit
    // -------------------------------------------------------------------------

    private final Instant createdAt;
    private Instant       updatedAt;

    // -------------------------------------------------------------------------
    // Owned collections (managed as unmodifiable views externally)
    // -------------------------------------------------------------------------

    private final List<ConsumptionSite>  consumptionSites  = new ArrayList<>();
    private final List<LogisticsCenter>  logisticsCenters  = new ArrayList<>();

    // -------------------------------------------------------------------------
    // Constructor — used by application layer and persistence mapper
    // -------------------------------------------------------------------------

    /**
     * Full constructor used when reconstituting a Company from persistence or
     * when the application service creates a new one.
     *
     * @param id    unique identifier (UUID string); pass null to auto-generate
     * @param name  company display name, must not be blank
     * @param type  shipper / carrier / both
     * @param status initial lifecycle status
     */
    public Company(String id, String name, Type type, Status status) {
        this.id        = id != null ? id : UUID.randomUUID().toString();
        this.name      = requireNonBlank(name, "Company name");
        this.type      = Objects.requireNonNull(type, "Company type must not be null");
        this.status    = Objects.requireNonNull(status, "Company status must not be null");
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    // -------------------------------------------------------------------------
    // Domain behaviour
    // -------------------------------------------------------------------------

    /**
     * Activates this company.
     * No-op if already ACTIVE.
     */
    public void activate() {
        if (status != Status.ACTIVE) {
            status    = Status.ACTIVE;
            updatedAt = Instant.now();
        }
    }

    /**
     * Deactivates this company.
     * No-op if already INACTIVE.
     */
    public void deactivate() {
        if (status != Status.INACTIVE) {
            status    = Status.INACTIVE;
            updatedAt = Instant.now();
        }
    }

    /**
     * Adds a consumption site to this company's logistics network.
     *
     * @param site the site to add; must not be null
     * @throws IllegalArgumentException if the site already belongs to another company
     */
    public void addConsumptionSite(ConsumptionSite site) {
        Objects.requireNonNull(site, "ConsumptionSite must not be null");
        if (site.getCompanyId() != null && !site.getCompanyId().equals(this.id)) {
            throw new IllegalArgumentException(
                    "ConsumptionSite already belongs to company " + site.getCompanyId());
        }
        site.assignToCompany(this.id);
        consumptionSites.add(site);
        updatedAt = Instant.now();
    }

    /**
     * Removes a consumption site by its ID.
     *
     * @param siteId the site to remove
     * @return true if the site was found and removed
     */
    public boolean removeConsumptionSite(String siteId) {
        boolean removed = consumptionSites.removeIf(s -> s.getId().equals(siteId));
        if (removed) updatedAt = Instant.now();
        return removed;
    }

    /**
     * Records a newly calculated logistics center for this company.
     *
     * @param center the logistics center result from the barycenter engine
     */
    public void addLogisticsCenter(LogisticsCenter center) {
        Objects.requireNonNull(center, "LogisticsCenter must not be null");
        logisticsCenters.add(center);
        updatedAt = Instant.now();
    }

    /**
     * Returns the total traffic volume across all active consumption sites.
     *
     * @return sum in tons
     */
    public double totalTrafficTons() {
        return consumptionSites.stream()
                .mapToDouble(s -> s.getTrafficVolume().tons())
                .sum();
    }

    // -------------------------------------------------------------------------
    // Update methods
    // -------------------------------------------------------------------------

    public void updateName(String name) {
        this.name      = requireNonBlank(name, "Company name");
        this.updatedAt = Instant.now();
    }

    public void updateType(Type type) {
        this.type      = Objects.requireNonNull(type);
        this.updatedAt = Instant.now();
    }

    public void updateContactInfo(String contactName, String contactEmail, String contactPhone) {
        this.contactName  = contactName;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.updatedAt    = Instant.now();
    }

    public void updateTaxId(String taxId) {
        this.taxId     = taxId;
        this.updatedAt = Instant.now();
    }

    public void updateNotes(String notes) {
        this.notes     = notes;
        this.updatedAt = Instant.now();
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public String  getId()           { return id; }
    public String  getName()         { return name; }
    public Type    getType()         { return type; }
    public Status  getStatus()       { return status; }
    public String  getTaxId()        { return taxId; }
    public String  getContactName()  { return contactName; }
    public String  getContactEmail() { return contactEmail; }
    public String  getContactPhone() { return contactPhone; }
    public String  getNotes()        { return notes; }
    public Instant getCreatedAt()    { return createdAt; }
    public Instant getUpdatedAt()    { return updatedAt; }

    /** Returns an unmodifiable view of the owned consumption sites. */
    public List<ConsumptionSite> getConsumptionSites() {
        return Collections.unmodifiableList(consumptionSites);
    }

    /** Returns an unmodifiable view of the owned logistics centers. */
    public List<LogisticsCenter> getLogisticsCenters() {
        return Collections.unmodifiableList(logisticsCenters);
    }

    // -------------------------------------------------------------------------
    // equals / hashCode — identity based on id
    // -------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Company c)) return false;
        return id.equals(c.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Company{id='" + id + "', name='" + name + "', status=" + status + "}";
    }

    // -------------------------------------------------------------------------
    // Guard
    // -------------------------------------------------------------------------

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
