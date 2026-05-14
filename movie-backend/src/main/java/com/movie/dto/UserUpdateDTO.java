package com.movie.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String nickname;
    private String email;
    private String avatar;
    private String oldPassword;
    private String newPassword;
}
