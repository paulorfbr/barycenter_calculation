package com.logistics.company.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Company aggregate root for the Company Service bounded context.
 *
 * This is a pure domain object — it carries no JPA annotations.  Persistence
 * concerns are handled by {@code CompanyEntity} in the infrastructure layer
 * and mapped via MapStruct.  This separation keeps the domain free of
 * framework dependencies and makes unit testing straightforward.
 *
 * Status lifecycle:
 *   PENDING -> ACTIVE -> INACTIVE
 */
public class Company {

    public enum Status { ACTIVE, INACTIVE, PENDING }

    public enum Type { SHIPPER, CARRIER, BOTH }

    // --- identity ---
    private final String  id;
    private String        name;
    private Type          type;
    private Status        status;
    private String        taxId;

    // --- contact ---
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private String notes;

    // --- audit ---
    private final Instant createdAt;
    private Instant       updatedAt;

    /**
     * Creates a new Company aggregate.
     *
     * @param id     unique identifier — null to auto-generate
     * @param name   display name, must not be blank
     * @param type   SHIPPER | CARRIER | BOTH
     * @param status initial lifecycle status
     */
    public Company(String id, String name, Type type, Status status) {
        this.id        = id != null ? id : UUID.randomUUID().toString();
        this.name      = requireNonBlank(name, "Company name");
        this.type      = Objects.requireNonNull(type,   "Company type");
        this.status    = Objects.requireNonNull(status, "Company status");
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    // --- domain behaviour ---

    public void activate() {
        if (status != Status.ACTIVE) {
            status = Status.ACTIVE;
            touch();
        }
    }

    public void deactivate() {
        if (status != Status.INACTIVE) {
            status = Status.INACTIVE;
            touch();
        }
    }

    public void updateName(String name) {
        this.name = requireNonBlank(name, "Company name");
        touch();
    }

    public void updateType(Type type) {
        this.type = Objects.requireNonNull(type);
        touch();
    }

    public void updateTaxId(String taxId) {
        this.taxId = taxId;
        touch();
    }

    public void updateContactInfo(String contactName, String contactEmail, String contactPhone) {
        this.contactName  = contactName;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        touch();
    }

    public void updateNotes(String notes) {
        this.notes = notes;
        touch();
    }

    // --- accessors ---

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

    // --- equality by identity ---

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Company c)) return false;
        return id.equals(c.id);
    }
    @Override public int    hashCode() { return id.hashCode(); }
    @Override public String toString()  { return "Company{id='" + id + "', name='" + name + "', status=" + status + "}"; }

    // --- helpers ---

    private void touch() { this.updatedAt = Instant.now(); }

    private static String requireNonBlank(String v, String field) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException(field + " must not be blank");
        return v.trim();
    }
}
