import request from '@/utils/request'

const API = {
  COMPANY_INFO: '/api/company/selectCompanyInfo',
  COMPANY_INFO_UPDATE: '/api/company/updateCompanyInfo',
  COMPANY_JOB_LIST: '/api/company/jobList',
  COMPANY_JOB_UPDATE: '/api/company/updateJob',
  COMPANY_SELECT_CATEGORY: '/api/company/selectCategory',
  COMPANY_JOB_DELETE: '/api/company/deleteJob',
  COMPANY_JOB_ADD: '/api/company/publishJob',
  COMPANY_USER_LIST: '/api/company/userCompanyList',
  COMPANY_ROLE_LIST: '/api/company/companyRole',
  COMPANY_USER_UPDATE: '/api/company/updateUserCompany',
  COMPANY_USER_ADD: '/api/company/addUserCompany',
  COMPANY_USER_DELETE: '/api/company/delUserCompany',
  COMPANY_RESUME_LIST: '/api/company/resumeList',
  COMPANY_SELECT_RESUME: '/api/company/selectResume',
  USER_INFO: '/api/company/selectUserInfo',
  USER_LOGOUT: '/user/logout',
  COMPANY_REGISTER_INFO: '/api/company/registerCompany',
  COMPANY_UPDATE_RESUME_STATUS: '/api/company/updateResumeStatus',
  COMPANY_ADD_INTERVIEW_DATE: '/api/company/addInterviewDate',
  COMPANY_SELECT_INTERVIEW_DATE: '/api/company/selectInterviewDate',
  COMPANY_DELETE_INTERVIEW_DATE_BY_ID: '/api/company/deleteInterviewDate',
  COMPANY_SEND_INVITATION: '/api/company/sendInterview',
  COMPANY_SELECT_INTERVIEW_LIST: '/api/company/selectInterviewList',
  COMPANY_UPDATE_INTERVIEW_LIST: '/api/company/updateInterviewList',
  COMPANY_OFFER_LIST: '/api/company/offerList',
  COMPANY_SELECT_OFFER_TEMPLATE: '/api/company/selectOfferTemplate',
  COMPANY_SEND_OFFER: '/api/company//offer/save',
  COMPANY_UPDATE_OFFER_STATUS: '/api/company/updateOfferStatus',
}

export const companyInfo = (companyId) => request.get(`${API.COMPANY_INFO}/${companyId}`)

export const companyInfoUpdate = (data) => {
  const formObject = {}
  for (let pair of data.entries()) {
    if (pair[0] !== 'logo' && pair[0] !== 'enterpriseFile' && pair[0] !== 'applyFiles') {
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

  for (let pair of data.entries()) {
    if (pair[0] === 'enterpriseFile') {
      formData.append('pdfFiles', pair[1])
    }
  }

  return request({
    url: API.COMPANY_INFO_UPDATE,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}

export const companyJobList = (page, size, jobName, jobReview, jobCategory) =>
  request.get(`${API.COMPANY_JOB_LIST}`, {
    params: { page, size, jobName, jobReview, jobCategory },
  })

export const companyJobUpdate = (data) => request.put(API.COMPANY_JOB_UPDATE, data)

export const companySelectCategory = (categoryName) =>
  request.get(API.COMPANY_SELECT_CATEGORY, {
    params: { categoryName },
  })
export const companyJobDelete = (jobid) => request.delete(`${API.COMPANY_JOB_DELETE}/${jobid}`)
export const companyJobAdd = (data) => request.post(API.COMPANY_JOB_ADD, data)

export const companyUserList = (page, size, userName) =>
  request.get(API.COMPANY_USER_LIST, {
    params: { page, size, userName },
  })

export const companyRoleList = () => request.get(API.COMPANY_ROLE_LIST)

export const updateUserCompany = (formData) => {
  return request({
    url: API.COMPANY_USER_UPDATE,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}

export const addUserCompany = (formData) => {
  return request({
    url: API.COMPANY_USER_ADD,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}

export const deleteUserCompany = (userId) => request.delete(`${API.COMPANY_USER_DELETE}/${userId}`)

export const companyResumeList = (jobId, page, size, resumeType, resumeName, resumeStatus) =>
  request.get(`${API.COMPANY_RESUME_LIST}/${jobId}`, {
    params: { page, size, resumeType, resumeName, resumeStatus },
  })

export const selectResume = (data) => request.post(API.COMPANY_SELECT_RESUME, data)

export const getUserInfo = () => request.get(API.USER_INFO)

export const userLogout = () => request.post(API.USER_LOGOUT)

export const registerCompany = (data) => request.post(API.COMPANY_REGISTER_INFO, data)

export const updateResumeStatus = (resumeStatus, jobResumeId, resumeDelete) =>
  request.post(
    `${API.COMPANY_UPDATE_RESUME_STATUS}/${resumeStatus}/${jobResumeId}/${resumeDelete}`,
  )
export const addInterviewDate = (data) => request.post(API.COMPANY_ADD_INTERVIEW_DATE, data)
export const selectInterviewDate = () => request.get(API.COMPANY_SELECT_INTERVIEW_DATE)
export const deleteInterviewDateById = (interviewDateId) => request.delete(`${API.COMPANY_DELETE_INTERVIEW_DATE_BY_ID}/${interviewDateId}`)
export const sendInvitationData = (data) => request.post(API.COMPANY_SEND_INVITATION, data)

export const selectInterviewList = (page, size, jobId) =>
  request.get(API.COMPANY_SELECT_INTERVIEW_LIST, {
    params: { page, size, jobId },
  });

export const updateInterviewList = (data) => request.put(API.COMPANY_UPDATE_INTERVIEW_LIST, data)

export const selectOffersList = (page, size, jobId) =>
  request.get(API.COMPANY_OFFER_LIST, {
    params: { page, size, jobId },
  })

export const selectOfferTemplate = () => request.get(API.COMPANY_SELECT_OFFER_TEMPLATE)

export const sendOffer = (data) => request.post(API.COMPANY_SEND_OFFER, data)

export const updateOfferStatus = (offerId, status,jobId) =>
  request.put(`${API.COMPANY_UPDATE_OFFER_STATUS}/${offerId}/${status}/${jobId}`)
