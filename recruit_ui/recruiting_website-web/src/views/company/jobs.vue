<template>
  <div class="jobs-container">
    <el-card class="jobs-card">
      <template #header>
        <div class="card-header">
          <h2>职位管理</h2>
          <el-button type="primary" @click="handleAddJob">
            <el-icon>
              <Plus />
            </el-icon>发布新职位
          </el-button>
        </div>
      </template>


      <div class="search-section">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="职位名称">
            <el-input v-model="searchForm.jobPosition" placeholder="请输入职位名称" clearable />
          </el-form-item>
          <el-form-item label="职位类别">
            <el-select v-model="searchForm.jobCategory" placeholder="请选择" clearable filterable remote
              :remote-method="getCategories" :loading="categoryLoading" style="width: 100px;">
              <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="审核状态">
            <el-select v-model="searchForm.jobReview" placeholder="请选择" clearable style="width: 100px;">
              <el-option label="待审核" value="0" />
              <el-option label="已通过" value="1" />
              <el-option label="打回修改" value="2" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 表格部分添加边框类名 -->
      <div class="table-section">
        <el-alert title="提示：点击职位名称可查看该职位的简历投递情况" type="info" :closable="false" show-icon style="margin-bottom: 16px;" />
        <el-table :data="jobList" style="width: 100%" v-loading="loading" border highlight-current-row
          class="custom-table">
          <el-table-column prop="jobPosition" label="职位名称" min-width="150" show-overflow-tooltip>
            <template #default="{ row }">
              <div class="position-cell">
                <el-tooltip placement="top" effect="light">
                  <span class="position-name" @click="handlePositionClick(row)" style="cursor: pointer;">
                    {{ row.jobPosition }}
                  </span>
                </el-tooltip>
                <el-tag size="small" :type="getStatusType(row.jobStatus)" effect="light">
                  {{ row.jobStatus === '0' ? '已停止' : '招聘中' }}
                </el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="jobSalary" label="薪资" width="110" align="center">
            <template #default="{ row }">
              <span class="salary-text">{{ row.jobSalary }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="jobCategory" label="类别" width="130" />
          <el-table-column prop="jobArea" label="地区" width="120" />
          <el-table-column prop="jobEducation" label="学历" width="100" />
          <el-table-column prop="jobExperience" label="经验" width="100" />
          <el-table-column prop="jobNature" label="类型" min-width="90" />
          <el-table-column prop="jobTime" label="发布时间" width="140">
            <template #default="{ row }">
              {{ formatDate(row.jobTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="jobReview" label="审核" width="100">
            <template #default="{ row }">
              <el-tag :type="getReviewType(row.jobReview)">
                {{ getReviewText(row.jobReview) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="240" fixed="right" align="center">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button type="primary" link @click="handleEdit(row)">
                  编辑
                </el-button>
                <el-divider direction="vertical" />
                <el-button type="primary" link @click="handleToggleStatus(row)" :disabled="row.jobReview !== '1'">
                  {{ row.jobStatus === '1' ? '停止招聘' : '开始招聘' }}
                </el-button>
                <el-divider direction="vertical" />
                <el-button type="danger" link @click="handleDelete(row)">
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 30, 50]"
          layout="total, sizes, prev, pager, next" :total="total" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '发布新职位' : '编辑职位'" width="60%"
      :close-on-click-modal="false">
      <el-form ref="jobFormRef" :model="jobForm" :rules="rules" label-width="100px" class="job-form">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="职位" prop="jobPosition" style="width: 100%;">
              <el-input v-model="jobForm.jobPosition" placeholder="请输入职位名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职位类别" prop="jobCategory">
              <el-select v-model="jobForm.jobCategory" placeholder="请选择职位类别" class="w-full" filterable remote
                :remote-method="getCategories" :loading="categoryLoading">
                <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工作性质" prop="jobNature">
              <el-select v-model="jobForm.jobNature" placeholder="请选择工作性质" class="w-full">
                <el-option label="全职" value="全职" />
                <el-option label="实习" value="实习" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="薪资范围" prop="jobSalary">
              <el-select v-model="jobForm.jobSalary" placeholder="请选择薪资范围">
                <el-option label="1k-2k" value="1k-2k"></el-option>
                <el-option label="3k-5k" value="3k-5k"></el-option>
                <el-option label="5k-8k" value="5k-8k"></el-option>
                <el-option label="8k-10k" value="8k-10k"></el-option>
                <el-option label="10k-15k" value="10k-15k"></el-option>
                <el-option label="15k-20k" value="15k-20k"></el-option>
                <el-option label="20k-30k" value="20k-30k"></el-option>
                <el-option label="30k以上" value="30k+"></el-option>
                <el-option label="面议" value="面议"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工作地区" prop="jobArea">
              <el-input v-model="jobForm.jobArea" placeholder="请输入工作地区" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系人" prop="jobContact">
              <el-input v-model="jobForm.jobContact" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学历要求" prop="jobEducation">
              <el-select v-model="jobForm.jobEducation" placeholder="请选择学历" class="full-width">
                <el-option label="高中" value="高中"></el-option>
                <el-option label="大专" value="大专"></el-option>
                <el-option label="本科" value="本科"></el-option>
                <el-option label="硕士" value="硕士"></el-option>
                <el-option label="博士" value="博士"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="经验要求" prop="jobExperience">
              <el-select v-model="jobForm.jobExperience" placeholder="请选择工作经验" class="full-width">
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
          <el-col :span="12">
            <el-form-item label="招聘人数" prop="jobPersonNumber">
              <el-input-number v-model="jobForm.jobPersonNumber" :min="1" placeholder="请输入招聘人数" class="w-full" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="职位描述" prop="jobPositionDescribe">
          <el-input v-model="jobForm.jobPositionDescribe" type="textarea" :rows="6" placeholder="请输入职位描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
            确认
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { companyJobList, companyJobUpdate, companySelectCategory, companyJobDelete, companyJobAdd } from '@/api/company/companyApi'
import { useRouter } from 'vue-router'

// 数据定义
const loading = ref(false)
const jobList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = ref({
  jobPosition: '',
  jobCategory: '',
  jobReview: ''
})

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const jobFormRef = ref(null)
const submitLoading = ref(false)

// 表单数据
const jobForm = ref({
  jobPosition: '',
  jobCategory: '',
  jobNature: '',
  jobSalary: '',
  jobArea: '',
  jobContact: '',
  jobEducation: '',
  jobExperience: '',
  jobPositionDescribe: '',
  jobPersonNumber: undefined
})

// 表单验证规则
const rules = {
  jobPosition: [{ required: true, message: '请输入职位名称', trigger: 'blur' }],
  jobCategory: [{ required: true, message: '请选择职位类别', trigger: 'change' }],
  jobNature: [{ required: true, message: '请选择工作性质', trigger: 'change' }],
  jobSalary: [{ required: true, message: '请输入薪资范围', trigger: 'blur' }],
  jobArea: [{ required: true, message: '请输入工作地区', trigger: 'blur' }],
  jobContact: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  jobEducation: [{ required: true, message: '请输入学历要求', trigger: 'blur' }],
  jobExperience: [{ required: true, message: '请输入经验要求', trigger: 'blur' }],
  jobPositionDescribe: [{ required: true, message: '请输入职位描述', trigger: 'blur' }],
  jobPersonNumber: [
    { required: true, message: '请输入招聘人数', trigger: 'blur' },
    { type: 'number', message: '招聘人数必须为数字', trigger: 'blur' }
  ]
}

const categoryOptions = ref([])
const categoryLoading = ref(false)

// 获取职位类别
const getCategories = async (query = '') => {
  categoryLoading.value = true
  try {
    const res = await companySelectCategory(query)
    if (res.code === 200) {
      categoryOptions.value = res.content.records.map(item => ({
        label: item.categoryName,
        value: item.categoryId
      }))
    } else {
      ElMessage.error(res.msg || '获取职位类别失败')
    }
  } catch (error) {
    console.error('获取职位类别失败:', error)
    ElMessage.error('获取职位类别失败')
  } finally {
    categoryLoading.value = false
  }
}

// 获取职位列表
const getJobList = async () => {
  loading.value = true
  try {
    const res = await companyJobList(
      currentPage.value,
      pageSize.value,
      searchForm.value.jobPosition,
      searchForm.value.jobReview,
      categoryOptions.value.find(item => item.value === searchForm.value.jobCategory)?.label || ''
    )

    if (res.code === 200) {
      jobList.value = res.content?.records
      total.value = res.content?.total
    } else {
      ElMessage.error(res.msg || '获取职位列表失败')
    }
  } catch (error) {
    ElMessage.error('获取职位列表失败')
  } finally {
    loading.value = false
  }
}

// 工具函数
const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD')
}

const getStatusType = (status) => {
  return status === '0' ? 'info' : 'success'
}

const getReviewType = (review) => {
  const types = {
    '0': 'warning',
    '1': 'success',
    '2': 'danger'
  }
  return types[review] || 'info'
}

const getReviewText = (review) => {
  const texts = {
    '0': '待审核',
    '1': '已通过',
    '2': '打回修改'
  }
  return texts[review] || '未知'
}

// 事件处理
const handleSearch = () => {
  currentPage.value = 1
  getJobList()
}

const resetSearch = () => {
  searchForm.value = {
    jobPosition: '',
    jobCategory: '',
    jobReview: ''
  }
  handleSearch()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  getJobList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  getJobList()
}

const handleAddJob = () => {
  dialogType.value = 'add'
  jobForm.value = {
    jobPosition: '',
    jobCategory: '',
    jobNature: '',
    jobSalary: '',
    jobArea: '',
    jobContact: '',
    jobEducation: '',
    jobExperience: '',
    jobPositionDescribe: '',
    jobPersonNumber: undefined
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogType.value = 'edit'
  jobForm.value = { ...row }
  dialogVisible.value = true
}

const handleToggleStatus = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要${row.jobStatus === '1' ? '停止' : '开始'}该职位的招聘吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const updateData = {
      ...row,
      jobStatus: row.jobStatus === '1' ? '0' : '1'
    }


    const res = await companyJobUpdate(updateData)

    if (res.code === 200) {
      ElMessage.success('操作成功')
      getJobList()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      '删除后无法恢复，是否确认删除？',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const res = await companyJobDelete(row.jobId)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      getJobList()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!jobFormRef.value) return

  await jobFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {

        const submitData = {
          ...jobForm.value,
          jobTime: new Date(),
          jobCategory: categoryOptions.value.find(item => item.value === jobForm.value.jobCategory)?.label || ''
        }

        let res
        if (dialogType.value === 'add') {
          submitData.jobReview = '0'
          submitData.jobStatus = '1'
          res = await companyJobAdd(submitData)
        } else {

          res = await companyJobUpdate(submitData)
        }

        if (res.code === 200) {
          ElMessage.success(dialogType.value === 'add' ? '添加成功' : '修改成功')
          dialogVisible.value = false
          getJobList() // 刷新列表
        } else {
          ElMessage.error(res.msg || (dialogType.value === 'add' ? '添加失败' : '修改失败'))
        }
      } catch (error) {
        ElMessage.error(dialogType.value === 'add' ? '添加失败' : '修改失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const router = useRouter()

const handlePositionClick = (row) => {
  router.push({
    path: '/home/jobResume',
    query: { jobId: row.jobId }
  })
}

onMounted(() => {
  getJobList()
  getCategories('')
})
</script>

<style scoped>
.jobs-container {
  padding: 24px;
  min-height: calc(100vh - 64px);
}

.jobs-card {
  background: #fff;
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
}

.card-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
}

.search-section {
  margin-bottom: 24px;
  padding: 24px;
  background: #f8fafc;
  border-radius: 8px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.position-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.position-name {
  font-weight: 500;
  color: #262626;
  font-size: 14px;
}

.salary-text {
  color: #f56c6c;
  font-weight: 500;
}

.table-section {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.03);
}


:deep(.custom-table) {
  border: none;
  border-radius: 8px 8px 0 0;
}

:deep(.custom-table th.el-table__cell) {
  background-color: #fafafa !important;
  border-bottom: 1px solid #f0f0f0;
  border-top: none;
  border-right: 1px solid #f0f0f0;
  color: #262626;
  font-weight: 500;
  padding: 16px;
  font-size: 14px;
}

:deep(.custom-table td.el-table__cell) {
  background-color: #fff;
  border-bottom: none;
  border-right: 1px solid #f0f0f0;
  padding: 16px;
  font-size: 14px;
  color: #595959;
  transition: all 0.3s;
}

:deep(.custom-table th.el-table__cell:last-child),
:deep(.custom-table td.el-table__cell:last-child) {
  border-right: none;
}


:deep(.custom-table .el-table__row:hover > td.el-table__cell) {
  background-color: #fafafa !important;
}

.pagination-container {
  padding: 16px 24px;
  display: flex;
  justify-content: center;
  background: #fff;
  border-radius: 0 0 8px 8px;
  border-top: 1px solid #f0f0f0;
}

:deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
  background-color: #1890ff;
}

:deep(.el-pagination.is-background .el-pager li) {
  border-radius: 4px;
  margin: 0 4px;
}


:deep(.el-loading-mask) {
  background-color: rgba(255, 255, 255, 0.9);
}

.job-form {
  padding: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 20px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-input__wrapper),
:deep(.el-textarea__inner) {
  box-shadow: none !important;
  border: 1px solid #dcdfe6;
}

:deep(.el-input__wrapper:hover),
:deep(.el-textarea__inner:hover) {
  border-color: #409eff;
}

:deep(.el-input__wrapper.is-focus),
:deep(.el-textarea__inner:focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 1px #409eff !important;
}

.w-full {
  width: 100%;
}

@media screen and (max-width: 768px) {
  .jobs-container {
    padding: 16px;
  }

  .search-form {
    flex-direction: column;
  }

  .search-form .el-form-item {
    margin-right: 0;
  }

  .el-dialog {
    width: 90% !important;
  }

  :deep(.el-form-item__label) {
    text-align: left;
  }

  :deep(.custom-table td.el-table__cell),
  :deep(.custom-table th.el-table__cell) {
    padding: 12px 8px;
  }

  .position-cell {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}


.action-buttons {
  display: flex;
  align-items: center;
  justify-content: center;
  white-space: nowrap;
}

:deep(.action-buttons .el-button) {
  padding: 0 4px;
  height: 20px;
  line-height: 20px;
  font-size: 14px;
}

:deep(.action-buttons .el-divider--vertical) {
  height: 16px;
  margin: 0 8px;
}

:deep(.el-button.is-disabled) {
  opacity: 0.5;
}

:deep(.el-button--primary.is-link:not(.is-disabled):hover) {
  color: #409eff;
  opacity: 0.8;
}

:deep(.el-button--danger.is-link:not(.is-disabled):hover) {
  color: #f56c6c;
  opacity: 0.8;
}

.position-name:hover {
  color: #409eff;
  text-decoration: underline;
}
</style>
