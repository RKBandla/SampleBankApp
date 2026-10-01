package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

	private final JwtAuthFilter jwtAuthFilter;

	public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
		this.jwtAuthFilter = jwtAuthFilter;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.cors(Customizer.withDefaults())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/auth/**").permitAll()                                   // register + login
				.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()  // Swagger UI
				.requestMatchers("/api/admin", "/api/admin/**").hasRole("ADMIN")                // Admin Dashboard
				.requestMatchers("/api/customers", "/api/customers/**").hasRole("ADMIN")        // customer CRUD
				.requestMatchers("/api/customerDashboard/**").hasRole("CUSTOMER")               // own dashboard
				.anyRequest().authenticated())
			.exceptionHandling(ex -> ex
				// no token / bad token → 401
				.authenticationEntryPoint((request, response, e) ->
					writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized - missing or invalid token"))
				// valid token but wrong role (e.g. customer → /api/admin) → 403
				.accessDeniedHandler((request, response, e) ->
					writeError(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden - you do not have access to this resource")))
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	private static void writeError(HttpServletResponse response, int status, String message) throws java.io.IOException {
		response.setStatus(status);
		response.setContentType("application/json");
		response.getWriter().write("{\"error\":\"" + message + "\"}");
	}
}
