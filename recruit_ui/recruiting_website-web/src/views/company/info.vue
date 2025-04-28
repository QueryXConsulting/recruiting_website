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
          <el-alert v-if="hasSubmitFailure" title="检测到上次提交失败" type="error"
            :description="`失败原因: ${failureReason}，失败时间: ${failureTimestamp}。系统已为您缓存了上次的提交内容，您可以点击下方按钮恢复提交。`"
            show-icon :closable="true" class="review-alert">
            <template #default>
              <div class="alert-actions">
                <el-button size="small" type="primary" @click="recoverFailedSubmission">
                  恢复上次提交
                </el-button>
                <el-button size="small" @click="hasSubmitFailure = false">
                  清除提示
                </el-button>
              </div>
            </template>
          </el-alert>

          <el-alert v-if="companyData.companyInfoReview === '0'" title="企业信息正在审核中" type="warning"
            description="您的企业信息正在审核中，审核通过后即可正常使用所有功能。" show-icon :closable="false" class="review-alert" />

          <el-alert v-if="companyData.enterpriseReview === '2'" title="企业资质审核未通过" type="error"
            description="您的企业资质审核未通过，请根据反馈进行修改后重新提交。" show-icon :closable="false" class="review-alert" />
        </div>

        <div class="profile-section">
          <div class="avatar-section">
            <div class="logo-upload-container">
              <div class="upload">
                <img v-if="companyData.companyLogo" :src="companyData.companyLogo" class="logo-preview-img" @click="openUpload" />
                <el-icon v-else class="avatar-uploader-icon" @click="openUpload"><Plus /></el-icon>
              </div>

              <div class="upload-tip">
                <el-icon>
                  <InfoFilled />
                </el-icon>
                <span>支持 jpg/png/jpeg 格式，大小不超过2MB。修改后需重新提交审核</span>
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
                  <el-button v-if="companyData.enterpriseReview !== null && companyData.enterpriseReview !== ''"
                    type="primary" link size="small" @click="handleViewEnterpriseFiles" class="view-files-btn">
                    <el-icon><Document /></el-icon>
                    查看资质
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="detail-info-section">
          <el-form :model="companyData" label-width="120px">
            <el-form-item label="公司介绍">
              <el-input v-model="companyData.companyInfoProfile" type="textarea" :rows="4"
                :disabled="companyData.companyInfoReview === '0'"
                placeholder="请输入公司介绍内容" />
            </el-form-item>
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
              <el-button type="success" @click="saveTempForm">临时保存</el-button>
              <el-button type="warning" @click="restoreTempForm" v-if="hasTempFormSave">还原</el-button>
              <el-button type="warning" @click="cancelReview" v-if="companyData.companyInfoReview === '0'">
                撤销审核
              </el-button>
              <el-button type="danger" @click="retrySubmit" v-if="hasSubmitFailure">
                重试提交
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 图片裁剪对话框 -->
        <el-dialog v-model="uploadDialog" title="裁剪Logo图片" width="500px" :before-close="handleCloseUpload">
          <div class="cropper-container">
            <div v-if="cropperImage" class="cropper-wrapper">
              <vue-cropper
                ref="cropperRef"
                :key="cropperKey"
                :src="cropperImage"
                :aspect-ratio="1"
                :view-mode="1"
                :auto-crop-area="0.8"
                :background="false"
                :zoomable="true"
                :center="true"
                :guides="true"
                :highlight="false"
                :responsive="true"
                :restore="false"
                class="cropper-img"
              ></vue-cropper>
            </div>
            <div v-else class="upload-placeholder" @click="chooseImage">
              <div>点击上传图片</div>
            </div>

            <div class="cropper-controls" v-if="cropperImage">
              <div class="status-text">{{ fileName ? `已选择: ${fileName}` : '' }}</div>
              <div class="cropper-tools">
                <el-button @click="rotateImage(-90)" size="small">左旋转</el-button>
                <el-button @click="rotateImage(90)" size="small">右旋转</el-button>
              </div>
            </div>
          </div>

          <!-- 隐藏的文件上传输入框 -->
          <input v-show="false" ref="fileRef" type="file" accept="image/png, image/jpeg, image/jpg" @change="getImageInfo" />

          <template #footer>
            <p class="upload-tips">{{ uploadTips }}</p>
            <div class="dialog-footer">
              <el-button @click="handleCloseUpload">取消</el-button>
              <el-button type="primary" @click="submitImage" :disabled="!cropperImage">确定</el-button>
            </div>
          </template>
        </el-dialog>

        <!-- PDF预览对话框 -->
        <el-dialog v-model="previewVisible" :title="currentFile?.name" width="80%" :before-close="handleClosePreview"
          class="pdf-preview-dialog">
          <iframe v-if="previewUrl"
            :src="previewUrl + '#pagemode=none&scrollbar=0&toolbar=0&statusbar=1&messages=0&scrollbar=0'"
            frameborder="0" class="pdf-preview-iframe"></iframe>
        </el-dialog>

        <!-- 添加一个全局的上传失败状态提示 -->
        <div v-if="hasSubmitFailure" class="submit-failure-indicator">
          <el-tag type="danger">
            <el-icon><Warning /></el-icon>
            <span>上次提交失败 ({{ failureTimestamp }})</span>
          </el-tag>
        </div>

        <!-- 保持现有的临时保存状态提示 -->
        <div v-if="hasTempFormSave" class="temp-save-indicator">
          <el-tag type="success">
            <el-icon><Check /></el-icon>
            <span>表单已临时保存 ({{ tempSavedData?.timestamp }})</span>
          </el-tag>
        </div>

        <!-- 添加企业资质文件查看对话框 -->
        <el-dialog v-model="enterpriseFilesVisible" title="企业资质文件" width="500px">
          <div v-if="loadingEnterpriseFiles" class="loading-files">
            <el-skeleton :rows="3" animated />
          </div>
          <div v-else-if="enterpriseFiles.length" class="pdf-list">
            <div v-for="(file, index) in enterpriseFiles" :key="index" class="pdf-item" @click="previewEnterpriseFile(file)">
              <div class="pdf-content">
                <el-icon class="pdf-icon">
                  <Document />
                </el-icon>
                <span class="pdf-name">{{ file.name }}</span>
              </div>
            </div>
          </div>
          <div v-else class="no-files">
            <el-empty description="暂无资质文件" />
          </div>
        </el-dialog>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { companyInfo as getCompanyInfoApi, companyInfoUpdate, companyInfoEnterpriseFiles } from '@/api/company/companyApi'
