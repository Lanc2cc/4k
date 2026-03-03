package com.movie.service;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.movie.entity.*;
import com.movie.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * TMDB数据同步服务 — 从TMDB API拉取电影数据存入本地数据库
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TmdbService {

    private final MovieMapper movieMapper;
    private final GenreMapper genreMapper;
    private final MovieGenreMapper movieGenreMapper;
    private final PersonMapper personMapper;
    private final MovieCastMapper movieCastMapper;
    private final MovieCrewMapper movieCrewMapper;

    @Value("${tmdb.api-key}")
    private String apiKey;

    @Value("${tmdb.base-url}")
    private String baseUrl;

    /**
     * 同步类型字典
     */
    public void syncGenres() {
        String url = baseUrl + "/genre/movie/list?api_key=" + apiKey + "&language=zh-CN";
        String result = HttpUtil.get(url);
        JSONObject json = JSONObject.parseObject(result);
        JSONArray genres = json.getJSONArray("genres");
        if (genres == null)
            return;

        for (int i = 0; i < genres.size(); i++) {
            JSONObject g = genres.getJSONObject(i);
            Genre genre = new Genre();
            genre.setId(g.getInteger("id"));
            genre.setName(g.getString("name"));
            genreMapper.insertOrUpdate(genre);
        }
        log.info("同步类型字典完成，共{}个", genres.size());
    }

    /**
     * 同步热门电影（按页）
     */
    public int syncPopularMovies(int page, String language) {
        String url = baseUrl + "/movie/popular?api_key=" + apiKey
                + "&language=" + language + "&page=" + page;
        String result = HttpUtil.get(url);
        JSONObject json = JSONObject.parseObject(result);
        JSONArray results = json.getJSONArray("results");
        if (results == null || results.isEmpty())
            return 0;

        int count = 0;
        for (int i = 0; i < results.size(); i++) {
            JSONObject m = results.getJSONObject(i);
            Integer movieId = m.getInteger("id");
            try {
                syncMovieDetail(movieId, language);
                count++;
            } catch (Exception e) {
                log.error("同步电影{}失败: {}", movieId, e.getMessage());
            }
        }
        log.info("同步热门电影第{}页完成，语言={}，成功{}部", page, language, count);
        return count;
    }

    /**
     * 同步单部电影详情（含演员、导演）
     */
    @Transactional
    public void syncMovieDetail(Integer movieId, String language) {
        String url = baseUrl + "/movie/" + movieId
                + "?api_key=" + apiKey + "&language=" + language
                + "&append_to_response=credits";
        String result = HttpUtil.get(url);
        JSONObject json = JSONObject.parseObject(result);

        // 保存电影主信息
        Movie movie = new Movie();
        movie.setId(json.getInteger("id"));
        movie.setTitle(json.getString("title"));
        movie.setOriginalTitle(json.getString("original_title"));
        movie.setOverview(json.getString("overview"));
        movie.setPosterPath(json.getString("poster_path"));
        movie.setBackdropPath(json.getString("backdrop_path"));
        String releaseDateStr = json.getString("release_date");
        if (releaseDateStr != null && !releaseDateStr.isEmpty()) {
            movie.setReleaseDate(LocalDate.parse(releaseDateStr));
        }
        movie.setRuntime(json.getInteger("runtime"));
        movie.setVoteAverage(
                json.getBigDecimal("vote_average") != null ? json.getBigDecimal("vote_average") : BigDecimal.ZERO);
        movie.setVoteCount(json.getInteger("vote_count") != null ? json.getInteger("vote_count") : 0);
        movie.setPopularity(
                json.getBigDecimal("popularity") != null ? json.getBigDecimal("popularity") : BigDecimal.ZERO);
        movie.setOriginalLanguage(json.getString("original_language"));
        movie.setStatus(json.getString("status"));
        movie.setTagline(json.getString("tagline"));
        movie.setAdult(json.getBooleanValue("adult") ? 1 : 0);
        movieMapper.insertOrUpdate(movie);

        // 保存类型关联
        JSONArray genres = json.getJSONArray("genres");
        if (genres != null) {
            // 先删旧关联
            movieGenreMapper.delete(new LambdaQueryWrapper<MovieGenre>().eq(MovieGenre::getMovieId, movieId));
            for (int i = 0; i < genres.size(); i++) {
                JSONObject g = genres.getJSONObject(i);
                // 确保类型字典里有
                Genre genre = new Genre();
                genre.setId(g.getInteger("id"));
                genre.setName(g.getString("name"));
                genreMapper.insertOrUpdate(genre);

                MovieGenre mg = new MovieGenre();
                mg.setMovieId(movieId);
                mg.setGenreId(g.getInteger("id"));
                movieGenreMapper.insert(mg);
            }
        }

        // 保存演员信息
        JSONObject credits = json.getJSONObject("credits");
        if (credits != null) {
            // 先删旧关联
            movieCastMapper.delete(new LambdaQueryWrapper<MovieCast>().eq(MovieCast::getMovieId, movieId));
            movieCrewMapper.delete(new LambdaQueryWrapper<MovieCrew>().eq(MovieCrew::getMovieId, movieId));

            JSONArray cast = credits.getJSONArray("cast");
            if (cast != null) {
                int limit = Math.min(cast.size(), 10); // 只保存前10个主演
                for (int i = 0; i < limit; i++) {
                    JSONObject c = cast.getJSONObject(i);
                    // 保存人员
                    Person person = new Person();
                    person.setId(c.getInteger("id"));
                    person.setName(c.getString("name"));
                    person.setOriginalName(c.getString("original_name"));
                    person.setProfilePath(c.getString("profile_path"));
                    person.setGender(c.getInteger("gender"));
                    personMapper.insertOrUpdate(person);

                    // 保存关联
                    MovieCast mc = new MovieCast();
                    mc.setMovieId(movieId);
                    mc.setPersonId(c.getInteger("id"));
                    mc.setCharacterName(c.getString("character"));
                    mc.setCastOrder(c.getInteger("order") != null ? c.getInteger("order") : i);
                    movieCastMapper.insert(mc);
                }
            }

            JSONArray crew = credits.getJSONArray("crew");
            if (crew != null) {
                for (int i = 0; i < crew.size(); i++) {
                    JSONObject c = crew.getJSONObject(i);
                    String job = c.getString("job");
                    // 只保存导演和编剧
                    if ("Director".equals(job) || "Screenplay".equals(job)) {
                        Person person = new Person();
                        person.setId(c.getInteger("id"));
                        person.setName(c.getString("name"));
                        person.setOriginalName(c.getString("original_name"));
                        person.setProfilePath(c.getString("profile_path"));
                        person.setGender(c.getInteger("gender") != null ? c.getInteger("gender") : 0);
                        personMapper.insertOrUpdate(person);

                        MovieCrew mc = new MovieCrew();
                        mc.setMovieId(movieId);
                        mc.setPersonId(c.getInteger("id"));
                        mc.setDepartment(c.getString("department"));
                        mc.setJob(job);
                        movieCrewMapper.insert(mc);
                    }
                }
            }
        }
    }

    /**
     * 批量同步：拉取多页热门电影
     */
    public int syncBatch(int pages, String language) {
        int total = 0;
        for (int i = 1; i <= pages; i++) {
            total += syncPopularMovies(i, language);
            // 避免API限流
            try {
                Thread.sleep(300);
            } catch (InterruptedException ignored) {
            }
        }
        return total;
    }
}
