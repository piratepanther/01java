import api from '@/utils/api'

export const authApi = {
  login(data) {
    return api.post('/auth/login', data)
  },
  
  register(data) {
    return api.post('/auth/register', data)
  },
  
  getCurrentUser() {
    return api.get('/auth/me')
  }
}

export const pollApi = {
  getActivePolls(params) {
    return api.get('/polls/active', { params })
  },
  
  getPollsByStatus(status, params) {
    return api.get(`/polls/status/${status}`, { params })
  },
  
  getPollById(id) {
    return api.get(`/polls/${id}`)
  },
  
  getMyPolls(params) {
    return api.get('/polls/my', { params })
  },
  
  getPollResults(id) {
    return api.get(`/polls/${id}/results`)
  },
  
  createPoll(data) {
    return api.post('/polls', data)
  },
  
  updatePoll(id, data) {
    return api.put(`/polls/${id}`, data)
  },
  
  deletePoll(id) {
    return api.delete(`/polls/${id}`)
  },
  
  activatePoll(id) {
    return api.post(`/polls/${id}/activate`)
  },
  
  closePoll(id) {
    return api.post(`/polls/${id}/close`)
  },
  
  castVote(data) {
    return api.post('/polls/vote', data)
  }
}

export const adminApi = {
  getAllPolls(params) {
    return api.get('/admin/polls', { params })
  },
  
  createPoll(data) {
    return api.post('/admin/polls', data)
  },
  
  updatePoll(id, data) {
    return api.put(`/admin/polls/${id}`, data)
  },
  
  deletePoll(id) {
    return api.delete(`/admin/polls/${id}`)
  },
  
  activatePoll(id) {
    return api.post(`/admin/polls/${id}/activate`)
  },
  
  closePoll(id) {
    return api.post(`/admin/polls/${id}/close`)
  }
}
