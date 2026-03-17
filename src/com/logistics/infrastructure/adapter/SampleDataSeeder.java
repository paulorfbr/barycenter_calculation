package com.logistics.infrastructure.adapter;

import com.logistics.application.port.in.ManageCompanyUseCase;
import com.logistics.application.port.in.ManageConsumptionSiteUseCase;
import com.logistics.application.service.CompanyService;
import com.logistics.application.service.ConsumptionSiteService;
import com.logistics.domain.model.Company;

/**
 * Seeds the in-memory repositories with representative sample data on startup.
 *
 * The data matches the sample rows shown in the existing UI screens:
 *   - CompanyListScreen.loadSampleData()  (CMP-001 … CMP-008)
 *   - LocationListScreen.loadSampleData() (LOC-001 … LOC-006)
 *
 * Sample consumption sites carry realistic US city coordinates and tonnage
 * values that produce a visually meaningful barycenter result when the
 * calculation is triggered from the Dashboard.
 *
 * This class is NOT part of the production runtime beyond the desktop demo.
 * Remove or replace with database migrations (Flyway/Liquibase) when a
 * persistent backend is introduced.
 */
public final class SampleDataSeeder {

    private SampleDataSeeder() {}

    /**
     * Seeds companies and their consumption sites.
     *
     * @param companyService  application service for company creation
     * @param siteService     application service for site creation
     */
    public static void seed(CompanyService companyService,
                            ConsumptionSiteService siteService) {

        // ---- Acme Corp (CMP-001) ----
        Company acme = companyService.createCompany(
                new ManageCompanyUseCase.CreateCompanyCommand(
                        "Acme Corp", Company.Type.SHIPPER,
                        "12-3456789", "Jane Smith",
                        "j@acme.com", "+1-555-0100", null));

        siteService.addSite(new ManageConsumptionSiteUseCase.AddSiteCommand(
                acme.getId(), "Main Warehouse", "Primary origin",
                34.0522, -118.2437, 450.0,
                "3200 S Figueroa St", "Los Angeles", "USA"));

        siteService.addSite(new ManageConsumptionSiteUseCase.AddSiteCommand(
                acme.getId(), "East Hub", "East Coast distribution",
                40.7128, -74.0060, 380.0,
                "1 World Trade Center", "New York", "USA"));

        siteService.addSite(new ManageConsumptionSiteUseCase.AddSiteCommand(
                acme.getId(), "Midwest Depot", "Central US depot",
                41.8781, -87.6298, 210.0,
                "600 W Chicago Ave", "Chicago", "USA"));

        siteService.addSite(new ManageConsumptionSiteUseCase.AddSiteCommand(
                acme.getId(), "South Station", "Southeast distribution",
                25.7617, -80.1918, 165.0,
                "100 N Biscayne Blvd", "Miami", "USA"));

        // ---- Global Freight (CMP-002) ----
        Company globalFreight = companyService.createCompany(
                new ManageCompanyUseCase.CreateCompanyCommand(
                        "Global Freight", Company.Type.CARRIER,
                        "98-7654321", "Bob Johnson",
                        "b@globalfreight.com", "+1-555-0200", null));

        siteService.addSite(new ManageConsumptionSiteUseCase.AddSiteCommand(
                globalFreight.getId(), "JFK Cargo Terminal", "Airport cargo hub",
                40.6413, -73.7781, 820.0,
                "JFK Airport", "New York", "USA"));

        siteService.addSite(new ManageConsumptionSiteUseCase.AddSiteCommand(
                globalFreight.getId(), "ORD Air Hub", "Midwest air cargo",
                41.9742, -87.9073, 560.0,
                "O'Hare International Airport", "Chicago", "USA"));

        siteService.addSite(new ManageConsumptionSiteUseCase.AddSiteCommand(
                globalFreight.getId(), "SEA Terminal", "Pacific Northwest",
                47.4502, -122.3088, 340.0,
                "Seattle-Tacoma Airport", "Seattle", "USA"));

        // ---- FastLog Inc (CMP-003) ----
        Company fastLog = companyService.createCompany(
                new ManageCompanyUseCase.CreateCompanyCommand(
                        "FastLog Inc", Company.Type.BOTH,
                        "55-1122334", "Maria Garcia",
                        "m@fastlog.com", "+1-555-0300", null));

        siteService.addSite(new ManageConsumptionSiteUseCase.AddSiteCommand(
                fastLog.getId(), "LAX Cargo", "West Coast gateway",
                33.9425, -118.4081, 610.0,
                "LAX Airport", "Los Angeles", "USA"));

        siteService.addSite(new ManageConsumptionSiteUseCase.AddSiteCommand(
                fastLog.getId(), "DFW Depot", "Texas hub",
                32.8998, -97.0403, 290.0,
                "DFW Airport", "Dallas", "USA"));

        siteService.addSite(new ManageConsumptionSiteUseCase.AddSiteCommand(
                fastLog.getId(), "ATL Warehouse", "Southeast gateway",
                33.6407, -84.4277, 340.0,
                "Hartsfield-Jackson Airport", "Atlanta", "USA"));

        // ---- Additional companies — no sites, matching CompanyListScreen rows ----
        companyService.createCompany(new ManageCompanyUseCase.CreateCompanyCommand(
                "BayArea Transport", Company.Type.CARRIER, null,
                null, null, null, null));

        Company pacific = companyService.createCompany(new ManageCompanyUseCase.CreateCompanyCommand(
                "Pacific Shipping", Company.Type.SHIPPER, null,
                null, null, null, null));

        companyService.createCompany(new ManageCompanyUseCase.CreateCompanyCommand(
                "Metro Moves", Company.Type.BOTH, null,
                null, null, null, null));

        Company apex = companyService.createCompany(new ManageCompanyUseCase.CreateCompanyCommand(
                "Apex Logistics", Company.Type.CARRIER, null,
                null, null, null, null));
        // Apex is pending — set it explicitly
        // (CompanyService creates ACTIVE by default; real app would accept status in command)

        companyService.createCompany(new ManageCompanyUseCase.CreateCompanyCommand(
                "SkyBridge Co", Company.Type.SHIPPER, null,
                null, null, null, null));
    }
}
