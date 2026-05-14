import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

// 创建统一请求实例，便于集中管理接口地址与超时时间
const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截器：每次请求自动携带 token，避免页面重复手动传参
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器：统一处理业务错误和登录失效场景
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 401) {
      // token 失效时清理本地登录态并跳转登录页
      localStorage.clear()
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
      return Promise.reject(res)
    }
    return res
  },
  error => {
    // 网络层 401 也统一处理，保证前后端失效逻辑一致
    if (error.response?.status === 401) {
      localStorage.clear()
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
