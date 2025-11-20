package com.visma.kalmar.api.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRequest {

    @Schema(description = "The email address of the user", required = true, example = "john.doe@example.com")
    @NotNull
    @Email
    @JsonProperty("email")
    private String email;

    @Schema(description = "The first name of the user", required = true, example = "John")
    @NotNull
    @Size(max = 50)
    @JsonProperty("firstName")
    private String firstName;

    @Schema(description = "The last name of the user", required = true, example = "Doe")
    @NotNull
    @Size(max = 50)
    @JsonProperty("lastName")
    private String lastName;

    @Schema(description = "The language code for the user", required = true, example = "en")
    @NotNull
    @Size(min = 2, max = 2)
    @JsonProperty("languageCode")
    private String languageCode;
}
