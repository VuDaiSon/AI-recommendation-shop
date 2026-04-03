package com.example.recommendershop.service.admin;

import com.example.recommendershop.authorization.PermissionCheck;
import com.example.recommendershop.config.PasswordEncoder;
import com.example.recommendershop.dto.ApiListBaseRequest;
import com.example.recommendershop.dto.BasePage;
import com.example.recommendershop.dto.admin.request.UserEditForAdmin;
import com.example.recommendershop.dto.admin.response.UserViewForAdmin;
import com.example.recommendershop.entity.User;
import com.example.recommendershop.entity.UserGroup;
import com.example.recommendershop.exception.MasterException;
import com.example.recommendershop.mapper.AdminMapper;
import com.example.recommendershop.mapper.UserMapper;
import com.example.recommendershop.repository.UserGroupRepository;
import com.example.recommendershop.repository.UserRepository;
import com.example.recommendershop.utils.FilterDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class AdminServiceImpl implements AdminService{

    @Autowired
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;
    private final PermissionCheck permissionCheck;
    private final UserGroupRepository userGroupRepository;

    public AdminServiceImpl(UserRepository userRepository, UserMapper userMapper, AdminMapper adminMapper, PasswordEncoder passwordEncoder, PermissionCheck permissionCheck, UserGroupRepository userGroupRepository){
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.adminMapper = adminMapper;
        this.passwordEncoder = passwordEncoder;
        this.permissionCheck = permissionCheck;
        this.userGroupRepository = userGroupRepository;
    }
    protected BasePage<UserViewForAdmin> map(Page<User> page) {
        BasePage<UserViewForAdmin> rPage = new BasePage<>();
        rPage.setData(userMapper.toListDao(page.getContent()));
        rPage.setTotalPage(page.getTotalPages());
        rPage.setTotalRecord( page.getTotalElements());
        rPage.setPage(page.getPageable().getPageNumber());
        return rPage;
    }
    public BasePage<UserViewForAdmin> getAllUser(ApiListBaseRequest listBaseRequest){
        permissionCheck.checkPermission("USER_MANAGE");
        Page<User> page = userRepository.findAll(FilterDataUtil.buildPageRequest(listBaseRequest));
        return this.map(page);
    }

    @Override
    public UserViewForAdmin update(UUID userId, UserEditForAdmin edit) {
        User existingUser = userRepository.findById(userId).orElseThrow(()-> new MasterException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng!"));
        adminMapper.update(edit, existingUser);
        Set<UUID> userGroupIds = edit.getUserGroupIds();
        Set<UserGroup> userGroups = new HashSet<>();
        for(UUID userGroupId : userGroupIds){
            UserGroup userGroup = userGroupRepository.findById(userGroupId).orElseThrow(() ->new MasterException(HttpStatus.NOT_FOUND, "Không tìm thấy nhóm người dùng tương ứng"));
            userGroups.add(userGroup);
        }
        existingUser.setUserGroups(userGroups);
        existingUser.setPassword(passwordEncoder.encode(edit.getPassword()));
        User user = userRepository.save(existingUser);
        return adminMapper.toDao(user);
    }

    @Override
    public void deleteUser(UUID userId) {
        permissionCheck.checkPermission("USER_MANAGE");
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new MasterException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng!"));
        userRepository.delete(existingUser);
    }
}
