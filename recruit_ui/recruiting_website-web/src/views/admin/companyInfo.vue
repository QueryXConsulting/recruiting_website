<template>
  <div class="company-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>公司信息管理</span>
          <el-button type="primary" @click="handleAdd">新增公司</el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="公司名称">
          <el-input v-model="queryParams.companyInfoName" placeholder="请输入公司名称" clearable />
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="queryParams.companyInfoReview" clearable placeholder="请选择审核状态" style="width: 100px">
            <el-option label="待审核" value="0" />
            <el-option label="审核通过" value="1" />
            <el-option label="打回修改" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="启用状态">
          <el-select v-model="queryParams.companyInfoStatus" clearable placeholder="请选择状态" style="width: 100px">
            <el-option label="启用" value="0" />
            <el-option label="禁用" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label=" ">
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格区域 -->
      <el-table :data="tableData" style="width: 100%">
        <el-table-column label="序号" width="60" align="center">
          <template #default="{ $index }">
            {{ calculateIndex($index) }}
          </template>
        </el-table-column>
        <el-table-column prop="companyInfoName" label="企业名" width="200" />
        <el-table-column prop="companyInfoUsername" label="邮箱账号" width="200" />
        <el-table-column prop="companyRegisterTime" label="注册时间" width="150" />
        <el-table-column prop="companyInfoReview" label="审核状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getReviewTagType(row.companyInfoReview)">
              {{ getReviewStatusText(row.companyInfoReview) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="companyInfoStatus" label="启用状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.companyInfoStatus === '0' ? 'success' : 'danger'">
              {{ row.companyInfoStatus === '0' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="enterpriseReview" label="企业资质审核状态" width="140" cell-class-name="enterprise-review-cell">
          <template #default="{ row }">
            <el-tag :type="getEnterpriseReviewTagType(row.enterpriseReview)">
              {{ getEnterpriseReviewStatusText(row.enterpriseReview) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)" v-if="row.companyInfoReview != null">编辑</el-button>
            <el-button type="primary" link @click="handlePreviewFiles(row)"
              v-if="row.enterpriseReview != null">查看资质</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页区域 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]" :total="total" layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>

      <!-- 新增/编辑对话框 -->
      <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px" :close-on-click-modal="false" top="5vh">
        <el-scrollbar height="calc(90vh - 200px)" class="dialog-scrollbar">
          <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" class="company-form">
            <el-form-item label="企业名" prop="companyInfoName">
              <el-input v-model="form.companyInfoName" placeholder="请输入企业名" />
            </el-form-item>

            <el-form-item label="邮箱账号" prop="companyInfoUsername">
              <el-input v-model="form.companyInfoUsername" placeholder="请输入账号" />
            </el-form-item>
            <el-form-item label="密码" prop="companyInfoPassword">
              <el-input v-model="form.companyInfoPassword" type="password" placeholder="请输入密码" show-password />
            </el-form-item>
            <el-form-item label="公司LOGO">
              <el-upload class="logo-uploader" action="" :show-file-list="false" ref="uploadRef" :multiple="false"
                :auto-upload="false" :on-change="handleLogoChange" :before-upload="beforeLogoUpload"
                accept="image/png,image/jpg,image/jpeg">
                <img v-if="form.companyLogo" :src="form.companyLogo" class="logo-preview-img" />
                <div v-else class="upload-placeholder">
                  <el-icon>
                    <Plus />
                  </el-icon>
                  <span>点击上传LOGO</span>
                </div>
              </el-upload>
            </el-form-item>
            <el-form-item label="公司介绍" prop="companyInfoProfile">
              <el-input v-model="form.companyInfoProfile" type="textarea" :rows="4" placeholder="请输入公司介绍" />
            </el-form-item>
            <el-form-item label="公司经营范围" prop="companyInfoScope">
              <el-input v-model="form.companyInfoScope" type="textarea" :rows="4" placeholder="请输入公司经营范围" />
            </el-form-item>
            <el-form-item label="审核状态" prop="companyInfoReview">
              <el-select v-model="form.companyInfoReview" placeholder="请选择审核状态">
                <el-option label="待审核" value="0" />
                <el-option label="审核通过" value="1" />
                <el-option label="打回修改" value="2" />
              </el-select>
            </el-form-item>
            <el-form-item label="资质审核状态" prop="enterpriseReview">
              <el-select v-model="form.enterpriseReview" placeholder="请选择资质审核状态">
                <el-option label="未提交" value="" />
                <el-option label="待审核" value="0" />
                <el-option label="审核通过" value="1" />
                <el-option label="打回修改" value="2" />
              </el-select>
            </el-form-item>
            <el-form-item label="启用状态" prop="companyInfoStatus">
              <el-select v-model="form.companyInfoStatus" placeholder="请选择启用状态">
                <el-option label="启用" value="0" />
                <el-option label="禁用" value="1" />
              </el-select>
            </el-form-item>
          </el-form>
        </el-scrollbar>
        <template #footer>
          <span class="dialog-footer">
            <el-button type="primary" @click="submitForm">确定</el-button>
            <el-button @click="dialogVisible = false">取消</el-button>
          </span>
        </template>
      </el-dialog>


      <el-dialog v-model="previewVisible" :title="currentFile?.name" width="80%" :before-close="handleClosePreview"
        class="pdf-preview-dialog" :destroy-on-close="true">
        <div class="pdf-preview-container">
          <embed v-if="previewUrl" :src="previewUrl + '#toolbar=0&navpanes=0&scrollbar=0'" type="application/pdf"
            width="100%" height="100%" class="pdf-preview-embed" />
        </div>
      </el-dialog>


      <el-dialog v-model="filesListVisible" title="企业资质文件" width="500px">
        <div class="pdf-list" v-if="qualificationFiles.length">
          <div v-for="(file, index) in qualificationFiles" :key="index" class="pdf-item" @click="previewPdf(file)">
            <div class="pdf-content">
              <el-icon class="pdf-icon">
                <Document />
              </el-icon>
              <span class="pdf-name">{{ file.name }}</span>
            </div>
          </div>
        </div>
        <div v-else class="no-files">暂无资质文件</div>
      </el-dialog>

    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Document } from '@element-plus/icons-vue'
import { companyList, companyUpdate, companyEnterpriseFiles } from '@/api/admin/adminApi'

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  companyInfoName: '',
  companyInfoReview: '',
  companyInfoStatus: ''
})

const total = ref(0)
const tableData = ref([])

// 查询列表数据
const handleQuery = async () => {
  try {
    const { content } = await companyList(
      queryParams.pageNum,
      queryParams.pageSize,
      queryParams.companyInfoStatus,
      queryParams.companyInfoReview,
      queryParams.companyInfoName
    )

    tableData.value = content?.records || []
    total.value = content?.total || 0

  } catch (error) {
    ElMessage.error('获取公司列表失败')
    tableData.value = []
    total.value = 0
  }
}

// 重置查询
const resetQuery = () => {
  queryParams.pageNum = 1
  queryParams.companyInfoName = ''
  queryParams.companyInfoReview = ''
  queryParams.companyInfoStatus = ''
  handleQuery()
}

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()

// 表单数据
const form = reactive({
  companyInfoId: '',
  companyInfoName: '',
  companyInfoUsername: '',
  companyInfoPassword: '',
  companyInfoProfile: '',
  companyInfoScope: '',
  companyInfoReview: '',
  companyInfoStatus: '',
  companyLogo: '',
  enterpriseReview: '',
  enterpriseFile: []
})

// 表单校验规则
const rules = {
  companyInfoName: [
    { required: true, message: '请输入企业名', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  companyInfoUsername: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 4, max: 50, message: '长度在 4 到 50 个字符', trigger: 'blur' },
    {
      pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
      message: '请输入正确的邮箱格式',
      trigger: 'blur'
    }
  ],
  companyInfoProfile: [
    { required: true, message: '请输入公司介绍', trigger: 'blur' }
  ],
  companyInfoScope: [
    { required: true, message: '请输入公司经营范围', trigger: 'blur' }
  ],
  companyInfoReview: [
    { required: true, message: '请选择审核状态', trigger: 'change' }
  ],
  companyInfoStatus: [
    { required: true, message: '请选择启用状态', trigger: 'change' }
  ],
  enterpriseReview: [
    { required: true, message: '请选择企业资质审核状态', trigger: 'change' }
  ]
}

// 新增公司
const handleAdd = () => {
  dialogTitle.value = '新增公司'
  dialogVisible.value = true
  nextTick(() => {
    formRef.value?.resetFields()
    Object.keys(form).forEach(key => {
      form[key] = key === 'enterpriseReview' ? '' : ''
    })
  })
}

// 编辑公司
const handleEdit = (row) => {
  dialogTitle.value = '编辑公司'
  dialogVisible.value = true

  nextTick(() => {
    formRef.value?.resetFields()
    const formData = { ...row }
    Object.assign(form, formData)
    form.enterpriseReview = form.enterpriseReview || ''
  })
}

const handleSizeChange = (val) => {
  queryParams.pageSize = val
  handleQuery()
}

const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  handleQuery()
}

