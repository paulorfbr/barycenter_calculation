package com.logistics.company.web.dto;

import com.logistics.company.domain.model.Company;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Response body for company read operations.
 *
 * Exposes only the fields that external clients need, hiding internal
 * audit fields that are irrelevant to the API contract.
 */
@Schema(description = "Company resource representation")
public record CompanyResponse(

        @Schema(description = "Unique company identifier (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "Legal or trading name", example = "Acme Corp")
        String name,

        @Schema(description = "Role in the logistics network", allowableValues = {"SHIPPER", "CARRIER", "BOTH"})
        String type,

        @Schema(description = "Lifecycle status", allowableValues = {"ACTIVE", "INACTIVE", "PENDING"})
        String status,

        @Schema(description = "Government-issued tax identifier", example = "12-3456789")
        String taxId,

        @Schema(description = "Primary contact person", example = "Jane Smith")
        String contactName,

        @Schema(description = "Contact email address", example = "j@acme.com")
        String contactEmail,

        @Schema(description = "Contact phone number", example = "+1-555-0100")
        String contactPhone,

        @Schema(description = "Freeform notes")
        String notes,

        @Schema(description = "ISO-8601 creation timestamp")
        Instant createdAt,

        @Schema(description = "ISO-8601 last-updated timestamp")
        Instant updatedAt

) {
    /** Factory method to map from domain model. */
    public static CompanyResponse from(Company c) {
        return new CompanyResponse(
                c.getId(), c.getName(), c.getType().name(), c.getStatus().name(),
                c.getTaxId(), c.getContactName(), c.getContactEmail(),
                c.getContactPhone(), c.getNotes(), c.getCreatedAt(), c.getUpdatedAt());
    }
}
