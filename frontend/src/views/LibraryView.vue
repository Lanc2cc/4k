<template>
  <div class="page-container">
    <div style="display:flex;gap:32px">
      <!-- 筛选侧边栏 -->
      <aside class="filter-sidebar" :class="{ collapsed: sidebarCollapsed }">
        <div class="filter-toggle" @click="sidebarCollapsed = !sidebarCollapsed">
          {{ sidebarCollapsed ? '展开筛选 ›' : '‹ 收起' }}
        </div>
        <div v-show="!sidebarCollapsed" class="filter-content">
          <!-- 类型 -->
          <div class="filter-group">
            <h4>类型</h4>
            <div class="filter-tags">
              <span v-for="g in genres" :key="g.id"
                :class="['ftag', { active: selectedGenre === g.id }]"
                @click="selectedGenre = selectedGenre === g.id ? null : g.id; loadMovies()">
                {{ g.name }}
              </span>
            </div>
          </div>
          <!-- 语言 -->
          <div class="filter-group">
            <h4>语言</h4>
            <div class="filter-tags">
              <span v-for="(label, code) in langOptions" :key="code"
                :class="['ftag', { active: selectedLang === code }]"
                @click="selectedLang = selectedLang === code ? '' : code; loadMovies()">
                {{ label }}
              </span>
            </div>
          </div>
          <!-- 年份 -->
          <div class="filter-group">
            <h4>年份</h4>
            <div class="filter-tags">
              <span v-for="y in yearOptions" :key="y.value"
                :class="['ftag', { active: selectedYear === y.value }]"
                @click="selectedYear = selectedYear === y.value ? '' : y.value; loadMovies()">
                {{ y.label }}
              </span>
            </div>
          </div>
          <!-- 评分 -->
          <div class="filter-group">
            <h4>评分</h4>
            <div class="filter-tags">
              <span v-for="s in scoreOptions" :key="s.value"
                :class="['ftag', { active: selectedScore === s.value }]"
                @click="selectedScore = selectedScore === s.value ? null : s.value; loadMovies()">
                {{ s.label }}
              </span>
            </div>
          </div>
          <!-- 排序 -->
          <div class="filter-group">
            <h4>排序</h4>
            <el-select v-model="selectedSort" @change="loadMovies()" size="small" style="width:100%">
              <el-option label="热门优先" value="popular" />
              <el-option label="最新上线" value="latest" />
              <el-option label="评分优先" value="rating" />
            </el-select>
          </div>
        </div>
      </aside>

      <!-- 电影列表 -->
      <main style="flex:1">
        <!-- 已选条件 -->
        <div v-if="hasFilter" style="margin-bottom:16px;display:flex;gap:8px;flex-wrap:wrap">
          <el-tag v-if="selectedGenre" closable @close="selectedGenre = null; loadMovies()">
            类型: {{ genres.find(g => g.id === selectedGenre)?.name }}
          </el-tag>
          <el-tag v-if="selectedLang" closable @close="selectedLang = ''; loadMovies()">
            语言: {{ langOptions[selectedLang] }}
          </el-tag>
          <el-tag v-if="selectedYear" closable @close="selectedYear = ''; loadMovies()">
            年份: {{ selectedYear }}
          </el-tag>
          <el-tag v-if="keyword" closable @close="keyword = ''; loadMovies()">
            搜索: {{ keyword }}
          </el-tag>
        </div>

        <div class="movie-grid">
          <div class="movie-card" v-for="m in movies" :key="m.id" @click="$router.push(`/movie/${m.id}`)">
            <img class="poster" :src="posterUrl(m.posterPath)" :alt="m.title" loading="lazy" />
            <div class="overlay">
              <p style="font-size:12px;color:#ccc;margin-bottom:8px;display:-webkit-box;-webkit-line-clamp:3;-webkit-box-orient:vertical;overflow:hidden">
                {{ m.overview }}
              </p>
              <button class="play-btn" @click.stop="$router.push(`/play/${m.id}`)">▶ 播放</button>
            </div>
            <div class="info">
              <div class="title">{{ m.title }}</div>
              <div class="meta">
                <span class="score">★ {{ m.voteAverage }}</span>
                <span>{{ m.releaseDate?.substring(0, 4) }}</span>
                <span class="tag tag-4k" style="font-size:10px">4K</span>
              </div>
            </div>
          </div>
        </div>

        <div v-if="!loading && movies.length === 0" style="text-align:center;padding:80px;color:var(--text-muted)">
          暂无符合条件的电影
        </div>

        <button v-if="hasMore" class="load-more-btn" @click="loadMore">加载更多</button>
        <div v-if="loading" style="text-align:center;padding:40px;color:var(--text-muted)">加载中...</div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import api, { posterUrl } from '../api'

