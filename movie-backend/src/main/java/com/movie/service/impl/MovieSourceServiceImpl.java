package com.movie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.movie.entity.MovieSource;
import com.movie.mapper.MovieSourceMapper;
import com.movie.service.MovieSourceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieSourceServiceImpl extends ServiceImpl<MovieSourceMapper, MovieSource> implements MovieSourceService {

    @Override
    public List<MovieSource> getSourcesByMovieId(Long movieId) {
        LambdaQueryWrapper<MovieSource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MovieSource::getMovieId, movieId);
        return this.list(wrapper);
    }
}
