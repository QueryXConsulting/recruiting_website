<template>
  <div class="user-info-container">
    <el-card class="info-card">
      <template #header>
        <div class="card-header">
          <h2>员工个人账号信息</h2>
        </div>
      </template>

      <div class="user-content">
        <div class="profile-section">
          <div class="avatar-section">
            <div class="avatar-upload-container">
              <el-avatar :size="120" :src="userInfo.userAvatar" />
              <el-upload class="avatar-uploader" action="#" :auto-upload="false" :show-file-list="false"
                :on-change="handleAvatarChange" accept="image/*">
                <el-button size="small">更换头像</el-button>
              </el-upload>
            </div>
          </div>

          <div class="info-section">
            <div class="info-table">
              <div class="info-row">
                <div class="info-label">用户名</div>
                <div class="info-value">
                  <el-input v-model="userInfo.userName" placeholder="请输入用户名" />
                </div>
              </div>
              <div class="info-row">
                <div class="info-label">邮箱账号</div>
                <div class="info-value">
                  <el-input v-model="userInfo.userEmail" placeholder="请输入邮箱" />
                </div>
                <div class="info-label">手机号</div>
                <div class="info-value">
                  <el-input v-model="userInfo.userPhone" placeholder="请输入手机号" />
                </div>
              </div>
              <div class="info-row">
                <div class="info-label">密码</div>
                <div class="info-value">
                  <el-input v-model="passwordInput" placeholder="输入新密码以修改" show-password />
                </div>
                <div class="info-label">角色</div>
                <div class="info-value">{{ userInfo.userRole }}</div>
              </div>
            </div>
            <div class="action-buttons">
              <el-button type="primary" @click="handleSave">保存修改</el-button>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserInfo, updateUserCompany, } from '@/api/company/companyApi'
import { ElMessage } from 'element-plus'

const userInfo = ref({
  userId: '',
  userName: '',
  userEmail: '',
  userPhone: '',
  userPassword: '',
  userAvatar: '',
  userRole: '',
  avatarFile: null
})

const passwordInput = ref('')

const getUserInfoData = async () => {
  try {
    let result = await getUserInfo()
    if (result.code === 200) {
      userInfo.value = result.content

    }
  } catch (error) {
    console.error('获取用户信息失败：', error)
  }
}

const handleAvatarChange = (file) => {
  userInfo.value.userAvatar = URL.createObjectURL(file.raw)
  userInfo.value.avatarFile = file.raw
}

const handleSave = async () => {
  try {
    const formData = new FormData()
    const currentUserInfo = await getUserInfo()

    const dtoData = {
      userId: userInfo.value.userId,
      userName: userInfo.value.userName,
    }

    if (passwordInput.value) {
      dtoData.userPassword = passwordInput.value
    }

    if (userInfo.value.userEmail !== currentUserInfo.content.userEmail) {
      dtoData.userEmail = userInfo.value.userEmail
    }

    if (userInfo.value.userPhone !== currentUserInfo.content.userPhone) {
      dtoData.userPhone = userInfo.value.userPhone
    }

    formData.append('dtoJson', JSON.stringify(dtoData))

    if (userInfo.value.avatarFile) {
      formData.append('userAvatar', userInfo.value.avatarFile)
    }

    const result = await updateUserCompany(formData)

    if (result.code === 200) {
      ElMessage.success('保存成功')

      await getUserInfoData()
    } else {
      ElMessage.error(result.message || '保存失败')
    }
  } catch (error) {
    console.error('保存失败：', error)
    ElMessage.error('保存失败，请重试')
  }
}

onMounted(() => {
  getUserInfoData()
})
</script>

<style scoped>
.user-info-container {
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

.user-content {
  padding: 24px;
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

.info-value .el-input {
  width: 100%;
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

.avatar-upload-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

:deep(.el-avatar) {
  border: 3px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.action-buttons {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

.info-value :deep(.el-input__wrapper) {
  box-shadow: none !important;
  background: transparent;
}

.info-value :deep(.el-input__wrapper:hover) {
  box-shadow: none !important;
}

.info-value :deep(.el-input__inner) {
  border: none;
  padding: 0;
}

.avatar-uploader {
  margin-top: 12px;
}
</style>
