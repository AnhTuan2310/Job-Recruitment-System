package com.example.jobrecruitmentsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Cho phép truy cập các file trong thư mục /home/kali/uploads/
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/home/kali/uploads/");
    }
}

