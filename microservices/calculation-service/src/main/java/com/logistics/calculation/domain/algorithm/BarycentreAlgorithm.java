package com.logistics.calculation.domain.algorithm;

import com.logistics.shared.domain.vo.GeoCoordinate;

import java.util.List;

/**
 * Strategy interface for barycenter calculation algorithms.
 *
 * Applying the Strategy pattern here allows adding new algorithm variants
 * (e.g. mini-sum with road distances, gravity model) without modifying the
 * calculation service orchestration code.
 */
public interface BarycentreAlgorithm {

    /**
     * Calculates the optimal logistics center position from a set of weighted sites.
     *
     * @param latitudes   decimal-degree latitudes of input sites
     * @param longitudes  decimal-degree longitudes of input sites
     * @param weightsTons traffic volume (tons) for each site
     * @return algorithm result containing the position and metadata
     */
    AlgorithmResult calculate(List<Double> latitudes,
                              List<Double> longitudes,
                              List<Double> weightsTons);

    /**
     * A short, stable identifier for this algorithm variant.
     * Used as the {@code algorithmDescription} on the {@code LogisticsCenter} entity.
     *
     * @return algorithm identifier, e.g. {@code "weighted-barycenter"} or {@code "weiszfeld-iterative"}
     */
    String algorithmId();

    /**
     * Carries the result of one algorithm execution.
     *
     * @param position          the calculated optimal coordinate
     * @param iterationCount    number of iterations performed (1 for single-step algorithms)
     * @param convergenceErrorKm residual displacement in km at last iteration (0 for single-step)
     */
    record AlgorithmResult(
            GeoCoordinate position,
            int           iterationCount,
            double        convergenceErrorKm) {}
}
