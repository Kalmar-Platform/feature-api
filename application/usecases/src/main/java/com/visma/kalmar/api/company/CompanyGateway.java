package com.visma.kalmar.api.company;

import com.visma.kalmar.api.entities.company.Company;
import com.visma.kalmar.api.entities.context.Context;

import java.util.UUID;

public interface CompanyGateway {
    
    Company save(Context company);
    
    Company findById(UUID idCompany);
    
    void deleteById(UUID idCompany);
    
    boolean existsByNameAndParent(String name, UUID idContextParent);
    
    boolean existsByOrganizationNumberAndCountryAndParent(String organizationNumber, UUID idCountry, UUID idContextParent);
}
