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
                  <el-table-column label="序号" type="index" width="70" align="center" :index="calculateIndex" />
                  <el-table-column prop="resumeName" label="简历名称" width="140" show-overflow-tooltip align="center" />
                  <el-table-column prop="resumeType" label="简历类型" width="120" align="center">
                    <template #default="scope">
                      <el-tag :type="scope.row.resumeType == 0 ? 'success' : 'primary'">
                        {{ scope.row.resumeType == 0 ? '在线简历' : '附件简历' }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column prop="resumeStatus" label="投递状态" width="450" align="center">
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
                          v-if="scope.row.resumeDelete !== '0' & scope.row.resumeStatus !== '0' & scope.row.resumeStatus != '1'&scope.row.resumeStatus != '6'">
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
                <el-table-column label="面试链接" prop="interviewUrl" width="160" align="center" />
                <el-table-column label="地点" prop="interviewRegion" width="160" align="center"/>
                <el-table-column label="类型" prop="interviewType" width="90" align="center">
                  <template #default="scope">
                    <el-tag :type="scope.row.interviewType == '0' ? 'success' : 'primary'">
                      {{ typeMapping[scope.row.interviewType] }}
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
                      <el-button type="primary" link @click="editInterview(scope.row)"
                        v-if="scope.row.interviewStatus == '1' && scope.row.interviewResult == '0'">
                        修改
                      </el-button>

                      <el-button type="danger" link @click="viewInterview(scope.row)"
                        v-if="scope.row.interviewStatus == '1' && scope.row.interviewResult == '0'">
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
            <el-alert title="提示: offer发送后，应聘者进行签字确认，方可生效。然后等待材料发送进行审核" type="info" :closable="false" show-icon
              style="margin-bottom: 15px;" />
            <div class="table-section" style="min-width: 830px; max-width: 100%;">
              <el-table :data="offersList" border stripe class="custom-table" row-key="offerId">
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
                      <el-button type="primary" link @click="viewOffer(scope.row)"
                        v-if="scope.row.offersStatus === '0'">
                        上传offer
                      </el-button>
                      <el-button type="primary" link @click="viewOffer(scope.row)"
                        v-if="scope.row.offersStatus === '1'">
                        查看
                      </el-button>

                      <el-button type="danger" link @click="cancelOffer(scope.row)"
                        v-if="scope.row.offersStatus && scope.row.offersStatus !== '2' && scope.row.offersStatus !== '3' && scope.row.offersStatus !== '6'">
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
          <el-tab-pane label="材料审核" name="material">
            <div class="table-section" style="min-width: 830px; max-width: 100%;">
              <el-table :data="materialList" border stripe class="custom-table" row-key="materialId">
                <el-table-column label="序号" type="index" width="100" align="center" />
                <el-table-column label="应聘者" prop="userName" width="180" align="center" />
                <el-table-column label="提交时间" prop="sendTime" width="180" align="center">
                  <template #default="scope">
                    {{ scope.row.sendTime }}
                  </template>
                </el-table-column>
                <el-table-column label="状态" prop="status" width="120" align="center">
                  <template #default="scope">
                    <el-tag :type="getMaterialStatusType(scope.row.status)">
                      {{ getMaterialStatusText(scope.row.status) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="250" align="center">
                  <template #default="scope">
                    <div class="action-buttons">
                      <template v-if="scope.row.status === '3'">
                        <el-button type="success" link @click="handleMaterialPass(scope.row)">
                          通过
                        </el-button>
                        <el-button type="danger" link @click="handleMaterialReject(scope.row)">
                          拒绝
                        </el-button>
                      </template>
                      <el-button type="primary" v-if="scope.row.status != '0'" link @click="viewMaterial(scope.row)">
                        查看
                      </el-button>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
              <div class="pagination-container">
                <el-pagination v-model:currentPage="materialPageNum" v-model:pageSize="materialPageSize"
                  :pageSizes="[10, 20, 30, 50]" layout="total, sizes, prev, pager, next" :total="materialTotal"
                  @size-change="handleMaterialSizeChange" @current-change="handleMaterialPageChange" />
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="信息录入" name="info">
            <div class="table-section" style="min-width: 830px; max-width: 100%;">
              <el-table :data="registrationList" border stripe class="custom-table" row-key="id">
                <el-table-column label="序号" type="index" width="60" align="center" />
                <el-table-column label="姓名" prop="userName" width="120" align="center" />
                <el-table-column label="性别" prop="gender" width="80" align="center" />
                <el-table-column label="职位" prop="position" width="120" align="center" />
                <el-table-column label="入职日期" prop="hireDate" width="120" align="center" />
                <el-table-column label="联系电话" prop="phoneNumber" width="120" align="center" />
                <el-table-column label="学历" prop="educationLevel" width="60" align="center" />

                <el-table-column label="状态" prop="registrationStatus" width="90" align="center">
                  <template #default="scope">
                    <el-tag :type="getRegistrationStatusType(scope.row.registrationStatus)">
                      {{ getRegistrationStatusText(scope.row.registrationStatus) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="200" fixed="right" align="center">
                  <template #default="scope">
                    <div class="action-buttons">
                      <el-button type="primary" link @click="viewRegistration(scope.row)">
                        查看
                      </el-button>
                      <template v-if="scope.row.registrationStatus === '1'">
                        <el-button type="success" link @click="handleRegistrationPass(scope.row)">
                          通过
                        </el-button>
                        <el-button type="danger" link @click="handleRegistrationReject(scope.row)">
                          拒绝
                        </el-button>
                      </template>

                      <el-button type="primary" link @click="downloadRegistrationFile(scope.row)"
                        v-if="scope.row.registrationStatus === '2'">
                        下载附件
                      </el-button>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
              <div class="pagination-container">
                <el-pagination v-model:currentPage="registrationPageNum" v-model:pageSize="registrationPageSize"
                  :pageSizes="[10, 20, 30, 50]" layout="total, sizes, prev, pager, next" :total="registrationTotal"
                  @size-change="handleRegistrationSizeChange" @current-change="handleRegistrationPageChange" />
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="报到预约" name="checkin">
            <div class="table-section" style="min-width: 830px; max-width: 100%;">
              <el-table :data="checkinList" border stripe class="custom-table" row-key="id">
                <el-table-column label="序号" type="index" width="80" align="center" />
                <el-table-column label="姓名" prop="userName" width="120" align="center" />
                <el-table-column label="性别" prop="gender" width="80" align="center" />
                <el-table-column label="手机号码" prop="phoneNumber" width="130" align="center" />
                <el-table-column label="邮箱地址" prop="email" width="180" align="center" show-overflow-tooltip />
                <el-table-column label="入职日期" prop="hireDate" width="120" align="center" />
                <el-table-column label="学历" prop="educationLevel" width="100" align="center" />
                <el-table-column label="状态" prop="reservationStatus" width="100" align="center">
                  <template #default="scope">
                    <el-tag :type="getCheckinStatusType(scope.row.reservationStatus)">
                      {{ getCheckinStatusText(scope.row.reservationStatus) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="180" fixed="right" align="center">
                  <template #default="scope">
                    <div class="action-buttons">
                      <el-button type="primary" link v-if="scope.row.reservationStatus === '0'"
                        @click="openCheckinDialog(scope.row)">
                        设置
                      </el-button>

                      <el-button type="success" link disabled v-else>
                        已设置
                      </el-button>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
              <div class="pagination-container">
                <el-pagination v-model:currentPage="checkinPageNum" v-model:pageSize="checkinPageSize"
                  :pageSizes="[10, 20, 30, 50]" layout="total, sizes, prev, pager, next" :total="checkinTotal"
                  @size-change="handleCheckinSizeChange" @current-change="handleCheckinPageChange" />
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 添加PDF预览对话框 -->
      <el-dialog v-model="pdfDialogVisible" title="简历预览" width="80%" :destroy-on-close="true"
        :close-on-click-modal="false" :style="{ marginLeft: '15%' }">
        <div class="pdf-container">
          <iframe v-if="pdfUrl" :src="pdfUrl" width="100%" height="600px" frameborder="0"></iframe>
        </div>
      </el-dialog>

      <el-dialog v-model="interviewDialogVisible" title="面试邀约" width="30%" :destroy-on-close="true"
        :close-on-click-modal="false">
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

          <el-form-item label="面试链接" prop="interviewUrl" v-if="interviewData.interviewType == '0'">
            <el-input v-model="interviewData.interviewUrl" placeholder="请输入线上面试地址" />
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
              <el-option label="线上" :value="0" />
              <el-option label="线下" :value="1" />
            </el-select>
          </el-form-item>
          <el-form-item label="结果" prop="interviewResult">
            <el-select v-model="editInterviewData.interviewResult" placeholder="请选择结果">
              <el-option label="待定" value="0" />
              <el-option label="通过" value="1" />
              <el-option label="未通过" value="2" />
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
          <el-upload class="upload-pdf" :data="{ offerId: currentOfferId }" :show-file-list="false"
            :before-upload="beforeUpload" :on-success="handleUploadSuccess" :on-error="handleUploadError" accept=".pdf">
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
                请上传.pdf文件
              </div>
            </template>
          </el-upload>
        </div>
      </el-dialog>



      <el-dialog v-model="pdfEditDialogVisible" title="offer信息填写" :fullscreen="false" width="85%"
        :destroy-on-close="true" :close-on-click-modal="false" class="offer-dialog"
        :style="{ marginLeft: '14%', marginTop: '10vh' }">
        <div class="offer-edit-container">
          <div class="offer-form-section">
            <el-form :model="offerForm" ref="offerFormRef" label-width="120px" size="large">
              <el-form-item label="公司名称" prop="companyName">
                <el-input v-model="offerForm.companyName" placeholder="请输入公司名称" />
              </el-form-item>
              <el-form-item label="求职者名称" prop="userName">
                <el-input v-model="offerForm.userName" placeholder="请输入求职者名称" />
              </el-form-item>
              <el-form-item label="工作时间" prop="workTime">
                <el-input v-model="offerForm.workTime" placeholder="请输入工作时间" />
              </el-form-item>
              <el-form-item label="职位" prop="position">
                <el-input v-model="offerForm.position" placeholder="请输入职位" />
              </el-form-item>
              <el-form-item label="薪资" prop="salary">
                <el-input v-model="offerForm.salary" placeholder="请输入薪资" />
              </el-form-item>
              <el-form-item label="福利待遇" prop="welfare">
                <el-input v-model="offerForm.welfare" placeholder="请输入福利待遇" />
              </el-form-item>
              <el-form-item label="工作地点" prop="workLocation">
                <el-input v-model="offerForm.workLocation" placeholder="请输入工作地点" />
              </el-form-item>
              <el-form-item label="报道材料" prop="material">
                <el-input type="textarea" v-model="offerForm.material" placeholder="请输入报道材料" :rows="6" />
              </el-form-item>
            </el-form>
          </div>
          <div class="offer-preview-section">
            <div class="preview-header">
              <span>模板预览</span>
            </div>
            <div class="preview-content">
              <iframe v-if="selectedTemplateUrl" :src="selectedTemplateUrl + '#toolbar=0&view=FitH'" width="100%"
                height="100%" frameborder="0"></iframe>
              <div v-else class="no-preview">
                <el-icon>
                  <Document />
                </el-icon>
                <span>暂无预览</span>
              </div>
            </div>
          </div>
        </div>
        <template #footer>
          <el-button @click="pdfEditDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitOffer">发送</el-button>
        </template>
      </el-dialog>

      <!-- 添加模板选择对话框 -->
      <el-dialog v-model="templateDialogVisible" title="选择模板" width="70%">
        <div class="template-list">
          <el-row :gutter="20">
            <el-col v-for="template in templateList" :key="template.id" :span="6">
              <div class="template-item" @click="handleTemplateSelect(template)"
                :class="{ 'active': selectedTemplate === template.id }">
                <div class="template-preview">
                  <el-image :src="template.thumbnail" fit="cover" :preview-src-list="[template.preview]"
                    :initial-index="0" :preview-teleported="true" :zoom-rate="1.2" :close-on-press-escape="true"
                    preview-teleported>
                    <template #error>
                      <div class="image-placeholder">
                        <el-icon>
                          <Document />
                        </el-icon>
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
          <el-button type="primary" @click="(confirmTemplate)" :disabled="!selectedTemplate">确定</el-button>
        </template>
      </el-dialog>

      <!-- 添加签名预览对话框 -->
      <el-dialog v-model="signatureDialogVisible" title="签名预览" width="30%" center>
        <div class="signature-container">
          <el-image :src="currentSignature" fit="contain" style="width: 100%;">
            <template #error>
              <div class="image-error">
                <el-icon>
                  <Picture />
                </el-icon>
                <div>加载失败</div>
              </div>
            </template>
          </el-image>
        </div>
        <template #footer>
          <div class="dialog-footer">
            <el-button type="primary" @click="handleSignatureConfirm">通过</el-button>
            <el-button type="danger" @click="handleSignatureReject">打回</el-button>
          </div>
        </template>
      </el-dialog>

      <!-- 添加材料预览对话框 -->
      <el-dialog v-model="materialDialogVisible" title="材料预览" width="70%" center>
        <div class="material-container">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="身份证">
              <div class="preview-item">
                <el-image v-if="currentMaterial.identityCard" :src="currentMaterial.identityCard" fit="contain"
                  :preview-src-list="[currentMaterial.identityCard]" />
                <span v-else>未上传</span>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="体检报告">
              <div class="preview-item">
                <template v-if="currentMaterial.physicalExamination">
                  <el-button type="primary" link @click="openPdfFile(currentMaterial.physicalExamination)">
                    查看PDF
                  </el-button>
                </template>
                <span v-else>未上传</span>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="学历证书">
              <div class="preview-item">
                <el-image v-if="currentMaterial.diploma" :src="currentMaterial.diploma" fit="contain"
                  :preview-src-list="[currentMaterial.diploma]" />
                <span v-else>未上传</span>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="证件照">
              <div class="preview-item">
                <el-image v-if="currentMaterial.identificationPhoto" :src="currentMaterial.identificationPhoto"
                  fit="contain" :preview-src-list="[currentMaterial.identificationPhoto]" />
                <span v-else>未上传</span>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="银行卡">
              <div class="preview-item">
                <el-image v-if="currentMaterial.bankCard" :src="currentMaterial.bankCard" fit="contain"
                  :preview-src-list="[currentMaterial.bankCard]" />
                <span v-else>未上传</span>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="资格证">
              <div class="preview-item">
                <el-image v-if="currentMaterial.qualificationCertificate"
                  :src="currentMaterial.qualificationCertificate" fit="contain"
                  :preview-src-list="[currentMaterial.qualificationCertificate]" />
                <span v-else>未上传</span>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="离职证明">
              <div class="preview-item">
                <el-image v-if="currentMaterial.resignCertificate" :src="currentMaterial.resignCertificate"
                  fit="contain" :preview-src-list="[currentMaterial.resignCertificate]" />
                <span v-else>未上传</span>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="其他材料">
              <div class="preview-item">
                <template v-if="currentMaterial.other && currentMaterial.other.length > 0">
                  <el-carousel height="150px" :autoplay="false" indicator-position="none"
                    v-if="currentMaterial.other.length > 1" class="material-carousel">
                    <el-carousel-item v-for="(url, index) in currentMaterial.other" :key="index">
                      <el-image :src="url" fit="contain" :preview-src-list="currentMaterial.other"
                        :initial-index="index" :preview-teleported="true" class="material-image" />
                    </el-carousel-item>
                  </el-carousel>
                  <el-image v-else :src="currentMaterial.other[0]" fit="contain"
                    :preview-src-list="currentMaterial.other" :preview-teleported="true" class="material-image" />
                </template>
                <span v-else>未上传</span>
              </div>
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </el-dialog>

      <!-- 修改入职信息详情对话框 -->
      <el-dialog v-model="registrationDialogVisible" title="入职信息详情" width="60%" center>
        <div class="registration-container">
          <el-descriptions :column="2" border>

            <el-descriptions-item label="姓名" class="info-item">
              <span class="info-content">{{ currentRegistration.userName || '未填写' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="性别" class="info-item">
              <span class="info-content">{{ currentRegistration.gender || '未填写' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="出生日期" class="info-item">
              <span class="info-content">{{ currentRegistration.birthDate || '未填写' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="身份证号" class="info-item">
              <span class="info-content">{{ currentRegistration.idCardNumber || '未填写' }}</span>
            </el-descriptions-item>


            <el-descriptions-item label="手机号码" class="info-item">
              <span class="info-content">{{ currentRegistration.phoneNumber || '未填写' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="邮箱地址" class="info-item">
              <span class="info-content">{{ currentRegistration.email || '未填写' }}</span>
            </el-descriptions-item>

            <el-descriptions-item label="入职日期" class="info-item">
              <span class="info-content">{{ currentRegistration.hireDate || '未填写' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="职位" class="info-item">
              <span class="info-content">{{ currentRegistration.position || '未填写' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="状态" class="info-item">
              <el-tag :type="getRegistrationStatusType(currentRegistration.registrationStatus)">
                {{ getRegistrationStatusText(currentRegistration.registrationStatus) }}
              </el-tag>
            </el-descriptions-item>


            <el-descriptions-item label="学历" class="info-item">
              <span class="info-content">{{ currentRegistration.educationLevel || '未填写' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="毕业学校" class="info-item">
              <span class="info-content">{{ currentRegistration.schoolName || '未填写' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="银行账号" class="info-item">
              <span class="info-content">{{ currentRegistration.bankAccount || '未填写' }}</span>
            </el-descriptions-item>

            <el-descriptions-item label="紧急联系人" :span="2" class="info-item">
              <span class="info-content">{{ currentRegistration.emergencyContact || '未填写' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="家庭地址" :span="2" class="info-item">
              <span class="info-content">{{ currentRegistration.address || '未填写' }}</span>
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </el-dialog>

      <!-- 添加入职日期设置对话框 -->
      <el-dialog v-model="checkinDialogVisible" title="设置入职日期" width="30%" :destroy-on-close="true">
        <el-form :model="checkinForm" ref="checkinFormRef" label-width="100px">
          <el-form-item label="入职日期" prop="checkinDate">
            <el-date-picker v-model="checkinForm.checkinDate" type="datetime" placeholder="请选择入职日期"
              format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DD HH:mm:ss" :disabled-date="disabledDate"
              :disabled-time="disabledTime" />
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="checkinDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitCheckin">确定</el-button>
          </span>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Calendar, Document, Upload, Picture } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { companyResumeList, selectResume, updateResumeStatus, sendInvitationData, selectInterviewList, updateInterviewList, selectOffersList, selectOfferTemplate, updateOfferStatus, selectMaterial, selectMaterialDetail, updateMaterialStatus, selectRegistration, updateRegistrationStatus, uploadWithThumbnail, downloadRegistrationPdf, postMessage, sendOffer } from '@/api/company/companyApi'





const currentOfferId = ref(null)
const currentUserId = ref(null)


const pdfEditDialogVisible = ref(false)

const editor = ref(null)


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
  } else if (newTab === 'material') {
    getMaterialList()
  } else if (newTab === 'info') {
    getRegistrationList()
  } else if (newTab === 'checkin') {
    getCheckinList()
  }
}, { immediate: true });



const interviewData = ref({
  userId: null,
  jobId: null,
  interviewRegion: '',
  interviewType: '1',
  interviewTime: 30,
  interviewUrl: ''
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
  jobId: '',
  interviewUrl: ''
})





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
    '3': 'warning',
    '4': 'success',
    '5': 'danger',
    '6': 'success'
  }
  return statusMap[status] || 'info'
}

const getOfferStatusText = (status) => {
  const statusMap = {
    '0': '待发送',
    '1': '已接受',
    '2': '已拒绝',
    '3': '已撤销',
    '4': '已发送',
    '5': '打回',
    '6': '通过'
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
      ElMessage.success('发送邀约成功,面试时间请及时设置')
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

    interviewResult: row.interviewResult,
    interviewTime: row.interviewTime,
    interviewStatus: row.interviewStatus,
    jobResumeId: row.jobResumeId,
    userId: row.userId,
    jobId: row.jobId
  }


  editInterviewDialogVisible.value = true
}
const typeMapping = {
  '0': '线上',
  '1': '线下'
};
const resultMapping = {
  '1': '通过',
  '2': '未通过'
};
const submitEditInterview = async () => {
  try {
    const result = await updateInterviewList(editInterviewData.value);
    if (result.code === 200) {
      await postMessage({
        userId: editInterviewData.value.userId,
        content: "您的面试信息已被更新请及时查看 —此消息来自系统自动发送"
      })
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
  if (row.offersStatus === '0') {
    currentOfferId.value = row.offerId
    currentUserId.value = row.userId
    uploadOfferDialogVisible.value = true
  } else if (row.offersStatus === '1') {
    currentOfferId.value = row.offerId
    currentUserId.value = row.userId

    currentSignature.value = row.signaturePath
    signatureDialogVisible.value = true
  } else if (row.offersFilePath) {
    openPdfFile(row.offersFilePath)
  }
}


const handleOfferStatus = async (offerId, status, jobId, userId) => {
  try {
    const result = userId ?
      await updateOfferStatus(offerId, status, jobId, userId) :
      await updateOfferStatus(offerId, status, jobId);

    if (result.code == 200) {
      ElMessage.success('状态更新成功')
      getOffersList()
    } else {
      ElMessage.error(result.msg || '状态更新失败')
    }
  } catch (error) {
    console.error('更新offer状态失败:', error)
    ElMessage.error('更新offer状态失败')
  }
}

const cancelOffer = async (row) => {
  try {
    await ElMessageBox.confirm('确认撤销该offer？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await handleOfferStatus(row.offerId, '3', jobId)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('撤销offer失败:', error)
      ElMessage.error('撤销失败')
    }
  }
}


const templateDialogVisible = ref(false)
const selectedTemplate = ref(null)
const templateList = ref([])


const handleTemplateSelect = (template) => {
  selectedTemplate.value = template.id
}


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

const selectTemplate = () => {
  getTemplateList()
  uploadOfferDialogVisible.value = false
  templateDialogVisible.value = true
}


const confirmTemplate = async () => {
  const template = templateList.value.find(t => t.id === selectedTemplate.value)
  if (!template) {
    ElMessage.error('请选择模板')
    return
  }

  templateDialogVisible.value = false
  selectedTemplateUrl.value = template.fileUrl
  pdfEditDialogVisible.value = true
}






const signatureDialogVisible = ref(false)
const currentSignature = ref('')


const handleSignatureConfirm = async () => {
  try {
    await handleOfferStatus(currentOfferId.value, "6", jobId, currentUserId.value)
    signatureDialogVisible.value = false
    ElMessage.success('已通过')
    getOffersList()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleSignatureReject = async () => {
  try {

    const result = await handleOfferStatus(currentOfferId.value, '5', jobId)


    signatureDialogVisible.value = false
    postMessage({
      userId: currentUserId.value,
      content: "您的签名有误,请重新签字 —此消息来自系统自动发送"
    })
    ElMessage.success('已打回')
    getOffersList()


  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const materialList = ref([])
const materialTotal = ref(0)
const materialPageNum = ref(1)
const materialPageSize = ref(10)

// 获取材料状态类型
const getMaterialStatusType = (status) => {
  const statusMap = {
    '0': 'info',
    '1': 'success',
    '2': 'danger',
    '3': 'warning'
  }
  return statusMap[status] || 'info'
}

// 获取材料状态文本
const getMaterialStatusText = (status) => {
  const statusMap = {
    '0': '待提交',
    '1': '通过',
    '2': '未通过',
    '3': '待审核'
  }
  return statusMap[status] || '未知'
}

// 获取材料列表
const getMaterialList = async () => {
  try {
    const res = await selectMaterial(materialPageNum.value, materialPageSize.value, jobId)
    if (res.code === 200) {
      materialList.value = res.content?.records
      materialTotal.value = res.content?.total
    } else {
      ElMessage.error(res.msg || '获取材料列表失败')
    }
  } catch (error) {
    console.error('获取材料列表失败:', error)
    ElMessage.error('获取材料列表失败')
  }
}

// 添加新的响应式变量
const materialDialogVisible = ref(false)
const currentMaterial = ref({
  identityCard: '',
  physicalExamination: '',
  diploma: '',
  identificationPhoto: '',
  bankCard: '',
  qualificationCertificate: '',
  resignCertificate: '',
  other: []
})

// 修改查看材料方法
const viewMaterial = async (row) => {
  try {
    const res = await selectMaterialDetail(row.materialId)
    if (res.code === 200) {
      // 直接使用后端返回的数组
      currentMaterial.value = {
        identityCard: res.content.identityCard,
        physicalExamination: res.content.physicalExamination,
        diploma: res.content.diploma,
        identificationPhoto: res.content.identificationPhoto,
        bankCard: res.content.bankCard,
        qualificationCertificate: res.content.qualificationCertificate,
        resignCertificate: res.content.resignCertificate,
        other: Array.isArray(res.content.other) ? res.content.other : []
      }
      materialDialogVisible.value = true
    } else {
      ElMessage.error(res.msg || '获取材料详情失败')
    }
  } catch (error) {
    console.error('获取材料详情失败:', error)
    ElMessage.error('获取材料详情失败')
  }
}

// 处理分页大小变化
const handleMaterialSizeChange = (val) => {
  materialPageSize.value = val
  getMaterialList()
}

// 处理页码变化
const handleMaterialPageChange = (val) => {
  materialPageNum.value = val
  getMaterialList()
}

// 处理材料通过
const handleMaterialPass = async (row) => {
  try {
    await ElMessageBox.confirm('确认通过该材料？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const result = await updateMaterialStatus(row.materialId, '1')
    if (result.code === 200) {
      ElMessage.success('审核通过')
      getMaterialList()
    } else {
      ElMessage.error(result.msg || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 处理材料拒绝
const handleMaterialReject = async (row) => {
  try {
    await ElMessageBox.confirm('确认拒绝该材料？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const result = await updateMaterialStatus(row.materialId, '2')
    if (result.code === 200) {
      postMessage({
        userId: row.userId,
        content: "您的材料审核未通过,请重新发送 —此消息来自系统自动发送"
      })
      ElMessage.success('已拒绝')
      getMaterialList()
    } else {
      ElMessage.error(result.msg || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 添加新的方法来处理 PDF 打开
const openPdfFile = (url) => {
  if (url) {
    window?.open(url)
  } else {
    ElMessage.warning('文件地址不存在')
  }
}

const registrationList = ref([])
const registrationTotal = ref(0)
const registrationPageNum = ref(1)
const registrationPageSize = ref(10)

// 获取入职信息列表
const getRegistrationList = async () => {
  try {
    const res = await selectRegistration(registrationPageNum.value, registrationPageSize.value, jobId, null)
    if (res.code === 200) {
      registrationList.value = res.content?.records
      registrationTotal.value = res.content?.total
    } else {
      ElMessage.error(res.msg || '获取入职信息列表失败')
    }
  } catch (error) {
    console.error('获取入职信息列表失败:', error)
    ElMessage.error('获取入职信息列表失败')
  }
}

// 处理分页大小变化
const handleRegistrationSizeChange = (val) => {
  registrationPageSize.value = val
  getRegistrationList()
}

// 处理页码变化
const handleRegistrationPageChange = (val) => {
  registrationPageNum.value = val
  getRegistrationList()
}

// 获取状态类型
const getRegistrationStatusType = (status) => {
  const statusMap = {
    '0': 'info',
    '1': 'warning',
    '2': 'success',
    '3': 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getRegistrationStatusText = (status) => {
  const statusMap = {
    '0': '待提交',
    '1': '待审核',
    '2': '通过',
    '3': '拒绝'
  }
  return statusMap[status] || '未知'
}

// 在其他对话框后面添加入职信息对话框
const registrationDialogVisible = ref(false)
const currentRegistration = ref({})

// 添加查看方法
const viewRegistration = (row) => {
  currentRegistration.value = { ...row }
  registrationDialogVisible.value = true
}

const checkinList = ref([])
const checkinTotal = ref(0)
const checkinPageNum = ref(1)
const checkinPageSize = ref(10)

// 处理分页大小变化
const handleCheckinSizeChange = (val) => {
  checkinPageSize.value = val
  getCheckinList()
}

// 处理页码变化
const handleCheckinPageChange = (val) => {
  checkinPageNum.value = val
  getCheckinList()
}

// 获取报到预约列表
const getCheckinList = async () => {
  try {

    const res = await selectRegistration(checkinPageNum.value, checkinPageSize.value, jobId, '2')
    if (res.code === 200) {
      checkinList.value = res.content?.records
      checkinTotal.value = res.content?.total
    } else {
      ElMessage.error(res.msg || '获取报到预约列表失败')
    }
  } catch (error) {
    console.error('获取报到预约列表失败:', error)
    ElMessage.error('获取报到预约列表失败')
  }
}

onMounted(() => {
  if (!jobId) {
    ElMessage.error('缺少职位ID参数')
    return
  }
  getResumeList()
  getInterviewList()
  // 根据当前激活的标签页加载数据
  if (activeTab.value === 'offer') {
    getOffersList()
  } else if (activeTab.value === 'material') {
    getMaterialList()
  } else if (activeTab.value === 'info') {
    getRegistrationList()
  } else if (activeTab.value === 'checkin') {
    getCheckinList()
  }
})

onBeforeUnmount(() => {

  if (pdfUrl.value) {
    URL.revokeObjectURL(pdfUrl.value)
  }

  // 安全地清理编辑器
  if (editor.value && typeof editor.value.destroy === 'function') {
    try {
      editor.value.destroy()
    } catch (error) {
      console.warn('清理编辑器时出错:', error)
    }
  }

  // 重置所有对话框状态
  pdfDialogVisible.value = false
  interviewDialogVisible.value = false
  editInterviewDialogVisible.value = false
  uploadOfferDialogVisible.value = false
  pdfEditDialogVisible.value = false
  templateDialogVisible.value = false
  signatureDialogVisible.value = false
  materialDialogVisible.value = false
  registrationDialogVisible.value = false
})

// 处理入职信息通过
const handleRegistrationPass = async (row) => {
  try {
    await ElMessageBox.confirm('确认通过该入职信息？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const result = await updateRegistrationStatus(row.id, '2')
    if (result.code === 200) {
      ElMessage.success('审核通过')
      getRegistrationList()
    } else {
      ElMessage.error(result.msg || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 处理入职信息拒绝
const handleRegistrationReject = async (row) => {
  try {
    await ElMessageBox.confirm('确认拒绝该入职信息？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const result = await updateRegistrationStatus(row.id, '3')
    if (result.code === 200) {
      ElMessage.success('已拒绝')
      getRegistrationList()
    } else {
      ElMessage.error(result.msg || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

const beforeUpload = async (file) => {
  try {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('offerId', currentOfferId.value)

    const result = await uploadWithThumbnail(formData)

    if (result.code === 200) {
      ElMessage.success('上传成功')
      getOffersList()
      uploadOfferDialogVisible.value = false
      return false // 阻止默认上传行为
    } else {
      ElMessage.error(result.msg || '上传失败')
      return false
    }
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error('上传失败')
    return false
  }
}




const getCheckinStatusType = (status) => {
  const statusMap = {
    '0': 'info',
    '1': 'warning',
    '2': 'success',
    '3': 'danger'
  }
  return statusMap[status] || 'info'
}

const getCheckinStatusText = (status) => {
  const statusMap = {
    '0': '待发送',
    '1': '已发送',
    '2': '已接受',
    '3': '已拒绝'
  }
  return statusMap[status] || '未知'
}

// 添加新的响应式变量
const checkinDialogVisible = ref(false)
const checkinForm = ref({
  checkinDate: '',
  userId: '',
  registrationId: ''
})
const checkinFormRef = ref(null)

// 打开设置对话框
const openCheckinDialog = (row) => {
  checkinForm.value = {
    checkinDate: '',
    userId: row.userId,
    registrationId: row.id
  }
  checkinDialogVisible.value = true
}



const submitCheckin = async () => {

  try {

    const dateStr = new Date(checkinForm.value.checkinDate)
      .toISOString()
      .split('T')[0]

    const result = await updateRegistrationStatus(
      checkinForm.value.registrationId,
      null,
      dateStr
    )

    if (result.code === 200) {
      ElMessage.success('入职日期设置成功')
      checkinDialogVisible.value = false
      getCheckinList() // 刷新列表
    } else {
      ElMessage.error(result.msg || '设置失败')
    }
  } catch (error) {
    console.error('设置入职日期失败:', error)
    ElMessage.error('设置失败')
  }
}

// 在 script setup 中添加以下方法
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7 // 禁用今天之前的日期
}

const disabledTime = (date) => {
  if (date && date.toDateString() === new Date().toDateString()) {
    const hours = new Date().getHours()
    const minutes = new Date().getMinutes()
    return {
      disabledHours: () => Array.from({ length: hours }, (_, i) => i),
      disabledMinutes: (hour) => hour === hours ? Array.from({ length: minutes }, (_, i) => i) : []
    }
  }
  return {}
}

const downloadRegistrationFile = async (row) => {
  try {
    const response = await downloadRegistrationPdf(row.id)

    if (response.code === 200 && response.content) {
      // 将 base64 转换为二进制数据
      const binaryString = atob(response.content)
      const bytes = new Uint8Array(binaryString.length)
      for (let i = 0; i < binaryString.length; i++) {
        bytes[i] = binaryString.charCodeAt(i)
      }

      // 创建 Blob 对象
      const blob = new Blob([bytes], { type: 'application/pdf' })

      // 创建下载链接
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `入职信息_${row.userName || '未命名'}.pdf`

      // 触发下载
      document.body.appendChild(link)
      link.click()

      // 清理
      document.body.removeChild(link)
      setTimeout(() => {
        window.URL.revokeObjectURL(url)
      }, 100)

      ElMessage.success('文件下载成功')
    } else {
      throw new Error(response.msg || '下载失败')
    }
  } catch (error) {
    console.error('下载文件失败:', error)
    ElMessage.error('下载文件失败')
  }
}

// 添加新的响应式变量
const offerForm = ref({
  companyName: '',
  userName: '',
  workTime: '',
  position: '',
  salary: '',
  welfare: '',
  workLocation: '',
  material: ''
})
const offerFormRef = ref(null)
const selectedTemplateUrl = ref('')

// 添加提交offer方法
const submitOffer = async () => {
  if (!offerFormRef.value) return

  try {
    await offerFormRef.value.validate()


    const offerData = {
      ...offerForm.value,
      offerId: currentOfferId.value,
      templatePath: selectedTemplateUrl.value
    }
    let resule = await sendOffer(offerData)
    if (resule.code === 200) {
      // 发送消息
      postMessage({
        userId: currentUserId.value,
        content: "您的offer已发送,请及时查收 —此消息来自系统自动发送"
      })

      // 更新offer状态
      handleOfferStatus(currentOfferId.value, '4', jobId)
      ElMessage.success('offer发送成功')
      getOffersList()
    }
    pdfEditDialogVisible.value = false
  } catch (error) {
    console.error('提交offer失败:', error)
    ElMessage.error('提交失败，请检查表单')
  }
}

const handleUploadError = (error) => {
  console.error('上传失败:', error)
  ElMessage.error('文件上传失败')
}

const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    ElMessage.success('文件上传成功')
    getOffersList()
    uploadOfferDialogVisible.value = false
  } else {
    ElMessage.error(response.msg || '上传失败')
  }
}


const interviewType = ref('')

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

.pdf-edit-container {
  height: 800px;
  background: #fff;
  overflow: hidden;
}


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
  object-fit: cover;
  /* 修改为 cover 以确保图片填满容器 */
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

.signature-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.image-error {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #909399;
  font-size: 14px;
  padding: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

:deep(.el-date-picker) {
  width: 100%;
}

.material-container {
  padding: 20px;
}

.preview-item {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 200px;
  height: 150px;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
  margin: 0 auto;
}

.material-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: 4px;
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.material-carousel {
  width: 100%;
  height: 100%;
}

:deep(.el-carousel__container) {
  height: 100% !important;
}

:deep(.el-carousel__item) {
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #fff;
}

:deep(.el-carousel__arrow) {
  transform: translateY(-50%);
  width: 24px;
  height: 24px;
  background-color: rgba(0, 0, 0, 0.3);

  &--left {
    left: -5px;
  }

  &--right {
    right: -5px;
  }
}

.other-materials-container {
  display: none;
}

.registration-container {
  padding: 20px;
  background-color: #fff;
}

:deep(.el-descriptions) {
  padding: 24px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

:deep(.el-descriptions__label) {
  width: 120px;
  padding: 16px 24px !important;
  background-color: #f7f8fa;
  font-weight: 500;
  color: #606266;
}

:deep(.el-descriptions__content) {
  padding: 16px 24px !important;
  color: #303133;
}

.info-item {
  line-height: 1.5;
}

.info-content {
  color: #606266;
  font-size: 14px;
}

:deep(.el-descriptions__body) {
  background-color: #fff;
}

:deep(.el-descriptions__cell) {
  padding: 0;
}

:deep(.el-tag) {
  font-weight: 500;
  padding: 0 12px;
  height: 28px;
  line-height: 26px;
}

:deep(.el-dialog__header) {
  margin-right: 0;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

:deep(.el-dialog__title) {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

:deep(.el-dialog__body) {
  padding: 0;
}

:deep(.el-dialog) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-descriptions__row) {
  border-bottom: 1px solid #f0f0f0;
}

:deep(.el-descriptions__row:last-child) {
  border-bottom: none;
}

.offer-edit-container {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.offer-form-section {
  flex: 1;
  margin-right: 20px;
}

.offer-preview-section {
  flex: 1;
}

.preview-header {
  margin-bottom: 10px;
  font-size: 16px;
  font-weight: 600;
}

.preview-content {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 10px;
}

.no-preview {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #909399;
  font-size: 14px;
}

.offer-dialog :deep(.el-dialog) {
  min-width: 1200px;
}

.offer-dialog :deep(.el-dialog__body) {
  padding: 30px;
  height: calc(100vh - 180px);
  /* 增加高度，减去header和footer的高度 */
}

.offer-edit-container {
  display: flex;
  justify-content: space-between;
  height: 100%;
  gap: 30px;
}

.offer-form-section {
  flex: 0 0 400px;
  /* 固定宽度 */
  padding: 30px;
  background: #f8f9fa;
  border-radius: 8px;
  overflow-y: auto;
  max-height: calc(100vh - 240px);
  /* 设置最大高度，确保可以滚动 */
}

.offer-preview-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 240px);
  /* 增加高度 */
  min-width: 0;
}

.preview-content {
  flex: 1;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
  height: 100%;
}

.preview-content iframe {
  width: 100%;
  height: 100%;
  border: none;
  display: block;
}

.no-preview {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #909399;
  gap: 10px;
}

.no-preview .el-icon {
  font-size: 48px;
}

.no-preview span {
  font-size: 16px;
}

.fullscreen-dialog :deep(.el-dialog) {
  margin: 0 !important;
  height: 100% !important;
  display: flex;
  flex-direction: column;
}

.fullscreen-dialog :deep(.el-dialog__body) {
  flex: 1;
  padding: 20px;
  height: calc(100vh - 120px);
  /* 减去header和footer的高度 */
  overflow: hidden;
}

.fullscreen-dialog :deep(.el-dialog__header) {
  padding: 20px;
  margin: 0;
  border-bottom: 1px solid #e4e7ed;
}

.fullscreen-dialog :deep(.el-dialog__footer) {
  padding: 20px;
  border-top: 1px solid #e4e7ed;
}

.offer-edit-container {
  display: flex;
  justify-content: space-between;
  height: 100%;
  gap: 30px;
}

.offer-form-section {
  flex: 0 0 500px;
  padding: 30px;
  background: #f8f9fa;
  border-radius: 8px;
  overflow-y: auto;
}

.offer-form-section :deep(.el-form-item) {
  margin-bottom: 30px;
}

.offer-form-section :deep(.el-input__wrapper) {
  padding: 8px 15px;
}

.offer-preview-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  /* 防止内容溢出 */
}

.preview-header {
  margin-bottom: 15px;
  padding: 0 10px;
  font-size: 18px;
  font-weight: 600;
}

.preview-content {
  flex: 1;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
}

.preview-content iframe {
  width: 100%;
  height: 100%;
  border: none;
}

.no-preview {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #909399;
  gap: 15px;
}

.no-preview .el-icon {
  font-size: 64px;
}

.no-preview span {
  font-size: 18px;
}

:deep(.offer-dialog .el-dialog) {
  z-index: 3000 !important;
}

:deep(.el-overlay) {
  z-index: 2999 !important;
}
</style>
