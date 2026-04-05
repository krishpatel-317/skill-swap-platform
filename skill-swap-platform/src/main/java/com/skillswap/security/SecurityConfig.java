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
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);
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

                .headers(headers ->
                        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // CUSTOM ERROR HANDLING
                .exceptionHandling(ex -> ex

                        // 403 → Permission issue
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

                        // 401 → Authentication issue (FIXED LOGIC)
                        .authenticationEntryPoint((request, response, authException) -> {

                            response.setStatus(401);
                            response.setContentType("application/json");

                            String authHeader = request.getHeader("Authorization");
                            String message;

                            if (authHeader == null || authHeader.isBlank()) {
                                message = "You must be logged in to access this resource";
                            } else {
                                message = "Invalid username or password";
                            }

                            response.getWriter().write("""
                    {
                        "status": 401,
                        "error": "Unauthorized",
                        "message": "%s",
                        "timestamp": "%s"
                    }
                    """.formatted(message, LocalDateTime.now()));
                        })
                )

                // AUTHORIZATION RULES
                .authorizeHttpRequests(auth -> auth

                        // Public
                        .requestMatchers(HttpMethod.POST, "/users/register").permitAll()

                        // ADMIN only
                        .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/reviews/**").hasRole("ADMIN")

                        // USER only
                        .requestMatchers(HttpMethod.POST, "/skills/**").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/skills/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/skills/**").hasRole("USER")

                        .requestMatchers(HttpMethod.POST, "/swap-requests/**").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/swap-requests/**").hasRole("USER")

                        .requestMatchers(HttpMethod.POST, "/reviews/**").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/reviews/**").hasRole("USER")

                        // VIEW (both roles)
                        .requestMatchers(HttpMethod.GET, "/users/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/skills/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/swap-requests/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/reviews/**").authenticated()

                        // Update user → USER only
                        .requestMatchers(HttpMethod.PUT, "/users/**").hasRole("USER")

                        .anyRequest().authenticated()
                )

                .httpBasic(Customizer.withDefaults())
                .authenticationProvider(authenticationProvider());

        return http.build();
    }
}