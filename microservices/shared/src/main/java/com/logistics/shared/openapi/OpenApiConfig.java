package com.logistics.shared.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3.0 configuration for microservices documentation.
 * Per constitution requirement: API-first design with comprehensive documentation.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI logisticsOpenApi(OpenApiProperties properties) {
        return new OpenAPI()
                .info(buildApiInfo(properties))
                .servers(buildServers(properties))
                .addSecurityItem(new SecurityRequirement().addList("JWT"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("JWT", buildJwtSecurityScheme()));
    }

    private Info buildApiInfo(OpenApiProperties properties) {
        return new Info()
                .title(properties.title())
                .description(properties.description())
                .version(properties.version())
                .contact(new Contact()
                        .name("Logistics Platform Team")
                        .email("platform@logistics.com")
                        .url("https://logistics-platform.com"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0"));
    }

    private List<Server> buildServers(OpenApiProperties properties) {
        return List.of(
                new Server().url(properties.serverUrl()).description("Current environment"),
                new Server().url("http://localhost:8080").description("Local development"),
                new Server().url("https://api.logistics-platform.com").description("Production")
        );
    }

    private SecurityScheme buildJwtSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("JWT Authentication token");
    }

    @ConfigurationProperties(prefix = "logistics.openapi")
    public record OpenApiProperties(
            String title,
            String description,
            String version,
            String serverUrl
    ) {
        public static OpenApiProperties defaultProperties() {
            return new OpenApiProperties(
                    "Logistics Platform API",
                    "Comprehensive logistics optimization platform for barycenter calculations",
                    "1.0.0",
                    "http://localhost:8080"
            );
        }
    }
}