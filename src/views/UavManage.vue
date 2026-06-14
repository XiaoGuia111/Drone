<template>
  <!--
    ============================================================
    无人机管理主页面
    ============================================================
    系统核心功能页面，提供无人机的全生命周期管理：
    - 多条件组合查询（名称/型号/序列号/状态）
    - 全字段信息展示列表 + 可展开详情面板
    - 新增/编辑/删除无人机记录
    - AI 模拟一键生成无人机属性（测试辅助功能）
    - 自动实时刷新 + 手动刷新
    ============================================================
  -->
  <div id="app-container">
    <!-- ========== 顶部导航栏 ========== -->
    <nav class="navbar navbar-expand navbar-dark top-navbar">
      <div class="container">
        <span class="navbar-brand">
          <span class="brand-icon">&#x1F6F0;</span>
          无人机信息管理系统
        </span>
        <div class="navbar-nav ms-auto">
          <span class="navbar-text user-info">
            <span class="user-icon">&#x1F464;</span>
            {{ currentUser }}
          </span>
          <button @click="handleLogout" class="btn btn-outline-light btn-sm ms-3">退出登录</button>
        </div>
      </div>
    </nav>

    <div class="container mt-4">
      <!-- ========== 查询条件区域 ========== -->
      <div class="card search-card">
        <div class="card-body">
          <h5 class="card-title">
            <span class="section-icon">&#x1F50D;</span>
            查询条件
          </h5>
          <div class="row">
            <div class="form-group col-md-3">
              <label>名称</label>
              <input type="text" class="form-control" v-model="searchForm.name" placeholder="请输入名称" @input="onSearchInput">
            </div>
            <div class="form-group col-md-3">
              <label>型号</label>
              <input type="text" class="form-control" v-model="searchForm.model" placeholder="请输入型号" @input="onSearchInput">
            </div>
            <div class="form-group col-md-3">
              <label>序列号</label>
              <input type="text" class="form-control" v-model="searchForm.serialNumber" placeholder="请输入序列号" @input="onSearchInput">
            </div>
            <div class="form-group col-md-3">
              <label>状态</label>
              <select class="form-control" v-model="searchForm.status" @change="onSearchInput">
                <option value="">全部</option>
                <option value="ACTIVE">运行中</option>
                <option value="INACTIVE">停用</option>
                <option value="MAINTENANCE">维护中</option>
              </select>
            </div>
          </div>
          <button @click="searchUav" class="btn btn-primary me-2">&#x1F50D; 查询</button>
          <button @click="resetSearch" class="btn btn-outline-secondary">&#x21BB; 重置</button>
        </div>
      </div>

      <!-- ========== 操作工具栏 ========== -->
      <div class="toolbar mt-3 d-flex justify-content-between align-items-center flex-wrap gap-2">
        <div class="d-flex align-items-center gap-2">
          <button @click="openAddModal" class="btn btn-primary">&#x2795; 添加无人机</button>
          <button @click="generateByAI" class="btn btn-success">&#x2728; AI 生成属性</button>
        </div>
        <!-- 实时刷新控制区 -->
        <div class="d-flex align-items-center gap-2">
          <!-- 上次更新时间 -->
          <span v-if="lastUpdated" class="text-muted small last-updated">
            &#x1F552; 更新于 {{ lastUpdated }}
          </span>
          <!-- 自动刷新开关 -->
          <div class="form-check form-switch mb-0">
            <input class="form-check-input" type="checkbox" id="autoRefreshSwitch" v-model="autoRefreshEnabled" @change="onAutoRefreshToggle">
            <label class="form-check-label small" for="autoRefreshSwitch">自动刷新</label>
          </div>
          <!-- 手动刷新按钮（加载中时旋转） -->
          <button @click="manualRefresh" class="btn btn-outline-primary btn-sm" :disabled="isLoading">
            <span :class="{ 'spinner-border spinner-border-sm': isLoading, 'refresh-icon': !isLoading }">&#x21BB;</span>
            <span v-if="isLoading" class="ms-1">刷新中...</span>
            <span v-else class="ms-1">刷新</span>
          </button>
        </div>
      </div>

      <!-- ========== 数据表格与详情 ========== -->
      <!-- 加载骨架屏 -->
      <div v-if="isLoading && uavList.length === 0" class="card table-card mt-3">
        <div class="card-body p-4">
          <div v-for="n in 5" :key="n" class="skeleton-row mb-3">
            <div class="skeleton-line skeleton-line-sm me-2"></div>
            <div class="skeleton-line skeleton-line-md me-2"></div>
            <div class="skeleton-line skeleton-line-lg me-2"></div>
            <div class="skeleton-line skeleton-line-md"></div>
          </div>
        </div>
      </div>

      <!-- 数据表格 -->
      <div v-show="!isLoading || uavList.length > 0" class="card table-card mt-3">
        <div class="card-body p-0">
          <table class="table table-hover uav-table mb-0">
            <thead>
              <tr>
                <th style="width:50px">#</th>
                <th>名称</th>
                <th>型号 / 序列号</th>
                <th>制造商</th>
                <th>性能参数</th>
                <th style="width:90px">状态</th>
                <th style="width:110px">操作</th>
              </tr>
            </thead>
            <tbody>
              <!-- 行点击切换展开状态 -->
              <template v-for="uav in uavList" :key="uav.id">
                <tr class="uav-row" :class="{ 'row-expanded': expandedRowId === uav.id }" @click="toggleRowExpand(uav.id)">
                  <td><span class="uav-id-badge">{{ uav.id }}</span></td>
                  <td>
                    <span class="uav-name">{{ uav.name }}</span>
                    <div class="uav-sub-info">{{ uav.model }}</div>
                  </td>
                  <td>
                    <div>{{ uav.model }}</div>
                    <code class="uav-serial">{{ uav.serialNumber }}</code>
                  </td>
                  <td>{{ uav.manufacturer || '-' }}</td>
                  <td>
                    <span class="param-badge" title="最大高度">
                      &#x2191;{{ uav.maxAltitude ? uav.maxAltitude + 'm' : '-' }}
                    </span>
                    <span class="param-badge ms-1" title="最大速度">
                      &#x26A1;{{ uav.maxSpeed ? uav.maxSpeed + 'km/h' : '-' }}
                    </span>
                    <span class="param-badge ms-1" title="重量">
                      &#x2696;{{ uav.weight ? uav.weight + 'kg' : '-' }}
                    </span>
                  </td>
                  <td>
                    <span :class="getStatusBadge(uav.status)">{{ getStatusText(uav.status) }}</span>
                  </td>
                  <td class="action-col" @click.stop>
                    <button @click="editUav(uav)" class="btn btn-sm btn-outline-info me-1" title="编辑">&#x270F;</button>
                    <button @click="deleteUavConfirm(uav.id)" class="btn btn-sm btn-outline-danger" title="删除">&#x1F5D1;</button>
                  </td>
                </tr>
                <!-- 展开的详情面板 -->
                <tr v-if="expandedRowId === uav.id" class="detail-row">
                  <td colspan="7" class="p-0">
                    <div class="detail-panel animate-slide-down">
                      <div class="detail-grid">
                        <!-- 基本信息 -->
                        <div class="detail-section">
                          <h6 class="detail-section-title">&#x1F4CB; 基本信息</h6>
                          <div class="detail-item">
                            <span class="detail-label">名称</span>
                            <span class="detail-value">{{ uav.name }}</span>
                          </div>
                          <div class="detail-item">
                            <span class="detail-label">型号</span>
                            <span class="detail-value">{{ uav.model }}</span>
                          </div>
                          <div class="detail-item">
                            <span class="detail-label">序列号</span>
                            <span class="detail-value"><code>{{ uav.serialNumber }}</code></span>
                          </div>
                          <div class="detail-item">
                            <span class="detail-label">制造商</span>
                            <span class="detail-value">{{ uav.manufacturer || '-' }}</span>
                          </div>
                          <div class="detail-item">
                            <span class="detail-label">生产日期</span>
                            <span class="detail-value">{{ uav.manufactureDate || '-' }}</span>
                          </div>
                        </div>
                        <!-- 性能参数 -->
                        <div class="detail-section">
                          <h6 class="detail-section-title">&#x2699; 性能参数</h6>
                          <div class="detail-item">
                            <span class="detail-label">最大飞行高度</span>
                            <span class="detail-value">{{ uav.maxAltitude ? uav.maxAltitude + ' 米' : '-' }}</span>
                          </div>
                          <div class="detail-item">
                            <span class="detail-label">最大飞行速度</span>
                            <span class="detail-value">{{ uav.maxSpeed ? uav.maxSpeed + ' km/h' : '-' }}</span>
                          </div>
                          <div class="detail-item">
                            <span class="detail-label">重量</span>
                            <span class="detail-value">{{ uav.weight ? uav.weight + ' kg' : '-' }}</span>
                          </div>
                          <div class="detail-item">
                            <span class="detail-label">当前状态</span>
                            <span class="detail-value">
                              <span :class="getStatusBadge(uav.status)">{{ getStatusText(uav.status) }}</span>
                            </span>
                          </div>
                        </div>
                        <!-- 描述与时间 -->
                        <div class="detail-section detail-section-wide">
                          <h6 class="detail-section-title">&#x1F4DD; 描述信息</h6>
                          <div class="detail-description">
                            {{ uav.description || '暂无描述信息' }}
                          </div>
                        </div>
                        <div class="detail-section">
                          <h6 class="detail-section-title">&#x1F4C5; 时间记录</h6>
                          <div class="detail-item">
                            <span class="detail-label">创建时间</span>
                            <span class="detail-value">{{ formatTime(uav.createdAt) }}</span>
                          </div>
                          <div class="detail-item">
                            <span class="detail-label">最后更新</span>
                            <span class="detail-value">{{ formatTime(uav.updatedAt) }}</span>
                          </div>
                        </div>
                      </div>
                      <!-- 详情面板底部操作 -->
                      <div class="detail-actions">
                        <button @click.stop="editUav(uav)" class="btn btn-sm btn-outline-info">&#x270F; 编辑</button>
                        <button @click.stop="deleteUavConfirm(uav.id)" class="btn btn-sm btn-outline-danger ms-2">&#x1F5D1; 删除</button>
                      </div>
                    </div>
                  </td>
                </tr>
              </template>
              <!-- 空数据占位 -->
              <tr v-if="uavList.length === 0 && !isLoading">
                <td colspan="7" class="text-center text-muted py-5">
                  <span style="font-size:2rem;">&#x1F4CB;</span><br>
                  <span class="mt-2 d-block">暂无数据</span>
                  <small class="d-block mt-1">点击"添加无人机"创建第一条记录</small>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- ========== 分页控制 ========== -->
      <div class="pagination-wrapper mt-3">
        <div class="page-info">
          第 <strong>{{ currentPage }}</strong> 页 / 共 <strong>{{ totalPages }}</strong> 页
          &nbsp;({{ totalElements }} 条记录)
        </div>
        <div class="page-buttons">
          <button @click="prevPage" class="btn btn-outline-primary btn-sm" :disabled="currentPage <= 1">
            &#x25C0; 上一页
          </button>
          <button @click="nextPage" class="btn btn-outline-primary btn-sm ms-2" :disabled="currentPage >= totalPages">
            下一页 &#x25B6;
          </button>
        </div>
      </div>
    </div>

    <!-- ========== 添加/编辑模态框 ========== -->
    <div v-if="showAddModal" class="modal-mask" @click.self="closeModal">
      <div class="modal-dialog modal-dialog-scrollable animated-modal">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ isEditing ? '&#x270F; 编辑无人机' : '&#x2795; 添加无人机' }}</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveUav" id="uavForm">
              <div class="mb-3">
                <label>名称 <span class="text-danger">*</span></label>
                <input type="text" class="form-control" v-model="formData.name" required placeholder="请输入无人机名称">
              </div>
              <div class="mb-3">
                <label>型号 <span class="text-danger">*</span></label>
                <input type="text" class="form-control" v-model="formData.model" required placeholder="请输入型号">
              </div>
              <div class="mb-3">
                <label>序列号 <span class="text-danger">*</span></label>
                <input type="text" class="form-control" v-model="formData.serialNumber" required placeholder="请输入序列号">
              </div>
              <div class="mb-3">
                <label>制造商</label>
                <input type="text" class="form-control" v-model="formData.manufacturer" placeholder="请输入制造商">
              </div>
              <div class="mb-3">
                <label>生产日期</label>
                <input type="date" class="form-control" v-model="formData.manufactureDate">
              </div>
              <div class="row">
                <div class="form-group col-md-4 mb-3">
                  <label>最大高度(米)</label>
                  <input type="number" class="form-control" v-model="formData.maxAltitude" placeholder="米">
                </div>
                <div class="form-group col-md-4 mb-3">
                  <label>最大速度(km/h)</label>
                  <input type="number" class="form-control" v-model="formData.maxSpeed" placeholder="km/h">
                </div>
                <div class="form-group col-md-4 mb-3">
                  <label>重量(kg)</label>
                  <input type="number" step="0.01" class="form-control" v-model="formData.weight" placeholder="kg">
                </div>
              </div>
              <div class="mb-3">
                <label>描述</label>
                <textarea class="form-control" v-model="formData.description" rows="3" placeholder="请输入描述信息"></textarea>
              </div>
              <div class="mb-3">
                <label>状态</label>
                <select class="form-control" v-model="formData.status">
                  <option value="ACTIVE">运行中</option>
                  <option value="INACTIVE">停用</option>
                  <option value="MAINTENANCE">维护中</option>
                </select>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button @click="closeModal" class="btn btn-outline-secondary">取消</button>
            <button @click="saveUav" class="btn btn-primary">{{ isEditing ? '保存修改' : '确认添加' }}</button>
          </div>
        </div>
      </div>
    </div>

    <!-- ========== 删除确认模态框 ========== -->
    <div v-if="showDeleteModal" class="modal-mask" @click.self="cancelDelete">
      <div class="modal-dialog modal-dialog-centered animated-modal">
        <div class="modal-content">
          <div class="modal-header bg-danger text-white">
            <h5 class="modal-title">&#x26A0; 确认删除</h5>
            <button type="button" class="btn-close btn-close-white" @click="cancelDelete"></button>
          </div>
          <div class="modal-body text-center py-4">
            <p class="mb-0">确定要删除该无人机吗？</p>
            <small class="text-muted">此操作无法撤销。</small>
          </div>
          <div class="modal-footer">
            <button @click="cancelDelete" class="btn btn-outline-secondary">取消</button>
            <button @click="confirmDelete" class="btn btn-danger">确认删除</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 无人机管理页面组件（Vue 3 Composition API + script setup）
 *
 * 系统核心功能页面，提供无人机信息的完整管理：
 * - 多条件组合查询（名称/型号/序列号/状态模糊搜索，带 300ms 防抖）
 * - 全字段信息列表展示（含性能参数徽章、可展开详情面板）
 * - 新增、编辑、删除无人机记录
 * - AI 模拟生成无人机属性（测试辅助功能）
 * - 自动实时刷新（30 秒轮询，可开关）+ 手动刷新
 * - 加载骨架屏、上次更新时间指示
 *
 * @module views/UavManage
 * @see module:api/uav
 */
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { getUavList, createUav, updateUav, deleteUav } from '../api/uav'
import { logout } from '../api/auth'

