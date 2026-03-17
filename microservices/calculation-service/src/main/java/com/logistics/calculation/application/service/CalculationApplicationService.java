package com.logistics.calculation.application.service;

import com.logistics.calculation.domain.algorithm.BarycentreAlgorithm;
import com.logistics.calculation.domain.algorithm.WeightedBarycentreAlgorithm;
import com.logistics.calculation.domain.algorithm.WeiszfeldAlgorithm;
import com.logistics.calculation.domain.model.LogisticsCenter;
import com.logistics.calculation.infrastructure.client.SiteServiceClient;
import com.logistics.calculation.web.dto.CalculationRequest;
import com.logistics.calculation.web.dto.CalculationResponse;
import com.logistics.calculation.web.dto.SiteInput;
import com.logistics.shared.domain.event.BarycentreCalculatedEvent;
import com.logistics.shared.domain.vo.GeoCoordinate;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Orchestrates barycenter calculations.
 *
 * Two request modes:
 *
 *   1. Inline (ad-hoc): sites and weights are supplied directly in the request body.
 *      Used by the "Quick Calculate" feature and the calculation form dialog.
 *
 *   2. Stored: the service fetches the active consumption sites for the given
 *      company from Site Service via Feign, then calculates.
 *
 * Algorithm selection:
 *   - {@code useIterative = false}  →  {@link WeightedBarycentreAlgorithm} (fast, O(n))
 *   - {@code useIterative = true}   →  {@link WeiszfeldAlgorithm} (more accurate, iterative)
 *
 * After a successful calculation the result is:
 *   1. Persisted to the {@code calculation} schema.
 *   2. Published to the {@code logistics.calculation-events} Kafka topic.
 */
@Service
@Observed(name = "calculation.service")
public class CalculationApplicationService {

    private static final Logger log = LoggerFactory.getLogger(CalculationApplicationService.class);
    private static final String CALC_TOPIC = "logistics.calculation-events";

    private final Map<String, BarycentreAlgorithm>          algorithms;
    private final LogisticsCenterRepository                  centerRepository;
    private final SiteServiceClient                          siteClient;
    private final KafkaTemplate<String, BarycentreCalculatedEvent> kafkaTemplate;

    public CalculationApplicationService(
            WeightedBarycentreAlgorithm         simpleAlgorithm,
            WeiszfeldAlgorithm                  iterativeAlgorithm,
            LogisticsCenterRepository           centerRepository,
            SiteServiceClient                   siteClient,
            KafkaTemplate<String, BarycentreCalculatedEvent> kafkaTemplate) {

        this.algorithms       = Map.of(
                simpleAlgorithm.algorithmId(),    simpleAlgorithm,
                iterativeAlgorithm.algorithmId(), iterativeAlgorithm);
        this.centerRepository = Objects.requireNonNull(centerRepository);
        this.siteClient       = Objects.requireNonNull(siteClient);
        this.kafkaTemplate    = Objects.requireNonNull(kafkaTemplate);
    }

    // -----------------------------------------------------------------------
    // Inline calculation (sites supplied in the request)
    // -----------------------------------------------------------------------

    @Transactional
    public CalculationResponse calculateInline(CalculationRequest request) {
        validateSiteCount(request.sites(), 2);

        List<Double> lats    = request.sites().stream().map(SiteInput::latitude).toList();
        List<Double> lons    = request.sites().stream().map(SiteInput::longitude).toList();
        List<Double> weights = request.sites().stream().map(SiteInput::weightTons).toList();

        BarycentreAlgorithm algorithm = selectAlgorithm(request.useIterative());
        BarycentreAlgorithm.AlgorithmResult result = algorithm.calculate(lats, lons, weights);

        double totalTons = weights.stream().mapToDouble(Double::doubleValue).sum();
        List<String> siteIds = request.sites().stream().map(SiteInput::siteId).toList();

        LogisticsCenter center = buildAndPersist(
                request.companyId(), siteIds, result, totalTons, algorithm.algorithmId());

        publishEvent(center, result, request.sites().size());
        log.info("Inline calculation complete: centerId={} company={} algorithm={}",
                center.getId(), request.companyId(), algorithm.algorithmId());

        return CalculationResponse.from(center, request.sites());
    }

