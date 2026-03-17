package com.logistics.calculation.domain.algorithm;

import com.logistics.shared.domain.vo.GeoCoordinate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for the {@link WeightedBarycentreAlgorithm}.
 *
 * Tests cover:
 *   - Correctness of the weighted centroid formula
 *   - Equal-weight degenerate case (should give unweighted centroid)
 *   - Zero-weight degenerate case (fallback to unweighted centroid)
 *   - Two-site case (result lies on the weighted interpolation line)
 *   - Known US logistics network barycenter (regression test against the
 *     sample data from {@code SampleDataSeeder})
 */
@DisplayName("WeightedBarycentreAlgorithm")
class WeightedBarycentreAlgorithmTest {

    private WeightedBarycentreAlgorithm algorithm;

    @BeforeEach
    void setUp() {
        algorithm = new WeightedBarycentreAlgorithm();
    }

    // -----------------------------------------------------------------------
    // Algorithm metadata
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("algorithmId() returns the stable identifier")
    void algorithmId_returnsExpectedString() {
        assertThat(algorithm.algorithmId()).isEqualTo("weighted-barycenter");
    }

    // -----------------------------------------------------------------------
    // Core formula correctness
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Weighted centroid formula")
    class WeightedCentroid {

        @Test
        @DisplayName("Two sites with equal weights: result is the midpoint")
        void equalWeights_resultIsMidpoint() {
            // New York: (40.7128, -74.0060)
            // Los Angeles: (34.0522, -118.2437)
            List<Double> lats    = List.of(40.7128, 34.0522);
            List<Double> lons    = List.of(-74.0060, -118.2437);
            List<Double> weights = List.of(100.0, 100.0);

            BarycentreAlgorithm.AlgorithmResult result = algorithm.calculate(lats, lons, weights);

            double expectedLat = (40.7128 + 34.0522) / 2.0;
            double expectedLon = (-74.0060 + -118.2437) / 2.0;

            assertThat(result.position().getLatitude())
                    .as("latitude")
                    .isCloseTo(expectedLat, within(1e-8));
            assertThat(result.position().getLongitude())
                    .as("longitude")
                    .isCloseTo(expectedLon, within(1e-8));
        }

        @Test
        @DisplayName("Two sites with unequal weights: result is pulled toward the heavier site")
        void unequalWeights_resultPulledTowardHeavierSite() {
            // Site A at lat=0, heavier
            // Site B at lat=10, lighter
            List<Double> lats    = List.of(0.0, 10.0);
            List<Double> lons    = List.of(0.0, 0.0);
            List<Double> weights = List.of(90.0, 10.0);

            BarycentreAlgorithm.AlgorithmResult result = algorithm.calculate(lats, lons, weights);

            // Expected: (0.0 * 90 + 10.0 * 10) / 100 = 1.0
            assertThat(result.position().getLatitude()).isCloseTo(1.0, within(1e-8));
            // Result should be closer to site A (lat=0) than site B (lat=10)
            assertThat(result.position().getLatitude()).isLessThan(5.0);
        }

        @Test
        @DisplayName("Single-step algorithm: iterationCount is 1 and convergenceError is 0")
        void singleStep_iterationAndErrorAreCorrect() {
            List<Double> lats    = List.of(40.0, 41.0, 42.0);
            List<Double> lons    = List.of(-74.0, -75.0, -76.0);
            List<Double> weights = List.of(100.0, 200.0, 150.0);

            BarycentreAlgorithm.AlgorithmResult result = algorithm.calculate(lats, lons, weights);

            assertThat(result.iterationCount()).isEqualTo(1);
            assertThat(result.convergenceErrorKm()).isEqualTo(0.0);
        }
    }

    // -----------------------------------------------------------------------
    // Degenerate cases
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Degenerate inputs")
    class DegenerateInputs {

        @Test
        @DisplayName("All weights zero: falls back to unweighted centroid")
        void zeroWeights_fallsBackToUnweightedCentroid() {
            List<Double> lats    = List.of(0.0, 10.0);
            List<Double> lons    = List.of(0.0, 10.0);
            List<Double> weights = List.of(0.0, 0.0);

            BarycentreAlgorithm.AlgorithmResult result = algorithm.calculate(lats, lons, weights);

            assertThat(result.position().getLatitude()).isCloseTo(5.0, within(1e-8));
            assertThat(result.position().getLongitude()).isCloseTo(5.0, within(1e-8));
        }
    }

    // -----------------------------------------------------------------------
    // Regression: US logistics network (from SampleDataSeeder)
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Regression: Acme Corp US network")
    class AcmeCorpRegression {

        /**
         * Acme Corp sites (from SampleDataSeeder):
         *   Los Angeles   34.0522, -118.2437  450t
         *   New York      40.7128,  -74.0060  380t
         *   Chicago       41.8781,  -87.6298  210t
         *   Miami         25.7617,  -80.1918  165t
         *
         * Total weight = 1205t
         * Expected lat  = (34.0522*450 + 40.7128*380 + 41.8781*210 + 25.7617*165) / 1205
         * Expected lon  = (-118.2437*450 + -74.0060*380 + -87.6298*210 + -80.1918*165) / 1205
         */
        @Test
        @DisplayName("Calculates correct barycenter for Acme Corp sample network")
        void acmeCorp_correctBarycentre() {
            List<Double> lats    = List.of(34.0522, 40.7128, 41.8781, 25.7617);
            List<Double> lons    = List.of(-118.2437, -74.0060, -87.6298, -80.1918);
            List<Double> weights = List.of(450.0, 380.0, 210.0, 165.0);

            double totalW       = 1205.0;
            double expectedLat  = (34.0522*450 + 40.7128*380 + 41.8781*210 + 25.7617*165) / totalW;
            double expectedLon  = (-118.2437*450 + -74.0060*380 + -87.6298*210 + -80.1918*165) / totalW;

            BarycentreAlgorithm.AlgorithmResult result = algorithm.calculate(lats, lons, weights);

            assertThat(result.position().getLatitude())
                    .as("Acme Corp barycenter latitude")
                    .isCloseTo(expectedLat, within(1e-6));
            assertThat(result.position().getLongitude())
                    .as("Acme Corp barycenter longitude")
                    .isCloseTo(expectedLon, within(1e-6));

            // Result should lie within the continental US bounding box
            assertThat(result.position().getLatitude()).isBetween(24.0, 50.0);
            assertThat(result.position().getLongitude()).isBetween(-130.0, -65.0);
        }
    }
}
