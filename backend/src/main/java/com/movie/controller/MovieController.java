package com.movie.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.movie.common.Result;
import com.movie.entity.Genre;
import com.movie.entity.Movie;
import com.movie.service.MovieService;
import com.movie.service.TmdbService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movie")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final TmdbService tmdbService;

    /**
     * 热门电影列表（轮播图用）
     */
    @GetMapping("/popular")
    public Result<List<Movie>> getPopular(@RequestParam(defaultValue = "8") int limit) {
        return Result.success(movieService.getPopularMovies(limit));
    }

    /**
     * 首页分类推荐（按语言分组）
     */
    @GetMapping("/list")
    public Result<Map<String, List<Movie>>> getMoviesByLanguage(
            @RequestParam(defaultValue = "8") int limit) {
        return Result.success(movieService.getMoviesByLanguage(limit));
    }

    /**
     * 电影详情
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getDetail(@PathVariable Integer id) {
        Map<String, Object> detail = movieService.getMovieDetail(id);
        if (detail == null) {
            return Result.error("电影不存在");
        }
        return Result.success(detail);
    }

    /**
     * 搜索电影
     */
    @GetMapping("/search")
    public Result<IPage<Movie>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.success(movieService.searchMovies(keyword, page, size));
    }

    /**
     * 影库筛选
     */
    @GetMapping("/filter")
    public Result<IPage<Movie>> filter(
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Integer genreId,
            @RequestParam(required = false) String yearRange,
            @RequestParam(required = false) Double minScore,
            @RequestParam(defaultValue = "popular") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.success(movieService.filterMovies(language, genreId, yearRange, minScore, sort, page, size));
    }

    /**
     * 相关推荐
     */
    @GetMapping("/{id}/related")
    public Result<List<Movie>> getRelated(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "6") int limit) {
        return Result.success(movieService.getRelatedMovies(id, limit));
    }

    /**
     * 获取所有类型
     */
    @GetMapping("/genres")
    public Result<List<Genre>> getGenres() {
        return Result.success(movieService.getAllGenres());
    }

    /**
     * 同步TMDB数据（管理接口）
     */
    @PostMapping("/sync")
    public Result<String> syncData(
            @RequestParam(defaultValue = "3") int pages,
            @RequestParam(defaultValue = "zh-CN") String language) {
        tmdbService.syncGenres();
        int count = tmdbService.syncBatch(pages, language);
        return Result.success("同步完成，共" + count + "部电影");
    }
}
