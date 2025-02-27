import request from '@/utils/request'

const API = {
  // 求职类型相关接口
  JOB_NATURE_UPDATE_URL: '/admin/updateJobNature',
  JOB_NATURE_UPDATE_STATUS_URL: '/admin/updateJobNatureStatus',  
  JOB_NATURE_LIST_URL: '/admin/selectJobNatureList',
  JOB_NATURE_ADD_URL: '/admin/addJobNature',
  JOB_NATURE_INFO_URL: '/admin/selectJobNatureInfo',
  JOB_NATURE_DELETE_URL: '/admin/delJobNature'
}

// 求职类型相关
export const jobNatureUpdate = (jobNatureDTO) => request.put(API.JOB_NATURE_UPDATE_URL, jobNatureDTO)
export const jobNatureUpdateStatus = (status, jobNatureId) => request.put(`${API.JOB_NATURE_UPDATE_STATUS_URL}/${status}/${jobNatureId}`)
export const jobNatureList = () => request.get(API.JOB_NATURE_LIST_URL)
export const jobNatureAdd = (jobNatureName) => request.get(API.JOB_NATURE_ADD_URL, {params: jobNatureName})
export const jobNatureInfo = (jobNatureId) => request.get(`${API.JOB_NATURE_INFO_URL}/${jobNatureId}`)
export const jobNatureDelete = (jobNatureId) => request.delete(`${API.JOB_NATURE_DELETE_URL}/${jobNatureId}`)

