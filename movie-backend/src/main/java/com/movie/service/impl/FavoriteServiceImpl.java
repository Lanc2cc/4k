package com.movie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.movie.common.PageResult;
import com.movie.common.Result;
import com.movie.entity.Favorite;
import com.movie.entity.Movie;
import com.movie.mapper.FavoriteMapper;
import com.movie.mapper.MovieMapper;
import com.movie.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Autowired
    private MovieMapper movieMapper;

    @Override
    public Result<?> addFavorite(Long userId, Long movieId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getMovieId, movieId);
        if (this.count(wrapper) > 0) {
            return Result.error("已收藏过该电影");
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setMovieId(movieId);
        this.save(favorite);
        return Result.success("收藏成功");
    }

    @Override
    public Result<?> removeFavorite(Long userId, Long movieId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getMovieId, movieId);
        this.remove(wrapper);
        return Result.success("取消收藏");
    }

    @Override
    public Result<PageResult<Movie>> getFavoriteList(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).orderByDesc(Favorite::getCreateTime);
        Page<Favorite> favPage = this.page(new Page<>(page, size), wrapper);

        List<Long> movieIds = favPage.getRecords().stream()
                .map(Favorite::getMovieId).collect(Collectors.toList());

        List<Movie> movies = movieIds.isEmpty() ? List.of() : movieMapper.selectBatchIds(movieIds);
        return Result.success(new PageResult<>(movies, favPage.getTotal(), favPage.getSize(), favPage.getCurrent()));
    }

    @Override
    public Result<Boolean> isFavorite(Long userId, Long movieId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getMovieId, movieId);
        return Result.success(this.count(wrapper) > 0);
    }
}
