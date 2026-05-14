package com.movie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.movie.entity.WatchHistory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WatchHistoryMapper extends BaseMapper<WatchHistory> {
}
