package com.visma.kalmar.api;

import com.visma.kalmar.api.exception.InvalidInputDataException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InputDataValidatorTest {
    private static final String TEST_VALUE = "testValue";
    private static final int MAX_STRING_LENGTH = 60;

    @Test
    void requireNonNull_nullObjectName_throwsIllegalArgumentException() {
        var object = new Object();
        Assertions.assertThrows(
                IllegalArgumentException.class, () -> InputDataValidator.requireNonNull(object, null));
    }

    @Test
    void requireNonNull_emptyObjectName_throwsIllegalArgumentException() {
        var object = new Object();
        Assertions.assertThrows(
                IllegalArgumentException.class, () -> InputDataValidator.requireNonNull(object, ""));
    }

    @Test
    void requireNonNull_nullObject_throwsInvalidInputDataException() {
        Assertions.assertThrows(
                InvalidInputDataException.class, () -> InputDataValidator.requireNonNull(null, "test"));
    }

    @Test
    void requireNonNullAndNonEmptyString_ValidInput_NoExceptionThrown() {
        Assertions.assertDoesNotThrow(
                () -> InputDataValidator.requireNonNullAndNonEmptyString(TEST_VALUE, "fieldName"));
    }

    @Test
    void requireNonNullAndNonEmptyString_NullString_ThrowsException() {
        Assertions.assertThrows(
                InvalidInputDataException.class,
                () -> InputDataValidator.requireNonNullAndNonEmptyString(null, "fieldName"));
    }

    @Test
    void requireNonNullAndNonEmptyString_EmptyString_ThrowsException() {
        Assertions.assertThrows(
                InvalidInputDataException.class,
                () -> InputDataValidator.requireNonNullAndNonEmptyString("", "fieldName"));
    }

    @Test
    void validateMaxStringLength_NullValue_ThrowsException() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> InputDataValidator.validateMaxStringLength(null, "fieldName", MAX_STRING_LENGTH));
    }

    @Test
    void validateMaxStringLength_StringIsLongerThanMaximumLength_ThrowsException() {
        Assertions.assertThrows(
                InvalidInputDataException.class,
                () -> InputDataValidator.validateMaxStringLength(TEST_VALUE, "fieldName", 5));
    }

    @Test
    void validateMaxStringLength_ValidInput_NoExceptionThrown() {
        Assertions.assertDoesNotThrow(
                () ->
                        InputDataValidator.validateMaxStringLength(TEST_VALUE, "fieldName", MAX_STRING_LENGTH));
    }
}
