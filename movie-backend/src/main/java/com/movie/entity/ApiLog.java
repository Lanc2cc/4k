package com.movie.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("api_log")
public class ApiLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String syncType;

    private Integer syncCount;

    private Integer status;

    private String message;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
