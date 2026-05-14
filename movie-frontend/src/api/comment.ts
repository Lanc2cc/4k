import request from '../utils/request'

export function addComment(data: { movieId: number; content: string }) {
  return request.post('/comment/add', data)
}

export function getComments(params: { movieId: number; page?: number; size?: number }) {
  return request.get('/comment/list', { params })
}

export function deleteComment(commentId: number) {
  return request.delete(`/comment/delete/${commentId}`)
}
