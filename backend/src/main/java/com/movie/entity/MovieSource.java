package com.movie.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("movie_source")
public class MovieSource {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer movieId;
    private String sourceName;
    private String sourceUrl;
    private String quality;
    private String stability;
    private Integer isRecommended;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createdAt;
}
