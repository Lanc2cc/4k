<template>
  <div class="login-page">
    <div class="login-card">
      <h2>登录 4K影视</h2>
      <el-form :model="form" @submit.prevent="handleLogin">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-button type="danger" size="large" style="width:100%" :loading="loading" @click="handleLogin">
          登 录
        </el-button>
      </el-form>
      <div class="login-footer">
        还没有账号？<router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api/user'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const form = ref({ username: '', password: '' })

async function handleLogin() {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const res: any = await login(form.value)
    if (res.code === 200) {
      userStore.setUser(res.data)
      ElMessage.success('登录成功')
      if (res.data.role === 1) {
        router.push('/admin')
      } else {
        router.push('/')
      }
    } else {
      ElMessage.error(res.message)
    }
  } catch (e) {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0f0f0f 0%, #1a1a2e 100%);
}
.login-card {
  width: 400px;
  padding: 40px;
  background: #1a1a2e;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.5);
}
.login-card h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #e50914;
  font-size: 28px;
}
.login-footer {
  text-align: center;
  margin-top: 20px;
  color: #b3b3b3;
}
.login-footer a {
  color: #e50914;
  margin-left: 5px;
}
</style>
