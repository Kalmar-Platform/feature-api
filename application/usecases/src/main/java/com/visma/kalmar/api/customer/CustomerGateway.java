package com.visma.kalmar.api.customer;

import com.visma.kalmar.api.entities.context.Context;
import com.visma.kalmar.api.entities.customer.Customer;

import java.util.UUID;

public interface CustomerGateway {
    
    Customer save(Context customer);
    
    Customer findById(UUID idCustomer);
    
    void deleteById(UUID idCustomer);
}
