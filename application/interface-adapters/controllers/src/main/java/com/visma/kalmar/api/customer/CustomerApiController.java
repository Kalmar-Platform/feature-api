package com.visma.kalmar.api.customer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class CustomerApiController implements CustomerApi {

    private final CreateCustomerInputPort createCustomerInputPort;
    private final DeleteCustomerInputPort deleteCustomerInputPort;
    private final CustomerPresenter customerPresenter;

    public CustomerApiController(
            CreateCustomerInputPort createCustomerInputPort,
            DeleteCustomerInputPort deleteCustomerInputPort,
            CustomerPresenter customerPresenter) {
        this.createCustomerInputPort = createCustomerInputPort;
        this.deleteCustomerInputPort = deleteCustomerInputPort;
        this.customerPresenter = customerPresenter;
    }

    @Override
    public ResponseEntity<CustomerResponse> createCustomer(CustomerRequest request) {
        var inputData = new CreateCustomerInputPort.CreateCustomerInputData(
                request.idCustomer(),
                request.idCountry(),
                request.idContextParent(),
                request.organizationNumber(),
                request.name()
        );

        createCustomerInputPort.createCustomer(inputData, customerPresenter);

        return customerPresenter.getResponse();
    }

    @Override
    public ResponseEntity<Void> deleteCustomer(String idCustomer) {
        UUID customerId = UUID.fromString(idCustomer);
        
        deleteCustomerInputPort.deleteCustomer(customerId, customerPresenter);
        
        return customerPresenter.getDeleteResponse();
    }
}
