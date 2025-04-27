<script setup>
import { jobInfo, resumeAll, resumeDeliver, postMessage } from '@/api/user/UserApi';
import { onMounted, reactive, ref } from 'vue';
import router from '@/router/index';
import { View, Picture } from '@element-plus/icons-vue';
import WBDialog from '@/components/WBDialog.vue';
// 头部导航栏
import userStore from '@/store/user';
import { iconMapping } from '@/utils/iconList';
import { Bell } from '@element-plus/icons-vue';
import { userLogout } from '@/api/company/companyApi';
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus';

const hasMessage = ref(false); // 是否有未读消息

const handleCommand = (command) => {
    if (command === 'logout') {
        ElMessageBox.confirm('确定要退出登录吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => {
            userLogout().then(() => {
                userStore().$reset();
                window.location.reload();
                // 跳转到登录页
                router.push('/auth/login');
                ElMessage.success('已安全退出登录');
            })
        }).catch(() => { })
    } else if (command === 'userInfo') {
        router.push('/users/userInfo');
    } else if (command === 'index') {
        router.push('/home');
    }
}


const loading = ref(true);// 控制loading
// 岗位信息
const jobDetail = ref();
let companyId = null;// 公司id
let jobName = null;// 岗位名称

// 组件挂载完成后请求岗位信息
onMounted(async () => {
    const route = router.currentRoute.value;
    let response = null;
    let _loading = null;
    try {
        _loading = ElLoading.service({
            lock: false,
            text: '加载中……',
            background: 'rgba(0, 0, 0, 0.7)',
        })
        response = await jobInfo(route.query.jobId);
    } catch (error) {
        ElMessage.error('网络繁忙，请稍后再试');
        router.go(-1);
    } finally {
        _loading.close();
    }
    jobDetail.value = response.content;
    loading.value = false;
    // 暂存公司id
    companyId = response.content.companyId;
    jobName = response.content.jobPosition;
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
        jobResumeId: jobDetail.value.resumeDeliveryId,
        jobId: router.currentRoute.value.query.jobId,
        resumeId: radioModel.value.resumeId,
        resumeType: radioModel.value.type,
        resumeStatus: 0
    }
    // 提交简历投递
    await resumeDeliver(dto).then((res) => {
        ElMessage.success(res.message);
        isShowDialog.value = true;
    });
    // 发送消息通知
    if (!companyId) {
        ElMessage.error('未知错误，请联系管理员');
        return;
    }
    await postMessage({ userId: companyId, content: `我向您发布的${jobName}岗位投递了简历，请注意查看。 ——此消息由系统自动发送，请勿回复。` });
    // 弹窗跳转至投递历史页面
    ElMessageBox.alert('恭喜您已投递成功!', '提示', {
        confirmButtonText: '确定',
        type:'success'
    }).then(() => {
        router.push('/users/application');
    }).catch(() => { });
};

// 撤销投递处理函数
const revokeDeliver = async () => {
    const dto = {
        jobResumeId: jobDetail.value.resumeDeliveryId,
        resumeStatus: 7
    }
    await resumeDeliver(dto).then((res) => {
        ElMessage.success(res.message);
    });
    // 刷新页面
    window.location.reload();
};

</script>

