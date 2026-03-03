package com.movie.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("movie_cast")
public class MovieCast {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer movieId;
    private Integer personId;
    private String characterName;
    private Integer castOrder;
}
