package com.logistics.dashboard.web.controller;

import com.logistics.dashboard.application.service.DashboardAggregationService;
import com.logistics.dashboard.web.dto.DashboardSummaryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing the aggregated dashboard summary.
 *
 * GET /api/v1/dashboard — returns the full KPI snapshot.
 *
 * The result is cached in Redis (TTL: 30 seconds) to handle dashboard
 * polling without hammering downstream services.  The cache is
 * invalidated by Kafka event consumers whenever relevant state changes.
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "Aggregated KPI and activity feed for the logistics platform dashboard")
public class DashboardController {

    private final DashboardAggregationService aggregationService;

    public DashboardController(DashboardAggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }

    @GetMapping
    @Cacheable(value = "dashboard-summary", key = "'global'")
    @Operation(
        summary     = "Get dashboard summary",
        description = """
            Returns a point-in-time snapshot of all dashboard KPIs:
            - Company counts (total, active)
            - Active shipment count
            - Total consumption sites and tonnage
            - Logistics center candidates
            - Recent activity feed
            - Overdue shipments table
            """
    )
    public DashboardSummaryDto getDashboard() {
        return aggregationService.buildSummary();
    }
}
