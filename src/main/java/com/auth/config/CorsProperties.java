package com.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "application.cors")
public class CorsProperties {
    
    private String allowedOrigins = "http://localhost:3000,http://localhost:8080";
    private String allowedMethods = "GET,POST,PUT,DELETE,OPTIONS";
    private String allowedHeaders = "*";
    private boolean allowCredentials = true;
    private long maxAge = 3600L;
    
    public List<String> getAllowedOriginsList() {
        return Arrays.asList(allowedOrigins.split(","));
    }
    
    public List<String> getAllowedMethodsList() {
        return Arrays.asList(allowedMethods.split(","));
    }
    
    public List<String> getAllowedHeadersList() {
        return Arrays.asList(allowedHeaders.split(","));
    }
} 