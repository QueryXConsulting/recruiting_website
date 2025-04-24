<script setup>
import userStore from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus';
import WBForm from '@/components/WBForm.vue';
import { iconMapping } from '@/utils/iconList';
import WBDialog from '@/components/WBDialog.vue';
import { ref, reactive, onMounted, nextTick } from 'vue';
import { userLogout } from '@/api/company/companyApi';
import { Edit, Plus, Picture, Delete, Bell, Medal, Opportunity, Place, School } from '@element-plus/icons-vue';
import {
    userInfo, attachmentList, uploadAttachment,
    resumeUpdate, resumeDelete, uploadAvatar
} from '@/api/user/UserApi';


// 表单根数据
const formItems = reactive([
    { required: false, prop: 'resumeId', label: '简历ID', value: '' },
    { required: true, prop: 'resumeName', label: '姓名', value: '' },
    { required: true, prop: 'resumeEmail', label: '邮箱', value: '' },
    { required: true, prop: 'resumeMajor', label: '专业', value: '' },
    { required: true, prop: 'resumeGender', label: '性别', value: '' },
    { required: true, prop: 'resumeJob', label: '意向职位', value: '' },
    { required: true, prop: 'resumePhone', label: '手机号', value: '' },
    { required: true, prop: 'resumeBirth', label: '出生日期', value: '' },
    { required: true, prop: 'resumeEducation', label: '学历', value: '' },
    { required: true, prop: 'resumeSalary', label: '期望薪资', value: '' },
    { required: true, prop: 'resumeCollege', label: '毕业院校', value: '' },
    { required: true, prop: 'resumeMarriage', label: '婚姻状况', value: '' },
    { required: true, prop: 'resumePolitical', label: '政治面貌', value: '' },
    { required: true, prop: 'resumeExperience', label: '工作经验(年)', value: '' },
    { required: false, prop: 'resumeEmploymentHistory', label: '工作经历', value: '' },
    { required: false, prop: 'resumeSkill', label: '专业技能', value: '' },
    { required: false, prop: 'resumeProject', label: '项目经验', value: '' },
    { required: false, prop: 'resumeIntroduction', label: '个人简介', value: '' },
]);
// 政治面貌选项
const politicalOptions = [
    { label: '--请选择--', value: '' },
    { label: '群众', value: '' },
    { label: '中共党员', value: '' },
    { label: '中共预备党员', value: '' },
    { label: '共青团员', value: '' },
    { label: '民革会员', value: '' },
    { label: '民盟盟员', value: '' },
    { label: '民建会员', value: '' },
    { label: '民进会员', value: '' },
    { label: '农工党党员', value: '' },
    { label: '致公党党员', value: '' },
    { label: '九三学社社员', value: '' },
    { label: '台盟盟员', value: '' },
    { label: '无党派民主人士', value: '' },
];
// 学历选项
const educationOptions = [
    { label: '--请选择--' },
    { label: '博士' },
    { label: '硕士' },
    { label: '本科' },
    { label: '专科' },
    { label: '大学以下' },
];
// 工资选项
const salaryOptions = [ // { range: "--请选择--" },
    { range: "面议" }, { range: "1k" }, { range: "2k" }, { range: "3k" },
    { range: "4k" }, { range: "5k" }, { range: "6k" }, { range: "7k" }, { range: "8k" },
    { range: "9k" }, { range: "10k" }, { range: "11k" }, { range: "12k" }, { range: "13k" }
];

// 对象数据填充
const createObject = (val, data, fn) => {
    const k = formItems.flatMap((item) => item.prop);
    const v = formItems.flatMap((item) => item[val]);
    for (let i = 0; i < k.length; i++) {
        data[k[i]] = v[i];
    }
    fn(data);
    return data;
}

/*====================== 工资相关 ======================*/
const salaryRange = reactive([]); // 工资范围(例如：['1k', '5k'])

