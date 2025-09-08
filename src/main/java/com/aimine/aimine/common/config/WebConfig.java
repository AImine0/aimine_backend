package com.aimine.aimine.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /images/** 경로로 요청이 오면 classpath:/static/images/ 에서 파일을 찾아서 제공
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
    }
}