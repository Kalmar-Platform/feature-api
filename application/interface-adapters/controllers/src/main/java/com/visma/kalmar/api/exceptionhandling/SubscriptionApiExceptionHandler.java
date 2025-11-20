package com.visma.kalmar.api.exceptionhandling;

import com.visma.kalmar.api.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.visma.kalmar.api.model.Error;

@RestControllerAdvice
public class SubscriptionApiExceptionHandler extends ResponseEntityExceptionHandler {
  private final Logger log = LoggerFactory.getLogger(SubscriptionApiExceptionHandler.class);

  @ExceptionHandler(ResourceAlreadyExistsException.class)
  public ResponseEntity<Error> handleResourceAlreadyExistsException(
      ResourceAlreadyExistsException exception) {
    log.warn("{} already exists", exception.getResourceType(), exception);

    var error = new Error();
    error.setCode(exception.getResourceType());
    error.setMessage(exception.getMessage());
    error.setMessageParameters(exception.getMessageParameters());
    return new ResponseEntity<>(error, HttpStatus.CONFLICT);
  }

  @ExceptionHandler
  public ResponseEntity<Error> handleResourceNotFoundException(
      ResourceNotFoundException exception) {
    log.warn("{} not found", exception.getResourceType(), exception);
    var error = new Error();
    error.setCode(exception.getResourceType());
    error.setMessage(exception.getLocalizedMessage());
    error.setMessageParameters(exception.getMessageParameters());
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ConnectUserException.class)
  public ResponseEntity<Error> handleVismaConnectUserException(ConnectUserException exception) {
    log.error("Error in Visma Connect User Gateway Adapter", exception);
    var error = new Error();
    var errorType = exception.getErrorType() == "CREATE_USER" ? "creating" : "updating";
    error.setCode(exception.getMessage());
    error.setMessage("Error " + errorType + " user in Visma Connect");

    return new ResponseEntity<>(error, HttpStatus.valueOf(exception.getStatusCode()));
  }

  @ExceptionHandler(JsonParsingException.class)
  public ResponseEntity<Error> jsonParsingException(JsonParsingException exception) {
    log.error("Error parsing JSON for open web registration", exception);
    var error = new Error();
    error.setCode(exception.getMessage());
    error.setMessage("Error parsing JSON for open web registration");
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(ConnectTenantException.class)
  public ResponseEntity<Error> handleVismaConnectTenantException(ConnectTenantException exception) {
    log.error("Error in Visma Connect Tenant Gateway Adapter", exception);
    var error = new Error();
    var errorType = exception.getErrorType() == "CREATE_TENANT" ? "creating" : "updating";
    error.setCode(exception.getMessage());
    error.setMessage("Error " + errorType + " user in Visma Connect");

    return new ResponseEntity<>(error, HttpStatus.valueOf(exception.getStatusCode()));
  }
}
