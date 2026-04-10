package com.logistics.shared.exception;

/**
 * Base exception for all logistics platform business logic errors.
 * Per constitution requirement: Comprehensive exception handling across microservices.
 */
public class LogisticsException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object[] params;

    public LogisticsException(ErrorCode errorCode, Object... params) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.params = params;
    }

    public LogisticsException(ErrorCode errorCode, Throwable cause, Object... params) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.params = params;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Object[] getParams() {
        return params;
    }
}