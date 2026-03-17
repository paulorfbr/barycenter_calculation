package com.logistics.company.application.port.in;

import com.logistics.company.domain.model.Company;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Inbound driving port for all company management use cases.
 *
 * The REST controller, event listeners, and any future CLI adapter depend
 * on this interface rather than the concrete service class.
 */
public interface ManageCompanyUseCase {

    Company createCompany(CreateCompanyCommand command);

    Company updateCompany(UpdateCompanyCommand command);

    void activateCompany(@NotBlank String companyId);

    void deactivateCompany(@NotBlank String companyId);

    void deleteCompany(@NotBlank String companyId);

    Optional<Company> findById(@NotBlank String companyId);

    List<Company> findAll();

    List<Company> findByStatus(@NotNull Company.Status status);

    // -----------------------------------------------------------------------
    // Commands
    // -----------------------------------------------------------------------

    record CreateCompanyCommand(
            @NotBlank String       name,
            @NotNull  Company.Type type,
                      String       taxId,
                      String       contactName,
                      String       contactEmail,
                      String       contactPhone,
                      String       notes) {}

    record UpdateCompanyCommand(
            @NotBlank String       companyId,
            @NotBlank String       name,
            @NotNull  Company.Type type,
                      String       taxId,
                      String       contactName,
                      String       contactEmail,
                      String       contactPhone,
                      String       notes) {}

    // -----------------------------------------------------------------------
    // Domain exceptions
    // -----------------------------------------------------------------------

    class CompanyNotFoundException extends RuntimeException {
        public CompanyNotFoundException(String companyId) {
            super("Company not found: " + companyId);
        }
    }

    class DuplicateCompanyNameException extends RuntimeException {
        public DuplicateCompanyNameException(String name) {
            super("A company with name '" + name + "' already exists.");
        }
    }
}
