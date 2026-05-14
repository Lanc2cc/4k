package com.movie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.movie.common.PageResult;
import com.movie.common.Result;
import com.movie.dto.CommentDTO;
import com.movie.entity.Comment;

public interface CommentService extends IService<Comment> {

    Result<?> addComment(Long userId, CommentDTO dto);

    Result<PageResult<Comment>> getCommentsByMovieId(Long movieId, Integer page, Integer size);

    Result<?> deleteComment(Long userId, Long commentId);
}
