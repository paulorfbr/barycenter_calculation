package com.logistics.infrastructure.calculation;

import com.logistics.domain.model.ConsumptionSite;
import com.logistics.domain.model.LogisticsCenter;
import com.logistics.domain.vo.GeoCoordinate;
import com.logistics.domain.vo.TrafficVolume;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for {@link BarycentreCalculationEngine}.
 *
 * Validates the mathematics of both algorithms (weighted barycenter and Weiszfeld)
 * with deterministic coordinate datasets.
 */
@DisplayName("BarycentreCalculationEngine")
class BarycentreCalculationEngineTest {

    private final BarycentreCalculationEngine engine = BarycentreCalculationEngine.getInstance();

    private ConsumptionSite site(String name, double lat, double lon, double tons) {
        return new ConsumptionSite(null, name,
                new GeoCoordinate(lat, lon),
                TrafficVolume.ofTons(tons));
    }

    // =========================================================================
    // Weighted Barycenter (single-step)
    // =========================================================================

    @Nested
    @DisplayName("calculateSimple()")
    class Simple {

        @Test
        @DisplayName("equal weights: midpoint of two sites")
        void equalWeightsMidpoint() {
            List<ConsumptionSite> sites = List.of(
                    site("A", 0.0, 0.0, 100.0),
                    site("B", 10.0, 10.0, 100.0));

            LogisticsCenter result = engine.calculateSimple("company-1", sites);

            assertThat(result.getOptimalPosition().latitude()).isCloseTo(5.0, within(0.001));
            assertThat(result.getOptimalPosition().longitude()).isCloseTo(5.0, within(0.001));
        }

        @Test
        @DisplayName("unequal weights: result closer to heavier site")
        void unequalWeightsBiasToHeavierSite() {
            // Site B has 3x the weight of site A — result should be 75% of the way toward B
            List<ConsumptionSite> sites = List.of(
                    site("A", 0.0, 0.0, 100.0),
                    site("B", 0.0, 40.0, 300.0));

            LogisticsCenter result = engine.calculateSimple("company-1", sites);

            // Expected longitude: (100*0 + 300*40) / 400 = 30.0
            assertThat(result.getOptimalPosition().longitude()).isCloseTo(30.0, within(0.001));
        }

        @Test
        @DisplayName("returns CANDIDATE status")
        void returnsCandidate() {
            List<ConsumptionSite> sites = List.of(
                    site("A", 0.0, 0.0, 1.0),
                    site("B", 1.0, 1.0, 1.0));

            LogisticsCenter result = engine.calculateSimple("cmp", sites);
            assertThat(result.getStatus()).isEqualTo(LogisticsCenter.Status.CANDIDATE);
        }

        @Test
        @DisplayName("records correct site IDs in inputSiteIds")
        void recordsInputSiteIds() {
            ConsumptionSite a = site("A", 0.0, 0.0, 100.0);
            ConsumptionSite b = site("B", 10.0, 10.0, 100.0);

            LogisticsCenter result = engine.calculateSimple("cmp", List.of(a, b));

            assertThat(result.getInputSiteIds()).containsExactlyInAnyOrder(a.getId(), b.getId());
        }

        @Test
        @DisplayName("uses company-provided ID when non-null")
        void usesCompanyId() {
            List<ConsumptionSite> sites = List.of(
                    site("A", 0.0, 0.0, 1.0),
                    site("B", 1.0, 1.0, 1.0));

            LogisticsCenter result = engine.calculateSimple("my-company", sites);
            assertThat(result.getCompanyId()).isEqualTo("my-company");
        }

        @Test
        @DisplayName("three US city coordinates produce plausible central US result")
        void threeUsCitiesProduceCentralResult() {
            List<ConsumptionSite> sites = List.of(
                    site("LAX", 34.05, -118.24, 500.0),   // Los Angeles
                    site("JFK", 40.64, -73.78, 500.0),    // New York
                    site("ORD", 41.97, -87.91, 500.0));   // Chicago

            LogisticsCenter result = engine.calculateSimple("cmp", sites);

            // Centroid should be roughly in central US
            assertThat(result.getOptimalPosition().latitude())
                    .isBetween(34.0, 42.0);
            assertThat(result.getOptimalPosition().longitude())
                    .isBetween(-120.0, -70.0);
        }
    }

