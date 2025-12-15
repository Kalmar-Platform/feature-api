package com.visma.kalmar.api.company;

import com.visma.kalmar.api.exception.ResourceNotFoundException;

import java.util.UUID;

public interface DeleteCompanyInputPort {

    void deleteCompany(UUID idCustomer, UUID idCompany) throws ResourceNotFoundException;
}
