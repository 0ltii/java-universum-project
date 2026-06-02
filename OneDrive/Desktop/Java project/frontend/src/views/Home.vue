<template>
  <div class="home">
    <section class="hero">
      <div class="container hero-content">
        <h1>Welcome to <span class="highlight">Universum</span></h1>
        <p class="hero-subtitle">
          A modern university course registration platform. Enroll in courses,
          manage your academic schedule, and connect with professors all in one place.
        </p>
        <div class="hero-actions">
          <router-link to="/register" class="btn btn-primary">Get Started</router-link>
          <router-link to="/login" class="btn btn-secondary">Sign In</router-link>
        </div>
      </div>
    </section>

    <section class="features container">
      <h2>Why Universum?</h2>
      <div class="features-grid">
        <div class="feature-card card">
          <div class="feature-icon">📚</div>
          <h3>Browse Courses</h3>
          <p>Explore a wide catalogue of courses across all departments and faculties.</p>
        </div>
        <div class="feature-card card">
          <div class="feature-icon">✅</div>
          <h3>Easy Enrollment</h3>
          <p>Enroll in courses with a single click and track your enrollment status in real time.</p>
        </div>
        <div class="feature-card card">
          <div class="feature-icon">🧑‍🏫</div>
          <h3>Professor Tools</h3>
          <p>Professors can create and manage their courses and review student applications.</p>
        </div>
        <div class="feature-card card">
          <div class="feature-icon">🔒</div>
          <h3>Secure Access</h3>
          <p>Role-based access ensures every user sees only what they are supposed to.</p>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.hero {
  background: linear-gradient(135deg, #4f46e5 0%, #06b6d4 100%);
  color: #fff;
  padding: 80px 0;
  text-align: center;
}
.hero-content h1 {
  font-size: 48px;
  font-weight: 800;
  margin-bottom: 16px;
}
.highlight { color: #fde68a; }
.hero-subtitle {
  font-size: 18px;
  opacity: 0.9;
  max-width: 560px;
  margin: 0 auto 32px;
}
.hero-actions { display: flex; gap: 12px; justify-content: center; }
.hero-actions .btn-secondary { background: rgba(255,255,255,0.2); color: #fff; }
.hero-actions .btn-secondary:hover { background: rgba(255,255,255,0.3); }

.features { padding: 64px 24px; }
.features h2 { font-size: 28px; font-weight: 700; text-align: center; margin-bottom: 36px; }
.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 20px;
}
.feature-card { padding: 28px 24px; text-align: center; }
.feature-icon { font-size: 36px; margin-bottom: 12px; }
.feature-card h3 { font-size: 16px; font-weight: 700; margin-bottom: 8px; }
.feature-card p { font-size: 14px; color: var(--text-muted); }
</style>
