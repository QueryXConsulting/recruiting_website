<template>
  <div class="resume-container">
    <el-card class="resume-card">
      <template #header>
        <div class="card-header">
          <h2>{{ title }}</h2>
          <el-button v-if="activeTab === 'interview'" type="primary" @click="router.push({
            path: '/home/interviewTime',
          })">
            <el-icon>
              <Calendar />
            </el-icon>
            设置面试时间
          </el-button>
        </div>
      </template>


      <div class="tab-container">
        <el-tabs v-model="activeTab" @tab-click="handleTabClick">
          <el-tab-pane label="简历查看" name="resume">
            <div v-if="activeTab === 'resume'">
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
                  <el-form-item label="投递状态">
                    <el-select v-model="searchForm.resumeStatus" placeholder="请选择" clearable style="width: 120px;">
                      <el-option label="待查看" value="0" />
                      <el-option label="已查看" value="1" />
                      <el-option label="待面试" value="2" />
                      <el-option label="offer发放" value="3" />
                      <el-option label="上传材料" value="4" />
                      <el-option label="录入信息" value="5" />
                      <el-option label="预约报道" value="6" />
                    </el-select>
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                    <el-button @click="resetSearch">重置</el-button>
                  </el-form-item>
                </el-form>
              </div>

              <div class="table-section">
                <el-table :data="resumeList" border stripe v-loading="loading" class="custom-table" row-key="resumeId">
                  <el-table-column label="序号" type="index" width="90" align="center" :index="calculateIndex" />
                  <el-table-column prop="resumeName" label="简历名称" width="160" show-overflow-tooltip align="center" />
                  <el-table-column prop="resumeType" label="简历类型" width="160" align="center">
                    <template #default="scope">
                      <el-tag :type="scope.row.resumeType == 0 ? 'success' : 'primary'">
                        {{ scope.row.resumeType == 0 ? '在线简历' : '附件简历' }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column prop="resumeStatus" label="投递状态" width="500" align="center">
                    <template #default="scope">
                      <div class="status-timeline">
                        <div v-for="(status, index) in statusList" :key="status.value" class="timeline-item" :class="{
                          'active': Number(scope.row.resumeStatus) >= Number(status.value),
                          'current': scope.row.resumeStatus === status.value,
                          'deleted': scope.row.resumeDelete === '0' && Number(scope.row.resumeStatus) >= Number(status.value)
                        }">
                          <div class="timeline-line"></div>
                          <div class="timeline-dot"></div>
                          <div class="timeline-text">{{ status.label }}</div>
                        </div>
                      </div>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="270" fixed="right" align="center">
                    <template #default="scope">
                      <div class="action-buttons">
                        <el-button type="primary" link @click="viewResume(scope.row)">
                          查看
                        </el-button>
                        <template v-if="scope.row.resumeStatus === '1' && scope.row.resumeDelete !== '0'">
                          <el-button type="success" link @click="sendInvitation(scope.row, 'interview')">
                            面试邀约
                          </el-button>
                          <el-button type="warning" link @click="sendInvitation(scope.row, 'test')">
                            笔试邀约
                          </el-button>
                        </template>
                        <el-button type="danger" link @click="handleUnsuitable(scope.row)"
                          v-if="scope.row.resumeDelete !== '0' && scope.row.resumeStatus !== '0'">
                          不合适
                        </el-button>
                      </div>
                    </template>
                  </el-table-column>
                </el-table>
              </div>


              <div class="pagination-container">
                <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize"
                  :page-sizes="[10, 20, 30, 50]" layout="total, sizes, prev, pager, next" :total="total"
                  @size-change="handleSizeChange" @current-change="handlePageChange" />
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="预约面试" name="interview">
            <div class="interview-header">

            </div>
          </el-tab-pane>
          <el-tab-pane label="确认offer" name="offer"></el-tab-pane>
          <el-tab-pane label="上传材料" name="material"></el-tab-pane>
          <el-tab-pane label="录入信息" name="info"></el-tab-pane>
          <el-tab-pane label="预约报到" name="checkin"></el-tab-pane>
        </el-tabs>
      </div>

      <!-- 添加PDF预览对话框 -->
      <el-dialog v-model="pdfDialogVisible" title="简历预览" width="80%" :destroy-on-close="true">
        <div class="pdf-container">
          <iframe v-if="pdfUrl" :src="pdfUrl" width="100%" height="600px" frameborder="0"></iframe>
        </div>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Calendar } from '@element-plus/icons-vue'

import { useRoute, useRouter } from 'vue-router'
import { companyResumeList, selectResume, updateResumeStatus } from '@/api/company/companyApi'

const route = useRoute()
const router = useRouter()
const jobId = route.query.jobId