const store = userStore();
const attachments = ref(); // 附件简历
const formData = ref({});// 表单数据
// 数据请求
onMounted(async () => {
    await nextTick();
    // 调整布局
    const nav = document.querySelector('.nav-bar');
    // 在线简历部分
    const infoBox = document.querySelector('.main-container');
    infoBox.style.marginTop = `${nav.offsetHeight + 10}px`;
    // 附件简历部分
    const attachmentBox = document.querySelector('.attachment-container');
    attachmentBox.style.marginTop = `${nav.offsetHeight + 10}px`;

    // 获取用户在线简历
    const res = await userInfo();
    // 获取附件简历
    const resp = await attachmentList();

    if (store.userInfo?.resumeId) {
        formData.value.resumeId = store.userInfo.resumeId;
    }
    if (typeof res === "undefined" || !res.hasOwnProperty('content')) {
        const requiredItems = formItems.filter((item) => item.required).flatMap((item) => item.prop);
        for (let i of requiredItems) {
            formData.value[i] = null;
        }

        return;
    }
    formData.value = res.content;
    // 工资赋值
    salaryRange.length = 0;// 清空工资范围

    const salary = res.content.resumeSalary;
    for (let m of salary.split('-')) {
        salaryRange.push(m);
    }

    attachments.value = resp.content;

})


// 表单显示数据
const formDisplay = reactive({});
createObject('label', formDisplay, (i) => {
    delete i.resumeId;
    delete i.resumeName;
    delete i.resumeSkill;
    delete i.resumeProject;
    delete i.resumeIntroduction;
    delete i.resumeEmploymentHistory;
})


const formRef = ref(null);// 表单实例

const notEdit = ref(true);// 是否编辑状态
// 表单确认按钮点击事件
const editInfo = async () => {
    // 姓名赋值
    formData.value.resumeName = document.querySelector('.user-name').innerText;
    if (!formData.value.resumeName) {
        ElMessage.error('请填写姓名');
        return;
    }
    for (let [key, value] of Object.entries(formData.value)) {
        // 新用户没有ID，需要跳过ID校验
        if (key === 'resumeId') continue;
        // 校验工资
        if (key === 'resumeSalary') {
            if (salaryRange[0] === '面议') {
                formData.value[key] = salaryRange.join('-');
                continue;
            }
            if ((salaryRange[0] === '' || !salaryRange[0]) || (salaryRange[1] === '' || !salaryRange[1])) {
                ElMessage.error('请填写完整工资范围');
                return;
            }
            if (parseInt(salaryRange[0]?.split('k')[0]) > parseInt(salaryRange[1]?.split('k')[0])) {
                ElMessage.error('工资范围错误');
                return;
            }
            formData.value[key] = salaryRange.join('-');
            continue;
        }
        // 校验政治面貌
        if (key === 'resumePolitical' && value === '--请选择--') {
            ElMessage.error('请选择政治面貌');
            return;
        }
        // 校验学历
        if (key === 'resumeEducation' && value === '--请选择--') {
            ElMessage.error('请选择学历');
            return;
        }
        // 校验其他必填项
        if (!value) {
            console.log('eeee', value, formData.value);
            ElMessage.error('请填写完整信息');
            return;
        }
    }
    notEdit.value = true;
    await resumeUpdate(formData.value).then((res) => {
        // 使用 Object.assign
        store.userInfo = Object.assign({}, store.userInfo, { resumeId: res.content });
        ElMessage.success(res.message);
        // window.location.reload();
    });
}

const project = ref(false);// 项目经验编辑状态
// 项目经验编辑按钮点击事件
const handleProjectEdit = async () => {
    project.value = false;
    await resumeUpdate(formData.value).then((res) => {
        ElMessage.success(res.message);
    });
}

const experience = ref(false);// 工作经验编辑状态
// 工作经验编辑按钮点击事件
const handleExperienceEdit = async () => {
    experience.value = false;
    await resumeUpdate(formData.value).then((res) => {
        ElMessage.success(res.message);
    });
}

const skillEdit = ref(false);// 个人技能编辑状态
// 个人技能编辑按钮点击事件
const handleSkillEdit = async () => {
    skillEdit.value = false;
    await resumeUpdate(formData.value).then((res) => {
        ElMessage.success(res.message);
    });
}

const introductionEdit = ref(false);// 个人简介编辑状态
// 个人简介编辑按钮点击事件
const handleIntroductionEdit = async () => {
    introductionEdit.value = false;
    await resumeUpdate(formData.value).then((res) => {
        ElMessage.success(res.message);
    });
}


