/**
 * 无人机 API 接口封装（Vue 3）
 *
 * 提供所有无人机 CRUD 操作的请求方法，包括分页列表和多条件组合搜索。
 */
import request from '../utils/request'

/**
 * 获取无人机分页列表（支持多条件搜索）
 *
 * @param {number} page    页码（从 1 开始）
 * @param {number} size    每页条数
 * @param {Object} params  可选的搜索条件 { name, model, serialNumber, status }
 * @returns {Promise<Object>} 分页结果 { content, totalElements, totalPages, page, size }获取无人机分页列表（支持多条件搜索）
 */
export const getUavList = (page, size, params = {}) => {
  const queryParams = new URLSearchParams({ page, size })
  if (params.name) queryParams.append('name', params.name)
  if (params.model) queryParams.append('model', params.model)
  if (params.serialNumber) queryParams.append('serialNumber', params.serialNumber)
  if (params.status) queryParams.append('status', params.status)
  return request.get(`/uavs?${queryParams.toString()}`)
}

/**
 * 根据 ID 获取无人机详情
 *
 * @param {number} id 无人机主键 ID
 * @returns {Promise<Object>}
 */
export const getUavById = (id) => {
  return request.get(`/uavs/${id}`)
}

/**
 * 新增无人机
 *
 * @param {Object} data 无人机信息（符合 UavDTO 结构）
 * @returns {Promise<Object>} 创建成功的无人机 DTO
 */
export const createUav = (data) => {
  return request.post('/uavs', data)
}

/**
 * 更新无人机信息
 *
 * @param {number} id   待更新无人机的 ID
 * @param {Object} data 新的无人机信息
 * @returns {Promise<Object>} 更新后的无人机 DTO
 */
export const updateUav = (id, data) => {
  return request.put(`/uavs/${id}`, data)
}

/**
 * 删除无人机
 *
 * @param {number} id 待删除无人机的 ID
 * @returns {Promise<Object>}
 */
export const deleteUav = (id) => {
  return request.delete(`/uavs/${id}`)
}
