package com.logistics.infrastructure.calculation;

import com.logistics.domain.model.ConsumptionSite;
import com.logistics.domain.model.LogisticsCenter;
import com.logistics.domain.vo.GeoCoordinate;
import com.logistics.domain.vo.TrafficVolume;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Core barycenter calculation engine for logistics center optimization.
 *
 * Two algorithms are implemented:
 *
 * 1. Weighted Geographic Barycenter (single-step)
 *    -----------------------------------------------
 *    The classic weighted centroid formula applied to decimal-degree coordinates:
 *
 *        lat* = sum(w_i * lat_i) / sum(w_i)
 *        lon* = sum(w_i * lon_i) / sum(w_i)
 *
 *    where w_i is the traffic volume (tons) of site i.
 *
 *    This minimises the sum of weighted squared distances in Euclidean space and
 *    is an exact closed-form solution. For logistics networks spanning a single
 *    continent or country it gives results indistinguishable from geodesic methods.
 *
 * 2. Weiszfeld Iterative Refinement
 *    --------------------------------
 *    Minimises the weighted sum of geodesic (Haversine) distances to all sites
 *    rather than squared distances. This gives the geometric median — the point
 *    that minimises total weighted transport distance — which is more appropriate
 *    for logistics cost optimisation.
 *
 *    The Weiszfeld algorithm iterates:
 *
 *        numerator_lat   = sum( w_i * lat_i   / d_i )
 *        numerator_lon   = sum( w_i * lon_i   / d_i )
 *        denominator     = sum( w_i           / d_i )
 *
 *        next_lat = numerator_lat / denominator
 *        next_lon = numerator_lon / denominator
 *
 *    where d_i = haversine_distance(current_estimate, site_i).
 *
 *    A minimum-distance guard (EPSILON_KM) prevents division by zero when the
 *    current estimate coincides exactly with a site.
 *
 *    Convergence criterion: |displacement| < toleranceKm.
 *
 * Design notes:
 *   - This class is stateless and thread-safe.
 *   - It depends only on domain value objects; no frameworks are imported.
 *   - All floating-point arithmetic uses double precision, which is sufficient
 *     for coordinate-space barycenter computations.
 */
public final class BarycentreCalculationEngine {

    // -------------------------------------------------------------------------
    // Configuration constants
    // -------------------------------------------------------------------------

    /** Default maximum iterations for Weiszfeld refinement. */
    public static final int    DEFAULT_MAX_ITERATIONS = 1_000;

    /** Default convergence tolerance in kilometres. */
    public static final double DEFAULT_TOLERANCE_KM   = 0.01;

    /**
     * Minimum Haversine distance guard used inside Weiszfeld iterations to avoid
     * division-by-zero when the current estimate is on top of a site.
     */
    private static final double EPSILON_KM = 1e-6;

    // -------------------------------------------------------------------------
    // Singleton: stateless, so a single instance is sufficient
    // -------------------------------------------------------------------------

    private static final BarycentreCalculationEngine INSTANCE = new BarycentreCalculationEngine();

    private BarycentreCalculationEngine() {}

    public static BarycentreCalculationEngine getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Public API: from ConsumptionSite list
    // -------------------------------------------------------------------------

    /**
     * Calculates the optimal logistics center position for a list of consumption sites.
     * Uses single-step weighted barycenter (fast, suitable for interactive UI updates).
     *
     * @param companyId owning company
     * @param sites     list of active consumption sites (must have at least 2 entries)
     * @return LogisticsCenter entity with status CANDIDATE
     */
    public LogisticsCenter calculateSimple(String companyId, List<ConsumptionSite> sites) {
        validateSites(sites);
        double[] latitudes  = extractLatitudes(sites);
        double[] longitudes = extractLongitudes(sites);
        double[] weights    = extractWeights(sites);
        List<String> siteIds = extractIds(sites);

        GeoCoordinate result = weightedBarycentre(latitudes, longitudes, weights);
        double totalTons     = sumWeights(weights);

        return buildCenter(companyId, siteIds, result, totalTons,
                "weighted-barycenter", 1, 0.0);
    }