const getReviewStatusText = (status) => {
  const statusMap = {
    '0': '待审核',
    '1': '审核通过',
    '2': '打回修改'
  }
  return statusMap[status] || '待提交'
}

const getReviewTagType = (status) => {
  const typeMap = {
    '0': 'warning',
    '1': 'success',
    '2': 'danger'
  }
  return typeMap[status] || 'info'
}

// 添加上传相关方法
const uploadRef = ref(null)
const logoFile = ref(null)

const beforeLogoUpload = (file) => {
  const isImage = ['image/jpeg', 'image/png', 'image/jpg'].includes(file.type)
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传JPG/PNG格式图片!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过2MB!')
    return false
  }
  return true
}

const handleLogoChange = (file) => {
  if (beforeLogoUpload(file.raw)) {
    logoFile.value = file.raw

    const reader = new FileReader()
    reader.onload = (e) => {
      form.companyLogo = e.target.result
    }
    reader.readAsDataURL(file.raw)
  }
}

// 修改提交表单方法
const submitForm = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const formData = new FormData()

        if (logoFile.value) {
          formData.append('applyFiles', logoFile.value)
          formData.append('companyLogo', form.companyLogo)
        }

        Object.keys(form).forEach(key => {
          if (key === 'companyInfoUsername' && form[key] === tableData.value.find(item => item.companyInfoId === form.companyInfoId)?.companyInfoUsername) {
            return
          }

          if (form[key] !== '' &&
            key !== 'companyLogo' &&
            key !== 'enterpriseFile') {
            formData.append(key, form[key])
          }
        })

        await companyUpdate(formData)
        ElMessage.success('保存成功')
        dialogVisible.value = false
        handleQuery()

        logoFile.value = null
      } catch (error) {
        console.error('保存失败:', error)
        ElMessage.error(error.message || '保存失败')
      }
    }
  })
}

