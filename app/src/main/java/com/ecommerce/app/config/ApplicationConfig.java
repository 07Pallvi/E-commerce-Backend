package com.ecommerce.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.Getter;

@Configuration
@Getter
public class ApplicationConfig {

    @Value("${this.server.url}")
    private String serverUrl;

    @Value("${ecommerce.security.key}")
    private String ecommerceSecurityKey;

    @Value("${ecommerce.security.value}")
    private String ecommerceSecurityValue;
    
    
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
