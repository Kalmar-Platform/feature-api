package com.visma.kalmar.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VismaConnectConfigurationImpl implements VismaConnectConfiguration {
    @Value("${connect.public-endpoint}")
    private String publicApiEndpoint;

    @Override
    public String publicApiEndpoint() {
        return publicApiEndpoint;
    }
}
