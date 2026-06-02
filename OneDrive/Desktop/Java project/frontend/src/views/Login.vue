<template>
  <div class="auth-page">
    <div class="auth-card card">
      <div class="auth-logo">🎓 Universum</div>
      <h2>Sign In</h2>
      <p class="auth-sub">Access your university portal</p>

      <div v-if="error" class="alert alert-error">{{ error }}</div>

      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label>Username</label>
          <input v-model="form.username" type="text" placeholder="Enter your username" required />
        </div>
        <div class="form-group">
          <label>Password</label>
          <input v-model="form.password" type="password" placeholder="Enter your password" required />
        </div>
        <button type="submit" class="btn btn-primary full-width" :disabled="loading">
          {{ loading ? 'Signing in...' : 'Sign In' }}
        </button>
      </form>

      <p class="auth-footer">
        Don't have an account? <router-link to="/register">Register here</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { authAPI } from '../service/api'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const form = reactive({ username: '', password: '' })
const error = ref('')
const loading = ref(false)

async function handleLogin() {
  error.value = ''
  loading.value = true
  try {
    const res = await authAPI.login(form)
    authStore.setUser(res.data)
    router.push('/dashboard')
  } catch (e) {
    error.value = e.response?.data?.message || 'Invalid credentials'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #4f46e5 0%, #06b6d4 100%);
  padding: 24px;
}
.auth-card { padding: 36px; width: 100%; max-width: 420px; }
.auth-logo { font-size: 22px; font-weight: 800; color: var(--primary); margin-bottom: 8px; }
h2 { font-size: 24px; font-weight: 700; margin-bottom: 4px; }
.auth-sub { color: var(--text-muted); font-size: 14px; margin-bottom: 24px; }
.full-width { width: 100%; justify-content: center; padding: 12px; font-size: 15px; }
.auth-footer { margin-top: 20px; text-align: center; font-size: 14px; color: var(--text-muted); }
.auth-footer a { color: var(--primary); font-weight: 600; }
</style>
