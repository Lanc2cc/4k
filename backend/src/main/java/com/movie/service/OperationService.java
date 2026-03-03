package com.movie.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.movie.entity.Advertisement;
import com.movie.entity.Activity;
import com.movie.entity.Danmu;
import com.movie.entity.HotSearch;
import com.movie.mapper.AdvertisementMapper;
import com.movie.mapper.ActivityMapper;
import com.movie.mapper.DanmuMapper;
import com.movie.mapper.HotSearchMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationService {

    private final AdvertisementMapper advertisementMapper;
    private final HotSearchMapper hotSearchMapper;
    private final ActivityMapper activityMapper;
    private final DanmuMapper danmuMapper;

    /**
     * 获取广告列表
     */
    public List<Advertisement> getAds(String position) {
        LambdaQueryWrapper<Advertisement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Advertisement::getStatus, 1);
        if (position != null && !position.isEmpty()) {
            wrapper.eq(Advertisement::getPosition, position);
        }
        wrapper.orderByAsc(Advertisement::getSortOrder);
        return advertisementMapper.selectList(wrapper);
    }

    /**
     * 获取热搜词
     */
    public List<HotSearch> getHotSearches() {
        return hotSearchMapper.selectList(
                new LambdaQueryWrapper<HotSearch>()
                        .eq(HotSearch::getStatus, 1)
                        .orderByDesc(HotSearch::getSortOrder));
    }

    /**
     * 获取运营活动列表
     */
    public List<Activity> getActivities() {
        return activityMapper.selectList(
                new LambdaQueryWrapper<Activity>()
                        .eq(Activity::getStatus, 1)
                        .orderByDesc(Activity::getSortOrder));
    }

    /**
     * 获取电影弹幕
     */
    public List<Danmu> getDanmuList(Integer movieId) {
        return danmuMapper.selectList(
                new LambdaQueryWrapper<Danmu>()
                        .eq(Danmu::getMovieId, movieId)
                        .eq(Danmu::getStatus, 1)
                        .orderByAsc(Danmu::getTimePoint));
    }

    /**
     * 发送弹幕
     */
    public void sendDanmu(Danmu danmu) {
        danmu.setStatus(1); // 默认通过
        danmuMapper.insert(danmu);
    }
}
