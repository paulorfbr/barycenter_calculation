package com.logistics.infrastructure.config;

import com.logistics.application.port.out.CompanyRepository;
import com.logistics.application.port.out.ConsumptionSiteRepository;
import com.logistics.application.port.out.DomainEventPublisher;
import com.logistics.application.port.out.LogisticsCenterRepository;
import com.logistics.application.service.BarycentreService;
import com.logistics.application.service.CompanyService;
import com.logistics.application.service.ConsumptionSiteService;
import com.logistics.application.service.DashboardService;
import com.logistics.infrastructure.adapter.SpringEventPublisher;
import com.logistics.infrastructure.calculation.BarycentreCalculationEngine;
import com.logistics.infrastructure.persistence.repository.InMemoryCompanyRepository;
import com.logistics.infrastructure.persistence.repository.InMemoryConsumptionSiteRepository;
import com.logistics.infrastructure.persistence.repository.InMemoryLogisticsCenterRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring @Configuration class that wires together the hexagonal architecture.
 *
 * This replaces the manual {@code ServiceLocator} from the JavaFX desktop build.
 * Spring manages all lifecycles; the application and domain layers remain free
 * of any Spring annotations.
 *
 * Repository implementations are in-memory for this build. Replacing them with
 * JPA adapters requires only new @Bean definitions here — no application or
 * domain code changes.
 */
@Configuration
public class ApplicationConfig {

    // =========================================================================
    // Repository beans (driven adapters — persistence port implementations)
    // =========================================================================

    @Bean
    public InMemoryCompanyRepository companyRepository() {
        return new InMemoryCompanyRepository();
    }

    @Bean
    public InMemoryConsumptionSiteRepository consumptionSiteRepository() {
        return new InMemoryConsumptionSiteRepository();
    }

    @Bean
    public InMemoryLogisticsCenterRepository logisticsCenterRepository() {
        return new InMemoryLogisticsCenterRepository();
    }

    // =========================================================================
    // Infrastructure beans
    // =========================================================================

    @Bean
    public BarycentreCalculationEngine barycentreCalculationEngine() {
        return BarycentreCalculationEngine.getInstance();
    }

    /**
     * Wraps Spring's ApplicationEventPublisher as a DomainEventPublisher,
     * enabling domain events to be dispatched via Spring's event bus.
     * Subscribers use @EventListener in Spring components.
     */
    @Bean
    public DomainEventPublisher domainEventPublisher(ApplicationEventPublisher springPublisher) {
        return new SpringEventPublisher(springPublisher);
    }

    // =========================================================================
    // Application service beans (use-case implementations)
    // =========================================================================

    @Bean
    public CompanyService companyService(CompanyRepository companyRepository) {
        return new CompanyService(companyRepository);
    }

    @Bean
    public ConsumptionSiteService consumptionSiteService(
            ConsumptionSiteRepository consumptionSiteRepository,
            CompanyRepository companyRepository) {
        return new ConsumptionSiteService(consumptionSiteRepository, companyRepository);
    }

    @Bean
    public BarycentreService barycentreService(
            BarycentreCalculationEngine engine,
            ConsumptionSiteRepository consumptionSiteRepository,
            CompanyRepository companyRepository,
            LogisticsCenterRepository logisticsCenterRepository,
            DomainEventPublisher domainEventPublisher) {
        return new BarycentreService(
                engine,
                consumptionSiteRepository,
                companyRepository,
                logisticsCenterRepository,
                domainEventPublisher);
    }

    @Bean
    public DashboardService dashboardService(
            CompanyRepository companyRepository,
            ConsumptionSiteRepository consumptionSiteRepository,
            LogisticsCenterRepository logisticsCenterRepository) {
        return new DashboardService(
                companyRepository,
                consumptionSiteRepository,
                logisticsCenterRepository);
    }
}
