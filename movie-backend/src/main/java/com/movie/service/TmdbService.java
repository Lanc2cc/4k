package com.movie.service;

import com.movie.common.Result;

public interface TmdbService {

    Result<?> syncPopularMovies(int count);

    Result<?> syncTopRatedMovies(int count);
}
