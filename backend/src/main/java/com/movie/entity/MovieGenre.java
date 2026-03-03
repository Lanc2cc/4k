package com.movie.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("movie_genre")
public class MovieGenre {
    private Integer movieId;
    private Integer genreId;
}
