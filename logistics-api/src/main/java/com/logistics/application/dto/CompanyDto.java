package com.logistics.application.dto;

/**
 * Data Transfer Object for Company read operations.
 *
 * Carries all fields shown in CompanyListScreen and the future Company Detail
 * screen (SCR-003). Also the shape of GET /api/v1/companies/{id} responses.
 */
public record CompanyDto(
        String id,
        String name,
        String type,           // "SHIPPER" | "CARRIER" | "BOTH"
        String status,         // "ACTIVE" | "INACTIVE" | "PENDING"
        String taxId,
        String contactName,
        String contactEmail,
        String contactPhone,
        String notes,
        int    locationCount,
        double totalTrafficTons,
        String createdAt,
        String updatedAt) {}
