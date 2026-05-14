package com.movie.controller;

import com.movie.common.Result;
import com.movie.dto.LoginDTO;
import com.movie.dto.RegisterDTO;
import com.movie.dto.UserUpdateDTO;
import com.movie.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterDTO dto) {
        return userService.register(dto);
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginDTO dto) {
        return userService.login(dto);
    }

    @GetMapping("/info")
    public Result<?> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return userService.getUserInfo(userId);
    }

    @PutMapping("/update")
    public Result<?> updateUserInfo(HttpServletRequest request, @RequestBody UserUpdateDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        return userService.updateUserInfo(userId, dto);
    }
}