const changeAvater = ref(false);// 头像更换状态
const imgUrl = ref('');// 头像上传地址
const newAvatar = ref(null);// 头像上传文件
// 头像上传预览
const handlePreview = (image) => {
    const reader = new FileReader();
    reader.readAsDataURL(image.file);
    reader.onload = (e) => {
        imgUrl.value = e.target.result;
        newAvatar.value = image.file;
    }
}
// 弹窗关闭
const dialogClose = () => {
    changeAvater.value = false;
    imgUrl.value = '';
}
// 头像上传
const sendUploadAvatar = async () => {
    const _formData = new FormData();
    _formData.append('image', newAvatar.value);
    await uploadAvatar(_formData).then((res) => {
        ElMessage.success(res.message);
    });
    changeAvater.value = false;
}


// 上传附件简历
const uploadResume = async (file) => {
    if (attachments.value?.length >= 5) {
        ElMessage.error('最多上传5个附件简历');
        return;
    }
    const _formData = new FormData();
    _formData.append('attachment', file.file);
    await uploadAttachment(_formData);
    ElMessage.info('上传成功');
    // 获取附件简历
    const resp = await attachmentList();
    attachments.value = resp.content;
}
// 附件简历删除
const deleteAttachment = async (id) => {
    await resumeDelete(id).then((res) => {
        ElMessage.success(res.message);
    });
    // 获取附件简历
    const resp = await attachmentList();
    attachments.value = resp.content;
}

const isShowPreview = ref(false);// 详情弹窗是否显示
const pdfUrl = ref('');// pdf地址
const previewAttachmentResume = (url) => {
    pdfUrl.value = url;
    isShowPreview.value = true;
};


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
    }
}

const hasMessage = ref(false); // 是否有未读消息
</script>

