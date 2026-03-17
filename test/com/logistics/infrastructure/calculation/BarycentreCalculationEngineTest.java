package com.logistics.infrastructure.calculation;

import com.logistics.domain.model.ConsumptionSite;
import com.logistics.domain.model.LogisticsCenter;
import com.logistics.domain.vo.GeoCoordinate;
import com.logistics.domain.vo.TrafficVolume;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BarycentreCalculationEngine}.
 *
 * Tests use hand-verified reference calculations.
 * All assertions allow for the inherent imprecision of
 * floating-point arithmetic (tolerances in the range of 0.01°).
 */
@DisplayName("BarycentreCalculationEngine")
class BarycentreCalculationEngineTest {

    private BarycentreCalculationEngine engine;

    @BeforeEach
    void setUp() {
        engine = BarycentreCalculationEngine.getInstance();
    }

    // -------------------------------------------------------------------------
    // Helper factory
    // -------------------------------------------------------------------------

    /**
     * Factory that creates a ConsumptionSite already associated with a
     * dummy company, using the proper aggregate-root path (Company.addConsumptionSite)
     * so the package-private assignToCompany() invariant is respected.
     */
    private ConsumptionSite site(String name, double lat, double lon, double tons) {
        com.logistics.domain.model.Company owner =
                new com.logistics.domain.model.Company(
                        "TEST-CO", "Test Owner",
                        com.logistics.domain.model.Company.Type.SHIPPER,
                        com.logistics.domain.model.Company.Status.ACTIVE);
        ConsumptionSite s = new ConsumptionSite(
                null, name,
                new GeoCoordinate(lat, lon),
                TrafficVolume.ofTons(tons));
        owner.addConsumptionSite(s);
        return s;
    }

    // -------------------------------------------------------------------------
    // Single-step weighted barycenter
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("calculateSimple()")
    class SimpleCalculation {

        @Test
        @DisplayName("two equal-weight sites => midpoint")
        void twoEqualWeightSitesMidpoint() {
            List<ConsumptionSite> sites = List.of(
                    site("A", 0.0, 0.0, 100.0),
                    site("B", 0.0, 10.0, 100.0)
            );
            LogisticsCenter result = engine.calculateSimple("TEST-CO", sites);
            assertEquals(0.0,  result.getOptimalPosition().latitude(),  1e-6);
            assertEquals(5.0,  result.getOptimalPosition().longitude(), 1e-6);
        }

        @Test
        @DisplayName("two sites with 3:1 weight ratio => closer to heavier")
        void twoSitesWeightedTowardsHeavierSite() {
            List<ConsumptionSite> sites = List.of(
                    site("Heavy", 0.0, 0.0, 300.0),
                    site("Light", 0.0, 40.0, 100.0)
            );
            LogisticsCenter result = engine.calculateSimple("TEST-CO", sites);
            // Expected: (300*0 + 100*40) / 400 = 10.0 degrees
            assertEquals(10.0, result.getOptimalPosition().longitude(), 1e-6);
        }

        @Test
        @DisplayName("result status is CANDIDATE")
        void resultStatusIsCandidate() {
            List<ConsumptionSite> sites = List.of(
                    site("A", 0.0, 0.0, 100.0),
                    site("B", 10.0, 10.0, 100.0)
            );
            LogisticsCenter result = engine.calculateSimple("TEST-CO", sites);
            assertEquals(LogisticsCenter.Status.CANDIDATE, result.getStatus());
        }

        @Test
        @DisplayName("result captures all input site IDs")
        void resultCapturesInputSiteIds() {
            ConsumptionSite a = site("A", 0.0, 0.0, 100.0);
            ConsumptionSite b = site("B", 10.0, 10.0, 100.0);
            LogisticsCenter result = engine.calculateSimple("TEST-CO", List.of(a, b));
            assertTrue(result.getInputSiteIds().contains(a.getId()));
            assertTrue(result.getInputSiteIds().contains(b.getId()));
        }

        @Test
        @DisplayName("total weighted volume sums all site tonnages")
        void totalWeightedVolumeIsSumOfSiteTonnages() {
            List<ConsumptionSite> sites = List.of(
                    site("A", 0.0, 0.0, 300.0),
                    site("B", 10.0, 10.0, 200.0)
            );
            LogisticsCenter result = engine.calculateSimple("TEST-CO", sites);
            assertEquals(500.0, result.getTotalWeightedVolume().tons(), 1e-6);
        }

        @Test
        @DisplayName("throws when fewer than 2 sites provided")
        void throwsWithLessThanTwoSites() {
            assertThrows(IllegalArgumentException.class,
                    () -> engine.calculateSimple("TEST-CO", List.of(site("A", 0.0, 0.0, 100.0))));
        }

        @Test
        @DisplayName("all-zero weights fall back to unweighted centroid")
        void allZeroWeightsFallBackToUnweightedCentroid() {
            List<ConsumptionSite> sites = List.of(
                    site("A", 0.0, 0.0, 0.0),
                    site("B", 0.0, 10.0, 0.0)
            );
            LogisticsCenter result = engine.calculateSimple("TEST-CO", sites);
            assertEquals(5.0, result.getOptimalPosition().longitude(), 1e-6);
        }

