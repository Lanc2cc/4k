package com.movie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.movie.common.Result;
import com.movie.dto.LoginDTO;
import com.movie.dto.RegisterDTO;
import com.movie.dto.UserUpdateDTO;
import com.movie.entity.User;

public interface UserService extends IService<User> {

    Result<?> register(RegisterDTO dto);

    Result<?> login(LoginDTO dto);

    Result<?> getUserInfo(Long userId);

    Result<?> updateUserInfo(Long userId, UserUpdateDTO dto);
}
