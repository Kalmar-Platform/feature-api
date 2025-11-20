package com.visma.kalmar.api.exception;

public class ResourceNotFoundException extends CustomRuntimeException {
    private final String resourceType;

    public ResourceNotFoundException(String resourceType, String message) {
        super(message);
        this.resourceType = resourceType;
    }

    public ResourceNotFoundException(String resourceType, String message, Object... arguments) {
        super(message, arguments);
        this.resourceType = resourceType;
    }

    public String getResourceType() {
        return resourceType;
    }
}
