package com.example.recommendershop.service.userGroup;

import com.example.recommendershop.dto.ResponseData;
import com.example.recommendershop.dto.userGroup.UserGroupRequest;
import com.example.recommendershop.dto.userGroup.UserGroupResponse;

import java.util.List;
import java.util.UUID;

public interface UserGroupService {
    public UserGroupResponse add(UserGroupRequest userGroupRequest);
    public UserGroupResponse update(UUID userGroupId, UserGroupRequest userGroupRequest);
    public List<UserGroupResponse> findAll();
    public UserGroupResponse getDetail(UUID userGroupId);
    public ResponseData<?> delete(UUID userGroupId);
}
