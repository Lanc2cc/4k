import request from '../utils/request'

export function getDashboard() {
  return request.get('/admin/dashboard')
}

// 用户管理
export function getAdminUsers(params: { page?: number; size?: number }) {
  return request.get('/admin/user/list', { params })
}

export function updateUserStatus(userId: number, status: number) {
  return request.put('/admin/user/status', null, { params: { userId, status } })
}

// 电影管理
export function getAdminMovies(params: { page?: number; size?: number }) {
  return request.get('/admin/movie/list', { params })
}

export function addMovie(data: any) {
  return request.post('/admin/movie/add', data)
}

export function updateMovie(data: any) {
  return request.put('/admin/movie/update', data)
}

export function deleteMovie(id: number) {
  return request.delete(`/admin/movie/delete/${id}`)
}

// 播放源
export function addSource(data: any) {
  return request.post('/admin/source/add', data)
}

export function deleteSource(id: number) {
  return request.delete(`/admin/source/delete/${id}`)
}

// 评论管理
export function getAdminComments(params: { page?: number; size?: number }) {
  return request.get('/admin/comment/list', { params })
}

export function updateCommentStatus(commentId: number, status: number) {
  return request.put('/admin/comment/status', null, { params: { commentId, status } })
}

export function adminDeleteComment(id: number) {
  return request.delete(`/admin/comment/delete/${id}`)
}

// 分类管理
export function addCategory(data: { name: string }) {
  return request.post('/admin/category/add', data)
}

export function deleteCategory(id: number) {
  return request.delete(`/admin/category/delete/${id}`)
}

// TMDB同步
export function syncPopular(count: number) {
  return request.post('/admin/tmdb/sync/popular', null, { params: { count } })
}

export function syncTopRated(count: number) {
  return request.post('/admin/tmdb/sync/top_rated', null, { params: { count } })
}

// API日志
export function getApiLogs(params: { page?: number; size?: number }) {
  return request.get('/admin/api-log/list', { params })
}
