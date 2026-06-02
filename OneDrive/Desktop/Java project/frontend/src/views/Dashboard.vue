<template>
  <div class="page container">
    <div class="page-header">
      <div>
        <h1>Dashboard</h1>
        <p class="page-sub">Welcome back, {{ fullName }} ({{ role }})</p>
      </div>
      <router-link to="/courses" class="btn btn-primary">Browse All Courses</router-link>
    </div>

    <!-- Stats -->
    <div class="stats-grid">
      <div class="stat-card card">
        <div class="stat-icon">📋</div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.total }}</span>
          <span class="stat-label">{{ isStudent ? 'My Enrollments' : 'My Courses' }}</span>
        </div>
      </div>
      <div class="stat-card card">
        <div class="stat-icon">✅</div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.approved }}</span>
          <span class="stat-label">{{ isStudent ? 'Approved' : 'Open Courses' }}</span>
        </div>
      </div>
      <div class="stat-card card">
        <div class="stat-icon">⏳</div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.pending }}</span>
          <span class="stat-label">Pending</span>
        </div>
      </div>
      <div class="stat-card card" v-if="isStudent">
        <div class="stat-icon">📚</div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.credits }}</span>
          <span class="stat-label">Credits Enrolled</span>
        </div>
      </div>
    </div>

    <!-- Student: My Enrollments -->
    <section v-if="isStudent">
      <h2 class="section-title">My Enrollments</h2>
      <div v-if="enrollments.length === 0" class="empty-state card">
        <p>You haven't enrolled in any courses yet.</p>
        <router-link to="/courses" class="btn btn-primary" style="margin-top:12px">Browse Courses</router-link>
      </div>
      <div class="table-card card" v-else>
        <table class="table">
          <thead>
            <tr>
              <th>Course</th>
              <th>Code</th>
              <th>Status</th>
              <th>Enrolled At</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="e in enrollments" :key="e.id">
              <td>{{ e.courseTitle }}</td>
              <td><code>{{ e.courseCode }}</code></td>
              <td><span :class="`badge badge-${e.status.toLowerCase()}`">{{ e.status }}</span></td>
              <td>{{ formatDate(e.enrolledAt) }}</td>
              <td>
                <button
                  v-if="e.status !== 'WITHDRAWN'"
                  class="btn btn-danger btn-sm"
                  @click="withdraw(e.courseId)">
                  Withdraw
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <!-- Professor/Admin: My Courses -->
    <section v-else>
      <div class="section-header">
        <h2 class="section-title">My Courses</h2>
        <button class="btn btn-primary btn-sm" @click="showCreateModal = true">+ New Course</button>
      </div>
      <div v-if="myCourses.length === 0" class="empty-state card">
        <p>You haven't created any courses yet.</p>
      </div>
      <div class="courses-grid" v-else>
        <div v-for="course in myCourses" :key="course.id" class="course-card card">
          <div class="course-card-header">
            <div>
              <h3>{{ course.title }}</h3>
              <code class="course-code">{{ course.code }}</code>
            </div>
            <span :class="`badge badge-${course.status.toLowerCase()}`">{{ course.status }}</span>
          </div>
          <p class="course-desc">{{ course.description || 'No description provided.' }}</p>
          <div class="course-meta">
            <span>🎓 {{ course.credits }} credits</span>
            <span>👥 {{ course.enrolledCount }}/{{ course.maxStudents }} students</span>
          </div>
          <div class="course-actions">
            <button class="btn btn-secondary btn-sm" @click="openEdit(course)">Edit</button>
            <button class="btn btn-danger btn-sm" @click="deleteCourse(course.id)">Delete</button>
            <router-link :to="`/courses`" class="btn btn-sm btn-primary">View Enrollments</router-link>
          </div>
        </div>
      </div>
    </section>

    <!-- Create/Edit Modal -->
    <div class="modal-overlay" v-if="showCreateModal" @click.self="closeModal">
      <div class="modal">
        <div class="modal-header">
          <h3>{{ editingCourse ? 'Edit Course' : 'Create New Course' }}</h3>
          <button class="modal-close" @click="closeModal">×</button>
        </div>
        <div v-if="modalError" class="alert alert-error">{{ modalError }}</div>
        <form @submit.prevent="saveCourse">
          <div class="form-group">
            <label>Title</label>
            <input v-model="courseForm.title" type="text" placeholder="Course title" required />
          </div>
          <div class="form-group">
            <label>Code</label>
            <input v-model="courseForm.code" type="text" placeholder="e.g. CS101" required />
          </div>
          <div class="form-group">
            <label>Description</label>
            <textarea v-model="courseForm.description" rows="3" placeholder="Course description"></textarea>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Credits</label>
              <input v-model.number="courseForm.credits" type="number" min="1" max="10" required />
            </div>
            <div class="form-group">
              <label>Max Students</label>
              <input v-model.number="courseForm.maxStudents" type="number" min="1" required />
            </div>
          </div>
          <div class="form-group">
            <label>Status</label>
            <select v-model="courseForm.status">
              <option value="OPEN">Open</option>
              <option value="CLOSED">Closed</option>
              <option value="CANCELLED">Cancelled</option>
            </select>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeModal">Cancel</button>
            <button type="submit" class="btn btn-primary" :disabled="saving">
              {{ saving ? 'Saving...' : (editingCourse ? 'Update' : 'Create') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import { courseAPI, enrollmentAPI } from '../service/api'

const authStore = useAuthStore()
const fullName = computed(() => authStore.fullName)
const role = computed(() => authStore.role)
const isStudent = computed(() => authStore.isStudent)

const enrollments = ref([])
const myCourses = ref([])
const showCreateModal = ref(false)
const editingCourse = ref(null)
const saving = ref(false)
const modalError = ref('')

const courseForm = reactive({
  title: '', code: '', description: '', credits: 3, maxStudents: 30, status: 'OPEN'
})

const stats = computed(() => {
  if (isStudent.value) {
    return {
      total: enrollments.value.length,
      approved: enrollments.value.filter(e => e.status === 'APPROVED').length,
      pending: enrollments.value.filter(e => e.status === 'PENDING').length,
      credits: enrollments.value
        .filter(e => e.status === 'APPROVED')
        .length * 3
    }
  }
  return {
    total: myCourses.value.length,
    approved: myCourses.value.filter(c => c.status === 'OPEN').length,
    pending: myCourses.value.reduce((acc, c) => acc + (c.enrolledCount || 0), 0)
  }
})

onMounted(async () => {
  if (isStudent.value) {
    const res = await enrollmentAPI.getMyEnrollments()
    enrollments.value = res.data
  } else {
    const res = await courseAPI.getAll()
    myCourses.value = res.data.filter(c => c.professorName === fullName.value)
  }
})

async function withdraw(courseId) {
  if (!confirm('Withdraw from this course?')) return
  await enrollmentAPI.withdraw(courseId)
  const res = await enrollmentAPI.getMyEnrollments()
  enrollments.value = res.data
}

function openEdit(course) {
  editingCourse.value = course
  Object.assign(courseForm, {
    title: course.title,
    code: course.code,
    description: course.description,
    credits: course.credits,
    maxStudents: course.maxStudents,
    status: course.status
  })
  showCreateModal.value = true
}

function closeModal() {
  showCreateModal.value = false
  editingCourse.value = null
  modalError.value = ''
  Object.assign(courseForm, { title: '', code: '', description: '', credits: 3, maxStudents: 30, status: 'OPEN' })
}

async function saveCourse() {
  saving.value = true
  modalError.value = ''
  try {
    if (editingCourse.value) {
      await courseAPI.update(editingCourse.value.id, courseForm)
    } else {
      await courseAPI.create(courseForm)
    }
    const res = await courseAPI.getAll()
    myCourses.value = res.data.filter(c => c.professorName === fullName.value)
    closeModal()
  } catch (e) {
    modalError.value = e.response?.data?.message || 'Failed to save course'
  } finally {
    saving.value = false
  }
}

async function deleteCourse(id) {
  if (!confirm('Delete this course?')) return
  await courseAPI.delete(id)
  myCourses.value = myCourses.value.filter(c => c.id !== id)
}

function formatDate(dateStr) {
  return new Date(dateStr).toLocaleDateString()
}
</script>

<style scoped>
.page { padding: 32px 24px; }
.page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 28px; }
.page-header h1 { font-size: 26px; font-weight: 800; margin-bottom: 4px; }
.page-sub { color: var(--text-muted); font-size: 14px; }

.stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 16px; margin-bottom: 36px; }
.stat-card { padding: 20px; display: flex; align-items: center; gap: 16px; }
.stat-icon { font-size: 32px; }
.stat-value { display: block; font-size: 28px; font-weight: 800; color: var(--primary); }
.stat-label { font-size: 12px; color: var(--text-muted); font-weight: 600; text-transform: uppercase; }

