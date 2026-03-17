package com.logistics.site.web.controller;

import com.logistics.shared.domain.vo.GeoCoordinate;
import com.logistics.shared.domain.event.SiteAddedEvent;
import com.logistics.shared.domain.event.SiteRemovedEvent;
import com.logistics.shared.domain.event.SiteUpdatedEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * REST controller for ConsumptionSite CRUD operations.
 *
 * Base path: /api/v1/sites
 *
 * The controller uses an in-memory store for the skeleton implementation.
 * Replace {@code store} with a JPA repository adapter in a production build,
 * following the same hexagonal pattern as Company Service.
 */
@RestController
@RequestMapping("/api/v1/sites")
@Tag(name = "Sites", description = "Consumption site management — the geographic inputs to barycenter calculations")
public class ConsumptionSiteController {

    // Skeleton in-memory store — replace with JPA adapter in production
    private final Map<String, SiteDto> store = new ConcurrentHashMap<>();
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String SITE_EVENTS_TOPIC = "logistics.site-events";

    public ConsumptionSiteController(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // -----------------------------------------------------------------------
    // POST /api/v1/sites
    // -----------------------------------------------------------------------

    @PostMapping
    @Operation(summary = "Add a consumption site")
    public ResponseEntity<SiteDto> addSite(@Valid @RequestBody SiteCreateRequest request) {
        String id = UUID.randomUUID().toString();
        SiteDto site = new SiteDto(
                id, request.companyId(), request.name(), request.description(),
                request.latitude(), request.longitude(), request.weightTons(),
                request.address(), request.city(), request.country(),
                "ACTIVE", Instant.now());

        store.put(id, site);

        kafkaTemplate.send(SITE_EVENTS_TOPIC, request.companyId(),
                SiteAddedEvent.of(id, request.companyId(), request.name(),
                        GeoCoordinate.of(request.latitude(), request.longitude()),
                        request.weightTons()));

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
        return ResponseEntity.created(location).body(site);
    }

    // -----------------------------------------------------------------------
    // GET /api/v1/sites/{id}
    // -----------------------------------------------------------------------

    @GetMapping("/{id}")
    @Operation(summary = "Get a site by ID")
    public ResponseEntity<SiteDto> getById(@PathVariable String id) {
        SiteDto site = store.get(id);
        return site != null ? ResponseEntity.ok(site) : ResponseEntity.notFound().build();
    }

    // -----------------------------------------------------------------------
    // GET /api/v1/sites/company/{companyId}
    // -----------------------------------------------------------------------

    @GetMapping("/company/{companyId}")
    @Operation(summary = "List all sites for a company")
    public List<SiteDto> listByCompany(@PathVariable String companyId) {
        return store.values().stream()
                .filter(s -> companyId.equals(s.companyId()))
                .toList();
    }

    // -----------------------------------------------------------------------
    // GET /api/v1/sites/company/{companyId}/active
    // -----------------------------------------------------------------------

    @GetMapping("/company/{companyId}/active")
    @Operation(summary = "List active sites for a company — used by Calculation Service")
    public List<SiteDto> listActiveSitesByCompany(@PathVariable String companyId) {
        return store.values().stream()
                .filter(s -> companyId.equals(s.companyId()) && "ACTIVE".equals(s.status()))
                .toList();
    }

    // -----------------------------------------------------------------------
    // PUT /api/v1/sites/{id}
    // -----------------------------------------------------------------------

    @PutMapping("/{id}")
    @Operation(summary = "Update a consumption site")
    public ResponseEntity<SiteDto> updateSite(
            @PathVariable String id,
            @Valid @RequestBody SiteUpdateRequest request) {

        SiteDto existing = store.get(id);
        if (existing == null) return ResponseEntity.notFound().build();

        SiteDto updated = new SiteDto(
                id, existing.companyId(), request.name(), request.description(),
                request.latitude(), request.longitude(), request.weightTons(),
                request.address(), request.city(), request.country(),
                existing.status(), Instant.now());

        store.put(id, updated);

        kafkaTemplate.send(SITE_EVENTS_TOPIC, existing.companyId(),
                SiteUpdatedEvent.of(id, existing.companyId(),
                        GeoCoordinate.of(request.latitude(), request.longitude()),
                        request.weightTons()));

        return ResponseEntity.ok(updated);
    }

    // -----------------------------------------------------------------------
    // DELETE /api/v1/sites/{id}
    // -----------------------------------------------------------------------

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove a consumption site")
    public void removeSite(@PathVariable String id) {
        SiteDto site = store.remove(id);
        if (site != null) {
            kafkaTemplate.send(SITE_EVENTS_TOPIC, site.companyId(),
                    SiteRemovedEvent.of(id, site.companyId()));
        }
    }

    // -----------------------------------------------------------------------
    // Inner DTOs and request records
    // -----------------------------------------------------------------------

    public record SiteDto(
            String  id,
            String  companyId,
            String  name,
            String  description,
            double  latitude,
            double  longitude,
            double  weightTons,
            String  address,
            String  city,
            String  country,
            String  status,
            Instant createdAt) {}

    public record SiteCreateRequest(
            @NotBlank String companyId,
            @NotBlank String name,
                      String description,
            @DecimalMin("-90.0") @DecimalMax("90.0")   double latitude,
            @DecimalMin("-180.0") @DecimalMax("180.0") double longitude,
            @Positive double weightTons,
                      String address,
                      String city,
                      String country) {}

    public record SiteUpdateRequest(
            @NotBlank String name,
                      String description,
            @DecimalMin("-90.0") @DecimalMax("90.0")   double latitude,
            @DecimalMin("-180.0") @DecimalMax("180.0") double longitude,
            @Positive double weightTons,
                      String address,
                      String city,
                      String country) {}
}
