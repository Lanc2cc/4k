package com.movie.controller;

import com.movie.common.Result;
import com.movie.entity.*;
import com.movie.service.AdminService;
import com.movie.service.TmdbService;
import com.movie.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private TmdbService tmdbService;

    /** 校验管理员权限 */
    private boolean isAdmin(HttpServletRequest request) {
        String token = (String) request.getAttribute("token");
        if (token == null) return false;
        Integer role = JwtUtils.getRole(token);
        return role != null && role == 1;
    }

    @GetMapping("/dashboard")
    public Result<?> getDashboard(HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        return adminService.getDashboard();
    }

    // ========== 用户管理 ==========
    @GetMapping("/user/list")
    public Result<?> getUserList(HttpServletRequest request,
                                 @RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer size) {
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        return adminService.getUserList(page, size);
    }

    @PutMapping("/user/status")
    public Result<?> updateUserStatus(HttpServletRequest request,
                                      @RequestParam Long userId, @RequestParam Integer status) {
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        return adminService.updateUserStatus(userId, status);
    }

    // ========== 电影管理 ==========
    @GetMapping("/movie/list")
    public Result<?> getMovieList(HttpServletRequest request,
                                  @RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "10") Integer size) {
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        return adminService.getMovieList(page, size);
    }

    @PostMapping("/movie/add")
    public Result<?> addMovie(HttpServletRequest request, @RequestBody Movie movie) {
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        return adminService.addMovie(movie);
    }

    @PutMapping("/movie/update")
    public Result<?> updateMovie(HttpServletRequest request, @RequestBody Movie movie) {
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        return adminService.updateMovie(movie);
    }

    @DeleteMapping("/movie/delete/{id}")
    public Result<?> deleteMovie(HttpServletRequest request, @PathVariable Long id) {
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        return adminService.deleteMovie(id);
    }

    // ========== 播放源管理 ==========
    @PostMapping("/source/add")
    public Result<?> addSource(HttpServletRequest request, @RequestBody MovieSource source) {
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        return adminService.addMovieSource(source);
    }

    @DeleteMapping("/source/delete/{id}")
    public Result<?> deleteSource(HttpServletRequest request, @PathVariable Long id) {
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        return adminService.deleteMovieSource(id);
    }

    // ========== 评论管理 ==========
    @GetMapping("/comment/list")
    public Result<?> getCommentList(HttpServletRequest request,
                                    @RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer size) {
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        return adminService.getCommentList(page, size);
    }

    @PutMapping("/comment/status")
    public Result<?> updateCommentStatus(HttpServletRequest request,
                                         @RequestParam Long commentId, @RequestParam Integer status) {
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        return adminService.updateCommentStatus(commentId, status);
    }

    @DeleteMapping("/comment/delete/{id}")
    public Result<?> deleteComment(HttpServletRequest request, @PathVariable Long id) {
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        return adminService.deleteComment(id);
    }

    // ========== 分类管理 ==========
    @PostMapping("/category/add")
    public Result<?> addCategory(HttpServletRequest request, @RequestBody Category category) {
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        return adminService.addCategory(category);
    }

    @DeleteMapping("/category/delete/{id}")
    public Result<?> deleteCategory(HttpServletRequest request, @PathVariable Long id) {
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        return adminService.deleteCategory(id);
    }

    // ========== TMDB 同步 ==========
    @PostMapping("/tmdb/sync/popular")
    public Result<?> syncPopular(HttpServletRequest request, @RequestParam(defaultValue = "50") Integer count) {
        // 同步接口仅管理员可调用，避免普通用户越权操作
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        // 调用 TMDB 同步服务执行热门电影导入
        return tmdbService.syncPopularMovies(count);
    }

    @PostMapping("/tmdb/sync/top_rated")
    public Result<?> syncTopRated(HttpServletRequest request, @RequestParam(defaultValue = "50") Integer count) {
        // 同步接口仅管理员可调用，避免普通用户越权操作
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        // 调用 TMDB 同步服务执行高分电影导入
        return tmdbService.syncTopRatedMovies(count);
    }

    // ========== API日志 ==========
    @GetMapping("/api-log/list")
    public Result<?> getApiLogs(HttpServletRequest request,
                                @RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer size) {
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        return adminService.getApiLogs(page, size);
    }
}
