import request from '../utils/request'

export function addFavorite(movieId: number) {
  return request.post('/favorite/add', null, { params: { movieId } })
}

export function removeFavorite(movieId: number) {
  return request.delete('/favorite/remove', { params: { movieId } })
}

export function getFavoriteList(params?: { page?: number; size?: number }) {
  return request.get('/favorite/list', { params })
}

export function checkFavorite(movieId: number) {
  return request.get('/favorite/check', { params: { movieId } })
}
