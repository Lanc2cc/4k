<template>
  <div class="page-container">
    <!-- 轮播图 -->
    <div class="carousel-wrapper" v-if="popularMovies.length">
      <el-carousel height="480px" :interval="5000" arrow="hover">
        <el-carousel-item v-for="movie in popularMovies.slice(0, 8)" :key="movie.id">
          <div
            class="carousel-item"
            :style="{ backgroundImage: `url(${backdropUrl(movie.backdropPath)})` }"
            @click="$router.push(`/movie/${movie.id}`)"
          >
            <div class="content">
              <div style="display:flex;gap:8px;margin-bottom:12px">
                <span class="tag tag-4k">4K超高清</span>
                <span class="tag">热门</span>
              </div>
              <h2>{{ movie.title }}</h2>
              <p>{{ movie.overview }}</p>
              <button class="play-btn" style="margin-top:16px" @click.stop="$router.push(`/play/${movie.id}`)">
                ▶ 立即观看
              </button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 分类推荐区 -->
    <div v-for="(movies, label) in categoryMovies" :key="label" style="margin-bottom:48px">
      <div class="section-header">
        <h2 class="section-title">{{ label }}</h2>
        <span class="section-more" @click="goLibrary(label)">查看更多 ›</span>
      </div>
      <div class="movie-grid">
        <div
          class="movie-card"
          v-for="movie in movies"
          :key="movie.id"
          @click="$router.push(`/movie/${movie.id}`)"
        >
          <img class="poster" :src="posterUrl(movie.posterPath)" :alt="movie.title" loading="lazy" />
          <div class="overlay">
            <button class="play-btn" @click.stop="$router.push(`/play/${movie.id}`)">▶ 播放</button>
          </div>
          <div class="info">
            <div class="title">{{ movie.title }}</div>
            <div class="meta">
              <span class="score">★ {{ movie.voteAverage }}</span>
              <span>{{ movie.releaseDate?.substring(0, 4) }}</span>
              <span class="tag tag-4k" style="font-size:10px">4K</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" style="text-align:center;padding:60px;color:var(--text-muted)">
      <el-icon class="is-loading" :size="32"><Loading /></el-icon>
      <p style="margin-top:12px">加载中...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Loading } from '@element-plus/icons-vue'
import api, { posterUrl, backdropUrl } from '../api'

const router = useRouter()
const popularMovies = ref([])
const categoryMovies = ref({})
const loading = ref(true)

const langMap = { '华语': 'zh', '英语': 'en', '日语': 'ja', '韩语': 'ko', '法语': 'fr', '德语': 'de' }

function goLibrary(label) {
  const lang = langMap[label] || ''
  router.push({ path: '/library', query: { language: lang } })
}

onMounted(async () => {
  try {
    const [popRes, catRes] = await Promise.all([
      api.get('/movie/popular', { params: { limit: 8 } }),
      api.get('/movie/list', { params: { limit: 8 } })
    ])
    popularMovies.value = popRes.data.data || []
    categoryMovies.value = catRes.data.data || {}
  } catch (e) {
    console.error('加载失败', e)
  } finally {
    loading.value = false
  }
})
</script>
