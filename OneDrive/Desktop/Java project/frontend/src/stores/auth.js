import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || null,
    username: localStorage.getItem('username') || null,
    fullName: localStorage.getItem('fullName') || null,
    role: localStorage.getItem('role') || null
  }),
  getters: {
    isAuthenticated: (state) => !!state.token,
    isStudent: (state) => state.role === 'STUDENT',
    isProfessor: (state) => state.role === 'PROFESSOR',
    isAdmin: (state) => state.role === 'ADMIN'
  },
  actions: {
    setUser(data) {
      this.token = data.token
      this.username = data.username
      this.fullName = data.fullName
      this.role = data.role
      localStorage.setItem('token', data.token)
      localStorage.setItem('username', data.username)
      localStorage.setItem('fullName', data.fullName)
      localStorage.setItem('role', data.role)
    },
    logout() {
      this.token = null
      this.username = null
      this.fullName = null
      this.role = null
      localStorage.clear()
    }
  }
})
