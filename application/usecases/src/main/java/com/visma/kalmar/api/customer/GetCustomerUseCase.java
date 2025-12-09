package com.visma.kalmar.api.customer;

import com.visma.kalmar.api.context.ContextGateway;
import com.visma.kalmar.api.entities.context.Context;
import com.visma.kalmar.api.entities.customer.Customer;
import com.visma.kalmar.api.exception.ResourceNotFoundException;

import java.util.UUID;

public class GetCustomerUseCase implements GetCustomerInputPort {

    private final CustomerGateway customerGateway;
    private final ContextGateway contextGateway;

    public GetCustomerUseCase(CustomerGateway customerGateway, ContextGateway contextGateway) {
        this.customerGateway = customerGateway;
        this.contextGateway = contextGateway;
    }

    @Override
    public void getCustomer(UUID idCustomer, GetCustomerOutputPort outputPort) {
        Customer customer = customerGateway.findById(idCustomer);
        if (customer == null) {
            throw new ResourceNotFoundException("Customer", "Customer not found with id: " + idCustomer);
        }

        Context context = contextGateway.findById(idCustomer);
        if (context == null) {
            throw new ResourceNotFoundException("Context", "Context not found with id: " + idCustomer);
        }

        outputPort.present(customer, context);
    }
}
