import request from '@/utils/request'

const API = {
  // 简历相关接口
  RESUME_LIST_URL: '/admin/selectResumeList',
  RESUME_INFO_URL: '/admin/selectResumeInfo',
  RESUME_REVIEW_URL: '/admin/resumeReview',
  RESUME_DELETE_URL: '/resume/delete',
  ATTACHMENT_REVIEW_URL: '/resume/review',
}

// 简历相关
export const resumeList = (resumeListDto) => request.get(API.RESUME_LIST_URL, {params: resumeListDto})
export const resumeInfo = (resumeInfoDto) => request.post(API.RESUME_INFO_URL, resumeInfoDto)
export const resumeReview = (reviewCode, resumeId, resumeType) => request.get(`${API.RESUME_REVIEW_URL}/${reviewCode}/${resumeId}/${resumeType}`)
export const resumeDelete = (resumeId) => request.delete(API.RESUME_DELETE_URL, {params: resumeId})
export const attachmentResumeReview = (resumeId, reviewStatus) =>
  request.put(API.ATTACHMENT_REVIEW_URL, null, { params: {id: resumeId, reviewCode: reviewStatus} })

