package com.visma.kalmar.api.adapters.contexttype;

import com.visma.kalmar.api.contexttype.ContextTypeGateway;
import com.visma.kalmar.api.entities.contexttype.ContextType;
import com.visma.kalmar.api.exception.ResourceNotFoundException;
import com.visma.feature.kalmar.api.contexttype.ContextTypeRepository;

public class ContextTypeGatewayAdapter implements ContextTypeGateway {

    private final ContextTypeRepository contextTypeRepository;

    public ContextTypeGatewayAdapter(ContextTypeRepository contextTypeRepository) {
        this.contextTypeRepository = contextTypeRepository;
    }

    @Override
    public ContextType findByName(String name) {
        var jpaEntity = contextTypeRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("ContextType", "ContextType not found with name: " + name));
        
        return toDomainEntity(jpaEntity);
    }

    private ContextType toDomainEntity(com.visma.feature.kalmar.api.contexttype.ContextType jpaEntity) {
        return new ContextType(
                jpaEntity.getIdContextType(),
                jpaEntity.getName()
        );
    }
}
