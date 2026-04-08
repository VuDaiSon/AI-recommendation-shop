package com.example.recommendershop.controller;

import com.example.recommendershop.dto.ResponseData;
import com.example.recommendershop.dto.user.request.*;
import com.example.recommendershop.dto.user.response.UserInfor;
import com.example.recommendershop.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseData<?> register(@RequestBody @Valid UserRequest userRequest) {
        return userService.register(userRequest);
    }

    @PostMapping("/login")
    public ResponseData<?> login(@RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }

    @PostMapping("/logout")
    public void logout() {
        userService.logout();
    }

    @GetMapping("/{userId}")
    public UserInfor getById(@PathVariable(name = "userId")UUID userId){
        return userService.detail(userId);
    }
    @PutMapping("/{userId}")
    public UserInfor edit(@PathVariable(name = "userId") UUID userId, @RequestBody UserEditRequest userEditRequest){
        return userService.update(userId, userEditRequest);
    }
    @PutMapping("/{userId}/change-password")
    public ResponseData<?> change(@PathVariable(name = "userId")UUID userId, @RequestBody ChangePasswordRequest changePasswordRequest){
        return userService.changePassword(userId, changePasswordRequest);
    }
    @PostMapping("/forgot-password")
    public ResponseData<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return userService.forgotPassword(request.getEmail());
    }   @PostMapping("/reset-password")
    public ResponseData<?> resetPassword(@RequestBody ResetPasswordRequest request){
        return userService.resetPassword(request);
    }

}
