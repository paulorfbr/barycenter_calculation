package com.logistics.infrastructure.persistence.repository;

import com.logistics.application.port.out.CompanyRepository;
import com.logistics.domain.model.Company;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of {@link CompanyRepository}.
 *
 * Used by the JavaFX desktop runtime where no external database is present.
 * Thread-safety is provided by {@link ConcurrentHashMap} so that background
 * service threads (e.g. calculation jobs) can safely read while the FX thread
 * updates the UI.
 *
 * When a Spring Boot + JPA backend is added, this class is replaced by a
 * JpaCompanyRepository adapter — the application and domain layers are
 * unchanged because they depend only on the {@link CompanyRepository} interface.
 */
public class InMemoryCompanyRepository implements CompanyRepository {

    private final Map<String, Company> store = new ConcurrentHashMap<>();

    @Override
    public Company save(Company company) {
        Objects.requireNonNull(company, "company must not be null");
        store.put(company.getId(), company);
        return company;
    }

    @Override
    public Optional<Company> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Company> findAll() {
        return store.values().stream()
                .sorted(Comparator.comparing(Company::getName))
                .collect(Collectors.toList());
    }

    @Override
    public List<Company> findByStatus(Company.Status status) {
        return store.values().stream()
                .filter(c -> c.getStatus() == status)
                .sorted(Comparator.comparing(Company::getName))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        store.remove(id);
    }

    @Override
    public long countByStatus(Company.Status status) {
        return store.values().stream().filter(c -> c.getStatus() == status).count();
    }
}
