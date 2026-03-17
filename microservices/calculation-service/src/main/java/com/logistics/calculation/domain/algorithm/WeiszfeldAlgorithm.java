package com.logistics.calculation.domain.algorithm;

import com.logistics.shared.domain.vo.GeoCoordinate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Weiszfeld iterative algorithm for the weighted geometric median.
 *
 * Minimises the weighted sum of Haversine (geodesic) distances to all sites,
 * which directly corresponds to minimising total weighted transport cost.
 * This is a better objective for logistics than the squared-distance centroid
 * when the transport cost scales linearly with distance.
 *
 * Algorithm:
 *   Warm-start from the weighted centroid, then iterate:
 *
 *     d_i     = haversine(current, site_i)  (clamped to EPSILON_KM to avoid ÷0)
 *     num_lat = sum( w_i * lat_i / d_i )
 *     num_lon = sum( w_i * lon_i / d_i )
 *     den     = sum( w_i         / d_i )
 *
 *     next = (num_lat / den, num_lon / den)
 *
 *   Until: haversine(current, next) < toleranceKm  OR  iterations >= maxIterations
 *
 * Convergence: guaranteed for the geometric median problem (Weiszfeld 1937).
 * Typical convergence for logistics networks: < 50 iterations at 0.01 km tolerance.
 *
 * Thread-safety: stateless, safe for concurrent use.
 */
@Component
public final class WeiszfeldAlgorithm implements BarycentreAlgorithm {

    public static final int    DEFAULT_MAX_ITERATIONS = 1_000;
    public static final double DEFAULT_TOLERANCE_KM   = 0.01;

    /** Minimum distance guard to prevent division by zero. */
    private static final double EPSILON_KM = 1e-6;

    private final int    maxIterations;
    private final double toleranceKm;

    /** Default constructor — uses production defaults. */
    public WeiszfeldAlgorithm() {
        this(DEFAULT_MAX_ITERATIONS, DEFAULT_TOLERANCE_KM);
    }

    /**
     * Configurable constructor — useful for testing convergence behaviour.
     *
     * @param maxIterations iteration cap
     * @param toleranceKm   convergence threshold in kilometres
     */
    public WeiszfeldAlgorithm(int maxIterations, double toleranceKm) {
        if (maxIterations < 1)  throw new IllegalArgumentException("maxIterations must be >= 1");
        if (toleranceKm   <= 0) throw new IllegalArgumentException("toleranceKm must be > 0");
        this.maxIterations = maxIterations;
        this.toleranceKm   = toleranceKm;
    }

    @Override
    public AlgorithmResult calculate(List<Double> latitudes,
                                     List<Double> longitudes,
                                     List<Double> weightsTons) {
        // Warm-start: use the weighted centroid as the initial estimate
        GeoCoordinate current = weightedCentroid(latitudes, longitudes, weightsTons);

        int    iteration = 0;
        double error     = Double.MAX_VALUE;

        while (iteration < maxIterations && error > toleranceKm) {
            double numLat = 0.0;
            double numLon = 0.0;
            double den    = 0.0;

            for (int i = 0; i < latitudes.size(); i++) {
                GeoCoordinate site = GeoCoordinate.of(latitudes.get(i), longitudes.get(i));
                double d  = Math.max(current.distanceKmTo(site), EPSILON_KM);
                double wd = weightsTons.get(i) / d;
                numLat += wd * latitudes.get(i);
                numLon += wd * longitudes.get(i);
                den    += wd;
            }

            if (den <= 0.0) break;

            GeoCoordinate next = GeoCoordinate.of(numLat / den, numLon / den);
            error   = current.distanceKmTo(next);
            current = next;
            iteration++;
        }

        return new AlgorithmResult(current, iteration, error);
    }

    @Override
    public String algorithmId() {
        return "weiszfeld-iterative";
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    private static GeoCoordinate weightedCentroid(List<Double> lats,
                                                   List<Double> lons,
                                                   List<Double> weights) {
        double sumW   = 0.0;
        double sumLat = 0.0;
        double sumLon = 0.0;
        for (int i = 0; i < lats.size(); i++) {
            sumW   += weights.get(i);
            sumLat += weights.get(i) * lats.get(i);
            sumLon += weights.get(i) * lons.get(i);
        }
        if (sumW <= 0.0) {
            return GeoCoordinate.of(
                    lats.stream().mapToDouble(Double::doubleValue).average().orElse(0.0),
                    lons.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
        }
        return GeoCoordinate.of(sumLat / sumW, sumLon / sumW);
    }
}
