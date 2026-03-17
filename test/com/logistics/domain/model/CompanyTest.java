package com.logistics.domain.model;

import com.logistics.domain.vo.GeoCoordinate;
import com.logistics.domain.vo.TrafficVolume;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Company} aggregate root.
 */
@DisplayName("Company aggregate")
class CompanyTest {

    private Company activeCompany() {
        return new Company(null, "Acme Corp", Company.Type.SHIPPER, Company.Status.ACTIVE);
    }

    private ConsumptionSite siteFor(String companyId) {
        return new ConsumptionSite(null, "Test Site",
                new GeoCoordinate(34.0, -118.0), TrafficVolume.ofTons(100.0));
    }

    // -------------------------------------------------------------------------
    // Construction
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("auto-generates id when null supplied")
    void autoGeneratesId() {
        Company c = new Company(null, "Test", Company.Type.CARRIER, Company.Status.ACTIVE);
        assertNotNull(c.getId());
        assertFalse(c.getId().isBlank());
    }

    @Test
    @DisplayName("preserves explicit id")
    void preservesExplicitId() {
        Company c = new Company("CMP-001", "Test", Company.Type.CARRIER, Company.Status.ACTIVE);
        assertEquals("CMP-001", c.getId());
    }

    @Test
    @DisplayName("rejects blank name")
    void rejectsBlankName() {
        assertThrows(IllegalArgumentException.class,
                () -> new Company(null, "  ", Company.Type.SHIPPER, Company.Status.ACTIVE));
    }

    @Test
    @DisplayName("rejects null type")
    void rejectsNullType() {
        assertThrows(NullPointerException.class,
                () -> new Company(null, "Test", null, Company.Status.ACTIVE));
    }

    // -------------------------------------------------------------------------
    // Status transitions
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("activate() sets status to ACTIVE")
    void activateSetsStatusToActive() {
        Company c = new Company(null, "Test", Company.Type.SHIPPER, Company.Status.INACTIVE);
        c.activate();
        assertEquals(Company.Status.ACTIVE, c.getStatus());
    }

    @Test
    @DisplayName("deactivate() sets status to INACTIVE")
    void deactivateSetsStatusToInactive() {
        Company c = activeCompany();
        c.deactivate();
        assertEquals(Company.Status.INACTIVE, c.getStatus());
    }

    // -------------------------------------------------------------------------
    // Consumption site management
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("addConsumptionSite() appends site to collection")
    void addConsumptionSiteAppendsToCollection() {
        Company c = activeCompany();
        ConsumptionSite site = siteFor(c.getId());
        c.addConsumptionSite(site);
        assertEquals(1, c.getConsumptionSites().size());
        assertTrue(c.getConsumptionSites().contains(site));
    }

    @Test
    @DisplayName("addConsumptionSite() sets site's companyId")
    void addConsumptionSiteSetsCompanyId() {
        Company c = activeCompany();
        ConsumptionSite site = siteFor(null);
        c.addConsumptionSite(site);
        assertEquals(c.getId(), site.getCompanyId());
    }

    @Test
    @DisplayName("addConsumptionSite() rejects site already owned by another company")
    void addConsumptionSiteRejectsSiteAlreadyOwnedByOther() {
        Company c1 = new Company(null, "Co1", Company.Type.SHIPPER, Company.Status.ACTIVE);
        Company c2 = new Company(null, "Co2", Company.Type.SHIPPER, Company.Status.ACTIVE);
        ConsumptionSite site = siteFor(null);
        c1.addConsumptionSite(site);
        assertThrows(IllegalArgumentException.class, () -> c2.addConsumptionSite(site));
    }

    @Test
    @DisplayName("removeConsumptionSite() returns true and removes site")
    void removeConsumptionSiteRemovesSite() {
        Company c = activeCompany();
        ConsumptionSite site = siteFor(null);
        c.addConsumptionSite(site);
        boolean removed = c.removeConsumptionSite(site.getId());
        assertTrue(removed);
        assertTrue(c.getConsumptionSites().isEmpty());
    }

    @Test
    @DisplayName("removeConsumptionSite() returns false for unknown id")
    void removeConsumptionSiteReturnsFalseForUnknownId() {
        Company c = activeCompany();
        assertFalse(c.removeConsumptionSite("unknown-id"));
    }

    @Test
    @DisplayName("getConsumptionSites() returns unmodifiable view")
    void getConsumptionSitesReturnsUnmodifiableView() {
        Company c = activeCompany();
        assertThrows(UnsupportedOperationException.class,
                () -> c.getConsumptionSites().add(siteFor(null)));
    }

    // -------------------------------------------------------------------------
    // Traffic aggregation
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("totalTrafficTons() sums all site tonnages")
    void totalTrafficTonsSumsAllSites() {
        Company c = activeCompany();
        ConsumptionSite s1 = new ConsumptionSite(null, "S1",
                new GeoCoordinate(0.0, 0.0), TrafficVolume.ofTons(300.0));
        ConsumptionSite s2 = new ConsumptionSite(null, "S2",
                new GeoCoordinate(1.0, 1.0), TrafficVolume.ofTons(200.0));
        c.addConsumptionSite(s1);
        c.addConsumptionSite(s2);
        assertEquals(500.0, c.totalTrafficTons(), 1e-9);
    }

    // -------------------------------------------------------------------------
    // Equality
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("two companies with same id are equal")
    void equalityById() {
        Company a = new Company("same-id", "Name A", Company.Type.SHIPPER, Company.Status.ACTIVE);
        Company b = new Company("same-id", "Name B", Company.Type.CARRIER, Company.Status.INACTIVE);
        assertEquals(a, b);
    }
}
