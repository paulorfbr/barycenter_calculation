package com.logistics.application.dto;

import java.util.List;

/**
 * Data Transfer Object carrying all values needed to populate the Dashboard screen.
 *
 * Produced by DashboardService and consumed by the DashboardScreen view-model
 * via DashboardViewModelMapper. Also the response payload for a future
 * GET /api/v1/dashboard REST endpoint.
 *
 * Using a flat DTO (rather than exposing aggregates directly) ensures:
 *   - The UI layer cannot accidentally mutate domain state.
 *   - REST serialisation is trivial (no lazy-load traps).
 *   - The dashboard can be refreshed from a single query rather than N queries
 *     (the service assembles this DTO in one pass).
 */
public record DashboardSummaryDto(

        // KPI values — directly map to KpiCard.withValue()
        int    totalCompanies,
        int    activeShipments,
        int    totalLocations,
        double onTimeRatePercent,

        // Trend texts — map to KpiCard.withTrend()
        String companiesTrend,
        String shipmentsTrend,
        String locationsTrend,
        String onTimeTrend,

        // Barycenter summary
        int    totalConsumptionSites,
        double totalTrafficTons,
        int    logisticsCenterCandidates,

        // Activity feed items
        List<ActivityItem> recentActivity,

        // Overdue shipments table
        List<OverdueShipmentDto> overdueShipments

) {
    /**
     * A single item in the Recent Activity feed.
     * Maps to DashboardScreen.buildActivityItem(time, message).
     */
    public record ActivityItem(String timeLabel, String message) {}

    /**
     * A single overdue shipment row.
     * Maps to DashboardScreen.OverdueRow constructor arguments.
     */
    public record OverdueShipmentDto(
            String shipmentId,
            String companyName,
            String origin,
            String destination,
            String daysOverdue) {}
}
