<template>
  <div class="job-manage">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="职位名称">
          <el-input v-model="searchForm.jobPosition" placeholder="请输入职位名称" clearable class="custom-input"></el-input>
        </el-form-item>
        <el-form-item label="公司名称">
          <el-input v-model="searchForm.companyName" placeholder="请输入公司名称" clearable class="custom-input"></el-input>
        </el-form-item>
        <el-form-item label="工作分类">
          <el-select v-model="searchForm.jobCategory" placeholder="请选择或输入" clearable filterable allow-create
            default-first-option class="custom-select">
            <el-option v-for="item in jobCategoryOptions" :key="item.value" :label="item.label"
              :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="求职类型">
          <el-select v-model="searchForm.jobNature" placeholder="请选择" clearable class="custom-select">
            <el-option v-for="item in jobNatureOptions" :key="item.value" :label="item.label"
              :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="职位状态">
          <el-select v-model="searchForm.jobStatus" placeholder="请选择" clearable class="custom-select">
            <el-option label="已发布" value="1"></el-option>
            <el-option label="已关闭" value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="searchForm.jobReview" placeholder="请选择" clearable class="custom-select">
            <el-option label="待审核" value="0"></el-option>
            <el-option label="已通过" value="1"></el-option>
            <el-option label="打回修改" value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item class="search-buttons">
          <el-button type="primary" :icon="Search" @click="handleSearch" class="search-btn">搜索</el-button>
          <el-button :icon="Refresh" @click="resetSearch" class="reset-btn">重置</el-button>
          <el-button type="success" :icon="Plus" @click="handleAdd" class="add-btn">新增职位</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading" :height="tableHeight" :border="false">
        <el-table-column prop="jobPosition" label="职位名称" min-width="140" align="center"></el-table-column>
        <el-table-column prop="companyName" label="公司名称" min-width="140" align="center"></el-table-column>
        <el-table-column prop="jobCategory" label="工作类别" min-width="100" align="center"></el-table-column>
        <el-table-column prop="jobSalary" label="工资" min-width="110" align="center"></el-table-column>
        <el-table-column prop="jobEducation" label="教育程度" min-width="100" align="center"></el-table-column>
        <el-table-column prop="jobExperience" label="工作经验" min-width="100" align="center"></el-table-column>
        <el-table-column prop="jobStatus" label="状态" min-width="90" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.jobStatus === '1' ? 'success' : 'warning'" size="small" effect="plain">
              {{ scope.row.jobStatus === '1' ? '已发布' : '已关闭' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="jobReview" label="审核状态" min-width="90" align="center">
          <template #default="scope">
            <el-tag :type="getAuditTagType(scope.row.jobReview)" size="small" effect="plain">
              {{ getAuditStatusText(scope.row.jobReview) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="160" fixed="right" align="center">
          <template #default="scope">
            <el-button size="small" type="primary" link :icon="Edit" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button size="small" type="danger" link :icon="Delete" @click="handleDelete(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination v-model:current-page="page.current" v-model:page-size="page.size" :page-sizes="[10, 20, 50, 100]"
          :total="page.total" background layout="total, sizes, prev, pager, next" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogType === 'add' ? '新增职位' : '编辑职位'" v-model="dialogVisible" width="65%" destroy-on-close
      :close-on-click-modal="false" top="5vh">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px" class="job-form">
        <div class="form-section">
          <el-row :gutter="30">
            <el-col :span="12">
              <el-form-item label="职位名称" prop="jobPosition">
                <el-input v-model="formData.jobPosition" placeholder="请输入职位名称"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="公司名称" prop="companyName">
                <el-select v-model="formData.companyName" placeholder="请选择公司" filterable remote
                  :remote-method="handleCompanySearch" :loading="companyLoading" @change="handleCompanyChange"
                  :disabled="dialogType === 'edit'" class="full-width">
                  <el-option v-for="item in companyOptions" :key="item.companyInfoId" :label="item.companyInfoName"
                    :value="item.companyInfoName" :data-company-id="item.companyInfoId">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="30">
            <el-col :span="12">
              <el-form-item label="工作地点" prop="jobArea">
                <el-input v-model="formData.jobArea" placeholder="请输入工作地点"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="工作分类" prop="jobCategory">
                <el-select v-model="formData.jobCategory" placeholder="请选择或输入工作分类" filterable allow-create
                  default-first-option class="full-width">
                  <el-option v-for="item in jobCategoryOptions" :key="item.value" :label="item.label"
                    :value="item.value"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="30">
            <el-col :span="12">
              <el-form-item label="求职类型" prop="jobNature">
                <el-select v-model="formData.jobNature" placeholder="请选择求职类型" class="full-width">
                  <el-option v-for="item in jobNatureOptions" :key="item.value" :label="item.label" :value="item.value">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="薪资" prop="jobSalary">
                <el-input v-model="formData.jobSalary" placeholder="请输入薪资范围"></el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="30">
            <el-col :span="12">
              <el-form-item label="审核状态" prop="jobReview">
                <el-select v-model="formData.jobReview" placeholder="请选择审核状态" class="full-width">
                  <el-option label="待审核" value="0"></el-option>
                  <el-option label="已通过" value="1"></el-option>
                  <el-option label="打回修改" value="2"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="职位状态" prop="jobStatus">
                <el-select v-model="formData.jobStatus" placeholder="请选择职位状态" class="full-width">
                  <el-option label="已发布" value="1"></el-option>
                  <el-option label="已关闭" value="0"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="30">
            <el-col :span="12">
              <el-form-item label="教育程度" prop="jobEducation">
                <el-select v-model="formData.jobEducation" placeholder="请选择学历" class="full-width">
                  <el-option label="高中" value="高中"></el-option>
                  <el-option label="大专" value="大专"></el-option>
                  <el-option label="本科" value="本科"></el-option>
                  <el-option label="硕士" value="硕士"></el-option>
                  <el-option label="博士" value="博士"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="工作经验" prop="jobExperience">
                <el-select v-model="formData.jobExperience" placeholder="请选择工作经验" class="full-width">
                  <el-option label="应届毕业生" value="应届毕业生"></el-option>
                  <el-option label="1年以下" value="1年以下"></el-option>
                  <el-option label="1-3年" value="1-3年"></el-option>
                  <el-option label="3-5年" value="3-5年"></el-option>
                  <el-option label="5-10年" value="5-10年"></el-option>
                  <el-option label="10年以上" value="10年以上"></el-option>
                  <el-option label="经验不限" value="经验不限"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="30">
            <el-col :span="12">
              <el-form-item label="联系人" prop="jobContact">
                <el-input v-model="formData.jobContact" placeholder="请输入联系人姓名"></el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="职位描述" prop="jobPositionDescribe">
            <el-input type="textarea" v-model="formData.jobPositionDescribe" :autosize="{ minRows: 4, maxRows: 8 }"
              placeholder="请详细描述职位要求、工作内容等信息"></el-input>
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="handleSubmit">确 定</el-button>
          <el-button @click="dialogVisible = false">取 消</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Refresh, Edit, Delete } from '@element-plus/icons-vue'
import { jobList, jobAdd, jobUpdate, jobDelete, categoryList, jobNatureList, companyList } from '@/api/admin/adminApi'


const jobCategoryOptions = ref([])
const jobNatureOptions = ref([])

// 加载状态
const loading = ref(false)


const searchForm = reactive({
  jobPosition: '',
  companyName: '',
  jobCategory: '',
  jobNature: '',
  jobStatus: '',
  jobReview: ''
})

const tableData = ref([])
const page = reactive({
  current: 1,
  size: 10,
  total: 0
})


const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)

// 表单数据
const formData = reactive({
  jobId: undefined,
  jobPosition: '',
  companyName: '',
  companyId: '',
  jobPositionDescribe: '',
  jobCategory: '',
  jobNature: '',
  jobArea: '',
  jobSalary: '',
  jobPersonNumber: 1,
  jobReview: '0',
  jobStatus: '1',
  jobEducation: '',
  jobExperience: '',
  jobTime: null,
  jobContact: ''
})


const rules = {
  jobPosition: [{ required: true, message: '请输入职位名称', trigger: 'blur' }],
  companyName: [{ required: true, message: '请输入公司名称', trigger: 'blur' }],
  jobPositionDescribe: [{ required: true, message: '请输入职位描述', trigger: 'blur' }],
  jobCategory: [{ required: true, message: '请选择工作分类', trigger: 'change' }],
  jobNature: [{ required: true, message: '请选择求职类型', trigger: 'change' }],
  jobArea: [{ required: true, message: '请输入工作地点', trigger: 'blur' }],
  jobSalary: [{ required: true, message: '请输入薪资', trigger: 'blur' }],
  jobStatus: [{ required: true, message: '请选择职位状态', trigger: 'change' }],
  jobEducation: [{ required: true, message: '请选择教育程度', trigger: 'change' }],
  jobExperience: [{ required: true, message: '请选择工作经验', trigger: 'change' }],
  jobContact: [{ required: true, message: '请输入联系人姓名', trigger: 'blur' }]
}

// 搜索方法
const handleSearch = () => {
  fetchData()
}

// 重置搜索
const resetSearch = () => {
  searchForm.jobPosition = ''
  searchForm.companyName = ''
  searchForm.jobCategory = ''
  searchForm.jobNature = ''
  searchForm.jobStatus = ''
  searchForm.jobReview = ''
  fetchData()
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const { jobPosition, companyName, jobCategory, jobNature, jobStatus, jobReview } = searchForm
    const res = await jobList(
      page.current,
      page.size,
      companyName,
      jobPosition,
      jobReview,
      jobStatus,
      jobCategory,
      jobNature,
    )

    if (res.code === 200) {
      tableData.value = res.content.records
      page.total = res.content.total
    } else {
      ElMessage.error(res.msg || '获取数据失败')
    }
  } catch (error) {
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const companyOptions = ref([])
const companyLoading = ref(false)


const handleCompanySearch = async (query) => {
  companyLoading.value = true
  try {
    const res = await companyList(1, 100, null, null, query)
    if (res.code === 200) {
      companyOptions.value = res.content.records
    } else {
      ElMessage.error(res.msg || '获取公司列表失败')
    }
  } catch (error) {
    ElMessage.error('获取公司列表失败')
  } finally {
    companyLoading.value = false
  }
}

// 新增
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  formData.jobId = undefined
  formData.jobPosition = ''
  formData.companyName = ''
  formData.companyId = ''
  formData.jobPositionDescribe = ''
  formData.jobCategory = ''
  formData.jobNature = ''
  formData.jobArea = ''
  formData.jobSalary = ''
  formData.jobPersonNumber = 1
  formData.jobReview = '0'
  formData.jobStatus = '1'
  formData.jobEducation = ''
  formData.jobExperience = ''
  formData.jobTime = null
  formData.jobContact = ''
}

// 编辑
const handleEdit = (row) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  // 重置表单数据，然后再赋值
  Object.keys(formData).forEach(key => {
    formData[key] = ''
  })
  // 使用解构赋值并确保所有字段都被正确复制
  Object.assign(formData, {
    jobId: row.jobId,
    jobPosition: row.jobPosition,
    companyName: row.companyName,
    companyId: row.companyId,
    jobPositionDescribe: row.jobPositionDescribe,
    jobCategory: row.jobCategory,
    jobNature: row.jobNature,
    jobArea: row.jobArea,
    jobSalary: row.jobSalary,
    jobPersonNumber: row.jobPersonNumber,
    jobReview: row.jobReview,
    jobStatus: row.jobStatus,
    jobEducation: row.jobEducation,
    jobExperience: row.jobExperience,
    jobTime: row.jobTime,
    jobContact: row.jobContact
  })
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该职位?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await jobDelete(row.jobId)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchData()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (dialogType.value === 'add') {
          res = await jobAdd(formData)
        } else {

          res = await jobUpdate(formData)
        }

        if (res.code === 200) {
          ElMessage.success(dialogType.value === 'add' ? '新增成功' : '编辑成功')
          dialogVisible.value = false
          fetchData()
        } else {
          ElMessage.error(res.msg || '操作失败')
        }
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

// 分页方法
const handleSizeChange = (val) => {
  page.size = val
  fetchData()
}

const handleCurrentChange = (val) => {
  page.current = val
  fetchData()
}

const getAuditStatusText = (status) => {
  const statusMap = {
    '0': '待审核',
    '1': '已通过',
    '2': '打回修改'
  }
  return statusMap[status] || '未知'
}

const getAuditTagType = (status) => {
  const typeMap = {
    '0': 'info',
    '1': 'success',
    '2': 'danger'
  }
  return typeMap[status] || ''
}

// 获取分类列表
const fetchCategories = async () => {
  try {
    const res = await categoryList(1, 100, '', 0)
    if (res.code === 200) {

      jobCategoryOptions.value = res.content.records.map(item => ({
        label: item.categoryName,
        value: item.categoryName
      }))

    } else {
      ElMessage.error(res.msg || '获取分类数据失败')
    }
  } catch (error) {
    ElMessage.error('获取分类数据失败')
  }
}

// 获取求职类型列表
const fetchJobNatures = async () => {
  try {
    const res = await jobNatureList(0)
    if (res.code === 200) {
      // 将返回的求职类型数据转换为选项格式
      jobNatureOptions.value = res.content.map(item => ({
        label: item.jobNatureName,
        value: item.jobNatureName
      }))
    } else {
      ElMessage.error(res.msg || '获取求职类型数据失败')
    }
  } catch (error) {
    ElMessage.error('获取求职类型数据失败')
  }
}

const handleCompanyChange = (value) => {
  const selectedCompany = companyOptions.value.find(item => item.companyInfoName === value)
  if (selectedCompany) {
    formData.companyId = selectedCompany.companyInfoId
  }
}

onMounted(() => {
  fetchData()
  fetchCategories()
  fetchJobNatures()
})

</script>

<style scoped lang="scss">
.job-manage {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);

  .search-card {
    margin-bottom: 20px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
    border-radius: 8px;

    :deep(.el-card__body) {
      padding: 24px 30px;
    }

    .search-form {
      display: flex;
      flex-wrap: wrap;
      gap: 16px;
      align-items: flex-start;

      .el-form-item {
        margin-bottom: 0;
        margin-right: 0;
        flex: 1;
        min-width: 220px;
        max-width: calc(25% - 12px);

        &:last-child {
          max-width: none;
          flex: 0 0 auto;
          margin-left: 0;
          padding-top: 4px;
        }

        &__label {
          font-weight: 500;
          color: #606266;
        }
      }

      .custom-input {
        width: 220px;
      }

      .custom-select {
        width: 160px;
      }

      :deep(.el-input__wrapper),
      :deep(.el-select .el-input__wrapper) {
        box-shadow: 0 0 0 1px #dcdfe6 inset;
        border-radius: 6px;
        padding: 0 12px;
        height: 36px;
        transition: all 0.3s;

        &:hover {
          box-shadow: 0 0 0 1px var(--el-color-primary-light-3) inset;
        }

        &.is-focus {
          box-shadow: 0 0 0 1px var(--el-color-primary) inset;
        }
      }

      .search-form-buttons {
        display: flex;
        gap: 8px;

        .el-button {
          padding: 8px 16px;
          height: 36px;

          .el-icon {
            margin-right: 4px;
          }

          &:hover {
            transform: translateY(-1px);
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
          }
        }
      }
    }
  }

  .table-card {
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
    display: flex;
    flex-direction: column;

    :deep(.el-card__body) {
      flex: 1;
      padding: 20px;
      display: flex;
      flex-direction: column;
      overflow: hidden;
    }

    :deep(.el-table) {
      flex: 1;

      // 移除表格线和背景色
      --el-table-border-color: transparent;
      --el-table-border: none;
      --el-table-header-bg-color: transparent;
      --el-table-row-hover-bg-color: #f5f7fa;

      // 表头样式
      th.el-table__cell {
        background-color: transparent !important;
        color: #606266;
        font-weight: 600;
        padding: 8px 0;
        border-bottom: 1px solid #ebeef5;
      }

      // 单元格样式
      td.el-table__cell {
        padding: 12px 0;
        border-bottom: 1px solid #ebeef5;
      }

      &::before {
        display: none;
      }
    }

    :deep(.el-tag) {
      border: none;
      padding: 0 12px;
    }

    // 按钮样式优化
    :deep(.el-button--link) {
      padding: 4px 8px;

      &:hover {
        background-color: #f5f7fa;
        border-radius: 4px;
      }
    }

    .pagination {
      margin-top: 16px;
      display: flex;
      justify-content: center;
    }
  }

  :deep(.el-button) {
    padding: 6px 12px;
    margin: 0 4px;
    border-radius: 4px;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }

    .el-icon {
      margin-right: 4px;
    }
  }
}

