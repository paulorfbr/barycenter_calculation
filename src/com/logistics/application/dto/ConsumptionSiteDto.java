package com.logistics.application.dto;

/**
 * Data Transfer Object for ConsumptionSite read operations.
 *
 * Used in the consumption site management table and as the payload for
 * GET /api/v1/companies/{id}/sites and GET /api/v1/sites/{id}.
 */
public record ConsumptionSiteDto(
        String id,
        String companyId,
        String name,
        String description,
        double latitude,
        double longitude,
        String formattedCoordinate,
        double weightTons,
        String weightFormatted,
        String address,
        String city,
        String country,
        String status,         // "ACTIVE" | "INACTIVE"
        String createdAt) {}
