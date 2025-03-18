import request from '@/utils/request'

const API = {
  // 用户相关接口
  USER_LIST_URL: '/admin/selectUserList',
  USER_INFO_URL: '/admin/selectUserInfo',
  USER_UPDATE_URL: '/admin/updateUserInfo',
  USER_ADD_URL: '/admin/addUser',
  USER_DELETE_URL: '/admin/deleteUser',
  USER_AVATAR_UPLOADED_URL: '/api/admin/uploadAvatar/user',
}

// 用户相关
export const userList = (LoginDto) => request.get(API.USER_LIST_URL, {params: LoginDto})
export const userInfo = (userId) => request.get(`${API.USER_INFO_URL}/${userId}`)
export const userUpdate = (userDto) => request.put(API.USER_UPDATE_URL, userDto)
export const userAdd = (userDto) => request.post(API.USER_ADD_URL, userDto)
export const userDelete = (userId) => request.delete(`${API.USER_DELETE_URL}/${userId}`)
export const userAvatarUpload = (formData) => request.post(API.USER_AVATAR_UPLOADED_URL, formData)