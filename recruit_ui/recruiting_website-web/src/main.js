import { createApp } from 'vue'
import App from './App.vue'
import '@/style/reset.scss'
import router from './router'
import 'element-plus/dist/index.css'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import pinia from './store/store'
import userStore from './store/user'
import * as pdfjsLib from 'pdfjs-dist'

const app = createApp(App)
app.use(ElementPlus, {
  locale: zhCn,
})

app.use(pinia)

pdfjsLib.GlobalWorkerOptions.workerSrc = '/pdf.worker.min.js'

// 刷新后,在此重新添加动态路由(后续可能进行权限等的处理)
;(async () => {
  await userStore().addRouters()
  app.use(router)
  app.mount('#app')
})()
