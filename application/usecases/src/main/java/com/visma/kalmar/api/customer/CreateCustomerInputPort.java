package com.visma.kalmar.api.customer;

import com.visma.kalmar.api.exception.InvalidInputDataException;
import com.visma.kalmar.api.exception.ResourceNotFoundException;

import java.util.UUID;

public interface CreateCustomerInputPort {

    void createCustomer(CreateCustomerInputData inputData, CustomerOutputPort outputPort)
            throws InvalidInputDataException, ResourceNotFoundException;

    record CreateCustomerInputData(
            UUID idCustomer,
            UUID idCountry,
            UUID idContextParent,
            String organizationNumber,
            String name
    ) {
        public CreateCustomerInputData {
            if (idCustomer == null) {
                idCustomer = UUID.randomUUID();
            }
            if (idCountry == null) {
                throw new InvalidInputDataException("Customer", "idCountry is mandatory");
            }
            if (organizationNumber == null || organizationNumber.isBlank()) {
                throw new InvalidInputDataException("Customer", "organizationNumber is mandatory");
            }
            if (name == null || name.isBlank()) {
                throw new InvalidInputDataException("Customer", "name is mandatory");
            }
        }
    }
}
