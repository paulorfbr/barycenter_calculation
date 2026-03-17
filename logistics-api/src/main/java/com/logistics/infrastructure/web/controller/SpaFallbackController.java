package com.logistics.infrastructure.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Handles Angular HTML5 history-mode routing for the single-page application.
 *
 * When a user navigates directly to an Angular route (e.g. /companies/details/123
 * or refreshes the browser), the browser sends a GET request for that path to
 * the server. Without this handler, Spring Boot would return a 404 because there
 * is no static file at that path.
 *
 * This controller intercepts all non-API, non-static-asset GET requests and
 * forwards them to index.html, letting the Angular router take over client-side.
 *
 * Exclusion rules (requests NOT forwarded — handled by their own handlers):
 *   /api/**         — REST controllers
 *   /actuator/**    — Spring Boot Actuator
 *   /swagger-ui/**  — SpringDoc UI
 *   /api-docs/**    — OpenAPI JSON
 *   /static/**      — static resources
 *   Requests with a file extension (e.g. .js, .css, .ico, .png, .map)
 */
@Controller
public class SpaFallbackController {

    /**
     * Returns the Angular SPA index.html for all unmatched GET requests that
     * are not API calls and do not have a file extension (no dot in the last
     * path segment).
     *
     * The forward to /index.html causes Spring's ResourceHttpRequestHandler to
     * serve the file from classpath:/static/index.html.
     */
    @GetMapping(value = {
            "/",
            "/dashboard",
            "/companies",
            "/companies/**",
            "/sites",
            "/sites/**",
            "/barycenter",
            "/barycenter/**",
            "/settings",
            "/settings/**"
    })
    public String forwardToAngular(HttpServletRequest request) {
        // If the URI looks like a static asset (has a dot in the filename), let
        // Spring's default resource handler deal with it — do not forward.
        String uri = request.getRequestURI();
        String lastSegment = uri.substring(uri.lastIndexOf('/') + 1);
        if (lastSegment.contains(".")) {
            return null;   // let the resource handler serve it (or 404)
        }
        return "forward:/index.html";
    }
}