// ====================================================================
// 预设数据池 — AI 随机生成功能使用
// ====================================================================
const manufacturers = ['大疆创新', '华为', '小米', '极飞科技', '昊翔', '零度智控', '亿航智能', 'Parrot', 'Autel', 'Skydio']
const models = ['Mavic 3', 'Phantom 4', 'Inspire 2', 'Mini 3 Pro', 'Air 2S', 'FPV Combo', 'Matrice 300', 'T60', 'H520', 'Firefly']

// ====================================================================
// 响应式状态定义
// ====================================================================

/** 无人机列表数据（当前页） */
const uavList = ref([])
/** 当前页码（从 1 开始） */
const currentPage = ref(1)
/** 总页数 */
const totalPages = ref(1)
/** 总记录数 */
const totalElements = ref(0)
/** 是否显示添加/编辑模态框 */
const showAddModal = ref(false)
/** 是否显示删除确认模态框 */
const showDeleteModal = ref(false)
/** 是否为编辑模式 */
const isEditing = ref(false)
/** 待删除的无人机 ID */
const deleteId = ref(null)
/** 当前登录用户名 */
const currentUser = ref('')

/** 当前展开详情的无人机 ID（null 表示全部折叠） */
const expandedRowId = ref(null)

/** 是否正在加载数据 */
const isLoading = ref(false)
/** 上次成功刷新的时间戳（格式化后的字符串） */
const lastUpdated = ref('')
/** 自动刷新开关 */
const autoRefreshEnabled = ref(true)
/** 自动刷新定时器句柄 */
let refreshTimer = null
/** 默认刷新间隔（毫秒） */
const REFRESH_INTERVAL = 30000

