import { defineStore } from 'pinia'

const useJobNatureStore = defineStore('jobNature', {
  state: () => {
    return {
    }
  },
  actions: {
    // setter方法，用于设置网络请求参数
    
  },
  // getters 用于定义计算属性。使用方法与调用属性一样，并不是调用方法。
  getters: {
    // getter方法，用于获取网络请求参数
    
  },
  persist: {
    key: 'jobNature',
    storage: sessionStorage,
    paths: ['jobNatureQueryParams', 'jobNatureScreenParams'],
  },
})

export default useJobNatureStore;