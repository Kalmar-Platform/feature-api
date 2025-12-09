package com.visma.kalmar.api.customer;

import com.visma.kalmar.api.entities.context.Context;
import com.visma.kalmar.api.entities.customer.Customer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCustomerGatewayAdapter implements CustomerGateway {

    private final Map<UUID, Context> customers = new ConcurrentHashMap<>();

    @Override
    public Customer save(Context context) {
        customers.put(context.idContext(), context);
        return new Customer(context.idContext());
    }

    @Override
    public Customer findById(UUID idCustomer) {
        Context context = customers.get(idCustomer);
        if (context == null) {
            return null;
        }
        return new Customer(context.idContext());
    }

    @Override
    public void deleteById(UUID idCustomer) {
        if (!customers.containsKey(idCustomer)) {
            throw new com.visma.kalmar.api.exception.ResourceNotFoundException("Customer", "Customer not found with id: " + idCustomer);
        }
        customers.remove(idCustomer);
    }

    public Context getContextById(UUID idCustomer) {
        return customers.get(idCustomer);
    }

    public void clear() {
        customers.clear();
    }

    public boolean exists(UUID idCustomer) {
        return customers.containsKey(idCustomer);
    }
}
