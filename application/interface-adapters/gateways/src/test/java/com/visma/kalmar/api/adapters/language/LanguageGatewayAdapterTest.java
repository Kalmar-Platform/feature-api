package com.visma.kalmar.api.adapters.language;

import com.visma.kalmar.api.entities.language.Language;
import com.visma.kalmar.api.exception.ResourceNotFoundException;
import com.visma.useraccess.kalmar.api.language.LanguageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LanguageGatewayAdapterTest {

    private static final UUID LANGUAGE_ID = UUID.randomUUID();
    private static final String LANGUAGE_CODE = "en";
    private static final String LANGUAGE_NAME = "English";

    @Mock
    private LanguageRepository languageRepository;

    private LanguageGatewayAdapter languageGatewayAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        languageGatewayAdapter = new LanguageGatewayAdapter(languageRepository);
    }

    @Test
    void findByCode_existingLanguage_returnsLanguage() {
        com.visma.useraccess.kalmar.api.language.Language languageEntity =
                new com.visma.useraccess.kalmar.api.language.Language();
        languageEntity.setIdLanguage(LANGUAGE_ID);
        languageEntity.setCode(LANGUAGE_CODE);
        languageEntity.setName(LANGUAGE_NAME);

        when(languageRepository.findByCode(LANGUAGE_CODE)).thenReturn(Optional.of(languageEntity));

        Language result = languageGatewayAdapter.findByCode(LANGUAGE_CODE);

        assertNotNull(result);
        assertEquals(LANGUAGE_ID, result.idLanguage());
        assertEquals(LANGUAGE_CODE, result.code());
        assertEquals(LANGUAGE_NAME, result.name());
        verify(languageRepository, times(1)).findByCode(LANGUAGE_CODE);
    }

    @Test
    void findByCode_nonExistingLanguage_throwsException() {
        when(languageRepository.findByCode(LANGUAGE_CODE)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> languageGatewayAdapter.findByCode(LANGUAGE_CODE));

        verify(languageRepository, times(1)).findByCode(LANGUAGE_CODE);
    }

    @Test
    void findById_existingLanguage_returnsLanguage() {
        com.visma.useraccess.kalmar.api.language.Language languageEntity =
                new com.visma.useraccess.kalmar.api.language.Language();
        languageEntity.setIdLanguage(LANGUAGE_ID);
        languageEntity.setCode(LANGUAGE_CODE);
        languageEntity.setName(LANGUAGE_NAME);

        when(languageRepository.findById(LANGUAGE_ID)).thenReturn(Optional.of(languageEntity));

        Language result = languageGatewayAdapter.findById(LANGUAGE_ID);

        assertNotNull(result);
        assertEquals(LANGUAGE_ID, result.idLanguage());
        assertEquals(LANGUAGE_CODE, result.code());
        assertEquals(LANGUAGE_NAME, result.name());
        verify(languageRepository, times(1)).findById(LANGUAGE_ID);
    }

    @Test
    void findById_nonExistingLanguage_throwsException() {
        when(languageRepository.findById(LANGUAGE_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> languageGatewayAdapter.findById(LANGUAGE_ID));

        verify(languageRepository, times(1)).findById(LANGUAGE_ID);
    }
}
