package com.aimine.aimine.common.config;

import com.aimine.aimine.auth.oauth.handler.OAuth2FailureHandler;
import com.aimine.aimine.auth.oauth.handler.OAuth2SuccessHandler;
import com.aimine.aimine.auth.oauth.service.OAuthService;
import com.aimine.aimine.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

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
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 프리플라이트 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/images/**", "/static/**").permitAll()

                        // 공개 엔드포인트
                        .requestMatchers("/", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs").permitAll()
                        .requestMatchers("/swagger-resources/**", "/webjars/**").permitAll()
                        .requestMatchers("/auth/google-login").permitAll()
                        .requestMatchers("/search/**").permitAll()
                        .requestMatchers("/ai-services/**").permitAll()
                        .requestMatchers("/categories/**").permitAll()
                        .requestMatchers("/keywords/**").permitAll()
                        .requestMatchers("/ai-combinations/**").permitAll()


                        // 리뷰 관련: 조회는 공개, 작성/삭제는 인증 필요
                        .requestMatchers(HttpMethod.GET, "/reviews/**").permitAll()      // 리뷰 조회 - 공개
                        .requestMatchers(HttpMethod.POST, "/reviews/**").authenticated() // 리뷰 작성 - 인증 필요
                        .requestMatchers(HttpMethod.DELETE, "/reviews/**").authenticated() // 리뷰 삭제 - 인증 필요

                        // OAuth2 관련 엔드포인트 허용
                        .requestMatchers("/oauth2/authorization/**").permitAll()
                        .requestMatchers("/login/oauth2/code/**").permitAll()

                        // 인증이 필요한 엔드포인트

                        .requestMatchers("/auth/me", "/auth/logout").authenticated()
                        .requestMatchers("/bookmarks/**").authenticated() // 북마크는 모든 작업에 인증 필요

                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuthService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                )
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ✅ 허용 오리진(패턴) — '*' 사용 금지 (credentials=true와 충돌 위험)
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:5173",
                "https://aimine.vercel.app",
                "https://*.railway.app"
        ));

        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        // (선택) 프론트에서 읽어야 하는 헤더가 있으면 노출
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Location"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
