<template>
  <div class="search-page">
    <h2>搜索电影</h2>
    <el-input
      v-model="keyword"
      placeholder="输入电影名称搜索..."
      size="large"
      @keyup.enter="doSearch"
      class="search-bar"
    >
      <template #append>
        <el-button @click="doSearch">搜索</el-button>
      </template>
    </el-input>

    <div class="movie-grid" v-loading="loading">
      <div class="movie-card" v-for="movie in movies" :key="movie.id" @click="$router.push(`/movie/${movie.id}`)">
        <div class="poster">
          <img :src="movie.posterPath || '/no-poster.png'" :alt="movie.movieName" />
          <div class="rating">{{ movie.voteAverage }}</div>
        </div>
        <div class="info">
          <h3>{{ movie.movieName }}</h3>
          <p>{{ movie.releaseYear }} · {{ movie.categoryName || '未分类' }}</p>
        </div>
      </div>
    </div>

    <div v-if="searched && movies.length === 0 && !loading" class="empty">未找到相关电影</div>

    <div class="pagination" v-if="total > pageSize">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        v-model:current-page="currentPage"
        @current-change="doSearch"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { searchMovies } from '../api/movie'

const route = useRoute()
const loading = ref(false)
const keyword = ref('')
const movies = ref<any[]>([])
const currentPage = ref(1)
const pageSize = 24
const total = ref(0)
const searched = ref(false)

onMounted(() => {
  if (route.query.q) {
    keyword.value = route.query.q as string
    doSearch()
  }
})

async function doSearch() {
  if (!keyword.value.trim()) return
  loading.value = true
  searched.value = true
  try {
    const res: any = await searchMovies({ keyword: keyword.value.trim(), page: currentPage.value, size: pageSize })
    if (res.code === 200) {
      movies.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.search-page h2 { margin-bottom: 20px; color: #fff; }
.search-bar { margin-bottom: 25px; }
.movie-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 20px;
  min-height: 200px;
}
.movie-card {
  cursor: pointer; border-radius: 8px; overflow: hidden;
  background: #1a1a2e; transition: transform 0.3s;
}
.movie-card:hover { transform: translateY(-5px); box-shadow: 0 8px 25px rgba(229,9,20,0.3); }
.poster { position: relative; aspect-ratio: 2/3; overflow: hidden; }
.poster img { width: 100%; height: 100%; object-fit: cover; }
.rating { position: absolute; top: 8px; right: 8px; background: rgba(229,9,20,0.9); color: #fff; padding: 2px 8px; border-radius: 4px; font-size: 13px; font-weight: bold; }
.info { padding: 10px; }
.info h3 { font-size: 14px; color: #fff; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.info p { font-size: 12px; color: #888; margin-top: 4px; }
.empty { text-align: center; color: #666; padding: 60px 0; }
.pagination { display: flex; justify-content: center; margin-top: 30px; }
</style>
