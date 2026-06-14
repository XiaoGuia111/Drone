/**
 * Vite 构建配置
 *
 * Vue 3 前端项目的 Vite 编译与开发服务器配置。
 * 开发环境下通过 proxy 将 /api 请求代理到后端 Java 服务，
 * 解决前后端分离开发时的跨域问题。
 *
 * @see https://vitejs.dev/config/
 */
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  // Vue 3 SFC（单文件组件）编译插件
  plugins: [vue()],

  server: {
    // 开发服务器端口
    port: 5173,

    // 开发环境代理：将前端 /api 请求透明转发到后端 Spring Boot 服务
    // 生产环境部署时需通过 Nginx 反向代理或后端 CORS 配置处理跨域
    proxy: {
      '/api': {
        target: 'http://localhost:8088',    // 后端服务地址
        changeOrigin: true                   // 变更请求的 Host 头为目标地址
      }
    }
  },

  build: {
    // 构建产物输出目录（生产部署时将其部署到 Web 服务器）
    outDir: 'dist'
  }
})
