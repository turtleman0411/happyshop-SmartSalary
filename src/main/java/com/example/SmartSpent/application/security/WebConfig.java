package com.example.SmartSpent.application.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final RememberMeInterceptor rememberMeInterceptor;

    public WebConfig(RememberMeInterceptor rememberMeInterceptor) {
        this.rememberMeInterceptor = rememberMeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(rememberMeInterceptor)
                // ✅ 全站攔截：Page + Command API 都會進 interceptor
                .addPathPatterns("/**")

                // ✅ 不需要登入的頁面/資源：排除以免干擾
                .excludePathPatterns(
                        // Auth pages
                        "/happyshop/login",
                        "/happyshop/register",
                        "/login",
                        "/register",

                        // Error
                        "/error",

                        // Static resources
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/uploads/**",

                        // Common static patterns
                        "/favicon.ico",
                        "/webjars/**"
                );
    }
}
