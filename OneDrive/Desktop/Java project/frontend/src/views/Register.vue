<template>
  <div class="auth-page">
    <div class="auth-card card">
      <div class="auth-logo">🎓 Universum</div>
      <h2>Create Account</h2>
      <p class="auth-sub">Join the university platform</p>

      <div v-if="error" class="alert alert-error">{{ error }}</div>
      <div v-if="success" class="alert alert-success">{{ success }}</div>

      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label>Full Name</label>
          <input v-model="form.fullName" type="text" placeholder="Your full name" required />
        </div>
        <div class="form-group">
          <label>Username</label>
          <input v-model="form.username" type="text" placeholder="Choose a username" required />
        </div>
        <div class="form-group">
          <label>Email</label>
          <input v-model="form.email" type="email" placeholder="your@email.com" required />
        </div>
        <div class="form-group">
          <label>Password</label>
          <input v-model="form.password" type="password" placeholder="At least 6 characters" required />
        </div>
        <div class="form-group">
          <label>Role</label>
          <select v-model="form.role" required>
            <option value="">Select your role</option>
            <option value="STUDENT">Student</option>
            <option value="PROFESSOR">Professor</option>
          </select>
        </div>
        <button type="submit" class="btn btn-primary full-width" :disabled="loading">
          {{ loading ? 'Creating account...' : 'Create Account' }}
        </button>
      </form>

      <p class="auth-footer">
        Already have an account? <router-link to="/login">Sign in</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { authAPI } from '../service/api'

const router = useRouter()
const form = reactive({ fullName: '', username: '', email: '', password: '', role: '' })
const error = ref('')
const success = ref('')
const loading = ref(false)

async function handleRegister() {
  error.value = ''
  success.value = ''
  loading.value = true
  try {
    await authAPI.register(form)
    success.value = 'Account created! Redirecting to login...'
    setTimeout(() => router.push('/login'), 1500)
  } catch (e) {
    error.value = e.response?.data?.message || 'Registration failed'
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
.auth-card { padding: 36px; width: 100%; max-width: 440px; }
.auth-logo { font-size: 22px; font-weight: 800; color: var(--primary); margin-bottom: 8px; }
h2 { font-size: 24px; font-weight: 700; margin-bottom: 4px; }
.auth-sub { color: var(--text-muted); font-size: 14px; margin-bottom: 24px; }
.full-width { width: 100%; justify-content: center; padding: 12px; font-size: 15px; }
.auth-footer { margin-top: 20px; text-align: center; font-size: 14px; color: var(--text-muted); }
.auth-footer a { color: var(--primary); font-weight: 600; }
</style>
