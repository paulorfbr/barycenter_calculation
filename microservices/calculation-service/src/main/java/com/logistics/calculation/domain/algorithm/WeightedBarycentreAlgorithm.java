package com.logistics.calculation.domain.algorithm;

import com.logistics.shared.domain.vo.GeoCoordinate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Single-step weighted geographic centroid algorithm.
 *
 * Formula:
 *   lat* = sum(w_i * lat_i) / sum(w_i)
 *   lon* = sum(w_i * lon_i) / sum(w_i)
 *
 * This minimises the sum of weighted squared Euclidean distances in
 * decimal-degree space.  It is O(n), always converges, and gives exact
 * results.  Suitable for logistics networks within a single continent
 * where the flat-earth approximation is acceptable.
 *
 * Thread-safety: this class is stateless and safe for concurrent use.
 */
@Component
public final class WeightedBarycentreAlgorithm implements BarycentreAlgorithm {

    @Override
    public AlgorithmResult calculate(List<Double> latitudes,
                                     List<Double> longitudes,
                                     List<Double> weightsTons) {
        double sumW   = 0.0;
        double sumLat = 0.0;
        double sumLon = 0.0;

        for (int i = 0; i < latitudes.size(); i++) {
            double w = weightsTons.get(i);
            sumW   += w;
            sumLat += w * latitudes.get(i);
            sumLon += w * longitudes.get(i);
        }

        GeoCoordinate position;
        if (sumW <= 0.0) {
            // Degenerate case: all weights are zero — fall back to unweighted centroid
            double uLat = latitudes.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            double uLon = longitudes.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            position = GeoCoordinate.of(uLat, uLon);
        } else {
            position = GeoCoordinate.of(sumLat / sumW, sumLon / sumW);
        }

        return new AlgorithmResult(position, 1, 0.0);
    }

    @Override
    public String algorithmId() {
        return "weighted-barycenter";
    }
}
