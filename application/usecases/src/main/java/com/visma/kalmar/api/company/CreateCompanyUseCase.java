package com.visma.kalmar.api.company;

import com.visma.kalmar.api.constants.ContextTypeName;
import com.visma.kalmar.api.context.ContextGateway;
import com.visma.kalmar.api.contexttype.ContextTypeGateway;
import com.visma.kalmar.api.country.CountryGateway;
import com.visma.kalmar.api.customer.CustomerGateway;
import com.visma.kalmar.api.entities.company.Company;
import com.visma.kalmar.api.entities.context.Context;
import com.visma.kalmar.api.exception.InvalidInputDataException;
import com.visma.kalmar.api.exception.ResourceAlreadyExistsException;
import com.visma.kalmar.api.exception.ResourceNotFoundException;

import java.util.UUID;

public class CreateCompanyUseCase implements CreateCompanyInputPort {

    private final CompanyGateway companyGateway;
    private final CustomerGateway customerGateway;
    private final ContextGateway contextGateway;
    private final ContextTypeGateway contextTypeGateway;
    private final CountryGateway countryGateway;

    public CreateCompanyUseCase(
            CompanyGateway companyGateway,
            CustomerGateway customerGateway,
            ContextGateway contextGateway,
            ContextTypeGateway contextTypeGateway,
            CountryGateway countryGateway) {
        this.companyGateway = companyGateway;
        this.customerGateway = customerGateway;
        this.contextGateway = contextGateway;
        this.contextTypeGateway = contextTypeGateway;
        this.countryGateway = countryGateway;
    }

    @Override
    public void createCompany(CreateCompanyInputData inputData, CompanyOutputPort outputPort)
            throws InvalidInputDataException, ResourceNotFoundException {

        Company existingCompany = companyGateway.findById(inputData.idCompany());
        if (existingCompany != null) {
            throw new ResourceAlreadyExistsException("Company", 
                "Company already exists with id: " + inputData.idCompany());
        }

        validateParentIsCustomer(inputData.idContextParent());

        if (companyGateway.existsByNameAndParent(inputData.name(), inputData.idContextParent())) {
            throw new ResourceAlreadyExistsException("Company",
                "Company with name '" + inputData.name() + "' already exists under this customer");
        }

        var contextType = contextTypeGateway.findByName(ContextTypeName.COMPANY.getValue());
        if (contextType == null) {
            throw new ResourceNotFoundException("ContextType", 
                "ContextType not found with name: " + ContextTypeName.COMPANY.getValue());
        }

        UUID idCountry = resolveCountryId(inputData.countryCode(), inputData.idContextParent());

        if (companyGateway.existsByOrganizationNumberAndCountryAndParent(
                inputData.organizationNumber(), idCountry, inputData.idContextParent())) {
            throw new ResourceAlreadyExistsException("Company",
                "Company with organization number '" + inputData.organizationNumber() + 
                "' and country code '" + inputData.countryCode() + "' already exists under this customer");
        }

        var context = new Context(
                inputData.idCompany(),
                contextType.idContextType(),
                inputData.idContextParent(),
                idCountry,
                inputData.name(),
                inputData.organizationNumber()
        );
        var savedCompany = companyGateway.save(context);

        outputPort.present(savedCompany, context, true);
    }

    private UUID resolveCountryId(String countryCode, UUID idContextParent) {
        if (countryCode != null && !countryCode.isBlank()) {
            var country = countryGateway.findByCode(countryCode);
            return country.idCountry();
        }
        
        var parentContext = contextGateway.findById(idContextParent);
        if (parentContext == null) {
            throw new ResourceNotFoundException("Context", "Parent context not found with id: " + idContextParent);
        }
        
        return parentContext.idCountry();
    }

    private void validateParentIsCustomer(UUID idContextParent) {
        var customer = customerGateway.findById(idContextParent);
        if (customer == null) {
            throw new ResourceNotFoundException("Customer", 
                "Parent context is not a valid customer with id: " + idContextParent);
        }
    }
}
