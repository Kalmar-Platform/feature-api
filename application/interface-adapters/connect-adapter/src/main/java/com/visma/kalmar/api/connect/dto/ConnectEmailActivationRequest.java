package com.visma.kalmar.api.connect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ConnectEmailActivationRequest(
        @JsonProperty("application_name")
        String applicationName,
        @JsonProperty("redirect_uri")
        String redirectUri,
        @JsonProperty("state")
        String state,
        @JsonProperty("email_recipient")
        String emailRecipient
) {
}
