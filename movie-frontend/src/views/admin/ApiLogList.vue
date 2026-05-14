<template>
  <div>
    <h2 style="color:#fff;margin-bottom:20px">API同步日志</h2>
    <el-table :data="logs" style="width:100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="syncType" label="同步类型" width="120" />
      <el-table-column prop="syncCount" label="同步数量" width="100" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '成功' : '失败' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="message" label="日志信息" min-width="200" show-overflow-tooltip />
      <el-table-column prop="createTime" label="时间" width="170" />
    </el-table>
    <div style="display:flex;justify-content:center;margin-top:20px">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="10"
        v-model:current-page="page" @current-change="loadData" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getApiLogs } from '../../api/admin'

const loading = ref(false)
const logs = ref<any[]>([])
const page = ref(1)
const total = ref(0)

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const res: any = await getApiLogs({ page: page.value, size: 10 })
    if (res.code === 200) { logs.value = res.data.records; total.value = res.data.total }
  } finally { loading.value = false }
}
</script>
