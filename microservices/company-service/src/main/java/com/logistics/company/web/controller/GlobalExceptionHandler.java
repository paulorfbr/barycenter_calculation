package com.logistics.company.web.controller;

import com.logistics.company.application.port.in.ManageCompanyUseCase;
import com.logistics.shared.web.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Translates domain exceptions and Spring validation errors into the
 * canonical {@link ApiError} response body used across all microservices.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ManageCompanyUseCase.CompanyNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            ManageCompanyUseCase.CompanyNotFoundException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of(404, "Not Found", ex.getMessage(), req.getRequestURI(), traceId()));
    }

    @ExceptionHandler(ManageCompanyUseCase.DuplicateCompanyNameException.class)
    public ResponseEntity<ApiError> handleConflict(
            ManageCompanyUseCase.DuplicateCompanyNameException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of(409, "Conflict", ex.getMessage(), req.getRequestURI(), traceId()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest req) {

        List<ApiError.Violation> violations = ex.getBindingResult().getAllErrors().stream()
                .filter(e -> e instanceof FieldError)
                .map(e -> new ApiError.Violation(((FieldError) e).getField(), e.getDefaultMessage()))
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiError.withViolations(400, "Validation Failed",
                        "Request body contains invalid fields.",
                        req.getRequestURI(), traceId(), violations));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of(400, "Bad Request", ex.getMessage(), req.getRequestURI(), traceId()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneral(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception on {}", req.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.of(500, "Internal Server Error",
                        "An unexpected error occurred.", req.getRequestURI(), traceId()));
    }

    private static String traceId() {
        // Micrometer Tracing provides the current trace ID via MDC;
        // fall back to empty string if no active span.
        String traceId = org.slf4j.MDC.get("traceId");
        return traceId != null ? traceId : "";
    }
}
