package com.logistics.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC configuration for CORS, static resource handling, and the
 * Angular SPA single-page application fallback.
 *
 * Static content strategy:
 *   1. Requests to /api/** are handled by REST controllers.
 *   2. Requests to /actuator/** are handled by Spring Boot Actuator.
 *   3. Requests to /swagger-ui.html and /api-docs/** are handled by SpringDoc.
 *   4. All other GET requests that do not match a file in /static/ are
 *      redirected to /index.html by SpaFallbackController so that Angular's
 *      HTML5 history routing works correctly on page refresh.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final LogisticsProperties properties;

    public WebConfig(LogisticsProperties properties) {
        this.properties = properties;
    }

    /**
     * Registers CORS rules using the origins configured in application.yml.
     * In production the Angular SPA is served from the same origin (Spring Boot),
     * so CORS is only needed during development (Angular CLI dev server on :4200).
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        var allowedOrigins = properties.cors().allowedOrigins();
        if (allowedOrigins == null || allowedOrigins.isEmpty()) {
            return;
        }

        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins.toArray(String[]::new))
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * Serves the Angular build output from classpath:/static/.
     * The Angular build should be placed in
     * {@code src/main/resources/static/} prior to running the Spring Boot build.
     *
     * Cache control is set to no-cache for index.html so browsers always
     * fetch the latest shell, while JS/CSS chunks can be cached long-term
     * because Angular generates content-hashed filenames.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(0);
    }
}
