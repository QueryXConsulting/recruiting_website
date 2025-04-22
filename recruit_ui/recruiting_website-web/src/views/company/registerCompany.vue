<template>
  <div class="signup-page">
    <!-- 导航栏 -->
    <nav class="nav-header">
      <div class="logo-container">
        <img style="cursor: pointer;" src="/public/logo.png" @click="router.push('/users/index');" alt="Google"
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

          <div class="form-group company-size-container">
            <input
              v-model="formData.companySize"
              type="text"
              class="form-input"
              :class="{ error: errors.companySize }"
              placeholder=" "
              readonly
              @click="showSizeOptions = true"
            >
            <label style="color: #FF7427;">公司规模</label>

            <div v-if="showSizeOptions" class="size-suggestions">
              <div
                v-for="size in companySizes"
                :key="size"
                class="size-suggestion-item"
                @click="selectSize(size)"
              >
                {{ size }}
              </div>
            </div>
          </div>

          <div class="form-group city-input-container">
            <input
              v-model="formData.region"
              type="text"
              class="form-input"
              :class="{ error: errors.region }"
              placeholder=" "
              @input="handleCityInput"
              @focus="showSuggestions = true"
            >
            <label style="color: #FF7427;">所在城市</label>

            <div v-if="showSuggestions && filteredCities.length > 0" class="city-suggestions">
              <div
                v-for="city in filteredCities"
                :key="city"
                class="city-suggestion-item"
                @click="selectCity(city)"
              >
                {{ city }}
              </div>
            </div>
          </div>

          <div class="button-group">
            <button class="next-button" @click="router.push('/users/index')">返回首页</button>
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
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()
const formData = ref({
  companyName: userStore.registerInfo[0]?.companyName || '',
  companySize: userStore.registerInfo[0]?.companySize || '',
  region: userStore.registerInfo[0]?.region || ''
})

// 城市列表
const cities = ref([
  '北京', '上海', '广州', '深圳', '杭州',
  '南京', '武汉', '成都', '重庆', '西安',
  '天津', '苏州', '长沙', '郑州', '青岛',
  '厦门', '大连', '宁波', '济南', '合肥',
  '昆明', '南宁', '福州', '贵阳', '哈尔滨',
  '石家庄', '太原', '南昌', '长春', '兰州',
  '海口', '乌鲁木齐', '西宁', '银川', '呼和浩特',
  '拉萨', '沈阳', '成都', '无锡', '佛山',
  '东莞', '珠海', '中山', '惠州', '南通'
])

// 是否显示建议列表
const showSuggestions = ref(false)

// 根据输入筛选城市
const filteredCities = computed(() => {
  if (!formData.value.region) return cities.value
  return cities.value.filter(city =>
    city.toLowerCase().includes(formData.value.region.toLowerCase())
  )
})

// 处理城市输入
const handleCityInput = () => {
  showSuggestions.value = true
}

// 选择城市
const selectCity = (city) => {
  formData.value.region = city
  showSuggestions.value = false
}

// 点击外部关闭建议列表
const handleClickOutside = (event) => {
  const cityInputContainer = document.querySelector('.city-input-container')
  if (cityInputContainer && !cityInputContainer.contains(event.target)) {
    showSuggestions.value = false
  }

  const companySizeContainer = document.querySelector('.company-size-container')
  if (companySizeContainer && !companySizeContainer.contains(event.target)) {
    showSizeOptions.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

// 公司规模选项
const companySizes = ref([
  '1-10人', '11-50人', '51-200人', '201-500人', '501-1000人', '1000人以上'
])

const errors = ref({
  companyName: false,
  companySize: false,
  region: false
})

const isSliding = ref(false)

// 是否显示公司规模选项
const showSizeOptions = ref(false)

// 选择公司规模
const selectSize = (size) => {
  formData.value.companySize = size
  showSizeOptions.value = false
}

const handleNext = () => {
  errors.value.companyName = formData.value.companyName === ''
  errors.value.companySize = formData.value.companySize === ''
  errors.value.region = formData.value.region === ''

  if (errors.value.companyName || errors.value.companySize || errors.value.region) {
    ElMessage.error('请输入完整信息')
    return
  }

  isSliding.value = true
  setTimeout(() => {
    userStore.registerInfo[0] = {
      companyName: formData.value.companyName,
      companySize: formData.value.companySize,
      region: formData.value.region
    }
    router.push('/users/registerCompany/contactInfo')
  }, 500)
}
</script>

<style scoped>

.back-arrow {
  position: absolute;
  top: 50%;
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

.city-input-container {
  position: relative;
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
input.form-input:not(:placeholder-shown)+label,
select.form-input:not([value=""]) + label {
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
  margin-right: 10px;
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

.city-suggestions {
  position: absolute;
  width: 100%;
  max-height: 200px;
  overflow-y: auto;
  background-color: white;
  border: 1px solid #dadce0;
  border-radius: 4px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  z-index: 10;
  margin-top: 4px;
}

.city-suggestion-item {
  padding: 10px 12px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.city-suggestion-item:hover {
  background-color: #f8f9fa;
  color: #FF7427;
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

.size-suggestions {
  position: absolute;
  width: 100%;
  max-height: 200px;
  overflow-y: auto;
  background-color: white;
  border: 1px solid #dadce0;
  border-radius: 4px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  z-index: 10;
  margin-top: 4px;
}

.size-suggestion-item {
  padding: 10px 12px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.size-suggestion-item:hover {
  background-color: #f8f9fa;
  color: #FF7427;
}

.company-size-container input {
  cursor: pointer;
  background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23FF7427' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
  background-repeat: no-repeat;
  background-position: right 12px center;
  background-size: 16px;
  padding-right: 36px;
}

.company-size-container select {
  appearance: none;
  background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23FF7427' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
  background-repeat: no-repeat;
  background-position: right 12px center;
  background-size: 16px;
  padding-right: 36px;
  cursor: pointer;
}

.company-size-container select option {
  padding: 10px 12px;
  cursor: pointer;
}

.company-size-container select option:hover {
  background-color: #f8f9fa;
  color: #FF7427;
}

/* For Firefox */
.company-size-container select option:checked,
.company-size-container select option:focus {
  background-color: #f8f9fa;
  color: #FF7427;
}

/* This ensures the same style across different browsers */
@media screen and (-webkit-min-device-pixel-ratio:0) {
  .company-size-container select {
    color-scheme: light;
  }

  .company-size-container select:focus {
    border-color: #FF7427;
  }
}

</style>
