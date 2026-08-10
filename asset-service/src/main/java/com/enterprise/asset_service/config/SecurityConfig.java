package com.enterprise.asset_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enforces @PreAuthorize annotations on endpoints
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 👈 Explicitly instantiate the converter here to bypass autowire package scanning glitches
        JwtAuthConverter explicitConverter = new JwtAuthConverter();

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(explicitConverter)) // 👈 Force use our explicit converter

                        // Defensive logging handlers for instant terminal insight
                        .authenticationEntryPoint((request, response, authException) -> {
                            System.err.println("🚨 RESOURCE SERVER AUTH ERROR: " + authException.getMessage());
                            response.sendError(401, "Unauthorized: Invalid or missing token.");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            System.err.println("⛔ ACCESS DENIED: Active token lacks required roles! " + accessDeniedException.getMessage());
                            response.sendError(403, "Forbidden: Insufficient privileges.");
                        })
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // Targets React development server
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
