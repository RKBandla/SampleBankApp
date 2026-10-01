package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

// Swagger UI: http://localhost:8080/swagger-ui.html
// Click "Authorize" and paste a token from /api/auth/login to call protected endpoints.
@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI bankOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("Sample Bank API")
						.version("1.0")
						.description("Customers, accounts and transfers. Log in with /api/auth/login, then click Authorize."))
				.components(new Components().addSecuritySchemes("bearerAuth",
						new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
				.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
	}
}
