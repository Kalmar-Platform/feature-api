package com.visma.kalmar.api.user;

import com.visma.kalmar.api.entities.user.User;
import com.visma.kalmar.api.exception.ConnectUserException;
import com.visma.kalmar.api.exception.ResourceNotFoundException;
import com.visma.kalmar.api.vismaconnect.InMemoryVismaConnectUserGatewayAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DeleteUserUseCaseTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LANGUAGE_ID = UUID.randomUUID();
    private static final String EMAIL = "test@example.com";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";

    private InMemoryUserGatewayAdapter userGateway;
    private InMemoryVismaConnectUserGatewayAdapter vismaConnectUserGateway;
    private DeleteUserUseCase useCase;

    @BeforeEach
    void setUp() {
        userGateway = new InMemoryUserGatewayAdapter();
        vismaConnectUserGateway = new InMemoryVismaConnectUserGatewayAdapter();
        useCase = new DeleteUserUseCase(userGateway, vismaConnectUserGateway);

        User existingUser = new User(USER_ID, LANGUAGE_ID, EMAIL, FIRST_NAME, LAST_NAME, 1L, new Date());
        userGateway.save(existingUser);
        vismaConnectUserGateway.createUser(existingUser, "en");
    }

    @Test
    void deleteUser_withValidUserId_deletesUserSuccessfully() {
        assertTrue(userGateway.existsById(USER_ID));

        useCase.deleteUser(USER_ID);

        assertFalse(userGateway.existsById(USER_ID));
        assertTrue(vismaConnectUserGateway.isUserUnlinked(USER_ID));
    }

    @Test
    void deleteUser_withNonExistentUserId_throwsResourceNotFoundException() {
        UUID nonExistentUserId = UUID.randomUUID();

        assertThrows(ResourceNotFoundException.class,
                () -> useCase.deleteUser(nonExistentUserId));
    }

    @Test
    void deleteUser_whenUserAlreadyUnlinkedFromConnect_continuesWithDeletion() {
        vismaConnectUserGateway.setShouldReturnAlreadyUnlinked(true);

        assertTrue(userGateway.existsById(USER_ID));

        assertDoesNotThrow(() -> useCase.deleteUser(USER_ID));

        assertFalse(userGateway.existsById(USER_ID));
    }

    @Test
    void deleteUser_whenVismaConnectReturns409WithDifferentMessage_throwsConnectUserException() {
        vismaConnectUserGateway.setShouldFailOnUnlink(true);

        assertThrows(ConnectUserException.class,
                () -> useCase.deleteUser(USER_ID));

        assertTrue(userGateway.existsById(USER_ID));
    }

    @Test
    void deleteUser_whenVismaConnectFailsWithOtherError_throwsConnectUserException() {
        vismaConnectUserGateway.setShouldFailOnUnlink(true);

        assertThrows(ConnectUserException.class,
                () -> useCase.deleteUser(USER_ID));

        assertTrue(userGateway.existsById(USER_ID));
    }

    @Test
    void deleteUser_doesNotDeleteFromDatabaseWhenConnectFails() {
        vismaConnectUserGateway.setShouldFailOnUnlink(true);

        try {
            useCase.deleteUser(USER_ID);
            fail("Expected ConnectUserException to be thrown");
        } catch (ConnectUserException e) {
        }

        assertTrue(userGateway.existsById(USER_ID));
        var stillExistingUser = userGateway.findById(USER_ID);
        assertEquals(EMAIL, stillExistingUser.email());
        assertEquals(FIRST_NAME, stillExistingUser.firstName());
        assertEquals(LAST_NAME, stillExistingUser.lastName());
    }

    @Test
    void deleteUser_removesUserFromGateway() {
        assertTrue(userGateway.existsById(USER_ID));
        assertTrue(userGateway.existsByEmail(EMAIL));

        useCase.deleteUser(USER_ID);

        assertFalse(userGateway.existsById(USER_ID));
        assertFalse(userGateway.existsByEmail(EMAIL));
    }

    @Test
    void deleteUser_throwsExceptionWhenTryingToAccessDeletedUser() {
        useCase.deleteUser(USER_ID);

        assertThrows(ResourceNotFoundException.class,
                () -> userGateway.findById(USER_ID));
    }
}
