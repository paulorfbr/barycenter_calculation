package com.logistics.company.application.service;

import com.logistics.company.application.port.in.ManageCompanyUseCase;
import com.logistics.company.application.port.out.CompanyEventPublisher;
import com.logistics.company.application.port.out.CompanyRepository;
import com.logistics.company.domain.model.Company;
import com.logistics.shared.domain.event.CompanyCreatedEvent;
import com.logistics.shared.domain.event.CompanyStatusChangedEvent;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Application service implementing {@link ManageCompanyUseCase}.
 *
 * Responsibilities:
 *   1. Enforce cross-aggregate business rules (e.g. name uniqueness).
 *   2. Delegate persistence to {@link CompanyRepository}.
 *   3. Publish domain events via {@link CompanyEventPublisher} after
 *      each successful state transition.
 *
 * All public methods are transactional.  Read-only methods use
 * {@code readOnly = true} to enable JPA optimisations and route to
 * read replicas when a routing DataSource is configured.
 *
 * Cache keys:
 *   - "companies"       : list cache — evicted on any mutation
 *   - "company::{id}"   : per-entity cache — populated on find, evicted on update/delete
 */
@Service
@Observed(name = "company.service")
public class CompanyApplicationService implements ManageCompanyUseCase {

    private static final Logger log = LoggerFactory.getLogger(CompanyApplicationService.class);

    private final CompanyRepository     repository;
    private final CompanyEventPublisher eventPublisher;

    public CompanyApplicationService(CompanyRepository repository,
                                     CompanyEventPublisher eventPublisher) {
        this.repository     = Objects.requireNonNull(repository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    // -----------------------------------------------------------------------
    // Writes
    // -----------------------------------------------------------------------

    @Override
    @Transactional
    @CacheEvict(value = "companies", allEntries = true)
    public Company createCompany(CreateCompanyCommand cmd) {
        if (repository.existsByName(cmd.name())) {
            throw new DuplicateCompanyNameException(cmd.name());
        }

        Company company = new Company(null, cmd.name(), cmd.type(), Company.Status.ACTIVE);
        company.updateTaxId(cmd.taxId());
        company.updateContactInfo(cmd.contactName(), cmd.contactEmail(), cmd.contactPhone());
        company.updateNotes(cmd.notes());

        Company saved = repository.save(company);
        log.info("Company created: id={} name={}", saved.getId(), saved.getName());

        eventPublisher.publish(CompanyCreatedEvent.of(
                saved.getId(), saved.getName(),
                saved.getType().name(), saved.getStatus().name()));

        return saved;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"companies", "company"}, allEntries = true)
    public Company updateCompany(UpdateCompanyCommand cmd) {
        Company company = loadOrThrow(cmd.companyId());

        company.updateName(cmd.name());
        company.updateType(cmd.type());
        company.updateTaxId(cmd.taxId());
        company.updateContactInfo(cmd.contactName(), cmd.contactEmail(), cmd.contactPhone());
        company.updateNotes(cmd.notes());

        Company saved = repository.save(company);
        log.info("Company updated: id={}", saved.getId());
        return saved;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"companies", "company"}, allEntries = true)
    public void activateCompany(String companyId) {
        Company company = loadOrThrow(companyId);
        String previous = company.getStatus().name();
        company.activate();
        repository.save(company);
        log.info("Company activated: id={}", companyId);
        eventPublisher.publish(CompanyStatusChangedEvent.of(companyId, previous, Company.Status.ACTIVE.name()));
    }

    @Override
    @Transactional
    @CacheEvict(value = {"companies", "company"}, allEntries = true)
    public void deactivateCompany(String companyId) {
        Company company = loadOrThrow(companyId);
        String previous = company.getStatus().name();
        company.deactivate();
        repository.save(company);
        log.info("Company deactivated: id={}", companyId);
        eventPublisher.publish(CompanyStatusChangedEvent.of(companyId, previous, Company.Status.INACTIVE.name()));
    }

    @Override
    @Transactional
    @CacheEvict(value = {"companies", "company"}, allEntries = true)
    public void deleteCompany(String companyId) {
        loadOrThrow(companyId); // ensure it exists before deletion
        repository.deleteById(companyId);
        log.info("Company deleted: id={}", companyId);
    }

    // -----------------------------------------------------------------------
    // Reads
    // -----------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "company", key = "#companyId")
    public Optional<Company> findById(String companyId) {
        return repository.findById(companyId);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "companies")
    public List<Company> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Company> findByStatus(Company.Status status) {
        return repository.findByStatus(status);
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private Company loadOrThrow(String companyId) {
        return repository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));
    }
}
