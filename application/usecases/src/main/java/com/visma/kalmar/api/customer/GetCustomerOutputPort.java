package com.visma.kalmar.api.customer;

import com.visma.kalmar.api.entities.context.Context;
import com.visma.kalmar.api.entities.customer.Customer;

public interface GetCustomerOutputPort {

    void present(Customer customer, Context context);
}