.section-title { font-size: 18px; font-weight: 700; margin-bottom: 16px; }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.section-header .section-title { margin-bottom: 0; }

.empty-state { padding: 40px; text-align: center; color: var(--text-muted); }

.table-card { overflow-x: auto; }
.table { width: 100%; border-collapse: collapse; }
.table th { text-align: left; padding: 12px 16px; font-size: 12px; font-weight: 700; text-transform: uppercase; color: var(--text-muted); border-bottom: 1px solid var(--border); }
.table td { padding: 12px 16px; font-size: 14px; border-bottom: 1px solid var(--border); }
.table tr:last-child td { border-bottom: none; }
.table code { background: var(--bg); padding: 2px 8px; border-radius: 4px; font-size: 12px; }

.courses-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px; }
.course-card { padding: 20px; }
.course-card-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px; }
.course-card-header h3 { font-size: 15px; font-weight: 700; margin-bottom: 4px; }
.course-code { font-size: 12px; color: var(--primary); background: #ede9fe; padding: 2px 8px; border-radius: 4px; }
.course-desc { font-size: 13px; color: var(--text-muted); margin-bottom: 14px; }
.course-meta { display: flex; gap: 16px; font-size: 13px; color: var(--text-muted); margin-bottom: 16px; }
.course-actions { display: flex; gap: 8px; flex-wrap: wrap; }

.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.modal-footer { display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px; }
</style>
