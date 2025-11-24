package com.visma.kalmar.api.user;

import com.visma.kalmar.api.entities.language.Language;
import com.visma.kalmar.api.exception.ConnectUserException;
import com.visma.kalmar.api.exception.ResourceAlreadyExistsException;
import com.visma.kalmar.api.exception.ResourceNotFoundException;
import com.visma.kalmar.api.language.InMemoryLanguageGatewayAdapter;
import com.visma.kalmar.api.vismaconnect.InMemoryVismaConnectUserGatewayAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class CreateUserUseCaseTest {

    private static final String EMAIL = "test@example.com";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String LANGUAGE_CODE = "en";
    private static final UUID LANGUAGE_ID = UUID.randomUUID();

    private InMemoryUserGatewayAdapter userGateway;
    private InMemoryLanguageGatewayAdapter languageGateway;
    private InMemoryVismaConnectUserGatewayAdapter vismaConnectUserGateway;
    private CreateUserUseCase useCase;

    @BeforeEach
    void setUp() {
        userGateway = new InMemoryUserGatewayAdapter();
        languageGateway = new InMemoryLanguageGatewayAdapter();
        vismaConnectUserGateway = new InMemoryVismaConnectUserGatewayAdapter();
        useCase = new CreateUserUseCase(userGateway, languageGateway, vismaConnectUserGateway);

        Language language = new Language(LANGUAGE_ID, "English", LANGUAGE_CODE);
        languageGateway.save(language);
    }

    @Test
    void createUser_withValidData_createsUserSuccessfully() {
        var inputData = UserInputData.forCreate(EMAIL, FIRST_NAME, LAST_NAME, LANGUAGE_CODE);
        final AtomicReference<UserOutputData> result = new AtomicReference<>();

        useCase.createUser(inputData, output -> result.set(output));

        assertNotNull(result.get());
        assertNotNull(result.get().userId());
        assertEquals(EMAIL, result.get().email());
        assertEquals(FIRST_NAME, result.get().firstName());
        assertEquals(LAST_NAME, result.get().lastName());
        assertEquals(LANGUAGE_CODE, result.get().languageCode());
        assertTrue(result.get().created());

        UUID userId = UUID.fromString(result.get().userId());
        var savedUser = userGateway.findById(userId);
        assertEquals(EMAIL, savedUser.email());
        assertEquals(FIRST_NAME, savedUser.firstName());
        assertEquals(LAST_NAME, savedUser.lastName());
        assertEquals(LANGUAGE_ID, savedUser.idLanguage());
    }

    @Test
    void createUser_withExistingEmail_throwsResourceAlreadyExistsException() {
        var inputData = UserInputData.forCreate(EMAIL, FIRST_NAME, LAST_NAME, LANGUAGE_CODE);

        useCase.createUser(inputData, output -> {
        });

        var duplicateInputData = UserInputData.forCreate(EMAIL, "Jane", "Smith", LANGUAGE_CODE);

        var exception = assertThrows(ResourceAlreadyExistsException.class,
                () -> useCase.createUser(duplicateInputData, output -> {
                }));

        assertTrue(exception.getMessage().contains("User with email: " + EMAIL + " already exists"));
    }

    @Test
    void createUser_withNonExistentLanguageCode_throwsResourceNotFoundException() {
        String invalidLanguageCode = "xx";
        var inputData = UserInputData.forCreate(EMAIL, FIRST_NAME, LAST_NAME, invalidLanguageCode);

        assertThrows(ResourceNotFoundException.class,
                () -> useCase.createUser(inputData, output -> {
                }));
    }

    @Test
    void createUser_whenVismaConnectFails_throwsConnectUserException() {
        vismaConnectUserGateway.setShouldFailOnCreate(true);
        var inputData = UserInputData.forCreate(EMAIL, FIRST_NAME, LAST_NAME, LANGUAGE_CODE);

        assertThrows(ConnectUserException.class,
                () -> useCase.createUser(inputData, output -> {
                }));

        assertFalse(userGateway.existsByEmail(EMAIL));
    }

    @Test
    void createUser_savesUserWithCorrectLanguageId() {
        UUID anotherLanguageId = UUID.randomUUID();
        Language norwegianLanguage = new Language(anotherLanguageId, "Norwegian", "no");
        languageGateway.save(norwegianLanguage);

        var inputData = UserInputData.forCreate("norwegian@example.com", "Ole", "Nordmann", "no");
        final AtomicReference<UserOutputData> result = new AtomicReference<>();

        useCase.createUser(inputData, output -> result.set(output));

        UUID userId = UUID.fromString(result.get().userId());
        var savedUser = userGateway.findById(userId);
        assertEquals(anotherLanguageId, savedUser.idLanguage());
        assertEquals("no", result.get().languageCode());
    }

    @Test
    void createUser_setsRecordVersionToOne() {
        var inputData = UserInputData.forCreate(EMAIL, FIRST_NAME, LAST_NAME, LANGUAGE_CODE);
        final AtomicReference<UserOutputData> result = new AtomicReference<>();

        useCase.createUser(inputData, output -> result.set(output));

        UUID userId = UUID.fromString(result.get().userId());
        var savedUser = userGateway.findById(userId);
        assertEquals(1L, savedUser.recordVersion());
    }

    @Test
    void createUser_setsWhenEditedDate() {
        var inputData = UserInputData.forCreate(EMAIL, FIRST_NAME, LAST_NAME, LANGUAGE_CODE);
        final AtomicReference<UserOutputData> result = new AtomicReference<>();

        useCase.createUser(inputData, output -> result.set(output));

        UUID userId = UUID.fromString(result.get().userId());
        var savedUser = userGateway.findById(userId);
        assertNotNull(savedUser.whenEdited());
    }
}
