package com.example.SmartSpent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.SmartSpent.application.security.RememberMeInterceptor;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String uploadDir;
    private final RememberMeInterceptor rememberMeInterceptor;

    public WebMvcConfig(RememberMeInterceptor rememberMeInterceptor) {
        this.rememberMeInterceptor = rememberMeInterceptor;
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        Path uploadPath = Paths.get(uploadDir);
        String uploadAbsolutePath = uploadPath.toFile().getAbsolutePath();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadAbsolutePath + "/");
    }

      @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(rememberMeInterceptor)
                .addPathPatterns(
                        "/",                // 首頁進來就補 session（才做得到「下次直接看結果」）
                        "/happyshop/**"      // HappyShop 全站
                )
                .excludePathPatterns(
                        // ===== 靜態資源 =====
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/favicon.ico",
                        "/uploads/**",
                        "/webjars/**",
                        "/static/**",

                        // ===== 登入/註冊/登出/錯誤 =====
                        "/happyshop/login",
                        "/happyshop/register",
                        "/happyshop/logout",
                        "/error"
                );
    }
}
