package com.visma.kalmar.api.customer;

import com.visma.kalmar.api.exception.ResourceNotFoundException;

import java.util.UUID;

public class DeleteCustomerUseCase implements DeleteCustomerInputPort {

    private final CustomerGateway customerGateway;

    public DeleteCustomerUseCase(CustomerGateway customerGateway) {
        this.customerGateway = customerGateway;
    }

    @Override
    public void deleteCustomer(UUID idCustomer, DeleteCustomerOutputPort outputPort)
            throws ResourceNotFoundException {
        
        customerGateway.deleteById(idCustomer);
        
        outputPort.presentDeleted();
    }
}
