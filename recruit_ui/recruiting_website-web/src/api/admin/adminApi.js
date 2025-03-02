import request from '@/utils/request'
import Company from '@/views/company/company.vue'

const API = {
  USER_LIST_URL: '/admin/selectUserList',
  USER_INFO_URL: '/admin/selectUserInfo',
  USER_RESUME_LIST_URL: '/admin/selectResumeList',
  USER_RESUME_INFO_URL: '/admin/selectResumeInfo',
  USER_RESUME_REVIEW_URL: '/admin/resumeReview',
  ADMIN_LIST_URL: '/admin/selectAdminList',
  ADMIN_INFO_URL: '/admin/selectAdminInfo',
  ADMIN_ADD_URL: '/admin/addAdmin',
  ADMIN_UPDATE_URL: '/admin/updateAdminInfo',
  ADMIN_DELETE_URL: '/admin/deleteAdmin',

  LOGIN_URL: '/user/login',
  ADMIN_INFO: '/admin/getInfo',
  COMPANY_LIST: '/admin/companyList',
  COMPANY_ENTERPRISEFILES: '/admin/enterpriseFiles',
  // COMPANY_DETAIL: '/admin/companyDetail',
  COMPANY_UPDATE: '/admin/updateCompanyInfo',
  ROUTER_URL: '/admin/getRouters',
  ROLE_LIST: '/admin/selectRoleList',
  ROLE_INFO: '/admin/role',
  ROLE_MENUS: '/admin/selectRoleMenusTree',
  ROLE_UPDATE: '/admin/updateRoleInfo',
  ROLE_ADD: '/admin/addRole',
  ROLE_DELETE: '/admin/delRole',
  ROLE_STATUS: '/admin/updateRoleStatus',
  MENU_LIST: '/admin/menuList',
  MENU_ADD: '/admin/addMenu',
  MENU_UPDATE: '/admin/updateMenu',
  MENU_DELETE: '/admin/delMenu',
  CATEGORY_LIST: '/admin/selectCategory',
  CATEGORY_ADD: '/admin/addCategory',
  CATEGORY_UPDATE: '/admin/updateCategory',
  CATEGORY_DELETE: '/admin/delCategory',
  CATEGORY_STATUS: '/admin/updateCategoryStatus',
  JOB_LIST: '/admin/selectJobList',
  JOB_ADD: '/admin/addJob',
  JOB_UPDATE: '/admin/updateJobInfo',
  JOB_DELETE: '/admin/deleteJob',
  JOBNATURE_LIST: '/admin/selectJobNatureList',
  JOBNATURE_STATUS: '/admin/updateJobNatureStatus',
  JOBNATURE_UPDATE: '/admin/updateJobNature',
  JOBNATURE_ADD: '/admin/addJobNature',
  JOBNATURE_DELETE: '/admin/delJobNature',
}

export const adminLogin = (adminLoginDto) => request.post(API.LOGIN_URL, adminLoginDto)
export const adminRouters = () => request.get(API.ROUTER_URL)

export const selectRoleList = () => request.get(API.ROLE_LIST)
export const roleInfo = (roleId) => request.get(`${API.ROLE_INFO}/${roleId}`)
export const roleMenus = () => request.get(API.ROLE_MENUS)
export const roleUpdate = (roleUpdateDto) => request.put(API.ROLE_UPDATE, roleUpdateDto)
export const roleAdd = (roleAddDto) => request.post(API.ROLE_ADD, roleAddDto)
export const roleStatus = (roleId, roleStatus) =>
  request.put(`${API.ROLE_STATUS}/${roleId}/${roleStatus}`)

export const roleDelete = (roleIds) => {
  if (roleIds.length === 1) {
    return request.delete(`${API.ROLE_DELETE}/${roleIds[0]}`)
  } else {
    const idsString = roleIds.join(',')
    return request.delete(`${API.ROLE_DELETE}/${idsString}`)
  }
}

// 用户相关
export const adminUserList = (adminLoginDto) =>
  request.get(API.USER_LIST_URL, { params: adminLoginDto })
export const adminUserInfo = (userId) => request.get(`${API.USER_INFO_URL}/${userId}`)
// 简历相关
export const adminUserResumeList = (adminResumeListDto) =>
  request.get(API.USER_RESUME_LIST_URL, { params: adminResumeListDto })
