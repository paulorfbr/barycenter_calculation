package com.logistics.company.web.dto;

import com.logistics.company.domain.model.Company;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request body for creating or updating a company.
 *
 * A single DTO is used for both POST (create) and PUT (update) requests.
 * The controller determines whether to call create or update based on
 * whether a {@code companyId} path variable is present.
 */
@Schema(description = "Request body for company creation or update")
public record CompanyRequest(

        @NotBlank(message = "Company name is required")
        @Size(max = 200, message = "Company name must not exceed 200 characters")
        @Schema(description = "Legal or trading name of the company", example = "Acme Corp")
        String name,

        @NotNull(message = "Company type is required")
        @Schema(description = "Role in the logistics network", allowableValues = {"SHIPPER", "CARRIER", "BOTH"})
        Company.Type type,

        @Size(max = 50, message = "Tax ID must not exceed 50 characters")
        @Schema(description = "Government-issued tax identifier", example = "12-3456789")
        String taxId,

        @Size(max = 200)
        @Schema(description = "Primary contact person", example = "Jane Smith")
        String contactName,

        @Email(message = "Contact email must be a valid email address")
        @Size(max = 200)
        @Schema(description = "Contact email address", example = "j@acme.com")
        String contactEmail,

        @Size(max = 50)
        @Schema(description = "Contact phone number", example = "+1-555-0100")
        String contactPhone,

        @Schema(description = "Freeform notes")
        String notes

) {}
