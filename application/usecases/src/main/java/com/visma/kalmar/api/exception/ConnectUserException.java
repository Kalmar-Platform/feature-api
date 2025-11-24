package com.visma.kalmar.api.exception;

public class ConnectUserException extends RuntimeException {

    private final int statusCode;
    private final String errorType;

    public ConnectUserException(String message, int statusCode, String errorType) {
        super(message);
        this.statusCode = statusCode;
        this.errorType = errorType;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorType() {
        return errorType;
    }
}