const calculateIndex = ($index) => {
  return (queryParams.pageNum - 1) * queryParams.pageSize + $index + 1
}


const previewVisible = ref(false)
const previewUrl = ref('')
const currentFile = ref(null)


const previewPdf = (file) => {
  currentFile.value = file
  previewUrl.value = file.url
  previewVisible.value = true
}

const handleClosePreview = () => {
  previewVisible.value = false
  previewUrl.value = ''
  currentFile.value = null
}


const filesListVisible = ref(false)
const qualificationFiles = ref([])


const handlePreviewFiles = async (row) => {
  try {
    const response = await companyEnterpriseFiles(row.companyInfoId)
    if (response.content && Object.keys(response.content).length > 0) {
      qualificationFiles.value = Object.entries(response.content).map(([name, content]) => {
        if (!content) {
          console.warn('文件内容为空:', name);
          return null;
        }

        try {

          const binaryString = atob(content);
          const bytes = new Uint8Array(binaryString.length);
          for (let i = 0; i < binaryString.length; i++) {
            bytes[i] = binaryString.charCodeAt(i);
          }

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

      if (qualificationFiles.value.length === 0) {
        ElMessage.warning('没有可显示的文件');
        return;
      }

      filesListVisible.value = true;
    } else {
      ElMessage.info('暂无资质文件');
      qualificationFiles.value = [];
    }
  } catch (error) {
    console.error('获取资质文件失败:', error);
    ElMessage.error('获取资质文件失败');
    qualificationFiles.value = [];
  }
}

const getEnterpriseReviewStatusText = (status) => {
  const statusMap = {
    '0': '待审核',
    '1': '审核通过',
    '2': '打回修改',
    '': '未提交'
  }
  return statusMap[status] || '待提交'
}

const getEnterpriseReviewTagType = (status) => {
  const typeMap = {
    '0': 'warning',
    '1': 'success',
    '2': 'danger',
    '': 'info'
  }
  return typeMap[status] || 'info'
}

handleQuery()


onBeforeUnmount(() => {
  qualificationFiles.value.forEach(file => {
    if (file.url && file.url.startsWith('blob:')) {
      URL.revokeObjectURL(file.url);
    }
  });
})
</script>

<style scoped lang="scss">
.company-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;

  :deep(.el-form-item) {
    margin-bottom: 0;

    .el-form-item__label {
      min-width: 70px;
    }
  }
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}


:deep(.el-table--default) {
  --el-table-border-color: transparent;
}

:deep(.el-table td.el-table__cell),
:deep(.el-table th.el-table__cell.is-leaf) {
  border-bottom: 1px solid var(--el-table-border-color);
}

:deep(.el-table--default .el-table__cell) {
  border-right: none;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.logo-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration);
}

.logo-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.upload-placeholder {
  width: 100px;
  height: 100px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--el-text-color-secondary);
}

.logo-preview-img {
  width: 140px;
  height: 140px;
  object-fit: cover;
}


