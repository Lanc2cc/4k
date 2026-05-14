package com.movie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.movie.common.Result;
import com.movie.dto.LoginDTO;
import com.movie.dto.RegisterDTO;
import com.movie.dto.UserUpdateDTO;
import com.movie.entity.User;
import com.movie.mapper.UserMapper;
import com.movie.service.UserService;
import com.movie.utils.JwtUtils;
import com.movie.utils.PasswordUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public Result<?> register(RegisterDTO dto) {
        // 注册前先完成基本参数校验，避免空用户名和弱密码进入业务流程
        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            return Result.error("密码长度不能少于6位");
        }

        // 检查用户名是否已存在，避免重复注册
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername().trim());
        if (this.count(wrapper) > 0) {
            return Result.error("用户名已存在");
        }

        // 构建用户对象并完成密码哈希、默认角色和状态设置
        User user = new User();
        user.setUsername(dto.getUsername().trim());
        user.setPassword(PasswordUtils.hashPassword(dto.getPassword()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setRole(0);
        user.setStatus(1);
        this.save(user);

        return Result.success("注册成功");
    }

    @Override
    public Result<?> login(LoginDTO dto) {
        // 登录前先校验用户名和密码是否为空
        if (dto.getUsername() == null || dto.getPassword() == null) {
            return Result.error("用户名和密码不能为空");
        }

        // 根据用户名查询用户信息
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername().trim());
        User user = this.getOne(wrapper);

        // 依次判断用户是否存在、是否被禁用、密码是否正确
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (user.getStatus() == 0) {
            return Result.error("账号已被禁用");
        }
        if (!PasswordUtils.checkPassword(dto.getPassword(), user.getPassword())) {
            return Result.error("密码错误");
        }

        // 登录成功后生成 JWT，并返回前端需要的用户基础信息
        String token = JwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        data.put("avatar", user.getAvatar());
        data.put("role", user.getRole());
        return Result.success(data);
    }

    @Override
    public Result<?> getUserInfo(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 返回用户信息时隐藏密码字段，避免敏感信息泄露
        user.setPassword(null);
        return Result.success(user);
    }

    @Override
    public Result<?> updateUserInfo(Long userId, UserUpdateDTO dto) {
        User user = this.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 按需更新昵称、邮箱和头像信息
        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }

        // 修改密码时必须先校验原密码
        if (dto.getNewPassword() != null && !dto.getNewPassword().isEmpty()) {
            if (dto.getOldPassword() == null || !PasswordUtils.checkPassword(dto.getOldPassword(), user.getPassword())) {
                return Result.error("原密码错误");
            }
            user.setPassword(PasswordUtils.hashPassword(dto.getNewPassword()));
        }

        // 更新用户数据并返回结果
        this.updateById(user);
        return Result.success("更新成功");
    }
}
