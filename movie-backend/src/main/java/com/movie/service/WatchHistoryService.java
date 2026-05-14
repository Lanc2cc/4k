package com.movie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.movie.common.PageResult;
import com.movie.common.Result;
import com.movie.entity.WatchHistory;

public interface WatchHistoryService extends IService<WatchHistory> {

    Result<?> addHistory(Long userId, Long movieId);

    Result<PageResult<WatchHistory>> getHistoryList(Long userId, Integer page, Integer size);

    Result<?> clearHistory(Long userId);
}
