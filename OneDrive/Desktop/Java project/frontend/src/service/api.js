import axios from 'axios'
import router from '../router'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' }
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.clear()
      router.push('/login')
    }
    return Promise.reject(error)
  }
)

export const authAPI = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data)
}

export const courseAPI = {
  getAll: () => api.get('/courses'),
  getById: (id) => api.get(`/courses/${id}`),
  getMyCourses: () => api.get('/courses/my'),
  create: (data) => api.post('/courses', data),
  update: (id, data) => api.put(`/courses/${id}`, data),
  delete: (id) => api.delete(`/courses/${id}`)
}

export const enrollmentAPI = {
  getMyEnrollments: () => api.get('/enrollments/my'),
  getCourseEnrollments: (courseId) => api.get(`/enrollments/course/${courseId}`),
  enroll: (courseId) => api.post(`/enrollments/enroll/${courseId}`),
  updateStatus: (enrollmentId, status) =>
    api.patch(`/enrollments/${enrollmentId}/status?status=${status}`),
  withdraw: (courseId) => api.delete(`/enrollments/withdraw/${courseId}`)
}

export default api
