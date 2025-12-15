package com.visma.kalmar.api.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Request to create a new company")
public record CompanyRequest(
        @Schema(description = "Company ID", example = "123e4567-e89b-12d3-a456-426614174000", required = false)
        @JsonProperty("idContext")
        UUID idContext,

        @Schema(description = "Country code (ISO 2-letter code). Optional - if not provided, uses parent customer's country", example = "NO", required = false)
        @JsonProperty("countryCode")
        String countryCode,

        @Schema(description = "Parent customer ID (mandatory)", example = "323e4567-e89b-12d3-a456-426614174000", required = true)
        @JsonProperty("idContextParent")
        UUID idContextParent,

        @Schema(description = "Organization number", example = "987654321", required = true)
        @JsonProperty("organizationNumber")
        String organizationNumber,

        @Schema(description = "Company name", example = "Subsidiary Corp", required = true)
        @JsonProperty("name")
        String name
) {
}
