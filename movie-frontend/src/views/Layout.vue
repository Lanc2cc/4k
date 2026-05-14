<template>
  <div class="layout">
    <!-- 顶部导航 -->
    <header class="navbar">
      <div class="navbar-inner">
        <router-link to="/" class="logo">🎬 4K影视</router-link>
        <nav class="nav-links">
          <router-link to="/">首页</router-link>
          <router-link to="/movies">电影</router-link>
          <router-link to="/search">搜索</router-link>
        </nav>
        <div class="nav-right">
          <template v-if="userStore.isLoggedIn">
            <el-dropdown>
              <span class="user-info">
                {{ userStore.nickname || userStore.username }}
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="$router.push('/profile')">个人中心</el-dropdown-item>
                  <el-dropdown-item @click="$router.push('/favorites')">我的收藏</el-dropdown-item>
                  <el-dropdown-item @click="$router.push('/history')">观看历史</el-dropdown-item>
                  <el-dropdown-item v-if="userStore.role === '1'" divided @click="$router.push('/admin')">
                    后台管理
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link to="/login" class="nav-btn">登录</router-link>
          </template>
        </div>
      </div>
    </header>

    <!-- 主内容 -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- 底部 -->
    <footer class="footer">
      <p>© 2026 4K影视资源网 - 毕业设计项目</p>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ArrowDown } from '@element-plus/icons-vue'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()

function handleLogout() {
  userStore.logout()
  router.push('/')
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.navbar {
  background: #141414;
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: 1px solid #222;
}
.navbar-inner {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.logo {
  font-size: 22px;
  font-weight: bold;
  color: #e50914;
}
.nav-links {
  display: flex;
  gap: 25px;
}
.nav-links a {
  color: #b3b3b3;
  font-size: 15px;
  transition: color 0.2s;
}
.nav-links a:hover, .nav-links a.router-link-active {
  color: #fff;
}
.nav-right {
  display: flex;
  align-items: center;
}
.user-info {
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
}
.nav-btn {
  padding: 6px 20px;
  background: #e50914;
  color: #fff;
  border-radius: 4px;
  font-size: 14px;
}
.main-content {
  flex: 1;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
  padding: 20px;
}
.footer {
  text-align: center;
  padding: 20px;
  color: #666;
  font-size: 13px;
  border-top: 1px solid #222;
}
</style>
