package com.movie.controller;

import com.movie.common.PageResult;
import com.movie.common.Result;
import com.movie.entity.Movie;
import com.movie.service.FavoriteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/add")
    public Result<?> addFavorite(HttpServletRequest request, @RequestParam Long movieId) {
        Long userId = (Long) request.getAttribute("userId");
        return favoriteService.addFavorite(userId, movieId);
    }

    @DeleteMapping("/remove")
    public Result<?> removeFavorite(HttpServletRequest request, @RequestParam Long movieId) {
        Long userId = (Long) request.getAttribute("userId");
        return favoriteService.removeFavorite(userId, movieId);
    }

    @GetMapping("/list")
    public Result<PageResult<Movie>> getFavoriteList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer size) {
        Long userId = (Long) request.getAttribute("userId");
        return favoriteService.getFavoriteList(userId, page, size);
    }

    @GetMapping("/check")
    public Result<Boolean> isFavorite(HttpServletRequest request, @RequestParam Long movieId) {
        Long userId = (Long) request.getAttribute("userId");
        return favoriteService.isFavorite(userId, movieId);
    }
}
