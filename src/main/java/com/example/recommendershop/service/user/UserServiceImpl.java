    package com.example.recommendershop.service.user;

    import com.example.recommendershop.dto.ResponseData;
    import com.example.recommendershop.dto.user.request.*;
    import com.example.recommendershop.dto.user.response.UserInfor;
    import com.example.recommendershop.entity.Role;
    import com.example.recommendershop.entity.RoleGroup;
    import com.example.recommendershop.entity.User;
    import com.example.recommendershop.entity.UserGroup;
    import com.example.recommendershop.exception.MasterException;
    import com.example.recommendershop.mapper.UserMapper;
    import com.example.recommendershop.repository.RoleGroupRepository;
    import com.example.recommendershop.repository.RoleRepository;
    import com.example.recommendershop.repository.UserGroupRepository;
    import com.example.recommendershop.repository.UserRepository;
    import com.example.recommendershop.config.PasswordEncoder;
//    import com.example.recommendershop.service.emailMessage.EmailService;
    import com.example.recommendershop.service.emailMessage.EmailService;
    import com.example.recommendershop.validation.Validator;
    import jakarta.servlet.http.HttpSession;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.http.HttpStatus;
    import org.springframework.stereotype.Service;

    import java.util.*;

    @Service
    public class UserServiceImpl implements UserService {
        @Value("${app.frontend.url}")
        private String frontendUrl;
        private final UserRepository userRepository;
        private final UserGroupRepository userGroupRepository;
        private final RoleGroupRepository roleGroupRepository;
        private final RoleRepository roleRepository;
        private final HttpSession httpSession;
        private final UserMapper userMapper;
        private final PasswordEncoder passwordEncoder;
        private final Validator validator;
        private final EmailService emailService;
        public UserServiceImpl(UserRepository userRepository,UserGroupRepository userGroupRepository, RoleGroupRepository roleGroupRepository, RoleRepository roleRepository,HttpSession httpSession, UserMapper userMapper, PasswordEncoder passwordEncoder, Validator validator, EmailService emailService) {
            this.userRepository = userRepository;
            this.userGroupRepository = userGroupRepository;
            this.roleGroupRepository = roleGroupRepository;
            this.roleRepository = roleRepository;
            this.httpSession = httpSession;
            this.userMapper = userMapper;
            this.passwordEncoder = passwordEncoder;
            this.validator = validator;
            this.emailService = emailService;
        }

        @Override
        public ResponseData<?> register(UserRequest userRequest) {
            validator.checkEntityExists(userRepository.findByName(userRequest.getName()), HttpStatus.BAD_REQUEST, "Người dùng đã tồn tại");
            validator.checkEntityExists(userRepository.getUserByEmail(userRequest.getEmail()), HttpStatus.BAD_REQUEST, "Email đã được sử dụng");
            User user = userMapper.toEntity(userRequest);
            String passwordEncode = passwordEncoder.encode(user.getPassword());
            user.setPassword(passwordEncode);
            userRepository.save(user);
            return new ResponseData<>(HttpStatus.OK.value(), "Tao tai khoan thanh cong");
        }

        @Override
        public ResponseData<?> login(LoginRequest loginRequest) {

            if (loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
                throw new MasterException(HttpStatus.BAD_REQUEST, "Email và mật khẩu không được để trống");
            }

            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new MasterException(HttpStatus.UNAUTHORIZED, "Email hoặc mật khẩu sai"));

            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                throw new MasterException(HttpStatus.UNAUTHORIZED, "Email hoặc mật khẩu sai");
            }

            // 🔥 LẤY ROLES
            Set<UserGroup> userGroups = userGroupRepository.findByUsers_UserId(user.getUserId());
            Set<Role> roles = new HashSet<>();

            for (UserGroup userGroup : userGroups) {
                Set<RoleGroup> roleGroups = roleGroupRepository.findByUserGroups(userGroup);
                for (RoleGroup roleGroup : roleGroups) {
                    roles.addAll(roleRepository.findByRoleGroups(roleGroup));
                }
            }

            List<String> roleNames = roles.stream()
                    .map(Role::getName)
                    .toList();

            httpSession.setAttribute("UserId", user.getUserId());

            // 🔥 TRẢ VỀ DTO
            Map<String, Object> result = new HashMap<>();
            result.put("userId", user.getUserId());
            result.put("roles", roleNames);

            return new ResponseData<>(HttpStatus.OK.value(), "Đăng nhập thành công", result);
        }
        @Override
        public void logout() {
            httpSession.invalidate();
        }
        public UserInfor detail(UUID userId){
            UUID sessionUserId = (UUID) httpSession.getAttribute("UserId");

            if (sessionUserId == null) {
                throw new MasterException(HttpStatus.UNAUTHORIZED, "Bạn cần đăng nhập");
            }

            if (!sessionUserId.equals(userId)) {
                throw new MasterException(HttpStatus.FORBIDDEN, "Không có quyền");
            }

            return userMapper.toDao(userRepository.getReferenceById(userId));
        }

        public UserInfor update(UUID userId, UserEditRequest userEditRequest) {

            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new MasterException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
            userMapper.update(userEditRequest, existingUser);

            User updatedUser = userRepository.save(existingUser);

            httpSession.setAttribute("UserId", updatedUser.getUserId());
            httpSession.setAttribute("UserName", updatedUser.getName());

            return userMapper.toDao(updatedUser);
        }
        private boolean verifyOldPassword(UUID userId, String oldPassword) {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                return passwordEncoder.matches(oldPassword, user.getPassword());
            }
            return false;
        }
        public ResponseData<?> changePassword(UUID userId, ChangePasswordRequest changePasswordRequest){

            if(!verifyOldPassword(userId, changePasswordRequest.getOldPassword())){
                throw new MasterException(HttpStatus.BAD_REQUEST, "Mật khẩu cũ không chính xác");
            }
            Optional<User> userOptional = userRepository.findById(userId);
            if(userOptional.isPresent()){
                User user = userOptional.get();
                user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
                userRepository.save(user);
            }
            else {
                throw new MasterException(HttpStatus.NOT_FOUND, "không tìm thấy người dùng");
            }
            return new ResponseData<>(HttpStatus.OK.value(), "Đổi mật khẩu thành công");
        }
        @Override
        public ResponseData<?> forgotPassword(String email) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new MasterException(HttpStatus.NOT_FOUND, "Email không tồn tại"));

            String token = UUID.randomUUID().toString();

            user.setResetToken(token);
            user.setResetTokenExpiry(new Date(System.currentTimeMillis() + 10 * 60 * 1000)); // 10 phút

            userRepository.save(user);

            String link = frontendUrl + "/reset-password?token=" + token;
            // TODO: gửi email
            emailService.sendResetPassword(user.getEmail(), link);
            return new ResponseData<>(200, "Đã gửi link reset mật khẩu vào mail của bạn");
        }
        @Override
        public ResponseData<?> resetPassword(ResetPasswordRequest request) {

            User user = userRepository.findByResetToken(request.getToken())
                    .orElseThrow(() -> new MasterException(HttpStatus.BAD_REQUEST, "Token không hợp lệ"));

            if (user.getResetTokenExpiry().before(new Date())) {
                throw new MasterException(HttpStatus.BAD_REQUEST, "Token đã hết hạn");
            }

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));

            // clear token
            user.setResetToken(null);
            user.setResetTokenExpiry(null);

            userRepository.save(user);

            return new ResponseData<>(200, "Đổi mật khẩu thành công");
        }
    }