const route = useRoute()
const movies = ref([])
const genres = ref([])
const loading = ref(false)
const page = ref(1)
const hasMore = ref(true)

const selectedGenre = ref(null)
const selectedLang = ref('')
const selectedYear = ref('')
const selectedScore = ref(null)
const selectedSort = ref('popular')
const keyword = ref('')
const sidebarCollapsed = ref(false)

const langOptions = { zh: '华语', en: '英语', ja: '日语', ko: '韩语', fr: '法语', de: '德语' }
const yearOptions = [
  { label: '2020-2026', value: '2020-2026' },
  { label: '2010-2019', value: '2010-2019' },
  { label: '2000-2009', value: '2000-2009' },
  { label: '更早', value: '1900-1999' }
]
const scoreOptions = [
  { label: '9分以上', value: 9 },
  { label: '8分以上', value: 8 },
  { label: '7分以上', value: 7 }
]

const hasFilter = computed(() => selectedGenre.value || selectedLang.value || selectedYear.value || keyword.value)

async function loadMovies() {
  page.value = 1
  loading.value = true
  try {
    let res
    if (keyword.value) {
      res = await api.get('/movie/search', { params: { keyword: keyword.value, page: 1, size: 20 } })
    } else {
      res = await api.get('/movie/filter', {
        params: {
          language: selectedLang.value || undefined,
          genreId: selectedGenre.value || undefined,
          yearRange: selectedYear.value || undefined,
          minScore: selectedScore.value || undefined,
          sort: selectedSort.value,
          page: 1, size: 20
        }
      })
    }
    const data = res.data.data
    movies.value = data.records || []
    hasMore.value = data.current < data.pages
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function loadMore() {
  page.value++
  loading.value = true
  try {
    const res = await api.get('/movie/filter', {
      params: {
        language: selectedLang.value || undefined,
        genreId: selectedGenre.value || undefined,
        yearRange: selectedYear.value || undefined,
        minScore: selectedScore.value || undefined,
        sort: selectedSort.value,
        page: page.value, size: 20
      }
    })
    const data = res.data.data
    movies.value.push(...(data.records || []))
    hasMore.value = data.current < data.pages
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  // 加载类型
  try {
    const gRes = await api.get('/movie/genres')
    genres.value = gRes.data.data || []
  } catch (e) { console.error(e) }

  // 从URL读取参数
  if (route.query.language) selectedLang.value = route.query.language
  if (route.query.keyword) keyword.value = route.query.keyword

  loadMovies()
})

watch(() => route.query, (q) => {
  if (q.keyword) { keyword.value = q.keyword; loadMovies() }
  if (q.language) { selectedLang.value = q.language; loadMovies() }
})
</script>

<style scoped>
.filter-sidebar {
  width: 240px;
  flex-shrink: 0;
  background: var(--bg-card);
  border-radius: var(--radius);
  padding: 20px;
  height: fit-content;
  position: sticky;
  top: 80px;
}
.filter-sidebar.collapsed { width: auto; padding: 12px; }
.filter-toggle {
  font-size: 13px; color: var(--accent); cursor: pointer; margin-bottom: 12px;
}
.filter-group { margin-bottom: 20px; }
.filter-group h4 { font-size: 13px; color: var(--text-secondary); margin-bottom: 10px; }
.filter-tags { display: flex; flex-wrap: wrap; gap: 8px; }
.ftag {
  padding: 4px 12px; border-radius: 6px; font-size: 12px;
  background: var(--bg-secondary); color: var(--text-secondary);
  cursor: pointer; transition: var(--transition);
}
.ftag:hover { color: var(--text-primary); background: var(--bg-card-hover); }
.ftag.active { background: var(--accent); color: white; }

@media (max-width: 768px) {
  .filter-sidebar { display: none; }
}
</style>
