package com.visma.kalmar.api.user;

import com.visma.kalmar.api.user.dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Generic presenter for user use cases.
 * Handles presentation logic for all user operations with appropriate HTTP status codes.
 */
public class UserPresenter implements UserOutputPort {

    private ResponseEntity<UserResponse> responseEntity;

    @Override
    public void present(UserOutputData outputData) {
        var response = new UserResponse();
        response.setUserId(outputData.userId());
        response.setEmail(outputData.email());
        response.setFirstName(outputData.firstName());
        response.setLastName(outputData.lastName());
        response.setLanguageCode(outputData.languageCode());

        // Choose HTTP status based on operation type
        HttpStatus status = outputData.created() ? HttpStatus.CREATED : HttpStatus.OK;

        responseEntity = ResponseEntity.status(status).body(response);
    }

    public ResponseEntity<UserResponse> getResponse() {
        return responseEntity;
    }
}
