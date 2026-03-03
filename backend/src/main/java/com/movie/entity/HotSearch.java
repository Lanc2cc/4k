package com.movie.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("hot_search")
public class HotSearch {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String keyword;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createdAt;
}
