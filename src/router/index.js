/**
 * 路由配置（Vue Router 4 + Vue 3）
 *
 * 使用 createRouter + createWebHistory（History 模式，无 # 哈希）。
 * 定义两个路由：
 *   - /login：登录页面（公开访问）
 *   - /：无人机管理主页（需认证）
 *
 * 通过全局前置守卫 beforeEach 实现路由级别的访问控制。
 */
import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import UavManage from '../views/UavManage.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/',
    name: 'UavManage',
    component: UavManage,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

/**
 * 全局路由前置守卫
 *
 * 检查目标路由是否需要认证（meta.requiresAuth），
 * 若需要且未登录（localStorage 中无 user 信息），则强制跳转到登录页。
 */
router.beforeEach((to, from, next) => {
  const isAuthenticated = localStorage.getItem('user')
  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
  } else {
    next()
  }
})

export default router
