package com.logistics.infrastructure.adapter;

import com.logistics.application.port.in.ManageCompanyUseCase;
import com.logistics.application.port.in.ManageConsumptionSiteUseCase;
import com.logistics.application.service.CompanyService;
import com.logistics.application.service.ConsumptionSiteService;
import com.logistics.domain.model.Company;
import com.logistics.infrastructure.config.LogisticsProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Spring ApplicationRunner that seeds sample data on startup when enabled via
 * {@code logistics.seed-data.enabled=true} in application.yml.
 *
 * Seeds the same companies and consumption sites as the original
 * SampleDataSeeder used in the JavaFX desktop build, keeping both apps
 * data-consistent during the migration phase.
 *
 * In production, disable seeding and introduce Flyway migrations instead.
 */
@Component
public class SampleDataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SampleDataInitializer.class);

    private final CompanyService         companyService;
    private final ConsumptionSiteService siteService;
    private final LogisticsProperties    properties;

    public SampleDataInitializer(CompanyService companyService,
                                 ConsumptionSiteService siteService,
                                 LogisticsProperties properties) {
        this.companyService = companyService;
        this.siteService    = siteService;
        this.properties     = properties;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!properties.seedData().enabled()) {
            log.info("Sample data seeding is disabled (logistics.seed-data.enabled=false).");
            return;
        }

        log.info("Seeding sample logistics data...");
        seedCompaniesAndSites();
        log.info("Sample data seeded successfully.");
    }

    // =========================================================================
    // Sample data — mirrors SampleDataSeeder.java and the Angular DataService
    // =========================================================================

    private void seedCompaniesAndSites() {

        // ---- Acme Corp (CMP-001) ----
        Company acme = companyService.createCompany(
                new ManageCompanyUseCase.CreateCompanyCommand(
                        "Acme Corp", Company.Type.SHIPPER,
                        "12-3456789", "Jane Smith",
                        "j@acme.com", "+1-555-0100", null));

        addSite(acme.getId(), "Main Warehouse",  "Primary origin",
                34.0522, -118.2437, 450.0, "3200 S Figueroa St", "Los Angeles", "USA");
        addSite(acme.getId(), "East Hub",        "East Coast distribution",
                40.7128, -74.0060,  380.0, "1 World Trade Center", "New York", "USA");
        addSite(acme.getId(), "Midwest Depot",   "Central US depot",
                41.8781, -87.6298,  210.0, "600 W Chicago Ave", "Chicago", "USA");
        addSite(acme.getId(), "South Station",   "Southeast distribution",
                25.7617, -80.1918,  165.0, "100 N Biscayne Blvd", "Miami", "USA");

        // ---- Global Freight (CMP-002) ----
        Company globalFreight = companyService.createCompany(
                new ManageCompanyUseCase.CreateCompanyCommand(
                        "Global Freight", Company.Type.CARRIER,
                        "98-7654321", "Bob Johnson",
                        "b@globalfreight.com", "+1-555-0200", null));

        addSite(globalFreight.getId(), "JFK Cargo Terminal", "Airport cargo hub",
                40.6413, -73.7781,  820.0, "JFK Airport", "New York", "USA");
        addSite(globalFreight.getId(), "ORD Air Hub",        "Midwest air cargo",
                41.9742, -87.9073,  560.0, "O'Hare International Airport", "Chicago", "USA");
        addSite(globalFreight.getId(), "SEA Terminal",       "Pacific Northwest",
                47.4502, -122.3088, 340.0, "Seattle-Tacoma Airport", "Seattle", "USA");

        // ---- FastLog Inc (CMP-003) ----
        Company fastLog = companyService.createCompany(
                new ManageCompanyUseCase.CreateCompanyCommand(
                        "FastLog Inc", Company.Type.BOTH,
                        "55-1122334", "Maria Garcia",
                        "m@fastlog.com", "+1-555-0300", null));

        addSite(fastLog.getId(), "LAX Cargo",      "West Coast gateway",
                33.9425, -118.4081, 610.0, "LAX Airport", "Los Angeles", "USA");
        addSite(fastLog.getId(), "DFW Depot",      "Texas hub",
                32.8998, -97.0403,  290.0, "DFW Airport", "Dallas", "USA");
        addSite(fastLog.getId(), "ATL Warehouse",  "Southeast gateway",
                33.6407, -84.4277,  340.0, "Hartsfield-Jackson Airport", "Atlanta", "USA");

        // ---- Additional companies (no sites) ----
        companyService.createCompany(new ManageCompanyUseCase.CreateCompanyCommand(
                "BayArea Transport", Company.Type.CARRIER,
                null, null, null, null, null));

        companyService.createCompany(new ManageCompanyUseCase.CreateCompanyCommand(
                "Pacific Shipping", Company.Type.SHIPPER,
                null, null, null, null, null));

        companyService.createCompany(new ManageCompanyUseCase.CreateCompanyCommand(
                "Metro Moves", Company.Type.BOTH,
                null, null, null, null, null));

        companyService.createCompany(new ManageCompanyUseCase.CreateCompanyCommand(
                "Apex Logistics", Company.Type.CARRIER,
                null, null, null, null, null));

        companyService.createCompany(new ManageCompanyUseCase.CreateCompanyCommand(
                "SkyBridge Co", Company.Type.SHIPPER,
                null, null, null, null, null));
    }

    private void addSite(String companyId, String name, String description,
                         double lat, double lon, double tons,
                         String address, String city, String country) {
        siteService.addSite(new ManageConsumptionSiteUseCase.AddSiteCommand(
                companyId, name, description, lat, lon, tons, address, city, country));
    }
}
