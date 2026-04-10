package com.logistics.shared.tracing;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

/**
 * Service for managing distributed tracing in business operations.
 * Per constitution requirement: Comprehensive observability across microservices.
 */
@Service
public class TracingService {

    private final Tracer tracer;

    public TracingService(Tracer tracer) {
        this.tracer = tracer;
    }

    /**
     * Execute operation with custom span.
     */
    public <T> T withSpan(String spanName, Supplier<T> operation) {
        Span span = tracer.nextSpan().name(spanName).start();
        try (Tracer.SpanInScope spanInScope = tracer.withSpanInScope(span)) {
            return operation.get();
        } catch (Exception ex) {
            span.tag("error", ex.getMessage());
            span.tag("error.class", ex.getClass().getSimpleName());
            throw ex;
        } finally {
            span.end();
        }
    }

    /**
     * Execute operation with custom span and tags.
     */
    public <T> T withSpan(String spanName, java.util.Map<String, String> tags, Supplier<T> operation) {
        Span span = tracer.nextSpan().name(spanName);
        tags.forEach(span::tag);
        span.start();

        try (Tracer.SpanInScope spanInScope = tracer.withSpanInScope(span)) {
            return operation.get();
        } catch (Exception ex) {
            span.tag("error", ex.getMessage());
            span.tag("error.class", ex.getClass().getSimpleName());
            throw ex;
        } finally {
            span.end();
        }
    }

    /**
     * Add tag to current span.
     */
    public void addTag(String key, String value) {
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            currentSpan.tag(key, value);
        }
    }

    /**
     * Add business context to current span.
     */
    public void addBusinessContext(String companyId, String operationType) {
        addTag("business.company.id", companyId);
        addTag("business.operation.type", operationType);
    }

    /**
     * Add calculation context to current span.
     */
    public void addCalculationContext(String algorithm, int siteCount, double latitude, double longitude) {
        addTag("calculation.algorithm", algorithm);
        addTag("calculation.site.count", String.valueOf(siteCount));
        addTag("calculation.result.latitude", String.valueOf(latitude));
        addTag("calculation.result.longitude", String.valueOf(longitude));
    }
}