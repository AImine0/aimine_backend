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
                // CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // JWT 사용: CSRF 비활성화
                .csrf(csrf -> csrf.disable())
                // 세션 미사용
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 인가 규칙
                .authorizeHttpRequests(auth -> auth
                        // 프리플라이트 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 공개 엔드포인트
                        .requestMatchers("/", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs").permitAll()
                        .requestMatchers("/swagger-resources/**", "/webjars/**").permitAll()
                        .requestMatchers("/auth/google-login").permitAll()
                        .requestMatchers("/search/**").permitAll()
                        .requestMatchers("/ai-services/**").permitAll()
                        .requestMatchers("/categories/**").permitAll()
                        .requestMatchers("/keywords/**").permitAll()
                        .requestMatchers("/ai-combinations/**").permitAll()

                        // OAuth2 관련 엔드포인트
                        .requestMatchers("/oauth2/authorization/**").permitAll()
                        .requestMatchers("/login/oauth2/code/**").permitAll()

                        // 인증 필요
                        .requestMatchers("/auth/me", "/auth/logout").authenticated()
                        .requestMatchers("/bookmarks/**", "/reviews/**").authenticated()
                        .anyRequest().authenticated()
                )
                // OAuth2 로그인
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuthService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                )
                // H2 콘솔 등 sameOrigin 허용이 필요할 때
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                // JWT 필터
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용 오리진(개발/배포)
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:5173",     // Vite
                "https://*.vercel.app",      // Vercel
                "https://*.railway.app"      // Railway
        ));

        // 허용 메서드/헤더
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"
        ));

        // 클라이언트에 노출할 응답 헤더(필요 시 확장)
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Location"));

        // 자격증명 허용(JWT/쿠키)
        configuration.setAllowCredentials(true);

        // 프리플라이트 캐시
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
