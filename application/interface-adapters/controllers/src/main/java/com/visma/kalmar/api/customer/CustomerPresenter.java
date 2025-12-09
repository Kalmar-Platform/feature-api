package com.visma.kalmar.api.customer;

import com.visma.kalmar.api.country.CountryGateway;
import com.visma.kalmar.api.entities.context.Context;
import com.visma.kalmar.api.entities.customer.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerPresenter implements CustomerOutputPort, GetCustomerOutputPort, DeleteCustomerOutputPort {

    private final CountryGateway countryGateway;
    private ResponseEntity<CustomerResponse> response;
    private ResponseEntity<Void> deleteResponse;

    public CustomerPresenter(CountryGateway countryGateway) {
        this.countryGateway = countryGateway;
    }

    @Override
    public void present(Customer customer, Context context, boolean created) {
        var country = countryGateway.findById(context.idCountry());
        
        var customerResponse = new CustomerResponse(
                customer.idContext(),
                context.name(),
                context.organizationNumber(),
                country.code(),
                context.idContextParent()
        );

        HttpStatus status = created ? HttpStatus.CREATED : HttpStatus.OK;
        this.response = ResponseEntity.status(status).body(customerResponse);
    }

    @Override
    public void present(Customer customer, Context context) {
        present(customer, context, false);
    }

    @Override
    public void presentDeleted() {
        this.deleteResponse = ResponseEntity.noContent().build();
    }

    public ResponseEntity<CustomerResponse> getResponse() {
        return response;
    }

    public ResponseEntity<Void> getDeleteResponse() {
        return deleteResponse;
    }
}