<template>
    <div class="user-info-container">
        <!-- 顶部导航栏 -->
        <nav class="nav-bar">
            <div class="logo">
                <img src="/public/logo.png" alt="问呗" class="logo-img">
                <span class="divider">|</span>
                <span class="recruit-text">招聘</span>
            </div>
            <div class="nav-items">
                <a href="/users/index" class="nav-item">首页</a>
                <a href="/users/search" class="nav-item">校园招聘</a>
                <a href="#" class="nav-item">社会招聘</a>
                <a href="/users/application" class="nav-item" v-if="userStore().role == '5'">应聘历史</a>
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

        <!-- 页面主体 -->
        <main class="main-container">
            <!-- 主体头部 -->
            <header class="user-info-header">
                <span class="user-avatar">
                    <el-tooltip content="点击更换头像">
                        <el-avatar :size="100" @click="changeAvater = true" :src="formData.userAvatar" @error="() => { }">
                            <img src="/public/default_user.png" alt="头像">
                        </el-avatar>
                    </el-tooltip>
                    <el-tooltip content="这里可以修改姓名" :visible="!notEdit">
                        <p class="user-name" :contenteditable='!notEdit'
                            :style="{ border: notEdit ? '0px solid rgb(121, 187, 255)' : '1px solid rgb(121, 187, 255)' }">
                            {{ formData?.resumeName }}
                        </p>
                    </el-tooltip>
                </span>
            </header>
            <!-- 主体内容 -->
            <section class="user-info-main">
                <WBForm v-model="formData" inline ref="formRef" class="user-info-form">
                    <!-- 个人信息 -->
                    <template #form-items>
                        <h3>个人信息</h3>
                        <el-row justify="center" :gutter="50" class="user-info-row">
                            <span v-if="notEdit" @click="notEdit = false" class="user-info-edit-btn">
                                <el-icon size="large">
                                    <Edit />
                                </el-icon>
                                编辑
                            </span>
                            <el-col v-for="(_, index) in formDisplay" :key="index" :span="8" class="user-info-col">
                                <label for="">{{ formDisplay[index] }}：</label><br>
                                <!-- 性别 -->
                                <el-radio-group v-if="index === 'resumeGender'" :disabled="notEdit" class="user-info-input"
                                    v-model="formData[index]" size="large">
                                    <span style="margin: 0 40px 0 0;">
                                        <el-radio-button label="男" value="男" style="" />
                                    </span>
                                    <span style="">
                                        <el-radio-button label="女" value="女" />
                                    </span>
                                </el-radio-group>

                                <!-- 选择部分 -->
                                <el-select v-else-if="index === 'resumePolitical' ||
                                    index === 'resumeEducation' ||
                                    index === 'resumeMarriage'" :disabled="notEdit" v-model="formData[index]"
                                    class="user-info-input" placeholder="请选择" size="large">
                                    <!-- 政治背景 -->
                                    <template v-if="index === 'resumePolitical'">
                                        <el-option v-for="(item, i1) in politicalOptions" :key="i1" :label="item.label"
                                            :value="item.label"></el-option>
                                    </template>
                                    <!-- 学历 -->
                                    <template v-else-if="index === 'resumeEducation'">
                                        <el-option v-for="(item, i2) in educationOptions" :key="i2" :label="item.label"
                                            :value="item.label"></el-option>
                                    </template>
                                    <!-- 婚姻状况 -->
                                    <template v-else-if="index === 'resumeMarriage'">
                                        <el-option label="未婚" value="未婚"></el-option>
                                        <el-option label="已婚" value="已婚"></el-option>
                                    </template>
                                </el-select>
                                <!-- 生日 -->
                                <el-date-picker v-else-if="index === 'resumeBirth'" v-model="formData[index]" type="date"
                                    placeholder="选择出生日期" :disabled="notEdit" :style="{ margin: '10px 0 0 0' }" size="large"
                                    class="user-info-input" />
                                <!-- 期待薪资 -->
                                <span v-else-if="index === 'resumeSalary'"
                                    style="display: flex;justify-content: space-between;">
                                    <el-select v-model="salaryRange[0]" placeholder="请选择" size="large" :disabled="notEdit"
                                        class="user-info-input" :style="{ width: '100px' }"
                                        @change="(val) => { if (val === '面议') { salaryRange[1] = '' } else { salaryRange[1] = val } }">
                                        <el-option v-for="(item, i) in salaryOptions" :key="i" :label="item.range"
                                            :value="item.range"></el-option>
                                    </el-select>
                                    <em style="color: #999;margin: 0 15px;font-size: 20px;line-height: 50px;">至</em>
                                    <el-select v-model="salaryRange[1]" placeholder="请选择" size="large"
                                        :disabled="notEdit || salaryRange[0] === '面议'" class="user-info-input"
                                        :style="{ width: '100px' }">
                                        <el-option v-for="(item, i) in salaryOptions" :key="i" :label="item.range"
                                            :value="item.range"></el-option>
                                    </el-select>
                                </span>
                                <!-- 工作经验 -->
                                <el-select v-else-if="index === 'resumeExperience'" v-model="formData[index]"
                                    :disabled="notEdit" size="large" class="user-info-input">
                                    <el-option label="应届毕业生" value="无工作经验"></el-option>
                                    <el-option label="1年以下" value="1年以下"></el-option>
                                    <el-option label="1-3年" value="1-3年"></el-option>
                                    <el-option label="3-5年" value="3-5年"></el-option>
                                    <el-option label="5-10年" value="5-10年"></el-option>
                                    <el-option label="10年以上" value="10年以上"></el-option>
                                    <el-option label="经验不限" value="经验不限"></el-option>
                                </el-select>
                                <!-- 其他 -->
                                <el-input v-else v-model="formData[index]" :readonly="notEdit" size="large"
                                    class="user-info-input" />
                            </el-col>
                            <!-- 提交按钮 -->
                            <label v-if="!notEdit" class="user-info-submit-btn">
                                <el-button @click="notEdit = true" size="large">取消</el-button>
                                <el-button type="primary" size="large" @click="editInfo">确认</el-button>
                            </label>
                        </el-row>
                    </template>
                </WBForm>
                <!-- 其他信息 -->
                <div>
                    <h3>
                        专业技能
                        <span v-show="!skillEdit" @click="skillEdit = true" class="user-info-other-edit-btn">
                            <el-icon style="color: #00bebd;margin: 0 3px 0 0;">
                                <Edit />
                            </el-icon>
                            编辑
                        </span>
                    </h3>
                    <p v-if="!skillEdit">
                        {{ formData?.resumeSkills }}
                    </p>
                    <el-row v-else>
                        <el-input v-model="formData.resumeSkills" type="textarea"></el-input>
                        <label class="user-info-submit-btn">
                            <el-button @click="skillEdit = false" size="large">取消</el-button>
                            <el-button type="primary" size="large" @click="handleSkillEdit">确认</el-button>
                        </label>
                    </el-row>
                </div>

                <div>
                    <h3>
                        项目经历
                        <span v-show="!project" @click="project = true" class="user-info-other-edit-btn">
                            <el-icon style="color: #00bebd;margin: 0 3px 0 0;">
                                <Edit />
                            </el-icon>
                            编辑
                        </span>
                    </h3>
                    <p v-if="!project">
                        {{ formData?.resumeProject }}
                    </p>
                    <el-row v-else>
                        <el-input v-model="formData.resumeProject" type="textarea"></el-input>
                        <label class="user-info-submit-btn">
                            <el-button @click="project = false" size="large">取消</el-button>
                            <el-button type="primary" size="large" @click="handleProjectEdit">确认</el-button>
                        </label>
                    </el-row>
                </div>

                <div>
                    <h3>
                        工作经历
                        <span v-show="!experience" @click="experience = true" class="user-info-other-edit-btn">
                            <el-icon style="color: #00bebd;margin: 0 3px 0 0;">
                                <Edit />
                            </el-icon>
                            编辑
                        </span>
                    </h3>
                    <p v-if="!experience">
                        {{ formData?.resumeEmploymentHistory }}
                    </p>
                    <el-row v-else>
                        <el-input v-model="formData.resumeEmploymentHistory" type="textarea"></el-input>
                        <label class="user-info-submit-btn">
                            <el-button @click="experience = false" size="large">取消</el-button>
                            <el-button type="primary" size="large" @click="handleExperienceEdit">确认</el-button>
                        </label>
                    </el-row>
                </div>

                <div>
                    <h3>
                        个人简介
                        <span v-show="!introductionEdit" @click="introductionEdit = true" class="user-info-other-edit-btn">
                            <el-icon style="margin: 0 3px 0 0;">
                                <Edit />
                            </el-icon>
                            编辑
                        </span>
                    </h3>
                    <p v-if="!introductionEdit">
                        {{ formData?.resumeIntroduction }}
                    </p>
                    <el-row v-else>
                        <el-input v-model="formData.resumeIntroduction" type="textarea"></el-input>
                        <label class="user-info-submit-btn">
                            <el-button @click="introductionEdit = false" size="large">取消</el-button>
                            <el-button type="primary" size="large" @click="handleIntroductionEdit">确认</el-button>
                        </label>
                    </el-row>
                </div>
            </section>
        </main>

        <!-- 附件简历 -->
        <article class="attachment-container">
            <header class="user-upload-header">
                附件管理
                <el-tooltip content="上传附件">
                    <el-upload ref="upload" :limit="1" :http-request="uploadResume" :show-file-list="false">
                        <el-button class="user-upload-btn">
                            <el-icon>
                                <Plus />
                            </el-icon>
                        </el-button>
                    </el-upload>
                </el-tooltip>
            </header>
            <ul class="user-upload-resumes">
                <li v-for="(item, index) in attachments" :key="index" class="user-upload-resume">
                    <!-- 文件图标 -->
                    <span class="user-upload-resume-icon">
                        <img src="/public/pdfIcon.png" alt="图标">
                    </span>
                    <!-- 文件 -->
                    <span class="user-upload-resume-other">
                        <p class="user-upload-resume-name">
                            <a href="javascript:void(0)" @click="previewAttachmentResume(item.filePath)">{{ item.fileName
                            }}</a>
                        </p>
                        <p class="user-upload-resume-desc">
                            <span class="user-upload-resume-size">{{ item.fileSize }}KB&nbsp;</span>
                            <span class="user-upload-resume-date">更新于：{{ item.uploadDate }}</span>
                        </p>
                    </span>
                    <label @click="deleteAttachment(item.resumeAttachmentId)" class="user-upload-resume-delete">
                        <el-icon size="large">
                            <Delete />
                        </el-icon>
                    </label>
                </li>
            </ul>
        </article>
    </div>

    <!-- 头像更换弹窗 -->
    <WBDialog v-model="changeAvater" @submit="sendUploadAvatar" @cancel="imgUrl = ''" @close="dialogClose" cancel-text="重选"
        title="更换头像">
        <img v-if="imgUrl" :src="imgUrl" />
        <el-upload v-else class="avatar-uploader" :show-file-list="false" :http-request="handlePreview">
            <el-icon class="avatar-uploader-icon">
                <Plus />
            </el-icon>
        </el-upload>
    </WBDialog>


    <!-- pdf预览弹窗 -->
    <WBDialog v-model="isShowPreview" fullscreen title="offer预览">
        <iframe :src="pdfUrl" class="pdf-preview" frameborder="0"></iframe>
        <template #footer><i></i></template>
    </WBDialog>
