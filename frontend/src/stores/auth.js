import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || null)
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  
  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  
  async function login(credentials) {
    const response = await authApi.login(credentials)
    if (response.success) {
      setAuth(response.data)
    }
    return response
  }
  
  async function register(userData) {
    const response = await authApi.register(userData)
    if (response.success) {
      setAuth(response.data)
    }
    return response
  }
  
  async function fetchCurrentUser() {
    if (!token.value) return null
    try {
      const response = await authApi.getCurrentUser()
      if (response.success) {
        user.value = response.data
        localStorage.setItem('user', JSON.stringify(response.data))
      }
      return response
    } catch (error) {
      logout()
      throw error
    }
  }
  
  function setAuth(data) {
    token.value = data.token
    user.value = {
      id: data.id,
      username: data.username,
      email: data.email,
      role: data.role
    }
    localStorage.setItem('token', data.token)
    localStorage.setItem('user', JSON.stringify(user.value))
  }
  
  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }
  
  return {
    token,
    user,
    isAuthenticated,
    isAdmin,
    login,
    register,
    fetchCurrentUser,
    logout
  }
})
