package com.visma.kalmar.api.config;

import com.visma.kalmar.api.adapters.role.RoleGatewayAdapter;
import com.visma.kalmar.api.role.RoleGateway;
import com.visma.useraccess.kalmar.api.role.UserAccessRoleRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoleConfig {
  @Bean
  public RoleGateway roleGateway(UserAccessRoleRepository userAccessUserAccessRoleRepository) {
    return new RoleGatewayAdapter(userAccessUserAccessRoleRepository);
  }
}
