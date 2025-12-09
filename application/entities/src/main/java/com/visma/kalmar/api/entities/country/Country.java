package com.visma.kalmar.api.entities.country;

import java.util.UUID;

public record Country(
        UUID idCountry,
        String name,
        String code
) {
}
