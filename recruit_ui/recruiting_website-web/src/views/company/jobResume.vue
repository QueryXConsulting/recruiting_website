<template>
  <div class="resume-container">
    <el-card class="resume-card">
      <template #header>
        <div class="card-header">
          <h2>职位投递简历查看</h2>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-section">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="简历名称">
            <el-input v-model="searchForm.resumeName" placeholder="请输入简历名称" clearable />
          </el-form-item>
          <el-form-item label="简历类型">
            <el-select v-model="searchForm.resumeType" placeholder="请选择" clearable style="width: 100px;">
              <el-option label="附件简历" :value="1" />
              <el-option label="在线简历" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 表格部分 -->
      <div class="table-section">
        <el-table :data="resumeList" border stripe v-loading="loading" class="custom-table">
          <el-table-column type="index" label="序号" width="325" align="center" :index="calculateIndex" />
          <el-table-column prop="resumeName" label="简历名称" width="365" show-overflow-tooltip align="center" />
          <el-table-column prop="resumeType" label="简历类型" width="345" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.resumeType == 0 ? 'success' : 'primary'">
                {{ scope.row.resumeType == 0 ? '在线简历' : '附件简历' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" fixed="right" align="center">
            <template #default="scope">
              <div class="action-buttons">
                <el-button type="primary" link @click="viewResume(scope.row)">
                  查看
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[10, 20, 30, 50]"
          layout="total, sizes, prev, pager, next" :total="total" @size-change="handleSizeChange"
          @current-change="handlePageChange" />
      </div>
    </el-card>

    <!-- 添加PDF预览对话框 -->
    <el-dialog v-model="pdfDialogVisible" title="简历预览" width="80%" :destroy-on-close="true">
      <div class="pdf-container">
        <iframe v-if="pdfUrl" :src="pdfUrl" width="100%" height="600px" frameborder="0"></iframe>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { companyResumeList, selectResume } from '@/api/company/companyApi'

const route = useRoute()
const router = useRouter()
const jobId = route.query.jobId

// 数据定义
const loading = ref(false)
const resumeList = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 搜索表单
const searchForm = ref({
  resumeName: '',
  resumeType: ''
})

// 添加新的响应式变量
const pdfDialogVisible = ref(false)
const pdfUrl = ref('')

// 获取简历列表
const getResumeList = async () => {
  if (!jobId) {
    ElMessage.error('职位ID不能为空')
    return
  }

  loading.value = true
  try {
    const res = await companyResumeList(
      jobId,
      pageNum.value,
      pageSize.value,
      searchForm.value.resumeType,
      searchForm.value.resumeName
    )

    if (res.code === 200) {
      resumeList.value = res.content?.records
      total.value = res.content?.total
    } else {
      ElMessage.error(res.msg || '获取简历列表失败')
    }
  } catch (error) {
    console.error('获取简历列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 修改查看简历方法
const viewResume = async (row) => {
  if (row.resumeType == 0) {
    router.push({
      path: '/home/resumeDetail',
      query: {
        resumeId: row.resumeId
      }
    })
  } else {
    try {
      const result = await selectResume(
        {
          resumeId: row.resumeId,
          resumeType: '1'
        })
      if (result.code === 200 && result.content) {

        const binaryString = atob(result.content);
        const bytes = new Uint8Array(binaryString.length);
        for (let i = 0; i < binaryString.length; i++) {
          bytes[i] = binaryString.charCodeAt(i);
        }

        const blob = new Blob([bytes], { type: 'application/pdf' });
        pdfUrl.value = URL.createObjectURL(blob);
        pdfDialogVisible.value = true;
      } else {
        ElMessage.error('获取PDF文件失败')
      }
    } catch (error) {
      console.error('预览PDF失败:', error)
      ElMessage.error('预览PDF失败')
    }
  }
}

// 搜索处理
const handleSearch = () => {
  pageNum.value = 1
  getResumeList()
}

const resetSearch = () => {
  searchForm.value = {
    resumeName: '',
    resumeType: ''
  }
  handleSearch()
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  getResumeList()
}

const handlePageChange = (val) => {
  pageNum.value = val
  getResumeList()
}

// 计算序号
const calculateIndex = (index) => {
  return (pageNum.value - 1) * pageSize.value + index + 1
}

onMounted(() => {
  if (!jobId) {
    ElMessage.error('缺少职位ID参数')
    return
  }
  getResumeList()
})

// 在组件卸载时清理URL
onBeforeUnmount(() => {
  if (pdfUrl.value) {
    URL.revokeObjectURL(pdfUrl.value)
  }
})
</script>

<style scoped>
.resume-container {
  padding: 24px;
  min-height: calc(100vh - 64px);
}

.resume-card {
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

:deep(.el-button--primary.is-link:not(.is-disabled):hover) {
  color: #409eff;
  opacity: 0.8;
}

@media screen and (max-width: 768px) {
  .resume-container {
    padding: 16px;
  }

  .search-form {
    flex-direction: column;
  }

  .search-form .el-form-item {
    margin-right: 0;
  }

  :deep(.custom-table td.el-table__cell),
  :deep(.custom-table th.el-table__cell) {
    padding: 12px 8px;
  }
}

.pdf-container {
  width: 100%;
  height: 600px;
  overflow: hidden;
}
</style>
