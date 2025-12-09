package com.visma.kalmar.api.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Customer response")
public record CustomerResponse(
        @Schema(description = "Context ID", example = "123e4567-e89b-12d3-a456-426614174000")
        @JsonProperty("idContext")
        UUID idContext,

        @Schema(description = "Customer name", example = "Acme Corporation")
        @JsonProperty("name")
        String name,

        @Schema(description = "Organization number", example = "123456789")
        @JsonProperty("organizationNumber")
        String organizationNumber,

        @Schema(description = "Country code (ISO 2-letter code)", example = "NO")
        @JsonProperty("countryCode")
        String countryCode,

        @Schema(description = "Parent context ID", example = "323e4567-e89b-12d3-a456-426614174000")
        @JsonProperty("idContextParent")
        UUID idContextParent
) {
}
