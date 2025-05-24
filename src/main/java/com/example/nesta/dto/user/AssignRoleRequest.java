package com.example.nesta.dto.user;

import com.example.nesta.model.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignRoleRequest {
    private UserRole role;
}