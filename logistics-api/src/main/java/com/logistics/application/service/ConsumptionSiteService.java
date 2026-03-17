package com.logistics.application.service;

import com.logistics.application.port.in.ManageConsumptionSiteUseCase;
import com.logistics.application.port.out.CompanyRepository;
import com.logistics.application.port.out.ConsumptionSiteRepository;
import com.logistics.domain.model.Company;
import com.logistics.domain.model.ConsumptionSite;
import com.logistics.domain.vo.GeoCoordinate;
import com.logistics.domain.vo.TrafficVolume;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Application service implementing {@link ManageConsumptionSiteUseCase}.
 *
 * Ensures that every new consumption site is properly attached to its owning
 * Company aggregate via {@link Company#addConsumptionSite(ConsumptionSite)},
 * which enforces the ownership invariant and keeps the company's internal
 * collection in sync.
 */
public class ConsumptionSiteService implements ManageConsumptionSiteUseCase {

    private final ConsumptionSiteRepository siteRepository;
    private final CompanyRepository         companyRepository;

    public ConsumptionSiteService(ConsumptionSiteRepository siteRepository,
                                   CompanyRepository companyRepository) {
        this.siteRepository    = Objects.requireNonNull(siteRepository);
        this.companyRepository = Objects.requireNonNull(companyRepository);
    }

    @Override
    public ConsumptionSite addSite(AddSiteCommand cmd) {
        Company company = companyRepository.findById(cmd.companyId())
                .orElseThrow(() -> new ManageCompanyUseCase.CompanyNotFoundException(cmd.companyId()));

        ConsumptionSite site = new ConsumptionSite(
                null,
                cmd.name(),
                new GeoCoordinate(cmd.latitude(), cmd.longitude()),
                TrafficVolume.ofTons(cmd.weightTons())
        );
        site.updateAddress(cmd.address(), cmd.city(), cmd.country());
        site.updateDescription(cmd.description());

        company.addConsumptionSite(site);
        companyRepository.save(company);
        return siteRepository.save(site);
    }

    @Override
    public ConsumptionSite updateSite(UpdateSiteCommand cmd) {
        ConsumptionSite site = siteRepository.findById(cmd.siteId())
                .orElseThrow(() -> new ConsumptionSiteNotFoundException(cmd.siteId()));

        site.updateName(cmd.name());
        site.updateDescription(cmd.description());
        site.relocate(new GeoCoordinate(cmd.latitude(), cmd.longitude()));
        site.updateTrafficVolume(TrafficVolume.ofTons(cmd.weightTons()));
        site.updateAddress(cmd.address(), cmd.city(), cmd.country());

        return siteRepository.save(site);
    }

    @Override
    public void removeSite(String companyId, String siteId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ManageCompanyUseCase.CompanyNotFoundException(companyId));
        company.removeConsumptionSite(siteId);
        companyRepository.save(company);
        siteRepository.deleteById(siteId);
    }

    @Override
    public Optional<ConsumptionSite> findById(String siteId) {
        return siteRepository.findById(siteId);
    }

    @Override
    public List<ConsumptionSite> findByCompany(String companyId) {
        return siteRepository.findByCompanyId(companyId);
    }

    @Override
    public List<ConsumptionSite> findActiveByCompany(String companyId) {
        return siteRepository.findActiveByCompanyId(companyId);
    }
}