    /**
     * Calculates using the Weiszfeld iterative refinement algorithm.
     * Minimises weighted sum of geodesic transport distances.
     *
     * @param companyId     owning company
     * @param sites         list of active consumption sites
     * @param maxIterations iteration cap
     * @param toleranceKm   convergence threshold in kilometres
     * @return LogisticsCenter entity with status CANDIDATE
     */
    public LogisticsCenter calculateWeiszfeld(String companyId,
                                              List<ConsumptionSite> sites,
                                              int maxIterations,
                                              double toleranceKm) {
        validateSites(sites);
        double[] latitudes  = extractLatitudes(sites);
        double[] longitudes = extractLongitudes(sites);
        double[] weights    = extractWeights(sites);
        List<String> siteIds = extractIds(sites);

        WeiszfeldResult result = weiszfeldIterate(latitudes, longitudes, weights,
                maxIterations, toleranceKm);
        double totalTons = sumWeights(weights);

        return buildCenter(companyId, siteIds, result.position(),
                totalTons, "weiszfeld-iterative", result.iterations(),
                result.convergenceErrorKm());
    }

    // -------------------------------------------------------------------------
    // Public API: from raw arrays (used by CalculateBarycentreUseCase.SimpleCalculateCommand)
    // -------------------------------------------------------------------------

    /**
     * Performs a single-step weighted barycenter calculation from raw arrays.
     * Useful for the "Quick Calculate" dialog that operates before sites are persisted.
     *
     * @param companyId   owning company (may be null for preview)
     * @param siteIds     parallel list of site identifiers
     * @param latitudes   decimal-degree latitudes
     * @param longitudes  decimal-degree longitudes
     * @param weightsTons traffic volumes in tons
     * @return LogisticsCenter entity with status CANDIDATE
     */
    public LogisticsCenter calculateSimpleFromArrays(String companyId,
                                                     List<String> siteIds,
                                                     List<Double> latitudes,
                                                     List<Double> longitudes,
                                                     List<Double> weightsTons) {
        validateArrays(latitudes, longitudes, weightsTons);
        double[] lats    = toPrimitive(latitudes);
        double[] lons    = toPrimitive(longitudes);
        double[] weights = toPrimitive(weightsTons);

        GeoCoordinate result = weightedBarycentre(lats, lons, weights);
        double totalTons     = sumWeights(weights);

        return buildCenter(companyId, siteIds, result, totalTons,
                "weighted-barycenter", 1, 0.0);
    }

    /**
     * Weiszfeld calculation from raw arrays.
     */
    public LogisticsCenter calculateWeiszfeldFromArrays(String companyId,
                                                        List<String> siteIds,
                                                        List<Double> latitudes,
                                                        List<Double> longitudes,
                                                        List<Double> weightsTons,
                                                        int maxIterations,
                                                        double toleranceKm) {
        validateArrays(latitudes, longitudes, weightsTons);
        double[] lats    = toPrimitive(latitudes);
        double[] lons    = toPrimitive(longitudes);
        double[] weights = toPrimitive(weightsTons);

        WeiszfeldResult result = weiszfeldIterate(lats, lons, weights, maxIterations, toleranceKm);
        double totalTons = sumWeights(weights);

        return buildCenter(companyId, siteIds, result.position(),
                totalTons, "weiszfeld-iterative", result.iterations(),
                result.convergenceErrorKm());
    }

    // -------------------------------------------------------------------------
    // Algorithm implementations
    // -------------------------------------------------------------------------

    /**
     * Single-step weighted centroid (arithmetic mean weighted by tonnage).
     *
     *   lat* = sum(w_i * lat_i) / sum(w_i)
     *   lon* = sum(w_i * lon_i) / sum(w_i)
     */
    private GeoCoordinate weightedBarycentre(double[] lats, double[] lons, double[] weights) {
        double sumW   = 0.0;
        double sumLat = 0.0;
        double sumLon = 0.0;

        for (int i = 0; i < lats.length; i++) {
            sumW   += weights[i];
            sumLat += weights[i] * lats[i];
            sumLon += weights[i] * lons[i];
        }

        if (sumW <= 0.0) {
            // All weights are zero — fall back to unweighted centroid
            double[] ones = new double[lats.length];
            java.util.Arrays.fill(ones, 1.0);
            return weightedBarycentre(lats, lons, ones);
        }

        return new GeoCoordinate(sumLat / sumW, sumLon / sumW);
    }

