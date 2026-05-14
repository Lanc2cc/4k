<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:20px">
      <h2 style="color:#fff">电影管理</h2>
      <el-button type="danger" @click="showAddDialog = true">添加电影</el-button>
    </div>

    <el-table :data="movies" style="width:100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column label="海报" width="80">
        <template #default="{ row }">
          <img :src="row.posterPath" style="width:50px;height:75px;object-fit:cover;border-radius:4px" />
        </template>
      </el-table-column>
      <el-table-column prop="movieName" label="电影名称" min-width="150" />
      <el-table-column prop="releaseYear" label="年份" width="80" />
      <el-table-column prop="voteAverage" label="评分" width="80" />
      <el-table-column prop="tmdbId" label="TMDB ID" width="100" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="info" @click="openSourceDialog(row)">播放源</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="display:flex;justify-content:center;margin-top:20px">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="10"
        v-model:current-page="page" @current-change="loadData" />
    </div>

    <!-- 添加/编辑电影弹窗 -->
    <el-dialog v-model="showAddDialog" :title="editingId ? '编辑电影' : '添加电影'" width="600px">
      <el-form :model="movieForm" label-width="100px">
        <el-form-item label="电影名称"><el-input v-model="movieForm.movieName" /></el-form-item>
        <el-form-item label="原始标题"><el-input v-model="movieForm.originalTitle" /></el-form-item>
        <el-form-item label="简介"><el-input v-model="movieForm.overview" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="海报URL"><el-input v-model="movieForm.posterPath" /></el-form-item>
        <el-form-item label="背景图URL"><el-input v-model="movieForm.backdropPath" /></el-form-item>
        <el-form-item label="上映年份"><el-input v-model="movieForm.releaseYear" /></el-form-item>
        <el-form-item label="评分"><el-input-number v-model="movieForm.voteAverage" :min="0" :max="10" :step="0.1" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="movieForm.categoryId" placeholder="选择分类">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="movieForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="danger" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 播放源弹窗 -->
    <el-dialog v-model="showSourceDialog" title="管理播放源" width="600px">
      <div v-for="s in currentSources" :key="s.id" style="display:flex;gap:10px;align-items:center;margin-bottom:10px">
        <span style="color:#fff">{{ s.sourceName }} ({{ s.quality }})</span>
        <span style="color:#888;flex:1;overflow:hidden;text-overflow:ellipsis">{{ s.sourceUrl }}</span>
        <el-button size="small" type="danger" @click="handleDeleteSource(s.id)">删除</el-button>
      </div>
      <el-divider />
      <el-form :model="sourceForm" label-width="80px" inline>
        <el-form-item label="名称"><el-input v-model="sourceForm.sourceName" placeholder="如: 线路1" style="width:120px" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="sourceForm.sourceUrl" placeholder="播放地址" style="width:200px" /></el-form-item>
        <el-form-item label="画质"><el-input v-model="sourceForm.quality" placeholder="4K" style="width:80px" /></el-form-item>
        <el-button type="danger" @click="handleAddSource">添加</el-button>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminMovies, addMovie, updateMovie, deleteMovie, addSource, deleteSource } from '../../api/admin'
import { getCategories, getMovieSources } from '../../api/movie'

const loading = ref(false)
const movies = ref<any[]>([])
const categories = ref<any[]>([])
const page = ref(1)
const total = ref(0)
const showAddDialog = ref(false)
const editingId = ref<number | null>(null)
const showSourceDialog = ref(false)
const currentMovieId = ref<number | null>(null)
const currentSources = ref<any[]>([])

const movieForm = ref<any>({
  movieName: '', originalTitle: '', overview: '', posterPath: '', backdropPath: '',
  releaseYear: '', voteAverage: 0, categoryId: null, status: 1
})

const sourceForm = ref({ sourceName: '', sourceUrl: '', quality: '1080P' })

onMounted(async () => {
  const catRes: any = await getCategories()
  if (catRes.code === 200) categories.value = catRes.data
  loadData()
})

async function loadData() {
  loading.value = true
  try {
    const res: any = await getAdminMovies({ page: page.value, size: 10 })
    if (res.code === 200) { movies.value = res.data.records; total.value = res.data.total }
  } finally { loading.value = false }
}

function openEdit(row: any) {
  editingId.value = row.id
  movieForm.value = { ...row }
  showAddDialog.value = true
}

async function handleSave() {
  if (editingId.value) {
    const res: any = await updateMovie(movieForm.value)
    if (res.code === 200) { ElMessage.success('更新成功'); showAddDialog.value = false; editingId.value = null; loadData() }
  } else {
    const res: any = await addMovie(movieForm.value)
    if (res.code === 200) { ElMessage.success('添加成功'); showAddDialog.value = false; loadData() }
  }
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('确定删除该电影？', '警告', { type: 'warning' })
  const res: any = await deleteMovie(id)
  if (res.code === 200) { ElMessage.success('删除成功'); loadData() }
}

async function openSourceDialog(row: any) {
  currentMovieId.value = row.id
  const res: any = await getMovieSources(row.id)
  if (res.code === 200) currentSources.value = res.data
  showSourceDialog.value = true
}

async function handleAddSource() {
  if (!sourceForm.value.sourceName || !sourceForm.value.sourceUrl) { ElMessage.warning('请填写完整'); return }
  const res: any = await addSource({ ...sourceForm.value, movieId: currentMovieId.value })
  if (res.code === 200) {
    ElMessage.success('添加成功')
    sourceForm.value = { sourceName: '', sourceUrl: '', quality: '1080P' }
    openSourceDialog({ id: currentMovieId.value })
  }
}

async function handleDeleteSource(id: number) {
  const res: any = await deleteSource(id)
  if (res.code === 200) {
    ElMessage.success('删除成功')
    openSourceDialog({ id: currentMovieId.value })
  }
}
</script>
