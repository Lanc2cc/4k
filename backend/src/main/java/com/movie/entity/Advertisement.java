package com.movie.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("advertisement")
public class Advertisement {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String adType;
    private String imageUrl;
    private String linkUrl;
    private String position;
    private Integer duration;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
}
