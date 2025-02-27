import { defineStore } from 'pinia'

const useAdminStore = defineStore('admin', {
  state: () => {
    return {
      // 持久化网络请求参数
      queryParams: {
        page: 1,
        size: 10,
        // userName: '',
        // resumeType: 0
      },
      // 筛选参数
      screenParams: {
        // resumeTypeOption: '0',
        statusOption: '',
      }
    }
  },
  actions: {
    // setter方法，用于设置网络请求参数
    setQueryParams(params) {
      this.queryParams = params
    },
    setScreenParams(params) {
      this.screenParams = params
    }
  },
  // getters 用于定义计算属性。使用方法与调用属性一样，并不是调用方法。
  getters: {
    // getter方法，用于获取网络请求参数
    getQueryParams() {
      return this.queryParams
    },
    getScreenParams() {
      return this.screenParams
    }
  },
  persist: {
    key: 'admin',
    storage: sessionStorage,
    paths: ['queryParams', 'screenParams'],
  },
})

export default useAdminStore;