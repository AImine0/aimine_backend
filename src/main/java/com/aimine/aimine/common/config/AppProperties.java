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
        if (host != null && (host.contains("localhost") || host.contains("127.0.0.1"))) {
            return "http://localhost:3000";
        }
        return frontend.getUrl();
    }
}