<template>
  <div class="play-page" v-loading="loading">
    <template v-if="movie">
      <h2>{{ movie.movieName }}</h2>

      <!-- 播放器区域 -->
      <div class="player-wrapper">
        <div v-if="sources.length > 0" class="player">
          <iframe
            :src="currentSource?.sourceUrl"
            frameborder="0"
            allowfullscreen
            referrerpolicy="no-referrer"
            style="width:100%; height:100%;"
          ></iframe>
        </div>
        <div v-else class="no-source">
          <p>暂无播放源</p>
          <p>管理员可在后台添加播放源</p>
        </div>
      </div>

      <!-- 播放源切换 -->
      <div class="source-list" v-if="sources.length > 1">
        <span>切换播放源：</span>
        <el-button
          v-for="(s, i) in sources"
          :key="s.id"
          :type="currentSourceIndex === i ? 'danger' : 'default'"
          size="small"
          @click="currentSourceIndex = i; currentSource = s"
        >{{ s.sourceName }} ({{ s.quality }})</el-button>
      </div>

      <!-- 电影信息 -->
      <div class="movie-info">
        <div class="info-row">
          <span>评分：{{ movie.voteAverage }}</span>
          <span>年份：{{ movie.releaseYear }}</span>
          <span>分类：{{ movie.categoryName || '未分类' }}</span>
        </div>
        <p class="overview">{{ movie.overview }}</p>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getMovieDetail, getMovieSources } from '../api/movie'
import { addHistory } from '../api/history'
import { useUserStore } from '../store/user'

const route = useRoute()
const userStore = useUserStore()
const loading = ref(true)
const movie = ref<any>(null)
const sources = ref<any[]>([])
const currentSource = ref<any>(null)
const currentSourceIndex = ref(0)

const movieId = Number(route.params.id)

onMounted(async () => {
  try {
    const [movieRes, sourceRes]: any[] = await Promise.all([
      getMovieDetail(movieId),
      getMovieSources(movieId)
    ])
    if (movieRes.code === 200) movie.value = movieRes.data
    if (sourceRes.code === 200) {
      sources.value = sourceRes.data.map((s: any) => {
        let url = s.sourceUrl;
        // 把旧的所有不可用或易受影响的源，全部热重定向到稳定的 autoembed 和 2embed，避免使用可能引用 cloudnestra 的不稳定分发节点
        if (url.includes('vidsrc.')) {
          // 如果是 vidsrc 系的源，将其替换为 2embed 或者保留为主站域
          let id = url.split('/movie/')[1]?.replace('?tmdb=', '');
          if (id) {
            url = `https://www.2embed.cc/embed/${id}`;
          }
        } else if (url.includes('multiembed.mov/?video_id=')) {
          let id = url.split('video_id=')[1]?.replace('&tmdb=1', '');
          if(id) {
            url = `https://autoembed.co/movie/tmdb/${id}`;
          }
        }
        s.sourceUrl = url;
        return s;
      })
      if (sources.value.length > 0) currentSource.value = sources.value[0]
    }

    // 记录观看历史
    if (userStore.isLoggedIn) {
      addHistory(movieId)
    }
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.play-page h2 { color: #fff; margin-bottom: 15px; }
.player-wrapper { margin-bottom: 20px; }
.player {
  width: 100%;
  aspect-ratio: 16/9;
  background: #000;
  border-radius: 8px;
  overflow: hidden;
}
.no-source {
  width: 100%;
  aspect-ratio: 16/9;
  background: #1a1a2e;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #666;
}
.source-list {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 20px;
  color: #b3b3b3;
}
.movie-info {
  background: #1a1a2e;
  border-radius: 12px;
  padding: 20px;
}
.info-row {
  display: flex;
  gap: 20px;
  margin-bottom: 12px;
  color: #b3b3b3;
}
.overview {
  color: #ccc;
  line-height: 1.7;
}
</style>