/**
 * 搜索表单数据
 * @property {string} name - 名称模糊搜索
 * @property {string} model - 型号模糊搜索
 * @property {string} serialNumber - 序列号模糊搜索
 * @property {string} status - 状态精确筛选
 */
const searchForm = reactive({
  name: '',
  model: '',
  serialNumber: '',
  status: ''
})

/** 搜索防抖定时器 */
let searchDebounceTimer = null

/**
 * 新增/编辑表单数据
 * @property {number|null} id - 无人机 ID
 * @property {string} name - 名称（必填）
 * @property {string} model - 型号（必填）
 * @property {string} serialNumber - 序列号（必填，业务唯一键）
 * @property {string} manufacturer - 制造商
 * @property {string} manufactureDate - 生产日期
 * @property {number|null} maxAltitude - 最大飞行高度（米）
 * @property {number|null} maxSpeed - 最大飞行速度（km/h）
 * @property {number|null} weight - 重量（kg）
 * @property {string} description - 描述
 * @property {string} status - 状态枚举值
 */
const formData = reactive({
  id: null,
  name: '',
  model: '',
  serialNumber: '',
  manufacturer: '',
  manufactureDate: '',
  maxAltitude: null,
  maxSpeed: null,
  weight: null,
  description: '',
  status: 'ACTIVE'
})