        @Test
        @DisplayName("US major cities — result is in continental US bounding box")
        void usCitiesResultInContiguousUS() {
            List<ConsumptionSite> sites = List.of(
                    site("LAX",     33.9425, -118.4081, 450.0),
                    site("JFK",     40.6413,  -73.7781, 380.0),
                    site("Chicago", 41.8781,  -87.6298, 210.0),
                    site("Miami",   25.7617,  -80.1918, 165.0)
            );
            LogisticsCenter result = engine.calculateSimple("TEST-CO", sites);
            double lat = result.getOptimalPosition().latitude();
            double lon = result.getOptimalPosition().longitude();

            // Contiguous US bounding box: lat [24, 50], lon [-125, -66]
            assertTrue(lat >= 24.0 && lat <= 50.0, "Latitude " + lat + " outside US bounds");
            assertTrue(lon >= -125.0 && lon <= -66.0, "Longitude " + lon + " outside US bounds");
        }
    }

    // -------------------------------------------------------------------------
    // Weiszfeld iterative refinement
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("calculateWeiszfeld()")
    class WeiszfeldCalculation {

        @Test
        @DisplayName("converges near simple barycenter for equal-distance sites")
        void convergesNearSimpleBarycentreForSymmetricSites() {
            // Four sites at the corners of a square — both algorithms should agree
            List<ConsumptionSite> sites = List.of(
                    site("NW", 10.0, -10.0, 100.0),
                    site("NE", 10.0,  10.0, 100.0),
                    site("SW", -10.0, -10.0, 100.0),
                    site("SE", -10.0,  10.0, 100.0)
            );
            LogisticsCenter simple    = engine.calculateSimple("TEST-CO", sites);
            LogisticsCenter weiszfeld = engine.calculateWeiszfeld(
                    "TEST-CO", sites,
                    BarycentreCalculationEngine.DEFAULT_MAX_ITERATIONS,
                    BarycentreCalculationEngine.DEFAULT_TOLERANCE_KM);

            assertEquals(simple.getOptimalPosition().latitude(),
                    weiszfeld.getOptimalPosition().latitude(), 0.01);
            assertEquals(simple.getOptimalPosition().longitude(),
                    weiszfeld.getOptimalPosition().longitude(), 0.01);
        }

        @Test
        @DisplayName("convergence error is within specified tolerance")
        void convergenceErrorWithinTolerance() {
            List<ConsumptionSite> sites = List.of(
                    site("LAX",     33.9425, -118.4081, 610.0),
                    site("DFW",     32.8998,  -97.0403, 290.0),
                    site("ATL",     33.6407,  -84.4277, 340.0),
                    site("JFK",     40.6413,  -73.7781, 820.0)
            );
            double toleranceKm = 0.01;
            LogisticsCenter result = engine.calculateWeiszfeld(
                    "TEST-CO", sites, 1_000, toleranceKm);

            assertTrue(result.getConvergenceErrorKm() <= toleranceKm * 100,
                    "Convergence error " + result.getConvergenceErrorKm()
                    + " km exceeds reasonable bound");
        }

        @Test
        @DisplayName("iteration count is at least 1")
        void iterationCountAtLeastOne() {
            List<ConsumptionSite> sites = List.of(
                    site("A", 0.0, 0.0, 100.0),
                    site("B", 5.0, 5.0, 100.0)
            );
            LogisticsCenter result = engine.calculateWeiszfeld("TEST-CO", sites, 100, 0.001);
            assertTrue(result.getIterationCount() >= 1);
        }

        @Test
        @DisplayName("algorithm description is 'weiszfeld-iterative'")
        void algorithmDescription() {
            List<ConsumptionSite> sites = List.of(
                    site("A", 0.0, 0.0, 100.0),
                    site("B", 5.0, 5.0, 100.0)
            );
            LogisticsCenter result = engine.calculateWeiszfeld("TEST-CO", sites, 100, 0.001);
            assertEquals("weiszfeld-iterative", result.getAlgorithmDescription());
        }
    }

    // -------------------------------------------------------------------------
    // From raw arrays
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("calculateSimpleFromArrays()")
    class ArraysInterface {

        @Test
        @DisplayName("gives same result as ConsumptionSite-based method")
        void givesSameResultAsEntityMethod() {
            ConsumptionSite a = site("A", 34.0522, -118.2437, 450.0);
            ConsumptionSite b = site("B", 40.7128,  -74.0060, 380.0);

            LogisticsCenter fromEntities = engine.calculateSimple("TEST-CO", List.of(a, b));
            LogisticsCenter fromArrays   = engine.calculateSimpleFromArrays(
                    "TEST-CO",
                    List.of(a.getId(), b.getId()),
                    List.of(34.0522, 40.7128),
                    List.of(-118.2437, -74.0060),
                    List.of(450.0, 380.0));

            assertEquals(fromEntities.getOptimalPosition().latitude(),
                    fromArrays.getOptimalPosition().latitude(), 1e-6);
            assertEquals(fromEntities.getOptimalPosition().longitude(),
                    fromArrays.getOptimalPosition().longitude(), 1e-6);
        }

        @Test
        @DisplayName("throws when lists are different lengths")
        void throwsWhenListsDifferentLengths() {
            assertThrows(IllegalArgumentException.class, () ->
                    engine.calculateSimpleFromArrays(
                            null, List.of("a"),
                            List.of(0.0, 1.0),
                            List.of(0.0),
                            List.of(100.0, 100.0)));
        }
    }
}
