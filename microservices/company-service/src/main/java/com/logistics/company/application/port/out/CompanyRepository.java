package com.logistics.company.application.port.out;

import com.logistics.company.domain.model.Company;

import java.util.List;
import java.util.Optional;

/**
 * Outbound driven port for Company persistence.
 *
 * The application service depends on this interface; the JPA adapter in the
 * infrastructure layer provides the implementation.  This inversion allows
 * the application layer to be tested without a database.
 */
public interface CompanyRepository {

    Company save(Company company);

    Optional<Company> findById(String companyId);

    boolean existsByName(String name);

    List<Company> findAll();

    List<Company> findByStatus(Company.Status status);

    void deleteById(String companyId);
}
