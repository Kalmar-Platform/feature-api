package com.visma.kalmar.api.entities.contexttype;

import java.util.UUID;

public record ContextType(
        UUID idContextType,
        String name
) {
}
