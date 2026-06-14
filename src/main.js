/**
 * 应用入口文件（Vue 3 + Vite）
 *
 * 初始化 Vue 应用实例，加载路由和全局 Bootstrap 样式。
 */
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import 'bootstrap/dist/css/bootstrap.min.css'

const app = createApp(App)
app.use(router)
app.mount('#app')
