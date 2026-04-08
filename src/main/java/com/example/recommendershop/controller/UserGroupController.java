package com.example.recommendershop.controller;

import com.example.recommendershop.dto.ResponseData;
import com.example.recommendershop.dto.userGroup.UserGroupRequest;
import com.example.recommendershop.dto.userGroup.UserGroupResponse;
import com.example.recommendershop.service.userGroup.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/userGroups")
public class UserGroupController {
    private final UserGroupService userGroupService;
    @Autowired
    public UserGroupController(UserGroupService userGroupService){
        this.userGroupService = userGroupService;
    }
    @PostMapping("/")
    public UserGroupResponse create(@RequestBody UserGroupRequest userGroupRequest){
        return userGroupService.add(userGroupRequest);
    }
    @PatchMapping("/{userGroupId}")
    public UserGroupResponse edit(@PathVariable(name = "userGroupId")UUID userGroupId, @RequestBody UserGroupRequest userGroupRequest){
        return userGroupService.update(userGroupId, userGroupRequest);
    }
    @GetMapping("/")
    public List<UserGroupResponse> getAll(){
        return userGroupService.findAll();
    }
    @GetMapping("/{userGroupId}")
    public UserGroupResponse findById(@PathVariable(name = "userGroupId") UUID userGroupId){
        return userGroupService.getDetail(userGroupId);
    }
    @DeleteMapping("/{userGroupId}")
    public ResponseData<?> delete(@PathVariable(name = "userGroupId") UUID userGroupId){
        return userGroupService.delete(userGroupId);
    }

}
