package com.movie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movie.common.Result;
import com.movie.entity.ApiLog;
import com.movie.entity.Category;
import com.movie.entity.Movie;
import com.movie.entity.MovieSource;
import com.movie.mapper.ApiLogMapper;
import com.movie.mapper.CategoryMapper;
import com.movie.mapper.MovieMapper;
import com.movie.mapper.MovieSourceMapper;
import com.movie.service.TmdbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

@Service
public class TmdbServiceImpl implements TmdbService {

    @Value("${tmdb.api-key}")
    private String apiKey;

    @Value("${tmdb.api-token}")
    private String apiToken;

    @Value("${tmdb.base-url}")
    private String baseUrl;

    @Value("${tmdb.image-base-url}")
    private String imageBaseUrl;

    @Autowired
    private MovieMapper movieMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ApiLogMapper apiLogMapper;

    @Autowired
    private MovieSourceMapper movieSourceMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // TMDB genre id -> 中文名 映射
    private static final Map<Integer, String> GENRE_MAP = new HashMap<>() {{
        put(28, "动作"); put(12, "冒险"); put(16, "动画"); put(35, "喜剧");
        put(80, "犯罪"); put(99, "纪录片"); put(18, "剧情"); put(10751, "家庭");
        put(14, "奇幻"); put(36, "历史"); put(27, "恐怖"); put(10402, "音乐");
        put(9648, "悬疑"); put(10749, "爱情"); put(878, "科幻");
        put(10770, "电视电影"); put(53, "惊悚"); put(10752, "战争"); put(37, "西部");
    }};

    @Override
    public Result<?> syncPopularMovies(int count) {
        return syncMovies("popular", count);
    }

    @Override
    public Result<?> syncTopRatedMovies(int count) {
        return syncMovies("top_rated", count);
    }

