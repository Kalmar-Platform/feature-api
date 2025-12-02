package com.visma.kalmar.api.context;

import com.visma.kalmar.api.entities.context.Context;

import java.util.UUID;

public interface ContextGateway {
    
    Context findById(UUID idContext);
    
    boolean existsById(UUID idContext);
}
