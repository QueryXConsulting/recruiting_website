import request from '@/utils/request'

const API = {
  // 角色相关接口
  ROLE_LIST_URL: '/admin/selectRoleList',
}

export const roleList = () => request.get(API.ROLE_LIST_URL)
