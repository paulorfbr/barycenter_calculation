package com.logistics.company.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * JPA entity mapping the {@code companies} table in the Company Service schema.
 *
 * Intentionally separate from the domain {@code Company} class to maintain
 * hexagonal architecture: JPA annotations live only here, never in the domain.
 */
@Entity
@Table(
    name = "companies",
    indexes = {
        @Index(name = "idx_companies_status",  columnList = "status"),
        @Index(name = "idx_companies_name",    columnList = "name")
    }
)
public class CompanyEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private CompanyTypeEnum type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private CompanyStatusEnum status;

    @Column(name = "tax_id", length = 50)
    private String taxId;

    @Column(name = "contact_name", length = 200)
    private String contactName;

    @Column(name = "contact_email", length = 200)
    private String contactEmail;

    @Column(name = "contact_phone", length = 50)
    private String contactPhone;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // --- Enums embedded in entity to avoid polluting domain layer ---

    public enum CompanyTypeEnum   { SHIPPER, CARRIER, BOTH }
    public enum CompanyStatusEnum { ACTIVE, INACTIVE, PENDING }

    // --- constructors ---

    public CompanyEntity() {}

    // --- accessors (getters and setters for JPA and MapStruct) ---

    public String             getId()           { return id; }
    public void               setId(String id)  { this.id = id; }

    public String             getName()          { return name; }
    public void               setName(String v)  { this.name = v; }

    public CompanyTypeEnum    getType()          { return type; }
    public void               setType(CompanyTypeEnum v) { this.type = v; }

    public CompanyStatusEnum  getStatus()        { return status; }
    public void               setStatus(CompanyStatusEnum v) { this.status = v; }

    public String             getTaxId()         { return taxId; }
    public void               setTaxId(String v) { this.taxId = v; }

    public String             getContactName()   { return contactName; }
    public void               setContactName(String v)  { this.contactName = v; }

    public String             getContactEmail()  { return contactEmail; }
    public void               setContactEmail(String v) { this.contactEmail = v; }

    public String             getContactPhone()  { return contactPhone; }
    public void               setContactPhone(String v) { this.contactPhone = v; }

    public String             getNotes()         { return notes; }
    public void               setNotes(String v) { this.notes = v; }

    public Instant            getCreatedAt()     { return createdAt; }
    public Instant            getUpdatedAt()     { return updatedAt; }
}
