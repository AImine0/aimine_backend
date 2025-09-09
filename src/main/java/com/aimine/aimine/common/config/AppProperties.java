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


        // Origin이나 Referer에서 localhost 확인
        boolean isFromLocalhost = false;
        if (origin != null && (origin.contains("localhost") || origin.contains("127.0.0.1"))) {
            isFromLocalhost = true;
        } else if (referer != null && (referer.contains("localhost") || referer.contains("127.0.0.1"))) {
            isFromLocalhost = true;
        }

        if (isFromLocalhost) {
            return "http://localhost:3000";
        }

        return frontend.getUrl();
    }
}