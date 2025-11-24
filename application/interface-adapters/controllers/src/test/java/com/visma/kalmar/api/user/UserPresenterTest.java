package com.visma.kalmar.api.user;

import com.visma.kalmar.api.user.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserPresenterTest {

    private static final String USER_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final String EMAIL = "test@example.com";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String LANGUAGE_CODE = "en";

    private UserPresenter userPresenter;

    @BeforeEach
    void setUp() {
        userPresenter = new UserPresenter();
    }

    @Test
    void present_withCreatedFalse_returnsHttpOk() {
        UserOutputData outputData = new UserOutputData(
                USER_ID,
                EMAIL,
                FIRST_NAME,
                LAST_NAME,
                LANGUAGE_CODE,
                false
        );

        userPresenter.present(outputData);
        ResponseEntity<UserResponse> response = userPresenter.getResponse();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(USER_ID, response.getBody().getUserId());
        assertEquals(EMAIL, response.getBody().getEmail());
        assertEquals(FIRST_NAME, response.getBody().getFirstName());
        assertEquals(LAST_NAME, response.getBody().getLastName());
        assertEquals(LANGUAGE_CODE, response.getBody().getLanguageCode());
    }

    @Test
    void present_withCreatedTrue_returnsHttpCreated() {
        UserOutputData outputData = new UserOutputData(
                USER_ID,
                EMAIL,
                FIRST_NAME,
                LAST_NAME,
                LANGUAGE_CODE,
                true
        );

        userPresenter.present(outputData);
        ResponseEntity<UserResponse> response = userPresenter.getResponse();

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(USER_ID, response.getBody().getUserId());
        assertEquals(EMAIL, response.getBody().getEmail());
        assertEquals(FIRST_NAME, response.getBody().getFirstName());
        assertEquals(LAST_NAME, response.getBody().getLastName());
        assertEquals(LANGUAGE_CODE, response.getBody().getLanguageCode());
    }

    @Test
    void present_mapsAllFieldsCorrectly() {
        UserOutputData outputData = new UserOutputData(
                USER_ID,
                EMAIL,
                FIRST_NAME,
                LAST_NAME,
                LANGUAGE_CODE,
                false
        );

        userPresenter.present(outputData);
        ResponseEntity<UserResponse> response = userPresenter.getResponse();

        UserResponse userResponse = response.getBody();
        assertNotNull(userResponse);
        assertEquals(outputData.userId(), userResponse.getUserId());
        assertEquals(outputData.email(), userResponse.getEmail());
        assertEquals(outputData.firstName(), userResponse.getFirstName());
        assertEquals(outputData.lastName(), userResponse.getLastName());
        assertEquals(outputData.languageCode(), userResponse.getLanguageCode());
    }
}
