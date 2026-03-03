<template>
  <div class="page-container">
    <div v-if="movie" class="detail-page">
      <!-- 返回按钮 -->
      <div style="margin-bottom:20px">
        <el-button text @click="$router.back()">‹ 返回</el-button>
        <el-button text @click="$router.push('/')">首页</el-button>
      </div>

      <!-- 核心信息区 -->
      <div class="detail-hero">
        <img class="detail-poster" :src="posterUrl(movie.posterPath)" :alt="movie.title" />
        <div class="detail-info">
          <h1>{{ movie.title }}
            <span class="tag tag-4k" style="font-size:13px;vertical-align:middle;margin-left:8px">4K</span>
          </h1>
          <div v-if="movie.originalTitle && movie.originalTitle !== movie.title" class="original-title">
            {{ movie.originalTitle }}
          </div>

          <div class="detail-score">
            <span class="score-num">★ {{ movie.voteAverage }}</span>
            <span class="score-count">{{ movie.voteCount }} 人评</span>
          </div>

          <div class="detail-meta">
            <span v-if="movie.releaseDate">{{ movie.releaseDate }}</span>
            <span v-if="movie.runtime">{{ movie.runtime }}分钟</span>
            <span v-if="movie.originalLanguage">{{ langLabel(movie.originalLanguage) }}</span>
          </div>

          <!-- 类型标签 -->
          <div v-if="genres.length" class="detail-genres">
            <span class="tag" v-for="g in genres" :key="g.id" @click="$router.push({ path:'/library', query:{genreId:g.id} })">
              {{ g.name }}
            </span>
          </div>

          <!-- 导演/演员 -->
          <div v-if="directors.length" class="detail-people">
            <strong>导演：</strong>{{ directors.map(d => d.name).join('、') }}
          </div>
          <div v-if="cast.length" class="detail-people">
            <strong>主演：</strong>{{ cast.slice(0, 5).map(c => c.name).join('、') }}
          </div>

          <div style="margin-top:24px;display:flex;gap:12px">
            <button class="play-btn" @click="$router.push(`/play/${movie.id}`)">▶ 立即播放</button>
            <el-button :type="isFav ? 'warning' : 'default'" @click="toggleFav">
              {{ isFav ? '★ 已收藏' : '☆ 收藏' }}
            </el-button>
          </div>
        </div>
      </div>

      <!-- 剧情简介 -->
      <div class="detail-section" v-if="movie.overview">
        <h3 class="section-title">剧情简介</h3>
        <p :class="['synopsis', { expanded: synopsisExpanded }]">{{ movie.overview }}</p>
        <span v-if="movie.overview?.length > 100" class="expand-btn" @click="synopsisExpanded = !synopsisExpanded">
          {{ synopsisExpanded ? '收起' : '展开' }}
        </span>
      </div>

      <!-- 播放资源 -->
      <div class="detail-section" v-if="sources.length">
        <h3 class="section-title">播放资源</h3>
        <div class="source-list">
          <div v-for="s in sources" :key="s.id" class="source-item"
            @click="$router.push({ path:`/play/${movie.id}`, query:{ sourceId: s.id } })">
            <span>{{ s.sourceName }}</span>
            <span class="source-meta">
              <span class="tag tag-4k">{{ s.quality }}</span>
              <span style="font-size:12px;color:var(--text-muted)">{{ s.stability }}</span>
              <span v-if="s.isRecommended" class="tag" style="background:rgba(34,197,94,0.15);color:#22c55e">推荐</span>
            </span>
          </div>
        </div>
      </div>

      <!-- 演员列表 -->
      <div class="detail-section" v-if="cast.length">
        <h3 class="section-title">演员</h3>
        <div class="cast-list">
          <div v-for="c in cast" :key="c.name" class="cast-card">
            <img :src="profileUrl(c.profilePath)" :alt="c.name" class="cast-avatar" />
            <div class="cast-name">{{ c.name }}</div>
            <div class="cast-char">{{ c.character }}</div>
          </div>
        </div>
      </div>

      <!-- 相关推荐 -->
      <div class="detail-section" v-if="related.length">
        <h3 class="section-title">相关推荐</h3>
        <div class="movie-grid">
          <div class="movie-card" v-for="m in related" :key="m.id" @click="$router.push(`/movie/${m.id}`)">
            <img class="poster" :src="posterUrl(m.posterPath)" :alt="m.title" loading="lazy" />
            <div class="info">
              <div class="title">{{ m.title }}</div>
              <div class="meta"><span class="score">★ {{ m.voteAverage }}</span></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="loading" style="text-align:center;padding:120px;color:var(--text-muted)">加载中...</div>
    <div v-else style="text-align:center;padding:120px;color:var(--text-muted)">
      电影不存在 <el-button text @click="$router.push('/')">返回首页</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import api, { posterUrl, profileUrl } from '../api'
