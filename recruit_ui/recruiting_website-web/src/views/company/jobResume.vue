<template >
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
                  <el-table-column label="操作" width="200" fixed="right" align="center">
                    <template #default="scope">
                      <div class="action-buttons">
                        <el-button type="primary" link @click="viewResume(scope.row)">
                          查看
                        </el-button>
                        <template v-if="scope.row.resumeStatus === '1' && scope.row.resumeDelete !== '0'">
                          <el-button v-if="scope.row.interviewStatus != '1' && scope.row.interviewStatus != '2'"
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
                <el-pagination v-model:currentPage="pageNum" v-model:pageSize="pageSize" :pageSizes="[10, 20, 30, 50]"
                  layout="total, sizes, prev, pager, next" :total="total" @sizeChange="handleSizeChange"
                  @currentChange="handlePageChange" />
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="预约面试" name="interview">
            <div class="interview-header">
              <el-table :data="interviewList" border stripe class="custom-table" row-key="interviewId">
                <el-table-column label="简历名称" prop="resumeName" width="150" align="center" />
                <el-table-column label="时间" prop="interviewDate" width="150" align="center" />
                <el-table-column label="地点" prop="interviewRegion" width="160" align="center" />
                <el-table-column label="类型" prop="interviewType" width="90" align="center">
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
                          scope.row.interviewStatus == '0') ||
                        scope.row.interviewStatus == '3' ||
                        scope.row.interviewStatus == '0'">
                        修改
                      </el-button>
                      <el-button type="danger" link @click="viewInterview(scope.row)"
                        v-if="scope.row.interviewStatus != '3' && scope.row.interviewResult == '0' && scope.row.interviewStatus != '0'">
                        取消
                      </el-button>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <div class="pagination-container">
              <el-pagination v-model:currentPage="interviewPageNum" v-model:pageSize="interviewPageSize"
                :pageSizes="[10, 20, 30, 50]" layout="total, sizes, prev, pager, next" :total="interviewTotal"
                @sizeChange="handleInterviewSizeChange" @currentChange="handleInterviewPageChange" />
            </div>
          </el-tab-pane>
          <el-tab-pane label="offer发放" name="offer">
            <div class="table-section" style="min-width: 830px; max-width: 100%;">
              <el-table :data="offersList" border stripe class="custom-table" row-key="offersId">
                <el-table-column label="序号" type="index" width="100" align="center" />
                <el-table-column label="应聘者" prop="userName" width="180" align="center" />
                <el-table-column label="发送时间" prop="offersDate" width="180" align="center">
                  <template #default="scope">
                    {{ scope.row.offersDate }}
                  </template>
                </el-table-column>
                <el-table-column label="状态" prop="offersStatus" width="120" align="center">
                  <template #default="scope">
                    <el-tag :type="getOfferStatusType(scope.row.offersStatus)">
                      {{ getOfferStatusText(scope.row.offersStatus) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="250" align="center">
                  <template #default="scope">
                    <div class="action-buttons">
                      <el-button type="primary" link @click="viewOffer(scope.row)">
                        {{ scope.row.offersFilePath ? '查看' : '上传offer' }}
                      </el-button>
                      <el-button type="success" link @click="sendOffer(scope.row)"
                        v-if="scope.row.offersFilePath && scope.row.offersStatus === '0'">
                        发送
                      </el-button>
                      <el-button type="danger" link @click="cancelOffer(scope.row)"
                        v-if="scope.row.offersStatus === '0'">
                        撤销
                      </el-button>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
              <div class="pagination-container">
                <el-pagination v-model:currentPage="offerPageNum" v-model:pageSize="offerPageSize"
                  :pageSizes="[10, 20, 30, 50]" layout="total, sizes, prev, pager, next" :total="offerTotal"
                  @sizeChange="handleOfferSizeChange" @currentChange="handleOfferPageChange" />
              </div>
            </div>
          </el-tab-pane>
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

      <!-- 修改上传offer对话框 -->
      <el-dialog v-model="uploadOfferDialogVisible" title="上传offer" width="30%">
        <div class="upload-options">
          <el-button type="primary" @click="selectTemplate">
            <el-icon>
              <Document />
            </el-icon>
            选择模板
          </el-button>
          <el-upload
            class="upload-pdf"
            :action="uploadUrl"
            :headers="headers"
            :data="{ offerId: currentOfferId }"
            :show-file-list="false"
            :before-upload="beforeUpload"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            accept=".doc,.docx">
            <template #default>
              <el-button type="primary">
                <el-icon>
                  <Upload />
                </el-icon>
                上传模板
              </el-button>
            </template>
            <template #tip>
              <div class="el-upload__tip">
                只能上传Word文件(.doc/.docx)
              </div>
            </template>
          </el-upload>
        </div>
      </el-dialog>

      <!-- 修改 Word 编辑对话框 -->
      <el-dialog
        v-model="wordEditDialogVisible"
        title="Word编辑"
        width="90%"
        :destroy-on-close="true"
        :close-on-click-modal="false">
        <div class="word-edit-container">
          <DocumentEditor
            v-if="documentConfig"
            :id="'docEditor'"
            :documentServerUrl="documentServerUrl"
            :config="documentConfig"
            :events_onDocumentReady="onDocumentReady"
            :events_onError="onDocumentError"
          />
        </div>
        <template #footer>
          <el-button type="primary" @click="saveWordEdit">保存</el-button>
          <el-button @click="wordEditDialogVisible = false">取消</el-button>
        </template>
      </el-dialog>

      <!-- 添加模板选择对话框 -->
      <el-dialog v-model="templateDialogVisible" title="选择模板" width="70%">
        <div class="template-list">
          <el-row :gutter="20">
            <el-col v-for="template in templateList" :key="template.id" :span="6">
              <div class="template-item" @click="handleTemplateSelect(template)" :class="{ 'active': selectedTemplate === template.id }">
                <div class="template-preview">
                  <el-image
                    :src="template.thumbnail"
                    fit="cover"
                    :preview-src-list="[template.preview]">
                    <template #error>
                      <div class="image-placeholder">
                        <el-icon><Document /></el-icon>
                      </div>
                    </template>
                  </el-image>
                </div>
                <div class="template-info">
                  <div class="template-name">{{ template.name }}</div>
                  <div class="template-desc">{{ template.description }}</div>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
        <template #footer>
          <el-button @click="templateDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmTemplate" :disabled="!selectedTemplate">确定</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import { Calendar, Document, Upload } from '@element-plus/icons-vue'
import { Editor } from '@hufe921/canvas-editor'
import { useRoute, useRouter } from 'vue-router'
import { companyResumeList, selectResume, updateResumeStatus, sendInvitationData, selectInterviewList, updateInterviewList, selectOffersList, selectOfferTemplate } from '@/api/company/companyApi'
import { DocumentEditor } from '@onlyoffice/document-editor-vue'
import userStore from '@/store/user'


const documentConfig = ref({
  width: "100%",
  height: "100%",
  type: "desktop",
  document: {
    fileType: "docx",
    key: "",
    title: "",
    url: "",
    permissions: {
      edit: true,
      download: true,
      review: true
    }
  },
  editorConfig: {
    mode: "edit",
    lang: "zh-CN",
    region: "cn",
    user: {
      id: userStore.userId || "guest",
      name: userStore.userName || "访客"
    },
    customization: {
      autosave: false,
      comments: false,
      chat: false,
      compactToolbar: false,
      feedback: false,
      forcesave: false,
      help: true,
      hideRightMenu: false,
      toolbarNoTabs: false,
      plugins: true
    }
  }
})

const wordEditDialogVisible = ref(false)

const editor = ref(null)

const currentOfferId = ref(null)
const uploadOfferDialogVisible = ref(false)

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
  if (newTab === 'offer') {
    getOffersList()
  }
});



