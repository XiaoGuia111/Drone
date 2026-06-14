<template>
  <!--
    登录页面
    全屏居中的登录卡片，包含用户名/密码输入和提交按钮。
    验证成功后保存 Token 跳转到首页，失败时显示错误提示。
  -->
  <div class="login-container">
    <div class="login-card">
      <!-- 系统图标与标题区域 -->
      <div class="login-header">
        <div class="login-icon">&#x1F6F0;</div>
        <h3>无人机信息管理系统</h3>
        <p class="text-muted">请登录以继续</p>
      </div>

      <!-- 登录表单区域 -->
      <div class="login-body">
        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label for="username">用户名</label>
            <input
              type="text"
              class="form-control"
              id="username"
              v-model="username"
              placeholder="请输入用户名"
              required
            />
          </div>
          <div class="form-group">
            <label for="password">密码</label>
            <input
              type="password"
              class="form-control"
              id="password"
              v-model="password"
              placeholder="请输入密码"
              required
            />
          </div>
          <!-- 登录按钮：加载中时禁用并显示"登录中..." -->
          <button type="submit" class="btn btn-primary btn-block login-btn" :disabled="loading">
            {{ loading ? '登录中...' : '登 录' }}
          </button>
        </form>
        <!-- 错误提示区域（登录失败时显示） -->
        <div v-if="error" class="alert alert-danger mt-3">
          {{ error }}
        </div>
      </div>

      <!-- 页脚：提示默认测试账号 -->
      <div class="login-footer">
        <small class="text-muted">默认账号: admin / admin123</small>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 登录页面组件（Vue 3 Composition API + script setup）
 *
 * 提供用户名/密码表单进行用户登录。登录成功后：
 * 1. 将 JWT Token 和用户基本信息保存到浏览器 localStorage
 * 2. 页面跳转到首页（无人机管理页面）
 *
 * @see module:api/auth
 */
import { ref } from 'vue'
import { login } from '../api/auth'

// ----- 表单响应式数据 -----
const username = ref('')     // 用户名输入框 v-model
const password = ref('')     // 密码输入框 v-model
const loading = ref(false)   // 登录加载状态（控制按钮禁用和文案切换）
const error = ref('')        // 登录失败的错误提示文本

/**
 * 处理用户登录
 *
 * 异步调用后端认证 API：
 * - 成功（code === 200）：保存 Token 和用户信息到 localStorage，跳转首页
 * - 业务失败（code !== 200）：显示后端返回的具体错误消息
 * - 网络异常：显示通用错误提示
 */
async function handleLogin() {
  loading.value = true
  error.value = ''
  try {
    const response = await login(username.value, password.value)
    if (response.code === 200) {
      // 将登录凭证和用户信息持久化到 localStorage
      localStorage.setItem('token', response.data.token)
      localStorage.setItem('user', JSON.stringify(response.data.user))
      // 跳转到首页（vue-router 导航在根路径）
      window.location.href = '/'
    } else {
      // 显示后端业务错误信息（如"用户名或密码错误"）
      error.value = response.message
    }
  } catch (e) {
    // 网络故障或服务器异常时显示通用提示
    error.value = '登录失败，请重试。'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 50%, #667db6 100%);
}
.login-card {
  width: 100%;
  max-width: 400px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.2);
  overflow: hidden;
  animation: cardFadeIn 0.4s ease-out;
}
@keyframes cardFadeIn {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}
.login-header {
  text-align: center;
  padding: 30px 30px 10px;
}
.login-icon {
  font-size: 3rem;
  margin-bottom: 10px;
}
.login-header h3 {
  color: #1e3c72;
  font-weight: 700;
  margin-bottom: 5px;
}
.login-body {
  padding: 20px 30px;
}
.login-body label {
  font-weight: 500;
  color: #444;
  margin-bottom: 4px;
}
.login-btn {
  padding: 10px;
  font-size: 1rem;
  font-weight: 600;
  border-radius: 8px;
  margin-top: 10px;
  background: linear-gradient(135deg, #1e3c72, #2a5298);
  border: none;
  transition: transform 0.1s ease;
}
.login-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(42,82,152,0.4);
}
.login-btn:active {
  transform: translateY(0);
}
.login-btn:disabled {
  opacity: 0.7;
  transform: none;
}
.login-footer {
  text-align: center;
  padding: 15px 30px;
  background: #f8faff;
  border-top: 1px solid #e8edf5;
}
</style>
