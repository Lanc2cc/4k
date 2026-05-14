<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:20px">
      <h2 style="color:#fff">分类管理</h2>
      <div style="display:flex;gap:10px">
        <el-input v-model="newCategoryName" placeholder="分类名称" style="width:200px" />
        <el-button type="danger" @click="handleAdd">添加分类</el-button>
      </div>
    </div>

    <el-table :data="categories" style="width:100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="分类名称" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCategories } from '../../api/movie'
import { addCategory, deleteCategory } from '../../api/admin'

const loading = ref(false)
const categories = ref<any[]>([])
const newCategoryName = ref('')

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const res: any = await getCategories()
    if (res.code === 200) categories.value = res.data
  } finally { loading.value = false }
}

async function handleAdd() {
  if (!newCategoryName.value.trim()) { ElMessage.warning('请输入分类名称'); return }
  const res: any = await addCategory({ name: newCategoryName.value.trim() })
  if (res.code === 200) { ElMessage.success('添加成功'); newCategoryName.value = ''; loadData() }
  else ElMessage.error(res.message)
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('确定删除该分类？', '警告', { type: 'warning' })
  const res: any = await deleteCategory(id)
  if (res.code === 200) { ElMessage.success('删除成功'); loadData() }
}
</script>
