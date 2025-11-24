package com.visma.kalmar.api;

import com.visma.kalmar.api.entities.error.ErrorCode;
import com.visma.kalmar.api.exception.InvalidInputDataException;

public class InputDataValidator {

    private InputDataValidator() {
    }

    /**
     * Method used to check if an input data field is null.
     *
     * @param object     field that is checked for null
     * @param objectName name of the field
     * @throws IllegalArgumentException  when a parameter is null
     * @throws InvalidInputDataException when the object is null
     */
    public static void requireNonNull(Object object, String objectName) {
        checkFieldName(objectName);

        if (object == null) {
            throw new InvalidInputDataException(
                    ErrorCode.FIELD_CANNOT_BE_NULL.toString(),
                    String.format(ErrorCode.FIELD_CANNOT_BE_NULL.getValue(), objectName),
                    objectName);
        }
    }

    /**
     * Method that checks if a string is not null and not empty
     *
     * @param value      the value to check
     * @param objectName name of the field
     * @throws IllegalArgumentException when the value is null
     * @throws IllegalArgumentException when the value is empty
     */
    public static void requireNonNullAndNonEmptyString(String value, String objectName) {
        checkFieldName(objectName);
        if (value == null) {
            throw new InvalidInputDataException(
                    ErrorCode.FIELD_CANNOT_BE_NULL.toString(),
                    String.format(ErrorCode.FIELD_CANNOT_BE_NULL.getValue(), objectName),
                    objectName);
        }

        if (value.isEmpty()) {
            throw new InvalidInputDataException(
                    ErrorCode.FIELD_CANNOT_BE_EMPTY.toString(),
                    String.format(ErrorCode.FIELD_CANNOT_BE_EMPTY.getValue(), objectName),
                    objectName);
        }
    }

    public static void validateMaxStringLength(String value, String objectName, int maxLength) {
        checkFieldName(objectName);

        if (value == null) {
            throw new IllegalArgumentException("Value is mandatory");
        }

        if (value.length() > maxLength) {
            throw new InvalidInputDataException(
                    ErrorCode.INVALID_FIELD_MAXIMUM_SIZE.toString(),
                    String.format(ErrorCode.INVALID_FIELD_MAXIMUM_SIZE.getValue(), objectName, maxLength),
                    objectName,
                    maxLength);
        }
    }

    private static void checkFieldName(String objectName) {
        if (objectName == null || objectName.isEmpty()) {
            throw new IllegalArgumentException("Field name is mandatory.");
        }
    }
}
