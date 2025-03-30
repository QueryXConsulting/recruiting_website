<template>
  <div class="company-info-container">
    <el-card class="info-card">
      <template #header>
        <div class="card-header">
          <h2>企业信息管理</h2>
        </div>
      </template>

      <div class="company-content">
        <div class="alert-section">
          <el-alert v-if="companyData.companyInfoReview === '0'" title="企业信息正在审核中" type="warning"
            description="您的企业信息正在审核中，审核通过后即可正常使用所有功能。" show-icon :closable="false" class="review-alert" />

          <el-alert v-if="companyData.enterpriseReview === '2'" title="企业资质审核未通过" type="error"
            description="您的企业资质审核未通过，请根据反馈进行修改后重新提交。" show-icon :closable="false" class="review-alert" />
        </div>

        <div class="profile-section">
          <div class="avatar-section">
            <div class="logo-upload-container">
              <el-upload class="logo-uploader" :show-file-list="false" :auto-upload="false"
                :on-change="handleLogoChange" accept="image/jpeg,image/png,image/jpg"
                :disabled="companyData.companyInfoReview === '0'">
                <img v-if="companyData.companyLogo" :src="companyData.companyLogo" class="logo-preview-img" />
                <div v-else class="upload-placeholder">
                  <el-icon>
                    <Plus />
                  </el-icon>
                  <span>点击上传LOGO</span>
                </div>
              </el-upload>

              <div class="upload-tip">
                <el-icon>
                  <InfoFilled />
                </el-icon>
                <span>支持 jpg/png 格式，大小不超过2MB。修改后需重新提交审核</span>
              </div>
            </div>

            <div class="stats-info">
              <div class="stat-item">
                <div class="stat-value">{{ companyData.companyInfoReview === '1' ? '已认证' : '未认证' }}</div>
                <div class="stat-label">企业信息认证</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ companyData.enterpriseReview === '1' ? '已认证' : '未认证' }}</div>
                <div class="stat-label">资质认证</div>
              </div>
            </div>
          </div>

          <div class="info-section">
            <div class="info-table">
              <div class="info-row">
                <div class="info-label">企业名称</div>
                <div class="info-value">{{ companyData.companyInfoName }}</div>
                <div class="info-label">注册时间</div>
                <div class="info-value">{{ companyData.companyRegisterTime }}</div>
              </div>
              <div class="info-row">
                <div class="info-label">邮箱账号</div>
                <div class="info-value">
                  <el-input v-model="companyData.companyInfoUsername" placeholder="请输入邮箱账号" @blur="validateEmail"
                    :disabled="companyData.companyInfoReview === '0'" />
                </div>
                <div class="info-label">密码</div>
                <div class="info-value">
                  <el-input v-model="companyData.companyInfoPassword" placeholder="修改密码" show-password
                    :disabled="companyData.companyInfoReview === '0'" />
                </div>
              </div>
              <div class="info-row">
                <div class="info-label">信息审核</div>
                <div class="info-value">
                  <el-tag :type="getReviewStatusType(companyData.companyInfoReview)">
                    {{ getReviewStatusText(companyData.companyInfoReview) }}
                  </el-tag>
                </div>
                <div class="info-label">资质审核</div>
                <div class="info-value">
                  <el-tag :type="getReviewStatusType(companyData.enterpriseReview)">
                    {{ getReviewStatusText(companyData.enterpriseReview) }}
                  </el-tag>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="detail-info-section">
          <el-form :model="companyData" label-width="120px">
            <el-form-item label="经营范围">
              <el-input v-model="companyData.companyInfoScope" type="textarea" :rows="3"
                :disabled="companyData.companyInfoReview === '0'" />
            </el-form-item>
            <el-form-item label="企业资质文件">
              <el-upload v-model:file-list="fileList" class="qualification-upload" :auto-upload="false"
                :on-change="handleFileChange" :on-remove="handleRemove" multiple :accept="'.pdf'" drag
                :disabled="companyData.enterpriseReview === '1' || companyData.companyInfoReview === '0'">
                <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                <div class="el-upload__text">
                  将文件拖到此处，或<em>点击上传</em>
                  <template v-if="companyData.enterpriseReview === '1'">
                    (资质已审核通过，如需修改请联系管理员)
                  </template>
                  <template v-else-if="companyData.companyInfoReview === '0'">
                    (企业信息审核中，暂时无法上传资质文件)
                  </template>
                </div>
                <template #tip>
                  <div class="el-upload__tip">
                    只能上传 PDF 文件
                  </div>
                </template>
              </el-upload>
              <div class="pdf-list" v-if="fileList.length">
                <div v-for="(file, index) in fileList" :key="index" class="pdf-item" @click="previewPdf(file)">
                  <div class="pdf-content">
                    <el-icon class="pdf-icon">
                      <Document />
                    </el-icon>
                    <span class="pdf-name">{{ file.name }}</span>
                    <el-icon v-if="companyData.enterpriseReview !== '1'" class="delete-icon"
                      @click.stop="handleRemove(file)">
                      <Delete />
                    </el-icon>
                  </div>
                </div>
              </div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitForm" :disabled="companyData.companyInfoReview === '0'"
                :title="companyData.companyInfoReview === '0' ? '企业信息审核中，暂时无法修改' : ''">
                保存修改
              </el-button>
              <el-button type="warning" @click="cancelReview" v-if="companyData.companyInfoReview === '0'">
                撤销审核
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- PDF预览对话框 -->
        <el-dialog v-model="previewVisible" :title="currentFile?.name" width="80%" :before-close="handleClosePreview"
          class="pdf-preview-dialog">
          <iframe v-if="previewUrl"
            :src="previewUrl + '#pagemode=none&scrollbar=0&toolbar=0&statusbar=1&messages=0&scrollbar=0'"
            frameborder="0" class="pdf-preview-iframe"></iframe>
        </el-dialog>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { companyInfo as getCompanyInfoApi, companyInfoUpdate } from '@/api/company/companyApi'
