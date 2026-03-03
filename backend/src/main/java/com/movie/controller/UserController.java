package com.movie.controller;

import com.movie.common.Result;
import com.movie.entity.User;
import com.movie.entity.UserFavorite;
import com.movie.entity.UserHistory;
import com.movie.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<User> register(@RequestBody Map<String, String> params) {
        try {
            User user = userService.register(
                    params.get("username"),
                    params.get("password"),
                    params.get("nickname"));
            return Result.success(user);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody Map<String, String> params) {
        try {
            User user = userService.login(params.get("username"), params.get("password"));
            return Result.success(user);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取观看历史
     */
    @GetMapping("/history")
    public Result<List<UserHistory>> getHistory(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        return Result.success(userService.getHistory(userId, limit));
    }

    /**
     * 保存/更新观看进度
     */
    @PostMapping("/history")
    public Result<Void> saveHistory(@RequestBody Map<String, Object> params) {
        userService.saveHistory(
                Long.valueOf(params.get("userId").toString()),
                Integer.valueOf(params.get("movieId").toString()),
                params.get("progress") != null ? Integer.valueOf(params.get("progress").toString()) : 0,
                params.get("sourceId") != null ? Long.valueOf(params.get("sourceId").toString()) : null);
        return Result.success();
    }

    /**
     * 获取收藏列表
     */
    @GetMapping("/favorite")
    public Result<List<UserFavorite>> getFavorites(@RequestParam Long userId) {
        return Result.success(userService.getFavorites(userId));
    }

    /**
     * 添加收藏
     */
    @PostMapping("/favorite")
    public Result<Void> addFavorite(@RequestBody Map<String, Object> params) {
        try {
            userService.addFavorite(
                    Long.valueOf(params.get("userId").toString()),
                    Integer.valueOf(params.get("movieId").toString()));
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/favorite")
    public Result<Void> removeFavorite(
            @RequestParam Long userId,
            @RequestParam Integer movieId) {
        userService.removeFavorite(userId, movieId);
        return Result.success();
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/favorite/check")
    public Result<Boolean> isFavorite(
            @RequestParam Long userId,
            @RequestParam Integer movieId) {
        return Result.success(userService.isFavorite(userId, movieId));
    }
}
