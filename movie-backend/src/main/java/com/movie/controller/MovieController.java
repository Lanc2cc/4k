package com.movie.controller;

import com.movie.common.PageResult;
import com.movie.common.Result;
import com.movie.entity.Category;
import com.movie.entity.Movie;
import com.movie.entity.MovieSource;
import com.movie.service.CategoryService;
import com.movie.service.MovieService;
import com.movie.service.MovieSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movie")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private MovieSourceService movieSourceService;

    @GetMapping("/list")
    public Result<PageResult<Movie>> getMovieList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer size,
            @RequestParam(required = false) Long categoryId) {
        return movieService.getMovieList(page, size, categoryId);
    }

    @GetMapping("/detail/{id}")
    public Result<Movie> getMovieDetail(@PathVariable Long id) {
        return movieService.getMovieDetail(id);
    }

    @GetMapping("/search")
    public Result<PageResult<Movie>> searchMovies(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer size) {
        return movieService.searchMovies(keyword, page, size);
    }

    @GetMapping("/hot")
    public Result<PageResult<Movie>> getHotMovies(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer size) {
        return movieService.getHotMovies(page, size);
    }

    @GetMapping("/categories")
    public Result<List<Category>> getCategories() {
        return Result.success(categoryService.getAllCategories());
    }

    @GetMapping("/sources/{movieId}")
    public Result<List<MovieSource>> getMovieSources(@PathVariable Long movieId) {
        return Result.success(movieSourceService.getSourcesByMovieId(movieId));
    }
}
