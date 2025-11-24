package com.visma.kalmar.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.security.oauth2.resourceserver.jwt")
public record ResourceServer(String issuer, String audience, String vismaConnectJoseType) {
}
