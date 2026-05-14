package com.movie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.movie.common.PageResult;
import com.movie.common.Result;
import com.movie.entity.Movie;
import com.movie.entity.WatchHistory;
import com.movie.mapper.MovieMapper;
import com.movie.mapper.WatchHistoryMapper;
import com.movie.service.WatchHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WatchHistoryServiceImpl extends ServiceImpl<WatchHistoryMapper, WatchHistory> implements WatchHistoryService {

    @Autowired
    private MovieMapper movieMapper;

    @Override
    public Result<?> addHistory(Long userId, Long movieId) {
        LambdaQueryWrapper<WatchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WatchHistory::getUserId, userId).eq(WatchHistory::getMovieId, movieId);
        WatchHistory existing = this.getOne(wrapper);

        if (existing != null) {
            existing.setUpdateTime(LocalDateTime.now());
            this.updateById(existing);
        } else {
            WatchHistory history = new WatchHistory();
            history.setUserId(userId);
            history.setMovieId(movieId);
            this.save(history);
        }
        return Result.success();
    }

    @Override
    public Result<PageResult<WatchHistory>> getHistoryList(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<WatchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WatchHistory::getUserId, userId).orderByDesc(WatchHistory::getUpdateTime);
        Page<WatchHistory> result = this.page(new Page<>(page, size), wrapper);

        List<Long> movieIds = result.getRecords().stream().map(WatchHistory::getMovieId).collect(Collectors.toList());
        if (!movieIds.isEmpty()) {
            List<Movie> movies = movieMapper.selectBatchIds(movieIds);
            Map<Long, Movie> movieMap = movies.stream().collect(Collectors.toMap(Movie::getId, m -> m));
            result.getRecords().forEach(h -> {
                Movie m = movieMap.get(h.getMovieId());
                if (m != null) {
                    h.setMovieName(m.getMovieName());
                    h.setPosterPath(m.getPosterPath());
                }
            });
        }

        return Result.success(new PageResult<>(result.getRecords(), result.getTotal(), result.getSize(), result.getCurrent()));
    }

    @Override
    public Result<?> clearHistory(Long userId) {
        LambdaQueryWrapper<WatchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WatchHistory::getUserId, userId);
        this.remove(wrapper);
        return Result.success("已清空观看历史");
    }
}
