package com.visma.kalmar.api.entities.context;

import java.util.UUID;

public class Context {
    
    private UUID idContext;
    private UUID idContextParent;
    private UUID idCountry;
    private String name;
    private String organizationNumber;

    public Context() {}

    public Context(UUID idContext, UUID idContextParent, UUID idCountry, String name, String organizationNumber) {
        this.idContext = idContext;
        this.idContextParent = idContextParent;
        this.idCountry = idCountry;
        this.name = name;
        this.organizationNumber = organizationNumber;
    }

    public UUID getIdContext() {
        return idContext;
    }

    public void setIdContext(UUID idContext) {
        this.idContext = idContext;
    }

    public UUID getIdContextParent() {
        return idContextParent;
    }

    public void setIdContextParent(UUID idContextParent) {
        this.idContextParent = idContextParent;
    }

    public UUID getIdCountry() {
        return idCountry;
    }

    public void setIdCountry(UUID idCountry) {
        this.idCountry = idCountry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganizationNumber() {
        return organizationNumber;
    }

    public void setOrganizationNumber(String organizationNumber) {
        this.organizationNumber = organizationNumber;
    }
}
