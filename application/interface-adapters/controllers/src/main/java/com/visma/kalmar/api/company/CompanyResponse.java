package com.visma.kalmar.api.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Company response")
public record CompanyResponse(
        @Schema(description = "Context ID", example = "123e4567-e89b-12d3-a456-426614174000")
        @JsonProperty("idContext")
        UUID idContext,

        @Schema(description = "Company name", example = "Subsidiary Corp")
        @JsonProperty("name")
        String name,

        @Schema(description = "Organization number", example = "987654321")
        @JsonProperty("organizationNumber")
        String organizationNumber,

        @Schema(description = "Country code (ISO 2-letter code)", example = "NO")
        @JsonProperty("countryCode")
        String countryCode,

        @Schema(description = "Parent customer ID", example = "323e4567-e89b-12d3-a456-426614174000")
        @JsonProperty("idContextParent")
        UUID idContextParent
) {
}
