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
        <el-tabs v-model="activeTab">
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
                <el-table :data="resumeList" border stripe class="custom-table" row-key="resumeId">
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
                          <el-button
                            v-if="scope.row.interviewStatus != '1' && scope.row.interviewStatus == null && scope.row.interviewStatus != '2'"
                            type="success" link @click="sendInvitation(scope.row, 'interview')">
                            面试邀约
                          </el-button>
                          <el-button v-else type="success" disabled link
                            @click="sendInvitation(scope.row, 'interview')">
                            已发送
                          </el-button>
                          <el-button type="warning" link @click="sendInvitation(scope.row, 'test')">
                            笔试邀约
                          </el-button>
                        </template>
                        <el-button type="danger" link @click="handleUnsuitable(scope.row)"
                          v-if="scope.row.resumeDelete !== '0' & scope.row.resumeStatus !== '0'">
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
              <el-table :data="interviewList" border stripe class="custom-table" row-key="interviewId">
                <el-table-column label="工作名称" prop="jobName" width="160" align="center" />
                <el-table-column label="时间" prop="interviewDate" width="150" align="center" />
                <el-table-column label="地点" prop="interviewRegion" width="160" align="center" />
                <el-table-column label="类型" prop="interviewType" width="110" align="center">
                  <template #default="scope">
                    <el-tag :type="scope.row.interviewType == '0' ? 'success' : 'primary'">
                      {{ scope.row.interviewType == '0' ? '线上' : '线下' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="结果" prop="interviewResult" width="120" align="center">
                  <template #default="scope">
                    <el-tag
                      :type="scope.row.interviewResult == '1' ? 'success' : (scope.row.interviewResult == '2' ? 'danger' : 'info')">
                      {{ scope.row.interviewResult == '1' ? '通过' : (scope.row.interviewResult == '2' ? '未通过' : '待面试') }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="状态" prop="interviewStatus" width="140" align="center">
                  <template #default="scope">
                    <el-tag
                      :type="scope.row.interviewStatus == '1' ? 'success' : (scope.row.interviewStatus == '0' ? 'danger' : (scope.row.interviewStatus == '3' ? 'warning' : 'info'))">
                      {{ scope.row.interviewStatus == '1' ? '接受' : (scope.row.interviewStatus == '0' ? '拒绝' :
                        (scope.row.interviewStatus == '3' ? '已取消' : '未选择')) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="时长" prop="interviewTime" width="120" align="center">
                  <template #default="scope">
                    {{ scope.row.interviewTime }} 分钟
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="180" fixed="right" align="center">
                  <template #default="scope">
                    <div class="action-buttons">
                      <el-button type="primary" link @click="editInterview(scope.row)" :disabled="scope.row.interviewResult != '0' ||
                        (scope.row.interviewStatus == '3' &&
                          scope.row.interviewStatus == '0')">
                        修改
                      </el-button>
                      <el-button type="danger" link @click="viewInterview(scope.row)"
                        v-if="scope.row.interviewStatus != '3' && scope.row.interviewResult == '0'">
                        取消
                      </el-button>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <div class="pagination-container">
              <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[10, 20, 30, 50]"
                layout="total, sizes, prev, pager, next" :total="interviewTotal"
                @size-change="handleInterviewSizeChange" @current-change="handleInterviewPageChange" />
            </div>
          </el-tab-pane>
          <el-tab-pane label="offer发放" name="offer"></el-tab-pane>
          <el-tab-pane label="材料审核" name="material"></el-tab-pane>
          <el-tab-pane label="信息录入" name="info"></el-tab-pane>
          <el-tab-pane label="报到预约" name="checkin"></el-tab-pane>
        </el-tabs>
      </div>

      <!-- 添加PDF预览对话框 -->
      <el-dialog v-model="pdfDialogVisible" title="简历预览" width="80%" :destroy-on-close="true">
        <div class="pdf-container">
          <iframe v-if="pdfUrl" :src="pdfUrl" width="100%" height="600px" frameborder="0"></iframe>
        </div>
      </el-dialog>

      <el-dialog v-model="interviewDialogVisible" title="面试邀约" width="50%" style="width: 300px;">
        <el-form :model="interviewData" ref="interviewForm">
          <el-form-item label="面试类型" prop="interviewType">
            <el-select v-model="interviewData.interviewType" placeholder="请选择面试类型" @change="handleInterviewTypeChange">
              <el-option label="线上" :value="0" />
              <el-option label="线下" :value="1" />
            </el-select>
          </el-form-item>
          <el-form-item label="面试地点" prop="interviewRegion" v-if="interviewData.interviewType == '1'">
            <el-input v-model="interviewData.interviewRegion" placeholder="请输入面试地点" />
          </el-form-item>
          <el-form-item label="面试时长" prop="interviewTime">
            <el-select v-model="interviewData.interviewTime" placeholder="请选择面试时长">
              <el-option label="30分钟" :value="30" />
              <el-option label="60分钟" :value="60" />
              <el-option label="90分钟" :value="90" />
              <el-option label="120分钟" :value="120" />
            </el-select>
          </el-form-item>
        </el-form>
        <template v-slot:footer>
          <el-button type="primary" @click="submitInterview">发送</el-button>
          <el-button @click="interviewDialogVisible = false">取消</el-button>
        </template>
      </el-dialog>

      <!-- 添加修改对话框 -->
      <el-dialog v-model="editInterviewDialogVisible" title="修改面试信息" width="20%">
        <el-form :model="editInterviewData" ref="editInterviewForm">
          <el-form-item label="时间" prop="interviewDate">
            <el-date-picker v-model="editInterviewData.interviewDate" type="datetime" placeholder="选择时间" />
          </el-form-item>
          <el-form-item label="地点" prop="interviewRegion">
            <el-input v-model="editInterviewData.interviewRegion" placeholder="请输入面试地点" />
          </el-form-item>
          <el-form-item label="类型" prop="interviewType">
            <el-select v-model="editInterviewData.interviewType" placeholder="请选择面试类型">
              <el-option v-for="(label, value) in interviewTypeOptions" :key="value" :label="label" :value="value" />
            </el-select>
          </el-form-item>
          <el-form-item label="结果" prop="interviewResult">
            <el-select v-model="editInterviewData.interviewResult" placeholder="请选择结果">
              <el-option label="通过" :value="1" />
              <el-option label="未通过" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="时长" prop="interviewTime">
            <el-select v-model="editInterviewData.interviewTime" placeholder="请选择时长">
              <el-option label="30分钟" :value="30" />
              <el-option label="60分钟" :value="60" />
              <el-option label="90分钟" :value="90" />
              <el-option label="120分钟" :value="120" />
            </el-select>
          </el-form-item>
        </el-form>
        <template v-slot:footer>
          <el-button type="primary" @click="submitEditInterview">保存</el-button>
          <el-button @click="editInterviewDialogVisible = false">取消</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Calendar } from '@element-plus/icons-vue'

import { useRoute, useRouter } from 'vue-router'
import { companyResumeList, selectResume, updateResumeStatus, sendInvitationData, selectInterviewList, updateInterviewList } from '@/api/company/companyApi'

const route = useRoute()
const router = useRouter()
const jobId = route.query.jobId

// 数据定义
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


const statusList = [
  { label: '已查看', value: '1' },
  { label: '待面试', value: '2' },
  { label: 'offer确认', value: '3' },
  { label: '上传材料', value: '4' },
  { label: '录入信息', value: '5' },
  { label: '预约报道', value: '6' }
]


const activeTab = ref('resume')

const tabTitles = {
  'resume': '职位投递简历查看',
  'interview': '预约面试',
  'offer': 'offer发放',
  'material': '材料审核',
  'info': '信息录入',
  'checkin': '报到预约'
};

watch(activeTab, (newTab) => {
  title.value = tabTitles[newTab] || '其他标题';
});



const interviewData = ref({
  userId: null,
  interviewRegion: '',
  interviewType: '1',
  interviewTime: 30
})

const interviewDialogVisible = ref(false)

const interviewList = ref([])
const interviewTotal = ref(0)


const editInterviewDialogVisible = ref(false)
const editInterviewData = ref({
  interviewDate: '',
  interviewRegion: '',
  interviewResult: '',
  interviewStatus: '',
  interviewTime: '',
  interviewType: ''
})


const interviewTypeOptions = {
  0: '线上',
  1: '线下'
}


const getResumeList = async () => {
  if (!jobId) {
    ElMessage.error('职位ID不能为空')
    return
  }


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
  }
}

// 获取面试列表
const getInterviewList = async () => {
  try {
    const res = await selectInterviewList(pageNum.value, pageSize.value, jobId)
    if (res.code === 200) {
      interviewList.value = res.content.records
      interviewTotal.value = res.content.total
    } else {
      ElMessage.error(res.msg || '获取面试列表失败')
    }
  } catch (error) {
    console.error('获取面试列表失败:', error)
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
    resumeStatus: '',
    interviewData: ''
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

// 分页处理
const handleInterviewSizeChange = (val) => {
  pageSize.value = val
  getInterviewList()
}

const handleInterviewPageChange = (val) => {
  pageNum.value = val
  getInterviewList()
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

const sendInvitation = (row, type) => {

  interviewData.value = {
    resumeId: row.resumeId,
    resumeType: row.resumeType,
    jobId: jobId,
    userId: row.userId,
    interviewRegion: '',
    interviewType: 1,
    interviewTime: 30
  };
  interviewDialogVisible.value = true

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

const submitInterview = async () => {
  try {
    let result = await sendInvitationData(interviewData.value);
    if (result.code === 200) {
      getResumeList()
      ElMessage.success('发送邀约成功')
      interviewDialogVisible.value = false
    } else {
      ElMessage.error('发送邀约失败')
    }

  } catch (error) {
    console.error('发送邀约失败:', error)
    ElMessage.error('发送邀约失败')
  }
}

const handleInterviewTypeChange = (value) => {
  interviewData.value.interviewType = value;
}

const viewInterview = async (row) => {
  editInterviewData.value.interviewId = row.interviewId
  editInterviewData.value.interviewStatus = 3

  let result = await updateInterviewList(editInterviewData.value)
  if (result.code === 200) {
    ElMessage.success('撤销成功')
    getInterviewList()
  } else {
    ElMessage.error('撤销失败')
  }
}

const editInterview = async (row) => {
  editInterviewData.value = { ...row }
  editInterviewData.value.interviewType = row.interviewType

  const resultMapping = {
    '1': '通过',
    '2': '未通过'
  };
  editInterviewData.value.interviewResult = resultMapping[row.interviewResult];
  editInterviewDialogVisible.value = true
}

const submitEditInterview = async () => {
  try {
    const result = await updateInterviewList(editInterviewData.value);
    if (result.code === 200) {
      ElMessage.success('面试信息更新成功');
      editInterviewDialogVisible.value = false;
      getInterviewList();
    } else {
      ElMessage.error('面试信息更新失败');
    }
  } catch (error) {
    console.error('更新面试信息失败:', error);
    ElMessage.error('更新面试信息失败');
  }
}

onMounted(() => {
  if (!jobId) {
    ElMessage.error('缺少职位ID参数')
    return
  }
  getResumeList()
  getInterviewList()
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
