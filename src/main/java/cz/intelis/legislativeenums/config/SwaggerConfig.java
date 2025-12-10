package cz.intelis.legislativeenums.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String API_KEY_SCHEME = "X-API-Key";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Legislative Codelists API")
                .version("1.0.0")
                .description("REST API for managing legislative codelists.\n\n" +
                    "## Authentication\n" +
                    "All API endpoints require authentication via API Key.\n\n" +
                    "Include your API key in the request header:\n" +
                    "```\nX-API-Key: your-api-key-here\n```\n\n")
                .contact(new Contact()
                    .name("Intelis, s.r.o.")
                    .email("ludek.cermak@intelis.cz")
                        .url("https://www.intelis.cz"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
            .components(new Components()
                .addSecuritySchemes(API_KEY_SCHEME, new SecurityScheme()
                    .type(SecurityScheme.Type.APIKEY)
                    .in(SecurityScheme.In.HEADER)
                    .name("X-API-Key")
                    .description("Enter your API key to authorize requests")))
            .addSecurityItem(new SecurityRequirement().addList(API_KEY_SCHEME));
    }
}
