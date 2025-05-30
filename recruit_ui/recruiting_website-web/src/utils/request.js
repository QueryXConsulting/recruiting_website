import userStore from '@/store/user'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  timeout: 5000,
})
// 请求拦截器
request.interceptors.request.use((config) => {
  const whiteList = [
    '/home',
    '/admin/getRouters',
    '/admin/getInfo',
    '/user/login',
    '/admin/menuList',
    '/admin/selectRoleMenusTree',
    '/admin/role',
    '/api/company/',
    '/admin/enterpriseFiles/',
    '/user/logout',
    '/users/search',
    // '/query/jobs',
    // '/query/jobNatureList'
  ]

  const token = userStore().token
  if (token != null) {
    config.headers['token'] = `${token}`
  }
  const regex = /^\/[^\/]+\/[^\/]+/;
  // 获取当前请求的基础URL（移除动态参数）
  const baseUrl = config.url.match(regex)[0]

  // 用户端测试专用
  if(baseUrl.startsWith('/api')){
    return config;
  }

  if (
    whiteList.some((path) => {
      return config.url.includes(path)
    })
  ) {
    return config
  }

  // 权限校验
  const perms = userStore().userInfo.permissions
  const requiredPerm = userStore().permissions[baseUrl]

  if (!perms.includes(requiredPerm)) {
    ElMessage({
      type: 'error',
      message: '没有权限',
    })
    return Promise.reject(new Error('没有权限'))
  }

  return config
})

// 响应拦截器
request.interceptors.response.use(
  (response) => {

     // 2xx 范围内的状态码都会触发该函数。
  // if (response.data.code !== 200) {
  //   ElMessage.error(response.data.message);
  //   return;
  // }

    if (response.data.code == 450) {
      userStore().token = null
      ElMessage({
        type: 'error',
        message: '登录状态已过期或还未登录，请登录',
      })
    }
    return response.data
  },
  (error) => {
    switch (error.response.status) {
      case 404:
        ElMessage({
          type: 'error',
          message: '资源不存在',
        })
        break
      case 500:
        ElMessage({
          type: 'error',
          message: '服务器错误请稍后再试',
        })
    }
    return Promise.reject(new Error(error.message))
  },
)

export default request
