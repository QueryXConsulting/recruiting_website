<template>
  <div class="signup-page">
    <!-- 导航栏 -->
    <nav class="nav-header">
      <div class="logo-container">
        <img src="/public/logo.png" alt="Google"
          class="logo">
        <span class="workspace-text">问呗</span>
      </div>
    </nav>

    <!-- 主要内容区域 -->
    <div class="main-container">
      <div class="form-section" :class="{ 'slide-out': isSliding }">
        <div class="register-container">
          <h1 class="title">我们开始吧</h1>


          <div class="form-group">
            <input v-model="formData.companyName" type="text" class="form-input" :class="{ error: errors.companyName }"
              placeholder=" ">
            <label style="color: #FF7427;">企业名称</label>
          </div>

          <div class="form-group">
            <input v-model="formData.region" type="text" class="form-input" :class="{ error: errors.region }"
              placeholder=" ">
            <label  style="color: #FF7427;">所在城市</label>
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

import useUserStore from '@/store/user'
import { ElMessage } from 'element-plus'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()
const formData = ref({
  companyName: userStore.registerInfo[0]?.companyName || '',
  region: userStore.registerInfo[0]?.region || ''
})

const errors = ref({
  companyName: false,
  region: false
})

const isSliding = ref(false)

const handleNext = () => {
  errors.value.companyName = formData.value.companyName === ''
  errors.value.region = formData.value.region === ''

  if (errors.value.companyName || errors.value.region) {
    ElMessage.error('请输入完整信息')
    return
  }

  isSliding.value = true
  setTimeout(() => {
    userStore.registerInfo[0] = {
      companyName: formData.value.companyName,
      region: formData.value.region
    }
    router.push('/users/registerCompany/contactInfo')
  }, 500)
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

.logo {
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
  font-size: 2rem;
  font-weight: 400;
  letter-spacing: 0;
  line-height: 2.5rem;
  /* margin: 0; */
  margin-bottom: 36px;
  color: #FF7427;
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

.form-input.error {
  border-color: #d93025;
}

.form-input.error+label {
  color: #d93025;
}

.button-group {
  margin-top: 32px;
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

.footer {
  padding: 24px;
  border-top: 1px solid #dadce0;
}

.footer-links {
  display: flex;
  gap: 24px;
  justify-content: center;
}

.footer-links a {
  color: #5f6368;
  text-decoration: none;
  font-size: 12px;
}

.footer-links a:hover {
  text-decoration: underline;
}
</style>
