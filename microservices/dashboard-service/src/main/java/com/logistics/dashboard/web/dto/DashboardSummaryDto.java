package com.logistics.dashboard.web.dto;

import java.time.Instant;
import java.util.List;

/**
 * Aggregated dashboard summary DTO.
 *
 * Assembled by {@link com.logistics.dashboard.application.service.DashboardAggregationService}
 * by combining data from Company, Site, and Calculation Services (via Feign or
 * from a cached Redis read model).
 *
 * Designed to match the existing {@code DashboardSummaryDto} from the desktop
 * application so the Angular SPA requires minimal changes to its data layer.
 */
public record DashboardSummaryDto(

        // --- Company KPIs ---
        int    totalCompanies,
        int    activeCompanies,

        // --- Site KPIs ---
        int    totalConsumptionSites,
        double totalTrafficTons,

        // --- Calculation KPIs ---
        int    logisticsCenterCandidates,

        // --- Shipment KPIs (stubbed until Shipment Service is implemented) ---
        int    activeShipments,
        double onTimeRatePercent,

        // --- Trend labels ---
        String companiesTrend,
        String shipmentsTrend,
        String locationsTrend,
        String onTimeTrend,

        // --- Activity feed ---
        List<ActivityItem> recentActivity,

        // --- Overdue shipments ---
        List<OverdueShipmentDto> overdueShipments,

        // --- Metadata ---
        Instant generatedAt

) {
    public record ActivityItem(String timeLabel, String message) {}

    public record OverdueShipmentDto(
            String shipmentId,
            String companyName,
            String origin,
            String destination,
            String daysOverdue) {}
}
