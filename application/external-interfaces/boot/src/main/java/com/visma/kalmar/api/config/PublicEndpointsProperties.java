package com.visma.kalmar.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security-public-request-paths")
public record PublicEndpointsProperties(String[] get, String[] post) {
}
