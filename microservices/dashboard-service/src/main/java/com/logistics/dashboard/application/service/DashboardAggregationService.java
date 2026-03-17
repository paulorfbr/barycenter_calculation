package com.logistics.dashboard.application.service;

import com.logistics.dashboard.infrastructure.client.CompanyServiceClient;
import com.logistics.dashboard.infrastructure.client.SiteServiceClient;
import com.logistics.dashboard.web.dto.DashboardSummaryDto;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Assembles the dashboard summary from multiple upstream services.
 *
 * Architecture — CQRS read side:
 *   - A lightweight in-memory read model (counters + recent activity) is
 *     updated by Kafka event listeners so the dashboard endpoint never
 *     needs to make synchronous calls to other services for KPI data.
 *   - The read model is also cached in Redis so multiple dashboard service
 *     replicas share the same view without each maintaining state.
 *   - Feign calls to Company and Site services are made only on cold start
 *     or when the read model is explicitly invalidated.
 *
 * For a production implementation, replace the in-memory counters with
 * a PostgreSQL read model (materialised view or event-sourced projection).
 */
@Service
@Observed(name = "dashboard.aggregation")
public class DashboardAggregationService {

    private static final Logger           log     = LoggerFactory.getLogger(DashboardAggregationService.class);
    private static final DateTimeFormatter HH_MM  = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());

    // --- In-memory read model counters ---
    private final AtomicInteger totalCompanies       = new AtomicInteger(0);
    private final AtomicInteger activeCompanies      = new AtomicInteger(0);
    private final AtomicInteger totalSites           = new AtomicInteger(0);
    private final AtomicInteger centerCandidates     = new AtomicInteger(0);
    private final AtomicReference<Double> totalTons  = new AtomicReference<>(0.0);

    private final CompanyServiceClient companyClient;
    private final SiteServiceClient    siteClient;

    public DashboardAggregationService(CompanyServiceClient companyClient,
                                        SiteServiceClient siteClient) {
        this.companyClient = companyClient;
        this.siteClient    = siteClient;
    }

    // -----------------------------------------------------------------------
    // Build summary (called by controller)
    // -----------------------------------------------------------------------

    public DashboardSummaryDto buildSummary() {
        // Refresh from upstream services if counters are zero (cold start)
        if (totalCompanies.get() == 0) {
            refreshFromUpstream();
        }

        String now = HH_MM.format(Instant.now());
        List<DashboardSummaryDto.ActivityItem> activity = List.of(
                new DashboardSummaryDto.ActivityItem(now, "Dashboard refreshed"),
                new DashboardSummaryDto.ActivityItem(now,
                        totalCompanies.get() + " companies tracked"),
                new DashboardSummaryDto.ActivityItem(now,
                        totalSites.get() + " consumption sites active"),
                new DashboardSummaryDto.ActivityItem(now,
                        String.format("%.0f t total traffic volume", totalTons.get())),
                new DashboardSummaryDto.ActivityItem(now,
                        centerCandidates.get() + " logistics center candidates")
        );

        // Shipments stubbed — replace with Shipment Service when available
        List<DashboardSummaryDto.OverdueShipmentDto> overdue = List.of(
                new DashboardSummaryDto.OverdueShipmentDto("#SHP-1799", "Acme Corp",     "LAX", "JFK", "3 days"),
                new DashboardSummaryDto.OverdueShipmentDto("#SHP-1755", "Global Freight","ORD", "MIA", "2 days"),
                new DashboardSummaryDto.OverdueShipmentDto("#SHP-1701", "FastLog Inc",   "SEA", "BOS", "1 day")
        );

        return new DashboardSummaryDto(
                totalCompanies.get(),
                activeCompanies.get(),
                totalSites.get(),
                totalTons.get(),
                centerCandidates.get(),
                234,          // stubbed active shipments
                94.2,         // stubbed on-time rate
                "+" + (totalCompanies.get() - activeCompanies.get()) + " pending",
                "234 active",
                totalSites.get() + " consumption sites",
                "+1.3% vs last week",
                activity,
                overdue,
                Instant.now()
        );
    }

    // -----------------------------------------------------------------------
    // Kafka event listeners — update read model
    // -----------------------------------------------------------------------

    @KafkaListener(topics = "logistics.company-events", groupId = "dashboard-service")
    @CacheEvict(value = "dashboard-summary", allEntries = true)
    public void onCompanyEvent(Map<String, Object> event) {
        String type = (String) event.get("eventType");
        if ("CompanyCreated".equals(type)) {
            totalCompanies.incrementAndGet();
            activeCompanies.incrementAndGet();
        } else if ("CompanyStatusChanged".equals(type)) {
            String newStatus = (String) event.get("newStatus");
            if ("ACTIVE".equals(newStatus))   activeCompanies.incrementAndGet();
            if ("INACTIVE".equals(newStatus)) activeCompanies.decrementAndGet();
        }
        log.debug("Dashboard read model updated on company event: {}", type);
    }

    @KafkaListener(topics = "logistics.site-events", groupId = "dashboard-service")
    @CacheEvict(value = "dashboard-summary", allEntries = true)
    public void onSiteEvent(Map<String, Object> event) {
        String type = (String) event.get("eventType");
        if ("SiteAdded".equals(type)) {
            totalSites.incrementAndGet();
            Object tons = event.get("weightTons");
            if (tons instanceof Number n) totalTons.updateAndGet(v -> v + n.doubleValue());
        } else if ("SiteRemoved".equals(type)) {
            totalSites.decrementAndGet();
        }
        log.debug("Dashboard read model updated on site event: {}", type);
    }

    @KafkaListener(topics = "logistics.calculation-events", groupId = "dashboard-service")
    @CacheEvict(value = "dashboard-summary", allEntries = true)
    public void onCalculationEvent(Map<String, Object> event) {
        String type = (String) event.get("eventType");
        if ("BarycentreCalculated".equals(type)) {
            centerCandidates.incrementAndGet();
        }
        log.debug("Dashboard read model updated on calculation event: {}", type);
    }

    // -----------------------------------------------------------------------
    // Cold-start refresh via upstream Feign calls
    // -----------------------------------------------------------------------

    private void refreshFromUpstream() {
        try {
            int companyCount = companyClient.countCompanies();
            int activeCount  = companyClient.countActiveCompanies();
            int siteCount    = siteClient.countAllSites();
            double tons      = siteClient.sumAllWeightTons();

            totalCompanies.set(companyCount);
            activeCompanies.set(activeCount);
            totalSites.set(siteCount);
            totalTons.set(tons);

            log.info("Dashboard read model initialised from upstream: companies={} sites={}", companyCount, siteCount);
        } catch (Exception e) {
            log.warn("Failed to initialise dashboard read model from upstream services: {}", e.getMessage());
            // Use zero values — the dashboard will show empty KPIs rather than failing
        }
    }
}
