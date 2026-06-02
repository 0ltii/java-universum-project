<template>
  <div class="page container">
    <div class="page-header">
      <div>
        <h1>Course Catalogue</h1>
        <p class="page-sub">Browse and enroll in available courses</p>
      </div>
    </div>

    <!-- Filters -->
    <div class="filters card">
      <input v-model="search" type="text" placeholder="🔍 Search by title or code..." class="filter-input" />
      <select v-model="filterStatus" class="filter-select">
        <option value="">All Statuses</option>
        <option value="OPEN">Open</option>
        <option value="CLOSED">Closed</option>
        <option value="CANCELLED">Cancelled</option>
      </select>
    </div>

    <div v-if="loading" class="loading">Loading courses...</div>

    <div v-else-if="filteredCourses.length === 0" class="empty-state card">
      <p>No courses found matching your filters.</p>
    </div>

    <div class="courses-grid" v-else>
      <div v-for="course in filteredCourses" :key="course.id" class="course-card card">
        <div class="course-header">
          <div>
            <h3>{{ course.title }}</h3>
            <code class="course-code">{{ course.code }}</code>
          </div>
          <span :class="`badge badge-${course.status.toLowerCase()}`">{{ course.status }}</span>
        </div>
        <p class="course-desc">{{ course.description || 'No description available.' }}</p>
        <div class="course-info">
          <div class="info-item">🧑‍🏫 <span>{{ course.professorName }}</span></div>
          <div class="info-item">🎓 <span>{{ course.credits }} credits</span></div>
          <div class="info-item">👥 <span>{{ course.enrolledCount }}/{{ course.maxStudents }}</span></div>
        </div>
        <div class="course-footer">
          <template v-if="isStudent">
            <button
              v-if="!enrolledCourseIds.has(course.id) && course.status === 'OPEN'"
              class="btn btn-primary btn-sm"
              @click="enroll(course.id)">
              Enroll
            </button>
            <span v-else-if="enrolledCourseIds.has(course.id)" class="enrolled-tag">
              ✓ Enrolled
            </span>
            <span v-else class="closed-tag">Not Available</span>
          </template>

          <template v-if="isProfessorOrAdmin && course.professorName === fullName">
            <button class="btn btn-secondary btn-sm" @click="viewEnrollments(course)">
              View Enrollments
            </button>
          </template>
        </div>
      </div>
    </div>

    <!-- Enrollments Modal (professor) -->
    <div class="modal-overlay" v-if="selectedCourse" @click.self="selectedCourse = null">
      <div class="modal" style="max-width:620px">
        <div class="modal-header">
          <h3>Enrollments – {{ selectedCourse.title }}</h3>
          <button class="modal-close" @click="selectedCourse = null">×</button>
        </div>
        <div v-if="courseEnrollments.length === 0" class="empty-state">
          <p>No enrollment requests yet.</p>
        </div>
        <table class="table" v-else>
          <thead>
            <tr>
              <th>Student</th>
              <th>Status</th>
              <th>Date</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="e in courseEnrollments" :key="e.id">
              <td>
                <div>{{ e.studentName }}</div>
                <small style="color:var(--text-muted)">@{{ e.studentUsername }}</small>
              </td>
              <td><span :class="`badge badge-${e.status.toLowerCase()}`">{{ e.status }}</span></td>
              <td>{{ formatDate(e.enrolledAt) }}</td>
              <td>
                <div style="display:flex;gap:6px">
                  <button
                    v-if="e.status === 'PENDING'"
                    class="btn btn-success btn-sm"
                    @click="updateStatus(e.id, 'APPROVED')">
                    Approve
                  </button>
                  <button
                    v-if="e.status === 'PENDING'"
                    class="btn btn-danger btn-sm"
                    @click="updateStatus(e.id, 'REJECTED')">
                    Reject
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import { courseAPI, enrollmentAPI } from '../service/api'

