package com.example.recommendershop.service.user;

import com.example.recommendershop.dto.ResponseData;
import com.example.recommendershop.dto.user.request.*;
import com.example.recommendershop.dto.user.response.UserInfor;

import java.util.UUID;

public interface UserService {
    ResponseData<?> register(UserRequest userRequest);

    ResponseData<?> login(LoginRequest loginRequest);

    void logout();

    UserInfor update(UUID userId, UserEditRequest userEditRequest);

    UserInfor detail(UUID userId);

    ResponseData<?> changePassword(UUID uuid, ChangePasswordRequest changePasswordRequest);

    ResponseData<?> forgotPassword(String email);

     ResponseData<?> resetPassword(ResetPasswordRequest request);
}