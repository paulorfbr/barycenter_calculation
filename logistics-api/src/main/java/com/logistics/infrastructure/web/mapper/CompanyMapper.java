package com.logistics.infrastructure.web.mapper;

import com.logistics.application.dto.CompanyDto;
import com.logistics.application.port.out.ConsumptionSiteRepository;
import com.logistics.domain.model.Company;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Maps between the {@link Company} domain aggregate and the REST-layer
 * {@link CompanyDto} record.
 *
 * Located in the infrastructure/web layer so that mapping concerns (formatting,
 * count derivation) do not bleed into the domain or application layers.
 */
@Component
public class CompanyMapper {

    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneOffset.UTC);

    private final ConsumptionSiteRepository siteRepository;

    public CompanyMapper(ConsumptionSiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    /**
     * Converts a domain Company to a CompanyDto suitable for JSON serialisation.
     * The locationCount is derived by querying the site repository so that it
     * reflects the current state rather than a stale cached value.
     *
     * @param company the domain aggregate
     * @return populated DTO
     */
    public CompanyDto toDto(Company company) {
        long siteCount = siteRepository.countByCompanyId(company.getId());
        return new CompanyDto(
                company.getId(),
                company.getName(),
                company.getType().name(),
                company.getStatus().name(),
                company.getTaxId(),
                company.getContactName(),
                company.getContactEmail(),
                company.getContactPhone(),
                company.getNotes(),
                (int) siteCount,
                company.totalTrafficTons(),
                ISO_FORMATTER.format(company.getCreatedAt()),
                ISO_FORMATTER.format(company.getUpdatedAt())
        );
    }
}
