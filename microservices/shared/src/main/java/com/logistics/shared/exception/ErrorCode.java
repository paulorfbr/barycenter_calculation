package com.logistics.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Standardized error codes across the logistics platform.
 * Per constitution requirement: Consistent error handling and observability.
 */
public enum ErrorCode {

    // Generic errors (1000-1099)
    INTERNAL_SERVER_ERROR(1000, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    VALIDATION_ERROR(1001, "Validation error", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND(1002, "Resource not found", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_ACCESS(1003, "Unauthorized access", HttpStatus.UNAUTHORIZED),
    FORBIDDEN_ACCESS(1004, "Forbidden access", HttpStatus.FORBIDDEN),

    // Company service errors (2000-2099)
    COMPANY_NOT_FOUND(2000, "Company not found", HttpStatus.NOT_FOUND),
    COMPANY_ALREADY_EXISTS(2001, "Company already exists", HttpStatus.CONFLICT),
    INVALID_COMPANY_DATA(2002, "Invalid company data", HttpStatus.BAD_REQUEST),

    // Site service errors (3000-3099)
    SITE_NOT_FOUND(3000, "Site not found", HttpStatus.NOT_FOUND),
    SITE_ALREADY_EXISTS(3001, "Site already exists", HttpStatus.CONFLICT),
    INVALID_COORDINATES(3002, "Invalid geographic coordinates", HttpStatus.BAD_REQUEST),
    INVALID_SITE_DATA(3003, "Invalid site data", HttpStatus.BAD_REQUEST),

    // Calculation service errors (4000-4099)
    CALCULATION_FAILED(4000, "Barycenter calculation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    INSUFFICIENT_DATA(4001, "Insufficient data for calculation", HttpStatus.BAD_REQUEST),
    INVALID_ALGORITHM(4002, "Invalid calculation algorithm", HttpStatus.BAD_REQUEST),
    CALCULATION_TIMEOUT(4003, "Calculation timeout exceeded", HttpStatus.REQUEST_TIMEOUT),

    // Dashboard service errors (5000-5099)
    DASHBOARD_DATA_ERROR(5000, "Dashboard data aggregation error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_DATE_RANGE(5001, "Invalid date range", HttpStatus.BAD_REQUEST),
    REPORT_GENERATION_FAILED(5002, "Report generation failed", HttpStatus.INTERNAL_SERVER_ERROR),

    // Authentication/Security errors (6000-6099)
    INVALID_TOKEN(6000, "Invalid authentication token", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(6001, "Authentication token expired", HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS(6002, "Invalid credentials", HttpStatus.UNAUTHORIZED),
    INSUFFICIENT_PRIVILEGES(6003, "Insufficient privileges", HttpStatus.FORBIDDEN);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}