package com.movie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.movie.common.PageResult;
import com.movie.common.Result;
import com.movie.entity.*;
import com.movie.mapper.*;
import com.movie.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MovieMapper movieMapper;
    @Autowired
    private MovieSourceMapper movieSourceMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ApiLogMapper apiLogMapper;
    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    public Result<PageResult<User>> getUserList(Integer page, Integer size) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> result = userMapper.selectPage(pageParam, wrapper);
        result.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal(), result.getSize(), result.getCurrent()));
    }

    @Override
    public Result<?> updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) return Result.error("用户不存在");
        user.setStatus(status);
        userMapper.updateById(user);
        return Result.success("更新成功");
    }

    @Override
    public Result<PageResult<Movie>> getMovieList(Integer page, Integer size) {
        Page<Movie> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Movie> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Movie::getCreateTime);
        Page<Movie> result = movieMapper.selectPage(pageParam, wrapper);
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal(), result.getSize(), result.getCurrent()));
    }

    @Override
    public Result<?> addMovie(Movie movie) {
        movie.setStatus(1);
        movieMapper.insert(movie);
        return Result.success("添加成功");
    }

    @Override
    public Result<?> updateMovie(Movie movie) {
        movieMapper.updateById(movie);
        return Result.success("更新成功");
    }

    @Override
    public Result<?> deleteMovie(Long movieId) {
        movieMapper.deleteById(movieId);
        // 同时删除播放源
        LambdaQueryWrapper<MovieSource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MovieSource::getMovieId, movieId);
        movieSourceMapper.delete(wrapper);
        return Result.success("删除成功");
    }

    @Override
    public Result<?> addMovieSource(MovieSource source) {
        movieSourceMapper.insert(source);
        return Result.success("添加播放源成功");
    }

    @Override
    public Result<?> deleteMovieSource(Long sourceId) {
        movieSourceMapper.deleteById(sourceId);
        return Result.success("删除播放源成功");
    }

    @Override
    public Result<PageResult<Comment>> getCommentList(Integer page, Integer size) {
        Page<Comment> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Comment::getCreateTime);
        Page<Comment> result = commentMapper.selectPage(pageParam, wrapper);
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal(), result.getSize(), result.getCurrent()));
    }

    @Override
    public Result<?> updateCommentStatus(Long commentId, Integer status) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) return Result.error("评论不存在");
        comment.setStatus(status);
        commentMapper.updateById(comment);
        return Result.success("更新成功");
    }

    @Override
    public Result<?> deleteComment(Long commentId) {
        commentMapper.deleteById(commentId);
        return Result.success("删除成功");
    }

    @Override
    public Result<?> addCategory(Category category) {
        categoryMapper.insert(category);
        return Result.success("添加成功");
    }

    @Override
    public Result<?> deleteCategory(Long categoryId) {
        categoryMapper.deleteById(categoryId);
        return Result.success("删除成功");
    }

    @Override
    public Result<PageResult<ApiLog>> getApiLogs(Integer page, Integer size) {
        Page<ApiLog> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ApiLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ApiLog::getCreateTime);
        Page<ApiLog> result = apiLogMapper.selectPage(pageParam, wrapper);
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal(), result.getSize(), result.getCurrent()));
    }

    @Override
    public Result<?> getDashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("userCount", userMapper.selectCount(null));
        data.put("movieCount", movieMapper.selectCount(null));
        data.put("commentCount", commentMapper.selectCount(null));
        data.put("favoriteCount", favoriteMapper.selectCount(null));
        return Result.success(data);
    }
}
