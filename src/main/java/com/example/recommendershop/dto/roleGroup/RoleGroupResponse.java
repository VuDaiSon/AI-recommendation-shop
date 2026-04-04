package com.example.recommendershop.dto.roleGroup;

import com.example.recommendershop.dto.role.RoleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleGroupResponse {
    private UUID roleGroupId;
    private String name;
    private Set<RoleResponse> roles;
}
