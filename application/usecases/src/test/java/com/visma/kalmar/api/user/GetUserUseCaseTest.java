package com.visma.kalmar.api.user;

import com.visma.kalmar.api.entities.language.Language;
import com.visma.kalmar.api.entities.user.User;
import com.visma.kalmar.api.exception.ResourceNotFoundException;
import com.visma.kalmar.api.language.InMemoryLanguageGatewayAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class GetUserUseCaseTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LANGUAGE_ID = UUID.randomUUID();
    private static final String EMAIL = "test@example.com";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String LANGUAGE_CODE = "en";

    private InMemoryUserGatewayAdapter userGateway;
    private InMemoryLanguageGatewayAdapter languageGateway;
    private GetUserUseCase useCase;

    @BeforeEach
    void setUp() {
        userGateway = new InMemoryUserGatewayAdapter();
        languageGateway = new InMemoryLanguageGatewayAdapter();
        useCase = new GetUserUseCase(userGateway, languageGateway);

        languageGateway.save(new Language(LANGUAGE_ID, "English", LANGUAGE_CODE));

        User testUser = new User(USER_ID, LANGUAGE_ID, EMAIL, FIRST_NAME, LAST_NAME, 1L, new Date());
        userGateway.save(testUser);
    }

    @Test
    void getUserById_withExistingUser_returnsUserData() {
        final AtomicReference<UserOutputData> result = new AtomicReference<>();

        useCase.getUserById(USER_ID, output -> result.set(output));

        assertNotNull(result.get());
        assertEquals(USER_ID.toString(), result.get().userId());
        assertEquals(EMAIL, result.get().email());
        assertEquals(FIRST_NAME, result.get().firstName());
        assertEquals(LAST_NAME, result.get().lastName());
        assertEquals(LANGUAGE_CODE, result.get().languageCode());
        assertFalse(result.get().created());
    }

    @Test
    void getUserById_withNonExistentUser_throwsException() {
        UUID nonExistentId = UUID.randomUUID();

        assertThrows(ResourceNotFoundException.class,
                () -> useCase.getUserById(nonExistentId, output -> {
                }));
    }

    @Test
    void getUserByEmail_withExistingUser_returnsUserData() {
        final AtomicReference<UserOutputData> result = new AtomicReference<>();

        useCase.getUserByEmail(EMAIL, output -> result.set(output));

        assertNotNull(result.get());
        assertEquals(USER_ID.toString(), result.get().userId());
        assertEquals(EMAIL, result.get().email());
        assertEquals(FIRST_NAME, result.get().firstName());
        assertEquals(LAST_NAME, result.get().lastName());
        assertEquals(LANGUAGE_CODE, result.get().languageCode());
        assertFalse(result.get().created());
    }

    @Test
    void getUserByEmail_withNonExistentEmail_throwsException() {
        String nonExistentEmail = "nonexistent@example.com";

        assertThrows(ResourceNotFoundException.class,
                () -> useCase.getUserByEmail(nonExistentEmail, output -> {
                }));
    }

    @Test
    void getUserById_withNonExistentLanguage_throwsException() {
        UUID userWithInvalidLanguage = UUID.randomUUID();
        UUID invalidLanguageId = UUID.randomUUID();
        User testUser = new User(userWithInvalidLanguage, invalidLanguageId, "test2@example.com",
                "Jane", "Smith", 1L, new Date());
        userGateway.save(testUser);

        assertThrows(ResourceNotFoundException.class,
                () -> useCase.getUserById(userWithInvalidLanguage, output -> {
                }));
    }
}
