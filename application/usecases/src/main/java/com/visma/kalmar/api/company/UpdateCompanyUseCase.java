package com.visma.kalmar.api.company;

import com.visma.kalmar.api.context.ContextGateway;
import com.visma.kalmar.api.country.CountryGateway;
import com.visma.kalmar.api.entities.company.Company;
import com.visma.kalmar.api.entities.context.Context;
import com.visma.kalmar.api.exception.ResourceNotFoundException;

import java.util.UUID;

public class UpdateCompanyUseCase implements UpdateCompanyInputPort {

    private final CompanyGateway companyGateway;
    private final ContextGateway contextGateway;
    private final CountryGateway countryGateway;

    public UpdateCompanyUseCase(
            CompanyGateway companyGateway,
            ContextGateway contextGateway,
            CountryGateway countryGateway) {
        this.companyGateway = companyGateway;
        this.contextGateway = contextGateway;
        this.countryGateway = countryGateway;
    }

    @Override
    public void updateCompany(UUID idCustomer, UpdateCompanyInputData inputData, CompanyOutputPort outputPort) {
        Company existingCompany = companyGateway.findById(inputData.idCompany());
        if (existingCompany == null) {
            throw new ResourceNotFoundException("Company", "Company not found with id: " + inputData.idCompany());
        }

        Context existingContext = contextGateway.findById(inputData.idCompany());
        if (existingContext == null) {
            throw new ResourceNotFoundException("Context", "Context not found with id: " + inputData.idCompany());
        }

        if (existingContext.idContextParent() == null || !existingContext.idContextParent().equals(idCustomer)) {
            throw new ResourceNotFoundException("Company", "Company not found with id: " + inputData.idCompany());
        }

        UUID idCountry = resolveCountryId(inputData.countryCode(), existingContext);

        Context updatedContext = new Context(
                inputData.idCompany(),
                existingContext.idContextType(),
                existingContext.idContextParent(),
                idCountry,
                inputData.name(),
                inputData.organizationNumber()
        );

        companyGateway.save(updatedContext);

        outputPort.present(existingCompany, updatedContext, false);
    }

    private UUID resolveCountryId(String countryCode, Context existingContext) {
        if (countryCode != null && !countryCode.isBlank()) {
            var country = countryGateway.findByCode(countryCode);
            return country.idCountry();
        }

        return existingContext.idCountry();
    }
}
