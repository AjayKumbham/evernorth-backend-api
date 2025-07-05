package com.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "application.error-handling")
public class ErrorHandlingProperties {
    
    private boolean showDetails = false;
    private boolean logStackTraces = true;
    private Map<String, String> errorMessages = new HashMap<>();
    
    public ErrorHandlingProperties() {
        // Default error messages
        errorMessages.put("general", "An unexpected error occurred");
        errorMessages.put("validation", "Validation failed");
        errorMessages.put("authentication", "Authentication failed");
        errorMessages.put("authorization", "Access denied");
    }
} 