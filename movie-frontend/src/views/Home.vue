<template>
  <div class="home">
    <!-- Banner -->
    <div class="banner">
      <div class="banner-content">
        <h1>4K影视资源网</h1>
        <p>海量高清电影，尽在眼前</p>
        <el-input
          v-model="searchKeyword"
          placeholder="搜索电影..."
          size="large"
          class="search-input"
          @keyup.enter="goSearch"
        >
          <template #append>
            <el-button @click="goSearch">搜索</el-button>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 热门电影 -->
    <section class="section">
      <h2 class="section-title">🔥 热门电影</h2>
      <div class="movie-grid" v-loading="loading">
        <div class="movie-card" v-for="movie in hotMovies" :key="movie.id" @click="goDetail(movie.id)">
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
    </section>

    <!-- 最新电影 -->
    <section class="section">
      <div class="section-header">
        <h2 class="section-title">🎬 最新上架</h2>
        <router-link to="/movies" class="more-link">查看更多 →</router-link>
      </div>
      <div class="movie-grid">
        <div class="movie-card" v-for="movie in latestMovies" :key="movie.id" @click="goDetail(movie.id)">
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
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getHotMovies, getMovieList } from '../api/movie'

const router = useRouter()
const loading = ref(false)
const hotMovies = ref<any[]>([])
const latestMovies = ref<any[]>([])
const searchKeyword = ref('')

onMounted(async () => {
  loading.value = true
  try {
    const [hotRes, latestRes]: any[] = await Promise.all([
      getHotMovies({ page: 1, size: 12 }),
      getMovieList({ page: 1, size: 12 })
    ])
    if (hotRes.code === 200) hotMovies.value = hotRes.data.records
    if (latestRes.code === 200) latestMovies.value = latestRes.data.records
  } finally {
    loading.value = false
  }
})

function goDetail(id: number) {
  router.push(`/movie/${id}`)
}

function goSearch() {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/search', query: { q: searchKeyword.value.trim() } })
  }
}
</script>

<style scoped>
.banner {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  border-radius: 12px;
  padding: 60px 40px;
  text-align: center;
  margin-bottom: 30px;
}
.banner h1 {
  font-size: 42px;
  color: #e50914;
  margin-bottom: 10px;
}
.banner p {
  color: #b3b3b3;
  font-size: 18px;
  margin-bottom: 25px;
}
.search-input {
  max-width: 500px;
  margin: 0 auto;
}
.section {
  margin-bottom: 40px;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.section-title {
  font-size: 22px;
  margin-bottom: 20px;
  color: #fff;
}
.more-link {
  color: #e50914;
  font-size: 14px;
}
.movie-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 20px;
}
.movie-card {
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  background: #1a1a2e;
  transition: transform 0.3s, box-shadow 0.3s;
}
.movie-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(229,9,20,0.3);
}
.poster {
  position: relative;
  aspect-ratio: 2/3;
  overflow: hidden;
}
.poster img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.rating {
  position: absolute;
  top: 8px;
  right: 8px;
  background: rgba(229,9,20,0.9);
  color: #fff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 13px;
  font-weight: bold;
}
.info {
  padding: 10px;
}
.info h3 {
  font-size: 14px;
  color: #fff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.info p {
  font-size: 12px;
  color: #888;
  margin-top: 4px;
}
</style>
