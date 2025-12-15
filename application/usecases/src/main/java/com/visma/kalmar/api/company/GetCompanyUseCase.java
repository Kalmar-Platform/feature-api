package com.visma.kalmar.api.company;

import com.visma.kalmar.api.context.ContextGateway;
import com.visma.kalmar.api.entities.company.Company;
import com.visma.kalmar.api.entities.context.Context;
import com.visma.kalmar.api.exception.ResourceNotFoundException;

import java.util.UUID;

public class GetCompanyUseCase implements GetCompanyInputPort {

    private final CompanyGateway companyGateway;
    private final ContextGateway contextGateway;

    public GetCompanyUseCase(CompanyGateway companyGateway, ContextGateway contextGateway) {
        this.companyGateway = companyGateway;
        this.contextGateway = contextGateway;
    }

    @Override
    public void getCompany(UUID idCustomer, UUID idCompany, GetCompanyOutputPort outputPort) {
        Company company = companyGateway.findById(idCompany);
        if (company == null) {
            throw new ResourceNotFoundException("Company", "Company not found with id: " + idCompany);
        }

        Context context = contextGateway.findById(idCompany);
        if (context == null) {
            throw new ResourceNotFoundException("Context", "Context not found with id: " + idCompany);
        }

        if (context.idContextParent() == null || !context.idContextParent().equals(idCustomer)) {
            throw new ResourceNotFoundException("Company", "Company not found with id: " + idCompany);
        }

        outputPort.present(company, context);
    }
}
