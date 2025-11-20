package com.visma.kalmar.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "aws.s3")
public record AWSS3Properties(@DefaultValue("eu-west-1") String region) {}
