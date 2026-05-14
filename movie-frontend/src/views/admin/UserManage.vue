<template>
  <div>
    <h2 style="color:#fff;margin-bottom:20px">用户管理</h2>
    <el-table :data="users" style="width:100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="role" label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="row.role === 1 ? 'danger' : 'info'">{{ row.role === 1 ? '管理员' : '用户' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button v-if="row.status === 1" type="danger" size="small" @click="toggleStatus(row.id, 0)">禁用</el-button>
          <el-button v-else type="success" size="small" @click="toggleStatus(row.id, 1)">启用</el-button>
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
import { ElMessage } from 'element-plus'
import { getAdminUsers, updateUserStatus } from '../../api/admin'

const loading = ref(false)
const users = ref<any[]>([])
const page = ref(1)
const total = ref(0)

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const res: any = await getAdminUsers({ page: page.value, size: 10 })
    if (res.code === 200) { users.value = res.data.records; total.value = res.data.total }
  } finally { loading.value = false }
}

async function toggleStatus(userId: number, status: number) {
  const res: any = await updateUserStatus(userId, status)
  if (res.code === 200) { ElMessage.success('更新成功'); loadData() }
}
</script>
