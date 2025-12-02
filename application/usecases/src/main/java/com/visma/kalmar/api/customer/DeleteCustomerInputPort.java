package com.visma.kalmar.api.customer;

import com.visma.kalmar.api.exception.ResourceNotFoundException;

import java.util.UUID;

public interface DeleteCustomerInputPort {

    void deleteCustomer(UUID idCustomer, DeleteCustomerOutputPort outputPort)
            throws ResourceNotFoundException;
}