const authStore = useAuthStore()
const isStudent = computed(() => authStore.isStudent)
const isProfessorOrAdmin = computed(() => authStore.isProfessor || authStore.isAdmin)
const fullName = computed(() => authStore.fullName)

const courses = ref([])
const myEnrollments = ref([])
const loading = ref(true)
const search = ref('')
const filterStatus = ref('')
const selectedCourse = ref(null)
const courseEnrollments = ref([])

const enrolledCourseIds = computed(() =>
  new Set(myEnrollments.value.map(e => e.courseId))
)

const filteredCourses = computed(() => {
  return courses.value.filter(c => {
    const matchesSearch = !search.value ||
      c.title.toLowerCase().includes(search.value.toLowerCase()) ||
      c.code.toLowerCase().includes(search.value.toLowerCase())
    const matchesStatus = !filterStatus.value || c.status === filterStatus.value
    return matchesSearch && matchesStatus
  })
})

onMounted(async () => {
  const res = await courseAPI.getAll()
  courses.value = res.data
  if (isStudent.value) {
    const enrollRes = await enrollmentAPI.getMyEnrollments()
    myEnrollments.value = enrollRes.data
  }
  loading.value = false
})

async function enroll(courseId) {
  await enrollmentAPI.enroll(courseId)
  const res = await enrollmentAPI.getMyEnrollments()
  myEnrollments.value = res.data
}

async function viewEnrollments(course) {
  selectedCourse.value = course
  const res = await enrollmentAPI.getCourseEnrollments(course.id)
  courseEnrollments.value = res.data
}

async function updateStatus(enrollmentId, status) {
  await enrollmentAPI.updateStatus(enrollmentId, status)
  const res = await enrollmentAPI.getCourseEnrollments(selectedCourse.value.id)
  courseEnrollments.value = res.data
}

function formatDate(dateStr) {
  return new Date(dateStr).toLocaleDateString()
}
</script>

<style scoped>
.page { padding: 32px 24px; }
.page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 24px; }
.page-header h1 { font-size: 26px; font-weight: 800; margin-bottom: 4px; }
.page-sub { color: var(--text-muted); font-size: 14px; }

.filters { padding: 16px 20px; display: flex; gap: 12px; margin-bottom: 24px; }
.filter-input {
  flex: 1;
  padding: 9px 14px;
  border: 1px solid var(--border);
  border-radius: 8px;
  font-size: 14px;
  outline: none;
}
.filter-input:focus { border-color: var(--primary); }
.filter-select {
  padding: 9px 14px;
  border: 1px solid var(--border);
  border-radius: 8px;
  font-size: 14px;
  background: var(--surface);
  outline: none;
}

.loading { text-align: center; padding: 40px; color: var(--text-muted); }
.empty-state { padding: 40px; text-align: center; color: var(--text-muted); }

.courses-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px; }
.course-card { padding: 20px; }
.course-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 10px; }
.course-header h3 { font-size: 15px; font-weight: 700; margin-bottom: 4px; }
.course-code { font-size: 12px; color: var(--primary); background: #ede9fe; padding: 2px 8px; border-radius: 4px; }
.course-desc { font-size: 13px; color: var(--text-muted); margin-bottom: 14px; min-height: 40px; }
.course-info { display: flex; flex-direction: column; gap: 6px; margin-bottom: 16px; }
.info-item { font-size: 13px; color: var(--text-muted); }
.info-item span { font-weight: 500; color: var(--text); }
.course-footer { display: flex; gap: 8px; }

.enrolled-tag { font-size: 13px; font-weight: 600; color: var(--success); }
.closed-tag { font-size: 13px; color: var(--text-muted); }

.table { width: 100%; border-collapse: collapse; }
.table th { text-align: left; padding: 10px 12px; font-size: 12px; font-weight: 700; text-transform: uppercase; color: var(--text-muted); border-bottom: 1px solid var(--border); }
.table td { padding: 10px 12px; font-size: 14px; border-bottom: 1px solid var(--border); }
.table tr:last-child td { border-bottom: none; }
</style>
