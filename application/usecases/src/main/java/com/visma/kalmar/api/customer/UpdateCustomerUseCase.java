package com.visma.kalmar.api.customer;

import com.visma.kalmar.api.context.ContextGateway;
import com.visma.kalmar.api.country.CountryGateway;
import com.visma.kalmar.api.entities.context.Context;
import com.visma.kalmar.api.entities.customer.Customer;
import com.visma.kalmar.api.exception.ResourceNotFoundException;

import java.util.UUID;

public class UpdateCustomerUseCase implements UpdateCustomerInputPort {

    private final CustomerGateway customerGateway;
    private final ContextGateway contextGateway;
    private final CountryGateway countryGateway;

    public UpdateCustomerUseCase(
            CustomerGateway customerGateway, 
            ContextGateway contextGateway,
            CountryGateway countryGateway) {
        this.customerGateway = customerGateway;
        this.contextGateway = contextGateway;
        this.countryGateway = countryGateway;
    }

    @Override
    public void updateCustomer(UpdateCustomerInputData inputData, CustomerOutputPort outputPort) {
        Customer existingCustomer = customerGateway.findById(inputData.idCustomer());
        if (existingCustomer == null) {
            throw new ResourceNotFoundException("Customer", "Customer not found with id: " + inputData.idCustomer());
        }

        Context existingContext = contextGateway.findById(inputData.idCustomer());
        if (existingContext == null) {
            throw new ResourceNotFoundException("Context", "Context not found with id: " + inputData.idCustomer());
        }

        if (inputData.idContextParent() != null && !contextGateway.existsById(inputData.idContextParent())) {
            throw new ResourceNotFoundException("Context", "Parent context not found with id: " + inputData.idContextParent());
        }

        UUID idCountry = resolveCountryId(inputData.countryCode(), inputData.idContextParent(), existingContext);

        Context updatedContext = new Context(
                inputData.idCustomer(),
                existingContext.idContextType(),
                inputData.idContextParent(),
                idCountry,
                inputData.name(),
                inputData.organizationNumber()
        );

        customerGateway.save(updatedContext);

        outputPort.present(existingCustomer, updatedContext, false);
    }

    private UUID resolveCountryId(String countryCode, UUID idContextParent, Context existingContext) {
        if (countryCode != null && !countryCode.isBlank()) {
            var country = countryGateway.findByCode(countryCode);
            return country.idCountry();
        }
        
        if (idContextParent != null) {
            var parentContext = contextGateway.findById(idContextParent);
            if (parentContext != null) {
                return parentContext.idCountry();
            }
        }
        
        return existingContext.idCountry();
    }
}
