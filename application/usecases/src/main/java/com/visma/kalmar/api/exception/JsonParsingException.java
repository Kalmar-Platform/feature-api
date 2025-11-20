package com.visma.kalmar.api.exception;

public class JsonParsingException extends RuntimeException {
  public JsonParsingException(String message, Throwable cause) {
    super(message,cause);
  }
}
