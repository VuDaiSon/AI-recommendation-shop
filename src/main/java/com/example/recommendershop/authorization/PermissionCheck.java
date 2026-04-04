package com.example.recommendershop.authorization;

import com.example.recommendershop.entity.Role;
import com.example.recommendershop.entity.RoleGroup;
import com.example.recommendershop.entity.UserGroup;
import com.example.recommendershop.exception.MasterException;
import com.example.recommendershop.repository.RoleRepository;
import com.example.recommendershop.repository.RoleGroupRepository;
import com.example.recommendershop.repository.UserGroupRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class PermissionCheck {
    private final HttpSession httpSession;
    private final UserGroupRepository userGroupRepository;
    private final RoleGroupRepository roleGroupRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public PermissionCheck(HttpSession httpSession,
                           UserGroupRepository userGroupRepository,
                           RoleGroupRepository roleGroupRepository,
                           RoleRepository roleRepository){
        this.httpSession = httpSession;
        this.userGroupRepository = userGroupRepository;
        this.roleGroupRepository = roleGroupRepository;
        this.roleRepository = roleRepository;
    }
    public void checkPermission(String permission) {
        UUID userId = (UUID) httpSession.getAttribute("UserId");
        Set<UserGroup> userGroups = userGroupRepository.findByUsers_UserId(userId);
        Set<Role> roles = new HashSet<>();
        for (UserGroup userGroup : userGroups) {
            Set<RoleGroup> roleGroups = roleGroupRepository.findByUserGroups(userGroup);
            for (RoleGroup roleGroup : roleGroups) {
                Set<Role> roleSet = roleRepository.findByRoleGroups(roleGroup);
                roles.addAll(roleSet);
            }
        }
        boolean hasPermission = false;
        for (Role role : roles) {
            if (role.getName().equals(permission)) {
                hasPermission = true;
                break;
            }
        }
        if (!hasPermission) {
            throw new MasterException(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập tài nguyên này!");
        }
    }
}