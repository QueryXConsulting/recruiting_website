<template>
  <div class="signup-page">
    <!-- 导航栏 -->
    <nav class="nav-header">
      <div class="back-arrow" @click="handleBack">
        <i class="arrow left"></i>
      </div>
      <div class="logo-container">
        <img src="/public/logo.png" alt="Google"
          class="google-logo">
        <span class="workspace-text">问呗</span>
      </div>
    </nav>


    <div class="main-container">
      <div class="form-section" :class="{ 'slide-out': isSliding }">
        <div class="register-container">
          <h1 class="title">您的联系信息是什么？</h1>
          <p class="subtitle">该信息将用于您的企业管理员账号。</p>

          <div class="form-group">
            <input v-model="formData.name" type="text" class="form-input" :class="{ 'error': errors.name }"
              placeholder=" ">
            <label style="color: #FF7427;">名字</label>
            <span class="error-message" v-if="errors.name">请输入名字</span>
          </div>

          <div class="form-group">
            <input v-model="formData.phone" type="text" class="form-input" :class="{ 'error': errors.phone }"
              placeholder=" ">
            <label style="color: #FF7427;">手机号</label>
            <span class="error-message" v-if="errors.phone">请输入有效的11位手机号</span>
          </div>

          <div class="form-group">
            <input v-model="formData.email" type="email" class="form-input" :class="{ 'error': errors.email }"
              placeholder=" ">
            <label style="color: #FF7427;">当前电子邮件地址</label>
            <span class="error-message" v-if="errors.email">请输入有效的邮箱地址</span>
          </div>

          <div class="button-group">
            <button class="next-button" @click="handleNext">下一步</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import userStore from '@/store/user'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const isSliding = ref(false)
const formData = ref({
  name: userStore().registerInfo[1]?.name || '',
  email: userStore().registerInfo[1]?.email || '',
  phone: userStore().registerInfo[1]?.phone || '',
})

const errors = ref({
  name: false,
  email: false,
  phone: false
})

// 手机号验证函数（中国大陆手机号格式）
const validatePhone = (phone) => {
  const phoneRegex = /^1[3-9]\d{9}$/
  return phoneRegex.test(phone)
}

// 邮箱验证函数
const validateEmail = (email) => {
  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
  return emailRegex.test(email)
}

const validateForm = () => {
  errors.value.name = !formData.value.name
  errors.value.phone = !formData.value.phone || !validatePhone(formData.value.phone)
  errors.value.email = !formData.value.email || !validateEmail(formData.value.email)

  return !errors.value.name && !errors.value.phone && !errors.value.email
}

const handleBack = () => {
  router.push('/users/registerCompany')
}

const handleNext = () => {
  userStore().registerInfo[1] = {
    name: formData.value.name,
    email: formData.value.email,
    phone: formData.value.phone
  }
  if (validateForm()) {
    isSliding.value = true;
    setTimeout(() => {
      router.push('/users/registerCompany/account')
    }, 500);
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
  height: 36px;
}

.workspace-text {
  color: #FF7427;
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
  transition: transform 0.5s ease-out, opacity 0.5s ease-out;
}

.form-section.slide-out {
  transform: translateX(-500px);
  opacity: 0;
}

.register-container {
  width: 100%;
}

.title {
  font-size: 24px;
  color: #FF7427;
  margin-bottom: 8px;
  font-weight: 400;
}

.subtitle {
  font-size: 16px;
  color: #FF7427;
  margin-bottom: 32px;
}

.form-group {
  position: relative;
  margin-bottom: 24px;
}

.form-input {
  width: 100%;
  padding: 16px 12px;
  border: 2px solid #EEE8AA;
  border-radius: 4px;
  font-size: 16px;
  transition: all 0.2s;
}

.form-input:focus {
  border-color: #FF7427;
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
  background-color: #FF7427;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
}

.next-button:hover {
  background-color: #FFAA66;
}

.back-arrow {
  position: absolute;
  left: 24px;
  cursor: pointer;
  padding: 8px;
}


.arrow {
  border: solid #FF7427;
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

.form-input.error+label {
  color: #d93025;
}

.form-input.error:focus+label {
  color: #d93025;
}
</style>