const interviewData = ref({
  userId: null,
  jobId: null,
  interviewRegion: '',
  interviewType: '1',
  interviewTime: 30
})

const interviewDialogVisible = ref(false)

const interviewList = ref([])
const interviewTotal = ref(0)


const editInterviewDialogVisible = ref(false)
const editInterviewData = ref({
  interviewId: '',
  interviewDate: '',
  interviewRegion: '',
  interviewResult: '',
  interviewStatus: '',
  interviewTime: '',
  interviewType: '',
  jobResumeId: '',
  userId: '',
  jobId: ''
})


const interviewTypeOptions = {
  0: '线上',
  1: '线下'
}


const interviewPageNum = ref(1)
const interviewPageSize = ref(10)


const offersList = ref([])
const offerTotal = ref(0)
const offerPageNum = ref(1)
const offerPageSize = ref(10)


const getOfferStatusType = (status) => {
  const statusMap = {
    '0': 'info',
    '1': 'success',
    '2': 'danger',
    '3': 'warning'
  }
  return statusMap[status] || 'info'
}

const getOfferStatusText = (status) => {
  const statusMap = {
    '0': '待发送',
    '1': '已接受',
    '2': '已拒绝',
    '3': '已撤销'
  }
  return statusMap[status] || '未知'
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


const getInterviewList = async () => {
  try {
    const res = await selectInterviewList(interviewPageNum.value, interviewPageSize.value, jobId)
    if (res.code === 200) {
      interviewList.value = res.content?.records
      interviewTotal.value = res.content?.total

    } else {
      ElMessage.error(res.msg || '获取面试列表失败')
    }
  } catch (error) {
    console.error('获取面试列表失败:', error)
  }
}


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


const handleSizeChange = (val) => {
  pageSize.value = val
  getResumeList()
}

const handlePageChange = (val) => {
  pageNum.value = val
  getResumeList()
}


const handleInterviewSizeChange = (val) => {
  interviewPageSize.value = val
  getInterviewList()
}

const handleInterviewPageChange = (val) => {
  interviewPageNum.value = val
  getInterviewList()
}

const calculateIndex = (index) => {
  return (pageNum.value - 1) * pageSize.value + index + 1
}

const handleStatusChange = async (resumeStatus, row) => {
  try {
    const res = await updateResumeStatus(resumeStatus, row.jobResumeId, 1)
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
    let result = await updateResumeStatus('7', row.jobResumeId, 0)
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
  console.log('当前行数据:', row)
  console.log('当前行ID:', row.interviewId)

  editInterviewData.value = {
    interviewId: row.interviewId,
    interviewDate: row.interviewDate,
    interviewRegion: row.interviewRegion,
    interviewType: row.interviewType,
    interviewResult: row.interviewResult,
    interviewTime: row.interviewTime,
    interviewStatus: 3,
    jobResumeId: row.jobResumeId,
    userId: row.userId,
    jobId: row.jobId
  }

  console.log('更新数据:', editInterviewData.value)

  let result = await updateInterviewList(editInterviewData.value)
  if (result.code === 200) {
    ElMessage.success('撤销成功')
    getInterviewList()
  } else {
    ElMessage.error('撤销失败')
  }
}

const editInterview = async (row) => {

  editInterviewData.value = {
    interviewId: row.interviewId,
    interviewDate: row.interviewDate,
    interviewRegion: row.interviewRegion,
    interviewType: row.interviewType,
    interviewResult: row.interviewResult,
    interviewTime: row.interviewTime,
    interviewStatus: row.interviewStatus,
    jobResumeId: row.jobResumeId,
    userId: row.userId,
    jobId: row.jobId
  }

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


const getOffersList = async () => {
  try {
    const res = await selectOffersList(offerPageNum.value, offerPageSize.value, jobId)
    if (res.code === 200) {
      offersList.value = res.content?.records
      offerTotal.value = res.content?.total
    } else {
      ElMessage.error(res.msg || '获取Offer列表失败')
    }
  } catch (error) {
    console.error('获取Offer列表失败:', error)
    ElMessage.error('获取Offer列表失败')
  }
}


const handleOfferSizeChange = (val) => {
  offerPageSize.value = val
  getOffersList()
}

const handleOfferPageChange = (val) => {
  offerPageNum.value = val
  getOffersList()
}

// Offer操作方法
const viewOffer = (row) => {
  if (!row.offersFilePath) {
    currentOfferId.value = row.offersId
    uploadOfferDialogVisible.value = true
  } else {
    window.open(row.offersFilePath)
  }
}

const sendOffer = async (row) => {
  try {

    const result = await sendOfferRequest(row.offersId)
    if (result.code === 200) {
      ElMessage.success('发送成功')
      getOffersList()
    } else {
      ElMessage.error(result.msg || '发送失败')
    }
  } catch (error) {
    console.error('发送offer失败:', error)
    ElMessage.error('发送失败')
  }
}

const cancelOffer = async (row) => {
  try {
    await ElMessageBox.confirm('确认撤销该offer？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const result = await cancelOfferRequest(row.offersId)
    if (result.code === 200) {
      ElMessage.success('撤销成功')
      getOffersList()
    } else {
      ElMessage.error(result.msg || '撤销失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('撤销offer失败:', error)
      ElMessage.error('撤销失败')
    }
  }
}

// 修改模板相关的状态变量
const templateDialogVisible = ref(false)
const selectedTemplate = ref(null)
const templateList = ref([])

// 处理模板选择
const handleTemplateSelect = (template) => {
  selectedTemplate.value = template.id
}

// 获取模板列表
const getTemplateList = async () => {
  try {
    const res = await selectOfferTemplate()
    if (res.code === 200) {
      templateList.value = res.content.map(template => ({
        id: template.offerTemplatesId,
        name: template.templateName,
        fileUrl: template.templateFilePath,
        thumbnail: template.templateImg,
        preview: template.templateImg
      }))
    } else {
      ElMessage.error(res.msg || '获取模板列表失败')
    }
  } catch (error) {
    console.error('获取模板列表失败:', error)
    ElMessage.error('获取模板列表失败')
  }
}

// 修改选择模板的方法
const selectTemplate = () => {
  getTemplateList()
  uploadOfferDialogVisible.value = false // 关闭上传对话框
  templateDialogVisible.value = true
}

// 修改 confirmTemplate 函数中的配置更新部分
const confirmTemplate = async () => {
  const loading = ElLoading.service({
    lock: true,
    text: '加载模板中...',
    background: 'rgba(0, 0, 0, 0.7)'
  })

  try {
    const template = templateList.value.find(t => t.id === selectedTemplate.value)
    if (!template) {
      ElMessage.error('请选择模板')
      loading.close()
      return
    }

    templateDialogVisible.value = false
    wordEditDialogVisible.value = true

    // 更新配置
    documentConfig.value = {
      ...documentConfig.value,
      document: {
        ...documentConfig.value.document,
        key: `template-${template.id}-${Date.now()}`,
        title: template.name,
        url: template.fileUrl,
        fileType: "docx"
      }
    }

    loading.close()
  } catch (error) {
    console.error('加载模板失败:', error)
    ElMessage.error('加载模板失败')
    loading.close()
  }
}

// 修改文档服务器地址
const documentServerUrl = ref('http://127.0.0.1')

// 修改保存函数
const saveWordEdit = async () => {
  const loading = ElLoading.service({
    lock: true,
    text: '保存中...',
    background: 'rgba(0, 0, 0, 0.7)'
  })

  try {
    if (window.DocEditor) {
      window.DocEditor.execCommand('save')
    }

    wordEditDialogVisible.value = false
    getOffersList()
    loading.close()
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
    loading.close()
  }
}

// 监听模板对话框关闭
watch(templateDialogVisible, (newVal) => {
  if (!newVal) {
    selectedTemplate.value = null
  }
})

// 添加 OnlyOffice 事件处理函数
const onDocumentReady = () => {
  console.log('Document is ready')
}

const onDocumentError = (event) => {
  console.error('Document error:', event)
  if (event.data) {
    ElMessage.error(`文档加载失败：${event.data}`)
  }
}

onMounted(() => {
  if (!jobId) {
    ElMessage.error('缺少职位ID参数')
    return
  }
  getResumeList()
  getInterviewList()
  if (activeTab.value === 'offer') {
    getOffersList()
  }
})

onBeforeUnmount(() => {
  if (pdfUrl.value) {
    URL.revokeObjectURL(pdfUrl.value)
  }
  // 清理fabric画布
  if (editor.value) {
    editor.value.destroy()
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
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  margin: 0 20px;
  overflow-x: auto;
}

:deep(.custom-table) {
  border: none !important;
  padding: 12px;
  width: 100% !important;
  table-layout: fixed !important;
}

:deep(.el-table__inner-wrapper) {
  width: 100% !important;
}

:deep(.el-table__body) {
  width: 100% !important;
}

:deep(.el-table__header) {
  width: 100% !important;
}

:deep(.el-table) {

  /* 去掉表格边框 */
  &::before,
  &::after {
    display: none;
  }
}

:deep(.custom-table th.el-table__cell) {
  background-color: #F7F8FA !important;
  border: none;
  color: #909399;
  font-weight: 500;
  padding: 12px 8px;
  font-size: 14px;
}

:deep(.custom-table td.el-table__cell) {
  background-color: transparent;
  border: none;
  padding: 16px 8px;
  font-size: 14px;
  color: #606266;
}




:deep(.el-table .cell) {
  &:empty::before {
    content: '--';
    color: #000000;
  }
}

:deep(.custom-table .el-table__row) {
  border-radius: 4px;
  margin: 4px 0;
}

:deep(.custom-table .el-table__row:hover > td.el-table__cell) {
  background-color: #F7F8FA !important;
}


:deep(.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell) {
  background-color: transparent;
}


:deep(.el-table .cell) {
  padding: 0 8px;
  line-height: 24px;
}

:deep(.el-table__row) {
  &:hover {
    transform: none;
    box-shadow: none;
  }
}

.pagination-container {
  padding: 24px;
  display: flex;
  justify-content: center;
  background: transparent;
  margin-top: 20px;
}

:deep(.el-pagination) {
  padding: 0;
  font-weight: normal;
}

:deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
  background-color: #409eff;
  border-radius: 4px;
}

:deep(.el-pagination.is-background .el-pager li) {
  margin: 0 4px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.action-buttons {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

:deep(.action-buttons .el-button) {
  padding: 4px 8px;
  height: 24px;
  line-height: 16px;
  font-size: 13px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

:deep(.el-tag) {
  border-radius: 4px;
  padding: 0 8px;
  height: 24px;
  line-height: 24px;
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
  padding: 0 20px 20px;
  width: 100%;
}

.offer-header {
  padding: 0 20px 20px;
  width: 100%;
}

.offer-header .pagination-container {
  margin-top: 20px;
}

:deep(.el-table .cell) {
  padding: 0 12px;
  line-height: 24px;
}

:deep(.el-table__row) {
  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  }
}

:deep(.el-button--primary.is-link:not(.is-disabled)) {
  &:hover {
    color: #409eff;
    background: rgba(64, 158, 255, 0.1);
    border-radius: 4px;
  }
}

:deep(.el-table__empty-block) {
  width: 100% !important;
  min-height: 200px;
}

:deep(.el-table__empty-text) {
  width: 100%;
}

.upload-options {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 20px 0;
}

.upload-options .el-button {
  width: 140px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.word-edit-container {
  height: 800px;
  background: #fff;
  overflow: hidden;
}

/* 确保编辑器容器填满对话框 */
:deep(.onlyoffice-editor) {
  width: 100%;
  height: 100%;
}

.template-list {
  padding: 20px 0;
}

.template-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.template-item:hover {
  border-color: #409eff;
  transform: translateY(-2px);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.template-item.active {
  border-color: #409eff;
  background-color: rgba(64, 158, 255, 0.1);
}

.template-preview {
  width: 100%;
  height: 200px;
  border-radius: 4px;
  overflow: hidden;
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.template-preview .el-image {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 修改为 cover 以确保图片填满容器 */
}

.image-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  color: #909399;
  font-size: 40px;
}

.template-info {
  margin-top: 12px;
}

.template-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.template-desc {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
}
</style>
