<template>
  <div class="history-page">
    <div class="history-header">
      <h2>观看历史</h2>
      <el-button type="danger" plain size="small" @click="handleClear" v-if="records.length > 0">清空历史</el-button>
    </div>
    <div class="history-list" v-loading="loading">
      <div class="history-item" v-for="item in records" :key="item.id" @click="$router.push(`/movie/${item.movieId}`)">
        <img :src="item.posterPath || '/no-poster.png'" class="thumb" />
        <div class="history-info">
          <h3>{{ item.movieName }}</h3>
          <p>观看时间：{{ item.updateTime }}</p>
        </div>
      </div>
    </div>
    <div v-if="records.length === 0 && !loading" class="empty">暂无观看记录</div>
    <div class="pagination" v-if="total > 12">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="12"
        v-model:current-page="page" @current-change="loadData" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getHistoryList, clearHistory } from '../api/history'

const loading = ref(false)
const records = ref<any[]>([])
const page = ref(1)
const total = ref(0)

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const res: any = await getHistoryList({ page: page.value, size: 12 })
    if (res.code === 200) {
      records.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

async function handleClear() {
  await ElMessageBox.confirm('确定要清空所有观看历史吗？', '提示', { type: 'warning' })
  const res: any = await clearHistory()
  if (res.code === 200) {
    ElMessage.success('已清空')
    records.value = []
    total.value = 0
  }
}
</script>

<style scoped>
.history-page h2 { color: #fff; }
.history-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.history-list { display: flex; flex-direction: column; gap: 12px; }
.history-item {
  display: flex; gap: 15px; padding: 12px;
  background: #1a1a2e; border-radius: 8px; cursor: pointer;
  transition: background 0.2s;
}
.history-item:hover { background: #222244; }
.thumb { width: 80px; height: 120px; object-fit: cover; border-radius: 6px; }
.history-info h3 { color: #fff; margin-bottom: 8px; }
.history-info p { color: #888; font-size: 13px; }
.empty { text-align: center; color: #666; padding: 60px 0; }
.pagination { display: flex; justify-content: center; margin-top: 30px; }
</style>
