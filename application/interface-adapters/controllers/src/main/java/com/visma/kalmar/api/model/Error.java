package com.visma.kalmar.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Error {

    @Schema
    @NotNull
    @JsonProperty("code")
    private String code;

    @Schema
    @NotNull
    @JsonProperty("message")
    private String message;

    @Schema
    @NotNull
    @JsonProperty("messageParameters")
    private List<String> messageParameters;
}