import { ElMessage, ElMessageBox } from 'element-plus'
import userStore from '@/store/user'
import { Document, UploadFilled, Plus, Delete, InfoFilled } from '@element-plus/icons-vue'

const companyData = ref({
  companyInfoId: '',
  companyInfoUsername: '',
  companyInfoPassword: '',
  companyLogo: '',
  companyRegisterTime: '',
  companyInfoName: '',
  companyInfoScope: '',
  companyInfoProfile: '',
  companyInfoReview: '0',
  enterpriseReview: '0',
  enterpriseFile: []
})

const fileList = ref([])

const previewVisible = ref(false)
const previewUrl = ref('')
const currentFile = ref(null)
const logoFile = ref(null)

const getReviewStatusType = (status) => {
  const statusMap = {
    'null': 'info',
    '0': 'warning',
    '1': 'success',
    '2': 'danger'
  }
  return statusMap[status] || 'info'
}

const getReviewStatusText = (status) => {
  const statusMap = {
    'null': '待提交',
    '0': '待审核',
    '1': '审核通过',
    '2': '打回修改'
  }
  return statusMap[status] || '待提交'
}

// 获取企业信息
const getCompanyInfo = async () => {
  try {
    const res = await getCompanyInfoApi(userStore().userInfo.companyInfoId)
    if (res.content) {
      companyData.value = res.content
      companyData.value.companyInfoPassword = null
      userStore().userInfo.companyInfoReview = res.content.companyInfoReview
      userStore().userInfo.enterpriseReview = res.content.enterpriseReview
    }
  } catch (error) {
    ElMessage.error('获取企业信息失败')
    console.error('获取企业信息失败:', error)
  }
}

