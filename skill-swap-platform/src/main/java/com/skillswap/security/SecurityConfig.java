package com.skillswap.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.time.LocalDateTime;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ── Custom Error Responses ───────────────────────────────────
                .exceptionHandling(ex -> ex

                        // When logged in but no permission → 403
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType("application/json");
                            response.getWriter().write("""
                        {
                            "status": 403,
                            "error": "Forbidden",
                            "message": "You do not have permission to perform this action",
                            "timestamp": "%s"
                        }
                        """.formatted(LocalDateTime.now()));
                        })

                        // When not logged in at all → 401
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.getWriter().write("""
                        {
                            "status": 401,
                            "error": "Unauthorized",
                            "message": "You must be logged in to access this resource",
                            "timestamp": "%s"
                        }
                        """.formatted(LocalDateTime.now()));
                        })
                )

                // ── Authorization Rules ──────────────────────────────────────
                .authorizeHttpRequests(auth -> auth

                        // Public
                        .requestMatchers(HttpMethod.POST, "/users/register").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // ADMIN only
                        .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/reviews/**").hasRole("ADMIN")

                        // Authenticated (USER or ADMIN)
                        .requestMatchers(HttpMethod.GET,    "/users/**").authenticated()
                        .requestMatchers(HttpMethod.PUT,    "/users/**").authenticated()
                        .requestMatchers(HttpMethod.POST,   "/skills/**").authenticated()
                        .requestMatchers(HttpMethod.GET,    "/skills/**").authenticated()
                        .requestMatchers(HttpMethod.PUT,    "/skills/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/skills/**").authenticated()
                        .requestMatchers(HttpMethod.POST,   "/swap-requests/**").authenticated()
                        .requestMatchers(HttpMethod.GET,    "/swap-requests/**").authenticated()
                        .requestMatchers(HttpMethod.PUT,    "/swap-requests/**").authenticated()
                        .requestMatchers(HttpMethod.POST,   "/reviews/**").authenticated()
                        .requestMatchers(HttpMethod.GET,    "/reviews/**").authenticated()
                        .requestMatchers(HttpMethod.PUT,    "/reviews/**").authenticated()

                        .anyRequest().authenticated()
                )

                .httpBasic(Customizer.withDefaults())
                .authenticationProvider(authenticationProvider());

        return http.build();
    }
}