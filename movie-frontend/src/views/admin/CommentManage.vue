<template>
  <div>
    <h2 style="color:#fff;margin-bottom:20px">评论管理</h2>
    <el-table :data="comments" style="width:100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="userId" label="用户ID" width="80" />
      <el-table-column prop="movieId" label="电影ID" width="80" />
      <el-table-column prop="content" label="评论内容" min-width="200" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '显示' : '隐藏' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="时间" width="170" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button v-if="row.status === 1" size="small" @click="toggleStatus(row.id, 0)">隐藏</el-button>
          <el-button v-else size="small" type="success" @click="toggleStatus(row.id, 1)">显示</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div style="display:flex;justify-content:center;margin-top:20px">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="10"
        v-model:current-page="page" @current-change="loadData" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminComments, updateCommentStatus, adminDeleteComment } from '../../api/admin'

const loading = ref(false)
const comments = ref<any[]>([])
const page = ref(1)
const total = ref(0)

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const res: any = await getAdminComments({ page: page.value, size: 10 })
    if (res.code === 200) { comments.value = res.data.records; total.value = res.data.total }
  } finally { loading.value = false }
}

async function toggleStatus(id: number, status: number) {
  const res: any = await updateCommentStatus(id, status)
  if (res.code === 200) { ElMessage.success('更新成功'); loadData() }
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('确定删除该评论？', '警告', { type: 'warning' })
  const res: any = await adminDeleteComment(id)
  if (res.code === 200) { ElMessage.success('删除成功'); loadData() }
}
</script>
