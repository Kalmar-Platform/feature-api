package com.visma.kalmar.api.language;

import com.visma.kalmar.api.entities.language.Language;
import com.visma.kalmar.api.exception.ResourceNotFoundException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryLanguageGatewayAdapter implements LanguageGateway {

    private final Map<UUID, Language> database = new ConcurrentHashMap<>();
    private final Map<String, Language> codeIndex = new ConcurrentHashMap<>();

    @Override
    public Language findByCode(String code) {
        Language language = codeIndex.get(code);
        if (language == null) {
            throw new ResourceNotFoundException("Language", "Language not found with code: " + code);
        }
        return language;
    }

    @Override
    public Language findById(UUID idLanguage) {
        Language language = database.get(idLanguage);
        if (language == null) {
            throw new ResourceNotFoundException("Language", "Language not found with id: " + idLanguage);
        }
        return language;
    }

    public void save(Language language) {
        database.put(language.idLanguage(), language);
        codeIndex.put(language.code(), language);
    }

    public void clear() {
        database.clear();
        codeIndex.clear();
    }
}
