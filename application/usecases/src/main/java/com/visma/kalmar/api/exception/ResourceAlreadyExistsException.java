package com.visma.kalmar.api.exception;

public class ResourceAlreadyExistsException extends CustomRuntimeException {

    private final String resourceType;

    public ResourceAlreadyExistsException(String resourceType, String message) {
        super(message);
        this.resourceType = resourceType;
    }

    public ResourceAlreadyExistsException(String resourceType, String message, Object... arguments) {
        super(message, arguments);
        this.resourceType = resourceType;
    }

    public String getResourceType() {
        return resourceType;
    }
}
