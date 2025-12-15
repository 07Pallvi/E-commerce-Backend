package com.ecommerce.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class ApplicationConfig {

    @Value("${this.server.url}")
    private String serverUrl;
        
}
