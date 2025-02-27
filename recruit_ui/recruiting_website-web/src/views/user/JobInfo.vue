<script setup>
import { jobInfo, resumeAll, resumeDeliver } from '@/api/user/UserApi';
import { onMounted, reactive, ref } from 'vue';
import router from '@/router/index';
import { View, Picture } from '@element-plus/icons-vue';
import WBDialog from '@/components/WBDialog.vue';
import { ElMessage } from 'element-plus';


const loading = ref(true);// 控制loading
// 岗位信息
const jobDetail = ref();

// 组件挂载完成后请求岗位信息
onMounted(async () => {
    const route = router.currentRoute.value;
    const response = await jobInfo(+route.query.jobId);
    jobDetail.value = response.content;
    loading.value = false;
});

const isShowDialog = ref(false);// 简历投递弹窗控制
const onlineResume = reactive({});// 在线简历
const attachmentResume = ref([]);// 附件简历
const radioModel = ref({});// 简历投递弹窗选择项
const resumeType = { online: 0, attachment: 1 };// 简历类型常量
// 简历投递按钮点击事件
const handleResumeDeliver = async () => {
    // 请求所有简历
    const response = await resumeAll();
    attachmentResume.value = response.content.attachmentsResumes;
    onlineResume.resumeId = response.content.resumeId;
    onlineResume.userRegisterTime = response.content.userRegisterTime;
    isShowDialog.value = true;
};
// 提交简历投递
const submitDeliver = async () => {
    if (!radioModel.value) {
        ElMessage.error('请选择简历');
        return;
    }
    // 准备数据
    const dto = {
        jobId: +router.currentRoute.value.query.jobId,
        resumeId: radioModel.value.resumeId,
        resumeType: radioModel.value.type
    }
    await resumeDeliver(dto).then((res)=> {
        ElMessage.success(res.message);
    });
};

</script>

<template>
    <el-container v-loading.fullscreen.lock="loading" v-if="jobDetail" class="info-container">
        <el-header class="info-header">
            <h1 class="job-name">{{ jobDetail.jobPosition }}&emsp;
                <el-tag class="job-nature" type="warning" size="large">{{ jobDetail.jobNature }}</el-tag>
            </h1>
            <div class="job-other">
                <span class="job-salary">{{ jobDetail.jobSalary }}</span>
                <span class="job-view">
                    <el-icon style="vertical-align: middle;">
                        <View />
                    </el-icon>
                    浏览量：{{ jobDetail.jobView }}
                </span>
            </div>

        </el-header>
        <el-main class="job-content">
            <ul>
                <li>
                    <h3 class="job-content-title">岗位信息</h3>
                    <p class="job-company">所属机构：{{}}南京分行</p>
                    <p class="job-location">工作地点：{{ jobDetail.jobArea }}</p>
                    <p class="job-category">工作分类：{{ jobDetail.jobCategory }}</p>
                    <p class="job-person-num">招聘人数：{{ jobDetail.jobPersonNumber }}</p>
                    <p class="job-contact">联系人名称：{{ jobDetail.jobContact }}</p>
                    <p>截止时间：{{}}2025-06-10</p>
                </li>
                <li>
                    <h3 class="job-content-title">岗位描述</h3>
                    <p class="job-desc">{{ jobDetail.jobPositionDescribe }}</p>
                </li>
                <li>
                    <h3 class="job-content-title">岗位要求</h3>
                    <div class="job-req">
                        <p>学历：{{ jobDetail.jobEducation }}</p>
                        <p>经验：{{ jobDetail.jobExperience }}</p>
                    </div>
                </li>
            </ul>

            <el-button type="danger" size="large" @click="handleResumeDeliver">立即投递</el-button>
        </el-main>
    </el-container>

    <!-- 简历投递弹窗 -->
    <WBDialog @submit="submitDeliver" @cancel="isShowDialog = false" v-model="isShowDialog" title="请选择要投递的简历" width="50%">
        <ul class="deliver-resumes">
            <!-- 在线简历 -->
            <li class="deliver-resume-item">
                <label class="deliver-resume-radio">
                    <input type="radio" v-model="radioModel"
                        :value="{ type: resumeType.online, resumeId: onlineResume.resumeId }" />
                </label>
                <!-- 文件图标  class="deliver-resume-radio"-->
                <span style="width: 30px;margin: 0 15px 0 9px;">
                    <el-image fit="fill" src="../../../public/resume.png">
                        <template #error>
                            <el-icon>
                                <Picture />
                            </el-icon>
                        </template>
                    </el-image>
                </span>
                <!-- 文件 -->
                <span class="deliver-resume-other">
                    <em style="color: #000;font-size: 17px;">在线简历</em>
                    <p class="deliver-resume-desc">
                        <span class="deliver-resume-date">创建于：{{ onlineResume.userRegisterTime }}</span>
                    </p>
                </span>
            </li>
            <!-- 附件简历 -->
            <li v-for="(item, index) in attachmentResume" :key="index" class="deliver-resume-item">
                <label class="deliver-resume-radio">
                    <input type="radio" v-model="radioModel"
                        :value="{ type: resumeType.attachment, resumeId: item.resumeAttachmentId }" />
                </label>
                <!-- 文件图标  class="deliver-resume-radio"-->
                <span class="deliver-resume-icon">
                    <el-image fit="fill" src="../../../public/pdfIcon.png">
                        <template #error>
                            <el-icon>
                                <Picture />
                            </el-icon>
                        </template>
                    </el-image>
                </span>
                <!-- 文件 -->
                <span class="deliver-resume-other">
                    <em style="color: #000;font-size: 17px;">{{ item.fileName }}</em>
                    <p class="deliver-resume-desc">
                        <span class="deliver-resume-size">{{ item.fileSize }}KB&nbsp;</span>
                        <span class="deliver-resume-date">上传于：{{ item.uploadDate }}</span>
                    </p>
                </span>
            </li>
        </ul>
    </WBDialog>
</template>

<style scoped lang="scss">
h1,
h2,
h3 {
    font: none;
    color: #000;
    font-weight: bold;
}

p {
    color: #505463;
}

.info-container {
    background: #fff;
    /* padding: 20px; */
}

.info-header {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    margin: 10px 0 0 0;
}

.job-content {
    .job-content-title {
        &:before {
            background: #c8152d;
            content: "";
            display: inline-block;
            height: 14px;
            margin-right: 6px;
            width: 3px;
            vertical-align: middle;
        }

    }

    ul {
        &>li {
            margin: 0 0 20px 0;

            .job-content-title {
                margin: 0 0 10px 0;
            }
        }
    }
}

.job-name {
    font-size: 24px;
    vertical-align: middle;
}

// .job-other {}

.job-salary {
    color: #f26d49;
    font-size: 20px;
    margin: 3px 0 0 0px;
}

.job-view {
    color: #999;
    font-size: 14px;
    margin: 0 0 0px 20px;
}

.deliver-resumes {
    margin: 10px;
}

.deliver-resume-item {
    display: flex;
    align-items: center;
    border: 1px solid #e6e6e6;
    padding: 5px 0 5px 10px;
    border-radius: 20px;
    margin: 0 0 10px 0;
    cursor: pointer;
}

.deliver-resume-icon {
    width: 44px;
    margin: 0 10px 0 0;
}

.deliver-resume-desc {
    color: #999;
}
</style>
