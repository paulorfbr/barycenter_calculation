package com.logistics.infrastructure.persistence.repository;

import com.logistics.application.port.out.LogisticsCenterRepository;
import com.logistics.domain.model.LogisticsCenter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of {@link LogisticsCenterRepository}.
 */
public class InMemoryLogisticsCenterRepository implements LogisticsCenterRepository {

    private final Map<String, LogisticsCenter> store = new ConcurrentHashMap<>();

    @Override
    public LogisticsCenter save(LogisticsCenter center) {
        Objects.requireNonNull(center);
        store.put(center.getId(), center);
        return center;
    }

    @Override
    public Optional<LogisticsCenter> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<LogisticsCenter> findByCompanyId(String companyId) {
        return store.values().stream()
                .filter(c -> companyId.equals(c.getCompanyId()))
                .sorted(Comparator.comparing(LogisticsCenter::getCalculatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<LogisticsCenter> findApprovedByCompanyId(String companyId) {
        return store.values().stream()
                .filter(c -> companyId.equals(c.getCompanyId())
                          && (c.getStatus() == LogisticsCenter.Status.APPROVED
                           || c.getStatus() == LogisticsCenter.Status.CONFIRMED))
                .sorted(Comparator.comparing(LogisticsCenter::getCalculatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        store.remove(id);
    }
}
