<template>
  <div class="signup-page">
    <!-- 导航栏 -->
    <nav class="nav-header">
      <div class="back-arrow" @click="handleBack">
        <i class="arrow left"></i>
      </div>
      <div class="logo-container">
        <img src="https://www.gstatic.com/images/branding/googlelogo/svg/googlelogo_clr_74x24px.svg" alt="Google"
          class="google-logo">
        <span class="workspace-text">问呗</span>
      </div>
    </nav>

    <!-- 主要内容区域 -->
    <div class="main-container">
      <div class="form-section">
        <div class="register-container">
          <h1 class="title">您将如何登录?</h1>
          <p class="subtitle">您将使用自己的邮箱来创建企业账号。</p>


          <div class="form-group">
            <input v-model="formData.email" type="email" class="form-input" :class="{ 'error': errors.email }"
              placeholder=" ">
            <label>企业电子邮件地址</label>
            <span v-if="errors.email" class="error-message">请输入企业电子邮件地址</span>
          </div>

          <div class="form-group">
            <input v-model="formData.password" type="password" class="form-input" :class="{ 'error': errors.password }"
              placeholder=" ">
            <label>密码</label>
            <span v-if="errors.password" class="error-message">请输入密码</span>
          </div>

          <div class="button-group">
            <button class="next-button" @click="handleRegister">注册</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { registerCompany } from '@/api/company/companyApi'
import userStore from '@/store/user'
import { ElMessage } from 'element-plus'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const formData = ref({
  email: '',
  password: '',

})

const errors = ref({
  email: false,
  password: false
})

const handleBack = () => {
  router.push('/users/registerCompany/contactInfo')
}

const handleRegister = () => {
  errors.value = {
    email: false,
    password: false
  }

  if (!formData.value.email) {
    errors.value.email = true
  }
  if (!formData.value.password) {
    errors.value.password = true
  }


  if (!errors.value.email && !errors.value.password) {
    console.log('注册信息：', formData.value)
    registerCompany({
      userEmail: formData.value.email,
      companyInfoName: userStore().registerInfo[0].companyName,
      companyInfoUsername: formData.value.email,
      userPhone: userStore().registerInfo[1].phone,
      companyInfoArea: userStore().registerInfo[0].region,
      companyInfoPassword: formData.value.password,
      userName: userStore().registerInfo[1].name,
    }).then(res => {
      if (res.code == 200) {
        ElMessage.success('注册成功')
        router.push('/auth/login')
      } else {
        ElMessage.error(res.message)
      }
    })
  }
}
</script>

<style scoped>
.signup-page {
  user-select: none;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #fff;
}

.nav-header {
  justify-content: center;
  height: 64px;
  border-bottom: 1px solid #dadce0;
  display: flex;
  align-items: center;
  padding: 0 24px;
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 4px;
}

.google-logo {
  height: 24px;
}

.workspace-text {
  color: #5f6368;
  font-size: 22px;
  font-weight: 400;
  margin-left: 4px;
}

.main-container {
  flex: 1;
  display: flex;
  justify-content: center;
  padding: 48px 24px;
}

.form-section {
  width: 100%;
  max-width: 450px;
}

.register-container {
  width: 100%;
}

.title {
  font-size: 24px;
  color: #202124;
  margin-bottom: 8px;
  font-weight: 400;
}

.subtitle {
  font-size: 16px;
  color: #5f6368;
  margin-bottom: 32px;
}

.form-group {
  position: relative;
  margin-bottom: 24px;
}

.form-input {
  width: 100%;
  padding: 16px 12px;
  border: 2px solid #dadce0;
  border-radius: 4px;
  font-size: 16px;
  transition: all 0.2s;
}

.form-input:focus {
  border-color: #1a73e8;
  border-width: 2px;
  outline: none;
}

.form-group label {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: white;
  padding: 0 4px;
  color: #5f6368;
  transition: all 0.2s;
  pointer-events: none;
}

.form-input:focus+label,
.form-input:not(:placeholder-shown)+label {
  top: 0;
  font-size: 12px;
  color: #1a73e8;
}

.button-group {
  margin-top: 32px;
  display: flex;
  gap: 12px;
}

.next-button {
  background-color: #1a73e8;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
}

.next-button:hover {
  background-color: #1557b0;
}

.back-arrow {
  position: absolute;
  left: 24px;
  cursor: pointer;
  padding: 8px;
}


.arrow {
  border: solid #5f6368;
  border-width: 0 2px 2px 0;
  display: inline-block;
  padding: 4px;
}

.back-arrow:hover {
  background-color: #f8f9fa;
}

.left {
  transform: rotate(135deg);
  -webkit-transform: rotate(135deg);
}

.form-input.error {
  border-color: #d93025;
}

.error-message {
  position: absolute;
  bottom: -20px;
  left: 12px;
  color: #d93025;
  font-size: 12px;
  margin-top: 4px;
  display: block;
}
</style>
