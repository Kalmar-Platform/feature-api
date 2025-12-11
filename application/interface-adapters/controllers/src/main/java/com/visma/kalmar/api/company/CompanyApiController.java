package com.visma.kalmar.api.company;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class CompanyApiController implements CompanyApi {

    private final CreateCompanyInputPort createCompanyInputPort;
    private final GetCompanyInputPort getCompanyInputPort;
    private final DeleteCompanyInputPort deleteCompanyInputPort;
    private final UpdateCompanyInputPort updateCompanyInputPort;
    private final CompanyPresenter companyPresenter;

    public CompanyApiController(
            CreateCompanyInputPort createCompanyInputPort,
            GetCompanyInputPort getCompanyInputPort,
            DeleteCompanyInputPort deleteCompanyInputPort,
            UpdateCompanyInputPort updateCompanyInputPort,
            CompanyPresenter companyPresenter) {
        this.createCompanyInputPort = createCompanyInputPort;
        this.getCompanyInputPort = getCompanyInputPort;
        this.deleteCompanyInputPort = deleteCompanyInputPort;
        this.updateCompanyInputPort = updateCompanyInputPort;
        this.companyPresenter = companyPresenter;
    }

    @Override
    public ResponseEntity<CompanyResponse> createCompany(CompanyRequest request) {
        var inputData = new CreateCompanyInputPort.CreateCompanyInputData(
                request.idContext(),
                request.countryCode(),
                request.idContextParent(),
                request.organizationNumber(),
                request.name()
        );

        createCompanyInputPort.createCompany(inputData, companyPresenter);

        return companyPresenter.getResponse();
    }

    @Override
    public ResponseEntity<CompanyResponse> getCompany(String idCustomer, String idCompany) {
        UUID customerId = UUID.fromString(idCustomer);
        UUID companyId = UUID.fromString(idCompany);
        
        getCompanyInputPort.getCompany(customerId, companyId, companyPresenter);
        
        return companyPresenter.getResponse();
    }

    @Override
    public ResponseEntity<Void> deleteCompany(String idCustomer, String idCompany) {
        UUID customerId = UUID.fromString(idCustomer);
        UUID companyId = UUID.fromString(idCompany);
        
        deleteCompanyInputPort.deleteCompany(customerId, companyId);
        
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<CompanyResponse> updateCompany(String idCustomer, String idCompany, CompanyRequest request) {
        UUID customerId = UUID.fromString(idCustomer);
        UUID companyId = UUID.fromString(idCompany);
        
        var inputData = new UpdateCompanyInputPort.UpdateCompanyInputData(
                companyId,
                request.name(),
                request.organizationNumber(),
                request.countryCode()
        );
        
        updateCompanyInputPort.updateCompany(customerId, inputData, companyPresenter);
        
        return companyPresenter.getResponse();
    }
}
