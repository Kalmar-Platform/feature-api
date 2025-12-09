package com.visma.kalmar.api.customer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class CustomerApiController implements CustomerApi {

    private final CreateCustomerInputPort createCustomerInputPort;
    private final GetCustomerInputPort getCustomerInputPort;
    private final UpdateCustomerInputPort updateCustomerInputPort;
    private final DeleteCustomerInputPort deleteCustomerInputPort;
    private final CustomerPresenter customerPresenter;

    public CustomerApiController(
            CreateCustomerInputPort createCustomerInputPort,
            GetCustomerInputPort getCustomerInputPort,
            UpdateCustomerInputPort updateCustomerInputPort,
            DeleteCustomerInputPort deleteCustomerInputPort,
            CustomerPresenter customerPresenter) {
        this.createCustomerInputPort = createCustomerInputPort;
        this.getCustomerInputPort = getCustomerInputPort;
        this.updateCustomerInputPort = updateCustomerInputPort;
        this.deleteCustomerInputPort = deleteCustomerInputPort;
        this.customerPresenter = customerPresenter;
    }

    @Override
    public ResponseEntity<CustomerResponse> createCustomer(CustomerRequest request) {
        var inputData = new CreateCustomerInputPort.CreateCustomerInputData(
                request.idContext(),
                request.countryCode(),
                request.idContextParent(),
                request.organizationNumber(),
                request.name()
        );

        createCustomerInputPort.createCustomer(inputData, customerPresenter);

        return customerPresenter.getResponse();
    }

    @Override
    public ResponseEntity<CustomerResponse> getCustomer(String idCustomer) {
        UUID customerId = UUID.fromString(idCustomer);
        
        getCustomerInputPort.getCustomer(customerId, customerPresenter);
        
        return customerPresenter.getResponse();
    }

    @Override
    public ResponseEntity<CustomerResponse> updateCustomer(String idCustomer, CustomerRequest request) {
        UUID customerId = UUID.fromString(idCustomer);
        
        var inputData = new UpdateCustomerInputPort.UpdateCustomerInputData(
                customerId,
                request.countryCode(),
                request.idContextParent(),
                request.organizationNumber(),
                request.name()
        );
        
        updateCustomerInputPort.updateCustomer(inputData, customerPresenter);
        
        return customerPresenter.getResponse();
    }

    @Override
    public ResponseEntity<Void> deleteCustomer(String idCustomer) {
        UUID customerId = UUID.fromString(idCustomer);
        
        deleteCustomerInputPort.deleteCustomer(customerId, customerPresenter);
        
        return customerPresenter.getDeleteResponse();
    }
}
