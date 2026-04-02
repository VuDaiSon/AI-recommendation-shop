    package com.example.recommendershop.service.user;

    import com.example.recommendershop.dto.ResponseData;
    import com.example.recommendershop.dto.user.request.ChangePasswordRequest;
    import com.example.recommendershop.dto.user.request.LoginRequest;
    import com.example.recommendershop.dto.user.request.UserRequest;
    import com.example.recommendershop.dto.user.response.UserInfor;
    import com.example.recommendershop.entity.User;
    import com.example.recommendershop.exception.MasterException;
    import com.example.recommendershop.mapper.UserMapper;
    import com.example.recommendershop.repository.UserRepository;
    import com.example.recommendershop.config.PasswordEncoder;
//    import com.example.recommendershop.service.emailMessage.EmailService;
    import com.example.recommendershop.validation.Validator;
    import jakarta.servlet.http.HttpSession;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.stereotype.Service;

    import java.util.Optional;
    import java.util.UUID;

    @Service
    public class UserServiceImpl implements UserService {
        @Autowired
        private final UserRepository userRepository;
        private final HttpSession httpSession;
        private final UserMapper userMapper;
        private final PasswordEncoder passwordEncoder;
        private final Validator validator;

        public UserServiceImpl(UserRepository userRepository, HttpSession httpSession, UserMapper userMapper, PasswordEncoder passwordEncoder, Validator validator) {
            this.userRepository = userRepository;
            this.httpSession = httpSession;
            this.userMapper = userMapper;
            this.passwordEncoder = passwordEncoder;
            this.validator = validator;
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
            Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
            User user = userOptional.get();
            if (userOptional.isEmpty() || !passwordEncoder.matches(loginRequest.getPassword(),user.getPassword())) {
                throw new MasterException(HttpStatus.UNAUTHORIZED, "Email hoặc mật khẩu sai");
            }
            UUID token = user.getUserId();
            httpSession.setAttribute("UserId", user.getUserId());
            httpSession.setAttribute("UserName", user.getName());
            httpSession.setAttribute("AuthToken", token);
            return new ResponseData<>(HttpStatus.OK.value(), "Đăng nhập thành công", token);
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

        public UserInfor update(UUID userId, UserRequest userRequest) {

            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new MasterException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
            existingUser.setName(userRequest.getName());
            existingUser.setPhone(userRequest.getPhone());
            existingUser.setAddress(userRequest.getAddress());

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

//        private String generateRandomPassword() {
//            String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
//            String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
//            String numbers = "0123456789";
//            String specialChars = "!@#$%&_/?";
//            String allChars = upperCaseLetters + lowerCaseLetters + numbers + specialChars;
//            Random random = new Random();
//            StringBuilder password = new StringBuilder();
//            for (int i = 0; i < 10; i++) {
//                password.append(allChars.charAt(random.nextInt(allChars.length())));
//            }
//            return password.toString();
//        }
//        @Override
//        public ResponseData<?> forgotPassword(String email) {
//            Optional<User> userOptional = userRepository.findByEmail(email);
//            if (userOptional.isPresent()) {
//                User user = userOptional.get();
//
//                String newPassword = generateRandomPassword();
//
//                user.setPassword((newPassword));
//                userRepository.save(user);
//
//                EmailMessage emailMessage = new EmailMessage();
//                emailMessage.setTo(email);
//                emailMessage.setSubject("Reset password");
//                emailMessage.setBody("Your new password: " + newPassword);
//                emailService.sendEmail(emailMessage);
//            } else {
//                throw new MasterException(HttpStatus.NOT_FOUND, "không tìm thấy tài khoản");
//            }
//            return new ResponseData<>(HttpStatus.OK.value(), "đã gửi mật khẩu mới đến email của bạn");
//        }


    }
