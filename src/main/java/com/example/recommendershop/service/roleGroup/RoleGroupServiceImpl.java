package com.example.recommendershop.service.roleGroup;

import com.example.recommendershop.authorization.PermissionCheck;
import com.example.recommendershop.dto.ResponseData;
import com.example.recommendershop.dto.roleGroup.RoleGroupRequest;
import com.example.recommendershop.dto.roleGroup.RoleGroupResponse;
import com.example.recommendershop.entity.Role;
import com.example.recommendershop.entity.RoleGroup;
import com.example.recommendershop.entity.UserGroup;
import com.example.recommendershop.exception.MasterException;
import com.example.recommendershop.mapper.RoleGroupMapper;
import com.example.recommendershop.repository.RoleGroupRepository;
import com.example.recommendershop.repository.RoleRepository;
import com.example.recommendershop.repository.UserGroupRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoleGroupServiceImpl implements RoleGroupService{
    private final RoleGroupRepository roleGroupRepository;
    private final RoleRepository roleRepository;
    private final RoleGroupMapper roleGroupMapper;
    private final UserGroupRepository userGroupRepository;
    private final PermissionCheck permissionCheck;

    public RoleGroupServiceImpl(RoleGroupRepository roleGroupRepository, RoleRepository roleRepository, RoleGroupMapper roleGroupMapper,
                                UserGroupRepository userGroupRepository, PermissionCheck permissionCheck){
        this.roleGroupRepository = roleGroupRepository;
        this.roleRepository = roleRepository;
        this.roleGroupMapper = roleGroupMapper;
        this.userGroupRepository = userGroupRepository;
        this.permissionCheck = permissionCheck;
    }

    @Override
    public RoleGroupResponse create(RoleGroupRequest roleGroupRequest) {
        permissionCheck.checkPermission("ROLE_GROUP_MANAGE");
        if(roleGroupRepository.findByName(roleGroupRequest.getName())!=null){
            throw new MasterException(HttpStatus.BAD_REQUEST, "Nhóm quyền đã tồn tại");
        }
        Set<Long> roleIds = roleGroupRequest.getRoleId();
        Set<Role> roles = new HashSet<>();
        for(Long roleId : roleIds){
            Role role = roleRepository.findById(roleId).orElseThrow(() -> new MasterException(HttpStatus.NOT_FOUND, "Quyền không tồn tại"));
            roles.add(role);
        }
        RoleGroup roleGroup = roleGroupMapper.toEntity(roleGroupRequest);
        roleGroup.setRoles(roles);
        roleGroup = roleGroupRepository.save(roleGroup);
        return roleGroupMapper.toDao(roleGroup);
    }

    @Override
    public RoleGroupResponse update(UUID roleGroupId, RoleGroupRequest roleGroupRequest) {
        permissionCheck.checkPermission("ROLE_GROUP_MANAGE");
        RoleGroup roleGroup = roleGroupRepository.findById(roleGroupId)
                .orElseThrow(() -> new MasterException(HttpStatus.NOT_FOUND, "Nhóm quyền không tồn tại!"));
        Set<Long> roleIds = roleGroupRequest.getRoleId();
        roleGroup.setName(roleGroupRequest.getName());
        Set<Role> roles = new HashSet<>();
        for (Long roleId : roleIds) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new MasterException(HttpStatus.NOT_FOUND, "Quyền không tồn tại"));
            roles.add(role);
        }
        roleGroup.setRoles(roles);
        RoleGroup updatedRoleGroup = roleGroupRepository.save(roleGroup);
        return roleGroupMapper.toDao(updatedRoleGroup);
    }
    public List<RoleGroupResponse> findAll(){
        List<RoleGroup> roleGroups = roleGroupRepository.findAll();
        return roleGroupMapper.toListDao(roleGroups);
    }

    @Override
    public ResponseData<?> delete(UUID roleGroupId) {
        permissionCheck.checkPermission("ROLE_GROUP_MANAGE");
        RoleGroup roleGroup = roleGroupRepository.findById(roleGroupId)
                .orElseThrow(() -> new MasterException(HttpStatus.NOT_FOUND, "Role group not found"));
        for (UserGroup userGroup : roleGroup.getUserGroups()) {
            userGroup.getRoleGroups().remove(roleGroup);
            userGroupRepository.save(userGroup);
        }
        for (Role role : roleGroup.getRoles()) {
            role.getRoleGroups().remove(roleGroup);
            roleRepository.save(role);
        }

        roleGroupRepository.delete(roleGroup);
        return new ResponseData<>(HttpStatus.OK.value(), "Đã xóa thành công nhóm quyền");
    }
    public RoleGroupResponse findById(UUID roleGroupId){
        if(!roleGroupRepository.existsById(roleGroupId)){
            throw new MasterException(HttpStatus.NOT_FOUND, "Không tìm thấy nhóm quyền!");
        }
        return roleGroupMapper.toDao(roleGroupRepository.getReferenceById(roleGroupId));
    }
}