<template>
  <div class="resume-container">
    <el-card class="resume-card">
      <template #header>
        <div class="card-header">
          <h2>简历查看</h2>
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
                          'current': scope.row.resumeStatus === status.value || (scope.row.resumeStatus === '7' && status.value === '1'),
                          'deleted': scope.row.resumeDelete === '0',
                        }">
                          <div class="timeline-line"></div>
                          <div class="timeline-dot"></div>
                          <div class="timeline-text">{{ scope.row.resumeStatus === '7' && status.value === '1' ? '用户已撤销'
                            : status.label }}</div>
                        </div>
                      </div>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="200" fixed="right" align="center">
                    <template #default="scope">
                      <div class="action-buttons">
                        <el-button type="primary" link @click="viewResume(scope.row)"
                          v-if="scope.row.resumeStatus !== '7'">
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
                        <!-- <el-button type="danger" link @click="handleUnsuitable(scope.row)" v-if="scope.row.resumeDelete !== '0' && scope.row.resumeStatus !== '0' && scope.row.resumeStatus !== '1'
                          && scope.row.resumeStatus !== '6' && scope.row.resumeStatus !== '7'">
                          不合适
                        </el-button> -->
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
            <el-alert title="提示: 面试通过后发放offer，对方接受后请等待应聘者上传材料" type="info" :closable="false" show-icon
              style="margin-bottom: 15px;" />
            <div class="interview-header">
              <el-table :data="interviewList" border stripe class="custom-table" row-key="interviewId">
                <el-table-column label="简历名称" prop="resumeName" width="150" align="center" />
                <el-table-column label="时间" prop="interviewDate" width="150" align="center" />
                <el-table-column label="面试链接" prop="interviewUrl" width="160" align="center" />
                <el-table-column label="地点" prop="interviewRegion" width="160" align="center" />
                <el-table-column label="类型" prop="interviewType" width="90" align="center">
                  <template #default="scope">
                    <el-tag :type="scope.row.interviewType == '0' ? 'success' : 'primary'">
                      {{ typeMapping[scope.row.interviewType] }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="结果" prop="interviewResult" width="110" align="center">
                  <template #default="scope">
                    <el-tag
                      :type="scope.row.interviewResult == '1' ? 'success' : (scope.row.interviewResult == '2' ? 'danger' : 'info')">
                      {{ scope.row.interviewResult == '1' ? '通过' : (scope.row.interviewResult == '2' ? '未通过' : '待面试') }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="状态" prop="interviewStatus" width="120" align="center">
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
                <el-table-column label="操作" width="190" fixed="right" align="center">
                  <template #default="scope">
                    <div class="action-buttons">
                      <el-button type="primary" link @click="editInterview(scope.row)"
                        v-if="(scope.row.interviewStatus == '1' || scope.row.interviewStatus == '2') && scope.row.interviewResult == 0">
                        修改
                      </el-button>
                      <el-button type="success" link @click="setInterviewResult(scope.row)"
                        v-if="scope.row.interviewStatus == '1' && scope.row.interviewResult == 0">
                        面试结果
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
          <el-tab-pane label="offer发放" name="offer" v-if="false">
            <el-alert title="提示: offer发送后，对方接受后，等待对方发送材料进行审核" type="info" :closable="false" show-icon
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
          <el-tab-pane label="信息录入" name="info" v-if="false">
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
          <el-tab-pane label="报道预约" name="checkin">
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

                      <el-button type="primary" link  @click="viewRegistration(scope.row)">
                        查看
                      </el-button>

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
      <el-dialog v-model="editInterviewDialogVisible" title="修改面试信息" width="20%" :destroy-on-close="true"
        :close-on-click-modal="false">
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
          <!-- <el-form-item label="结果" prop="interviewResult">
            <el-select v-model="editInterviewData.interviewResult" placeholder="请选择结果">
              <el-option label="待定" value="0" />
              <el-option label="通过" value="1" />
              <el-option label="未通过" value="2" />
            </el-select>
          </el-form-item> -->
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
          <el-button @click="editInterviewDialogVisible = false">关闭</el-button>
          <el-button type="danger" @click="cancelInterview"
            v-if="editInterviewData.interviewStatus == '1' || editInterviewData.interviewStatus == '2'">
            取消面试
          </el-button>
        </template>
      </el-dialog>



      <!-- 添加签名预览对话框 -->
      <!-- <el-dialog v-model="signatureDialogVisible" title="签名预览" width="30%" center>
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
      </el-dialog> -->

      <!-- 添加材料预览对话框 -->
      <el-dialog v-model="materialDialogVisible" title="材料预览" width="80%" center :destroy-on-close="true"
        class="material-preview-dialog" :style="{ marginLeft: '15%' }">
        <div class="material-container">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="身份证">
              <div class="review-buttons"
                v-if="identityCardList.length && currentMaterial.status != '1' && currentMaterial.status != '2' && currentMaterial.identityCardStatus == '3'">
                <el-button type="success" size="small"
                  @click="reviewMaterial(currentMaterial.materialId, 1, '1')">通过</el-button>
                <el-button type="danger" size="small"
                  @click="reviewMaterial(currentMaterial.materialId, 1, '2')">拒绝</el-button>
              </div>
              <div class="preview-item">
                <template v-if="identityCardList.length > 0">
                  <el-image v-for="(url, idx) in identityCardList" :key="idx" :src="url" fit="contain"
                    :preview-src-list="identityCardList" :initial-index="idx" :preview-teleported="true"
                    :z-index="3100" class="material-image" style="width: 240px; height: 160px; margin: 0 10px;" />
                </template>
                <span v-else class="no-material">未上传</span>
              </div>
              <div v-if="currentMaterial.identityCardStatus === '1'" class="material-status">
                <el-tag type="success">已通过</el-tag>
              </div>
              <div v-if="currentMaterial.identityCardStatus === '2'" class="material-status">
                <el-tag type="danger">未通过</el-tag>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="体检报告">
              <div class="review-buttons"
                v-if="currentMaterial.physicalExamination && currentMaterial.status != '1' && currentMaterial.status != '2' && currentMaterial.physicalExaminationStatus == '3'">
                <el-button type="success" size="small"
                  @click="reviewMaterial(currentMaterial.materialId, 2, '1')">通过</el-button>
                <el-button type="danger" size="small"
                  @click="reviewMaterial(currentMaterial.materialId, 2, '2')">拒绝</el-button>
              </div>
              <div class="preview-item">
                <template v-if="currentMaterial.physicalExamination">
                  <el-button type="primary" link @click="openPdfFile(currentMaterial.physicalExamination)"
                    class="pdf-button">
                    <el-icon class="mr-5">
                      <Document />
                    </el-icon>查看PDF
                  </el-button>
                </template>
                <span v-else class="no-material">未上传</span>
              </div>
              <div v-if="currentMaterial.physicalExaminationStatus === '1'" class="material-status">
                <el-tag type="success">已通过</el-tag>
              </div>
              <div v-if="currentMaterial.physicalExaminationStatus === '2'" class="material-status">
                <el-tag type="danger">未通过</el-tag>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="学历证书">
              <div class="review-buttons"
                v-if="currentMaterial.diploma && currentMaterial.status != '1' && currentMaterial.status != '2' && currentMaterial.diplomaStatus == '3'">
                <el-button type="success" size="small"
                  @click="reviewMaterial(currentMaterial.materialId, 3, '1')">通过</el-button>
                <el-button type="danger" size="small"
                  @click="reviewMaterial(currentMaterial.materialId, 3, '2')">拒绝</el-button>
              </div>
              <div class="preview-item">
                <el-image v-if="currentMaterial.diploma" :src="currentMaterial.diploma" fit="contain"
                  :preview-src-list="[currentMaterial.diploma]" :initial-index="0" :preview-teleported="true"
                  :z-index="3100" class="material-image" style="width: 240px; height: 160px;" />
                <span v-else class="no-material">未上传</span>
              </div>
              <div v-if="currentMaterial.diplomaStatus === '1'" class="material-status">
                <el-tag type="success">已通过</el-tag>
              </div>
              <div v-if="currentMaterial.diplomaStatus === '2'" class="material-status">
                <el-tag type="danger">未通过</el-tag>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="证件照">
              <div class="review-buttons"
                v-if="currentMaterial.identificationPhoto && currentMaterial.status != '1' && currentMaterial.status != '2' && currentMaterial.identificationPhotoStatus == '3'">
                <el-button type="success" size="small"
                  @click="reviewMaterial(currentMaterial.materialId, 4, '1')">通过</el-button>
                <el-button type="danger" size="small"
                  @click="reviewMaterial(currentMaterial.materialId, 4, '2')">拒绝</el-button>
              </div>
              <div class="preview-item">
                <el-image v-if="currentMaterial.identificationPhoto" :src="currentMaterial.identificationPhoto"
                  fit="contain" :preview-src-list="[currentMaterial.identificationPhoto]" :initial-index="0"
                  :preview-teleported="true" :z-index="3100" class="material-image"
                  style="width: 240px; height: 160px;" />
                <span v-else class="no-material">未上传</span>
              </div>
              <div v-if="currentMaterial.identificationPhotoStatus === '1'" class="material-status">
                <el-tag type="success">已通过</el-tag>
              </div>
              <div v-if="currentMaterial.identificationPhotoStatus === '2'" class="material-status">
                <el-tag type="danger">未通过</el-tag>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="银行卡">
              <div class="review-buttons"
                v-if="bankCardList.length && currentMaterial.status != '1' && currentMaterial.status != '2' && currentMaterial.bankCardStatus == '3'">
                <el-button type="success" size="small"
                  @click="reviewMaterial(currentMaterial.materialId, 5, '1')">通过</el-button>
                <el-button type="danger" size="small"
                  @click="reviewMaterial(currentMaterial.materialId, 5, '2')">拒绝</el-button>
              </div>
              <div class="preview-item">
                <template v-if="bankCardList.length > 0">
                  <el-image v-for="(url, idx) in bankCardList" :key="idx" :src="url" fit="contain"
                    :preview-src-list="bankCardList" :initial-index="idx" :preview-teleported="true"
                    :z-index="3100" class="material-image" style="width: 240px; height: 160px; margin: 0 10px;" />
                </template>
                <span v-else class="no-material">未上传</span>
              </div>
              <div v-if="currentMaterial.bankCardStatus === '1'" class="material-status">
                <el-tag type="success">已通过</el-tag>
              </div>
              <div v-if="currentMaterial.bankCardStatus === '2'" class="material-status">
                <el-tag type="danger">未通过</el-tag>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="资格证">
              <div class="review-buttons"
                v-if="currentMaterial.qualificationCertificate && currentMaterial.status != '1' && currentMaterial.status != '2' && currentMaterial.qualificationCertificateStatus == '3'">
                <el-button type="success" size="small"
                  @click="reviewMaterial(currentMaterial.materialId, 6, '1')">通过</el-button>
                <el-button type="danger" size="small"
                  @click="reviewMaterial(currentMaterial.materialId, 6, '2')">拒绝</el-button>
              </div>
              <div class="preview-item">
                <el-image v-if="currentMaterial.qualificationCertificate"
                  :src="currentMaterial.qualificationCertificate" fit="contain"
                  :preview-src-list="[currentMaterial.qualificationCertificate]" :initial-index="0"
                  :preview-teleported="true" :z-index="3100" class="material-image"
                  style="width: 240px; height: 160px;" />
                <span v-else class="no-material">未上传</span>
              </div>
              <div v-if="currentMaterial.qualificationCertificateStatus === '1'" class="material-status">
                <el-tag type="success">已通过</el-tag>
              </div>
              <div v-if="currentMaterial.qualificationCertificateStatus === '2'" class="material-status">
                <el-tag type="danger">未通过</el-tag>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="离职证明">
              <div class="review-buttons"
                v-if="currentMaterial.resignCertificate && currentMaterial.status != '1' && currentMaterial.status != '2' && currentMaterial.resignCertificateStatus == '3'">
                <el-button type="success" size="small"
                  @click="reviewMaterial(currentMaterial.materialId, 7, '1')">通过</el-button>
                <el-button type="danger" size="small"
                  @click="reviewMaterial(currentMaterial.materialId, 7, '2')">拒绝</el-button>
              </div>
              <div class="preview-item">
                <el-image v-if="currentMaterial.resignCertificate" :src="currentMaterial.resignCertificate"
                  fit="contain" :preview-src-list="[currentMaterial.resignCertificate]" :initial-index="0"
                  :preview-teleported="true" :z-index="3100" class="material-image"
                  style="width: 240px; height: 160px;" />
                <span v-else class="no-material">未上传</span>
              </div>
              <div v-if="currentMaterial.resignCertificateStatus === '1'" class="material-status">
                <el-tag type="success">已通过</el-tag>
              </div>
              <div v-if="currentMaterial.resignCertificateStatus === '2'" class="material-status">
                <el-tag type="danger">未通过</el-tag>
              </div>
            </el-descriptions-item>
            <!-- 新增 offer签字 项 -->
            <el-descriptions-item label="offer签字">
              <div class="review-buttons"
                v-if="currentMaterial.signaturePath && currentMaterial.status != '1' && currentMaterial.status != '2' && currentMaterial.signatureStatus == '3'">
                <el-button type="success" size="small"
                  @click="reviewMaterial(currentMaterial.materialId, 8, '1')">通过</el-button>
                <el-button type="danger" size="small"
                  @click="reviewMaterial(currentMaterial.materialId, 8, '2')">拒绝</el-button>
              </div>
              <div class="preview-item">
                <el-image v-if="currentMaterial.signaturePath" :src="currentMaterial.signaturePath" fit="contain"
                  :preview-src-list="[currentMaterial.signaturePath]" :initial-index="0" :preview-teleported="true"
                  :z-index="3100" class="material-image" style="width: 240px; height: 160px;" />
                <span v-else class="no-material">未上传</span>
              </div>
              <div v-if="currentMaterial.signatureStatus === '1'" class="material-status">
                <el-tag type="success">已通过</el-tag>
              </div>
              <div v-if="currentMaterial.signatureStatus === '2'" class="material-status">
                <el-tag type="danger">未通过</el-tag>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="其他材料" :span="2">
              <div class="review-buttons"
                v-if="currentMaterial.other && currentMaterial.other.length > 0 && currentMaterial.status != '1' && currentMaterial.status != '2' && currentMaterial.otherStatus == '3'">
                <el-button type="success" size="small"
                  @click="reviewMaterial(currentMaterial.materialId, 9, '1')">通过</el-button>
                <el-button type="danger" size="small"
                  @click="reviewMaterial(currentMaterial.materialId, 9, '2')">拒绝</el-button>
              </div>
              <div class="preview-item other-materials">
                <template v-if="currentMaterial.other && currentMaterial.other.length > 0">
                  <el-image v-for="(url, idx) in currentMaterial.other" :key="idx" :src="url" fit="contain"
                    :preview-src-list="currentMaterial.other" :initial-index="idx" :preview-teleported="true"
                    :z-index="3100" class="material-image" style="width: 240px; height: 160px; margin: 0 10px;" />
                </template>
                <span v-else class="no-material">未上传</span>
              </div>
              <div v-if="currentMaterial.otherStatus === '1'" class="material-status">
                <el-tag type="success">已通过</el-tag>
              </div>
              <div v-if="currentMaterial.otherStatus === '2'" class="material-status">
                <el-tag type="danger">未通过</el-tag>
              </div>
            </el-descriptions-item>
          </el-descriptions>
        </div>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="materialDialogVisible = false">关闭</el-button>
          </span>
        </template>
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
            <!-- <el-descriptions-item label="状态" class="info-item">
              <el-tag :type="getRegistrationStatusType(currentRegistration.registrationStatus)">
                {{ getRegistrationStatusText(currentRegistration.registrationStatus) }}
              </el-tag>
            </el-descriptions-item> -->


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

      <!-- 上传offer对话框 -->
      <el-dialog v-model="uploadOfferDialogVisible" title="上传offer" width="30%">
        <div class="upload-container">
          <div class="upload-area">
            <input ref="fileInputRef" type="file" accept=".pdf" style="display: none;" @change="handleFileChange" />
            <el-button type="primary" @click="triggerFileUpload" class="upload-button">
              <el-icon>
                <Upload />
              </el-icon>
              选择PDF文件
            </el-button>
            <div class="el-upload__tip">
              请上传.pdf格式的offer文件
            </div>
          </div>
        </div>
      </el-dialog>

      <!-- 面试结果设置对话框 -->
      <el-dialog v-model="resultDialogVisible" title="设置面试结果" width="20%">
        <el-form :model="resultInterviewData" label-width="80px">
          <el-form-item label="结果" prop="interviewResult">
            <el-select v-model="resultInterviewData.interviewResult" placeholder="请选择结果">
              <el-option label="待定" value="0" />
              <el-option label="通过" value="1" />
              <el-option label="未通过" value="2" />
            </el-select>
          </el-form-item>
        </el-form>
        <template v-slot:footer>
          <el-button type="primary" @click="submitInterviewResult">保存</el-button>
          <el-button @click="resultDialogVisible = false">取消</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Calendar, Document, Upload } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { companyResumeList, selectResume, updateResumeStatus, sendInvitationData, selectInterviewList, updateInterviewList, selectOffersList, selectOfferTemplate, updateOfferStatus, selectMaterial, selectMaterialDetail, updateMaterialStatus, selectRegistration, updateRegistrationStatus, uploadWithThumbnail, downloadRegistrationPdf, postMessage, sendOffer, checkInterviewDate, updateStuffs } from '@/api/company/companyApi'


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
  // { label: '录入信息', value: '5' },
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

const reviewMaterial = async (materialId, code, status) => {
  try {
    const res = await updateStuffs(materialId, code, status)
    if (res.code === 200) {
      ElMessage.success('审核材料修改成功')
      Object.assign(currentMaterial.value, {
        identityCardStatus: res.content.identityCardStatus,
        physicalExaminationStatus: res.content.physicalExaminationStatus,
        identificationPhotoStatus: res.content.identificationPhotoStatus,
        diplomaStatus: res.content.diplomaStatus,
        bankCardStatus: res.content.bankCardStatus,
        qualificationCertificateStatus: res.content.qualificationCertificateStatus,
        resignCertificateStatus: res.content.resignCertificateStatus,
        otherStatus: res.content.otherStatus,
        signatureStatus: res.content.signatureStatus
      })
    } else {
      ElMessage.error('审核材料修改失败')
    }
  } catch (error) {
    console.error('审核材料修改失败')
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

const sendInvitation = async (row) => {
  let result = await checkInterviewDate()

  if (!result.content) {
    ElMessage.error('请设置面试时间')
    router.push({
      path: '/home/interviewTime',
      query: {
        skip: 0
      }
    })
    return
  }
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
const signatureDialogVisible = ref(false)


const offerId = ref('')
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
  materialId: '',
  identityCard: '',
  physicalExamination: '',
  diploma: '',
  identificationPhoto: '',
  bankCard: '',
  qualificationCertificate: '',
  resignCertificate: '',
  signaturePath: '',
  other: [],
  status: '',
  identityCardStatus: '',
  physicalExaminationStatus: '',
  diplomaStatus: '',
  identificationPhotoStatus: '',
  bankCardStatus: '',
  qualificationCertificateStatus: '',
  resignCertificateStatus: '',
  otherStatus: '',
  signatureStatus: ''
})

// 修改查看材料方法
const viewMaterial = async (row) => {
  try {
    const res = await selectMaterialDetail(row.materialId, row.offerId)
    if (res.code === 200) {
      currentMaterial.value = {
        materialId: res.content.materialId,
        identityCard: res.content.identityCard,
        physicalExamination: res.content.physicalExamination,
        diploma: res.content.diploma,
        identificationPhoto: res.content.identificationPhoto,
        bankCard: res.content.bankCard,
        qualificationCertificate: res.content.qualificationCertificate,
        resignCertificate: res.content.resignCertificate,
        signaturePath: res.content.signaturePath || '',
        other: Array.isArray(res.content.other) ? res.content.other : [],
        status: res.content.status,
        identityCardStatus: res.content.identityCardStatus,
        physicalExaminationStatus: res.content.physicalExaminationStatus,
        diplomaStatus: res.content.diplomaStatus,
        identificationPhotoStatus: res.content.identificationPhotoStatus,
        bankCardStatus: res.content.bankCardStatus,
        qualificationCertificateStatus: res.content.qualificationCertificateStatus,
        resignCertificateStatus: res.content.resignCertificateStatus,
        otherStatus: res.content.otherStatus,
        signatureStatus: res.content.signatureStatus
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
  resultDialogVisible.value = false
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


const selectedTemplateUrl = ref('')


// 添加文件输入引用
const fileInputRef = ref(null)

// 触发文件选择对话框
const triggerFileUpload = () => {
  fileInputRef.value.click()
}

// 处理文件选择变更
const handleFileChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  try {
    const formData = new FormData()
    formData.append('file', file)
    // formData.append('offerId', currentOfferId.value)
    formData.append('offerId', offerId.value)
    const result = await uploadWithThumbnail(formData)

    if (result.code === 200) {
      // 发送消息
      postMessage({
        userId: currentUserId.value,
        content: "您的offer已发送,请及时查收 —此消息来自系统自动发送"
      })

      // 更新offer状态
      handleOfferStatus(currentOfferId.value, '4', jobId)
      ElMessage.success('offer发送成功')
      ElMessage.success('上传成功')
      getOffersList()
      uploadOfferDialogVisible.value = false
    } else {
      ElMessage.error(result.msg || '上传失败')
    }
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error('上传失败')
  } finally {
    event.target.value = ''
  }
}

// ... existing code ...

const resultInterviewData = ref({
  interviewResult: ''
})

const resultDialogVisible = ref(false)

const setInterviewResult = (row) => {
  // 保存当前面试记录到editInterviewData用于提交
  editInterviewData.value = {
    interviewId: row.interviewId,
    interviewDate: row.interviewDate,
    interviewRegion: row.interviewRegion,
    interviewType: row.interviewType,
    interviewTime: row.interviewTime,
    interviewStatus: row.interviewStatus,
    jobResumeId: row.jobResumeId,
    userId: row.userId,
    jobId: row.jobId
  }

  // 初始化面试结果数据
  resultInterviewData.value.interviewResult = row.interviewResult || '0'

  // 打开对话框
  resultDialogVisible.value = true
}

const submitInterviewResult = async () => {
  try {
    const result = await updateInterviewList({
      ...editInterviewData.value,
      interviewResult: resultInterviewData.value.interviewResult
    })
    if (result.code === 200) {
      ElMessage.success('面试结果设置成功')
      resultDialogVisible.value = false
      if (resultInterviewData.value.interviewResult == '1') {
        uploadOfferDialogVisible.value = true
        offerId.value = result.content
      }
      getInterviewList()
    } else {
      ElMessage.error('面试结果设置失败')
    }
  } catch (error) {
    console.error('设置面试结果失败:', error)
    ElMessage.error('设置面试结果失败')
  }
}

const cancelInterview = async () => {
  try {
    await ElMessageBox.confirm('确认取消该面试吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    const cancelData = {
      ...editInterviewData.value,
      interviewStatus: 3
    };
    const result = await updateInterviewList(cancelData);
    if (result.code === 200) {
      ElMessage.success('面试已取消');
      editInterviewDialogVisible.value = false;
      getInterviewList();
    } else {
      ElMessage.error('取消失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败');
    }
  }
};

const identityCardList = computed(() => currentMaterial.value.identityCard || []);
const bankCardList = computed(() => currentMaterial.value.bankCard || []);

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
  gap: 4px;
}

:deep(.action-buttons .el-button) {
  padding: 2px 6px;
  height: 24px;
  line-height: 16px;
  font-size: 13px;
  border-radius: 4px;
  margin: 0;
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
  border-color: #F56C6C !important;
  background-color: #F56C6C !important;
}

.timeline-item.deleted .timeline-text {
  color: #909399;
}

.timeline-item.deleted.active:not(.current),
.timeline-item.deleted.active:not(.current) {
  background-color: #fff;
  border-color: #409EFF;
}

.timeline-item.current[class*="timeline-item"] {
  z-index: 3;
}

.timeline-item.active[class*="timeline-item"] {
  z-index: 2;
}


.timeline-item.current .timeline-dot {
  background-color: #409EFF;
}

.timeline-item.current:first-child .timeline-dot {
  border-color: #409EFF;
  background-color: #409EFF;
}


.timeline-item.current:first-child .timeline-line {
  background-color: #409EFF;
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
  overflow: auto;
}

.preview-item {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 360px; /* 原来是180px，改大一倍或更大 */
  margin: 0 auto;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: none;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.preview-item:hover {
  box-shadow: none;
  transform: none;
}

.preview-item.other-materials {
  height: 220px;
  width: 100%;
}

.material-image {
  width: 480px;   /* 原来是240px，改大一倍或更大 */
  height: 320px;  /* 原来是160px，改大一倍或更大 */
  object-fit: contain;
  border-radius: 4px;
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
  cursor: pointer;
}

.material-image:hover {
  transform: scale(1.05);
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
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  padding: 8px;
}

:deep(.el-carousel__item .material-image) {
  width: 240px;
  height: 160px;
  object-fit: contain;
}

:deep(.el-carousel__arrow) {
  transform: translateY(-50%);
  width: 32px;
  height: 32px;
  background-color: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  transition: all 0.3s ease;
}

:deep(.el-carousel__arrow:hover) {
  background-color: rgba(0, 0, 0, 0.7);
}

:deep(.el-carousel__arrow--left) {
  left: 10px;
}

:deep(.el-carousel__arrow--right) {
  right: 10px;
}

:deep(.el-carousel__indicators) {
  bottom: 8px;
}

:deep(.el-carousel__indicator) {
  padding: 6px;
}

:deep(.el-carousel__button) {
  width: 8px;
  height: 8px;
  background-color: rgba(255, 255, 255, 0.7);
  border-radius: 50%;
}

:deep(.el-carousel__indicator.is-active .el-carousel__button) {
  background-color: white;
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

.upload-container {
  padding: 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.upload-area {
  text-align: center;
}

.upload-button {
  margin-bottom: 15px;
  height: 50px;
  font-size: 16px;
}

.el-upload__tip {
  font-size: 14px;
  color: #909399;
  margin-top: 10px;
}

.material-preview-dialog :deep(.el-dialog) {
  min-width: 800px;
}

.material-preview-dialog :deep(.el-dialog__body) {
  padding: 20px;
  height: calc(100vh - 180px);
}

/* 确保图片预览遮罩层始终在最上层 */
:deep(.el-image-viewer__mask),
:deep(.el-image-viewer__wrapper) {
  z-index: 3150 !important;
}

.material-preview-dialog :deep(.el-descriptions) {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.material-preview-dialog :deep(.el-descriptions__label) {
  width: 120px;
  padding: 16px 20px !important;
  background-color: #f7f8fa;
  font-weight: 500;
  color: #606266;
  border-right: 1px solid #ebeef5;
}

.material-preview-dialog :deep(.el-descriptions__content) {
  padding: 8px !important;
  color: #303133;
}

.material-preview-dialog :deep(.el-descriptions__table) {
  width: 100%;
  border-collapse: collapse;
}

.material-preview-dialog :deep(.el-descriptions__cell) {
  border: 1px solid #ebeef5;
}

.preview-item {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 180px;
  margin: 0 auto;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: none;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.preview-item:hover {
  box-shadow: none;
  transform: none;
}

.preview-item.other-materials {
  height: 220px;
  width: 100%;
}

.material-image {
  width: 240px;
  height: 160px;
  object-fit: contain;
  border-radius: 4px;
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
  cursor: pointer;
}

.material-image:hover {
  transform: scale(1.05);
}

.no-material {
  color: #909399;
  font-size: 14px;
}

.pdf-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin: 0 auto;
}

.timeline-item.interview-accepted.current:first-child .timeline-dot {
  border-color: #409EFF;
  background-color: #409EFF;
}

/* Override for interview-accepted when resumeDelete is 0 */
.timeline-item.deleted.current.interview-accepted .timeline-dot {
  border-color: #F56C6C !important;
  background-color: #F56C6C !important;
}

.review-buttons {
  display: flex;
  gap: 10px;
  justify-content: center;
  margin-bottom: 10px;
}

.result-dialog {
  margin-top: 20px;
}

.result-dialog .el-form-item {
  margin-bottom: 20px;
}

.result-dialog .el-form-item label {
  font-weight: bold;
}

.result-dialog .el-input {
  width: 100%;
}

.result-dialog .el-button {
  margin-top: 20px;
}

.material-status {
  margin-top: 8px;
  display: flex;
  justify-content: center;
}
</style>
