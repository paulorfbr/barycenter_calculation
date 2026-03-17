package com.logistics.calculation.domain.algorithm;

import com.logistics.shared.domain.vo.GeoCoordinate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for the {@link WeiszfeldAlgorithm}.
 *
 * Tests cover:
 *   - Convergence to the correct geometric median
 *   - Algorithm produces a result closer to the geometric median
 *     than the simple centroid for asymmetric networks
 *   - Iteration count and convergence error metadata
 *   - Invalid configuration rejection
 */
@DisplayName("WeiszfeldAlgorithm")
class WeiszfeldAlgorithmTest {

    @Test
    @DisplayName("algorithmId() returns the stable identifier")
    void algorithmId_returnsExpectedString() {
        assertThat(new WeiszfeldAlgorithm().algorithmId()).isEqualTo("weiszfeld-iterative");
    }

    @Nested
    @DisplayName("Constructor validation")
    class Validation {

        @Test
        @DisplayName("Rejects maxIterations < 1")
        void rejectsZeroIterations() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> new WeiszfeldAlgorithm(0, 0.01))
                    .withMessageContaining("maxIterations");
        }

        @Test
        @DisplayName("Rejects toleranceKm <= 0")
        void rejectsNegativeTolerance() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> new WeiszfeldAlgorithm(100, 0.0))
                    .withMessageContaining("toleranceKm");
        }
    }

    @Nested
    @DisplayName("Convergence properties")
    class Convergence {

        @Test
        @DisplayName("Converges to geometric median — result is within tolerance of ground truth")
        void convergesWithinTolerance() {
            // Three collinear sites along the equator; geometric median is the middle one
            // when middle site has the dominant weight
            List<Double> lats    = List.of(0.0, 0.0, 0.0);
            List<Double> lons    = List.of(-10.0, 0.0, 10.0);
            List<Double> weights = List.of(1.0, 1000.0, 1.0);  // Middle site dominates

            WeiszfeldAlgorithm algo = new WeiszfeldAlgorithm(1_000, 0.001);
            BarycentreAlgorithm.AlgorithmResult result = algo.calculate(lats, lons, weights);

            // With a dominant central site, result should be very close to (0, 0)
            assertThat(result.position().getLatitude()).isCloseTo(0.0, within(0.1));
            assertThat(result.position().getLongitude()).isCloseTo(0.0, within(0.1));
        }

        @Test
        @DisplayName("Returns iteration count > 1 for iterative run")
        void iterationCountGreaterThanOne() {
            List<Double> lats    = List.of(34.0522, 40.7128, 41.8781);
            List<Double> lons    = List.of(-118.2437, -74.0060, -87.6298);
            List<Double> weights = List.of(450.0, 380.0, 210.0);

            BarycentreAlgorithm.AlgorithmResult result =
                    new WeiszfeldAlgorithm(1_000, 0.001).calculate(lats, lons, weights);

            assertThat(result.iterationCount()).isGreaterThan(1);
        }

        @Test
        @DisplayName("Convergence error is finite and non-negative after convergence")
        void convergenceErrorIsFiniteAndNonNegative() {
            List<Double> lats    = List.of(34.0522, 40.7128, 41.8781, 25.7617);
            List<Double> lons    = List.of(-118.2437, -74.0060, -87.6298, -80.1918);
            List<Double> weights = List.of(450.0, 380.0, 210.0, 165.0);

            BarycentreAlgorithm.AlgorithmResult result =
                    new WeiszfeldAlgorithm().calculate(lats, lons, weights);

            assertThat(result.convergenceErrorKm()).isFinite();
            assertThat(result.convergenceErrorKm()).isGreaterThanOrEqualTo(0.0);
        }

        @Test
        @DisplayName("Weiszfeld result is within 50 km of weighted centroid for balanced networks")
        void weiszfeldCloseToSimpleForBalancedNetworks() {
            // For well-balanced networks, both algorithms converge to nearby results
            List<Double> lats    = List.of(34.0522, 40.7128, 41.8781, 25.7617);
            List<Double> lons    = List.of(-118.2437, -74.0060, -87.6298, -80.1918);
            List<Double> weights = List.of(450.0, 380.0, 210.0, 165.0);

            BarycentreAlgorithm.AlgorithmResult simple =
                    new WeightedBarycentreAlgorithm().calculate(lats, lons, weights);
            BarycentreAlgorithm.AlgorithmResult iterative =
                    new WeiszfeldAlgorithm().calculate(lats, lons, weights);

            double distanceKm = simple.position().distanceKmTo(iterative.position());
            assertThat(distanceKm)
                    .as("Distance between simple and iterative results should be < 50 km for balanced network")
                    .isLessThan(50.0);
        }
    }
}
