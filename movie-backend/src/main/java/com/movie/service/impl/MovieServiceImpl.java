package com.movie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.movie.common.PageResult;
import com.movie.common.Result;
import com.movie.entity.Category;
import com.movie.entity.Movie;
import com.movie.mapper.CategoryMapper;
import com.movie.mapper.MovieMapper;
import com.movie.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl extends ServiceImpl<MovieMapper, Movie> implements MovieService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Result<PageResult<Movie>> getMovieList(Integer page, Integer size, Long categoryId) {
        Page<Movie> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Movie> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Movie::getStatus, 1);
        if (categoryId != null) {
            wrapper.eq(Movie::getCategoryId, categoryId);
        }
        wrapper.orderByDesc(Movie::getCreateTime);
        Page<Movie> result = this.page(pageParam, wrapper);
        fillCategoryName(result.getRecords());
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal(), result.getSize(), result.getCurrent()));
    }

    @Override
    public Result<Movie> getMovieDetail(Long id) {
        // 先从 Redis 中读取缓存，减少数据库重复查询
        String cacheKey = "movie:detail:" + id;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof Movie movie) {
            return Result.success(movie);
        }

        // 缓存未命中时查询数据库
        Movie movie = this.getById(id);
        if (movie == null) {
            return Result.error(404, "电影不存在");
        }

        // 补充分类名称，方便详情页直接展示
        if (movie.getCategoryId() != null) {
            Category category = categoryMapper.selectById(movie.getCategoryId());
            if (category != null) {
                movie.setCategoryName(category.getName());
            }
        }

        // 将查询结果写回缓存，设置 1 小时过期时间
        redisTemplate.opsForValue().set(cacheKey, movie, 1, TimeUnit.HOURS);
        return Result.success(movie);
    }

    @Override
    public Result<PageResult<Movie>> searchMovies(String keyword, Integer page, Integer size) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getMovieList(page, size, null);
        }

        String cacheKey = "movie:search:" + keyword.trim() + ":" + page + ":" + size;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof PageResult) {
            @SuppressWarnings("unchecked")
            PageResult<Movie> pr = (PageResult<Movie>) cached;
            return Result.success(pr);
        }

        Page<Movie> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Movie> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Movie::getStatus, 1);
        wrapper.and(w -> w.like(Movie::getMovieName, keyword.trim())
                .or().like(Movie::getOriginalTitle, keyword.trim())
                .or().like(Movie::getOverview, keyword.trim()));
        wrapper.orderByDesc(Movie::getPopularity);
        Page<Movie> result = this.page(pageParam, wrapper);
        fillCategoryName(result.getRecords());

        PageResult<Movie> pageResult = new PageResult<>(result.getRecords(), result.getTotal(), result.getSize(), result.getCurrent());
        redisTemplate.opsForValue().set(cacheKey, pageResult, 5, TimeUnit.MINUTES);
        return Result.success(pageResult);
    }

    @Override
    public Result<PageResult<Movie>> getHotMovies(Integer page, Integer size) {
        String cacheKey = "movie:hot:" + page + ":" + size;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof PageResult) {
            @SuppressWarnings("unchecked")
            PageResult<Movie> pr = (PageResult<Movie>) cached;
            return Result.success(pr);
        }

        Page<Movie> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Movie> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Movie::getStatus, 1);
        wrapper.orderByDesc(Movie::getPopularity);
        Page<Movie> result = this.page(pageParam, wrapper);
        fillCategoryName(result.getRecords());

        PageResult<Movie> pageResult = new PageResult<>(result.getRecords(), result.getTotal(), result.getSize(), result.getCurrent());
        redisTemplate.opsForValue().set(cacheKey, pageResult, 30, TimeUnit.MINUTES);
        return Result.success(pageResult);
    }

    private void fillCategoryName(List<Movie> movies) {
        if (movies == null || movies.isEmpty()) return;
        List<Category> categories = categoryMapper.selectList(null);
        Map<Long, String> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));
        movies.forEach(m -> {
            if (m.getCategoryId() != null) {
                m.setCategoryName(categoryMap.get(m.getCategoryId()));
            }
        });
    }
}
