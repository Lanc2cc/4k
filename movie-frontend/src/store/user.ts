import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userId = ref(localStorage.getItem('userId') || '')
  const username = ref(localStorage.getItem('username') || '')
  const nickname = ref(localStorage.getItem('nickname') || '')
  const avatar = ref(localStorage.getItem('avatar') || '')
  const role = ref(localStorage.getItem('role') || '0')
  const isLoggedIn = ref(!!localStorage.getItem('token'))

  function setUser(data: any) {
    token.value = data.token
    userId.value = data.userId
    username.value = data.username
    nickname.value = data.nickname || data.username
    avatar.value = data.avatar || ''
    role.value = String(data.role)
    isLoggedIn.value = true

    localStorage.setItem('token', data.token)
    localStorage.setItem('userId', data.userId)
    localStorage.setItem('username', data.username)
    localStorage.setItem('nickname', data.nickname || data.username)
    localStorage.setItem('avatar', data.avatar || '')
    localStorage.setItem('role', String(data.role))
  }

  function logout() {
    token.value = ''
    userId.value = ''
    username.value = ''
    nickname.value = ''
    avatar.value = ''
    role.value = '0'
    isLoggedIn.value = false

    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
    localStorage.removeItem('nickname')
    localStorage.removeItem('avatar')
    localStorage.removeItem('role')
  }

  return { token, userId, username, nickname, avatar, role, isLoggedIn, setUser, logout }
})
