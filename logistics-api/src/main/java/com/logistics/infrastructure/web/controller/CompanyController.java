package com.logistics.infrastructure.web.controller;

import com.logistics.application.dto.CompanyDto;
import com.logistics.application.port.in.ManageCompanyUseCase;
import com.logistics.application.service.CompanyService;
import com.logistics.domain.model.Company;
import com.logistics.infrastructure.web.mapper.CompanyMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for company management.
 *
 * All endpoints are prefixed with /api/v1/companies and correspond to the
 * Angular DataService company methods.
 *
 * The controller is deliberately thin: validation and HTTP mapping are its
 * only responsibilities. All business logic lives in CompanyService.
 */
@RestController
@RequestMapping("/api/v1/companies")
@Tag(name = "Companies", description = "Company lifecycle management endpoints")
public class CompanyController {

    private final CompanyService companyService;
    private final CompanyMapper  mapper;

    public CompanyController(CompanyService companyService, CompanyMapper mapper) {
        this.companyService = companyService;
        this.mapper         = mapper;
    }

    // =========================================================================
    // GET /api/v1/companies
    // =========================================================================

    @GetMapping
    @Operation(summary = "List all companies",
               description = "Returns all companies ordered by name ascending. " +
                             "Use the status query parameter to filter by lifecycle status.")
    @ApiResponse(responseCode = "200", description = "Company list returned successfully")
    public List<CompanyDto> listCompanies(
            @Parameter(description = "Filter by status: ACTIVE, INACTIVE, or PENDING")
            @RequestParam(required = false) String status) {

        List<Company> companies = (status != null)
                ? companyService.findByStatus(Company.Status.valueOf(status.toUpperCase()))
                : companyService.findAll();

        return companies.stream()
                .map(mapper::toDto)
                .toList();
    }

    // =========================================================================
    // GET /api/v1/companies/{id}
    // =========================================================================

    @GetMapping("/{id}")
    @Operation(summary = "Get company by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Company found"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public CompanyDto getCompany(
            @Parameter(description = "Company UUID", required = true)
            @PathVariable String id) {

        Company company = companyService.findById(id)
                .orElseThrow(() -> new ManageCompanyUseCase.CompanyNotFoundException(id));
        return mapper.toDto(company);
    }

    // =========================================================================
    // POST /api/v1/companies
    // =========================================================================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new company")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Company created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    public CompanyDto createCompany(
            @Valid @RequestBody CreateCompanyRequest request) {

        Company company = companyService.createCompany(
                new ManageCompanyUseCase.CreateCompanyCommand(
                        request.name(),
                        Company.Type.valueOf(request.type().toUpperCase()),
                        request.taxId(),
                        request.contactName(),
                        request.contactEmail(),
                        request.contactPhone(),
                        request.notes()));

        return mapper.toDto(company);
    }

    // =========================================================================
    // PUT /api/v1/companies/{id}
    // =========================================================================

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing company")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Company updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public CompanyDto updateCompany(
            @PathVariable String id,
            @Valid @RequestBody UpdateCompanyRequest request) {

        Company company = companyService.updateCompany(
                new ManageCompanyUseCase.UpdateCompanyCommand(
                        id,
                        request.name(),
                        Company.Type.valueOf(request.type().toUpperCase()),
                        request.taxId(),
                        request.contactName(),
                        request.contactEmail(),
                        request.contactPhone(),
                        request.notes()));

        return mapper.toDto(company);
    }

    // =========================================================================
    // PATCH /api/v1/companies/{id}/activate
    // =========================================================================

    @PatchMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Activate a company")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Company activated"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public void activateCompany(@PathVariable String id) {
        companyService.activateCompany(id);
    }

    // =========================================================================
    // PATCH /api/v1/companies/{id}/deactivate
    // =========================================================================

    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deactivate a company")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Company deactivated"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public void deactivateCompany(@PathVariable String id) {
        companyService.deactivateCompany(id);
    }

    // =========================================================================
    // DELETE /api/v1/companies/{id}
    // =========================================================================

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a company and all its consumption sites")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Company deleted"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public void deleteCompany(@PathVariable String id) {
        companyService.deleteCompany(id);
    }

    // =========================================================================
    // Request body records
    // =========================================================================

    /**
     * Request body for POST /api/v1/companies.
     * Mirrors CreateCompanyPayload in the Angular model.
     */
    public record CreateCompanyRequest(
            @NotBlank(message = "Company name must not be blank")
            String name,

            @NotBlank(message = "Company type must not be blank")
            String type,            // "SHIPPER" | "CARRIER" | "BOTH"

            String taxId,
            String contactName,
            String contactEmail,
            String contactPhone,
            String notes) {}

    /**
     * Request body for PUT /api/v1/companies/{id}.
     * All fields are required for a full update.
     */
    public record UpdateCompanyRequest(
            @NotBlank(message = "Company name must not be blank")
            String name,

            @NotBlank(message = "Company type must not be blank")
            String type,

            String taxId,
            String contactName,
            String contactEmail,
            String contactPhone,
            String notes) {}
}
