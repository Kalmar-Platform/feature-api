package com.visma.kalmar.api.customer;

import com.visma.kalmar.api.exception.InvalidInputDataException;

import java.util.UUID;

public interface UpdateCustomerInputPort {

    void updateCustomer(UpdateCustomerInputData inputData, CustomerOutputPort outputPort);

    record UpdateCustomerInputData(
            UUID idCustomer,
            String countryCode,
            UUID idContextParent,
            String organizationNumber,
            String name
    ) {
        public UpdateCustomerInputData {
            if (idCustomer == null) {
                throw new InvalidInputDataException("Customer", "idCustomer is mandatory for update operation");
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
