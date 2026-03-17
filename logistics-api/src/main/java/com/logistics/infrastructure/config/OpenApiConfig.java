package com.logistics.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3 metadata bean.
 *
 * Swagger UI is accessible at: http://localhost:8080/swagger-ui.html
 * Raw OpenAPI JSON is at:      http://localhost:8080/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI logisticsOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Logistics Barycenter API")
                        .description("""
                                REST API for the Define Company Logistic Place application.

                                Provides full CRUD for companies and consumption sites, \
                                barycenter calculation using both weighted-centroid and \
                                Weiszfeld iterative algorithms, and a pre-aggregated \
                                dashboard summary endpoint.

                                The Angular SPA is served from the root path (/). \
                                All API endpoints are prefixed with /api/v1.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Logistics Platform Team")
                                .email("platform@logistics.example.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://logistics.example.com/license")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local development server"),
                        new Server()
                                .url("https://logistics.example.com")
                                .description("Production server")));
    }
}
