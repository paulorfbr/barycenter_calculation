package com.logistics.application.port.out;

import com.logistics.domain.model.ConsumptionSite;

import java.util.List;
import java.util.Optional;

/**
 * Outbound port: persistence contract for ConsumptionSite entities.
 *
 * ConsumptionSites are accessed both as owned children of a Company aggregate
 * (when the company is loaded) and independently (when the barycenter engine
 * needs only the sites for a specific company).
 */
public interface ConsumptionSiteRepository {

    ConsumptionSite save(ConsumptionSite site);

    Optional<ConsumptionSite> findById(String id);

    List<ConsumptionSite> findByCompanyId(String companyId);

    List<ConsumptionSite> findActiveByCompanyId(String companyId);

    void deleteById(String id);

    long countByCompanyId(String companyId);
}