    private RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000);
        factory.setReadTimeout(60000);
        // 通过本地代理访问TMDB API
        factory.setProxy(new java.net.Proxy(java.net.Proxy.Type.HTTP,
                new java.net.InetSocketAddress("127.0.0.1", 10808)));
        return new RestTemplate(factory);
    }

    private Result<?> syncMovies(String type, int count) {
        // 创建带超时和代理的 HTTP 客户端，保证外部接口调用稳定
        RestTemplate restTemplate = createRestTemplate();
        int totalSynced = 0;
        int totalPages = (count + 19) / 20; // TMDB每页20条

        try {
            // 预加载分类，便于后续进行类型映射
            List<Category> categories = categoryMapper.selectList(null);
            Map<String, Long> categoryNameMap = new HashMap<>();
            categories.forEach(c -> categoryNameMap.put(c.getName(), c.getId()));

            // 按页拉取 TMDB 数据，控制单次同步数量
            for (int page = 1; page <= totalPages && totalSynced < count; page++) {
                String url = baseUrl + "/movie/" + type + "?api_key=" + apiKey + "&language=zh-CN&page=" + page;

                org.springframework.http.ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode results = root.get("results");

                if (results == null || !results.isArray()) continue;

                for (JsonNode item : results) {
                    if (totalSynced >= count) break;

                    // 提取影片基础信息，并从上映日期推导上映年份
                    String movieName = item.has("title") ? item.get("title").asText() : "";
                    String releaseDate = item.has("release_date") ? item.get("release_date").asText() : "";
                    String releaseYear = releaseDate.length() >= 4 ? releaseDate.substring(0, 4) : "";

                    // 按“片名 + 上映年份”去重，避免重复入库
                    LambdaQueryWrapper<Movie> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(Movie::getMovieName, movieName).eq(Movie::getReleaseYear, releaseYear);
                    if (movieMapper.selectCount(wrapper) > 0) continue;

                    // 组装电影对象，并完成海报、背景图、评分等字段转换
                    Movie movie = new Movie();
                    movie.setMovieName(movieName);
                    movie.setOriginalTitle(item.has("original_title") ? item.get("original_title").asText() : "");
                    movie.setOverview(item.has("overview") ? item.get("overview").asText() : "");
                    movie.setPosterPath(item.has("poster_path") && !item.get("poster_path").isNull()
                            ? imageBaseUrl + item.get("poster_path").asText() : "");
                    movie.setBackdropPath(item.has("backdrop_path") && !item.get("backdrop_path").isNull()
                            ? imageBaseUrl + item.get("backdrop_path").asText() : "");
                    movie.setReleaseYear(releaseYear);
                    movie.setReleaseDate(releaseDate);
                    movie.setVoteAverage(item.has("vote_average")
                            ? BigDecimal.valueOf(item.get("vote_average").asDouble()) : BigDecimal.ZERO);
                    movie.setVoteCount(item.has("vote_count") ? item.get("vote_count").asInt() : 0);
                    movie.setPopularity(item.has("popularity")
                            ? BigDecimal.valueOf(item.get("popularity").asDouble()) : BigDecimal.ZERO);
                    movie.setTmdbId(item.has("id") ? item.get("id").asInt() : null);
                    movie.setStatus(1);

                    // 将 TMDB 类型映射为本地分类，保证前后端分类一致
                    if (item.has("genre_ids") && item.get("genre_ids").isArray()) {
                        for (JsonNode gid : item.get("genre_ids")) {
                            String genreName = GENRE_MAP.get(gid.asInt());
                            if (genreName != null && categoryNameMap.containsKey(genreName)) {
                                movie.setCategoryId(categoryNameMap.get(genreName));
                                break;
                            }
                        }
                    }

                    movieMapper.insert(movie);

                    // 写入电影数据后自动生成默认播放源
                    generateMovieSources(movie.getId(), movieName, movie.getTmdbId());

                    totalSynced++;
                }
            }

            // 记录同步日志，便于管理员追踪同步结果
            ApiLog log = new ApiLog();
            log.setSyncType(type);
            log.setSyncCount(totalSynced);
            log.setStatus(1);
            log.setMessage("成功同步 " + totalSynced + " 部电影");
            apiLogMapper.insert(log);

            // 同步完成后清除电影相关缓存，保证前台展示最新数据
            flushRedisCache();

            return Result.success("成功同步 " + totalSynced + " 部电影");
        } catch (Exception e) {
            // 同步失败时记录失败日志，便于后续排查问题
            ApiLog log = new ApiLog();
            log.setSyncType(type);
            log.setSyncCount(totalSynced);
            log.setStatus(0);
            log.setMessage("同步失败: " + e.getMessage());
            apiLogMapper.insert(log);
            return Result.error("同步失败: " + e.getMessage());
        }
    }

    /**
     * 根据 TMDB 主键自动生成多个播放源，保证导入后即可形成可播放状态。
     */
    private void generateMovieSources(Long movieId, String movieName, Integer tmdbId) {
        // 使用多个清晰度链接模拟多线路播放源
        String[][] sources = {
            {"播放源1", "1080p", "https://vidsrc.icu/embed/movie/" + tmdbId},
            {"播放源2", "1080p", "https://vidsrc.cc/v2/embed/movie/" + tmdbId},
            {"播放源3", "720p",  "https://multiembed.mov/?video_id=" + tmdbId + "&tmdb=1"}
        };
        for (String[] src : sources) {
            MovieSource ms = new MovieSource();
            ms.setMovieId(movieId);
            ms.setSourceName(src[0]);
            ms.setQuality(src[1]);
            ms.setSourceUrl(src[2]);
            movieSourceMapper.insert(ms);
        }
    }

    /**
     * 清理 Redis 中所有与电影相关的缓存键，避免旧数据残留。
     */
    private void flushRedisCache() {
        try {
            Set<String> keys = redisTemplate.keys("movie:*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception ignored) {
        }
    }
}
