package com.logistics.application.service;

import com.logistics.application.port.in.ManageCompanyUseCase;
import com.logistics.application.port.out.CompanyRepository;
import com.logistics.domain.model.Company;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Application service implementing {@link ManageCompanyUseCase}.
 *
 * Thin orchestration layer: validates inputs, delegates to the repository,
 * and enforces business rules that span multiple aggregates (e.g. preventing
 * deletion of a company that has active shipments — placeholder guards are
 * included and noted for future implementation).
 */
public class CompanyService implements ManageCompanyUseCase {

    private final CompanyRepository repository;

    public CompanyService(CompanyRepository repository) {
        this.repository = Objects.requireNonNull(repository, "CompanyRepository must not be null");
    }

    @Override
    public Company createCompany(CreateCompanyCommand cmd) {
        Company company = new Company(null, cmd.name(), cmd.type(), Company.Status.ACTIVE);
        company.updateTaxId(cmd.taxId());
        company.updateContactInfo(cmd.contactName(), cmd.contactEmail(), cmd.contactPhone());
        company.updateNotes(cmd.notes());
        return repository.save(company);
    }

    @Override
    public Company updateCompany(UpdateCompanyCommand cmd) {
        Company company = repository.findById(cmd.companyId())
                .orElseThrow(() -> new CompanyNotFoundException(cmd.companyId()));

        company.updateName(cmd.name());
        company.updateType(cmd.type());
        company.updateTaxId(cmd.taxId());
        company.updateContactInfo(cmd.contactName(), cmd.contactEmail(), cmd.contactPhone());
        company.updateNotes(cmd.notes());

        return repository.save(company);
    }

    @Override
    public void activateCompany(String companyId) {
        Company company = repository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));
        company.activate();
        repository.save(company);
    }

    @Override
    public void deactivateCompany(String companyId) {
        Company company = repository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));
        company.deactivate();
        repository.save(company);
    }

    @Override
    public void deleteCompany(String companyId) {
        repository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));
        // TODO: guard against deletion if company has in-transit shipments
        repository.deleteById(companyId);
    }

    @Override
    public Optional<Company> findById(String companyId) {
        return repository.findById(companyId);
    }

    @Override
    public List<Company> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Company> findByStatus(Company.Status status) {
        return repository.findByStatus(status);
    }
}
