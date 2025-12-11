package com.visma.feature.kalmar.api.company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {

  boolean existsByNameAndIdContextParent(String name, UUID idContextParent);

  boolean existsByOrganizationNumberAndIdCountryAndIdContextParent(
      String organizationNumber, UUID idCountry, UUID idContextParent);
}
