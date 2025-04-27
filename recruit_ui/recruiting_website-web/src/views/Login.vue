<template>
  <div class="login-container">
    <div class="login-box">
      <div class="back-arrow">
        <el-link :underline="false" href="/" type="info">
          <el-icon class="arrow-icon">
            <ArrowLeft />
          </el-icon>
        </el-link>
      </div>
      <div class="login-form">
        <!-- <img src="/public/logo.png" alt="问呗" class="logo-img" style="height: 50px; display: block; margin: 0 auto;"> -->
        <el-form :model="loginForm" :rules="rules" ref="formRef" class="form-content">
          <el-form-item prop="username">
            <el-input v-model="loginForm.username" placeholder="请输入手机号或邮箱  " size="large" maxlength="20">
              <template #prefix>
                <el-icon class="input-icon">
                  <User />
                </el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="userPassword">
            <el-input v-model="loginForm.userPassword" type="password" placeholder="请输入密码" show-password size="large"
              maxlength="20">
              <template #prefix>
                <el-icon class="input-icon">
                  <Lock />
                </el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleLogin" :loading="loading" class="login-button" size="large">
              登录
            </el-button>
          </el-form-item>
          <div class="bottom">
            <el-link :underline="false" href="/users/index" type="primary"
              class="link-text">忘记密码</el-link>
            <el-link :underline="false" href="/users/register" type="primary" class="link-text">注册</el-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock, ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import userStore from '@/store/user'
import { adminLogin, menuList } from '@/api/admin/adminApi'



const router = useRouter()
const formRef = ref()
const loading = ref(false)
const loginForm = reactive({
  username: '',
  userPassword: '',
})

const rules = {
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  userPassword: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

const store = userStore()

const perms = async (menus) => {
  menus.forEach((menu) => {
    if (menu.menuType == 'F' || menu.menuType == 'U') {
      store.permissions[menu.path] = menu.perms
    }
    if (menu.children.length > 0) {
      perms(menu.children)
    }
  })
}

// 登录
const handleLogin = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        const response = await adminLogin(loginForm)

        if (response.code === 200) {
          ElMessage.success('登录成功')
          store.token = response.content.token
          store.role = response.content?.userInfoVO?.userRole
          store.userInfo = response.content.userInfoVO

          const result = await menuList()
          await perms(result.content)

          if (store.role == null) {
            await store.getMenus()
            store.getUserInfo()
            router.push('/home')
            return
          }

          if (store.role != '5' && store.role != null) {
            await store.getMenus()
            router.push('/home/homePage')
            return
          }
          // 学生跳转
          router.push('/users/index')
        } else {
          ElMessage.error(response?.msg || '登录失败')
        }
      } catch (error) {

        ElMessage.error('登录失败：' + (error.message || '服务器错误'))
      } finally {
        loading.value = false
      }
    }
  })
}

</script>

<style scoped>
.login-container {
  user-select: none;
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
}

.login-box {
  position: relative;
  width: 460px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  animation: fadeIn 0.5s ease;
}

.login-form {
  width: 100%;
}

.login-title {
  text-align: center;
  margin-bottom: 40px;
  margin-bottom: 40px;
  font-weight: 600;
  font-size: 28px;
  color: var(--el-text-color-primary);
  letter-spacing: 1px;
}

.form-content {
  margin-top: 20px;
}

:deep(.el-input__wrapper) {
  background-color: #f5f7fa;
  box-shadow: none !important;
  border: 2px solid transparent;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  border-color: var(--el-primary-color-light);
  background-color: #fff;
}

.back-arrow {
  position: absolute;
  top: 20px;
  left: 20px;
}

.arrow-icon {
  font-size: 20px;
  color: var(--el-color-info);
  transition: transform 0.3s ease;
}

.arrow-icon:hover {
  transform: translateX(-5px);
  color: #FF8C00;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #FF8C00;
  background-color: #fff;
}

.input-icon {
  font-size: 18px;
  color: #909399;
}

:deep(.el-input__wrapper:hover) {
  border-color: #FFA940;
  background-color: #fff;
}

.login-button {
  background-color: #FF8C00;
  width: 100%;
  height: 44px;
  font-size: 16px;
  margin-top: 10px;
  border-radius: 6px;
  font-weight: 500;
  letter-spacing: 1px;
}

.login-button:hover {
  background-color: #FFEFD5;
  border-color: #FF8C00;
}


@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

:deep(.el-form-item) {
  margin-bottom: 25px;
}

:deep(.el-input__inner) {
  height: 44px;
}

.bottom {
  display: flex;
  justify-content: space-between;
}

.link-text {
  color: #FF8C00;
  transition: color 0.3s ease;
}

.link-text:hover {
  color: #FFA940;
}

</style>
