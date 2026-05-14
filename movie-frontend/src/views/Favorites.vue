<template>
  <div class="favorites-page">
    <h2>我的收藏</h2>
    <div class="movie-grid" v-loading="loading">
      <div class="movie-card" v-for="movie in movies" :key="movie.id" @click="$router.push(`/movie/${movie.id}`)">
        <div class="poster">
          <img :src="movie.posterPath || '/no-poster.png'" :alt="movie.movieName" />
          <div class="rating">{{ movie.voteAverage }}</div>
        </div>
        <div class="info">
          <h3>{{ movie.movieName }}</h3>
          <p>{{ movie.releaseYear }}</p>
        </div>
      </div>
    </div>
    <div v-if="movies.length === 0 && !loading" class="empty">暂无收藏</div>
    <div class="pagination" v-if="total > 12">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="12"
        v-model:current-page="page" @current-change="loadData" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getFavoriteList } from '../api/favorite'

const loading = ref(false)
const movies = ref<any[]>([])
const page = ref(1)
const total = ref(0)

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const res: any = await getFavoriteList({ page: page.value, size: 12 })
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
.favorites-page h2 { color: #fff; margin-bottom: 20px; }
.movie-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 20px; }
.movie-card { cursor: pointer; border-radius: 8px; overflow: hidden; background: #1a1a2e; transition: transform 0.3s; }
.movie-card:hover { transform: translateY(-5px); box-shadow: 0 8px 25px rgba(229,9,20,0.3); }
.poster { position: relative; aspect-ratio: 2/3; overflow: hidden; }
.poster img { width: 100%; height: 100%; object-fit: cover; }
.rating { position: absolute; top: 8px; right: 8px; background: rgba(229,9,20,0.9); color:#fff; padding: 2px 8px; border-radius: 4px; font-size: 13px; font-weight: bold; }
.info { padding: 10px; }
.info h3 { font-size: 14px; color: #fff; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.info p { font-size: 12px; color: #888; margin-top: 4px; }
.empty { text-align: center; color: #666; padding: 60px 0; }
.pagination { display: flex; justify-content: center; margin-top: 30px; }
</style>
