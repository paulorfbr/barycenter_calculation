package com.logistics.application.service;

import com.logistics.application.dto.DashboardSummaryDto;
import com.logistics.application.port.out.CompanyRepository;
import com.logistics.application.port.out.ConsumptionSiteRepository;
import com.logistics.application.port.out.LogisticsCenterRepository;
import com.logistics.domain.model.Company;
import com.logistics.domain.model.LogisticsCenter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * Application service that assembles the {@link DashboardSummaryDto} from
 * the current state of all repositories.
 *
 * This service is deliberately read-only — it queries but never mutates.
 * It is called from the Dashboard refresh flow on a background thread;
 * the FX thread receives the completed DTO via Platform.runLater and passes
 * it to DashboardScreen.refresh(DashboardSummaryDto).
 */
public class DashboardService {

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());

    private final CompanyRepository          companyRepository;
    private final ConsumptionSiteRepository  siteRepository;
    private final LogisticsCenterRepository  centerRepository;

    public DashboardService(CompanyRepository         companyRepository,
                             ConsumptionSiteRepository  siteRepository,
                             LogisticsCenterRepository  centerRepository) {
        this.companyRepository = Objects.requireNonNull(companyRepository);
        this.siteRepository    = Objects.requireNonNull(siteRepository);
        this.centerRepository  = Objects.requireNonNull(centerRepository);
    }

    /**
     * Builds a full dashboard summary.
     *
     * @return populated DTO ready for the Dashboard screen
     */
    public DashboardSummaryDto buildSummary() {
        List<Company> allCompanies = companyRepository.findAll();
        int totalCompanies         = allCompanies.size();
        int activeCompanies        = (int) allCompanies.stream()
                .filter(c -> c.getStatus() == Company.Status.ACTIVE).count();

        int totalSites             = allCompanies.stream()
                .mapToInt(c -> (int) siteRepository.countByCompanyId(c.getId())).sum();

        double totalTons           = allCompanies.stream()
                .mapToDouble(Company::totalTrafficTons).sum();

        int candidateCenters       = (int) allCompanies.stream()
                .flatMap(c -> centerRepository.findByCompanyId(c.getId()).stream())
                .filter(lc -> lc.getStatus() == LogisticsCenter.Status.CANDIDATE
                           || lc.getStatus() == LogisticsCenter.Status.APPROVED)
                .count();

        // Shipment and location counts are stubbed until those repositories are added.
        // When ShipmentRepository is introduced, replace the literals below.
        int activeShipments  = 234;
        int totalLocations   = totalSites;   // proxy: treat sites as locations
        double onTimeRate    = 94.2;

        String companiesTrend  = "+" + (totalCompanies - activeCompanies) + " pending";
        String shipmentsTrend  = activeShipments + " active";
        String locationsTrend  = totalSites + " consumption sites";
        String onTimeTrend     = "+1.3% vs last week";

        // Activity feed — in a real implementation this comes from an audit log
        List<DashboardSummaryDto.ActivityItem> activity = List.of(
                new DashboardSummaryDto.ActivityItem(TIME_FMT.format(Instant.now()), "Barycenter recalculated for " + activeCompanies + " companies"),
                new DashboardSummaryDto.ActivityItem(TIME_FMT.format(Instant.now()), totalCompanies + " companies loaded from repository"),
                new DashboardSummaryDto.ActivityItem(TIME_FMT.format(Instant.now()), totalSites + " consumption sites active"),
                new DashboardSummaryDto.ActivityItem(TIME_FMT.format(Instant.now()), String.format("%.0f t total traffic volume tracked", totalTons)),
                new DashboardSummaryDto.ActivityItem(TIME_FMT.format(Instant.now()), candidateCenters + " logistics center candidates ready for review")
        );

        // Overdue shipments — stubbed; replace with real ShipmentRepository query
        List<DashboardSummaryDto.OverdueShipmentDto> overdue = List.of(
                new DashboardSummaryDto.OverdueShipmentDto("#SHP-1799", "Acme Corp",     "LAX", "JFK", "3 days"),
                new DashboardSummaryDto.OverdueShipmentDto("#SHP-1755", "Global Freight","ORD", "MIA", "2 days"),
                new DashboardSummaryDto.OverdueShipmentDto("#SHP-1701", "FastLog Inc",   "SEA", "BOS", "1 day")
        );

        return new DashboardSummaryDto(
                totalCompanies,
                activeShipments,
                totalLocations,
                onTimeRate,
                companiesTrend,
                shipmentsTrend,
                locationsTrend,
                onTimeTrend,
                totalSites,
                totalTons,
                candidateCenters,
                activity,
                overdue
        );
    }
}
