package com.visma.kalmar.api.customer;

import java.util.UUID;

public interface GetCustomerInputPort {

    void getCustomer(UUID idCustomer, GetCustomerOutputPort outputPort);
}