import { ElMessage, ElMessageBox } from 'element-plus'
import userStore from '@/store/user'
import { Document, UploadFilled, Plus, Delete, InfoFilled, Check, Warning } from '@element-plus/icons-vue'
import 'cropperjs/dist/cropper.css'
import VueCropper from 'vue-cropperjs'
import { companyEnterpriseFiles } from '@/api/admin/adminApi'

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

// 裁剪相关变量
const uploadDialog = ref(false)
const uploadTips = ref('支持 jpg/png/jpeg 格式，大小不超过2MB')
const cropperImage = ref('')
const cropperRef = ref(null)
const fileRef = ref(null)
const fileName = ref('')
const cropperKey = ref(0)
const tempSavedImage = ref(null)
const hasTempSave = ref(false)

// 添加表单临时保存相关变量
const tempSavedData = ref(null) // 临时保存的整个表单数据
const hasTempFormSave = ref(false) // 是否有临时保存的表单

// 在 script setup 下，引入 ref 等之后，添加存储原始数据的变量
const originalCompanyData = ref(null)
const originalFileList = ref([])
const originalLogoFile = ref(null)

// 添加提交失败缓存相关变量
const submitFailedFiles = ref([])
const hasSubmitFailure = ref(false)
const failureReason = ref('')
const failureTimestamp = ref('')
const retryCount = ref(0)
const maxRetries = 3

// 添加查看企业资质文件相关变量
const enterpriseFilesVisible = ref(false)
const enterpriseFiles = ref([])
const loadingEnterpriseFiles = ref(false)

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

