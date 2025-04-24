<template>
  <div class="change-password-dialog">
    <el-dialog v-model="dialogVisible" title="绑定用户信息" :close-on-click-modal="false" :close-on-press-escape="false"
      :show-close="false" width="500px">
      <div class="avatar-upload-container">
        <el-upload
          class="avatar-uploader"
          action="#"
          :show-file-list="false"
          :before-upload="beforeAvatarUpload"
          :http-request="uploadAvatar"
        >
          <img v-if="userAvatar" :src="userAvatar" class="avatar" />
          <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
        </el-upload>
        <div class="avatar-tip">点击上传头像</div>
      </div>

      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱地址" />
        </el-form-item>
        <el-form-item label="密码" prop="confirmPassword">
          <el-input v-model="formData.confirmPassword" type="password" placeholder="请输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="handleSubmit">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { updateUserCompany } from '@/api/company/companyApi'
import userStore from '@/store/user'

const dialogVisible = ref(true)
const formRef = ref(null)

const formData = reactive({
  confirmPassword: '',
  email: ''
})

// 用户头像URL和文件
const userAvatar = ref(userStore().userInfo?.avatar || '')
const avatarFile = ref(null)

// 上传前验证
const beforeAvatarUpload = (file) => {
  // 检查文件类型
  const isJPG = file.type === 'image/jpeg'
  const isPNG = file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG && !isPNG) {
    ElMessage.error('头像只能是JPG或PNG格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过2MB!')
    return false
  }

  // 保存文件，但不上传
  avatarFile.value = file

  // 创建本地预览
  userAvatar.value = URL.createObjectURL(file)

  return false // 阻止默认上传
}

// 自定义上传 - 不再使用
const uploadAvatar = () => {
  // 此函数不再立即上传
  return false
}

// 邮箱验证函数
const validateEmail = (rule, value, callback) => {
  const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/
  if (!value) {
    callback(new Error('请输入邮箱地址'))
  } else if (!emailRegex.test(value)) {
    callback(new Error('请输入正确的邮箱地址'))
  } else {
    callback()
  }
}

// 表单验证规则
const rules = {
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
  ],

  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { validator: validateEmail, trigger: 'blur' }
  ]
}

// 提交表单
const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const formDataObj = new FormData()

        // 添加用户数据
        const dtoJson = JSON.stringify({
          userPassword: formData.confirmPassword,
          userEmail: formData.email
        })
        formDataObj.append('dtoJson', dtoJson)

        // 如果有新的头像文件，添加到formData
        if (avatarFile.value) {
          formDataObj.append('userAvatar', avatarFile.value)
        }

        const result = await updateUserCompany(formDataObj)

        if (result.code == 200) {
          dialogVisible.value = false
          userStore().userInfo.isFirstLogin = 1

          // 如果成功并返回了头像URL，更新store中的头像
          if (result.content && result.content.userAvatar) {
            userStore().userInfo.avatar = result.content.userAvatar
          }

          ElMessage.success("修改成功")
        } else {
          dialogVisible.value = true
          ElMessage.error(result.message)
        }
      } catch (error) {
        dialogVisible.value = true
        ElMessage.error(error.message || '信息修改失败')
      }
    }
  })
}
</script>

<style scoped>
.change-password-dialog {
  :deep(.el-dialog__header) {
    text-align: center;
    margin-bottom: 20px;
    padding: 20px;
    border-bottom: 1px solid #eee;
  }

  :deep(.el-dialog__body) {
    padding: 20px 40px;
  }

  :deep(.el-dialog__footer) {
    text-align: center;
    padding: 20px;
    border-top: 1px solid #eee;
  }

  :deep(.el-button) {
    min-width: 120px;
    padding: 12px 20px;
  }

  :deep(.el-form-item__label) {
    font-weight: 500;
  }

  :deep(.el-input) {
    .el-input__wrapper {
      box-shadow: 0 0 0 1px #dcdfe6 inset;

      &:hover {
        box-shadow: 0 0 0 1px #409eff inset;
      }
    }
  }
}

/* 头像上传样式 */
.avatar-upload-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
}

.avatar-uploader {
  width: 120px;
  height: 120px;
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.avatar-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #606266;
}
</style>