    // -----------------------------------------------------------------------
    // Stored-sites calculation (fetch sites from Site Service)
    // -----------------------------------------------------------------------

    @Transactional
    public CalculationResponse calculateForCompany(String companyId, boolean useIterative) {
        List<SiteInput> sites = siteClient.getActiveSitesByCompany(companyId);
        validateSiteCount(sites, 2);

        List<Double> lats    = sites.stream().map(SiteInput::latitude).toList();
        List<Double> lons    = sites.stream().map(SiteInput::longitude).toList();
        List<Double> weights = sites.stream().map(SiteInput::weightTons).toList();

        BarycentreAlgorithm algorithm = selectAlgorithm(useIterative);
        BarycentreAlgorithm.AlgorithmResult result = algorithm.calculate(lats, lons, weights);

        double totalTons = weights.stream().mapToDouble(Double::doubleValue).sum();
        List<String> siteIds = sites.stream().map(SiteInput::siteId).toList();

        LogisticsCenter center = buildAndPersist(
                companyId, siteIds, result, totalTons, algorithm.algorithmId());

        publishEvent(center, result, sites.size());
        log.info("Company calculation complete: centerId={} company={} algorithm={}",
                center.getId(), companyId, algorithm.algorithmId());

        return CalculationResponse.from(center, sites);
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    private BarycentreAlgorithm selectAlgorithm(boolean useIterative) {
        String key = useIterative ? "weiszfeld-iterative" : "weighted-barycenter";
        return algorithms.get(key);
    }

    private LogisticsCenter buildAndPersist(String companyId,
                                             List<String> siteIds,
                                             BarycentreAlgorithm.AlgorithmResult result,
                                             double totalTons,
                                             String algorithmId) {
        LogisticsCenter center = new LogisticsCenter(
                UUID.randomUUID().toString(),
                companyId != null ? companyId : "PREVIEW",
                result.position(),
                totalTons,
                siteIds,
                algorithmId,
                result.iterationCount(),
                result.convergenceErrorKm(),
                Instant.now());

        return centerRepository.save(center);
    }

    private void publishEvent(LogisticsCenter center,
                               BarycentreAlgorithm.AlgorithmResult result,
                               int siteCount) {
        BarycentreCalculatedEvent event = BarycentreCalculatedEvent.of(
                center.getId(),
                center.getCompanyId(),
                center.getOptimalPosition(),
                center.getTotalWeightedTons(),
                siteCount,
                result.iterationCount(),
                result.convergenceErrorKm(),
                center.getAlgorithmDescription());

        kafkaTemplate.send(CALC_TOPIC, center.getCompanyId(), event)
                .whenComplete((r, ex) -> {
                    if (ex != null) log.error("Failed to publish BarycentreCalculatedEvent", ex);
                });
    }

    private static void validateSiteCount(List<?> sites, int minimum) {
        if (sites == null || sites.size() < minimum) {
            throw new InsufficientSitesException(sites == null ? 0 : sites.size(), minimum);
        }
    }

    // -----------------------------------------------------------------------
    // Repository port (inner interface — keeps this file self-contained)
    // -----------------------------------------------------------------------

    public interface LogisticsCenterRepository {
        LogisticsCenter save(LogisticsCenter center);
        java.util.Optional<LogisticsCenter> findById(String id);
        List<LogisticsCenter> findByCompanyId(String companyId);
    }

    // -----------------------------------------------------------------------
    // Domain exception
    // -----------------------------------------------------------------------

    public static class InsufficientSitesException extends RuntimeException {
        public InsufficientSitesException(int actual, int required) {
            super("Insufficient sites: required " + required + " but got " + actual);
        }
    }
}
