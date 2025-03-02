import { defineStore } from 'pinia'

const useUserListStore = defineStore('userList', {
  state: () => {
    return {
      // 持久化网络请求参数
      userListQueryParams: {
        page: 1,
        size: 10,
        userName: '',
        // userStatus: '0'
      }  
    }
  },
  actions: {
    // setter方法，用于设置网络请求参数
    setUserListQueryParams(params) {
      this.userListQueryParams = params
    },
    
  },
  // getters 用于定义计算属性。使用方法与调用属性一样，并不是调用方法。
  getters: {
    // getter方法，用于获取网络请求参数
    getUserListQueryParams() {
      return this.userListQueryParams
    }
  },
  persist: {
    key: 'userList',
    storage: sessionStorage,
    paths: ['userListQueryParams'],
  },
})

export default useUserListStore;