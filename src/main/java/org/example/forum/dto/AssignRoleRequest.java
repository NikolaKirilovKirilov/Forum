package org.example.forum.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.forum.model.Role;

@Getter
@Setter
public class AssignRoleRequest {

    @NotNull
    private Role role;
}
