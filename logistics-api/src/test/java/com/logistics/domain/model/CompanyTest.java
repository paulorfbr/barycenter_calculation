package com.logistics.domain.model;

import com.logistics.domain.vo.GeoCoordinate;
import com.logistics.domain.vo.TrafficVolume;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link Company} domain aggregate.
 */
@DisplayName("Company aggregate")
class CompanyTest {

    private Company newCompany() {
        return new Company(null, "Test Corp", Company.Type.SHIPPER, Company.Status.ACTIVE);
    }

    @Nested
    @DisplayName("constructor")
    class Constructor {

        @Test
        @DisplayName("auto-generates ID when null is passed")
        void autoGeneratesId() {
            Company company = new Company(null, "Corp", Company.Type.BOTH, Company.Status.PENDING);
            assertThat(company.getId()).isNotNull().isNotBlank();
        }

        @Test
        @DisplayName("uses provided ID when non-null")
        void usesProvidedId() {
            Company company = new Company("custom-id", "Corp", Company.Type.BOTH, Company.Status.ACTIVE);
            assertThat(company.getId()).isEqualTo("custom-id");
        }

        @Test
        @DisplayName("throws when name is blank")
        void throwsForBlankName() {
            assertThatThrownBy(() -> new Company(null, "  ", Company.Type.BOTH, Company.Status.ACTIVE))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Company name");
        }

        @Test
        @DisplayName("throws when name is null")
        void throwsForNullName() {
            assertThatThrownBy(() -> new Company(null, null, Company.Type.BOTH, Company.Status.ACTIVE))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("lifecycle transitions")
    class Lifecycle {

        @Test
        @DisplayName("activate() sets status to ACTIVE")
        void activate() {
            Company company = new Company(null, "Corp", Company.Type.SHIPPER, Company.Status.INACTIVE);
            company.activate();
            assertThat(company.getStatus()).isEqualTo(Company.Status.ACTIVE);
        }

        @Test
        @DisplayName("deactivate() sets status to INACTIVE")
        void deactivate() {
            Company company = newCompany();
            company.deactivate();
            assertThat(company.getStatus()).isEqualTo(Company.Status.INACTIVE);
        }
    }

    @Nested
    @DisplayName("consumption site management")
    class Sites {

        @Test
        @DisplayName("addConsumptionSite() adds and assigns company ID")
        void addsSiteAndAssignsCompanyId() {
            Company company = newCompany();
            ConsumptionSite site = new ConsumptionSite(
                    null, "Site A",
                    new GeoCoordinate(0.0, 0.0),
                    TrafficVolume.ofTons(100.0));

            company.addConsumptionSite(site);

            assertThat(company.getConsumptionSites()).hasSize(1);
            assertThat(site.getCompanyId()).isEqualTo(company.getId());
        }

        @Test
        @DisplayName("removeConsumptionSite() removes by site ID")
        void removesSite() {
            Company company = newCompany();
            ConsumptionSite site = new ConsumptionSite(null, "Site A",
                    new GeoCoordinate(0.0, 0.0), TrafficVolume.ofTons(100.0));
            company.addConsumptionSite(site);

            boolean removed = company.removeConsumptionSite(site.getId());

            assertThat(removed).isTrue();
            assertThat(company.getConsumptionSites()).isEmpty();
        }

        @Test
        @DisplayName("getConsumptionSites() returns unmodifiable view")
        void returnsUnmodifiableList() {
            Company company = newCompany();
            assertThatThrownBy(() -> company.getConsumptionSites().add(null))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("totalTrafficTons() sums tonnage across all sites")
        void totalTrafficTons() {
            Company company = newCompany();
            company.addConsumptionSite(new ConsumptionSite(null, "A",
                    new GeoCoordinate(0.0, 0.0), TrafficVolume.ofTons(300.0)));
            company.addConsumptionSite(new ConsumptionSite(null, "B",
                    new GeoCoordinate(1.0, 1.0), TrafficVolume.ofTons(700.0)));

            assertThat(company.totalTrafficTons()).isEqualTo(1000.0);
        }
    }

    @Nested
    @DisplayName("equality and hashing")
    class Equality {

        @Test
        @DisplayName("two companies with same ID are equal")
        void sameIdIsEqual() {
            Company a = new Company("same-id", "Alpha", Company.Type.BOTH, Company.Status.ACTIVE);
            Company b = new Company("same-id", "Beta",  Company.Type.CARRIER, Company.Status.INACTIVE);
            assertThat(a).isEqualTo(b);
        }

        @Test
        @DisplayName("two companies with different IDs are not equal")
        void differentIdIsNotEqual() {
            Company a = newCompany();
            Company b = newCompany();
            assertThat(a).isNotEqualTo(b);
        }
    }
}
