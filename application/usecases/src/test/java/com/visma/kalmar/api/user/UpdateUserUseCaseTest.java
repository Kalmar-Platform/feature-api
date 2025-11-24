package com.visma.kalmar.api.user;

import com.visma.kalmar.api.entities.language.Language;
import com.visma.kalmar.api.entities.user.User;
import com.visma.kalmar.api.exception.ConnectUserException;
import com.visma.kalmar.api.exception.ResourceNotFoundException;
import com.visma.kalmar.api.language.InMemoryLanguageGatewayAdapter;
import com.visma.kalmar.api.vismaconnect.InMemoryVismaConnectUserGatewayAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class UpdateUserUseCaseTest {

    private static final String EMAIL = "test@example.com";
    private static final String UPDATED_EMAIL = "updated@example.com";
    private static final String FIRST_NAME = "John";
    private static final String UPDATED_FIRST_NAME = "Jane";
    private static final String LAST_NAME = "Doe";
    private static final String UPDATED_LAST_NAME = "Smith";
    private static final String LANGUAGE_CODE = "en";
    private static final String UPDATED_LANGUAGE_CODE = "no";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LANGUAGE_ID = UUID.randomUUID();
    private static final UUID UPDATED_LANGUAGE_ID = UUID.randomUUID();

    private InMemoryUserGatewayAdapter userGateway;
    private InMemoryLanguageGatewayAdapter languageGateway;
    private InMemoryVismaConnectUserGatewayAdapter vismaConnectUserGateway;
    private UpdateUserUseCase useCase;

    @BeforeEach
    void setUp() {
        userGateway = new InMemoryUserGatewayAdapter();
        languageGateway = new InMemoryLanguageGatewayAdapter();
        vismaConnectUserGateway = new InMemoryVismaConnectUserGatewayAdapter();
        useCase = new UpdateUserUseCase(userGateway, languageGateway, vismaConnectUserGateway);

        Language englishLanguage = new Language(LANGUAGE_ID, "English", LANGUAGE_CODE);
        Language norwegianLanguage = new Language(UPDATED_LANGUAGE_ID, "Norwegian", UPDATED_LANGUAGE_CODE);
        languageGateway.save(englishLanguage);
        languageGateway.save(norwegianLanguage);

        User existingUser = new User(USER_ID, LANGUAGE_ID, EMAIL, FIRST_NAME, LAST_NAME, 1L, new Date());
        userGateway.save(existingUser);
        vismaConnectUserGateway.createUser(existingUser, LANGUAGE_CODE);
    }

    @Test
    void updateUser_withValidData_updatesUserSuccessfully() {
        var inputData = UserInputData.forUpdate(
                USER_ID.toString(),
                UPDATED_EMAIL,
                UPDATED_FIRST_NAME,
                UPDATED_LAST_NAME,
                UPDATED_LANGUAGE_CODE
        );
        final AtomicReference<UserOutputData> result = new AtomicReference<>();

        useCase.updateUser(inputData, output -> result.set(output));

        assertNotNull(result.get());
        assertEquals(USER_ID.toString(), result.get().userId());
        assertEquals(UPDATED_EMAIL, result.get().email());
        assertEquals(UPDATED_FIRST_NAME, result.get().firstName());
        assertEquals(UPDATED_LAST_NAME, result.get().lastName());
        assertEquals(UPDATED_LANGUAGE_CODE, result.get().languageCode());
        assertFalse(result.get().created());

        var updatedUser = userGateway.findById(USER_ID);
        assertEquals(UPDATED_EMAIL, updatedUser.email());
        assertEquals(UPDATED_FIRST_NAME, updatedUser.firstName());
        assertEquals(UPDATED_LAST_NAME, updatedUser.lastName());
        assertEquals(UPDATED_LANGUAGE_ID, updatedUser.idLanguage());
    }

    @Test
    void updateUser_withNonExistentUserId_throwsResourceNotFoundException() {
        UUID nonExistentUserId = UUID.randomUUID();
        var inputData = UserInputData.forUpdate(
                nonExistentUserId.toString(),
                UPDATED_EMAIL,
                UPDATED_FIRST_NAME,
                UPDATED_LAST_NAME,
                LANGUAGE_CODE
        );

        assertThrows(ResourceNotFoundException.class,
                () -> useCase.updateUser(inputData, output -> {
                }));
    }

    @Test
    void updateUser_withNullUserId_throwsIllegalArgumentException() {
        var inputData = UserInputData.forUpdate(
                null,
                UPDATED_EMAIL,
                UPDATED_FIRST_NAME,
                UPDATED_LAST_NAME,
                LANGUAGE_CODE
        );

        var exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.updateUser(inputData, output -> {
                }));

        assertEquals("User ID is required for update operation", exception.getMessage());
    }

    @Test
    void updateUser_withEmptyUserId_throwsIllegalArgumentException() {
        var inputData = UserInputData.forUpdate(
                "",
                UPDATED_EMAIL,
                UPDATED_FIRST_NAME,
                UPDATED_LAST_NAME,
                LANGUAGE_CODE
        );

        var exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.updateUser(inputData, output -> {
                }));

        assertEquals("User ID is required for update operation", exception.getMessage());
    }

    @Test
    void updateUser_withNonExistentLanguageCode_throwsResourceNotFoundException() {
        String invalidLanguageCode = "xx";
        var inputData = UserInputData.forUpdate(
                USER_ID.toString(),
                UPDATED_EMAIL,
                UPDATED_FIRST_NAME,
                UPDATED_LAST_NAME,
                invalidLanguageCode
        );

        assertThrows(ResourceNotFoundException.class,
                () -> useCase.updateUser(inputData, output -> {
                }));
    }

    @Test
    void updateUser_whenVismaConnectFails_throwsConnectUserException() {
        vismaConnectUserGateway.setShouldFailOnUpdate(true);
        var inputData = UserInputData.forUpdate(
                USER_ID.toString(),
                UPDATED_EMAIL,
                UPDATED_FIRST_NAME,
                UPDATED_LAST_NAME,
                LANGUAGE_CODE
        );

        assertThrows(ConnectUserException.class,
                () -> useCase.updateUser(inputData, output -> {
                }));

        var unchangedUser = userGateway.findById(USER_ID);
        assertEquals(EMAIL, unchangedUser.email());
        assertEquals(FIRST_NAME, unchangedUser.firstName());
        assertEquals(LAST_NAME, unchangedUser.lastName());
    }

    @Test
    void updateUser_preservesRecordVersion() {
        var inputData = UserInputData.forUpdate(
                USER_ID.toString(),
                UPDATED_EMAIL,
                UPDATED_FIRST_NAME,
                UPDATED_LAST_NAME,
                LANGUAGE_CODE
        );
        final AtomicReference<UserOutputData> result = new AtomicReference<>();

        useCase.updateUser(inputData, output -> result.set(output));

        var updatedUser = userGateway.findById(USER_ID);
        assertEquals(1L, updatedUser.recordVersion());
    }

    @Test
    void updateUser_updatesWhenEditedDate() {
        var originalUser = userGateway.findById(USER_ID);
        Date originalWhenEdited = originalUser.whenEdited();

        var inputData = UserInputData.forUpdate(
                USER_ID.toString(),
                UPDATED_EMAIL,
                UPDATED_FIRST_NAME,
                UPDATED_LAST_NAME,
                LANGUAGE_CODE
        );

        useCase.updateUser(inputData, output -> {
        });

        var updatedUser = userGateway.findById(USER_ID);
        assertNotNull(updatedUser.whenEdited());
        assertTrue(updatedUser.whenEdited().getTime() >= originalWhenEdited.getTime());
    }

    @Test
    void updateUser_onlyUpdatesEmailField() {
        var inputData = UserInputData.forUpdate(
                USER_ID.toString(),
                UPDATED_EMAIL,
                FIRST_NAME,
                LAST_NAME,
                LANGUAGE_CODE
        );
        final AtomicReference<UserOutputData> result = new AtomicReference<>();

        useCase.updateUser(inputData, output -> result.set(output));

        var updatedUser = userGateway.findById(USER_ID);
        assertEquals(UPDATED_EMAIL, updatedUser.email());
        assertEquals(FIRST_NAME, updatedUser.firstName());
        assertEquals(LAST_NAME, updatedUser.lastName());
        assertEquals(LANGUAGE_ID, updatedUser.idLanguage());
    }

    @Test
    void updateUser_onlyUpdatesFirstNameField() {
        var inputData = UserInputData.forUpdate(
                USER_ID.toString(),
                EMAIL,
                UPDATED_FIRST_NAME,
                LAST_NAME,
                LANGUAGE_CODE
        );
        final AtomicReference<UserOutputData> result = new AtomicReference<>();

        useCase.updateUser(inputData, output -> result.set(output));

        var updatedUser = userGateway.findById(USER_ID);
        assertEquals(EMAIL, updatedUser.email());
        assertEquals(UPDATED_FIRST_NAME, updatedUser.firstName());
        assertEquals(LAST_NAME, updatedUser.lastName());
    }

    @Test
    void updateUser_onlyUpdatesLanguageCode() {
        var inputData = UserInputData.forUpdate(
                USER_ID.toString(),
                EMAIL,
                FIRST_NAME,
                LAST_NAME,
                UPDATED_LANGUAGE_CODE
        );
        final AtomicReference<UserOutputData> result = new AtomicReference<>();

        useCase.updateUser(inputData, output -> result.set(output));

        var updatedUser = userGateway.findById(USER_ID);
        assertEquals(UPDATED_LANGUAGE_ID, updatedUser.idLanguage());
        assertEquals(UPDATED_LANGUAGE_CODE, result.get().languageCode());
    }
}