// ====================================================================
// 生命周期钩子
// ====================================================================

/**
 * 组件挂载时：
 * 1. 读取登录用户信息
 * 2. 加载第一页数据
 * 3. 启动自动刷新定时器
 */
onMounted(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  currentUser.value = user.username || '管理员'
  loadUavList()
  startAutoRefresh()
})

/**
 * 组件卸载时：清理自动刷新定时器和搜索防抖定时器
 */
onUnmounted(() => {
  stopAutoRefresh()
  if (searchDebounceTimer) clearTimeout(searchDebounceTimer)
})

// ====================================================================
// 自动刷新控制
// ====================================================================

/** 启动自动刷新定时器（每 30 秒轮询一次） */
function startAutoRefresh() {
  stopAutoRefresh()
  if (autoRefreshEnabled.value) {
    refreshTimer = setInterval(() => {
      loadUavList(true)
    }, REFRESH_INTERVAL)
  }
}

/** 停止自动刷新定时器 */
function stopAutoRefresh() {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
}

/** 自动刷新开关切换时重新启停定时器 */
function onAutoRefreshToggle() {
  if (autoRefreshEnabled.value) {
    startAutoRefresh()
  } else {
    stopAutoRefresh()
  }
}

/** 手动刷新按钮触发（强制显示加载状态） */
async function manualRefresh() {
  await loadUavList()
}

