package com.logistics.infrastructure.web.advice;

import com.logistics.application.port.in.CalculateBarycentreUseCase;
import com.logistics.application.port.in.ManageCompanyUseCase;
import com.logistics.application.port.in.ManageConsumptionSiteUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Centralised exception-to-HTTP-response mapping for all REST controllers.
 *
 * Uses RFC 7807 Problem Details (ProblemDetail) for machine-readable error
 * responses consistent with the Spring 6 / Boot 3 error model.
 *
 * Error categories handled:
 *   - Domain not-found exceptions   -> 404 Not Found
 *   - Domain invariant violations   -> 400 Bad Request
 *   - Bean Validation failures      -> 422 Unprocessable Entity
 *   - Spring ResponseStatusException -> passthrough
 *   - Unexpected errors             -> 500 Internal Server Error
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final URI NOT_FOUND_TYPE       = URI.create("https://logistics.example.com/errors/not-found");
    private static final URI BAD_REQUEST_TYPE     = URI.create("https://logistics.example.com/errors/bad-request");
    private static final URI VALIDATION_TYPE      = URI.create("https://logistics.example.com/errors/validation");
    private static final URI SERVER_ERROR_TYPE    = URI.create("https://logistics.example.com/errors/internal");

    // =========================================================================
    // Domain: entity not found
    // =========================================================================

    @ExceptionHandler(ManageCompanyUseCase.CompanyNotFoundException.class)
    public ProblemDetail handleCompanyNotFound(ManageCompanyUseCase.CompanyNotFoundException ex) {
        return buildProblem(HttpStatus.NOT_FOUND, NOT_FOUND_TYPE, "Company Not Found", ex.getMessage());
    }

    @ExceptionHandler(ManageConsumptionSiteUseCase.ConsumptionSiteNotFoundException.class)
    public ProblemDetail handleSiteNotFound(
            ManageConsumptionSiteUseCase.ConsumptionSiteNotFoundException ex) {
        return buildProblem(HttpStatus.NOT_FOUND, NOT_FOUND_TYPE, "Site Not Found", ex.getMessage());
    }

    // =========================================================================
    // Domain: business rule violations
    // =========================================================================

    @ExceptionHandler(CalculateBarycentreUseCase.InsufficientSitesException.class)
    public ProblemDetail handleInsufficientSites(
            CalculateBarycentreUseCase.InsufficientSitesException ex) {
        return buildProblem(HttpStatus.BAD_REQUEST, BAD_REQUEST_TYPE,
                "Insufficient Sites", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        return buildProblem(HttpStatus.BAD_REQUEST, BAD_REQUEST_TYPE,
                "Invalid Request", ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(IllegalStateException ex) {
        return buildProblem(HttpStatus.CONFLICT, BAD_REQUEST_TYPE,
                "Invalid State Transition", ex.getMessage());
    }

    // =========================================================================
    // Spring MVC: Bean Validation failure
    // =========================================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value",
                        (existing, replacement) -> existing));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "One or more fields failed validation.");
        problem.setType(VALIDATION_TYPE);
        problem.setTitle("Validation Failed");
        problem.setProperty("fieldErrors", fieldErrors);
        problem.setProperty("timestamp", Instant.now().toString());
        return problem;
    }

    // =========================================================================
    // Spring: explicit status exceptions (pass-through)
    // =========================================================================

    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handleResponseStatus(ResponseStatusException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.valueOf(ex.getStatusCode().value()),
                ex.getReason() != null ? ex.getReason() : ex.getMessage());
        problem.setType(BAD_REQUEST_TYPE);
        problem.setTitle("Request Error");
        problem.setProperty("timestamp", Instant.now().toString());
        return problem;
    }

    // =========================================================================
    // Catch-all — unexpected server errors
    // =========================================================================

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        log.error("Unhandled exception in REST layer", ex);
        return buildProblem(HttpStatus.INTERNAL_SERVER_ERROR, SERVER_ERROR_TYPE,
                "Internal Server Error",
                "An unexpected error occurred. Please contact support.");
    }

    // =========================================================================
    // Internal builder
    // =========================================================================

    private ProblemDetail buildProblem(HttpStatus status, URI type, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(type);
        problem.setTitle(title);
        problem.setProperty("timestamp", Instant.now().toString());
        return problem;
    }
}
