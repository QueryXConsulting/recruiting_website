import { defineStore } from 'pinia'

const useResumeListStore = defineStore('resumeList', {
  state: () => {
    return {
      // 持久化网络请求参数
      resumeListQueryParams: {
        page: 1,
        size: 10,
        userName: '',
        resumeType: 0
        // ,
        // resumeReview: 1,
        // resumeStatus: 0
      },
      // 筛选参数
      resumeListScreenParams: {
        resumeTypeOption: '0',
        resumeStatusOption: '',
      }
    }
  },
  actions: {
    // setter方法，用于设置网络请求参数
    setResumeListQueryParams(params) {
      this.resumeListQueryParams = params
    },
    setResumeListScreenParams(params) {
      this.resumeListScreenParams = params
    }
  },
  // getters 用于定义计算属性。使用方法与调用属性一样，并不是调用方法。
  getters: {
    // getter方法，用于获取网络请求参数
    getResumeListQueryParams() {
      return this.resumeListQueryParams
    },
    getResumeListScreenParams() {
      return this.resumeListScreenParams
    }
  },
  persist: {
    key: 'resumeList',
    storage: sessionStorage,
    paths: ['resumeListQueryParams', 'resumeListScreenParams'],
  },
})

export default useResumeListStore;