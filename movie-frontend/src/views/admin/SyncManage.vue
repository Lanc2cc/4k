<template>
  <div class="sync-page">
    <h2 style="color:#fff;margin-bottom:25px">TMDB 电影同步</h2>

    <div class="sync-cards">
      <el-card class="sync-card">
        <h3>🔥 同步热门电影</h3>
        <p>从TMDB获取当前热门电影数据</p>
        <div style="display:flex;gap:10px;align-items:center;margin-top:15px">
          <span style="color:#b3b3b3">同步数量：</span>
          <el-select v-model="popularCount" style="width:120px">
            <el-option :value="50" label="50部" />
            <el-option :value="100" label="100部" />
            <el-option :value="200" label="200部" />
          </el-select>
          <el-button type="danger" @click="handleSyncPopular" :loading="syncingPopular">开始同步</el-button>
        </div>
      </el-card>

      <el-card class="sync-card">
        <h3>⭐ 同步高分电影</h3>
        <p>从TMDB获取评分最高的电影数据</p>
        <div style="display:flex;gap:10px;align-items:center;margin-top:15px">
          <span style="color:#b3b3b3">同步数量：</span>
          <el-select v-model="topRatedCount" style="width:120px">
            <el-option :value="50" label="50部" />
            <el-option :value="100" label="100部" />
            <el-option :value="200" label="200部" />
          </el-select>
          <el-button type="danger" @click="handleSyncTopRated" :loading="syncingTopRated">开始同步</el-button>
        </div>
      </el-card>
    </div>

    <div v-if="syncResult" class="sync-result">
      <el-alert :title="syncResult" type="success" show-icon />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { syncPopular, syncTopRated } from '../../api/admin'

const popularCount = ref(50)
const topRatedCount = ref(50)
const syncingPopular = ref(false)
const syncingTopRated = ref(false)
const syncResult = ref('')

async function handleSyncPopular() {
  syncingPopular.value = true
  syncResult.value = ''
  try {
    const res: any = await syncPopular(popularCount.value)
    if (res.code === 200) {
      syncResult.value = res.data
      ElMessage.success('同步完成')
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    syncingPopular.value = false
  }
}

async function handleSyncTopRated() {
  syncingTopRated.value = true
  syncResult.value = ''
  try {
    const res: any = await syncTopRated(topRatedCount.value)
    if (res.code === 200) {
      syncResult.value = res.data
      ElMessage.success('同步完成')
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    syncingTopRated.value = false
  }
}
</script>

<style scoped>
.sync-cards {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 25px;
}
.sync-card h3 { color: #fff; margin-bottom: 8px; }
.sync-card p { color: #888; }
.sync-result { margin-top: 20px; }
</style>
