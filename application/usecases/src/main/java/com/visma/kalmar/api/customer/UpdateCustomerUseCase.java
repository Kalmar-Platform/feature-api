package com.visma.kalmar.api.customer;

import com.visma.kalmar.api.context.ContextGateway;
import com.visma.kalmar.api.entities.context.Context;
import com.visma.kalmar.api.entities.customer.Customer;
import com.visma.kalmar.api.exception.ResourceNotFoundException;

public class UpdateCustomerUseCase implements UpdateCustomerInputPort {

    private final CustomerGateway customerGateway;
    private final ContextGateway contextGateway;

    public UpdateCustomerUseCase(CustomerGateway customerGateway, ContextGateway contextGateway) {
        this.customerGateway = customerGateway;
        this.contextGateway = contextGateway;
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

        Context updatedContext = new Context(
                inputData.idCustomer(),
                existingContext.idContextType(),
                inputData.idContextParent(),
                inputData.idCountry(),
                inputData.name(),
                inputData.organizationNumber()
        );

        customerGateway.save(updatedContext);

        outputPort.present(existingCustomer, updatedContext, false);
    }
}
