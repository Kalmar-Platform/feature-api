package com.visma.kalmar.api.exception;

/**
 * Exception thrown when an external service call fails.
 * This includes API calls to external systems like payment processors, identity providers, etc.
 */
public class ExternalServiceException extends RuntimeException {

    private final String serviceName;

    public ExternalServiceException(String message) {
        super(message);
        this.serviceName = "Unknown";
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
        this.serviceName = "Unknown";
    }

    public ExternalServiceException(String serviceName, String message) {
        super(message);
        this.serviceName = serviceName;
    }

    public ExternalServiceException(String serviceName, String message, Throwable cause) {
        super(message, cause);
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }
}
