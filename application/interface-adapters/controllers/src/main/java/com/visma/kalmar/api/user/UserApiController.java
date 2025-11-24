package com.visma.kalmar.api.user;

import com.visma.kalmar.api.user.dto.UserRequest;
import com.visma.kalmar.api.user.dto.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for user operations.
 * Implements the UserApi interface and handles HTTP requests.
 */
@RestController
@RequestMapping("/api/v1")
public class UserApiController implements UserApi {

    private final CreateUserInputPort createUserInputPort;
    private final UpdateUserInputPort updateUserInputPort;
    private final DeleteUserInputPort deleteUserInputPort;
    private final GetUserInputPort getUserInputPort;
    private final UserPresenter userPresenter;

    @Autowired
    public UserApiController(CreateUserInputPort createUserInputPort,
                             UpdateUserInputPort updateUserInputPort,
                             DeleteUserInputPort deleteUserInputPort,
                             GetUserInputPort getUserInputPort) {
        this(createUserInputPort, updateUserInputPort, deleteUserInputPort, getUserInputPort, new UserPresenter());
    }

    UserApiController(CreateUserInputPort createUserInputPort,
                      UpdateUserInputPort updateUserInputPort,
                      DeleteUserInputPort deleteUserInputPort,
                      GetUserInputPort getUserInputPort,
                      UserPresenter userPresenter) {
        this.createUserInputPort = createUserInputPort;
        this.updateUserInputPort = updateUserInputPort;
        this.deleteUserInputPort = deleteUserInputPort;
        this.getUserInputPort = getUserInputPort;
        this.userPresenter = userPresenter;
    }

    @Override
    public ResponseEntity<UserResponse> createUser(UserRequest userRequest) {
        var inputData = UserInputData.forCreate(
                userRequest.getEmail(),
                userRequest.getFirstName(),
                userRequest.getLastName(),
                userRequest.getLanguageCode()
        );

        createUserInputPort.createUser(inputData, userPresenter);
        return userPresenter.getResponse();
    }

    @Override
    public ResponseEntity<UserResponse> updateUser(String userId, UserRequest userRequest) {
        var inputData = UserInputData.forUpdate(
                userId,
                userRequest.getEmail(),
                userRequest.getFirstName(),
                userRequest.getLastName(),
                userRequest.getLanguageCode()
        );

        updateUserInputPort.updateUser(inputData, userPresenter);
        return userPresenter.getResponse();
    }

    @Override
    public ResponseEntity<Void> deleteUser(String userId) {
        UUID userUuid = UUID.fromString(userId);
        deleteUserInputPort.deleteUser(userUuid);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<UserResponse> getUserById(String userId) {
        UUID userUuid = UUID.fromString(userId);
        getUserInputPort.getUserById(userUuid, userPresenter);
        return userPresenter.getResponse();
    }

    @Override
    public ResponseEntity<UserResponse> getUserByEmail(String email) {
        getUserInputPort.getUserByEmail(email, userPresenter);
        return userPresenter.getResponse();
    }
}
