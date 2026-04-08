package com.example.recommendershop.dto.user.request;

import com.example.recommendershop.enums.Sex;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEditRequest {
    @NotNull
    private String name;
    @NotNull
    private String avatar;
    @NotNull
    @Email(message = "Email không hợp lệ")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@gmail\\.com$")
    private String email;
    @NotNull
    private String phone;
    @NotNull
    private String address;
    @NotNull
    private String password;

    @NotNull
    private Integer age;
    @NotNull
    private Sex sex;
}
