package com.visma.kalmar.api.entities.language;

import java.util.UUID;

public record Language(
        UUID idLanguage,
        String name,
        String code
) {
}
