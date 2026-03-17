package com.logistics.application.service;

import com.logistics.application.port.in.CalculateBarycentreUseCase;
import com.logistics.application.port.out.CompanyRepository;
import com.logistics.application.port.out.ConsumptionSiteRepository;
import com.logistics.application.port.out.DomainEventPublisher;
import com.logistics.application.port.out.LogisticsCenterRepository;
import com.logistics.domain.event.BarycentreCalculatedEvent;
import com.logistics.domain.model.Company;
import com.logistics.domain.model.ConsumptionSite;
import com.logistics.domain.model.LogisticsCenter;
import com.logistics.infrastructure.calculation.BarycentreCalculationEngine;

import java.util.List;
import java.util.Objects;

/**
 * Application service implementing {@link CalculateBarycentreUseCase}.
 *
 * Orchestration responsibilities:
 *   1. Validate inputs and load data via output ports.
 *   2. Delegate pure computation to {@link BarycentreCalculationEngine}.
 *   3. Persist the result via {@link LogisticsCenterRepository}.
 *   4. Attach the result to the owning Company aggregate.
 *   5. Publish a {@link BarycentreCalculatedEvent} so UI subscribers can react.
 *
 * No JavaFX imports here — this service runs on whatever thread the caller
 * chooses. The UI adapter must invoke this on a background thread and
 * dispatch results back to the FX thread via Platform.runLater().
 */
public class BarycentreService implements CalculateBarycentreUseCase {

    // -------------------------------------------------------------------------
    // Dependencies (injected via constructor — no framework required)
    // -------------------------------------------------------------------------

    private final BarycentreCalculationEngine  engine;
    private final ConsumptionSiteRepository    siteRepository;
    private final CompanyRepository            companyRepository;
    private final LogisticsCenterRepository    centerRepository;
    private final DomainEventPublisher         eventPublisher;

    public BarycentreService(BarycentreCalculationEngine  engine,
                              ConsumptionSiteRepository    siteRepository,
                              CompanyRepository            companyRepository,
                              LogisticsCenterRepository    centerRepository,
                              DomainEventPublisher         eventPublisher) {
        this.engine            = Objects.requireNonNull(engine);
        this.siteRepository    = Objects.requireNonNull(siteRepository);
        this.companyRepository = Objects.requireNonNull(companyRepository);
        this.centerRepository  = Objects.requireNonNull(centerRepository);
        this.eventPublisher    = Objects.requireNonNull(eventPublisher);
    }

    // -------------------------------------------------------------------------
    // CalculateBarycentreUseCase implementation
    // -------------------------------------------------------------------------

    /**
     * Calculates a barycenter from inline data (before sites are persisted).
     * The result is persisted as a CANDIDATE LogisticsCenter and linked to the
     * company if a companyId is supplied.
     */
    @Override
    public LogisticsCenter calculate(SimpleCalculateCommand cmd) {
        LogisticsCenter center;

        if (cmd.useIterative()) {
            center = engine.calculateWeiszfeldFromArrays(
                    cmd.companyId(),
                    cmd.siteName(),
                    cmd.latitudes(),
                    cmd.longitudes(),
                    cmd.weightsTons(),
                    BarycentreCalculationEngine.DEFAULT_MAX_ITERATIONS,
                    BarycentreCalculationEngine.DEFAULT_TOLERANCE_KM);
        } else {
            center = engine.calculateSimpleFromArrays(
                    cmd.companyId(),
                    cmd.siteName(),
                    cmd.latitudes(),
                    cmd.longitudes(),
                    cmd.weightsTons());
        }

        centerRepository.save(center);
        attachToCompanyIfPresent(center);
        eventPublisher.publish(BarycentreCalculatedEvent.from(center));
        return center;
    }

    /**
     * Calculates using the persisted consumption sites for a given company.
     */
    @Override
    public LogisticsCenter calculateForCompany(StoredSitesCommand cmd) {
        List<ConsumptionSite> activeSites = siteRepository.findActiveByCompanyId(cmd.companyId());

        if (activeSites.size() < 2) {
            throw new InsufficientSitesException(cmd.companyId(), activeSites.size());
        }

        LogisticsCenter center;
        if (cmd.useIterative()) {
            center = engine.calculateWeiszfeld(
                    cmd.companyId(), activeSites, cmd.maxIterations(), cmd.toleranceKm());
        } else {
            center = engine.calculateSimple(cmd.companyId(), activeSites);
        }

        centerRepository.save(center);
        attachToCompanyIfPresent(center);
        eventPublisher.publish(BarycentreCalculatedEvent.from(center));
        return center;
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private void attachToCompanyIfPresent(LogisticsCenter center) {
        if (center.getCompanyId() == null || center.getCompanyId().equals("PREVIEW")) {
            return;
        }
        companyRepository.findById(center.getCompanyId()).ifPresent(company -> {
            company.addLogisticsCenter(center);
            companyRepository.save(company);
        });
    }
}
