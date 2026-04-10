package com.logistics.shared.tracing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods for automatic tracing.
 * Per constitution requirement: Observability for business operations.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TraceableOperation {

    /**
     * Custom span name. If not provided, uses class.method format.
     */
    String value() default "";

    /**
     * Operation type for business context.
     */
    String operationType() default "";

    /**
     * Whether to include method parameters as span tags.
     */
    boolean includeParams() default false;
}