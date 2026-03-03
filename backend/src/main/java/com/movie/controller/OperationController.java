package com.movie.controller;

import com.movie.common.Result;
import com.movie.entity.Advertisement;
import com.movie.entity.Activity;
import com.movie.entity.Danmu;
import com.movie.entity.HotSearch;
import com.movie.service.OperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OperationController {

    private final OperationService operationService;

    /**
     * 获取广告列表
     */
    @GetMapping("/ad/list")
    public Result<List<Advertisement>> getAds(
            @RequestParam(required = false) String position) {
        return Result.success(operationService.getAds(position));
    }

    /**
     * 获取热搜词
     */
    @GetMapping("/hot-search")
    public Result<List<HotSearch>> getHotSearches() {
        return Result.success(operationService.getHotSearches());
    }

    /**
     * 获取运营活动列表
     */
    @GetMapping("/activity/list")
    public Result<List<Activity>> getActivities() {
        return Result.success(operationService.getActivities());
    }

    /**
     * 获取电影弹幕
     */
    @GetMapping("/danmu/{movieId}")
    public Result<List<Danmu>> getDanmuList(@PathVariable Integer movieId) {
        return Result.success(operationService.getDanmuList(movieId));
    }

    /**
     * 发送弹幕
     */
    @PostMapping("/danmu")
    public Result<Void> sendDanmu(@RequestBody Danmu danmu) {
        operationService.sendDanmu(danmu);
        return Result.success();
    }
}
