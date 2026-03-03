import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/HomeView.vue')
    },
    {
      path: '/library',
      name: 'library',
      component: () => import('../views/LibraryView.vue')
    },
    {
      path: '/movie/:id',
      name: 'detail',
      component: () => import('../views/DetailView.vue')
    },
    {
      path: '/play/:id',
      name: 'play',
      component: () => import('../views/PlayView.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue')
    }
  ],
  scrollBehavior() {
    return { top: 0 }
  }
})

export default router