import { useUserStore } from '../stores/user'

const route = useRoute()
const userStore = useUserStore()

const movie = ref(null)
const genres = ref([])
const cast = ref([])
const directors = ref([])
const sources = ref([])
const related = ref([])
const loading = ref(true)
const isFav = ref(false)
const synopsisExpanded = ref(false)

const langMap = { zh: '华语', en: '英语', ja: '日语', ko: '韩语', fr: '法语', de: '德语' }
function langLabel(code) { return langMap[code] || code }

async function loadDetail(id) {
  loading.value = true
  try {
    const res = await api.get(`/movie/${id}`)
    const data = res.data.data
    movie.value = data.movie
    genres.value = data.genres || []
    cast.value = data.cast || []
    directors.value = (data.crew || []).filter(c => c.job === 'Director')
    sources.value = data.sources || []

    // 相关推荐
    const relRes = await api.get(`/movie/${id}/related`, { params: { limit: 6 } })
    related.value = relRes.data.data || []

    // 检查收藏状态
    if (userStore.isLoggedIn) {
      const favRes = await api.get('/user/favorite/check', { params: { userId: userStore.user.id, movieId: id } })
      isFav.value = favRes.data.data
    }
  } catch (e) {
    movie.value = null
  } finally {
    loading.value = false
  }
}

async function toggleFav() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    if (isFav.value) {
      await api.delete('/user/favorite', { params: { userId: userStore.user.id, movieId: movie.value.id } })
      isFav.value = false
      ElMessage.success('已取消收藏')
    } else {
      await api.post('/user/favorite', { userId: userStore.user.id, movieId: movie.value.id })
      isFav.value = true
      ElMessage.success('收藏成功')
    }
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '操作失败')
  }
}

onMounted(() => loadDetail(route.params.id))
watch(() => route.params.id, (id) => { if (id) loadDetail(id) })
</script>

<style scoped>
.detail-hero { display:flex; gap:32px; margin-bottom:40px; }
.detail-poster { width:300px; border-radius:var(--radius); object-fit:cover; }
.detail-info { flex:1; }
.detail-info h1 { font-size:28px; margin-bottom:8px; }
.original-title { color:var(--text-muted); font-size:14px; margin-bottom:16px; }
.detail-score { display:flex; align-items:baseline; gap:8px; margin-bottom:12px; }
.score-num { font-size:28px; font-weight:700; color:var(--gold); }
.score-count { font-size:13px; color:var(--text-muted); }
.detail-meta { display:flex; gap:16px; font-size:14px; color:var(--text-secondary); margin-bottom:12px; }
.detail-genres { display:flex; gap:8px; flex-wrap:wrap; margin-bottom:12px; }
.detail-genres .tag { cursor:pointer; }
.detail-people { font-size:14px; color:var(--text-secondary); margin-bottom:6px; }
.detail-people strong { color:var(--text-primary); }
.detail-section { margin-bottom:40px; }
.synopsis { color:var(--text-secondary); font-size:14px; line-height:1.8; max-height:56px; overflow:hidden; transition:max-height 0.3s; }
.synopsis.expanded { max-height:none; }
.expand-btn { font-size:13px; color:var(--accent); cursor:pointer; }
.source-list { display:flex; flex-direction:column; gap:8px; }
.source-item { display:flex; justify-content:space-between; align-items:center; padding:12px 16px; background:var(--bg-card); border-radius:var(--radius-sm); cursor:pointer; transition:var(--transition); }
.source-item:hover { background:var(--bg-card-hover); }
.source-meta { display:flex; gap:8px; align-items:center; }
.cast-list { display:flex; gap:16px; overflow-x:auto; padding-bottom:8px; }
.cast-card { flex-shrink:0; width:100px; text-align:center; }
.cast-avatar { width:80px; height:80px; border-radius:50%; object-fit:cover; background:var(--bg-card); }
.cast-name { font-size:13px; margin-top:8px; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
.cast-char { font-size:11px; color:var(--text-muted); white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }

@media (max-width:768px) {
  .detail-hero { flex-direction:column; }
  .detail-poster { width:100%; max-width:300px; }
}
</style>
