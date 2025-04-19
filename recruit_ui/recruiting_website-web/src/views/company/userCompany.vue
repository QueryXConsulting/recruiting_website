<template>
  <div class="users-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item>
          <el-input v-model="searchForm.keyword" placeholder="搜索用户名" clearable @keyup.enter="handleSearch"
            class="search-input">
            <template #prefix>
              <el-icon>
                <Search />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch" class="search-button">搜索</el-button>
          <el-button type="success" :icon="Plus" @click="handleAdd" class="add-button">新增用户</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 用户列表 -->
    <div class="table-container">
      <el-table :data="userList" style="width: 100%" v-loading="loading" :border="false" stripe highlight-current-row
        class="custom-table">
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="userName" label="用户名" align="center" />
        <el-table-column prop="userAvatar" label="头像" align="center">
          <template #default="scope">
            <el-avatar :size="40" :src="scope.row.userAvatar">
              <img :src="scope.row.userAvatar" />
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="userEmail" label="邮箱" align="center" width="170" />
        <el-table-column prop="userPhone" label="手机号" align="center" />
        <el-table-column prop="userRole" label="角色" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.userRole ? 'primary' : 'info'">
              {{ scope.row.roleName || scope.row.userRole || '未分配角色' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="userRegisterTime" label="注册时间" align="center">
          <template #default="scope">
            {{ formatDate(scope.row.userRegisterTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="userStatus" label="用户状态" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.userStatus === '0' ? 'success' : 'danger'">
              {{ scope.row.userStatus === '0' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="scope">
            <div class="operation-buttons">
              <el-button size="small" type="primary" :icon="Edit" @click="handleEdit(scope.row)" class="edit-button">
                编辑
              </el-button>
              <el-button size="small" type="danger" :icon="Delete" @click="handleDelete(scope.row)"
                class="delete-button">
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next" :total="total" @size-change="handleSizeChange"
        @current-change="handleCurrentChange" />
    </div>

    <!-- 编辑用户对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="userFormRef" :model="userForm" :rules="rules" label-width="100px">
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="userForm.userName" />
        </el-form-item>
        <el-form-item label="邮箱" prop="userEmail">
          <el-input v-model="userForm.userEmail" />
        </el-form-item>
        <el-form-item label="手机号" prop="userPhone">
          <el-input v-model="userForm.userPhone" />
        </el-form-item>
        <el-form-item label="角色" prop="userRole">
          <el-select v-model="userForm.userRole" placeholder="请选择角色(未选择则不修改)">
            <el-option v-for="role in roleList" :key="role.roleId" :label="role.roleName"
              :value="role.roleId.toString()" />
          </el-select>
        </el-form-item>
        <el-form-item label="密码" prop="userPassword">
          <el-input v-model="userForm.userPassword" type="password" show-password placeholder="未输入则不修改" />
        </el-form-item>
        <el-form-item label="头像" prop="userAvatar">
          <el-upload class="avatar-uploader" :auto-upload="false" :show-file-list="false"
            :on-change="handleAvatarChange" accept=".jpg,.jpeg,.png">
            <img v-if="userForm.userAvatar" :src="userForm.userAvatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon">
              <Plus />
            </el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="用户状态" prop="userStatus">
          <el-radio-group v-model="userForm.userStatus">
            <el-radio value="0">启用</el-radio>
            <el-radio value="1">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Search, Edit, Delete, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { companyUserList, companyRoleList, updateUserCompany, addUserCompany, deleteUserCompany } from '@/api/company/companyApi'

// 搜索表单
const searchForm = ref({
  keyword: ''
})

// 用户列表数据
const userList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 编辑表单
const dialogVisible = ref(false)
const dialogTitle = ref('')
const userFormRef = ref(null)
const userForm = ref({
  userId: '',
  userName: '',
  userEmail: '',
  userPhone: '',
  userPassword: '',
  userAvatar: '',
  userRole: '',
  userStatus: '0'
})

// 表单验证规则
const rules = {
  userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  userEmail: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  userPhone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],

  userStatus: [
    { required: true, message: '请选择用户状态', trigger: 'change' }
  ],
}

// 格式化日期
const formatDate = (date) => {
  return new Date(date).toLocaleDateString()
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchUserList()
}

// 获取用户列表
const fetchUserList = async () => {
  loading.value = true
  try {
    const res = await companyUserList(
      currentPage.value,
      pageSize.value,
      searchForm.value.keyword
    )
    userList.value = res.content?.records
    total.value = res.content?.total
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  }
  loading.value = false
}

// 添加一个变量来存储原始数据
const originalUserData = ref(null)

// 修改编辑用户方法
const handleEdit = (row) => {
  dialogTitle.value = '编辑用户'
  const { userPassword, ...rest } = row
  // 保存原始数据
  originalUserData.value = { ...rest }
  userForm.value = {
    userId: row.userId,
    ...rest,
    userPassword: '',
    userRole: row.roleId?.toString() || '',
  }
  dialogVisible.value = true
}

// 删除用户
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const result = await deleteUserCompany(row.userId)
      if (result.code === 200) {
        ElMessage.success('删除成功')
        fetchUserList()
      } else {
        ElMessage.error(result.message || '删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 修改提交表单方法
const submitForm = async () => {
  if (!userFormRef.value) return
  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const formData = new FormData()
        const { userAvatar, ...userData } = userForm.value

        let submitData = {
          userId: userData.userId,
          userName: userData.userName,
          userStatus: userData.userStatus,
        }


        if (userData.userRole && userData.userRole.trim() !== '') {
          submitData.userRole = userData.userRole
        }

        if (!originalUserData.value || userData.userEmail !== originalUserData.value.userEmail) {
          submitData.userEmail = userData.userEmail
        }
        if (!originalUserData.value || userData.userPhone !== originalUserData.value.userPhone) {
          submitData.userPhone = userData.userPhone
        }

        if (userData.userPassword) {
          submitData.userPassword = userData.userPassword
        }

        const dtoJson = JSON.stringify(submitData)
        formData.append('dtoJson', dtoJson)

        if (userAvatar && userAvatar.startsWith('data:')) {
          const base64Data = userAvatar.split(',')[1]
          const blob = atob(base64Data)
          const array = []
          for (let i = 0; i < blob.length; i++) {
            array.push(blob.charCodeAt(i))
          }
          const file = new File([new Uint8Array(array)], 'avatar.png', { type: 'image/png' })
          formData.append('userAvatar', file)
        }
        let result;

        if (userForm.value.userId) {
           result = await updateUserCompany(formData)
        } else {
          result = await addUserCompany(formData)
        }
        if (result.code === 200) {
          ElMessage.success('保存成功')
          dialogVisible.value = false
          fetchUserList()
        } else if (result.code === 452) {
          ElMessage.error('手机号已存在')
        } else if (result.code === 453) {
          ElMessage.error('邮箱已存在')
        }
      } catch (error) {
        ElMessage.error('保存失败')
      }
    }
  })
}

// 分页相关
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchUserList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchUserList()
}

// 修改头像上传相关方法
const handleAvatarChange = (file) => {
  const isJPG = file.raw.type === 'image/jpeg' || file.raw.type === 'image/png'
  const isLt2M = file.raw.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('头像只能是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!')
    return false
  }

  // 创建 FileReader 对象来读取文件
  const reader = new FileReader()
  reader.readAsDataURL(file.raw)
  reader.onload = (e) => {
    userForm.value.userAvatar = e.target.result
  }
}

// 角色列表
const roleList = ref([])

// 获取角色列表
const fetchRoleList = async () => {
  try {
    const res = await companyRoleList()
    roleList.value = res.content
  } catch (error) {
    ElMessage.error('获取角色列表失败')
  }
}

// 新增用户
const handleAdd = () => {
  dialogTitle.value = '新增用户'
  userForm.value = {
    userId: '',
    userName: '',
    userEmail: '',
    userPhone: '',
    userPassword: '',
    userAvatar: '',
    userRole: '',
    userStatus: '0'
  }
  dialogVisible.value = true
}

onMounted(() => {
  fetchUserList()
  fetchRoleList()
})
</script>

<style scoped>
.users-container {
  padding: 24px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 60px);
}

.search-bar {
  margin-bottom: 24px;
  padding: 24px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
}

.search-form {
  display: flex;
  align-items: center;
}

.search-input {
  width: 200px;
}

.search-button {
  margin-left: 16px;
  background-color: #1890ff;
  border: none;
  padding: 0 24px;
  height: 36px;
  transition: all 0.2s;
}

.search-button:hover {
  background-color: #40a9ff;
}

.table-container {
  background-color: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
}

.custom-table {
  --el-table-header-bg-color: #fafafa;
  --el-table-border-color: #f0f0f0;
  --el-table-header-text-color: #262626;
}

:deep(.el-table th) {
  font-weight: 600;
  height: 50px;
  font-size: 14px;
}

:deep(.el-table td) {
  height: 60px;
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background-color: #fafafa;
}

.operation-buttons {
  display: flex;
  gap: 12px;
}

.edit-button,
.delete-button {
  padding: 8px 16px;
  border-radius: 4px;
  transition: all 0.2s;
}

.edit-button {
  background-color: #1890ff;
  border: none;
}

.edit-button:hover {
  background-color: #40a9ff;
}

.delete-button {
  background-color: #ff4d4f;
  border: none;
}

.delete-button:hover {
  background-color: #ff7875;
}

.pagination {
  margin-top: 24px;
  padding: 24px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
  display: flex;
  justify-content: center;
}

:deep(.el-pagination) {
  --el-pagination-button-bg-color: #fff;
  --el-pagination-hover-color: #1890ff;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.avatar-uploader {
  text-align: center;
}

.avatar-uploader .avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  border-radius: 50%;
  line-height: 100px;
}

.box-card {
  box-shadow: 0 4px 18px 0 rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.box-card:hover {
  box-shadow: 0 6px 24px 0 rgba(0, 0, 0, 0.18);
  transform: translateY(-2px);
}

.search-bar,
.table-container,
.pagination {
  box-shadow: 0 4px 18px 0 rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.search-bar:hover,
.table-container:hover,
.pagination:hover {
  box-shadow: 0 6px 24px 0 rgba(0, 0, 0, 0.18);
}

.add-button {
  margin-left: 16px;
  background-color: #67c23a;
  border: none;
  padding: 0 24px;
  height: 36px;
  transition: all 0.2s;
}

.add-button:hover {
  background-color: #85ce61;
}
</style>
