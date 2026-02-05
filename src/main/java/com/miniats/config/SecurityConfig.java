package com.miniats.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.Arrays;
import java.util.List;

/**
 * Security configuration for Mini-ATS.
 * Currently configured for development with permissive access.
 *
 * DEVELOPMENT MODE: All endpoints are accessible without authentication.
 * TODO: Add JWT authentication and authorization for production.
 */
@Configuration
@EnableWebSecurity
@org.springframework.core.annotation.Order(1)
public class SecurityConfig {

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable()) // Inaktivera CSRF för API-testning
//                .cors(org.springframework.security.config.Customizer.withDefaults())
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/candidates/**", "/jobs/**", "/organizations/**", "/users/**", "/health/**", "/applications/**").permitAll() // Tillåt alla anrop till API:et
//                        .anyRequest().authenticated()
//                );
//        return http.build();
//    }
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            // 1. Inaktivera standardinloggning och HTTP Basic helt
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            // 2. Inaktivera CSRF (viktigt för API:er)
            .csrf(csrf -> csrf.disable())

            // 3. Aktivera CORS
            .cors(org.springframework.security.config.Customizer.withDefaults())

            // 4. Tillåt ALLT under utveckling för att verifiera anslutningen
            .authorizeHttpRequests(auth -> auth
                    .anyRequest().permitAll()
            )

            // 5. Gör sessioner statslösa (bra för API:er)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
}

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Detta tillåter alla domäner att prata med din backend under utveckling
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:8081", "http://localhost:5173"));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}