import { adminInfoURL, adminRouters } from '@/api/admin/adminApi'
import router from '@/router'
import { ElMessage } from 'element-plus'
import { defineStore } from 'pinia'

const userStore = defineStore('User', {
  state: () => {
    return {
      token: null,
      menuTree: [],
      role: null,
      permissions: {},
      userInfo: null,
      registerInfo: [],
    }
  },
  actions: {
    // 拿到管理员数据
    async getUserInfo() {
      let result = await adminInfoURL()
      if (result.code == 200) {
        this.userInfo = result.content
        return
      }
    },

    // 发送请求获得菜单导航栏以及权限数据等
    async getMenus() {
      const result = await adminRouters()
      if (result.code === 200) {
        this.menuTree = result.content.menus
        this.addRouters()
      } else {
        ElMessage.error(result?.msg)
      }
    },

    addRouters(menus = this.menuTree) {
      if (!menus) return
      if (menus.length > 0) {
        const modules = import.meta.glob('../views/**.vue')
        menus.forEach((menu) => {
          const componentPath = '../views/' + menu.component + '.vue'
          const route = {
            path: '/' + menu.path,
            name: menu.menuName,
            component: modules[componentPath],
            // component: () => import('../views/' + menu.component + '.vue'),
          }
          router.addRoute('home', route)
          if (menu.children && menu.children.length > 0) {
            this.addRouters(menu.children)
          }
        })
      }
    },
  },
  getters: {},
  persist: {
    key: 'userStore',
    storage: localStorage,
    paths: ['token', 'menuTree', 'permissions', 'role', 'userInfo'],
  },
})

export default userStore
