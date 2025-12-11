package com.visma.kalmar.api.adapters.company;

import com.visma.kalmar.api.company.CompanyGateway;
import com.visma.kalmar.api.entities.company.Company;
import com.visma.kalmar.api.entities.context.Context;
import com.visma.kalmar.api.exception.ResourceNotFoundException;
import com.visma.feature.kalmar.api.company.CompanyRepository;

import java.util.UUID;

public class CompanyGatewayAdapter implements CompanyGateway {

    private final CompanyRepository companyRepository;

    public CompanyGatewayAdapter(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public Company save(Context company) {
        var jpaEntity = toJpaEntity(company);
        var savedEntity = companyRepository.save(jpaEntity);
        return toDomainEntity(savedEntity);
    }

    @Override
    public Company findById(UUID idCompany) {
        var jpaEntity = companyRepository.findById(idCompany)
                .orElse(null);
        
        if (jpaEntity == null) {
            return null;
        }
        
        return toDomainEntity(jpaEntity);
    }

    @Override
    public void deleteById(UUID idCompany) {
        if (!companyRepository.existsById(idCompany)) {
            throw new ResourceNotFoundException("Company", "Company not found with id: " + idCompany);
        }
        companyRepository.deleteById(idCompany);
    }

    @Override
    public boolean existsByNameAndParent(String name, UUID idContextParent) {
        return companyRepository.existsByNameAndIdContextParent(name, idContextParent);
    }

    @Override
    public boolean existsByOrganizationNumberAndCountryAndParent(String organizationNumber, UUID idCountry, UUID idContextParent) {
        return companyRepository.existsByOrganizationNumberAndIdCountryAndIdContextParent(organizationNumber, idCountry, idContextParent);
    }

    private com.visma.feature.kalmar.api.company.Company toJpaEntity(Context domain) {
        var jpaEntity = new com.visma.feature.kalmar.api.company.Company();
        jpaEntity.setIdContext(domain.idContext());
        jpaEntity.setName(domain.name());
        jpaEntity.setIdCountry(domain.idCountry());
        jpaEntity.setIdContextType(domain.idContextType());
        jpaEntity.setIdContextParent(domain.idContextParent());
        jpaEntity.setOrganizationNumber(domain.organizationNumber());
        return jpaEntity;
    }

    private Company toDomainEntity(com.visma.feature.kalmar.api.company.Company jpaEntity) {
        return new Company(jpaEntity.getIdContext());
    }
}
