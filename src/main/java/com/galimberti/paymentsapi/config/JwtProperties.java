package com.galimberti.paymentsapi.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;

@Validated                                   // (3)
@ConfigurationProperties(prefix = "jwt")     // (2)
public record JwtProperties(                 // (1)
        @NotBlank String secret,             // (3)
        long validityMs) {}
