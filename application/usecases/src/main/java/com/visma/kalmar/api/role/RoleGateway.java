package com.visma.kalmar.api.role;

import com.visma.kalmar.api.entities.role.Role;

public interface RoleGateway {
  Role findByName(String name);
}
