package com.example.recommendershop.mapper;

import com.example.recommendershop.dto.user.request.UserEditRequest;
import com.example.recommendershop.dto.user.request.UserRequest;
import com.example.recommendershop.dto.user.response.UserInfor;
import com.example.recommendershop.dto.admin.response.UserViewForAdmin;
import com.example.recommendershop.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRequest userRequest);
    void update(UserEditRequest request, @MappingTarget User user);
    UserInfor toDao(User user);
    List<UserViewForAdmin> toListDao(List<User> users);
    void update(UserRequest userRequest, @MappingTarget User user);


}
