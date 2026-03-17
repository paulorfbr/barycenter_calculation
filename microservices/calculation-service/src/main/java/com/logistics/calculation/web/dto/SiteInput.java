package com.logistics.calculation.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * A single consumption site input for an inline calculation request.
 *
 * Also used as the response DTO when listing input sites in the result,
 * enriched with a {@code distanceToOptimalKm} field.
 */
@Schema(description = "A single consumption site contributing to the barycenter calculation")
public record SiteInput(

        @NotBlank(message = "Site ID is required")
        @Schema(description = "Site identifier (UUID or a client-generated preview ID)",
                example = "site-001")
        String siteId,

        @Schema(description = "Human-readable site name", example = "LAX Cargo Terminal")
        String siteName,

        @DecimalMin(value = "-90.0",  message = "Latitude must be in [-90, 90]")
        @DecimalMax(value = "90.0",   message = "Latitude must be in [-90, 90]")
        @Schema(description = "WGS-84 latitude in decimal degrees", example = "33.9425")
        double latitude,

        @DecimalMin(value = "-180.0", message = "Longitude must be in [-180, 180]")
        @DecimalMax(value = "180.0",  message = "Longitude must be in [-180, 180]")
        @Schema(description = "WGS-84 longitude in decimal degrees", example = "-118.4081")
        double longitude,

        @Positive(message = "Weight must be positive")
        @Schema(description = "Annual (or periodic) traffic volume through this site in metric tons",
                example = "610.0")
        double weightTons,

        @Schema(description = "Haversine distance from this site to the calculated optimal position (km) — populated in responses",
                accessMode = Schema.AccessMode.READ_ONLY)
        Double distanceToOptimalKm

) {}
