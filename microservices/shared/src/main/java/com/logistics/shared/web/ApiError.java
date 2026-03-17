package com.logistics.shared.web;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/**
 * Canonical error response body returned by all microservices.
 *
 * Standardising the error envelope across services lets the API Gateway and
 * the Angular SPA parse errors uniformly without service-specific handling.
 *
 * Example JSON:
 * <pre>
 * {
 *   "timestamp": "2026-03-17T10:00:00Z",
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "Company not found: abc-123",
 *   "path": "/api/v1/companies/abc-123",
 *   "traceId": "7d3f9a1b2e4c",
 *   "violations": null
 * }
 * </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        Instant       timestamp,
        int           status,
        String        error,
        String        message,
        String        path,
        String        traceId,
        List<Violation> violations
) {
    /** A single Bean Validation constraint violation. */
    public record Violation(String field, String message) {}

    public static ApiError of(int status, String error, String message, String path, String traceId) {
        return new ApiError(Instant.now(), status, error, message, path, traceId, null);
    }

    public static ApiError withViolations(int status, String error, String message,
                                          String path, String traceId, List<Violation> violations) {
        return new ApiError(Instant.now(), status, error, message, path, traceId, violations);
    }
}