// 对话框样式优化
:deep(.el-dialog) {
  border-radius: 6px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);

  .el-dialog__header {
    margin: 0;
    padding: 15px 20px;
    border-bottom: 1px solid #f0f0f0;

    .el-dialog__title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }
  }

  .el-dialog__body {
    padding: 15px 0;
  }

  .el-dialog__footer {
    padding: 8px 20px 15px;
    border-top: 1px solid #f0f0f0;
  }
}

.job-form {
  .form-section {
    padding: 0 15px;

    .section-title {
      font-size: 15px;
      font-weight: 600;
      color: #303133;
      margin: 15px 0 12px;
      padding-left: 8px;
      border-left: 3px solid var(--el-color-primary);
      line-height: 1;

      &:first-child {
        margin-top: 0;
      }
    }

    .el-row {
      margin-bottom: 12px;
    }

    :deep(.el-form-item) {
      margin-bottom: 12px;

      .el-form-item__label {
        font-weight: 500;
        padding-right: 15px;
        line-height: 32px;
      }

      .el-input__wrapper,
      .el-textarea__wrapper {
        box-shadow: 0 0 0 1px #dcdfe6 inset;

        &:hover {
          box-shadow: 0 0 0 1px var(--el-color-primary-light-3) inset;
        }

        &.is-focus {
          box-shadow: 0 0 0 1px var(--el-color-primary) inset;
        }
      }

      .el-input__wrapper {
        height: 32px;
        line-height: 32px;
      }

      .el-select {
        width: 100%;
      }
    }

    :deep(.el-textarea__inner) {
      min-height: 80px !important;
    }
  }

  .full-width {
    width: 100%;
  }

  :deep(.el-input__wrapper),
  :deep(.el-textarea__wrapper),
  :deep(.el-select .el-input__wrapper) {
    box-shadow: 0 0 0 1px #dcdfe6 inset;
    padding: 0 15px;
    border-radius: 6px;
    transition: all 0.3s;
    margin: 8px 0;
    background-color: transparent;

    &:hover {
      box-shadow: 0 0 0 1px var(--el-color-primary-light-3) inset;
      transform: translateY(-1px);
    }

    &.is-focus {
      box-shadow: 0 0 0 1px var(--el-color-primary) inset;
      transform: translateY(-1px);
    }
  }

  :deep(.el-input__inner) {
    height: 45px;
    line-height: 45px;
  }

  .el-row {
    --el-row-gutter: 20px;
  }
}

.dialog-footer {
  padding-top: 15px;
  text-align: right;

  .el-button {
    padding: 8px 16px;
    margin-left: 8px;

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
  }
}

:deep(.el-form-item__content) {
  line-height: 32px;
}

:deep(.el-select .el-input__wrapper) {
  height: 32px;
  line-height: 32px;
}
</style>
