package com.logistics.calculation.infrastructure.client;

import com.logistics.calculation.web.dto.SiteInput;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Feign declarative HTTP client for the Site Service.
 *
 * Uses Spring Cloud LoadBalancer for client-side load balancing across
 * Site Service instances registered in the service registry.
 *
 * The circuit breaker is configured in application.yml under
 * resilience4j.circuitbreaker.instances.site-service-client.
 *
 * Falls back to an empty list when the Site Service is unavailable,
 * which causes the calculation service to return an appropriate error
 * rather than failing with a connection exception.
 */
@FeignClient(
    name     = "site-service",
    path     = "/api/v1",
    fallback = SiteServiceClientFallback.class
)
public interface SiteServiceClient {

    /**
     * Fetches all active consumption sites for the given company.
     *
     * @param companyId the company whose sites to retrieve
     * @return list of active sites with coordinates and weights
     */
    @GetMapping("/sites/company/{companyId}/active")
    List<SiteInput> getActiveSitesByCompany(@PathVariable("companyId") String companyId);
}
