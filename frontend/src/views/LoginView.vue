<template>
  <div class="page-container">
    <div class="login-wrapper">
      <div class="login-card">
        <h2>{{ isRegister ? '注册账号' : '登录' }}</h2>
        <p class="login-subtitle">{{ isRegister ? '加入4K Movie，享受高清观影' : '欢迎回来' }}</p>

        <el-form :model="form" @submit.prevent="handleSubmit" style="margin-top:24px">
          <el-form-item>
            <el-input v-model="form.username" placeholder="用户名" size="large" prefix-icon="User" />
          </el-form-item>
          <el-form-item v-if="isRegister">
            <el-input v-model="form.nickname" placeholder="昵称（选填）" size="large" prefix-icon="UserFilled" />
          </el-form-item>
          <el-form-item>
            <el-input v-model="form.password" type="password" placeholder="密码" size="large" prefix-icon="Lock"
              show-password @keyup.enter="handleSubmit" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" style="width:100%" @click="handleSubmit" :loading="submitting">
              {{ isRegister ? '注册' : '登录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-switch">
          <span v-if="!isRegister">
            还没有账号？<a @click="isRegister = true">注册</a>
          </span>
          <span v-else>
            已有账号？<a @click="isRegister = false">登录</a>
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../api'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const isRegister = ref(false)
const submitting = ref(false)
const form = reactive({ username: '', password: '', nickname: '' })

async function handleSubmit() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  submitting.value = true
  try {
    const url = isRegister.value ? '/user/register' : '/user/login'
    const res = await api.post(url, form)
    if (res.data.code === 200) {
      userStore.setUser(res.data.data)
      ElMessage.success(isRegister.value ? '注册成功' : '登录成功')
      router.push('/')
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (e) {
    ElMessage.error('请求失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.login-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 120px);
}
.login-card {
  width: 400px;
  padding: 40px;
  background: var(--bg-card);
  border-radius: var(--radius);
  border: 1px solid var(--border);
}
.login-card h2 {
  font-size: 24px;
  margin-bottom: 4px;
}
.login-subtitle {
  color: var(--text-muted);
  font-size: 14px;
}
.login-switch {
  text-align: center;
  font-size: 13px;
  color: var(--text-muted);
  margin-top: 8px;
}
.login-switch a {
  color: var(--accent);
  cursor: pointer;
}
</style>
