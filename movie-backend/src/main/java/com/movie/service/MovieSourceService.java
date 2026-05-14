package com.movie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.movie.entity.MovieSource;
import java.util.List;

public interface MovieSourceService extends IService<MovieSource> {

    List<MovieSource> getSourcesByMovieId(Long movieId);
}
