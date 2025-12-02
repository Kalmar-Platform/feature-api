package com.visma.kalmar.api.contexttype;

import com.visma.kalmar.api.entities.contexttype.ContextType;

public interface ContextTypeGateway {

    ContextType findByName(String name);
}