// ====================================================================
// 辅助函数
// ====================================================================

/**
 * 将状态枚举值映射为中文显示文本
 * @param {string} status - 状态枚举值
 * @returns {string} 中文状态描述
 */
function getStatusText(status) {
  const map = { ACTIVE: '运行中', INACTIVE: '停用', MAINTENANCE: '维护中' }
  return map[status] || status
}

/**
 * 根据状态值返回 Bootstrap 徽章 CSS 类名
 * @param {string} status - 状态枚举值
 * @returns {string} Bootstrap 徽章 class
 */
function getStatusBadge(status) {
  const map = {
    ACTIVE: 'badge rounded-pill bg-success status-badge',
    INACTIVE: 'badge rounded-pill bg-secondary status-badge',
    MAINTENANCE: 'badge rounded-pill bg-warning status-badge'
  }
  return map[status] || 'badge rounded-pill bg-secondary status-badge'
}

/**
 * 格式化时间戳为可读字符串
 * @param {string} timeStr - ISO 时间字符串或 null
 * @returns {string} 格式化后的时间（yyyy-MM-dd HH:mm:ss）或 "-"
 */
function formatTime(timeStr) {
  if (!timeStr) return '-'
  try {
    const d = new Date(timeStr)
    const pad = (n) => String(n).padStart(2, '0')
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
  } catch {
    return timeStr
  }
}

/**
 * 获取当前时间的格式化字符串（用于"更新于"显示）
 * @returns {string} 例如 "14:32:05"
 */
function getCurrentTimeStr() {
  const now = new Date()
  const pad = (n) => String(n).padStart(2, '0')
  return `${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`
}

