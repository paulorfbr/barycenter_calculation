package com.logistics.infrastructure.web.mapper;

import com.logistics.application.dto.BarycentreResultDto;
import com.logistics.domain.model.ConsumptionSite;
import com.logistics.domain.model.LogisticsCenter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Maps a {@link LogisticsCenter} calculation result to a {@link BarycentreResultDto}
 * suitable for JSON serialisation.
 *
 * The input site sub-DTOs require the original ConsumptionSite objects to derive
 * name and distance-to-optimal values, so callers must pass both the center and
 * the sites used as inputs.
 */
@Component
public class BarycentreMapper {

    /**
     * Converts a LogisticsCenter and its input sites to a BarycentreResultDto.
     *
     * @param center     the calculation result
     * @param inputSites the ConsumptionSite objects used as inputs (used for name lookup
     *                   and distance calculation)
     * @return populated DTO
     */
    public BarycentreResultDto toDto(LogisticsCenter center,
                                     List<ConsumptionSite> inputSites) {
        double optLat = center.getOptimalPosition().latitude();
        double optLon = center.getOptimalPosition().longitude();

        List<BarycentreResultDto.InputSiteDto> inputSiteDtos = inputSites.stream()
                .map(site -> {
                    double siteLat = site.getCoordinate().latitude();
                    double siteLon = site.getCoordinate().longitude();
                    double dist = center.getOptimalPosition()
                            .distanceKmTo(site.getCoordinate());
                    return new BarycentreResultDto.InputSiteDto(
                            site.getId(),
                            site.getName(),
                            siteLat,
                            siteLon,
                            site.getTrafficVolume().tons(),
                            Math.round(dist * 100.0) / 100.0
                    );
                })
                .toList();

        return new BarycentreResultDto(
                center.getId(),
                center.getCompanyId(),
                optLat,
                optLon,
                String.format("%.4f, %.4f", optLat, optLon),
                center.getAlgorithmDescription(),
                center.getIterationCount(),
                center.getConvergenceErrorKm(),
                center.getTotalWeightedVolume().tons(),
                inputSites.size(),
                inputSiteDtos,
                center.getStatus().name()
        );
    }
}
