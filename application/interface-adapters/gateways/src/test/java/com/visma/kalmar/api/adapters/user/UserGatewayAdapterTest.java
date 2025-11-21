package com.visma.kalmar.api.adapters.user;

import com.visma.kalmar.api.entities.user.User;
import com.visma.kalmar.api.exception.ResourceNotFoundException;
import com.visma.useraccess.kalmar.api.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserGatewayAdapterTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LANGUAGE_ID = UUID.randomUUID();
    private static final String EMAIL = "test@example.com";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final Long RECORD_VERSION = 1L;

    @Mock
    private UserRepository userRepository;

    private UserGatewayAdapter userGatewayAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userGatewayAdapter = new UserGatewayAdapter(userRepository);
    }

    @Test
    void save_success() {
        User domainUser = createDomainUser();
        com.visma.useraccess.kalmar.api.user.User repositoryUser = createRepositoryUser();

        when(userRepository.save(any(com.visma.useraccess.kalmar.api.user.User.class)))
                .thenReturn(repositoryUser);

        User result = userGatewayAdapter.save(domainUser);

        assertNotNull(result);
        assertEquals(USER_ID, result.idUser());
        assertEquals(LANGUAGE_ID, result.idLanguage());
        assertEquals(EMAIL, result.email());
        assertEquals(FIRST_NAME, result.firstName());
        assertEquals(LAST_NAME, result.lastName());
        assertEquals(RECORD_VERSION, result.recordVersion());
        verify(userRepository, times(1)).save(any(com.visma.useraccess.kalmar.api.user.User.class));
    }

    @Test
    void findById_success() {
        com.visma.useraccess.kalmar.api.user.User repositoryUser = createRepositoryUser();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(repositoryUser));

        User result = userGatewayAdapter.findById(USER_ID);

        assertNotNull(result);
        assertEquals(USER_ID, result.idUser());
        assertEquals(LANGUAGE_ID, result.idLanguage());
        assertEquals(EMAIL, result.email());
        assertEquals(FIRST_NAME, result.firstName());
        assertEquals(LAST_NAME, result.lastName());
        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    void findById_notFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userGatewayAdapter.findById(USER_ID));
        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    void findByEmail_success() {
        com.visma.useraccess.kalmar.api.user.User repositoryUser = createRepositoryUser();
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(repositoryUser));

        User result = userGatewayAdapter.findByEmail(EMAIL);

        assertNotNull(result);
        assertEquals(USER_ID, result.idUser());
        assertEquals(LANGUAGE_ID, result.idLanguage());
        assertEquals(EMAIL, result.email());
        assertEquals(FIRST_NAME, result.firstName());
        assertEquals(LAST_NAME, result.lastName());
        verify(userRepository, times(1)).findByEmail(EMAIL);
    }

    @Test
    void findByEmail_notFound() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userGatewayAdapter.findByEmail(EMAIL));
        verify(userRepository, times(1)).findByEmail(EMAIL);
    }

    @Test
    void update_success() {
        User domainUser = createDomainUser();
        com.visma.useraccess.kalmar.api.user.User repositoryUser = createRepositoryUser();

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(repositoryUser));
        when(userRepository.save(any(com.visma.useraccess.kalmar.api.user.User.class)))
                .thenReturn(repositoryUser);

        User result = userGatewayAdapter.update(domainUser);

        assertNotNull(result);
        assertEquals(USER_ID, result.idUser());
        assertEquals(LANGUAGE_ID, result.idLanguage());
        assertEquals(EMAIL, result.email());
        assertEquals(FIRST_NAME, result.firstName());
        assertEquals(LAST_NAME, result.lastName());
        verify(userRepository, times(1)).findById(USER_ID);
        verify(userRepository, times(1)).save(any(com.visma.useraccess.kalmar.api.user.User.class));
    }

    @Test
    void update_userNotFound() {
        User domainUser = createDomainUser();

        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userGatewayAdapter.update(domainUser));
        verify(userRepository, times(1)).findById(USER_ID);
        verify(userRepository, never()).save(any(com.visma.useraccess.kalmar.api.user.User.class));
    }

    @Test
    void existsByEmail_returnsTrue() {
        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

        boolean result = userGatewayAdapter.existsByEmail(EMAIL);

        assertTrue(result);
        verify(userRepository, times(1)).existsByEmail(EMAIL);
    }

    @Test
    void existsByEmail_returnsFalse() {
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);

        boolean result = userGatewayAdapter.existsByEmail(EMAIL);

        assertFalse(result);
        verify(userRepository, times(1)).existsByEmail(EMAIL);
    }

    @Test
    void existsById_returnsTrue() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);

        boolean result = userGatewayAdapter.existsById(USER_ID);

        assertTrue(result);
        verify(userRepository, times(1)).existsById(USER_ID);
    }

    @Test
    void existsById_returnsFalse() {
        when(userRepository.existsById(USER_ID)).thenReturn(false);

        boolean result = userGatewayAdapter.existsById(USER_ID);

        assertFalse(result);
        verify(userRepository, times(1)).existsById(USER_ID);
    }

    @Test
    void deleteById_success() {
        com.visma.useraccess.kalmar.api.user.User repositoryUser = createRepositoryUser();

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(repositoryUser));

        userGatewayAdapter.deleteById(USER_ID);

        verify(userRepository, times(1)).findById(USER_ID);
        verify(userRepository, times(1)).delete(repositoryUser);
    }

    @Test
    void deleteById_userNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userGatewayAdapter.deleteById(USER_ID));
        verify(userRepository, times(1)).findById(USER_ID);
        verify(userRepository, never()).delete(any(com.visma.useraccess.kalmar.api.user.User.class));
    }

    private User createDomainUser() {
        return new User(
                USER_ID,
                LANGUAGE_ID,
                EMAIL,
                FIRST_NAME,
                LAST_NAME,
                RECORD_VERSION,
                new Date()
        );
    }

    private com.visma.useraccess.kalmar.api.user.User createRepositoryUser() {
        return new com.visma.useraccess.kalmar.api.user.User(
                USER_ID,
                LANGUAGE_ID,
                EMAIL,
                FIRST_NAME,
                LAST_NAME,
                RECORD_VERSION,
                new Date()
        );
    }
}
