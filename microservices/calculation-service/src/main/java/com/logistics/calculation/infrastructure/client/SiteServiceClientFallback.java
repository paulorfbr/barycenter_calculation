package com.logistics.calculation.infrastructure.client;

import com.logistics.calculation.web.dto.SiteInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Resilience4j fallback for {@link SiteServiceClient}.
 *
 * Returns an empty list when the Site Service is unavailable.  The
 * {@link com.logistics.calculation.application.service.CalculationApplicationService}
 * validates that at least 2 sites are present before computing, so this
 * fallback results in a clear {@code 422 Insufficient Sites} error rather
 * than a confusing network exception bubbling up to the caller.
 */
@Component
public class SiteServiceClientFallback implements SiteServiceClient {

    private static final Logger log = LoggerFactory.getLogger(SiteServiceClientFallback.class);

    @Override
    public List<SiteInput> getActiveSitesByCompany(String companyId) {
        log.warn("Site Service unavailable — returning empty site list for companyId={}", companyId);
        return Collections.emptyList();
    }
}
