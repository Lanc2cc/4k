package com.movie.service;

import com.movie.common.PageResult;
import com.movie.common.Result;
import com.movie.entity.*;

public interface AdminService {

    Result<PageResult<User>> getUserList(Integer page, Integer size);

    Result<?> updateUserStatus(Long userId, Integer status);

    Result<PageResult<Movie>> getMovieList(Integer page, Integer size);

    Result<?> addMovie(Movie movie);

    Result<?> updateMovie(Movie movie);

    Result<?> deleteMovie(Long movieId);

    Result<?> addMovieSource(MovieSource source);

    Result<?> deleteMovieSource(Long sourceId);

    Result<PageResult<Comment>> getCommentList(Integer page, Integer size);

    Result<?> updateCommentStatus(Long commentId, Integer status);

    Result<?> deleteComment(Long commentId);

    Result<?> addCategory(Category category);

    Result<?> deleteCategory(Long categoryId);

    Result<PageResult<ApiLog>> getApiLogs(Integer page, Integer size);

    Result<?> getDashboard();
}