export const adminUserResumeInfo = (adminResumeInfoDto) =>
  request.post(API.USER_RESUME_INFO_URL, adminResumeInfoDto)
export const adminUserResumeReview = (reviewCode, resumeId, resumeType) =>
  request.get(`${API.USER_RESUME_REVIEW_URL}/${reviewCode}/${resumeId}/${resumeType}`)

export const menuList = (status, menuName) =>
  request.get(API.MENU_LIST, { params: { status, menuName } })
export const menuAdd = (menuAddDto) => request.post(API.MENU_ADD, menuAddDto)
export const menuUpdate = (menuUpdateDto) => request.put(API.MENU_UPDATE, menuUpdateDto)
export const menuDelete = (menuId) => request.delete(`${API.MENU_DELETE}/${menuId}`)

export const categoryList = (page, size, categoryName, status) =>
  request.get(API.CATEGORY_LIST, { params: { page, size, categoryName, status } })
export const categoryAdd = (categoryAddDto) => request.post(API.CATEGORY_ADD, categoryAddDto)
export const categoryUpdate = (categoryUpdateDto) =>
  request.put(API.CATEGORY_UPDATE, categoryUpdateDto)
export const categoryDelete = (categoryId) => request.delete(`${API.CATEGORY_DELETE}/${categoryId}`)
export const categoryStatus = (categoryId, categoryStatus) =>
  request.put(`${API.CATEGORY_STATUS}/${categoryId}/${categoryStatus}`)

export const jobList = (
  page,
  size,
  companyName,
  jobName,
  jobReview,
  jobStatus,
  jobCategory,
  jobNature,
) =>
  request.get(API.JOB_LIST, {
    params: { page, size, companyName, jobName, jobReview, jobStatus, jobCategory, jobNature },
  })
export const jobAdd = (jobAddDto) => request.post(API.JOB_ADD, jobAddDto)
export const jobUpdate = (jobUpdateDto) => request.put(API.JOB_UPDATE, jobUpdateDto)
export const jobDelete = (jobId) => request.delete(`${API.JOB_DELETE}/${jobId}`)

export const companyList = (page, size, companyStatus, companyReview, companyName) =>
  request.get(API.COMPANY_LIST, {
    params: {
      page,
      size,
      companyStatus,
      companyReview,
      companyName,
    },
  })
export const companyEnterpriseFiles = (companyInfoId) =>
  request({
    url: `${API.COMPANY_ENTERPRISEFILES}/${companyInfoId}`,
    method: 'get',
    responseType: 'json',
    headers: {
      Accept: 'application/json',
    },
  })
export function companyUpdate(data) {
  const formObject = {}
  for (let pair of data.entries()) {
    if (pair[0] !== 'logo') {
      formObject[pair[0]] = pair[1]
    }
  }

  const dtoJson = JSON.stringify(formObject)
  const formData = new FormData()
  formData.append('dtoJson', dtoJson)

  const logoFile = data.get('applyFiles')
  if (logoFile && logoFile.size > 0) {
    formData.append('applyFiles', logoFile)
  }

  return request({
    url: API.COMPANY_UPDATE,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}

export const jobNatureList = (status) => request.get(API.JOBNATURE_LIST, { params: { status } })
export const jobNatureStatus = (natureId, status) =>
  request.put(`${API.JOBNATURE_STATUS}/${status}/${natureId}`)
export const jobNatureUpdate = (jobNatureDto) => request.put(API.JOBNATURE_UPDATE, jobNatureDto)
export const jobNatureAdd = (jobNatureName, natureStatus) =>
  request.get(API.JOBNATURE_ADD, { params: { jobNatureName, natureStatus } })
export const jobNatureDelete = (natureId) => request.delete(`${API.JOBNATURE_DELETE}/${natureId}`)
export const adminInfoURL = () => request.get(API.ADMIN_INFO)

export const adminList = (params) => request.get(API.ADMIN_LIST_URL, { params: params })
export const adminInfo = (id) => request.get(`${API.ADMIN_INFO_URL}/${id}`)
export const adminAdd = (params) => request.post(API.ADMIN_ADD_URL, params)
export const adminUpdate = (params) => request.put(API.ADMIN_UPDATE_URL, params)
export const adminDelete = (id) => request.delete(`${API.ADMIN_DELETE_URL}/${id}`)

