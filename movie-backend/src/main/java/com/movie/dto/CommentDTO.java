package com.movie.dto;

import lombok.Data;

@Data
public class CommentDTO {
    private Long movieId;
    private String content;
}
