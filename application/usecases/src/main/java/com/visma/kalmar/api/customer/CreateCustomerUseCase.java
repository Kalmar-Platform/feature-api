package com.visma.kalmar.api.customer;

import com.visma.kalmar.api.constants.ContextTypeName;
import com.visma.kalmar.api.context.ContextGateway;
import com.visma.kalmar.api.contexttype.ContextTypeGateway;
import com.visma.kalmar.api.entities.context.Context;
import com.visma.kalmar.api.entities.customer.Customer;
import com.visma.kalmar.api.exception.InvalidInputDataException;
import com.visma.kalmar.api.exception.ResourceNotFoundException;

import java.util.UUID;

public class CreateCustomerUseCase implements CreateCustomerInputPort {

    private final CustomerGateway customerGateway;
    private final ContextGateway contextGateway;
    private final ContextTypeGateway contextTypeGateway;

    public CreateCustomerUseCase(
            CustomerGateway customerGateway,
            ContextGateway contextGateway,
            ContextTypeGateway contextTypeGateway) {
        this.customerGateway = customerGateway;
        this.contextGateway = contextGateway;
        this.contextTypeGateway = contextTypeGateway;
    }

    @Override
    public void createCustomer(CreateCustomerInputData inputData, CustomerOutputPort outputPort)
            throws InvalidInputDataException, ResourceNotFoundException {

        validateParentContextExists(inputData.idContextParent());

        var contextType = contextTypeGateway.findByName(ContextTypeName.CUSTOMER.getValue());
        if (contextType == null) {
            throw new ResourceNotFoundException("ContextType", 
                "ContextType not found with name: " + ContextTypeName.CUSTOMER.getValue());
        }

        var context = new Context(
                inputData.idCustomer(),
                contextType.idContextType(),
                inputData.idContextParent(),
                inputData.idCountry(),
                inputData.name(),
                inputData.organizationNumber()
        );
        var savedCustomer = customerGateway.save(context);

        outputPort.present(savedCustomer, context, true);
    }

    private void validateParentContextExists(UUID idContextParent) {
        if (idContextParent == null || !contextGateway.existsById(idContextParent)) {
            throw new ResourceNotFoundException("Context", "Parent context not found with id: " + idContextParent);
        }
    }
}
