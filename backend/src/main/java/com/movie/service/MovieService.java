package com.movie.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.movie.entity.*;
import com.movie.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieMapper movieMapper;
    private final GenreMapper genreMapper;
    private final MovieGenreMapper movieGenreMapper;
    private final PersonMapper personMapper;
    private final MovieCastMapper movieCastMapper;
    private final MovieCrewMapper movieCrewMapper;
    private final MovieSourceMapper movieSourceMapper;

    /**
     * 热门电影列表（用于轮播图）
     */
    public List<Movie> getPopularMovies(int limit) {
        return movieMapper.selectList(
                new LambdaQueryWrapper<Movie>()
                        .orderByDesc(Movie::getPopularity)
                        .last("LIMIT " + limit));
    }

    /**
     * 按语言分组获取电影列表（首页分类推荐）
     */
    public Map<String, List<Movie>> getMoviesByLanguage(int limit) {
        Map<String, List<Movie>> result = new LinkedHashMap<>();
        String[] languages = { "zh", "en", "ja", "ko", "fr", "de" };
        String[] labels = { "华语", "英语", "日语", "韩语", "法语", "德语" };

        for (int i = 0; i < languages.length; i++) {
            List<Movie> movies = movieMapper.selectList(
                    new LambdaQueryWrapper<Movie>()
                            .eq(Movie::getOriginalLanguage, languages[i])
                            .orderByDesc(Movie::getPopularity)
                            .last("LIMIT " + limit));
            if (!movies.isEmpty()) {
                result.put(labels[i], movies);
            }
        }
        return result;
    }

    /**
     * 电影详情（含演员、导演、类型、播放源）
     */
    public Map<String, Object> getMovieDetail(Integer id) {
        Movie movie = movieMapper.selectById(id);
        if (movie == null)
            return null;

        Map<String, Object> detail = new HashMap<>();
        detail.put("movie", movie);

        // 类型
        List<MovieGenre> mgs = movieGenreMapper.selectList(
                new LambdaQueryWrapper<MovieGenre>().eq(MovieGenre::getMovieId, id));
        if (!mgs.isEmpty()) {
            List<Integer> genreIds = mgs.stream().map(MovieGenre::getGenreId).collect(Collectors.toList());
            List<Genre> genres = genreMapper.selectBatchIds(genreIds);
            detail.put("genres", genres);
        }

        // 演员
        List<MovieCast> casts = movieCastMapper.selectList(
                new LambdaQueryWrapper<MovieCast>().eq(MovieCast::getMovieId, id).orderByAsc(MovieCast::getCastOrder));
        if (!casts.isEmpty()) {
            List<Integer> personIds = casts.stream().map(MovieCast::getPersonId).collect(Collectors.toList());
            List<Person> persons = personMapper.selectBatchIds(personIds);
            Map<Integer, Person> personMap = persons.stream().collect(Collectors.toMap(Person::getId, p -> p));
            List<Map<String, Object>> castList = new ArrayList<>();
            for (MovieCast mc : casts) {
                Map<String, Object> item = new HashMap<>();
                item.put("character", mc.getCharacterName());
                item.put("order", mc.getCastOrder());
                Person p = personMap.get(mc.getPersonId());
                if (p != null) {
                    item.put("name", p.getName());
                    item.put("originalName", p.getOriginalName());
                    item.put("profilePath", p.getProfilePath());
                }
                castList.add(item);
            }
            detail.put("cast", castList);
        }

        // 导演
        List<MovieCrew> crews = movieCrewMapper.selectList(
                new LambdaQueryWrapper<MovieCrew>().eq(MovieCrew::getMovieId, id));
        if (!crews.isEmpty()) {
            List<Integer> personIds = crews.stream().map(MovieCrew::getPersonId).collect(Collectors.toList());
            List<Person> persons = personMapper.selectBatchIds(personIds);
            Map<Integer, Person> personMap = persons.stream()
                    .collect(Collectors.toMap(Person::getId, p -> p, (a, b) -> a));
            List<Map<String, Object>> crewList = new ArrayList<>();
            for (MovieCrew mc : crews) {
                Map<String, Object> item = new HashMap<>();
                item.put("job", mc.getJob());
                item.put("department", mc.getDepartment());
                Person p = personMap.get(mc.getPersonId());
                if (p != null) {
                    item.put("name", p.getName());
                    item.put("originalName", p.getOriginalName());
                }
                crewList.add(item);
            }
            detail.put("crew", crewList);
        }

        // 播放源
        List<MovieSource> sources = movieSourceMapper.selectList(
                new LambdaQueryWrapper<MovieSource>()
                        .eq(MovieSource::getMovieId, id)
                        .eq(MovieSource::getStatus, 1)
                        .orderByAsc(MovieSource::getSortOrder));
        detail.put("sources", sources);

        return detail;
    }

    /**
     * 搜索电影
     */
    public IPage<Movie> searchMovies(String keyword, int page, int size) {
        return movieMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Movie>()
                        .like(Movie::getTitle, keyword)
                        .or().like(Movie::getOriginalTitle, keyword)
                        .orderByDesc(Movie::getPopularity));
    }

    /**
     * 影库筛选
     */
    public IPage<Movie> filterMovies(String language, Integer genreId, String yearRange,
            Double minScore, String sort, int page, int size) {
        LambdaQueryWrapper<Movie> wrapper = new LambdaQueryWrapper<>();

        if (language != null && !language.isEmpty()) {
            wrapper.eq(Movie::getOriginalLanguage, language);
        }
        if (yearRange != null && !yearRange.isEmpty()) {
            String[] parts = yearRange.split("-");
            if (parts.length == 2) {
                wrapper.between(Movie::getReleaseDate,
                        parts[0] + "-01-01", parts[1] + "-12-31");
            }
        }
        if (minScore != null) {
            wrapper.ge(Movie::getVoteAverage, minScore);
        }
        if (genreId != null) {
            // 先查出该类型下的电影ID
            List<MovieGenre> mgs = movieGenreMapper.selectList(
                    new LambdaQueryWrapper<MovieGenre>().eq(MovieGenre::getGenreId, genreId));
            if (!mgs.isEmpty()) {
                List<Integer> movieIds = mgs.stream().map(MovieGenre::getMovieId).collect(Collectors.toList());
                wrapper.in(Movie::getId, movieIds);
            } else {
                return new Page<>(page, size); // 空结果
            }
        }

        // 排序
        if ("latest".equals(sort)) {
            wrapper.orderByDesc(Movie::getReleaseDate);
        } else if ("rating".equals(sort)) {
            wrapper.orderByDesc(Movie::getVoteAverage);
        } else {
            wrapper.orderByDesc(Movie::getPopularity);
        }

        return movieMapper.selectPage(new Page<>(page, size), wrapper);
    }

    /**
     * 相关推荐（同类型电影）
     */
    public List<Movie> getRelatedMovies(Integer movieId, int limit) {
        List<MovieGenre> mgs = movieGenreMapper.selectList(
                new LambdaQueryWrapper<MovieGenre>().eq(MovieGenre::getMovieId, movieId));
        if (mgs.isEmpty())
            return Collections.emptyList();

        List<Integer> genreIds = mgs.stream().map(MovieGenre::getGenreId).collect(Collectors.toList());
        List<MovieGenre> relatedMgs = movieGenreMapper.selectList(
                new LambdaQueryWrapper<MovieGenre>()
                        .in(MovieGenre::getGenreId, genreIds)
                        .ne(MovieGenre::getMovieId, movieId));
        if (relatedMgs.isEmpty())
            return Collections.emptyList();

        List<Integer> relatedIds = relatedMgs.stream()
                .map(MovieGenre::getMovieId).distinct()
                .collect(Collectors.toList());
        return movieMapper.selectList(
                new LambdaQueryWrapper<Movie>()
                        .in(Movie::getId, relatedIds)
                        .orderByDesc(Movie::getPopularity)
                        .last("LIMIT " + limit));
    }

    /**
     * 获取所有类型
     */
    public List<Genre> getAllGenres() {
        return genreMapper.selectList(null);
    }
}
