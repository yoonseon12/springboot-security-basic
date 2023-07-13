package com.cos.security1.model;

import lombok.Data;

@Data
public class UserDto {
    private String email;
    private String password;
    private String username;

    public static User toEntity(UserDto userDto, String role) {
        return User.builder()
                .email(userDto.email)
                .password(userDto.password)
                .username(userDto.username)
                .role(role)
                .build();
    }
}