    /**
     * Weiszfeld iterative algorithm minimising sum of weighted geodesic distances.
     *
     * Each iteration:
     *   d_i   = haversine(current, site_i)   (clamped to EPSILON_KM)
     *   num_lat = sum( w_i * lat_i / d_i )
     *   num_lon = sum( w_i * lon_i / d_i )
     *   den     = sum( w_i         / d_i )
     *
     *   next_lat = num_lat / den
     *   next_lon = num_lon / den
     *
     * Starts from the weighted centroid as the initial estimate.
     */
    private WeiszfeldResult weiszfeldIterate(double[] lats,
                                             double[] lons,
                                             double[] weights,
                                             int maxIterations,
                                             double toleranceKm) {
        // Warm-start: use the simple barycenter as the initial estimate
        GeoCoordinate current = weightedBarycentre(lats, lons, weights);
        int iteration = 0;
        double error = Double.MAX_VALUE;

        while (iteration < maxIterations && error > toleranceKm) {
            double numLat = 0.0;
            double numLon = 0.0;
            double den    = 0.0;

            for (int i = 0; i < lats.length; i++) {
                GeoCoordinate site = new GeoCoordinate(lats[i], lons[i]);
                double d = Math.max(current.distanceKmTo(site), EPSILON_KM);
                double wd = weights[i] / d;
                numLat += wd * lats[i];
                numLon += wd * lons[i];
                den    += wd;
            }

            if (den <= 0.0) break;

            GeoCoordinate next = new GeoCoordinate(numLat / den, numLon / den);
            error   = current.distanceKmTo(next);
            current = next;
            iteration++;
        }

        return new WeiszfeldResult(current, iteration, error);
    }

    // -------------------------------------------------------------------------
    // Builder helper
    // -------------------------------------------------------------------------

    private LogisticsCenter buildCenter(String companyId,
                                        List<String> siteIds,
                                        GeoCoordinate position,
                                        double totalTons,
                                        String algorithmDesc,
                                        int iterations,
                                        double convergenceErrorKm) {
        return new LogisticsCenter(
                null,
                companyId != null ? companyId : "PREVIEW",
                null,
                position,
                TrafficVolume.ofTons(totalTons),
                siteIds,
                algorithmDesc,
                iterations,
                convergenceErrorKm
        );
    }

    // -------------------------------------------------------------------------
    // Validation
    // -------------------------------------------------------------------------

    private void validateSites(List<ConsumptionSite> sites) {
        Objects.requireNonNull(sites, "sites must not be null");
        if (sites.size() < 2) {
            throw new IllegalArgumentException(
                    "At least 2 sites are required for barycenter calculation, got: " + sites.size());
        }
    }

    private void validateArrays(List<Double> latitudes, List<Double> longitudes, List<Double> weights) {
        Objects.requireNonNull(latitudes,  "latitudes must not be null");
        Objects.requireNonNull(longitudes, "longitudes must not be null");
        Objects.requireNonNull(weights,    "weights must not be null");
        if (latitudes.size() < 2) {
            throw new IllegalArgumentException("At least 2 sites required.");
        }
        if (latitudes.size() != longitudes.size() || latitudes.size() != weights.size()) {
            throw new IllegalArgumentException("All lists must be the same length.");
        }
    }

    // -------------------------------------------------------------------------
    // Extraction helpers
    // -------------------------------------------------------------------------

    private double[] extractLatitudes(List<ConsumptionSite> sites) {
        return sites.stream().mapToDouble(s -> s.getCoordinate().latitude()).toArray();
    }

    private double[] extractLongitudes(List<ConsumptionSite> sites) {
        return sites.stream().mapToDouble(s -> s.getCoordinate().longitude()).toArray();
    }

    private double[] extractWeights(List<ConsumptionSite> sites) {
        return sites.stream().mapToDouble(s -> s.getTrafficVolume().tons()).toArray();
    }

    private List<String> extractIds(List<ConsumptionSite> sites) {
        return sites.stream().map(ConsumptionSite::getId).toList();
    }

    private double[] toPrimitive(List<Double> list) {
        return list.stream().mapToDouble(Double::doubleValue).toArray();
    }

    private double sumWeights(double[] weights) {
        double sum = 0.0;
        for (double w : weights) sum += w;
        return sum;
    }

    // -------------------------------------------------------------------------
    // Internal result carrier
    // -------------------------------------------------------------------------

    private record WeiszfeldResult(
            GeoCoordinate position,
            int           iterations,
            double        convergenceErrorKm) {}
}
