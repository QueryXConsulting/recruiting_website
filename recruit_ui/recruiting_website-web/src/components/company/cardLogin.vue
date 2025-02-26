<template>
  <div class="change-password-dialog">
    <el-dialog v-model="dialogVisible" title="绑定用户信息" :close-on-click-modal="false" :close-on-press-escape="false"
      :show-close="false" width="500px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号码" />
        </el-form-item>
        <el-form-item label="邮箱地址" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱地址" />
        </el-form-item>
        <el-form-item label="密码" prop="confirmPassword">
          <el-input v-model="formData.confirmPassword" type="password" placeholder="请再次输入新密码" />
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
import { updateUserCompany } from '@/api/company/companyApi'
import userStore from '@/store/user'


const dialogVisible = ref(true)
const formRef = ref(null)

const formData = reactive({
  confirmPassword: '',
  phone: '',
  email: ''
})

// 手机号验证函数
const validatePhone = (rule, value, callback) => {
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!value) {
    callback(new Error('请输入手机号码'))
  } else if (!phoneRegex.test(value)) {
    callback(new Error('请输入正确的手机号码'))
  } else {
    callback()
  }
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
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { validator: validatePhone, trigger: 'blur' }
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
        const dtoJson = JSON.stringify({
          userPassword: formData.confirmPassword,
          userPhone: formData.phone,
          userEmail: formData.email
        })
        formDataObj.append('dtoJson', dtoJson)

        let result = await updateUserCompany(formDataObj)
        if (result.code == 200) {
          dialogVisible.value = false
          userStore().userInfo.isFirstLogin = 1
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
</style>
