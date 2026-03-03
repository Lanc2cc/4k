<template>
  <div id="app">
    <!-- 顶部导航栏 -->
    <nav class="nav-bar" v-if="$route.name !== 'play'">
      <div class="nav-logo" @click="$router.push('/')">4K Movie</div>
      <ul class="nav-links">
        <li><router-link to="/">首页</router-link></li>
        <li><router-link to="/library">影库</router-link></li>
      </ul>
      <div class="nav-right">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索电影..."
          :prefix-icon="Search"
          style="width: 220px"
          @keyup.enter="handleSearch"
          size="default"
          clearable
        />
        <template v-if="userStore.isLoggedIn">
          <el-dropdown @command="handleCommand">
            <span style="color: var(--text-primary); cursor: pointer; font-size: 14px">
              {{ userStore.user?.nickname || userStore.user?.username }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" size="small" @click="$router.push('/login')">登录</el-button>
        </template>
      </div>
    </nav>

    <router-view />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search, ArrowDown } from '@element-plus/icons-vue'
import { useUserStore } from './stores/user'

const router = useRouter()
const userStore = useUserStore()
const searchKeyword = ref('')

function handleSearch() {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/library', query: { keyword: searchKeyword.value.trim() } })
  }
}

function handleCommand(cmd) {
  if (cmd === 'logout') {
    userStore.logout()
    router.push('/')
  }
}
</script>

<style scoped>
</style>