/**
 * 重置表单数据到初始状态
 */
function resetFormData() {
  formData.id = null
  formData.name = ''
  formData.model = ''
  formData.serialNumber = ''
  formData.manufacturer = ''
  formData.manufactureDate = ''
  formData.maxAltitude = null
  formData.maxSpeed = null
  formData.weight = null
  formData.description = ''
  formData.status = 'ACTIVE'
}

/**
 * 切换行的展开/折叠
 * @param {number} id - 无人机 ID
 */
function toggleRowExpand(id) {
  expandedRowId.value = expandedRowId.value === id ? null : id
}

// ====================================================================
// 数据加载与查询
// ====================================================================

/**
 * 从后端加载无人机列表
 * 搜索条件中仅传递非空字段以减少请求体积。
 *
 * @param {boolean} [isSilent=false] - 静默刷新（不显示加载状态，用于自动轮询）
 */
async function loadUavList(isSilent = false) {
  if (!isSilent) {
    isLoading.value = true
  }
  try {
    const params = {}
    if (searchForm.name) params.name = searchForm.name
    if (searchForm.model) params.model = searchForm.model
    if (searchForm.serialNumber) params.serialNumber = searchForm.serialNumber
    if (searchForm.status) params.status = searchForm.status

    const response = await getUavList(currentPage.value, 10, params)
    if (response.code === 200) {
      uavList.value = response.data.content
      totalElements.value = response.data.totalElements
      totalPages.value = Math.ceil(response.data.totalElements / 10)
      // 更新"上次刷新"时间戳
      lastUpdated.value = getCurrentTimeStr()
    }
  } catch (e) {
    console.error('加载无人机列表失败:', e)
    if (!isSilent) alert('加载无人机列表失败')
  } finally {
    if (!isSilent) {
      isLoading.value = false
    }
  }
}

/**
 * 搜索输入防抖处理
 * 用户在搜索框中输入时，延迟 300ms 后自动发起查询，
 * 避免频繁请求后端 API。
 */
function onSearchInput() {
  if (searchDebounceTimer) clearTimeout(searchDebounceTimer)
  searchDebounceTimer = setTimeout(() => {
    searchUav()
  }, 300)
}

/**
 * 执行查询（将页码重置为第 1 页后重新加载）
 */
async function searchUav() {
  currentPage.value = 1
  await loadUavList()
}

/**
 * 重置搜索条件并重新查询
 */
function resetSearch() {
  searchForm.name = ''
  searchForm.model = ''
  searchForm.serialNumber = ''
  searchForm.status = ''
  searchUav()
}

// ====================================================================
// 模态框操作（新增 / 编辑）
// ====================================================================

/** 打开新增模态框 */
function openAddModal() {
  isEditing.value = false
  resetFormData()
  showAddModal.value = true
}

/** 关闭模态框 */
function closeModal() {
  showAddModal.value = false
  isEditing.value = false
  resetFormData()
}

/**
 * 打开编辑模态框
 * @param {Object} uav - 要编辑的无人机对象
 */
function editUav(uav) {
  isEditing.value = true
  formData.id = uav.id
  formData.name = uav.name
  formData.model = uav.model
  formData.serialNumber = uav.serialNumber
  formData.manufacturer = uav.manufacturer || ''
  formData.manufactureDate = uav.manufactureDate || ''
  formData.maxAltitude = uav.maxAltitude || null
  formData.maxSpeed = uav.maxSpeed || null
  formData.weight = uav.weight || null
  formData.description = uav.description || ''
  formData.status = uav.status || 'ACTIVE'
  showAddModal.value = true
}

/**
 * 保存无人机数据（新增或更新）
 * 前端验证必填字段后调用后端 API。
 */
async function saveUav() {
  try {
    if (!formData.name.trim()) { alert('请输入名称'); return }
    if (!formData.model.trim()) { alert('请输入型号'); return }
    if (!formData.serialNumber.trim()) { alert('请输入序列号'); return }

    const payload = { ...formData }

    if (isEditing.value) {
      await updateUav(formData.id, payload)
      alert('更新成功!')
    } else {
      await createUav(payload)
      alert('添加成功!')
    }
    closeModal()
    await loadUavList()
  } catch (e) {
    const msg = e?.response?.data?.message || e?.message || '保存失败'
    alert('保存失败: ' + msg)
  }
}

