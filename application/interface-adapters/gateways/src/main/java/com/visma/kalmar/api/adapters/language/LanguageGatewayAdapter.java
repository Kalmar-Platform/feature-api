package com.visma.kalmar.api.adapters.language;

import com.visma.kalmar.api.entities.language.Language;
import com.visma.kalmar.api.exception.ResourceNotFoundException;
import com.visma.kalmar.api.language.LanguageGateway;
import com.visma.useraccess.kalmar.api.language.LanguageRepository;

public class LanguageGatewayAdapter implements LanguageGateway {

    private final LanguageRepository userAccessLanguageRepository;

    public LanguageGatewayAdapter(LanguageRepository userAccessLanguageRepository) {
        this.userAccessLanguageRepository = userAccessLanguageRepository;
    }

    @Override
    public Language findByCode(String code) {
        return userAccessLanguageRepository.findByCode(code)
                .map(lang -> new Language(lang.getIdLanguage(), lang.getName(), lang.getCode()))
                .orElseThrow(() -> new ResourceNotFoundException("Language", "Language not found with code: " + code));
    }
}