// 获取企业信息，修改此函数以保存原始数据
const getCompanyInfo = async () => {
  try {
    const res = await getCompanyInfoApi(userStore().userInfo.companyInfoId)
    if (res.content) {
      companyData.value = res.content
      companyData.value.companyInfoPassword = null
      userStore().userInfo.companyInfoReview = res.content.companyInfoReview
      userStore().userInfo.enterpriseReview = res.content.enterpriseReview
      if (companyData.value.companyInfoReview == null && companyData.value.enterpriseReview == null) {
        ElMessage.warning('请先完善企业信息并提交资质文件，以便我们能够审核您的企业资质。')
      }
      // 保存原始数据
      originalCompanyData.value = JSON.parse(JSON.stringify(res.content))
      originalCompanyData.value.companyInfoPassword = null
      if (fileList.value.length) {
        originalFileList.value = [...fileList.value]
      }
      originalLogoFile.value = logoFile.value
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

// 打开裁剪对话框
const openUpload = () => {
  if (companyData.value.companyInfoReview === '0') {
    ElMessage.warning('企业信息审核中，暂时无法修改')
    return
  }
  uploadDialog.value = true
}

// 关闭裁剪对话框
const handleCloseUpload = () => {
  if (hasTempSave.value) {
    ElMessageBox.confirm(
      '您有未提交的临时保存，关闭后将丢失这些更改。确定要关闭吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    ).then(() => {
      uploadDialog.value = false
      cropperImage.value = ''
      // 不清除临时保存，以便下次可以恢复
    }).catch(() => {
      // 取消关闭
    })
  } else {
    uploadDialog.value = false
    cropperImage.value = ''
  }
}

// 选择图片
const chooseImage = () => {
  fileRef.value.click()
}

// 获取文件信息
const getImageInfo = (e) => {
  // 上传的文件
  const file = e.target.files[0]
  if (!file) return

  const fileSize = (file.size / 1024 / 1024).toFixed(2)
  if (fileSize > 2) {
    ElMessage.warning('图片大小必须在2MB以内！')
    return false
  }

  fileName.value = file.name

  // 清理旧的URL
  if (cropperImage.value && cropperImage.value.startsWith('blob:')) {
    URL.revokeObjectURL(cropperImage.value)
    cropperImage.value = ''
  }


  cropperKey.value++


  const URL = window.URL || window.webkitURL


  setTimeout(() => {
    cropperImage.value = URL.createObjectURL(file)
  }, 50)

  e.target.value = ''
}


const rotateImage = (angle) => {
  if (cropperRef.value) {
    cropperRef.value.rotate(angle)
  }
}

// 确认裁剪
const submitImage = () => {
  if (!cropperRef.value || !cropperImage.value) {
    ElMessage.warning('请先上传图片')
    return
  }

  const canvas = cropperRef.value.getCroppedCanvas({
    width: 200,
    height: 200,
    minWidth: 100,
    minHeight: 100,
    maxWidth: 2000,
    maxHeight: 2000,
    fillColor: '#fff',
    imageSmoothingEnabled: true,
    imageSmoothingQuality: 'high',
  })

  canvas.toBlob(function (blob) {
    // 创建文件对象用于上传
    const file = new File([blob], fileName.value || 'company-logo.png', { type: 'image/png' })
    logoFile.value = file

    // 更新预览
    companyData.value.companyLogo = URL.createObjectURL(blob)

    // 关闭裁剪对话框
    uploadDialog.value = false
    cropperImage.value = ''

    ElMessage.success('Logo裁剪成功')
  }, 'image/png')
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

// 修改提交表单函数，添加错误处理和缓存
const submitForm = async () => {
  try {
    if (!validateEmail()) {
      return
    }

    const formData = new FormData()
    formData.append('companyInfoId', companyData.value.companyInfoId)

    // 缓存所有将要上传的文件
    const filesToUpload = []

    if (logoFile.value) {
      formData.append('applyFiles', logoFile.value)
      filesToUpload.push({
        type: 'logo',
        file: logoFile.value,
        name: logoFile.value.name
      })
    }

    if (companyData.value.enterpriseFile && companyData.value.enterpriseFile.length > 0) {
      companyData.value.enterpriseFile.forEach(file => {
        formData.append('enterpriseFile', file)
        filesToUpload.push({
          type: 'enterprise',
          file: file,
          name: file.name
        })
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

    // 保存表单数据副本以便失败后恢复
    const formDataBackup = {
      formData: JSON.parse(JSON.stringify(companyData.value)),
      files: filesToUpload
    }

    try {
      await companyInfoUpdate(formData)
      ElMessage.success('保存成功')

      // 清除临时保存状态和失败缓存
      hasTempFormSave.value = false
      tempSavedData.value = null
      hasSubmitFailure.value = false
      submitFailedFiles.value = []
      failureReason.value = ''
      failureTimestamp.value = ''
      retryCount.value = 0

      // 重新获取数据，更新原始数据
      await getCompanyInfo()
    } catch (error) {
      // 详细的错误处理
      console.error('提交失败:', error)

      // 保存失败信息
      hasSubmitFailure.value = true
      failureReason.value = error.message || '网络错误或服务器无响应'
      failureTimestamp.value = new Date().toLocaleString()
      submitFailedFiles.value = filesToUpload

      // 显示详细错误提示
      ElMessageBox.alert(
        `提交失败，原因：${failureReason.value}。系统已缓存您的提交内容，您可以稍后再试。`,
        '提交失败',
        {
          confirmButtonText: '确定',
          type: 'error',
          callback: () => {
            // 如果文件较大，建议用户检查网络
            if (getTotalFileSize(filesToUpload) > 5) { // 大于5MB
              ElMessage.warning('您上传的文件较大，请确保网络连接稳定后再次尝试')
            }
          }
        }
      )
    }
  } catch (error) {
    console.error('表单处理失败:', error)
    ElMessage.error('表单处理失败：' + (error.message || '未知错误'))
  }
}

// 计算文件总大小的辅助函数
const getTotalFileSize = (files) => {
  return files.reduce((total, file) => {
    return total + (file.file.size / (1024 * 1024))
  }, 0)
}

// 添加自动重试功能
const retrySubmit = async () => {
  if (retryCount.value >= maxRetries) {
    ElMessage.error('已达到最大重试次数，请手动重试或联系管理员')
    return
  }

  retryCount.value++
  ElMessage.info(`正在进行第 ${retryCount.value} 次重试...`)

  try {
    await submitForm()
  } catch (error) {
    console.error('重试失败:', error)
    ElMessage.error(`重试失败: ${error.message || '未知错误'}`)
  }
}

// 添加恢复失败提交的功能
const recoverFailedSubmission = () => {
  if (!hasSubmitFailure.value || submitFailedFiles.value.length === 0) {
    ElMessage.warning('没有需要恢复的提交')
    return
  }

  ElMessageBox.confirm(
    `是否恢复上次失败的提交？失败时间: ${failureTimestamp.value}`,
    '恢复提交',
    {
      confirmButtonText: '恢复',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 这里可以直接调用submitForm，因为表单数据仍然保持原样
    submitForm()
  }).catch(() => {
    // 用户取消恢复
  })
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

    try {
      await companyInfoUpdate(formData)
      ElMessage.success('已撤销审核状态')
      fileList.value = []
      companyData.value.enterpriseFile = []
      await getCompanyInfo()
    } catch (error) {
      // 详细的错误处理
      console.error('撤销审核失败:', error)
      ElMessageBox.alert(
        `撤销审核失败，原因：${error.message || '网络错误或服务器无响应'}`,
        '操作失败',
        {
          confirmButtonText: '确定',
          type: 'error'
        }
      )
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('撤销审核被取消:', error)
    }
  }
}

// 修改还原功能，恢复到修改前的原始数据
const restoreTempForm = () => {
  if (!originalCompanyData.value) {
    ElMessage.warning('没有可还原的原始数据')
    return
  }

  ElMessageBox.confirm(
    '确定要还原为修改前的数据吗？当前所有未保存的修改将会丢失。',
    '还原数据',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    // 还原表单数据
    companyData.value = JSON.parse(JSON.stringify(originalCompanyData.value))

    // 还原Logo文件
    logoFile.value = originalLogoFile.value

    // 如果当前有新的Logo图片URL，释放它
    if (companyData.value.companyLogo && companyData.value.companyLogo.startsWith('blob:')
        && originalCompanyData.value.companyLogo !== companyData.value.companyLogo) {
      URL.revokeObjectURL(companyData.value.companyLogo)
      companyData.value.companyLogo = originalCompanyData.value.companyLogo
    }

    // 还原文件列表
    // 先释放当前fileList中的blob URLs
    fileList.value.forEach(file => {
      if (file.url && file.url.startsWith('blob:')) {
        URL.revokeObjectURL(file.url)
      }
    })
    fileList.value = originalFileList.value.length ? [...originalFileList.value] : []

    // 清除临时保存
    hasTempFormSave.value = false
    tempSavedData.value = null

    ElMessage.success('已还原为修改前的数据')
  }).catch(() => {
    // 取消操作
  })
}

// 临时保存表单数据
const saveTempForm = () => {
  // 先验证邮箱
  if (!validateEmail()) {
    return
  }


  let logoUrl = null

  const saveData = async () => {
    // 创建临时保存对象
    tempSavedData.value = {
      formData: JSON.parse(JSON.stringify(companyData.value)), // 深拷贝表单数据
      logoFile: logoFile.value, // 保存Logo文件
      logoUrl: logoUrl, // 保存Logo URL
      fileList: [...fileList.value], // 保存文件列表
      timestamp: new Date().toLocaleString() // 保存时间戳
    }

    hasTempFormSave.value = true
    ElMessage.success(`表单数据已临时保存 (${tempSavedData.value.timestamp})`)
  }

  // 如果有logoFile，需要特殊处理保存blob
  if (logoFile.value) {
    const reader = new FileReader()
    reader.onload = (e) => {
      logoBlob = new Blob([e.target.result], { type: logoFile.value.type })
      logoUrl = companyData.value.companyLogo
      saveData()
    }
    reader.readAsArrayBuffer(logoFile.value)
  } else {
    saveData()
  }
}

// 页面关闭前提示
const handleBeforeUnload = (e) => {
  if (hasTempFormSave.value) {
    e.preventDefault()
    e.returnValue = '您有未提交的临时保存数据，关闭页面将丢失这些更改！'
    return e.returnValue
  }
}

// 修改企业资质文件查看方法，调用真实API
const handleViewEnterpriseFiles = async () => {
  if (!companyData.value.companyInfoId) {
    ElMessage.warning('企业信息ID不存在')
    return
  }

  enterpriseFilesVisible.value = true
  loadingEnterpriseFiles.value = true
  enterpriseFiles.value = []

  try {
    // 如果已经有fileList中的文件，可以直接使用
    if (fileList.value.length > 0) {
      enterpriseFiles.value = fileList.value.map(file => ({
        name: file.name,
        url: file.url || (file.raw ? URL.createObjectURL(file.raw) : ''),
        size: file.size || (file.raw ? file.raw.size : 0),
        isTemp: file.url ? false : true
      })).filter(file => file.url)

      if (enterpriseFiles.value.length > 0) {
        loadingEnterpriseFiles.value = false
        return
      }
    }


    const response = await companyInfoEnterpriseFiles(companyData.value.companyInfoId)

    if (response && response.content && Object.keys(response.content).length > 0) {
      enterpriseFiles.value = Object.entries(response.content).map(([name, content]) => {
        if (!content) {
          return null;
        }

        try {
          // 处理后端返回的base64编码数据
          const binaryString = atob(content);
          const bytes = new Uint8Array(binaryString.length);
          for (let i = 0; i < binaryString.length; i++) {
            bytes[i] = binaryString.charCodeAt(i);
          }

          // 创建blob对象
          const blob = new Blob([bytes], { type: 'application/pdf' });
          const url = URL.createObjectURL(blob);

          return {
            name: decodeURIComponent(name),
            url: url,
            size: bytes.length
          };
        } catch (error) {
          console.error('处理文件失败:', name, error);
          return null;
        }
      }).filter(Boolean);

      if (enterpriseFiles.value.length === 0) {
        ElMessage.info('暂无资质文件或文件格式不正确');
      }
    } else {
      ElMessage.info('暂无资质文件');
    }
  } catch (error) {
    console.error('获取企业资质文件失败:', error);
    ElMessage.error('获取企业资质文件失败: ' + (error.message || '未知错误'));
  } finally {
    loadingEnterpriseFiles.value = false;
  }
}

// 预览企业资质文件
const previewEnterpriseFile = (file) => {
  currentFile.value = file;
  previewUrl.value = file.url;
  previewVisible.value = true;
}

onMounted(() => {
  getCompanyInfo()

  // 添加页面关闭提示
  window.addEventListener('beforeunload', handleBeforeUnload)

  // 检查是否有上次失败的提交需要恢复
  if (hasSubmitFailure.value && submitFailedFiles.value.length > 0) {
    ElMessage({
      message: '检测到上次有未成功的提交，您可以点击"恢复上次提交"按钮进行恢复',
      type: 'warning',
      duration: 5000
    })
  }
})

onBeforeUnmount(() => {
  fileList.value.forEach(file => {
    if (file.url && file.url.startsWith('blob:')) {
      URL.revokeObjectURL(file.url)
    }
  })

  if (cropperImage.value && cropperImage.value.startsWith('blob:')) {
    URL.revokeObjectURL(cropperImage.value)
  }

  if (companyData.value.companyLogo && companyData.value.companyLogo.startsWith('blob:')) {
    URL.revokeObjectURL(companyData.value.companyLogo)
  }

  // 清理临时保存的资源
  if (tempSavedImage.value && tempSavedImage.value.url.startsWith('blob:')) {
    URL.revokeObjectURL(tempSavedImage.value.url)
  }

  // 移除页面关闭提示
  window.removeEventListener('beforeunload', handleBeforeUnload)

  // 释放新增的资源
  enterpriseFiles.value.forEach(file => {
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

.upload {
  cursor: pointer;
}

.logo-preview-img {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #ebeef5;
  cursor: pointer;
}

:deep(.el-icon.avatar-uploader-icon) {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
  border: 1px dashed var(--el-border-color);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
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

/* 添加裁剪相关样式 */
.cropper-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.cropper-wrapper {
  width: 100%;
  height: 300px;
  overflow: hidden;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.cropper-img {
  display: block;
  max-width: 100%;
  height: 300px;
}

/* Override Cropper.js styles for better visibility */
:deep(.cropper-view-box) {
  outline: 1px solid #409eff;
  outline-color: rgba(64, 158, 255, 0.75);
}

:deep(.cropper-line) {
  background-color: #409eff;
}

:deep(.cropper-point) {
  background-color: #409eff;
  width: 8px;
  height: 8px;
  opacity: 0.75;
}

.upload-placeholder {
  width: 100%;
  height: 300px;
  border: 2px dashed #dcdfe6;
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: border-color 0.3s;
}

.upload-placeholder:hover {
  border-color: #409eff;
}

.upload-placeholder .el-icon {
  font-size: 48px;
  color: #909399;
  margin-bottom: 12px;
}

.upload-placeholder div {
  color: #606266;
  font-size: 14px;
}

.cropper-controls {
  width: 100%;
  margin-top: 16px;
}

.cropper-tools {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.upload-tips {
  margin: 0;
  font-size: 12px;
  color: #909399;
  text-align: center;
}

.status-text {
  text-align: center;
  margin-bottom: 8px;
  font-size: 14px;
  color: #606266;
}

.success-text {
  color: #67c23a;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.success-text .el-icon {
  font-size: 16px;
}

.temp-save-indicator {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 100;
  display: flex;
  align-items: center;
  padding: 8px 12px;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.temp-save-indicator .el-tag {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 6px 10px;
}

.temp-save-indicator .el-icon {
  margin-right: 4px;
}

/* 添加新的样式 */
.alert-actions {
  margin-top: 8px;
  display: flex;
  gap: 12px;
}

.submit-failure-indicator {
  position: fixed;
  bottom: 60px;
  right: 20px;
  z-index: 100;
  display: flex;
  align-items: center;
  padding: 8px 12px;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.submit-failure-indicator .el-tag {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 6px 10px;
}

.submit-failure-indicator .el-icon {
  margin-right: 4px;
}

/* 添加新的样式 */
.view-files-btn {
  margin-left: 12px;
}

.loading-files {
  padding: 20px;
}

.no-files {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 120px;
  color: #909399;
}
</style>
