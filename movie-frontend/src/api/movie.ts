import request from '../utils/request'

export function getMovieList(params: { page?: number; size?: number; categoryId?: number }) {
  return request.get('/movie/list', { params })
}

export function getMovieDetail(id: number) {
  return request.get(`/movie/detail/${id}`)
}

export function searchMovies(params: { keyword: string; page?: number; size?: number }) {
  return request.get('/movie/search', { params })
}

export function getHotMovies(params?: { page?: number; size?: number }) {
  return request.get('/movie/hot', { params })
}

export function getCategories() {
  return request.get('/movie/categories')
}

export function getMovieSources(movieId: number) {
  return request.get(`/movie/sources/${movieId}`)
}
