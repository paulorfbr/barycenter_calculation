package com.logistics.infrastructure.web.controller;

import com.logistics.application.dto.DashboardSummaryDto;
import com.logistics.application.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for dashboard aggregation.
 *
 * Provides a single pre-aggregated endpoint that the Angular Dashboard component
 * can call to populate all KPI cards, the activity feed, and the overdue
 * shipments table in one round-trip.
 *
 * GET /api/v1/dashboard
 *   Returns {@link DashboardSummaryDto} assembled by {@link DashboardService}.
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "Pre-aggregated dashboard summary data")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @Operation(
            summary = "Get dashboard summary",
            description = "Returns all KPI values, recent activity, and overdue shipments " +
                          "in a single pre-aggregated response. Designed for the Angular " +
                          "dashboard component to populate on load and on refresh.")
    @ApiResponse(responseCode = "200", description = "Dashboard summary returned")
    public DashboardSummaryDto getDashboard() {
        return dashboardService.buildSummary();
    }
}
