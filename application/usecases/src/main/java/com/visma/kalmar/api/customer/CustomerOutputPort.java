package com.visma.kalmar.api.customer;

import com.visma.kalmar.api.entities.customer.Customer;
import com.visma.kalmar.api.entities.context.Context;

import java.util.UUID;

public interface CustomerOutputPort {

    void present(Customer customer, Context context, boolean created);
}
