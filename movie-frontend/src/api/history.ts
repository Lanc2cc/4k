import request from '../utils/request'

export function addHistory(movieId: number) {
  return request.post('/history/add', null, { params: { movieId } })
}

export function getHistoryList(params?: { page?: number; size?: number }) {
  return request.get('/history/list', { params })
}

export function clearHistory() {
  return request.delete('/history/clear')
}
