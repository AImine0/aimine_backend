package com.aimine.aimine.common.config;

import com.aimine.aimine.auth.oauth.handler.OAuth2FailureHandler;
import com.aimine.aimine.auth.oauth.handler.OAuth2SuccessHandler;
import com.aimine.aimine.auth.oauth.service.OAuthService;
import com.aimine.aimine.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final OAuthService oAuthService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 공개 엔드포인트
                        .requestMatchers("/", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs").permitAll()
                        .requestMatchers("/swagger-resources/**", "/webjars/**").permitAll()
                        .requestMatchers("/auth/google-login").permitAll()
                        .requestMatchers("/search/**").permitAll()
                        .requestMatchers("/ai-services/**").permitAll()
                        .requestMatchers("/keywords/**").permitAll()
                        .requestMatchers("/ai-combinations/**").permitAll()
                        // OAuth2 관련 엔드포인트 허용
                        .requestMatchers("/oauth2/authorization/**").permitAll()
                        .requestMatchers("/login/oauth2/code/**").permitAll()
                        // 인증이 필요한 엔드포인트
                        .requestMatchers("/auth/me", "/auth/logout").authenticated()
                        .requestMatchers("/bookmarks/**", "/reviews/**").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuthService)
                        )
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}