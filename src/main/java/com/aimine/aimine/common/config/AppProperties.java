package com.aimine.aimine.common.config;

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
}