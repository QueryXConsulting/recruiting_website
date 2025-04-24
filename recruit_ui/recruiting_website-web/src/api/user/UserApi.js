import request from '@/utils/request'

/* 以下接口/api前缀是为了测试，实际使用时应该去掉 */
const API = {
  // 用户相关接口
  USER_REGISTER_URL: '/api/user/register',
  UPLOAD_AVATAR_URL: '/api/user/uploadAvatar',
  // 简历相关接口
  RESUME_DELIVER_URL: '/api/deliver/resume',
  USER_INFO_URL: '/api/query/resume/online',
  ATTACHMENT_RESUME_URL: '/api/query/resume/attachments',
  UPLOAD_ATTACHMENT_URL: '/api/resume/upload',
  RESUME_UPDATE_URL: '/api/resume/update',
  RESUME_DELETE_URL: '/api/resume/delete',
  ALL_RESUME_URL: '/api/query/resume/all',
  // 职位相关接口
  JOB_LIST_URL: '/api/query/jobs',
  JOB_INFO_URL: '/api/query/job',
  // 面试相关接口
  INTERVIEW_LIST_URL: '/api/interview/list',
  INTERVIEW_INFO_URL: '/api/interview/info',
  INTERVIEW_DATE_URL: '/api/interview/date',
  INTERVIEW_UPDATE_URL: '/api/interview/isAccept',
  // 应聘相关接口
  DELIVER_LIST_URL: '/api/deliver/list',
  // offer相关接口
  OFFER_LIST_URL: '/api/offer/list',
  OFFER_STATUS_URL: '/api/offer/status',
  OFFER_FILE_PATH_URL: '/api/offer/detail',
  OFFER_SIGNATURE_URL: '/api/offer/signature',
  // 入职材料相关接口
  MATERIAL_STATUS_URL: '/api/materials/status',
  UPLOAD_MATERIAL_URL: '/api/materials/upload',
  UPLOAD_OTHER_MATERIAL_URL: '/api/materials/upload/other',
  // 信息录入相关接口
  REGISTRATION_STATUS_URL: '/api/registration/status/query',
  REGISTRATION_INFO_URL: '/api/registration/position',
  REGISTRATION_SUBMIT_URL: '/api/registration/submit',
  RESERVATION_INFO_URL: '/api/registration/reservation',
  RESERVATION_STATUS_UPDATE_URL: '/api/registration/status/update',
  // 公司相关接口
  COMPANY_LIST_URL: '/api/query/companyList',
  // 搜索接口
  SEARCH_URL: '/api/query/search',
  // 留言相关接口
  MESSAGE_LIST_URL: '/api/message/list',
  MESSAGE_SEND_URL: '/api/message/send',
  MESSAGE_INFO_URL: '/api/message/info',
  // 工作性质相关接口
  JOB_NATURE_URL: '/api/query/jobNatureList',
}

// 用户相关
export const userRegister = (dto) => request.post(API.USER_REGISTER_URL, dto)
export const uploadAvatar = (image) => request.post(API.UPLOAD_AVATAR_URL, image)
// 职位相关
export const jobList = (params) =>
  request.post(API.JOB_LIST_URL, params)
// export const jobList = (keyword, page, size, asc = false, education, nature) =>
//   request.get(API.JOB_LIST_URL, { params: { keyword: keyword, page: page, pageSize: size, isAsc: asc, education: education, nature: nature } })
export const jobInfo = (id) => request.get(API.JOB_INFO_URL, { params: { id: id } })
// 简历相关
export const resumeDeliver = (dto) => request.post(API.RESUME_DELIVER_URL, dto)
export const userInfo = () => request.get(API.USER_INFO_URL)
export const attachmentList = () => request.get(API.ATTACHMENT_RESUME_URL)
export const uploadAttachment = (file) => request.post(API.UPLOAD_ATTACHMENT_URL, file)
export const resumeUpdate = (dto) => request.put(API.RESUME_UPDATE_URL, dto)
export const resumeDelete = (id) => request.delete(API.RESUME_DELETE_URL, { params: { id: id } })
export const resumeAll = () => request.get(API.ALL_RESUME_URL)
// 面试相关
export const interviewList = (page, pageSize) => request.get(API.INTERVIEW_LIST_URL, { params: { page: page, pageSize: pageSize } })
export const interviewDate = (companyId) => request.get(API.INTERVIEW_DATE_URL, { params: { companyId: companyId } })
export const interviewAccept = (id, isAccept, data) => request.put(API.INTERVIEW_UPDATE_URL, data, { params: { interviewId: id, isAccept: isAccept } })
// 应聘相关
export const deliverList = (page, pageSize) => request.get(API.DELIVER_LIST_URL, { params: { page: page, pageSize: pageSize } })
// offer相关
export const offerList = (page, size) => request.get(API.OFFER_LIST_URL, { params: { page: page, size: size } })
export const offerStatus = (id, status) => request.put(API.OFFER_STATUS_URL, null, { params: { offerId: id, status: status } })
export const offerFilePath = (id) => request.get(API.OFFER_FILE_PATH_URL, { params: { offerId: id } })
export const offerSignature = (id, img) => request.put(API.OFFER_SIGNATURE_URL, img, { params: { offerId: id } })
// 入职材料相关
export const materialStatus = () => request.get(API.MATERIAL_STATUS_URL)
export const materialUpload = (file) => request.post(API.UPLOAD_MATERIAL_URL, file)
export const materialOtherUpload = (file) => request.post(API.UPLOAD_OTHER_MATERIAL_URL, file)
// 信息录入相关
export const registrationStatus = () => request.get(API.REGISTRATION_STATUS_URL)
export const registrationInfo = () => request.get(API.REGISTRATION_INFO_URL)
export const registrationSubmit = (dto) => request.post(API.REGISTRATION_SUBMIT_URL, dto)
export const reservationInfo = () => request.get(API.RESERVATION_INFO_URL)
export const reservationUpdateStatus = (status) => request.put(API.RESERVATION_STATUS_UPDATE_URL, status, {headers: {'Content-Type': 'text/plain'}})
// 公司相关
export const companyList = (params) => request.post(API.COMPANY_LIST_URL, params)
// export const companyList = (kw, page, size, asc = false) => request.get(API.COMPANY_LIST_URL, { params: { keyword: kw, page: page, pageSize: size, isAsc: asc } })
// 搜索接口
export const search = (dto) => request.post(API.SEARCH_URL, dto)
// 留言相关
export const getLastMessage = () => request.get(API.MESSAGE_LIST_URL)
export const getMessageData = (params) => request.get(API.MESSAGE_INFO_URL, { params: params })
export const postMessage = (dto) => request.post(API.MESSAGE_SEND_URL,dto)
// 工作性质相关
export const jobNature = () => request.get(API.JOB_NATURE_URL)