.dialog-scrollbar {
  :deep(.el-scrollbar__wrap) {
    padding: 20px;
  }

  :deep(.el-scrollbar__bar) {
    &.is-horizontal {
      display: none;
    }

    &.is-vertical {
      width: 6px;
      right: 2px;

      .el-scrollbar__thumb {
        background-color: var(--el-text-color-secondary);
        opacity: 0.15;
        border-radius: 6px;

        &:hover {
          opacity: 0.3;
        }
      }
    }
  }
}

:deep(.el-dialog__body) {
  padding: 0;
}


:deep(.el-dialog) {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 12px 32px 4px rgba(0, 0, 0, .04), 0 8px 20px rgba(0, 0, 0, .08);

  .el-dialog__header {
    margin: 0;
    padding: 20px 24px;
    border-bottom: 1px solid var(--el-border-color-lighter);
    background-color: var(--el-bg-color);

    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }

    .el-dialog__headerbtn {
      top: 20px;
      right: 20px;

      .el-dialog__close {
        font-size: 18px;
        color: var(--el-text-color-secondary);

        &:hover {
          color: var(--el-color-primary);
          transform: rotate(90deg);
          transition: all 0.3s ease;
        }
      }
    }
  }

  .el-dialog__body {
    padding: 0;
  }

  .el-dialog__footer {
    margin: 0;
    padding: 20px 24px;
    border-top: 1px solid var(--el-border-color-lighter);
    background-color: var(--el-bg-color);
  }
}


.company-form {
  .form-content {
    display: flex;
    gap: 16px;

    .form-left {
      flex: 1;
      min-width: 0;
    }

    .form-right {
      width: 180px;
      flex-shrink: 0;
    }
  }

  .form-section {
    background-color: var(--el-bg-color);
    border-radius: 12px;
    padding: 20px;
    margin-bottom: 24px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, .04);
    border: 1px solid var(--el-border-color-lighter);
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 2px 12px rgba(0, 0, 0, .08);
    }

    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: var(--el-text-color-primary);
      margin-bottom: 24px;
      padding-left: 12px;
      border-left: 4px solid var(--el-color-primary);
      display: flex;
      align-items: center;

      &::after {
        content: '';
        flex: 1;
        height: 1px;
        background: var(--el-border-color-lighter);
        margin-left: 12px;
      }
    }
  }
}


.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;

  :deep(.el-button) {
    padding: 8px 24px;
    border-radius: 6px;
    font-weight: 500;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 2px 8px rgba(0, 0, 0, .1);
    }

    &:active {
      transform: translateY(0);
    }
  }
}


:deep(.el-form-item) {
  margin-bottom: 24px;

  &:last-child {
    margin-bottom: 0;
  }

  .el-input__wrapper,
  .el-textarea__inner,
  .el-select .el-input__wrapper {
    box-shadow: 0 0 0 1px var(--el-border-color) inset;
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 0 0 1px var(--el-border-color-darker) inset;
    }

    &.is-focus {
      box-shadow: 0 0 0 1px var(--el-color-primary) inset;
    }
  }
}


.logo-uploader {
  :deep(.el-upload) {
    width: 140px;
    height: 140px;
    border: 2px dashed var(--el-border-color);
    border-radius: 12px;
    transition: all 0.3s ease;
    background-color: var(--el-fill-color-lighter);

    &:hover {
      border-color: var(--el-color-primary);
      background-color: var(--el-fill-color);
    }
  }

  .upload-placeholder {
    .el-icon {
      font-size: 28px;
      margin-bottom: 8px;
      color: var(--el-text-color-secondary);
    }

    span {
      font-size: 14px;
      color: var(--el-text-color-secondary);
    }
  }
}

.logo-preview-img {
  border-radius: 8px;
  transition: all 0.3s ease;

  &:hover {
    transform: scale(1.02);
  }
}

.upload-tip {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-top: 12px;
  text-align: center;
  line-height: 1.5;
}



.pdf-preview-container {
  width: 100%;
  height: calc(90vh - 120px);
  background-color: #f0f2f5;
  border-radius: 8px;
  padding: 16px;
  box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.05);

  .pdf-preview-embed {
    border: none;
    width: 100%;
    height: 100%;
    display: block;
    border-radius: 4px;
    background-color: #fff;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }
}

.pdf-list {
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
  line-height: 40px;
}

.pdf-item:hover .pdf-name {
  color: #409eff;
}

.no-files {
  text-align: center;
  color: #909399;
  padding: 20px 0;
}


:deep(.warning-cell) {
  color: #e6a23c;
}

:deep(.success-cell) {
  color: #67c23a;
}

:deep(.danger-cell) {
  color: #f56c6c;
}
</style>
