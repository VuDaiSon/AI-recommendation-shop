package com.example.recommendershop.controller;

import com.example.recommendershop.dto.role.RoleRequest;
import com.example.recommendershop.dto.role.RoleResponse;
import com.example.recommendershop.service.role.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {
    private final RoleService roleService;
    @Autowired
    public RoleController(RoleService roleService){
        this.roleService = roleService;
    }
    @PostMapping("/")
    public RoleResponse add(@RequestBody RoleRequest roleRequest){
        return roleService.create(roleRequest);
    }
    @GetMapping("/")
    public List<RoleResponse> getAll(){
        return roleService.findAll();
    }
}
