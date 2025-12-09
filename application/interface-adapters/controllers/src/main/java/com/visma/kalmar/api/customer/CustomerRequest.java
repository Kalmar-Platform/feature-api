package com.visma.kalmar.api.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Request to create a new customer")
public record CustomerRequest(
        @Schema(description = "Customer ID", example = "123e4567-e89b-12d3-a456-426614174000", required = false)
        @JsonProperty("idContext")
        UUID idContext,

        @Schema(description = "Country code (ISO 2-letter code). Optional - if not provided, uses parent context's country", example = "NO", required = false)
        @JsonProperty("countryCode")
        String countryCode,

        @Schema(description = "Parent context ID", example = "323e4567-e89b-12d3-a456-426614174000", required = false)
        @JsonProperty("idContextParent")
        UUID idContextParent,

        @Schema(description = "Organization number", example = "123456789", required = true)
        @JsonProperty("organizationNumber")
        String organizationNumber,

        @Schema(description = "Customer name", example = "Acme Corporation", required = true)
        @JsonProperty("name")
        String name
) {
}
