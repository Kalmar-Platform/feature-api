package com.visma.kalmar.api.entities.error;

public enum ErrorCode {
    FIELD_CANNOT_BE_NULL("Field %s cannot be null. Please provide valid input."),
    FIELD_CANNOT_BE_EMPTY("Field %s cannot be empty. Please provide valid input."),
    INVALID_FIELD_MAXIMUM_SIZE("Value for %s must be maximum %s characters long");

    private final String value;

    ErrorCode(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
