package com.movie.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("genre")
public class Genre {
    @TableId(type = IdType.INPUT)
    private Integer id;
    private String name;
}
