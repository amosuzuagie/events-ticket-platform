package com.mstra.tickets.config;

import com.mstra.tickets.filters.UserProvisioningFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            UserProvisioningFilter userProvisioningFilter,
            JwtAuthenticationConverter jwtAuthenticationConverter
    ) throws Exception {

        return http.authorizeHttpRequests(authorize ->
                authorize
                        .requestMatchers(HttpMethod.GET, "/api/v1/published-events/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers("/api/v1/events").hasRole("ORGANIZER")
                        .requestMatchers("/api/v1/ticket-validation").hasRole("STAFF")
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(
                                jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                )
                .addFilterAfter(userProvisioningFilter, BearerTokenAuthenticationFilter.class)
                .build();

    }

}
