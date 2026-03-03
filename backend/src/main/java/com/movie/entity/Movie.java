package com.movie.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("movie")
public class Movie {
    @TableId(type = IdType.INPUT)
    private Integer id;
    private String title;
    private String originalTitle;
    private String overview;
    private String posterPath;
    private String backdropPath;
    private LocalDate releaseDate;
    private Integer runtime;
    private BigDecimal voteAverage;
    private Integer voteCount;
    private BigDecimal popularity;
    private String originalLanguage;
    private String status;
    private String tagline;
    private Integer adult;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
