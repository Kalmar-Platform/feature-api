package com.visma.kalmar.api.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Request to create a new customer")
public record CustomerRequest(
        @Schema(description = "Customer ID", example = "123e4567-e89b-12d3-a456-426614174000", required = false)
        @JsonProperty("idCustomer")
        UUID idCustomer,

        @Schema(description = "Country ID", example = "223e4567-e89b-12d3-a456-426614174000", required = true)
        @JsonProperty("idCountry")
        UUID idCountry,

        @Schema(description = "Parent context ID (optional)", example = "323e4567-e89b-12d3-a456-426614174000")
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
