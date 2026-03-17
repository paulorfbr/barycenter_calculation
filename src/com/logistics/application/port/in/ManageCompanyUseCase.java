package com.logistics.application.port.in;

import com.logistics.domain.model.Company;

import java.util.List;
import java.util.Optional;

/**
 * Inbound port for company CRUD operations.
 *
 * Driving adapters (callers):
 *   - CompanyListScreen / dialog via CompanyService
 *   - REST adapter (future React integration)
 *
 * All commands are records so they are trivially serialisable for a future
 * REST layer without any boilerplate.
 */
public interface ManageCompanyUseCase {

    Company createCompany(CreateCompanyCommand command);

    Company updateCompany(UpdateCompanyCommand command);

    void activateCompany(String companyId);

    void deactivateCompany(String companyId);

    void deleteCompany(String companyId);

    Optional<Company> findById(String companyId);

    List<Company> findAll();

    List<Company> findByStatus(Company.Status status);

    // -------------------------------------------------------------------------
    // Commands
    // -------------------------------------------------------------------------

    record CreateCompanyCommand(
            String       name,
            Company.Type type,
            String       taxId,
            String       contactName,
            String       contactEmail,
            String       contactPhone,
            String       notes) {}

    record UpdateCompanyCommand(
            String       companyId,
            String       name,
            Company.Type type,
            String       taxId,
            String       contactName,
            String       contactEmail,
            String       contactPhone,
            String       notes) {}

    // -------------------------------------------------------------------------
    // Domain exception
    // -------------------------------------------------------------------------

    class CompanyNotFoundException extends RuntimeException {
        public CompanyNotFoundException(String companyId) {
            super("Company not found: " + companyId);
        }
    }
}
