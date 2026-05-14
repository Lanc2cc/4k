<template>
  <div class="admin-layout">
    <el-container>
      <el-aside width="220px" class="admin-aside">
        <div class="admin-logo">🎬 4K影视管理</div>
        <el-menu :default-active="activeMenu" router background-color="#1a1a2e" text-color="#b3b3b3" active-text-color="#e50914">
          <el-menu-item index="/admin">
            <span>📊 仪表盘</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <span>👥 用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/movies">
            <span>🎬 电影管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/comments">
            <span>💬 评论管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/categories">
            <span>📁 分类管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/sync">
            <span>🔄 TMDB同步</span>
          </el-menu-item>
          <el-menu-item index="/admin/logs">
            <span>📋 同步日志</span>
          </el-menu-item>
          <el-divider />
          <el-menu-item @click="$router.push('/')">
            <span>🏠 返回前台</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
}
.admin-aside {
  background: #1a1a2e;
  border-right: 1px solid #333;
  min-height: 100vh;
}
.admin-logo {
  padding: 20px;
  font-size: 18px;
  font-weight: bold;
  color: #e50914;
  text-align: center;
  border-bottom: 1px solid #333;
}
.admin-main {
  background: #0f0f0f;
  padding: 25px;
}
</style>
