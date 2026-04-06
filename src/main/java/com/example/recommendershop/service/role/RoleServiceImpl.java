package com.example.recommendershop.service.role;

import com.example.recommendershop.authorization.PermissionCheck;
import com.example.recommendershop.dto.role.RoleRequest;
import com.example.recommendershop.dto.role.RoleResponse;
import com.example.recommendershop.entity.Role;
import com.example.recommendershop.exception.MasterException;
import com.example.recommendershop.mapper.RoleMapper;
import com.example.recommendershop.repository.RoleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService{
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final PermissionCheck permissionCheck;
    public RoleServiceImpl(RoleRepository roleRepository, RoleMapper roleMapper, PermissionCheck permissionCheck){
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.permissionCheck = permissionCheck;
    }

    @Override
    public RoleResponse create(RoleRequest roleRequest) {
        permissionCheck.checkPermission("ROLE_GROUP_MANAGE");
        if(roleRepository.findByName(roleRequest.getName()) != null){
            throw new MasterException(HttpStatus.BAD_REQUEST, "Quyền đã tồn tại");
        }
        Role role = roleMapper.toEntity(roleRequest);
        role = roleRepository.save(role);
        return roleMapper.toDao(role);
    }

    @Override
    public List<RoleResponse> findAll() {
        List<Role> roles = roleRepository.findAll();
        return roleMapper.toListDao(roles);
    }
}
