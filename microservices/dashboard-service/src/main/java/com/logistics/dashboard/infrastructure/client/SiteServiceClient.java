package com.logistics.dashboard.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

/**
 * Feign client for Site Service — used for cold-start refresh of the
 * dashboard read model.
 */
@FeignClient(name = "site-service", path = "/api/v1")
public interface SiteServiceClient {

    @GetMapping("/sites")
    List<Map<String, Object>> getAllSites();

    default int countAllSites() {
        try { return getAllSites().size(); } catch (Exception e) { return 0; }
    }

    default double sumAllWeightTons() {
        try {
            return getAllSites().stream()
                    .mapToDouble(s -> {
                        Object w = s.get("weightTons");
                        return w instanceof Number n ? n.doubleValue() : 0.0;
                    }).sum();
        } catch (Exception e) { return 0.0; }
    }
}
