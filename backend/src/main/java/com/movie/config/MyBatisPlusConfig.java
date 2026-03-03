package com.movie.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.movie.mapper")
public class MyBatisPlusConfig {
}
