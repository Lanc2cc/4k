package com.movie.service;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.movie.entity.User;
import com.movie.entity.UserFavorite;
import com.movie.entity.UserHistory;
import com.movie.mapper.UserFavoriteMapper;
import com.movie.mapper.UserHistoryMapper;
import com.movie.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserHistoryMapper userHistoryMapper;
    private final UserFavoriteMapper userFavoriteMapper;

    /**
     * 注册
     */
    public User register(String username, String password, String nickname) {
        // 检查用户名是否已存在
        User exists = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (exists != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(DigestUtil.md5Hex(password));
        user.setNickname(nickname != null ? nickname : username);
        user.setStatus(1);
        userMapper.insert(user);
        user.setPassword(null); // 不返回密码
        return user;
    }

    /**
     * 登录
     */
    public User login(String username, String password) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!DigestUtil.md5Hex(password).equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        user.setPassword(null);
        return user;
    }

    // ========== 观看历史 ==========

    /**
     * 获取用户观看历史
     */
    public List<UserHistory> getHistory(Long userId, int limit) {
        return userHistoryMapper.selectList(
                new LambdaQueryWrapper<UserHistory>()
                        .eq(UserHistory::getUserId, userId)
                        .orderByDesc(UserHistory::getWatchedAt)
                        .last("LIMIT " + limit));
    }

    /**
     * 记录/更新观看历史
     */
    public void saveHistory(Long userId, Integer movieId, Integer progress, Long sourceId) {
        UserHistory history = userHistoryMapper.selectOne(
                new LambdaQueryWrapper<UserHistory>()
                        .eq(UserHistory::getUserId, userId)
                        .eq(UserHistory::getMovieId, movieId));
        if (history != null) {
            history.setWatchProgress(progress);
            history.setSourceId(sourceId);
            history.setWatchedAt(LocalDateTime.now());
            userHistoryMapper.updateById(history);
        } else {
            history = new UserHistory();
            history.setUserId(userId);
            history.setMovieId(movieId);
            history.setWatchProgress(progress);
            history.setSourceId(sourceId);
            userHistoryMapper.insert(history);
        }
    }

    // ========== 收藏 ==========

    /**
     * 获取用户收藏列表
     */
    public List<UserFavorite> getFavorites(Long userId) {
        return userFavoriteMapper.selectList(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
                        .orderByDesc(UserFavorite::getCreatedAt));
    }

    /**
     * 添加收藏
     */
    public void addFavorite(Long userId, Integer movieId) {
        UserFavorite exists = userFavoriteMapper.selectOne(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
                        .eq(UserFavorite::getMovieId, movieId));
        if (exists != null) {
            throw new RuntimeException("已收藏该电影");
        }
        UserFavorite fav = new UserFavorite();
        fav.setUserId(userId);
        fav.setMovieId(movieId);
        userFavoriteMapper.insert(fav);
    }

    /**
     * 取消收藏
     */
    public void removeFavorite(Long userId, Integer movieId) {
        userFavoriteMapper.delete(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
                        .eq(UserFavorite::getMovieId, movieId));
    }

    /**
     * 检查是否已收藏
     */
    public boolean isFavorite(Long userId, Integer movieId) {
        return userFavoriteMapper.selectCount(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
                        .eq(UserFavorite::getMovieId, movieId)) > 0;
    }
}
