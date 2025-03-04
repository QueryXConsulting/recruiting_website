import request from '@/utils/request'

const API = {
  // 用户相关接口
  USER_REGISTER_URL: '/user/register',
  UPLOAD_AVATAR_URL: '/user/uploadAvatar',
  // 简历相关接口
  RESUME_DELIVER_URL: '/deliver/resume',
  USER_INFO_URL: '/query/resume/online',
  ATTACHMENT_RESUME_URL: '/query/resume/attachments',
  UPLOAD_ATTACHMENT_URL: '/resume/upload',
  RESUME_UPDATE_URL: '/resume/update',
  RESUME_DELETE_URL: '/resume/delete',
  ALL_RESUME_URL: '/query/resume/all',
  // 职位相关接口
  JOB_LIST_URL: '/query/jobs',
  JOB_INFO_URL: '/query/job',
  // 面试相关接口
  INTERVIEW_LIST_URL: '/interview/list',
  INTERVIEW_INFO_URL: '/interview/info',
  INTERVIEW_DATE_URL: '/interview/date',
  INTERVIEW_UPDATE_URL: '/interview/isAccept',
  // 应聘相关接口
  DELIVER_LIST_URL: '/deliver/list',
  // offer相关接口
  OFFER_LIST_URL: '/offer/list',
  OFFER_STATUS_URL: '/offer/status',
  OFFER_FILE_PATH_URL: '/offer/detail',
  OFFER_SIGNATURE_URL: '/offer/signature'
}

// 用户相关
export const userRegister = (dto) => request.post(API.USER_REGISTER_URL, dto)
export const uploadAvatar = (image) => request.post(API.UPLOAD_AVATAR_URL, image)
// 职位相关
export const jobList = (keyword, page, pageSize) => 
    request.get(API.JOB_LIST_URL, {params: {keyword: keyword, page: page, pageSize: pageSize}})
export const jobInfo = (id) => request.get(API.JOB_INFO_URL, {params: {id: id}})
// 简历相关
export const resumeDeliver = (dto) => request.post(API.RESUME_DELIVER_URL, dto)
export const userInfo = () => request.get(API.USER_INFO_URL)
export const attachmentList = () => request.get(API.ATTACHMENT_RESUME_URL)
export const uploadAttachment = (file) => request.post(API.UPLOAD_ATTACHMENT_URL, file)
export const resumeUpdate = (dto) => request.put(API.RESUME_UPDATE_URL, dto)
export const resumeDelete = (id) => request.delete(API.RESUME_DELETE_URL, {params: {id: id}})
export const resumeAll = () => request.get(API.ALL_RESUME_URL)
// 面试相关
export const interviewList = (page, pageSize) => request.get(API.INTERVIEW_LIST_URL, {params: {page: page, pageSize: pageSize}})
export const interviewDate = (companyId) => request.get(API.INTERVIEW_DATE_URL, {params: {companyId: companyId}})
export const interviewAccept = (id, isAccept) => request.put(API.INTERVIEW_UPDATE_URL, null, {params: {interviewId: id, isAccept: isAccept}})
// 应聘相关
export const deliverList = (page, pageSize) => request.get(API.DELIVER_LIST_URL, { params: { page: page, pageSize: pageSize } })
// offer相关
export const offerList = (page, size) => request.get(API.OFFER_LIST_URL, { params: { page: page, size: size } })
export const offerStatus = (id, status) => request.put(API.OFFER_STATUS_URL, null, { params: { offerId: id, status: status } })
export const offerFilePath = (id) => request.get(API.OFFER_FILE_PATH_URL, { params: { offerId: id } })
export const offerSignature = (id, img) => request.put(API.OFFER_SIGNATURE_URL, null, { params: { offerId: id, image: img } })


