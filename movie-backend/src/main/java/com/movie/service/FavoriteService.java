package com.movie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.movie.common.PageResult;
import com.movie.common.Result;
import com.movie.entity.Favorite;
import com.movie.entity.Movie;

public interface FavoriteService extends IService<Favorite> {

    Result<?> addFavorite(Long userId, Long movieId);

    Result<?> removeFavorite(Long userId, Long movieId);

    Result<PageResult<Movie>> getFavoriteList(Long userId, Integer page, Integer size);

    Result<Boolean> isFavorite(Long userId, Long movieId);
}
