package com.movie.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("danmu")
public class Danmu {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer movieId;
    private Long userId;
    private String content;
    private Integer timePoint;
    private String color;
    private Integer fontSize;
    private Integer status;
    private LocalDateTime createdAt;
}
