package com.logistics.dashboard.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Feign client for Company Service — used for cold-start refresh of the
 * dashboard read model.
 */
@FeignClient(name = "company-service", path = "/api/v1")
public interface CompanyServiceClient {

    @GetMapping("/companies")
    List<Map<String, Object>> getAllCompanies();

    default int countCompanies() {
        try { return getAllCompanies().size(); } catch (Exception e) { return 0; }
    }

    default int countActiveCompanies() {
        try {
            return (int) getAllCompanies().stream()
                    .filter(c -> "ACTIVE".equals(c.get("status")))
                    .count();
        } catch (Exception e) { return 0; }
    }
}
