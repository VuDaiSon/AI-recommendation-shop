package com.example.recommendershop.mapper;

import com.example.recommendershop.dto.userGroup.UserGroupRequest;
import com.example.recommendershop.dto.userGroup.UserGroupResponse;
import com.example.recommendershop.entity.UserGroup;
import org.mapstruct.Mapper;

import java.util.List;
@Mapper(componentModel = "spring")
public interface UserGroupMapper {
    UserGroup toEntity(UserGroupRequest userGroupRequest);
    UserGroupResponse toDao(UserGroup userGroup);
    List<UserGroupResponse> toListDao(List<UserGroup> userGroups);

}