// ====================================================================
// 删除操作
// ====================================================================

/**
 * 打开删除确认弹窗
 * @param {number} id - 要删除的无人机 ID
 */
function deleteUavConfirm(id) {
  deleteId.value = id
  showDeleteModal.value = true
}

/** 取消删除 */
function cancelDelete() {
  showDeleteModal.value = false
  deleteId.value = null
}

/** 确认删除 */
async function confirmDelete() {
  try {
    await deleteUav(deleteId.value)
    showDeleteModal.value = false
    deleteId.value = null
    await loadUavList()
    alert('删除成功')
  } catch (e) {
    console.error('删除失败:', e)
    alert('删除失败')
  }
}

// ====================================================================
// AI 模拟生成
// ====================================================================

/**
 * AI 一键生成无人机属性（测试辅助功能）
 * 从前端预设数据池中随机取值填充表单，模拟 AI 辅助录入场景。
 */
function generateByAI() {
  const randomManufacturer = manufacturers[Math.floor(Math.random() * manufacturers.length)]
  const randomModel = models[Math.floor(Math.random() * models.length)]
  const randomSerial = 'SN' + Date.now().toString().slice(-8) + Math.random().toString(36).slice(-4).toUpperCase()
  const randomAltitude = Math.floor(Math.random() * 500) + 100
  const randomSpeed = Math.floor(Math.random() * 120) + 60
  const randomWeight = (Math.random() * 3 + 0.5).toFixed(2)

  // 生成随机日期（最近两年内）
  const now = new Date()
  const pastDate = new Date(now.getTime() - Math.random() * 2 * 365 * 24 * 60 * 60 * 1000)
  const pad = (n) => String(n).padStart(2, '0')
  const dateStr = `${pastDate.getFullYear()}-${pad(pastDate.getMonth() + 1)}-${pad(pastDate.getDate())}`

  formData.name = randomModel + ' 无人机'
  formData.model = randomModel
  formData.serialNumber = randomSerial
  formData.manufacturer = randomManufacturer
  formData.manufactureDate = dateStr
  formData.maxAltitude = randomAltitude
  formData.maxSpeed = randomSpeed
  formData.weight = randomWeight
  formData.description = `这是一款由${randomManufacturer}生产的${randomModel}型号无人机，具备高性能飞行能力。最大飞行高度${randomAltitude}米，最大飞行速度${randomSpeed}km/h，整机重量${randomWeight}kg。`
  formData.status = 'ACTIVE'

  isEditing.value = false
  showAddModal.value = true
  alert('AI 已自动生成无人机属性，请检查并保存！')
}

// ====================================================================
// 分页控制
// ====================================================================

/** 上一页 */
function prevPage() {
  if (currentPage.value > 1) {
    currentPage.value--
    expandedRowId.value = null  // 翻页时折叠所有详情
    loadUavList()
  }
}

/** 下一页 */
function nextPage() {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    expandedRowId.value = null
    loadUavList()
  }
}

// ====================================================================
// 登出操作
// ====================================================================

/**
 * 用户退出登录
 */
async function handleLogout() {
  try {
    await logout()
  } finally {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    window.location.href = '/login'
  }
}
</script>

<style scoped>
/* ====================================================================
   导航栏
   ==================================================================== */
.top-navbar {
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
}
.brand-icon { font-size: 1.4rem; margin-right: 6px; }
.user-info {
  color: rgba(255,255,255,0.9) !important;
  font-size: 0.95rem;
}
.user-icon { margin-right: 4px; }

/* ====================================================================
   查询卡片
   ==================================================================== */
.search-card {
  border: none;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
}
.search-card .card-title {
  color: #2a5298;
  font-size: 1rem;
  font-weight: 600;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid #e8edf5;
}
.section-icon { margin-right: 4px; }

/* ====================================================================
   工具栏
   ==================================================================== */
.toolbar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 0.5rem;
}
.last-updated {
  white-space: nowrap;
}
.refresh-icon {
  display: inline-block;
}

/* ====================================================================
   表格
   ==================================================================== */
