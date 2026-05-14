package com.movie.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("movie")
public class Movie {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String movieName;

    private String originalTitle;

    private String overview;

    private String posterPath;

    private String backdropPath;

    private String releaseYear;

    private String releaseDate;

    private BigDecimal voteAverage;

    private Integer voteCount;

    private BigDecimal popularity;

    private Integer tmdbId;

    private Long categoryId;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 非数据库字段 */
    @TableField(exist = false)
    private String categoryName;
}
