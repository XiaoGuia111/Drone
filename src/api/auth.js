/**
 * 认证 API 接口封装（Vue 3）
 *
 * 提供登录、登出、获取当前用户等认证相关接口的请求方法。
 * 所有请求自动附带 JWT Token（通过请求拦截器实现）。
 */
import request from '../utils/request'

/**
 * 用户登录
 *
 * @param {string} username 用户名
 * @param {string} password 密码
 * @returns {Promise<{code: number, message: string, data: {user: Object, token: string}}>}
 */
export const login = (username, password) => {
  return request.post('/auth/login', { username, password })
}

/**
 * 用户登出
 *
 * @returns {Promise<{code: number, message: string, data: null}>}
 */
export const logout = () => {
  return request.post('/auth/logout')
}

/**
 * 获取当前登录用户信息
 *
 * @returns {Promise<{code: number, message: string, data: Object}>}
 */
export const getCurrentUser = () => {
  return request.get('/auth/current')
}
