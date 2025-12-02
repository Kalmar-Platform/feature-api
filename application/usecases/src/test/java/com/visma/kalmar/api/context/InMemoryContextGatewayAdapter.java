package com.visma.kalmar.api.context;

import com.visma.kalmar.api.entities.context.Context;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryContextGatewayAdapter implements ContextGateway {

    private final ConcurrentHashMap<UUID, Context> contextStore = new ConcurrentHashMap<>();

    @Override
    public Context findById(UUID idContext) {
        return contextStore.get(idContext);
    }

    @Override
    public boolean existsById(UUID idContext) {
        return contextStore.containsKey(idContext);
    }

    public void save(Context context) {
        contextStore.put(context.idContext(), context);
    }

    public void clear() {
        contextStore.clear();
    }
}
