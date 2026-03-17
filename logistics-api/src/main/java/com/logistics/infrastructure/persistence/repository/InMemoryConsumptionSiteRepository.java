package com.logistics.infrastructure.persistence.repository;

import com.logistics.application.port.out.ConsumptionSiteRepository;
import com.logistics.domain.model.ConsumptionSite;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of {@link ConsumptionSiteRepository}.
 * Thread-safe for concurrent reads from background calculation threads.
 */
public class InMemoryConsumptionSiteRepository implements ConsumptionSiteRepository {

    private final Map<String, ConsumptionSite> store = new ConcurrentHashMap<>();

    @Override
    public ConsumptionSite save(ConsumptionSite site) {
        Objects.requireNonNull(site);
        store.put(site.getId(), site);
        return site;
    }

    @Override
    public Optional<ConsumptionSite> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<ConsumptionSite> findByCompanyId(String companyId) {
        return store.values().stream()
                .filter(s -> companyId.equals(s.getCompanyId()))
                .sorted(Comparator.comparing(ConsumptionSite::getName))
                .collect(Collectors.toList());
    }

    @Override
    public List<ConsumptionSite> findActiveByCompanyId(String companyId) {
        return store.values().stream()
                .filter(s -> companyId.equals(s.getCompanyId())
                          && s.getStatus() == ConsumptionSite.Status.ACTIVE)
                .sorted(Comparator.comparing(ConsumptionSite::getName))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        store.remove(id);
    }

    @Override
    public long countByCompanyId(String companyId) {
        return store.values().stream()
                .filter(s -> companyId.equals(s.getCompanyId()))
                .count();
    }
}
