package com.movie.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("movie_source")
public class MovieSource {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long movieId;

    private String sourceName;

    private String sourceUrl;

    private String quality;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
