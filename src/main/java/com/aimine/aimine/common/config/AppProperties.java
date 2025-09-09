package com.aimine.aimine.common.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Frontend frontend = new Frontend();

    @Getter
    @Setter
    public static class Frontend {
        private String url;
    }

    public String getFrontendUrl(HttpServletRequest request) {
        String host = request.getHeader("Host");
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");

        // 임시 디버깅 (System.out은 Railway 콘솔에서 확인 가능)
        System.out.println("=== OAuth 헤더 디버깅 ===");
        System.out.println("Host: " + host);
        System.out.println("Origin: " + origin);
        System.out.println("Referer: " + referer);
        System.out.println("========================");

        // Origin이나 Referer에서 localhost 확인
        boolean isFromLocalhost = false;
        if (origin != null && (origin.contains("localhost") || origin.contains("127.0.0.1"))) {
            isFromLocalhost = true;
        } else if (referer != null && (referer.contains("localhost") || referer.contains("127.0.0.1"))) {
            isFromLocalhost = true;
        }

        if (isFromLocalhost) {
            System.out.println(">>> localhost로 리다이렉트");
            return "http://localhost:3000";
        }

        System.out.println(">>> vercel로 리다이렉트: " + frontend.getUrl());
        return frontend.getUrl();
    }
}