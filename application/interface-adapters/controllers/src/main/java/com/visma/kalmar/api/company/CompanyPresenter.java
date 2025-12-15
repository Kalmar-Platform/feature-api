package com.visma.kalmar.api.company;

import com.visma.kalmar.api.country.CountryGateway;
import com.visma.kalmar.api.entities.company.Company;
import com.visma.kalmar.api.entities.context.Context;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CompanyPresenter implements CompanyOutputPort, GetCompanyOutputPort {

    private final CountryGateway countryGateway;
    private ResponseEntity<CompanyResponse> response;

    public CompanyPresenter(CountryGateway countryGateway) {
        this.countryGateway = countryGateway;
    }

    @Override
    public void present(Company company, Context context, boolean created) {
        var country = countryGateway.findById(context.idCountry());
        
        var companyResponse = new CompanyResponse(
                company.idContext(),
                context.name(),
                context.organizationNumber(),
                country.code(),
                context.idContextParent()
        );

        HttpStatus status = created ? HttpStatus.CREATED : HttpStatus.OK;
        this.response = ResponseEntity.status(status).body(companyResponse);
    }

    @Override
    public void present(Company company, Context context) {
        present(company, context, false);
    }

    public ResponseEntity<CompanyResponse> getResponse() {
        return response;
    }
}
