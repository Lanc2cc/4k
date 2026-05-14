package com.movie.controller;

import com.movie.common.PageResult;
import com.movie.common.Result;
import com.movie.dto.CommentDTO;
import com.movie.entity.Comment;
import com.movie.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    public Result<?> addComment(HttpServletRequest request, @RequestBody CommentDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        return commentService.addComment(userId, dto);
    }

    @GetMapping("/list")
    public Result<PageResult<Comment>> getComments(
            @RequestParam Long movieId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getCommentsByMovieId(movieId, page, size);
    }

    @DeleteMapping("/delete/{commentId}")
    public Result<?> deleteComment(HttpServletRequest request, @PathVariable Long commentId) {
        Long userId = (Long) request.getAttribute("userId");
        return commentService.deleteComment(userId, commentId);
    }
}
