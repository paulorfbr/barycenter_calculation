package com.logistics.application.port.in;

import com.logistics.domain.model.ConsumptionSite;

import java.util.List;
import java.util.Optional;

/**
 * Inbound port for managing consumption sites within a company's logistics network.
 *
 * Consumption sites are the primary input to the barycenter engine. Adding,
 * moving or updating a site's traffic volume should trigger (or offer to trigger)
 * a recalculation via {@link CalculateBarycentreUseCase}.
 */
public interface ManageConsumptionSiteUseCase {

    ConsumptionSite addSite(AddSiteCommand command);

    ConsumptionSite updateSite(UpdateSiteCommand command);

    void removeSite(String companyId, String siteId);

    Optional<ConsumptionSite> findById(String siteId);

    List<ConsumptionSite> findByCompany(String companyId);

    List<ConsumptionSite> findActiveByCompany(String companyId);

    // -------------------------------------------------------------------------
    // Commands
    // -------------------------------------------------------------------------

    record AddSiteCommand(
            String companyId,
            String name,
            String description,
            double latitude,
            double longitude,
            double weightTons,
            String address,
            String city,
            String country) {}

    record UpdateSiteCommand(
            String siteId,
            String name,
            String description,
            double latitude,
            double longitude,
            double weightTons,
            String address,
            String city,
            String country) {}

    // -------------------------------------------------------------------------
    // Domain exceptions
    // -------------------------------------------------------------------------

    class ConsumptionSiteNotFoundException extends RuntimeException {
        public ConsumptionSiteNotFoundException(String siteId) {
            super("ConsumptionSite not found: " + siteId);
        }
    }
}