</template>

<style lang="scss" scoped>
h1,
h2,
h3 {
    font: none;
    padding: 30px 0 10px 0;
    font-size: 24px;

    &:before {
        content: '';
        display: inline-block;
        width: 4px;
        height: 20px;
        background: #00bebd;
        border-radius: 3px;
        left: 0;
        top: 5px;
        margin: 0 10px 0 0;
        vertical-align: middle;
    }
}


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

.logo-img {
    height: 32px;
    width: auto;
}

.divider {
    margin: 0 10px;
    color: #FF7427;
}

.user-info-container {
    height: 100%;
    padding: 20px;
    display: flex;

    &>main {
        background: #fff;
        width: 77%;
        border-radius: 20px;
        overflow: hidden;
        margin: 0 10px 0 0;
    }

    &>article {
        border-radius: 20px;
        // padding: 20px;
        width: 23%;
        height: 100%;
        overflow: hidden;
        background: #fff;
    }
}

.recruit-text {
    font-size: 16px;
    color: #FF7427;
}

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

.user-info-header {
    width: 100%;
    height: 30vh;
    position: relative;
    background: url(@/../public/mountain.png);
}

.user-avatar {
    position: absolute;
    left: 50%;
    bottom: 0;
    transform: translate(-50%, 50%);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
}

