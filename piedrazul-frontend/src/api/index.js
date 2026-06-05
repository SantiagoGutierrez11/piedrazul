import axios from 'axios'

const API_BASE_URL = 'http://localhost:8090'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
})

// --- Identity Service ---
export const identityApi = {
  login:       (credentials) => api.post('/api/v1/identity/login', credentials),
  getUserById: (id)          => api.get(`/api/v1/identity/users/${id}`),
  register:    (data)        => api.post('/api/v1/identity/register', data),
}

// --- Patient Service ---
export const patientApi = {
  registerWeb: (data) => api.post('/api/v1/patients/register/web', data),
  getById:     (id)   => api.get(`/api/v1/patients/${id}`),
  listAll:     ()     => api.get('/api/v1/patients'),
}

// --- Medical Staff Service ---
export const medicalApi = {
  listDoctors:       ()               => api.get('/api/v1/medical/doctors'),
  getDoctorSchedule: (doctorId)       => {
    const timestamp = new Date().getTime()
    return api.get(`/api/v1/medical/doctors/${doctorId}/schedule?_t=${timestamp}`)
  },
  getAvailability:   (doctorId, date) => {
    const timestamp = new Date().getTime()
    return api.get(`/api/v1/medical/availability?doctorId=${doctorId}&date=${date}&_t=${timestamp}`)
  },
  updateSchedule:    (doctorId, data) =>
      api.put(`/api/v1/medical/doctors/${doctorId}/schedule`, data),
}

// --- Appointment Service ---
export const appointmentApi = {
  create:              (data)           => api.post('/api/v1/appointments', data),
  listAll:             ()               => api.get('/api/v1/appointments'),
  listByDoctorAndDate: (doctorId, date) =>
      api.get(`/api/v1/appointments/doctor/${doctorId}/date/${date}`),
  listByPatient:       (patientId)      =>
      api.get(`/api/v1/appointments/patient/${patientId}`),
  cancel:              (id)             =>
      api.patch(`/api/v1/appointments/${id}/cancel`),
  markAsAttended:      (id)             =>
      api.patch(`/api/v1/appointments/${id}/attend`),
  reschedule:          (id, data)       =>
      api.patch(`/api/v1/appointments/${id}/reschedule`, data),
}

export default api