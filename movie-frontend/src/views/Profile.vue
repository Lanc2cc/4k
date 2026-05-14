<template>
  <div class="profile-page">
    <div class="profile-card">
      <h2>个人中心</h2>
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input :value="userStore.username" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-divider>修改密码</el-divider>
        <el-form-item label="原密码">
          <el-input v-model="form.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="form.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="danger" @click="handleUpdate" :loading="loading">保存修改</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserInfo, updateUserInfo } from '../api/user'
import { useUserStore } from '../store/user'

const userStore = useUserStore()
const loading = ref(false)
const form = ref({ nickname: '', email: '', oldPassword: '', newPassword: '' })

onMounted(async () => {
  const res: any = await getUserInfo()
  if (res.code === 200) {
    form.value.nickname = res.data.nickname || ''
    form.value.email = res.data.email || ''
  }
})

async function handleUpdate() {
  loading.value = true
  try {
    const data: any = { nickname: form.value.nickname, email: form.value.email }
    if (form.value.newPassword) {
      data.oldPassword = form.value.oldPassword
      data.newPassword = form.value.newPassword
    }
    const res: any = await updateUserInfo(data)
    if (res.code === 200) {
      ElMessage.success('更新成功')
      if (form.value.nickname) {
        localStorage.setItem('nickname', form.value.nickname)
      }
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.profile-page {
  max-width: 600px;
  margin: 30px auto;
  padding: 0 20px;
}
.profile-card {
  background: #1a1a2e;
  border-radius: 12px;
  padding: 30px;
}
.profile-card h2 {
  text-align: center;
  margin-bottom: 25px;
  color: #e50914;
}
</style>
