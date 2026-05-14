package com.movie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.movie.entity.ApiLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApiLogMapper extends BaseMapper<ApiLog> {
}
