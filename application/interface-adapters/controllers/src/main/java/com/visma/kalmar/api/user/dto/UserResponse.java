package com.visma.kalmar.api.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    @Schema(description = "The unique identifier of the user", required = true)
    @JsonProperty("userId")
    private String userId;

    @Schema(description = "The email address of the user", required = true)
    @JsonProperty("email")
    private String email;

    @Schema(description = "The first name of the user", required = true)
    @JsonProperty("firstName")
    private String firstName;

    @Schema(description = "The last name of the user", required = true)
    @JsonProperty("lastName")
    private String lastName;

    @Schema(description = "The language code for the user", required = true)
    @JsonProperty("languageCode")
    private String languageCode;
}
