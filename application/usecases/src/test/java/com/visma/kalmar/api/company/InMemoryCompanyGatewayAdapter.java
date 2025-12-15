package com.visma.kalmar.api.company;

import com.visma.kalmar.api.entities.company.Company;
import com.visma.kalmar.api.entities.context.Context;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCompanyGatewayAdapter implements CompanyGateway {

    private final Map<UUID, Context> companies = new ConcurrentHashMap<>();

    @Override
    public Company save(Context context) {
        companies.put(context.idContext(), context);
        return new Company(context.idContext());
    }

    @Override
    public Company findById(UUID idCompany) {
        Context context = companies.get(idCompany);
        if (context == null) {
            return null;
        }
        return new Company(context.idContext());
    }

    @Override
    public void deleteById(UUID idCompany) {
        if (!companies.containsKey(idCompany)) {
            throw new com.visma.kalmar.api.exception.ResourceNotFoundException("Company", "Company not found with id: " + idCompany);
        }
        companies.remove(idCompany);
    }

    @Override
    public boolean existsByNameAndParent(String name, UUID idContextParent) {
        return companies.values().stream()
                .anyMatch(context -> context.name().equals(name) 
                        && context.idContextParent().equals(idContextParent));
    }

    @Override
    public boolean existsByOrganizationNumberAndCountryAndParent(String organizationNumber, UUID idCountry, UUID idContextParent) {
        return companies.values().stream()
                .anyMatch(context -> context.organizationNumber().equals(organizationNumber)
                        && context.idCountry().equals(idCountry)
                        && context.idContextParent().equals(idContextParent));
    }

    public Context getContextById(UUID idCompany) {
        return companies.get(idCompany);
    }

    public void clear() {
        companies.clear();
    }

    public boolean exists(UUID idCompany) {
        return companies.containsKey(idCompany);
    }
}
