package com.visma.kalmar.api.connect.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConnectUserResponse(
        UUID id,
        String email,
        @JsonProperty("email_verified")
        Boolean emailVerified,
        @JsonProperty("email_verified_date")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        LocalDateTime emailVerifiedDate,
        String name,
        @JsonProperty("first_name")
        String firstName,
        @JsonProperty("last_name")
        String lastName,
        @JsonProperty("country_code")
        String countryCode,
        @JsonProperty("preferred_language")
        String preferredLanguage,
        @JsonProperty("created_date")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        LocalDateTime createdDate,
        @JsonProperty("updated_date")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        LocalDateTime updatedDate,
        @JsonProperty("login_count")
        String loginCount,
        @JsonProperty("login_date")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        LocalDateTime loginDate,
        @JsonProperty("last_login_date")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        LocalDateTime lastLoginDate
) {
}
