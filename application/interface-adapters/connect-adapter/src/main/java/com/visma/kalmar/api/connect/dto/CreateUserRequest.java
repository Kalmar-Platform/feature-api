package com.visma.kalmar.api.connect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateUserRequest(
        String email,
        @JsonProperty("country_code")
        String countryCode,
        @JsonProperty("preferred_language")
        String preferredLanguage,
        @JsonProperty("first_name")
        String firstName,
        @JsonProperty("last_name")
        String lastName
) {
}