// 数据定义
const loading = ref(false)
const resumeList = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
let title = ref('职位投递简历查看')

const searchForm = ref({
  resumeName: '',
  resumeType: '',
  resumeStatus: ''
})


const pdfDialogVisible = ref(false)
const pdfUrl = ref('')

// 添加状态列表定义
const statusList = [
  { label: '已查看', value: '1' },
  { label: '待面试', value: '2' },
  { label: 'offer发放', value: '3' },
  { label: '上传材料', value: '4' },
  { label: '录入信息', value: '5' },
  { label: '预约报道', value: '6' }
]

// 修改选项卡相关逻辑
const activeTab = ref('resume') // 默认显示简历查看

const titleMap = {
  'resume': '职位投递简历查看',
  'interview': '预约面试',
  'offer': '确认offer',
  'material': '上传材料',
  'info': '录入信息',
  'checkin': '预约报到'
}

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
      searchForm.value.resumeName,
      searchForm.value.resumeStatus
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
  if (row.resumeStatus == 0) {
    await handleStatusChange(1, row)
  }

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
    resumeType: '',
    resumeStatus: ''
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


const calculateIndex = (index) => {
  return (pageNum.value - 1) * pageSize.value + index + 1
}


const handleStatusChange = async (resumeStatus, row) => {
  try {
    const res = await updateResumeStatus(resumeStatus, row.resumeId, jobId, 1)
    if (res.code === 200) {
      getResumeList()
    }
  } catch (error) {
    console.error('状态更新失败:', error)
    ElMessage.error('状态更新失败')
  }
}


const sendInvitation = async (row, type) => {
  try {

    const message = type === 'interview' ? '面试' : '笔试'
    ElMessage.success(`已发送${message}邀约`)


    if (type === 'interview') {
      await handleStatusChange('2', row)
    }
  } catch (error) {
    console.error('发送邀约失败:', error)
    ElMessage.error('发送邀约失败')
  }
}

const handleUnsuitable = async (row) => {
  try {
    await ElMessageBox.confirm('确认将该简历标记为不合适吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    let result = await updateResumeStatus('7', row.resumeId, jobId, 0)
    if (result.code === 200) {
      ElMessage.success('已将简历标记为不合适')
      getResumeList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  }
}




const handleTabClick = (tab) => {
  title.value = titleMap[tab.props.name] || '职位投递简历查看'
}


const handleSetInterviewTime = () => {
  router.push({
    path: '/home/interviewTime',
  })
}

onMounted(() => {
  if (!jobId) {
    ElMessage.error('缺少职位ID参数')
    return
  }
  getResumeList()
})


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
  padding: 8px 16px;
  font-size: 14px;
}

:deep(.custom-table td.el-table__cell) {
  background-color: #fff;
  border-bottom: none;
  border-right: 1px solid #f0f0f0;
  padding: 8px 16px;
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
  gap: 8px;
  flex-wrap: wrap;
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


.status-timeline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;
  padding: 10px 40px;
  width: 100%;
}

.timeline-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  flex: 1;
}

.timeline-line {
  position: absolute;
  top: 10px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #E4E7ED;
  z-index: 1;
}

.timeline-item:first-child .timeline-line {
  left: 50%;
  width: 50%;
}

.timeline-item:last-child .timeline-line {
  width: 50%;
}

.timeline-dot {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background-color: #fff;
  border: 2px solid #E4E7ED;
  position: relative;
  z-index: 2;
}

.timeline-text {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}


.timeline-item.active .timeline-line {
  background-color: #409EFF;
}

.timeline-item.active .timeline-dot {
  border-color: #409EFF;
  background-color: #fff;
}

.timeline-item.current .timeline-dot {
  background-color: #409EFF;
}

.timeline-item.active .timeline-text {
  color: #409EFF;
}


.timeline-item.deleted .timeline-line {
  background-color: #409EFF;
}

.timeline-item.deleted .timeline-dot {
  border-color: #409EFF;
  background-color: #fff;
}

.timeline-item.deleted.current .timeline-dot {
  border-color: #F56C6C;
  background-color: #F56C6C;
}

.timeline-item.deleted .timeline-text {
  color: #909399;
}

.timeline-item.deleted.active:not(.current),
.timeline-item.deleted.active:not(.current) {
  background-color: #fff;
  border-color: #409EFF;
}


.tab-container {
  margin: 20px 0;
}

:deep(.el-tabs__nav-wrap::after) {
  height: 1px;
}

:deep(.el-tabs__item) {
  font-size: 14px;
  padding: 0 20px;
}

:deep(.el-tabs__item.is-active) {
  color: #409EFF;
}


:deep(.el-tab-pane) {
  padding: 20px 0;
}

.interview-header {
  padding: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
