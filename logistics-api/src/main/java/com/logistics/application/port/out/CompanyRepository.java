package com.logistics.application.port.out;

import com.logistics.domain.model.Company;

import java.util.List;
import java.util.Optional;

/**
 * Outbound port (driven port): persistence contract for Company aggregates.
 *
 * The application layer depends on this interface. The concrete implementation
 * lives in the infrastructure layer (e.g. InMemoryCompanyRepository for the
 * JavaFX desktop runtime, JpaCompanyRepository for a future Spring Boot service).
 *
 * This separation ensures that:
 *   - The domain and application layers have zero framework dependencies.
 *   - Switching persistence backends requires only a new adapter — no business logic changes.
 *   - Unit tests can use a fast, in-memory fake without a database.
 */
public interface CompanyRepository {

    /**
     * Persists a new company or updates an existing one (upsert semantics).
     *
     * @param company the aggregate to save
     * @return the saved aggregate (may differ if the implementation enriches it)
     */
    Company save(Company company);

    /**
     * Retrieves a company by its unique identifier.
     *
     * @param id the company ID
     * @return an Optional containing the company, or empty if not found
     */
    Optional<Company> findById(String id);

    /**
     * Returns all companies, ordered by name ascending.
     *
     * @return list of all companies; never null, may be empty
     */
    List<Company> findAll();

    /**
     * Returns companies filtered by lifecycle status.
     *
     * @param status the target status
     * @return matching companies; never null, may be empty
     */
    List<Company> findByStatus(Company.Status status);

    /**
     * Permanently removes a company by id.
     * Implementations should cascade-delete owned consumption sites and
     * logistics centers.
     *
     * @param id the company id to delete
     */
    void deleteById(String id);

    /**
     * Returns the number of companies with the given status.
     *
     * @param status the filter status
     * @return count
     */
    long countByStatus(Company.Status status);
}
