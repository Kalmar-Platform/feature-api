package com.visma.kalmar.api.connect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ConnectErrorResponse(
        @JsonProperty("error_code")
        String errorCode
) {
}
