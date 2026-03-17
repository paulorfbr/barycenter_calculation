package com.logistics.infrastructure.web.mapper;

import com.logistics.application.dto.ConsumptionSiteDto;
import com.logistics.domain.model.ConsumptionSite;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Maps between the {@link ConsumptionSite} domain entity and
 * {@link ConsumptionSiteDto} for REST serialisation.
 */
@Component
public class ConsumptionSiteMapper {

    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneOffset.UTC);

    public ConsumptionSiteDto toDto(ConsumptionSite site) {
        double lat = site.getCoordinate().latitude();
        double lon = site.getCoordinate().longitude();
        double tons = site.getTrafficVolume().tons();

        return new ConsumptionSiteDto(
                site.getId(),
                site.getCompanyId(),
                site.getName(),
                site.getDescription(),
                lat,
                lon,
                formatCoordinate(lat, lon),
                tons,
                site.getTrafficVolume().toDisplayString(),
                site.getAddress(),
                site.getCity(),
                site.getCountry(),
                site.getStatus().name(),
                ISO_FORMATTER.format(site.getCreatedAt())
        );
    }

    private String formatCoordinate(double lat, double lon) {
        return String.format("%.4f, %.4f", lat, lon);
    }
}
