package com.aimine.core.config;

import com.aimine.core.security.JwtAuthenticationEntryPoint;
import com.aimine.core.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint entryPoint;
    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationEntryPoint entryPoint, JwtAuthenticationFilter jwtFilter) {
        this.entryPoint = entryPoint;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(reg -> reg
                        .requestMatchers("/actuator/health", "/v3/api-docs/**", "/swagger-ui/**", "/uploads/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().permitAll()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(entryPoint))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(Customizer.withDefaults());
        return http.build();
    }
}
