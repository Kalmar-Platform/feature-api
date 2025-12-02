package com.visma.kalmar.api.entities.context;

import java.util.UUID;

public record Context(
        UUID idContext,
        UUID idContextType,
        UUID idContextParent,
        UUID idCountry,
        String name,
        String organizationNumber
) {
}
