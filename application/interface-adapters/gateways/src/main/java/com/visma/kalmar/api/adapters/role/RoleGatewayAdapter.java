package com.visma.kalmar.api.adapters.role;

import com.visma.kalmar.api.entities.role.Role;
import com.visma.kalmar.api.exception.ResourceNotFoundException;
import com.visma.kalmar.api.role.RoleGateway;
import com.visma.useraccess.kalmar.api.role.UserAccessRoleRepository;

public class RoleGatewayAdapter implements RoleGateway {
  private UserAccessRoleRepository userAccessUserAccessRoleRepository;

  public RoleGatewayAdapter(UserAccessRoleRepository userAccessUserAccessRoleRepository) {
    this.userAccessUserAccessRoleRepository = userAccessUserAccessRoleRepository;
  }

  @Override
  public Role findByName(String name) {
    return userAccessUserAccessRoleRepository
        .findByName(name)
        .map(this::toDomainRole)
        .orElseThrow(
            () -> new ResourceNotFoundException("Role", "Role not found with name: " + name));
  }

  private Role toDomainRole(com.visma.useraccess.kalmar.api.role.Role userAccessRole) {
    return new Role(userAccessRole.getIdRole(), userAccessRole.getName());
  }
}
