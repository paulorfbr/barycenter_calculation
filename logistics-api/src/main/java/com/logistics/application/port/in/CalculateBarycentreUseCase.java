package com.logistics.application.port.in;

import com.logistics.domain.model.LogisticsCenter;

import java.util.List;

/**
 * Inbound port: calculates the optimal barycenter position for a company's
 * logistics network given a set of weighted consumption sites.
 *
 * This is a driving port (called from the UI or REST layer). The hexagonal
 * architecture rule is: the UI depends on this interface, not on the
 * implementation in the infrastructure layer.
 *
 * Two command variants are provided:
 *
 *   SimpleCalculateCommand  — supply data inline (used by the barycenter
 *                             calculation form dialog before sites are persisted).
 *
 *   StoredSitesCommand      — calculate using the persisted ConsumptionSite
 *                             entities for a given company (used by the
 *                             Dashboard "Recalculate" button).
 */
public interface CalculateBarycentreUseCase {

    /**
     * Performs a weighted barycenter calculation from explicitly supplied data.
     *
     * @param command the calculation inputs
     * @return the resulting logistics center entity (status = CANDIDATE)
     */
    LogisticsCenter calculate(SimpleCalculateCommand command);

    /**
     * Performs a weighted barycenter calculation using the stored consumption
     * sites for the given company.
     *
     * @param command identifies which company's sites to use
     * @return the resulting logistics center entity (status = CANDIDATE)
     * @throws com.logistics.application.port.in.CalculateBarycentreUseCase.InsufficientSitesException
     *         if the company has fewer than two active sites
     */
    LogisticsCenter calculateForCompany(StoredSitesCommand command);

    // -------------------------------------------------------------------------
    // Commands
    // -------------------------------------------------------------------------

    /**
     * Lightweight command for calculating from ad-hoc site data (e.g. the
     * "Quick Calculate" panel on the Dashboard or the barycenter form dialog).
     *
     * @param companyId       the owning company (may be null for a stateless preview)
     * @param siteName        names parallel to coordinates / weights lists
     * @param latitudes       decimal-degree latitude for each site
     * @param longitudes      decimal-degree longitude for each site
     * @param weightsTons     traffic volume in tons for each site
     * @param useIterative    true = Weiszfeld iterative refinement; false = single-step
     */
    record SimpleCalculateCommand(
            String       companyId,
            List<String> siteName,
            List<Double> latitudes,
            List<Double> longitudes,
            List<Double> weightsTons,
            boolean      useIterative) {

        public SimpleCalculateCommand {
            if (latitudes == null || longitudes == null || weightsTons == null) {
                throw new IllegalArgumentException("Coordinate and weight lists must not be null.");
            }
            if (latitudes.size() != longitudes.size() || latitudes.size() != weightsTons.size()) {
                throw new IllegalArgumentException(
                        "Latitudes, longitudes and weights lists must be the same size.");
            }
            if (latitudes.size() < 2) {
                throw new IllegalArgumentException(
                        "At least 2 sites are required for a barycenter calculation.");
            }
        }
    }

    /**
     * Command to re-calculate from persisted data.
     *
     * @param companyId     the company whose active consumption sites are used
     * @param useIterative  true = Weiszfeld iterative refinement
     * @param maxIterations maximum refinement iterations (ignored when useIterative=false)
     * @param toleranceKm   convergence threshold in km (ignored when useIterative=false)
     */
    record StoredSitesCommand(
            String  companyId,
            boolean useIterative,
            int     maxIterations,
            double  toleranceKm) {

        public StoredSitesCommand {
            if (companyId == null || companyId.isBlank()) {
                throw new IllegalArgumentException("companyId must not be blank.");
            }
            if (maxIterations < 1) {
                throw new IllegalArgumentException("maxIterations must be at least 1.");
            }
            if (toleranceKm <= 0) {
                throw new IllegalArgumentException("toleranceKm must be positive.");
            }
        }

        /** Convenience factory with sensible defaults. */
        public static StoredSitesCommand defaultFor(String companyId) {
            return new StoredSitesCommand(companyId, true, 1_000, 0.01);
        }
    }

    // -------------------------------------------------------------------------
    // Domain exception
    // -------------------------------------------------------------------------

    /** Thrown when there are not enough active sites to perform a meaningful calculation. */
    class InsufficientSitesException extends RuntimeException {
        public InsufficientSitesException(String companyId, int activeSiteCount) {
            super("Company '" + companyId + "' has only " + activeSiteCount
                    + " active consumption site(s). At least 2 are required.");
        }
    }
}
