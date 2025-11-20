package com.visma.kalmar.api.language;

import com.visma.kalmar.api.entities.language.Language;

public interface LanguageGateway {
    
    Language findByCode(String code);
}