.user-name {
    padding: 5px 0 0 0;
    color: #999;
    font-size: 24px;
    margin: 5px 0px 0 0;
    padding: 0px 50px;
    cursor: pointer;
}

.user-info-main {
    margin: 40px;
    padding: 30px 0 0 0;
}


.user-info-row {
    background: #f8f8f8;
    border-radius: 20px;
    padding: 20px;
    position: relative;
}

.user-info-form {
    margin: 0 0 10px 0px;
}

.user-info-edit-btn {
    position: absolute;
    right: 5px;
    top: -30px;
    color: #00bebd;
    display: flex;
    justify-content: space-between;
    align-items: center;
    cursor: pointer;
}

.user-info-col {
    font-size: 18px;
    margin: 0 0 20px 0;

    label {
        color: #999;
    }
}

.user-info-input {
    margin: 10px 0 0 0;
    width: 260px;
}

.user-info-input.el-input--disabled .el-input__inner {
    background-color: #f5f7fa;
    border-color: #e4e7ed;
    color: #606266;
    cursor: not-allowed;
}

.user-info-input.el-select--disabled .el-input__inner {
    background-color: #f5f7fa;
    border-color: #e4e7ed;
    color: #606266;
    cursor: not-allowed;
}

.user-info-submit-btn {
    margin: 20px 0 0 80%;
}

.user-info-other-edit-btn {
    padding: 5px 0 0 0;
    display: flex;
    align-items: center;
    float: right;
    font-size: 16px;
    color: #00bebd;
    cursor: pointer;
}

.user-upload-header {
    display: flex;
    padding: 20px;
    justify-content: space-between;
    background: linear-gradient(90deg, #f5fcfc, #fcfbfa);
}

.user-info-upload-btn {
    vertical-align: middle;
}

.user-upload-resume {
    display: flex;
    padding: 10px;
    position: relative;
    border-bottom: 1px solid #dcdfe6;

    a {
        text-decoration: none;
        color: #000;
    }
}

.user-upload-resume-icon {
    width: 44px;
}

.user-upload-resume-other {
    padding: 0 0 0 10px;
}

.user-upload-resume-desc {
    color: #999;
    font-size: 14px;
    padding: 5px 0 0 0;
}

.user-upload-resume-delete {
    position: absolute;
    right: 10px;
    top: 40%;
    color: #999;
    cursor: pointer;
}


.avatar-uploader {
    margin: 0 auto;
    width: 178px;
    height: 178px;
    display: block;
    border: 1px dashed #999;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
}

.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    text-align: center;
}

/* 为iframe设置一些基本样式 */
.pdf-preview {
    width: 100%;
    /* 以适应其父容器 */
    height: 600px;
    /* 高度可以根据需要调整 */
    border: none;
    /* 去掉边框 */
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    /* 添加阴影效果 */
    margin: 5px auto;
    /* 上下外边距为20px，左右自动居中 */
    display: block;
    /* 将iframe显示为块级元素 */
    background-color: #f5f5f5;
    /* 背景颜色，当PDF加载失败时会显示 */

}
</style>
