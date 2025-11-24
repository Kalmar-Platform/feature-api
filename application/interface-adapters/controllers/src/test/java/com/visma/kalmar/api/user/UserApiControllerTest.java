package com.visma.kalmar.api.user;

import com.visma.kalmar.api.user.dto.UserRequest;
import com.visma.kalmar.api.user.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserApiControllerTest {

    private static final String USER_ID_STRING = "123e4567-e89b-12d3-a456-426614174000";
    private static final UUID USER_ID = UUID.fromString(USER_ID_STRING);
    private static final String EMAIL = "test@example.com";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String LANGUAGE_CODE = "en";

    @Mock
    private CreateUserInputPort createUserInputPort;

    @Mock
    private UpdateUserInputPort updateUserInputPort;

    @Mock
    private DeleteUserInputPort deleteUserInputPort;

    @Mock
    private GetUserInputPort getUserInputPort;

    @Mock
    private UserPresenter userPresenter;

    private UserApiController userApiController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userApiController = new UserApiController(
                createUserInputPort,
                updateUserInputPort,
                deleteUserInputPort,
                getUserInputPort,
                userPresenter
        );
    }

    @Test
    void createUser_success() {
        UserRequest userRequest = createUserRequest();
        UserResponse expectedResponse = createUserResponse();
        ResponseEntity<UserResponse> expectedEntity = ResponseEntity.status(HttpStatus.CREATED).body(expectedResponse);

        when(userPresenter.getResponse()).thenReturn(expectedEntity);

        ResponseEntity<UserResponse> response = userApiController.createUser(userRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());

        ArgumentCaptor<UserInputData> inputDataCaptor = ArgumentCaptor.forClass(UserInputData.class);
        verify(createUserInputPort, times(1)).createUser(inputDataCaptor.capture(), eq(userPresenter));

        UserInputData capturedInputData = inputDataCaptor.getValue();
        assertEquals(EMAIL, capturedInputData.email());
        assertEquals(FIRST_NAME, capturedInputData.firstName());
        assertEquals(LAST_NAME, capturedInputData.lastName());
        assertEquals(LANGUAGE_CODE, capturedInputData.languageCode());
    }

    @Test
    void updateUser_success() {
        UserRequest userRequest = createUserRequest();
        UserResponse expectedResponse = createUserResponse();
        ResponseEntity<UserResponse> expectedEntity = ResponseEntity.ok(expectedResponse);

        when(userPresenter.getResponse()).thenReturn(expectedEntity);

        ResponseEntity<UserResponse> response = userApiController.updateUser(USER_ID_STRING, userRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());

        ArgumentCaptor<UserInputData> inputDataCaptor = ArgumentCaptor.forClass(UserInputData.class);
        verify(updateUserInputPort, times(1)).updateUser(inputDataCaptor.capture(), eq(userPresenter));

        UserInputData capturedInputData = inputDataCaptor.getValue();
        assertEquals(USER_ID_STRING, capturedInputData.userId());
        assertEquals(EMAIL, capturedInputData.email());
        assertEquals(FIRST_NAME, capturedInputData.firstName());
        assertEquals(LAST_NAME, capturedInputData.lastName());
        assertEquals(LANGUAGE_CODE, capturedInputData.languageCode());
    }

    @Test
    void deleteUser_success() {
        ResponseEntity<Void> response = userApiController.deleteUser(USER_ID_STRING);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(deleteUserInputPort, times(1)).deleteUser(eq(USER_ID));
    }

    @Test
    void getUserById_success() {
        UserResponse expectedResponse = createUserResponse();
        ResponseEntity<UserResponse> expectedEntity = ResponseEntity.ok(expectedResponse);

        when(userPresenter.getResponse()).thenReturn(expectedEntity);

        ResponseEntity<UserResponse> response = userApiController.getUserById(USER_ID_STRING);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());

        verify(getUserInputPort, times(1)).getUserById(eq(USER_ID), eq(userPresenter));
    }

    @Test
    void getUserByEmail_success() {
        UserResponse expectedResponse = createUserResponse();
        ResponseEntity<UserResponse> expectedEntity = ResponseEntity.ok(expectedResponse);

        when(userPresenter.getResponse()).thenReturn(expectedEntity);

        ResponseEntity<UserResponse> response = userApiController.getUserByEmail(EMAIL);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());

        verify(getUserInputPort, times(1)).getUserByEmail(eq(EMAIL), eq(userPresenter));
    }

    @Test
    void createUser_callsPresenterToGetResponse() {
        UserRequest userRequest = createUserRequest();
        ResponseEntity<UserResponse> expectedEntity = ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse());

        when(userPresenter.getResponse()).thenReturn(expectedEntity);

        userApiController.createUser(userRequest);

        verify(userPresenter, times(1)).getResponse();
    }

    @Test
    void updateUser_callsPresenterToGetResponse() {
        UserRequest userRequest = createUserRequest();
        ResponseEntity<UserResponse> expectedEntity = ResponseEntity.ok(new UserResponse());

        when(userPresenter.getResponse()).thenReturn(expectedEntity);

        userApiController.updateUser(USER_ID_STRING, userRequest);

        verify(userPresenter, times(1)).getResponse();
    }

    @Test
    void getUserById_callsPresenterToGetResponse() {
        ResponseEntity<UserResponse> expectedEntity = ResponseEntity.ok(new UserResponse());

        when(userPresenter.getResponse()).thenReturn(expectedEntity);

        userApiController.getUserById(USER_ID_STRING);

        verify(userPresenter, times(1)).getResponse();
    }

    @Test
    void getUserByEmail_callsPresenterToGetResponse() {
        ResponseEntity<UserResponse> expectedEntity = ResponseEntity.ok(new UserResponse());

        when(userPresenter.getResponse()).thenReturn(expectedEntity);

        userApiController.getUserByEmail(EMAIL);

        verify(userPresenter, times(1)).getResponse();
    }

    private UserRequest createUserRequest() {
        UserRequest request = new UserRequest();
        request.setEmail(EMAIL);
        request.setFirstName(FIRST_NAME);
        request.setLastName(LAST_NAME);
        request.setLanguageCode(LANGUAGE_CODE);
        return request;
    }

    private UserResponse createUserResponse() {
        UserResponse response = new UserResponse();
        response.setUserId(USER_ID_STRING);
        response.setEmail(EMAIL);
        response.setFirstName(FIRST_NAME);
        response.setLastName(LAST_NAME);
        response.setLanguageCode(LANGUAGE_CODE);
        return response;
    }
}
