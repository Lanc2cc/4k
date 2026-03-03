import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
    const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
    const isLoggedIn = ref(!!user.value)

    function setUser(userData) {
        user.value = userData
        isLoggedIn.value = true
        localStorage.setItem('user', JSON.stringify(userData))
    }

    function logout() {
        user.value = null
        isLoggedIn.value = false
        localStorage.removeItem('user')
    }

    return { user, isLoggedIn, setUser, logout }
})
