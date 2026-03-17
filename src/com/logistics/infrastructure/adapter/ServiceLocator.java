package com.logistics.infrastructure.adapter;

import com.logistics.application.service.BarycentreService;
import com.logistics.application.service.CompanyService;
import com.logistics.application.service.ConsumptionSiteService;
import com.logistics.infrastructure.calculation.BarycentreCalculationEngine;
import com.logistics.infrastructure.persistence.repository.InMemoryCompanyRepository;
import com.logistics.infrastructure.persistence.repository.InMemoryConsumptionSiteRepository;
import com.logistics.infrastructure.persistence.repository.InMemoryLogisticsCenterRepository;

/**
 * Manual dependency-injection container for the JavaFX desktop runtime.
 *
 * This class wires together the entire hexagonal architecture:
 *
 *   Infrastructure layer:
 *     BarycentreCalculationEngine  (stateless algorithm)
 *     InMemory*Repository          (driven adapters — persistence)
 *     InProcessEventPublisher      (driven adapter — events)
 *
 *   Application layer:
 *     BarycentreService            (use-case implementation)
 *     CompanyService               (use-case implementation)
 *     ConsumptionSiteService       (use-case implementation)
 *
 * When a Spring Boot context is introduced, this class is replaced by
 * Spring's component scanning + @Bean factories. The service and domain
 * classes require zero changes.
 *
 * Lifecycle:
 *   Call ServiceLocator.getInstance() from MainApplication.start() to
 *   trigger initialisation. All screen classes then call ServiceLocator
 *   to obtain the services they need. This avoids passing service references
 *   through every constructor in the navigation chain.
 */
public final class ServiceLocator {

    // -------------------------------------------------------------------------
    // Singleton
    // -------------------------------------------------------------------------

    private static volatile ServiceLocator INSTANCE;

    public static ServiceLocator getInstance() {
        if (INSTANCE == null) {
            synchronized (ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator();
                }
            }
        }
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Wired components
    // -------------------------------------------------------------------------

    // Repositories
    private final InMemoryCompanyRepository          companyRepository;
    private final InMemoryConsumptionSiteRepository  siteRepository;
    private final InMemoryLogisticsCenterRepository  centerRepository;

    // Infrastructure
    private final BarycentreCalculationEngine        calculationEngine;
    private final InProcessEventPublisher            eventPublisher;

    // Application services
    private final CompanyService         companyService;
    private final ConsumptionSiteService consumptionSiteService;
    private final BarycentreService      barycentreService;

    // -------------------------------------------------------------------------
    // Private constructor — build the full graph
    // -------------------------------------------------------------------------

    private ServiceLocator() {
        // Repositories
        companyRepository = new InMemoryCompanyRepository();
        siteRepository    = new InMemoryConsumptionSiteRepository();
        centerRepository  = new InMemoryLogisticsCenterRepository();

        // Infrastructure
        calculationEngine = BarycentreCalculationEngine.getInstance();
        eventPublisher    = new InProcessEventPublisher();

        // Application services
        companyService = new CompanyService(companyRepository);

        consumptionSiteService = new ConsumptionSiteService(
                siteRepository, companyRepository);

        barycentreService = new BarycentreService(
                calculationEngine,
                siteRepository,
                companyRepository,
                centerRepository,
                eventPublisher);

        // Seed with representative sample data
        SampleDataSeeder.seed(companyService, consumptionSiteService);
    }

    // -------------------------------------------------------------------------
    // Public accessors
    // -------------------------------------------------------------------------

    public CompanyService         getCompanyService()          { return companyService; }
    public ConsumptionSiteService getConsumptionSiteService()  { return consumptionSiteService; }
    public BarycentreService      getBarycentreService()       { return barycentreService; }
    public InProcessEventPublisher getEventPublisher()         { return eventPublisher; }
    public BarycentreCalculationEngine getCalculationEngine()  { return calculationEngine; }
}
