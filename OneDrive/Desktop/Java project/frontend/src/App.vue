<template>
  <div>
    <nav class="navbar" v-if="!isAuthPage">
      <div class="container nav-inner">
        <router-link to="/" class="nav-brand">
          🎓 Universum
        </router-link>
        <div class="nav-links">
          <template v-if="isAuthenticated">
            <router-link to="/dashboard" class="nav-link">Dashboard</router-link>
            <router-link to="/courses" class="nav-link">Courses</router-link>
            <span class="nav-user">👤 {{ fullName }}</span>
            <button class="btn btn-secondary btn-sm" @click="logout">Logout</button>
          </template>
          <template v-else>
            <router-link to="/login" class="nav-link">Sign In</router-link>
            <router-link to="/register" class="btn btn-primary btn-sm">Register</router-link>
          </template>
        </div>
      </div>
    </nav>
    <router-view />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from './stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const isAuthPage = computed(() => ['/login', '/register'].includes(route.path))
const isAuthenticated = computed(() => authStore.isAuthenticated)
const fullName = computed(() => authStore.fullName)

function logout() {
  authStore.logout()
  router.push('/login')
}
</script>

<style>
.navbar {
  background: #fff;
  border-bottom: 1px solid var(--border);
  padding: 0 0;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: var(--shadow);
}
.nav-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
}
.nav-brand {
  font-size: 20px;
  font-weight: 800;
  color: var(--primary);
}
.nav-links {
  display: flex;
  align-items: center;
  gap: 16px;
}
.nav-link {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-muted);
  transition: color 0.2s;
}
.nav-link:hover,
.nav-link.router-link-active { color: var(--primary); }
.nav-user { font-size: 13px; font-weight: 600; color: var(--text); }
</style>
