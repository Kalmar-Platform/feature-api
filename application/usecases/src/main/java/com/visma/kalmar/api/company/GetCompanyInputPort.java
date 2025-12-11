package com.visma.kalmar.api.company;

import java.util.UUID;

public interface GetCompanyInputPort {

    void getCompany(UUID idCustomer, UUID idCompany, GetCompanyOutputPort outputPort);
}