const handleFileChange = (uploadFile) => {
  if (companyData.value.enterpriseReview === '1') {
    ElMessage.warning('资质已审核通过，如需修改请联系管理员')
    return
  }

  if (companyData.value.companyInfoReview === '0') {
    ElMessage.warning('企业信息审核中，暂时无法上传资质文件')
    return
  }

  if (!companyData.value.enterpriseFile) {
    companyData.value.enterpriseFile = []
  }

  const fileUrl = URL.createObjectURL(uploadFile.raw)
  uploadFile.url = fileUrl

  fileList.value.push(uploadFile)
  companyData.value.enterpriseFile.push(uploadFile.raw)
  ElMessage.success('文件添加成功')
}

const handleRemove = (file) => {
  if (companyData.value.enterpriseReview === '1') {
    ElMessage.warning('资质已审核通过，如需修改请联系管理员')
    return
  }

  if (!companyData.value.enterpriseFile) {
    companyData.value.enterpriseFile = []
    return
  }

  const index = companyData.value.enterpriseFile.findIndex(f => f === file.raw)
  if (index > -1) {
    companyData.value.enterpriseFile.splice(index, 1)
  }

  // 释放创建的URL
  if (file.url && file.url.startsWith('blob:')) {
    URL.revokeObjectURL(file.url)
  }

  const fileIndex = fileList.value.findIndex(f => f.uid === file.uid)
  if (fileIndex > -1) {
    fileList.value.splice(fileIndex, 1)
  }
}

const previewPdf = (file) => {
  currentFile.value = file
  if (file.raw) {
    previewUrl.value = URL.createObjectURL(file.raw)
  } else {
    previewUrl.value = file.url
  }
  previewVisible.value = true
}

const handleClosePreview = () => {
  previewVisible.value = false
  previewUrl.value = ''
  currentFile.value = null
}

const handleLogoChange = (file) => {
  const isValid = beforeLogoUpload(file.raw)
  if (!isValid) {
    return
  }
  logoFile.value = file.raw
  companyData.value.companyLogo = URL.createObjectURL(file.raw)
}

const beforeLogoUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/jpg'
  const isPNG = file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isJPG && !isPNG) {
    ElMessage.error('只能上传JPG/PNG格式的图片!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 修改邮箱验证函数
const validateEmail = () => {
  const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/
  if (!companyData.value.companyInfoUsername) {
    ElMessage.warning('请输入邮箱账号')
    return false
  }
  if (!emailRegex.test(companyData.value.companyInfoUsername)) {
    ElMessage.error('请输入正确的邮箱格式')
    return false
  }
  return true
}

// 修改提交表单函数
const submitForm = async () => {
  try {
    if (!validateEmail()) {
      return
    }

    const formData = new FormData()
    formData.append('companyInfoId', companyData.value.companyInfoId)

    if (logoFile.value) {
      formData.append('applyFiles', logoFile.value)
    }

    if (companyData.value.enterpriseFile && companyData.value.enterpriseFile.length > 0) {
      companyData.value.enterpriseFile.forEach(file => {
        formData.append('enterpriseFile', file)
      })
    } else if (companyData.value.enterpriseReview !== '1' && companyData.value.enterpriseFile == null) {
      ElMessage.warning('请上传资质文件')
      return
    }

    if (companyData.value.enterpriseReview !== '1') {
      formData.append('enterpriseReview', '0')
    }

    companyData.value.companyInfoReview = '0'


    const originalData = await getCompanyInfoApi(userStore().userInfo.companyInfoId)
    Object.keys(companyData.value).forEach(key => {
      if (key !== 'companyLogo' &&
        key !== 'enterpriseFile' &&
        key !== 'enterpriseReview') {
        if (companyData.value[key] !== originalData.content[key] ||
          (key === 'companyInfoPassword' && companyData.value[key])) {
          formData.append(key, companyData.value[key] || '')
        }
      }
    })

    await companyInfoUpdate(formData)
    ElMessage.success('保存成功')
    await getCompanyInfo()
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error(error.message || '保存失败')
  }
}

const cancelReview = async () => {
  try {
    await ElMessageBox.confirm(
      '撤销审核后，您可以重新编辑企业信息，需要重新上传PDF文件。确定要撤销吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    const formData = new FormData()
    formData.append('companyInfoId', companyData.value.companyInfoId)
    if (companyData.value.enterpriseReview !== '1') {
      formData.append('enterpriseReview', '2')
    }
    formData.append('companyInfoReview', '2')
    formData.append('enterpriseFile', '')

    await companyInfoUpdate(formData)
    ElMessage.success('已撤销审核状态')
    fileList.value = []
    companyData.value.enterpriseFile = []
    await getCompanyInfo()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('撤销审核失败:', error)
      ElMessage.error(error.message || '撤销审核失败')
    }
  }
}

onMounted(() => {
  getCompanyInfo()
})

onBeforeUnmount(() => {
  fileList.value.forEach(file => {
    if (file.url && file.url.startsWith('blob:')) {
      URL.revokeObjectURL(file.url)
    }
  })
})
</script>

<style scoped>
.company-info-container {
  padding: 16px;
  max-width: 1200px;
  margin: 0 auto;
  background-color: #f5f7fa;
  min-height: calc(100vh - 48px);
}

.info-card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 8px;
}

.card-header h2 {
  margin: 0;
  font-size: 18px;
  color: #303133;
  font-weight: 600;
}

.company-content {
  padding: 24px;
}

.alert-section {
  margin-bottom: 16px;
}

.profile-section {
  display: flex;
  gap: 40px;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  margin-bottom: 24px;
}

.avatar-section {
  width: 240px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
}

.logo-preview-img {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #ebeef5;
}

.upload-placeholder {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  border: 2px dashed #dcdfe6;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.stats-info {
  display: flex;
  gap: 24px;
  width: 100%;
  justify-content: center;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.info-section {
  flex: 1;
}

.info-table {
  width: 100%;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
}

.info-row {
  display: grid;
  grid-template-columns: 120px 1fr 120px 1fr;
  border-bottom: 1px solid #ebeef5;
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  padding: 16px;
  background: #f8f9fa;
  color: #606266;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  border-right: 1px solid #ebeef5;
  min-width: 120px;
  font-weight: 500;
}

.info-value {
  padding: 16px;
  color: #303133;
  display: flex;
  align-items: center;
  flex: 1;
  min-width: 0;
}

:deep(.el-input__wrapper) {
  box-shadow: none !important;
  background: transparent;
  width: 100%;
}

:deep(.el-input__inner) {
  height: 32px;
}

:deep(.el-tag) {
  border-radius: 4px;
  padding: 0 12px;
  height: 28px;
  line-height: 28px;
}

.detail-info-section {
  padding: 0 12px;
}

.mt-2 {
  margin-top: 12px;
}

.mt-4 {
  margin-top: 16px;
}

:deep(.el-button--primary) {
  padding: 12px 24px;
  font-weight: 500;
  transition: all 0.3s;
}

:deep(.el-button--primary:hover) {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.readonly-text {
  color: #606266;
  display: flex;
  align-items: center;
  font-size: 14px;
}

:deep(.el-upload-list) {
  margin-top: 8px;
}

.pdf-list {
  margin-top: 16px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 8px;
  background-color: #fafafa;
}

.pdf-item {
  height: 40px;
  display: flex;
  align-items: center;
  padding: 0 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 4px;
  margin-bottom: 4px;
}

.pdf-item:last-child {
  margin-bottom: 0;
}

.pdf-item:hover {
  background-color: #ecf5ff;
}

.pdf-content {
  display: flex;
  align-items: center;
  width: 100%;
}

.pdf-icon {
  margin-right: 8px;
  font-size: 18px;
  color: #409eff;
}

.pdf-name {
  color: #606266;
  font-size: 14px;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.pdf-item:hover .pdf-name {
  color: #409eff;
}

.pdf-preview-iframe {
  width: 100%;
  height: 100%;
  min-height: 75vh;
}

.qualification-upload {
  width: 100%;
}

:deep(.qualification-upload .el-upload) {
  width: 100%;
}

:deep(.qualification-upload .el-upload-dragger) {
  width: 100%;
  height: 180px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 2px dashed #dcdfe6;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}

:deep(.qualification-upload .el-upload-dragger:hover) {
  border-color: #409eff;
}

:deep(.qualification-upload .el-icon--upload) {
  font-size: 48px;
  color: #909399;
  margin-bottom: 16px;
}

:deep(.qualification-upload .el-upload__text) {
  color: #606266;
  font-size: 14px;
  text-align: center;
}

:deep(.qualification-upload .el-upload__text em) {
  color: #409eff;
  font-style: normal;
}

:deep(.el-upload__tip) {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
  text-align: center;
}

.review-alert {
  margin-bottom: 16px;
}

.review-alert :deep(.el-alert__title) {
  font-size: 16px;
  font-weight: bold;
}

.review-alert :deep(.el-alert__description) {
  margin: 8px 0;
  font-size: 14px;
  color: #666;
}

:deep(.el-input.is-disabled .el-input__wrapper) {
  background-color: transparent !important;
  box-shadow: none !important;
}

:deep(.el-input.is-disabled .el-input__inner) {
  -webkit-text-fill-color: #606266 !important;
  color: #606266 !important;
}

:deep(.el-textarea.is-disabled .el-textarea__inner) {
  background-color: transparent !important;
  border: none !important;
  -webkit-text-fill-color: #606266 !important;
  color: #606266 !important;
}

:deep(.el-upload.is-disabled) {
  opacity: 0.8;
}

:deep(.el-upload.is-disabled .upload-placeholder) {
  border-style: solid;
  border-color: #e4e7ed;
  cursor: not-allowed;
}

:deep(.el-upload.is-disabled .upload-placeholder:hover) {
  border-color: #e4e7ed;
}

:deep(.el-upload-dragger.is-disabled) {
  background-color: transparent !important;
  border-style: solid !important;
  border-color: #e4e7ed !important;
}

:deep(.el-upload-dragger.is-disabled:hover) {
  border-color: #e4e7ed !important;
}

.info-content :deep(.is-disabled) {
  color: #606266 !important;
}

:deep(.el-upload.is-disabled .el-icon) {
  color: #909399 !important;
}

.delete-icon {
  margin-left: 8px;
  color: #f56c6c;
  cursor: pointer;
  font-size: 16px;
}

.delete-icon:hover {
  color: #f78989;
}

:deep(.el-upload.is-disabled) {
  cursor: not-allowed;
}

:deep(.el-upload.is-disabled .el-upload-dragger) {
  background-color: #f5f7fa;
  border-color: #e4e7ed;
  cursor: not-allowed;
}

:deep(.el-upload.is-disabled .el-upload-dragger:hover) {
  border-color: #e4e7ed;
}

:deep(.el-upload.is-disabled .el-upload__text) {
  color: #909399;
}

:deep(.el-upload.is-disabled .el-upload__text em) {
  color: #909399;
  font-style: normal;
}

@media (max-width: 768px) {
  .profile-section {
    flex-direction: column;
  }

  .avatar-section {
    width: 100%;
  }

  .info-row {
    grid-template-columns: 120px 1fr;
  }

  .info-label,
  .info-value {
    padding: 12px;
  }
}

.logo-upload-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.upload-tip {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
  padding: 4px 8px;
  background-color: #f4f4f5;
  border-radius: 4px;
}

.upload-tip .el-icon {
  font-size: 14px;
  color: #909399;
}
</style>
