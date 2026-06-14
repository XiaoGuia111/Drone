/**
 * Axios HTTP 请求实例（Vue 3）
 *
 * 封装了 baseURL、超时时间、请求/响应拦截器。
 * 所有 API 请求通过此实例发出，确保统一的 JWT Token 携带和错误处理逻辑。
 */
import axios from 'axios'

const request = axios.create({
  // 开发时由 Vite 代理转发，生产环境需配置为后端实际地址
  baseURL: '/api',
  timeout: 10000
})

/**
 * 请求拦截器
 * 在每次请求发出前自动从 localStorage 获取 JWT Token，
 * 并添加至 Authorization header（Bearer 格式）。
 */
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 * 统一处理响应数据和错误：
 * - 成功时直接返回 response.data（解构 { code, message, data }）
 * - 401/403 时清除登录态并跳转登录页
 * - 网络/配置错误时给出友好提示
 */
request.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    console.error('响应错误:', error)
    if (error.response) {
      if (error.response.status === 401 || error.response.status === 403) {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        alert('登录已过期，请重新登录')
        window.location.href = '/login'
        return Promise.reject(error)
      }
    } else if (error.request) {
      alert('网络请求失败，请检查服务器是否正常')
    } else {
      alert('请求配置错误')
    }
    return Promise.reject(error)
  }
)

export default request
