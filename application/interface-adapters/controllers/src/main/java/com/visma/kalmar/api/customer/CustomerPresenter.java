package com.visma.kalmar.api.customer;

import com.visma.kalmar.api.entities.context.Context;
import com.visma.kalmar.api.entities.customer.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerPresenter implements CustomerOutputPort, DeleteCustomerOutputPort {

    private ResponseEntity<CustomerResponse> response;
    private ResponseEntity<Void> deleteResponse;

    @Override
    public void present(Customer customer, Context context, boolean created) {
        var customerResponse = new CustomerResponse(
                customer.idContext(),
                context.name(),
                context.organizationNumber(),
                context.idCountry(),
                context.idContextParent()
        );

        HttpStatus status = created ? HttpStatus.CREATED : HttpStatus.OK;
        this.response = ResponseEntity.status(status).body(customerResponse);
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
