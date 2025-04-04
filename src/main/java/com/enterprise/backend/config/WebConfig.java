package com.enterprise.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final SecretKeyInterceptor secretKeyInterceptor; // Inject Interceptor

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Đăng ký Interceptor cho các URL mà bạn muốn kiểm tra secret key
        registry.addInterceptor(secretKeyInterceptor)
                .addPathPatterns("/internal/**")
                .addPathPatterns("/file/upload");  // Áp dụng cho tất cả các endpoint có prefix "/api"
    }
}