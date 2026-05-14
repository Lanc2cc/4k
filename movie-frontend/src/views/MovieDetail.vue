<template>
  <div class="movie-detail" v-loading="loading">
    <template v-if="movie">
      <!-- 背景图 -->
      <div class="backdrop" :style="{ backgroundImage: `url(${movie.backdropPath})` }">
        <div class="backdrop-overlay"></div>
      </div>

      <div class="detail-content">
        <div class="detail-left">
          <img :src="movie.posterPath || '/no-poster.png'" :alt="movie.movieName" class="poster" />
        </div>
        <div class="detail-right">
          <h1>{{ movie.movieName }}</h1>
          <p class="original-title" v-if="movie.originalTitle">{{ movie.originalTitle }}</p>
          <div class="meta">
            <el-tag type="danger">{{ movie.voteAverage }} 分</el-tag>
            <el-tag>{{ movie.releaseYear }}</el-tag>
            <el-tag v-if="movie.categoryName">{{ movie.categoryName }}</el-tag>
            <el-tag type="info">{{ movie.voteCount }} 人评分</el-tag>
          </div>
          <p class="overview">{{ movie.overview || '暂无简介' }}</p>
          <div class="actions">
            <el-button type="danger" size="large" @click="goPlay">▶ 在线播放</el-button>
            <el-button
              :type="isFav ? 'warning' : 'default'"
              size="large"
              @click="toggleFavorite"
            >{{ isFav ? '★ 已收藏' : '☆ 收藏' }}</el-button>
          </div>
        </div>
      </div>

      <!-- 评论区 -->
      <div class="comment-section">
        <h3>影评 ({{ commentTotal }})</h3>
        <div v-if="userStore.isLoggedIn" class="comment-input">
          <el-input v-model="newComment" type="textarea" :rows="3" placeholder="写下你的评价..." />
          <el-button type="danger" @click="submitComment" style="margin-top:10px">发表评论</el-button>
        </div>
        <div v-else class="comment-login-tip">
          <router-link to="/login">登录</router-link> 后可以发表评论
        </div>

        <div class="comment-list">
          <div class="comment-item" v-for="c in comments" :key="c.id">
            <div class="comment-header">
              <span class="comment-user">{{ c.username || '匿名' }}</span>
              <span class="comment-time">{{ c.createTime }}</span>
            </div>
            <p class="comment-content">{{ c.content }}</p>
          </div>
          <div v-if="comments.length === 0" class="empty">暂无评论</div>
        </div>

        <div class="pagination" v-if="commentTotal > 10">
          <el-pagination
            background
            layout="prev, pager, next"
            :total="commentTotal"
            :page-size="10"
            v-model:current-page="commentPage"
            @current-change="loadComments"
          />
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMovieDetail } from '../api/movie'
import { checkFavorite, addFavorite, removeFavorite } from '../api/favorite'
import { getComments, addComment } from '../api/comment'
import { useUserStore } from '../store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(true)
const movie = ref<any>(null)
const isFav = ref(false)
const comments = ref<any[]>([])
const commentTotal = ref(0)
const commentPage = ref(1)
const newComment = ref('')

const movieId = Number(route.params.id)

onMounted(async () => {
  try {
    const res: any = await getMovieDetail(movieId)
    if (res.code === 200) movie.value = res.data

    if (userStore.isLoggedIn) {
      const favRes: any = await checkFavorite(movieId)
      if (favRes.code === 200) isFav.value = favRes.data
    }

    await loadComments()
  } finally {
    loading.value = false
  }
})

async function loadComments() {
  const res: any = await getComments({ movieId, page: commentPage.value, size: 10 })
  if (res.code === 200) {
    comments.value = res.data.records
    commentTotal.value = res.data.total
  }
}

async function toggleFavorite() {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  if (isFav.value) {
    await removeFavorite(movieId)
    isFav.value = false
    ElMessage.success('已取消收藏')
  } else {
    await addFavorite(movieId)
    isFav.value = true
    ElMessage.success('收藏成功')
  }
}

async function submitComment() {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  const res: any = await addComment({ movieId, content: newComment.value.trim() })
  if (res.code === 200) {
    ElMessage.success('评论成功')
    newComment.value = ''
    commentPage.value = 1
    await loadComments()
  } else {
    ElMessage.error(res.message)
  }
}

function goPlay() {
  router.push(`/play/${movieId}`)
}
</script>

<style scoped>
.backdrop {
  height: 300px;
  background-size: cover;
  background-position: center;
  border-radius: 12px;
  position: relative;
  margin-bottom: -80px;
}
.backdrop-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(transparent 30%, #0f0f0f 100%);
  border-radius: 12px;
}
.detail-content {
  display: flex;
  gap: 30px;
  position: relative;
  z-index: 1;
  padding: 0 20px;
}
.poster {
  width: 250px;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0,0,0,0.6);
}
.detail-right {
  flex: 1;
  padding-top: 20px;
}
.detail-right h1 {
  font-size: 30px;
  color: #fff;
  margin-bottom: 5px;
}
.original-title {
  color: #888;
  margin-bottom: 10px;
}
.meta {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 15px;
}
.overview {
  color: #ccc;
  line-height: 1.7;
  margin-bottom: 20px;
}
.actions {
  display: flex;
  gap: 12px;
}
.comment-section {
  margin-top: 40px;
  background: #1a1a2e;
  border-radius: 12px;
  padding: 25px;
}
.comment-section h3 {
  color: #fff;
  margin-bottom: 15px;
}
.comment-login-tip {
  color: #888;
  margin-bottom: 15px;
}
.comment-login-tip a {
  color: #e50914;
}
.comment-list {
  margin-top: 15px;
}
.comment-item {
  padding: 12px 0;
  border-bottom: 1px solid #333;
}
.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
}
.comment-user {
  color: #e50914;
  font-weight: bold;
}
.comment-time {
  color: #666;
  font-size: 12px;
}
.comment-content {
  color: #ddd;
  line-height: 1.6;
}
.empty {
  text-align: center;
  color: #666;
  padding: 30px;
}
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
