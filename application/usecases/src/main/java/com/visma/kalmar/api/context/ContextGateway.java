package com.visma.kalmar.api.context;


import com.visma.kalmar.api.entities.context.Context;

import java.util.UUID;

public interface ContextGateway {
    
    Context findUserAccessContextById(UUID id);
    Context findSubscriptionContextById(UUID id);
    
    void save(Context context);
    
}
