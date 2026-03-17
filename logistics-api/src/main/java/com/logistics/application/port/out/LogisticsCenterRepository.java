package com.logistics.application.port.out;

import com.logistics.domain.model.LogisticsCenter;

import java.util.List;
import java.util.Optional;

/**
 * Outbound port: persistence contract for LogisticsCenter calculation results.
 */
public interface LogisticsCenterRepository {

    LogisticsCenter save(LogisticsCenter center);

    Optional<LogisticsCenter> findById(String id);

    List<LogisticsCenter> findByCompanyId(String companyId);

    /** Returns only APPROVED and CONFIRMED centers for a company. */
    List<LogisticsCenter> findApprovedByCompanyId(String companyId);

    void deleteById(String id);
}
