package com.aimine.aimine.security.jwt;

import com.aimine.aimine.user.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    // SecurityConfig의 permitAll 목록과 반드시 일치시켜 주세요.
    private static final String[] WHITELIST = {
            "/", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs",
            "/swagger-resources/**", "/webjars/**",
            "/auth/google-login",
            "/search/**", "/ai-services/**", "/categories/**", "/keywords/**", "/ai-combinations/**",
            "/oauth2/authorization/**", "/login/oauth2/code/**"
    };
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /** CORS Preflight(OPTIONS) 및 화이트리스트 경로는 필터 스킵 */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) return true; // CORS preflight
        String path = request.getRequestURI();
        for (String pattern : WHITELIST) {
            if (PATH_MATCHER.match(pattern, path)) return true;
        }
        return false;
    }

    /** JWT 처리: 토큰 없거나/이상해도 예외 던지지 말고 그냥 통과(500 방지) */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String jwt = getJwtFromRequest(request);

            // 토큰 없으면 인증 시도 없이 통과
            if (!StringUtils.hasText(jwt)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 토큰 형식/유효성 문제여도 500 내지 않음
            if (jwtTokenProvider.validateToken(jwt)) {
                Long userId = jwtTokenProvider.getUserIdFromToken(jwt);

                // 사용자 정보 조회 (예외 발생 시 catch 블록에서 처리)
                var user = userService.findById(userId);

                // 이미 인증이 없을 때만 설정 (다른 필터/인증 흐름과 충돌 방지)
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    var authentication = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            Collections.emptyList() // 필요하면 권한 매핑
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("JWT 인증 성공: userId={}, email={}", user.getId(), user.getEmail());
                }
            } else {
                log.debug("JWT 유효성 실패 → 인증 미설정 후 통과");
            }
        } catch (Exception e) {
            // 여기서 500 만들지 않기: 컨트롤러까지 흘려보내고 보호 리소스는 최종적으로 401/403
            log.error("JWT 토큰 처리 중 오류 발생: {}", e.toString(), e);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /** Authorization 헤더에서 Bearer 토큰 추출 */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
