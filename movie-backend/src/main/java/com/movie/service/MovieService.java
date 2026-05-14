package com.movie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.movie.common.PageResult;
import com.movie.common.Result;
import com.movie.entity.Movie;

public interface MovieService extends IService<Movie> {

    Result<PageResult<Movie>> getMovieList(Integer page, Integer size, Long categoryId);

    Result<Movie> getMovieDetail(Long id);

    Result<PageResult<Movie>> searchMovies(String keyword, Integer page, Integer size);

    Result<PageResult<Movie>> getHotMovies(Integer page, Integer size);
}
