<template>
  <div class="movie-list-page">
    <h2>电影列表</h2>

    <!-- 分类筛选 -->
    <div class="category-bar">
      <el-tag
        :type="!currentCategory ? 'danger' : 'info'"
        @click="currentCategory = null; loadMovies()"
        class="cat-tag"
      >全部</el-tag>
      <el-tag
        v-for="cat in categories"
        :key="cat.id"
        :type="currentCategory === cat.id ? 'danger' : 'info'"
        @click="currentCategory = cat.id; loadMovies()"
        class="cat-tag"
      >{{ cat.name }}</el-tag>
    </div>

    <!-- 电影网格 -->
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

    <div v-if="movies.length === 0 && !loading" class="empty">暂无电影数据</div>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        v-model:current-page="currentPage"
        @current-change="loadMovies"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getMovieList, getCategories } from '../api/movie'

const loading = ref(false)
const movies = ref<any[]>([])
const categories = ref<any[]>([])
const currentCategory = ref<number | null>(null)
const currentPage = ref(1)
const pageSize = 24
const total = ref(0)

onMounted(async () => {
  const catRes: any = await getCategories()
  if (catRes.code === 200) categories.value = catRes.data
  loadMovies()
})

async function loadMovies() {
  loading.value = true
  try {
    const res: any = await getMovieList({
      page: currentPage.value,
      size: pageSize,
      categoryId: currentCategory.value || undefined
    })
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
.movie-list-page h2 {
  margin-bottom: 20px;
  color: #fff;
}
.category-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}
.cat-tag {
  cursor: pointer;
  font-size: 13px;
}
.movie-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 20px;
  min-height: 200px;
}
.movie-card {
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  background: #1a1a2e;
  transition: transform 0.3s;
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
.poster img { width: 100%; height: 100%; object-fit: cover; }
.rating {
  position: absolute;
  top: 8px; right: 8px;
  background: rgba(229,9,20,0.9);
  color: #fff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 13px;
  font-weight: bold;
}
.info { padding: 10px; }
.info h3 { font-size: 14px; color: #fff; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.info p { font-size: 12px; color: #888; margin-top: 4px; }
.empty { text-align: center; color: #666; padding: 60px 0; }
.pagination { display: flex; justify-content: center; margin-top: 30px; }
</style>
