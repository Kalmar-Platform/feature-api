package com.visma.kalmar.api.contexttype;

import com.visma.kalmar.api.entities.contexttype.ContextType;

import java.util.concurrent.ConcurrentHashMap;

public class InMemoryContextTypeGatewayAdapter implements ContextTypeGateway {

    private final ConcurrentHashMap<String, ContextType> contextTypeStore = new ConcurrentHashMap<>();

    @Override
    public ContextType findByName(String name) {
        return contextTypeStore.get(name);
    }

    public void save(ContextType contextType) {
        contextTypeStore.put(contextType.name(), contextType);
    }

    public void clear() {
        contextTypeStore.clear();
    }
}
