<template>
  <div class="dashboard">
    <h2>管理仪表盘</h2>
    <div class="stat-cards" v-loading="loading">
      <div class="stat-card">
        <div class="stat-num">{{ stats.userCount || 0 }}</div>
        <div class="stat-label">用户总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-num">{{ stats.movieCount || 0 }}</div>
        <div class="stat-label">电影总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-num">{{ stats.commentCount || 0 }}</div>
        <div class="stat-label">评论总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-num">{{ stats.favoriteCount || 0 }}</div>
        <div class="stat-label">收藏总数</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDashboard } from '../../api/admin'

const loading = ref(true)
const stats = ref<any>({})

onMounted(async () => {
  try {
    const res: any = await getDashboard()
    if (res.code === 200) stats.value = res.data
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.dashboard h2 { color: #fff; margin-bottom: 25px; }
.stat-cards { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.stat-card {
  background: #1a1a2e; border-radius: 12px; padding: 25px;
  text-align: center; border: 1px solid #333;
}
.stat-num { font-size: 36px; font-weight: bold; color: #e50914; }
.stat-label { color: #b3b3b3; margin-top: 8px; font-size: 14px; }
</style>
