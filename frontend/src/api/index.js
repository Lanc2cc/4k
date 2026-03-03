import axios from 'axios'

const api = axios.create({
    baseURL: 'http://localhost:8088/api',
    timeout: 15000
})

// TMDB 图片基础URL
export const TMDB_IMG = 'https://image.tmdb.org/t/p'
export const posterUrl = (path) => path ? `${TMDB_IMG}/w500${path}` : '/placeholder.jpg'
export const backdropUrl = (path) => path ? `${TMDB_IMG}/w1280${path}` : ''
export const profileUrl = (path) => path ? `${TMDB_IMG}/w185${path}` : ''

export default api
