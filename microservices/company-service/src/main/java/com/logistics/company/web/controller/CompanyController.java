package com.logistics.company.web.controller;

import com.logistics.company.application.port.in.ManageCompanyUseCase;
import com.logistics.company.domain.model.Company;
import com.logistics.company.web.dto.CompanyRequest;
import com.logistics.company.web.dto.CompanyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * REST controller for the Company resource.
 *
 * Base path:  /api/v1/companies
 *
 * All request bodies are validated with Bean Validation before reaching
 * the application service.  The {@code GlobalExceptionHandler} translates
 * domain exceptions to standardised {@code ApiError} responses.
 */
@RestController
@RequestMapping("/api/v1/companies")
@Tag(name = "Companies", description = "Company lifecycle management — create, read, update, activate, deactivate, delete")
public class CompanyController {

    private final ManageCompanyUseCase useCase;

    public CompanyController(ManageCompanyUseCase useCase) {
        this.useCase = useCase;
    }

    // -----------------------------------------------------------------------
    // POST /api/v1/companies
    // -----------------------------------------------------------------------

    @PostMapping
    @Operation(
        summary     = "Create a company",
        description = "Creates a new company in ACTIVE status and publishes a CompanyCreated event.",
        responses   = {
            @ApiResponse(responseCode = "201", description = "Company created"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                         content = @Content(schema = @Schema(ref = "#/components/schemas/ApiError"))),
            @ApiResponse(responseCode = "409", description = "Company name already exists")
        }
    )
    public ResponseEntity<CompanyResponse> create(@Valid @RequestBody CompanyRequest request) {
        Company created = useCase.createCompany(toCreateCommand(request));
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(CompanyResponse.from(created));
    }

    // -----------------------------------------------------------------------
    // GET /api/v1/companies
    // -----------------------------------------------------------------------

    @GetMapping
    @Operation(summary = "List all companies", description = "Returns all companies; optionally filtered by status.")
    public List<CompanyResponse> list(
            @Parameter(description = "Filter by lifecycle status", example = "ACTIVE")
            @RequestParam(required = false) Company.Status status) {

        List<Company> companies = (status != null)
                ? useCase.findByStatus(status)
                : useCase.findAll();
        return companies.stream().map(CompanyResponse::from).toList();
    }

    // -----------------------------------------------------------------------
    // GET /api/v1/companies/{id}
    // -----------------------------------------------------------------------

    @GetMapping("/{id}")
    @Operation(summary = "Get a company by ID")
    public ResponseEntity<CompanyResponse> getById(
            @Parameter(description = "Company UUID", required = true)
            @PathVariable String id) {

        return useCase.findById(id)
                .map(c -> ResponseEntity.ok(CompanyResponse.from(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    // -----------------------------------------------------------------------
    // PUT /api/v1/companies/{id}
    // -----------------------------------------------------------------------

    @PutMapping("/{id}")
    @Operation(summary = "Update a company")
    public CompanyResponse update(
            @PathVariable String id,
            @Valid @RequestBody CompanyRequest request) {

        Company updated = useCase.updateCompany(toUpdateCommand(id, request));
        return CompanyResponse.from(updated);
    }

    // -----------------------------------------------------------------------
    // PATCH /api/v1/companies/{id}/activate
    // -----------------------------------------------------------------------

    @PatchMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Activate a company")
    public void activate(@PathVariable String id) {
        useCase.activateCompany(id);
    }

    // -----------------------------------------------------------------------
    // PATCH /api/v1/companies/{id}/deactivate
    // -----------------------------------------------------------------------

    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deactivate a company")
    public void deactivate(@PathVariable String id) {
        useCase.deactivateCompany(id);
    }

    // -----------------------------------------------------------------------
    // DELETE /api/v1/companies/{id}
    // -----------------------------------------------------------------------

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a company")
    public void delete(@PathVariable String id) {
        useCase.deleteCompany(id);
    }

    // -----------------------------------------------------------------------
    // Request → Command mapping
    // -----------------------------------------------------------------------

    private ManageCompanyUseCase.CreateCompanyCommand toCreateCommand(CompanyRequest r) {
        return new ManageCompanyUseCase.CreateCompanyCommand(
                r.name(), r.type(), r.taxId(),
                r.contactName(), r.contactEmail(), r.contactPhone(), r.notes());
    }

    private ManageCompanyUseCase.UpdateCompanyCommand toUpdateCommand(String id, CompanyRequest r) {
        return new ManageCompanyUseCase.UpdateCompanyCommand(
                id, r.name(), r.type(), r.taxId(),
                r.contactName(), r.contactEmail(), r.contactPhone(), r.notes());
    }
}
