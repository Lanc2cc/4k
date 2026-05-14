<template>
  <div class="login-page">
    <div class="login-card">
      <h2>注册账号</h2>
      <el-form :model="form" @submit.prevent="handleRegister">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码(至少6位)" prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.nickname" placeholder="昵称(选填)" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.email" placeholder="邮箱(选填)" size="large" />
        </el-form-item>
        <el-button type="danger" size="large" style="width:100%" :loading="loading" @click="handleRegister">
          注 册
        </el-button>
      </el-form>
      <div class="login-footer">
        已有账号？<router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '../api/user'

const router = useRouter()
const loading = ref(false)
const form = ref({ username: '', password: '', nickname: '', email: '' })

async function handleRegister() {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  if (form.value.password.length < 6) {
    ElMessage.warning('密码长度不能少于6位')
    return
  }
  loading.value = true
  try {
    const res: any = await register(form.value)
    if (res.code === 200) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
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
