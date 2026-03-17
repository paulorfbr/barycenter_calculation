package com.logistics.company.infrastructure.persistence.repository;

import com.logistics.company.application.port.out.CompanyRepository;
import com.logistics.company.domain.model.Company;
import com.logistics.company.infrastructure.persistence.entity.CompanyEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * JPA adapter implementing the outbound {@link CompanyRepository} port.
 *
 * Handles the bidirectional mapping between the domain {@link Company}
 * aggregate and the persistence {@link CompanyEntity}.  This explicit
 * mapping (rather than using JPA directly on the domain class) maintains
 * the hexagonal architecture boundary.
 */
@Component
public class CompanyRepositoryAdapter implements CompanyRepository {

    private final CompanyJpaRepository jpa;

    public CompanyRepositoryAdapter(CompanyJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Company save(Company company) {
        CompanyEntity entity = toEntity(company);
        CompanyEntity saved  = jpa.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Company> findById(String companyId) {
        return jpa.findById(companyId).map(this::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return jpa.existsByName(name);
    }

    @Override
    public List<Company> findAll() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Company> findByStatus(Company.Status status) {
        return jpa.findByStatus(CompanyEntity.CompanyStatusEnum.valueOf(status.name()))
                  .stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(String companyId) {
        jpa.deleteById(companyId);
    }

    // -----------------------------------------------------------------------
    // Private mapping helpers
    // -----------------------------------------------------------------------

    private CompanyEntity toEntity(Company d) {
        CompanyEntity e = new CompanyEntity();
        e.setId(d.getId());
        e.setName(d.getName());
        e.setType(CompanyEntity.CompanyTypeEnum.valueOf(d.getType().name()));
        e.setStatus(CompanyEntity.CompanyStatusEnum.valueOf(d.getStatus().name()));
        e.setTaxId(d.getTaxId());
        e.setContactName(d.getContactName());
        e.setContactEmail(d.getContactEmail());
        e.setContactPhone(d.getContactPhone());
        e.setNotes(d.getNotes());
        return e;
    }

    private Company toDomain(CompanyEntity e) {
        Company d = new Company(
                e.getId(),
                e.getName(),
                Company.Type.valueOf(e.getType().name()),
                Company.Status.valueOf(e.getStatus().name()));
        d.updateTaxId(e.getTaxId());
        d.updateContactInfo(e.getContactName(), e.getContactEmail(), e.getContactPhone());
        d.updateNotes(e.getNotes());
        return d;
    }
}
