package com.example.recommendershop.dto.user.response;

import com.example.recommendershop.enums.Sex;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.UUID;

@Getter
@Setter
public class UserInfor {
    private UUID userId;
    private String name;
    private String avatar;
    private String email;
    private String phone;
    private String address;
    private Date date;
    private Integer age;
    private Sex sex;
}
