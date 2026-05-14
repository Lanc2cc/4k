package com.movie.controller;

import com.movie.common.PageResult;
import com.movie.common.Result;
import com.movie.entity.WatchHistory;
import com.movie.service.WatchHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history")
public class WatchHistoryController {

    @Autowired
    private WatchHistoryService watchHistoryService;

    @PostMapping("/add")
    public Result<?> addHistory(HttpServletRequest request, @RequestParam Long movieId) {
        Long userId = (Long) request.getAttribute("userId");
        return watchHistoryService.addHistory(userId, movieId);
    }

    @GetMapping("/list")
    public Result<PageResult<WatchHistory>> getHistoryList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer size) {
        Long userId = (Long) request.getAttribute("userId");
        return watchHistoryService.getHistoryList(userId, page, size);
    }

    @DeleteMapping("/clear")
    public Result<?> clearHistory(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return watchHistoryService.clearHistory(userId);
    }
}