.table-card {
  border: none;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  overflow: hidden;
}
.uav-table {
  font-size: 0.92rem;
}
.uav-table thead th {
  background: #f0f4fa;
  color: #1e3c72;
  font-weight: 600;
  border-top: none;
  padding: 12px 10px;
}
.uav-table tbody td {
  padding: 10px;
  vertical-align: middle;
}
.action-col {
  text-align: center;
}
.uav-id-badge {
  display: inline-block;
  background: #e8edf5;
  color: #2a5298;
  font-weight: 700;
  font-size: 0.85rem;
  padding: 2px 10px;
  border-radius: 10px;
}
.uav-name {
  font-weight: 500;
}
.uav-sub-info {
  font-size: 0.78rem;
  color: #888;
  margin-top: 2px;
}
.uav-serial {
  font-size: 0.78rem;
  color: #666;
}
.uav-row {
  cursor: pointer;
  transition: background-color 0.15s ease;
}
.uav-row:hover {
  background-color: #f0f6ff !important;
}
.uav-row.row-expanded {
  background-color: #eef3fc !important;
  border-bottom: none;
}

/* 性能参数徽章 */
.param-badge {
  display: inline-block;
  background: #eef3fc;
  color: #2a5298;
  font-size: 0.78rem;
  padding: 1px 6px;
  border-radius: 4px;
  white-space: nowrap;
}

/* ====================================================================
   展开详情面板
   ==================================================================== */
.detail-panel {
  background: #f7faff;
  border-top: 2px solid #2a5298;
  border-bottom: 1px solid #e0e8f2;
  padding: 20px 24px;
  animation: slideDown 0.25s ease-out;
}
@keyframes slideDown {
  from { opacity: 0; max-height: 0; padding-top: 0; padding-bottom: 0; }
  to   { opacity: 1; max-height: 600px; padding-top: 20px; padding-bottom: 20px; }
}
.detail-row td {
  border-top: none !important;
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}
.detail-section {
  background: #fff;
  border: 1px solid #e8edf5;
  border-radius: 8px;
  padding: 14px 16px;
}
.detail-section-wide {
  grid-column: 1 / -1;
}
.detail-section-title {
  font-size: 0.85rem;
  font-weight: 600;
  color: #2a5298;
  margin-bottom: 10px;
  padding-bottom: 6px;
  border-bottom: 1px solid #e8edf5;
}
.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 0;
  font-size: 0.88rem;
}
.detail-label {
  color: #888;
  flex-shrink: 0;
  margin-right: 12px;
}
.detail-value {
  color: #333;
  font-weight: 500;
  text-align: right;
  word-break: break-all;
}
.detail-description {
  font-size: 0.88rem;
  color: #555;
  line-height: 1.6;
  padding: 4px 0;
}
.detail-actions {
  margin-top: 12px;
  padding-top: 10px;
  border-top: 1px dashed #dce3ed;
  text-align: right;
}

/* ====================================================================
   骨架屏
   ==================================================================== */
.skeleton-row {
  display: flex;
  align-items: center;
}
.skeleton-line {
  height: 16px;
  border-radius: 6px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}
.skeleton-line-sm { width: 40px; }
.skeleton-line-md { width: 120px; }
.skeleton-line-lg { width: 200px; }
@keyframes shimmer {
  0%   { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

/* ====================================================================
   分页
   ==================================================================== */
.pagination-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}
.page-info {
  color: #666;
  font-size: 0.9rem;
}
.page-buttons {
  display: flex;
  gap: 4px;
}

/* ====================================================================
   模态框覆盖层
   ==================================================================== */
.modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.45);
  z-index: 1050;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: fadeIn 0.15s ease;
}
@keyframes fadeIn {
  from { opacity: 0; }
  to   { opacity: 1; }
}
.modal-dialog {
  width: 90%;
  max-width: 560px;
  max-height: 80vh;
}
.modal-dialog-scrollable {
  overflow-y: auto;
}
.modal-dialog-centered {
  display: flex;
  align-items: center;
}
.modal-content {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.25);
  border: none;
}
.modal-header {
  padding: 16px 20px;
  border-bottom: 1px solid #e8edf5;
}
.modal-body {
  padding: 20px;
}
.modal-footer {
  padding: 12px 20px;
  border-top: 1px solid #e8edf5;
}
.animated-modal {
  animation: modalSlideIn 0.2s ease;
}
@keyframes modalSlideIn {
  from { transform: translateY(-20px); opacity: 0; }
  to   { transform: translateY(0); opacity: 1; }
}
</style>
