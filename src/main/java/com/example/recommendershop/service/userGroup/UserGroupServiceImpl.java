package com.example.recommendershop.service.userGroup;

import com.example.recommendershop.authorization.PermissionCheck;
import com.example.recommendershop.dto.ResponseData;
import com.example.recommendershop.dto.userGroup.UserGroupChangeRequest;
import com.example.recommendershop.dto.userGroup.UserGroupRequest;
import com.example.recommendershop.dto.userGroup.UserGroupResponse;
import com.example.recommendershop.entity.RoleGroup;
import com.example.recommendershop.entity.User;
import com.example.recommendershop.entity.UserGroup;
import com.example.recommendershop.exception.MasterException;
import com.example.recommendershop.mapper.UserGroupMapper;
import com.example.recommendershop.repository.RoleGroupRepository;
import com.example.recommendershop.repository.UserGroupRepository;
import com.example.recommendershop.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserGroupServiceImpl implements UserGroupService{
    private final UserGroupRepository userGroupRepository;
    private final UserGroupMapper userGroupMapper;
    private final RoleGroupRepository roleGroupRepository;
    private final UserRepository userRepository;
    private final PermissionCheck permissionCheck;

    public UserGroupServiceImpl(UserGroupRepository userGroupRepository, UserGroupMapper userGroupMapper,
                                RoleGroupRepository roleGroupRepository,
                                UserRepository userRepository, PermissionCheck permissionCheck){
        this.userGroupRepository = userGroupRepository;
        this.userGroupMapper = userGroupMapper;
        this.roleGroupRepository = roleGroupRepository;
        this.userRepository = userRepository;
        this.permissionCheck = permissionCheck;
    }

    @Override
    public UserGroupResponse add(UserGroupRequest userGroupRequest) {
        permissionCheck.checkPermission("USER_GROUP_MANAGE");
        if(userGroupRepository.findByName(userGroupRequest.getName())!=null){
            throw new MasterException(HttpStatus.BAD_REQUEST, "Nhóm người dùng đã tồn tại!");
        }
        Set<UUID> roleGroupIds = userGroupRequest.getRoleGroupIds();
        Set<RoleGroup> roleGroups = new HashSet<>();
        for(UUID roleGroupId: roleGroupIds){
            RoleGroup roleGroup = roleGroupRepository.findById(roleGroupId).orElseThrow(()-> new MasterException(HttpStatus.NOT_FOUND, "Nhóm quyền không tồn tại!"));
            roleGroups.add(roleGroup);
        }
        UserGroup userGroup = userGroupMapper.toEntity(userGroupRequest);
        userGroup.setRoleGroups(roleGroups);
        userGroup = userGroupRepository.save(userGroup);
        return userGroupMapper.toDao(userGroup);
    }
    @Override
    public UserGroupResponse update(UUID userGroupId, UserGroupRequest userGroupRequest) {
        permissionCheck.checkPermission("USER_GROUP_MANAGE");
        Optional<UserGroup> userGroupOptional = Optional.ofNullable(userGroupRepository.findById(userGroupId).orElseThrow(()-> new MasterException(HttpStatus.NOT_FOUND, "Không tìm thấy nhóm người dùng!")));
        UserGroup userGroup = userGroupOptional.get();
        Set<UUID> roleGroupIds = userGroupRequest.getRoleGroupIds();
        userGroup.setName(userGroupRequest.getName());
        Set<RoleGroup> roleGroups = new HashSet<>();
        for(UUID roleGroupId : roleGroupIds){
            RoleGroup roleGroup = roleGroupRepository.findById(roleGroupId).orElseThrow(() -> new MasterException(HttpStatus.NOT_FOUND, "Nhóm quyền không tồn tại!"));
            roleGroups.add(roleGroup);
        }
        userGroup.setRoleGroups(roleGroups);
        UserGroup updatedUserGroup = userGroupRepository.save(userGroup);
        return userGroupMapper.toDao(updatedUserGroup);
    }
    public List<UserGroupResponse> findAll(){
        List<UserGroup> userGroups = userGroupRepository.findAll();
        return userGroupMapper.toListDao(userGroups);
    }
    public UserGroupResponse getDetail(UUID userGroupId){
        if(!userGroupRepository.existsById(userGroupId)){
            throw new MasterException(HttpStatus.NOT_FOUND, "không tìm thấy nhóm người dùng!");
        }
        return userGroupMapper.toDao(userGroupRepository.getReferenceById(userGroupId));
    }

    @Override
    public ResponseData<?> delete(UUID userGroupId) {
        permissionCheck.checkPermission("USER_GROUP_MANAGE");

        UserGroup userGroup = userGroupRepository.findById(userGroupId)
                .orElseThrow(() -> new MasterException(HttpStatus.NOT_FOUND, "User group not found"));

        // Remove associations with User
        for (User user : userGroup.getUsers()) {
            user.getUserGroups().remove(userGroup);
            userRepository.save(user);
        }

        // Remove associations with RoleGroup
        for (RoleGroup roleGroup : userGroup.getRoleGroups()) {
            roleGroup.getUserGroups().remove(userGroup);
            roleGroupRepository.save(roleGroup);
        }

        userGroupRepository.delete(userGroup);
        return new ResponseData<>(HttpStatus.OK.value(), "Đã xóa thành công");
    }

    @Override
    public UserGroupResponse editUserGroup(UUID userGroupId, UserGroupChangeRequest edit) {
        permissionCheck.checkPermission("USER_GROUP_MANAGE");
        UserGroup userGroup = userGroupRepository.findById(userGroupId).orElseThrow(()-> new MasterException(HttpStatus.NOT_FOUND, "Nhóm quyền không tồn tại"));
        userGroupMapper.update(edit, userGroup);
        return userGroupMapper.toDao(userGroup);
    }
}