<template>
    <el-container v-loading.fullscreen.lock="loading" v-if="jobDetail" class="info-container">

        <!-- 顶部导航栏 -->
        <nav class="nav-bar">
            <div class="logo">
                <!-- <img src="/public/logo.png" alt="问呗" class="logo-img">
                    <span class="divider">|</span>
                    <span class="recruit-text">招聘</span> -->
            </div>
            <div class="nav-items">
                <a href="/users/index" class="nav-item">首页</a>
                <a href="/users/search" class="nav-item active">校园招聘</a>
                <a href="#" class="nav-item">社会招聘</a>
                <a href="/users/application" class="nav-item" v-if="userStore().role == '5'">投递历史</a>
                <a href="/users/message" class="nav-item" v-if="userStore().role == '5'" style="padding-top: 15px;"
                    alt="留言板">
                    <el-icon>
                        <component :is="Bell"></component>
                    </el-icon>
                    <!-- 未读消息提示(小圆点) -->
                    <i v-if="hasMessage" class="message-flag"></i>
                </a>
                <!-- 用户头像 -->
                <el-dropdown v-if="userStore().role == '5'" @command="handleCommand">
                    <span class="user-dropdown">
                        <el-avatar size="large" :src="userStore().userInfo.userAvatar" @error="() => { }">
                            <!-- 头像加载失败时显示默认头像 -->
                            <img src="/public/default_user.png" alt="用户头像">
                        </el-avatar>
                    </span>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item command="userInfo">
                                <el-icon>
                                    <component :is="iconMapping['user']" />
                                </el-icon>
                                个人信息
                            </el-dropdown-item>
                            <el-dropdown-item command="logout" divided>
                                <el-icon>
                                    <component :is="iconMapping['switch']" />
                                </el-icon>
                                退出登录
                            </el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
                <router-link to="/auth/login" class="login-btn" v-else-if="userStore().token == null">登录</router-link>
            </div>
        </nav>

        <section class="job-info">
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
                        <p class="job-company">所属机构：{{}}</p>
                        <p class="job-location">工作地点：{{ jobDetail.jobArea }}</p>
                        <p class="job-category">工作分类：{{ jobDetail.jobCategory }}</p>
                        <p class="job-person-num">招聘人数：{{ jobDetail.jobPersonNumber }}</p>
                        <p class="job-contact">联系人名称：{{ jobDetail.jobContact }}</p>
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

                <!-- 投递按钮 -->
                <el-button :disabled="jobDetail.jobIsDelivery" style="color: #fff;" color="#FF7427" size="large" @click="handleResumeDeliver">
                    {{ jobDetail.jobIsDelivery ? '已投递' : '立即投递' }}
                </el-button>
                <!-- 投递撤销按钮 -->
                <el-button v-if="jobDetail.jobIsDelivery" style="color: #fff;" color="#FF7427" size="large" @click="revokeDeliver">
                    撤销投递
                </el-button>

            </el-main>
        </section>

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
                <!-- 文件图标 -->
                <span style="width: 30px;margin: 0 15px 0 9px;">
                    <img src="/public/resume.png" alt="简历图标">
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
                <!-- 文件图标 -->
                <span class="deliver-resume-icon">
                    <img src="/public/pdfIcon.png" alt="简历图标">
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
    height: 600px;
}

.info-header {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    margin: 10px 0 10px 0;
}

.job-info {
    width: 100%;
    padding: 10px;
    height: 100%;
    border-radius: 10px;
    background: #fff;
    margin: 90px 10vw 0 10vw;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}
.job-content {
    .job-content-title {
        &:before {
            background: #FF7427;
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

/* 头部导航栏 */
.nav-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0.5rem 4rem;
    background: rgba(255, 255, 255, 0.95);
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 100;
    backdrop-filter: blur(8px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.logo {
    display: flex;
    align-items: center;
}

// .logo-img {
//   height: 32px;
//   width: auto;
// }

.divider {
    margin: 0 10px;
    color: #FF7427;
}

// .recruit-text {
//     font-size: 16px;
//     color: #FF7427;
// }

.nav-items {
    display: flex;
    align-items: center;
    gap: 2rem;
}

.nav-item {
    color: #333;
    text-decoration: none;
    font-size: 16px;
    padding: 0.5rem 0;
    position: relative;
}

.nav-item:hover,
.nav-item.active {
    color: #FF7427;
}

.nav-item.active::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 2px;
    background-color: #FF7427;
}

.message-flag {
    position: absolute;
    width: 5px;
    height: 5px;
    right: 1%;
    background: #FF7427;
    border-radius: 50%;
}

.login-btn {
    background: #FF7427;
    color: white;
    padding: 0.5rem 1.5rem;
    border-radius: 4px;
    text-decoration: none;
    transition: background-color 0.3s;
}

.login-btn:hover {
    background: #FFAA66;
}
</style>