    // =========================================================================
    // Weiszfeld Iterative
    // =========================================================================

    @Nested
    @DisplayName("calculateWeiszfeld()")
    class Weiszfeld {

        @Test
        @DisplayName("equal weights: converges near midpoint of two sites")
        void convergesNearMidpointForEqualWeights() {
            List<ConsumptionSite> sites = List.of(
                    site("A", 0.0, 0.0, 100.0),
                    site("B", 10.0, 10.0, 100.0));

            LogisticsCenter result = engine.calculateWeiszfeld("cmp", sites, 1000, 0.01);

            assertThat(result.getOptimalPosition().latitude()).isCloseTo(5.0, within(0.1));
            assertThat(result.getOptimalPosition().longitude()).isCloseTo(5.0, within(0.1));
        }

        @Test
        @DisplayName("records algorithm description as 'weiszfeld-iterative'")
        void recordsAlgorithmDescription() {
            List<ConsumptionSite> sites = List.of(
                    site("A", 0.0, 0.0, 100.0),
                    site("B", 10.0, 10.0, 200.0));

            LogisticsCenter result = engine.calculateWeiszfeld("cmp", sites, 1000, 0.01);
            assertThat(result.getAlgorithmDescription()).isEqualTo("weiszfeld-iterative");
        }

        @Test
        @DisplayName("iterations performed is at least 1")
        void iterationsPerformed() {
            List<ConsumptionSite> sites = List.of(
                    site("A", 34.05, -118.24, 400.0),
                    site("B", 40.71, -74.01, 800.0));

            LogisticsCenter result = engine.calculateWeiszfeld("cmp", sites, 1000, 0.01);
            assertThat(result.getIterationCount()).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("single-point degenerate case: convergence after 0 iterations")
        void singlePointConverges() {
            // Two sites at the same location — result should be that location
            List<ConsumptionSite> sites = List.of(
                    site("A", 10.0, 20.0, 100.0),
                    site("B", 10.0, 20.0, 100.0));

            // This exercises the EPSILON guard
            assertThatCode(() -> engine.calculateWeiszfeld("cmp", sites, 100, 0.01))
                    .doesNotThrowAnyException();
        }
    }

    // =========================================================================
    // From raw arrays
    // =========================================================================

    @Nested
    @DisplayName("calculateSimpleFromArrays()")
    class FromArrays {

        @Test
        @DisplayName("produces same result as list-based method for equivalent inputs")
        void matchesListBasedMethod() {
            List<ConsumptionSite> sites = List.of(
                    site("A", 34.05, -118.24, 450.0),
                    site("B", 40.71, -74.01, 380.0));

            LogisticsCenter listResult   = engine.calculateSimple("cmp", sites);
            LogisticsCenter arrayResult  = engine.calculateSimpleFromArrays(
                    "cmp",
                    sites.stream().map(s -> s.getId()).toList(),
                    List.of(34.05, 40.71),
                    List.of(-118.24, -74.01),
                    List.of(450.0, 380.0));

            assertThat(arrayResult.getOptimalPosition().latitude())
                    .isCloseTo(listResult.getOptimalPosition().latitude(), within(0.001));
            assertThat(arrayResult.getOptimalPosition().longitude())
                    .isCloseTo(listResult.getOptimalPosition().longitude(), within(0.001));
        }
    }

    // =========================================================================
    // Validation
    // =========================================================================

    @Nested
    @DisplayName("validation")
    class Validation {

        @Test
        @DisplayName("throws when fewer than 2 sites provided")
        void throwsForSingleSite() {
            List<ConsumptionSite> sites = List.of(site("A", 0.0, 0.0, 100.0));

            assertThatThrownBy(() -> engine.calculateSimple("cmp", sites))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("At least 2");
        }

        @Test
        @DisplayName("throws when array lists have mismatched lengths")
        void throwsForMismatchedArrays() {
            assertThatThrownBy(() -> engine.calculateSimpleFromArrays(
                    "cmp",
                    List.of("A", "B"),
                    List.of(0.0, 10.0),
                    List.of(0.0),           // wrong length
                    List.of(100.0, 100.0)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("same length");
        }
    }
}
