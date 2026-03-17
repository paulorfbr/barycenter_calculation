package com.logistics.application.service;

import com.logistics.application.port.in.CalculateBarycentreUseCase;
import com.logistics.application.port.out.CompanyRepository;
import com.logistics.application.port.out.ConsumptionSiteRepository;
import com.logistics.application.port.out.DomainEventPublisher;
import com.logistics.application.port.out.LogisticsCenterRepository;
import com.logistics.domain.model.Company;
import com.logistics.domain.model.ConsumptionSite;
import com.logistics.domain.model.LogisticsCenter;
import com.logistics.domain.vo.GeoCoordinate;
import com.logistics.domain.vo.TrafficVolume;
import com.logistics.infrastructure.calculation.BarycentreCalculationEngine;
import com.logistics.infrastructure.persistence.repository.InMemoryCompanyRepository;
import com.logistics.infrastructure.persistence.repository.InMemoryConsumptionSiteRepository;
import com.logistics.infrastructure.persistence.repository.InMemoryLogisticsCenterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BarycentreService}.
 *
 * Uses real in-memory repository implementations to avoid mocking overhead
 * and to exercise the full integration of domain model, repositories,
 * and calculation engine.
 */
@DisplayName("BarycentreService")
class BarycentreServiceTest {

    // -------------------------------------------------------------------------
    // Test doubles and collaborators
    // -------------------------------------------------------------------------

    private InMemoryCompanyRepository         companyRepo;
    private InMemoryConsumptionSiteRepository siteRepo;
    private InMemoryLogisticsCenterRepository centerRepo;
    private List<Object>                      publishedEvents;
    private BarycentreService                 service;

    @BeforeEach
    void setUp() {
        companyRepo    = new InMemoryCompanyRepository();
        siteRepo       = new InMemoryConsumptionSiteRepository();
        centerRepo     = new InMemoryLogisticsCenterRepository();
        publishedEvents = new ArrayList<>();

        DomainEventPublisher publisher = publishedEvents::add;

        service = new BarycentreService(
                BarycentreCalculationEngine.getInstance(),
                siteRepo,
                companyRepo,
                centerRepo,
                publisher);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Company seedCompany() {
        Company c = new Company(null, "Test Corp", Company.Type.SHIPPER, Company.Status.ACTIVE);
        return companyRepo.save(c);
    }

    private ConsumptionSite seedSite(Company company, String name, double lat, double lon, double tons) {
        ConsumptionSite site = new ConsumptionSite(null, name,
                new GeoCoordinate(lat, lon), TrafficVolume.ofTons(tons));
        company.addConsumptionSite(site);
        companyRepo.save(company);
        return siteRepo.save(site);
    }

    // -------------------------------------------------------------------------
    // calculateForCompany()
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("calculateForCompany()")
    class CalculateForCompany {

        @Test
        @DisplayName("returns CANDIDATE center when 2+ active sites exist")
        void returnsCandidateCenter() {
            Company c = seedCompany();
            seedSite(c, "LAX", 33.9425, -118.4081, 610.0);
            seedSite(c, "JFK", 40.6413, -73.7781, 820.0);

            LogisticsCenter result = service.calculateForCompany(
                    CalculateBarycentreUseCase.StoredSitesCommand.defaultFor(c.getId()));

            assertNotNull(result);
            assertEquals(LogisticsCenter.Status.CANDIDATE, result.getStatus());
        }

        @Test
        @DisplayName("persists result to LogisticsCenterRepository")
        void persistsResultToRepository() {
            Company c = seedCompany();
            seedSite(c, "A", 0.0, 0.0, 100.0);
            seedSite(c, "B", 10.0, 10.0, 100.0);

            LogisticsCenter result = service.calculateForCompany(
                    CalculateBarycentreUseCase.StoredSitesCommand.defaultFor(c.getId()));

            Optional<LogisticsCenter> persisted = centerRepo.findById(result.getId());
            assertTrue(persisted.isPresent());
        }

        @Test
        @DisplayName("publishes BarycentreCalculatedEvent")
        void publishesDomainEvent() {
            Company c = seedCompany();
            seedSite(c, "A", 0.0, 0.0, 100.0);
            seedSite(c, "B", 10.0, 10.0, 100.0);

            service.calculateForCompany(
                    CalculateBarycentreUseCase.StoredSitesCommand.defaultFor(c.getId()));

            assertEquals(1, publishedEvents.size());
        }

        @Test
        @DisplayName("throws InsufficientSitesException when fewer than 2 active sites")
        void throwsInsufficientSites() {
            Company c = seedCompany();
            seedSite(c, "Only Site", 0.0, 0.0, 100.0);

            assertThrows(CalculateBarycentreUseCase.InsufficientSitesException.class,
                    () -> service.calculateForCompany(
                            CalculateBarycentreUseCase.StoredSitesCommand.defaultFor(c.getId())));
        }

        @Test
        @DisplayName("throws InsufficientSitesException when company has no sites")
        void throwsWhenNoSites() {
            Company c = seedCompany();
            assertThrows(CalculateBarycentreUseCase.InsufficientSitesException.class,
                    () -> service.calculateForCompany(
                            CalculateBarycentreUseCase.StoredSitesCommand.defaultFor(c.getId())));
        }

        @Test
        @DisplayName("attaches result to Company's logisticsCenters list")
        void attachesResultToCompany() {
            Company c = seedCompany();
            seedSite(c, "A", 0.0, 0.0, 100.0);
            seedSite(c, "B", 10.0, 10.0, 100.0);

            service.calculateForCompany(
                    CalculateBarycentreUseCase.StoredSitesCommand.defaultFor(c.getId()));

            Company refreshed = companyRepo.findById(c.getId()).orElseThrow();
            assertEquals(1, refreshed.getLogisticsCenters().size());
        }
    }

    // -------------------------------------------------------------------------
    // calculate() — from inline command
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("calculate() from SimpleCalculateCommand")
    class CalculateFromCommand {

        @Test
        @DisplayName("returns CANDIDATE center")
        void returnsCandidateCenter() {
            var cmd = new CalculateBarycentreUseCase.SimpleCalculateCommand(
                    null,
                    List.of("A", "B"),
                    List.of(0.0, 10.0),
                    List.of(0.0, 10.0),
                    List.of(100.0, 100.0),
                    false);

            LogisticsCenter result = service.calculate(cmd);
            assertNotNull(result);
            assertEquals(LogisticsCenter.Status.CANDIDATE, result.getStatus());
        }

        @Test
        @DisplayName("publishes event for inline calculation")
        void publishesEventForInlineCalculation() {
            var cmd = new CalculateBarycentreUseCase.SimpleCalculateCommand(
                    null,
                    List.of("A", "B"),
                    List.of(0.0, 10.0),
                    List.of(0.0, 10.0),
                    List.of(100.0, 100.0),
                    false);

            service.calculate(cmd);
            assertEquals(1, publishedEvents.size());
        }
    }
}
