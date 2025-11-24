package com.visma.kalmar.api.language;

import com.visma.kalmar.api.entities.language.Language;

import java.util.UUID;

public interface LanguageGateway {

    Language findByCode(String code);

    Language findById(UUID idLanguage);
}
