package com.visma.kalmar.api.adapters.customer;

import com.visma.kalmar.api.customer.CustomerGateway;
import com.visma.kalmar.api.entities.context.Context;
import com.visma.kalmar.api.entities.customer.Customer;
import com.visma.kalmar.api.exception.ResourceNotFoundException;
import com.visma.feature.kalmar.api.customer.CustomerRepository;

import java.util.UUID;

public class CustomerGatewayAdapter implements CustomerGateway {

    private final CustomerRepository customerRepository;

    public CustomerGatewayAdapter(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer save(Context customer) {
        var jpaEntity = toJpaEntity(customer);
        var savedEntity = customerRepository.save(jpaEntity);
        return toDomainEntity(savedEntity);
    }

    @Override
    public Customer findById(UUID idCustomer) {
        var jpaEntity = customerRepository.findById(idCustomer)
                .orElse(null);
        
        if (jpaEntity == null) {
            return null;
        }
        
        return toDomainEntity(jpaEntity);
    }

    @Override
    public void deleteById(UUID idCustomer) {
        if (!customerRepository.existsById(idCustomer)) {
            throw new ResourceNotFoundException("Customer", "Customer not found with id: " + idCustomer);
        }
        customerRepository.deleteById(idCustomer);
    }

    private com.visma.feature.kalmar.api.customer.Customer toJpaEntity(Context domain) {
        var jpaEntity = new com.visma.feature.kalmar.api.customer.Customer();
        jpaEntity.setIdContext(domain.idContext());
        jpaEntity.setName(domain.name());
        jpaEntity.setIdCountry(domain.idCountry());
        jpaEntity.setIdContextType(domain.idContextType());
        jpaEntity.setIdContextParent(domain.idContextParent());
        jpaEntity.setOrganizationNumber(domain.organizationNumber());
        return jpaEntity;
    }

    private Customer toDomainEntity(com.visma.feature.kalmar.api.customer.Customer jpaEntity) {
        return new Customer(jpaEntity.getIdContext());
    }
}
