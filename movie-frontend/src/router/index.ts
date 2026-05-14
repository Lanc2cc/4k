import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('../views/Home.vue') },
      { path: 'movies', name: 'MovieList', component: () => import('../views/MovieList.vue') },
      { path: 'movie/:id', name: 'MovieDetail', component: () => import('../views/MovieDetail.vue') },
      { path: 'play/:id', name: 'Play', component: () => import('../views/Play.vue') },
      { path: 'search', name: 'Search', component: () => import('../views/Search.vue') },
      { path: 'profile', name: 'Profile', component: () => import('../views/Profile.vue') },
      { path: 'favorites', name: 'Favorites', component: () => import('../views/Favorites.vue') },
      { path: 'history', name: 'History', component: () => import('../views/WatchHistory.vue') },
    ]
  },
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('../views/Register.vue') },
  {
    path: '/admin',
    component: () => import('../views/admin/AdminLayout.vue'),
    children: [
      { path: '', name: 'Dashboard', component: () => import('../views/admin/Dashboard.vue') },
      { path: 'users', name: 'AdminUsers', component: () => import('../views/admin/UserManage.vue') },
      { path: 'movies', name: 'AdminMovies', component: () => import('../views/admin/MovieManage.vue') },
      { path: 'comments', name: 'AdminComments', component: () => import('../views/admin/CommentManage.vue') },
      { path: 'categories', name: 'AdminCategories', component: () => import('../views/admin/CategoryManage.vue') },
      { path: 'sync', name: 'AdminSync', component: () => import('../views/admin/SyncManage.vue') },
      { path: 'logs', name: 'AdminLogs', component: () => import('../views/admin/ApiLogList.vue') },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path.startsWith('/admin')) {
    const role = localStorage.getItem('role')
    if (!token || role !== '1') {
      next('/login')
      return
    }
  }
  if (['/profile', '/favorites', '/history'].includes(to.path)) {
    if (!token) {
      next('/login')
      return
    }
  }
  next()
})

export default router
