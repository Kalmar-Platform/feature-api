package com.visma.kalmar.api.exception;

import java.util.ArrayList;
import java.util.List;

public class CustomRuntimeException extends RuntimeException {

    private final List<String> messageParameters;

    public CustomRuntimeException(String message) {
        super(message);
        this.messageParameters = new ArrayList<>();
    }

    public CustomRuntimeException(String message, Object... messageArguments) {
        super(message);
        this.messageParameters = new ArrayList<>();
        for (Object item : messageArguments) {
            if (item == null) {
                messageParameters.add(null);
            } else {
                messageParameters.add(item.toString());
            }
        }
    }

    public List<String> getMessageParameters() {
        return messageParameters;
    }
}
