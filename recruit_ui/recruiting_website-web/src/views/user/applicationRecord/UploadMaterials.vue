<script setup>
import { onMounted, reactive, ref } from 'vue';
import WBForm from '@/components/WBForm.vue';
import { materialStatus, materialUpload, materialOtherUpload } from '@/api/user/UserApi';
import { Delete, Plus, ZoomIn, WarningFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus';


// 文件上传迭代配置对象
const iterationObject = reactive([
    { required: true, prop: 'setIdentityCard', title: '身份证', multiple: false, limit: 1, accept: '.jpg,.jpeg,.png', tip: '请上传身份证正背面或户口照片' },
    { required: true, prop: 'setPhysicalExamination', title: '体检报告', multiple: false, limit: 1, accept: '.pdf', tip: '请上传PDF文件' },
    { required: true, prop: 'setDiploma', title: '学历证书', multiple: false, limit: 1, accept: '.jpg,.jpeg,.png', tip: '请上传学历、学位证书或学生证照片' },
    { required: true, prop: 'setIdentificationPhoto', title: '证件照', multiple: false, limit: 1, accept: '.jpg,.jpeg,.png', tip: '请上传2寸彩色证件照' },
    { required: true, prop: 'setBankCard', title: '银行卡', multiple: false, limit: 1, accept: '.jpg,.jpeg,.png', tip: '请上传银行卡正背面照片' },
    { required: false, prop: 'setResignCertificate', title: '离职证明', multiple: false, limit: 1, accept: '.jpg,.jpeg,.png,.pdf', tip: '请上传照片或PDF文件' },
    { required: false, prop: 'setQualificationCertificate', title: '岗位资格证书', multiple: false, limit: 1, accept: '.jpg,.jpeg,.png', tip: '请上传照片' },
    { required: false, prop: 'setOther', title: '其他资料', multiple: true, limit: null, accept: null, tip: '请上传其他资料（如：户口本、结婚证等）' },
]);

const filteredIO = iterationObject.filter(item => item.title !== '其他资料');
const windowStatus = ref(null);// 窗口状态
const windowStatusMessage = ref(null);// 窗口状态信息
const fileList = ref([]);// 上传文件列表
const uploadOtherRef = ref(null);// 上传组件实例
const imgUrl = ref('');// 图片预览地址
const dialogVisible = ref(false)
const disabled = ref(false);// 上传按钮是否禁用

onMounted(async () => {
    const res = await materialStatus();
    windowStatus.value = res.content;
    windowStatusMessage.value = res.message;
})


const handleUpload = async () => {
    // 非法校验
    if (fileList === null || fileList.length === 0) {
        ElMessage.error('请选择文件');
        return;
    }
    for (let i = 0; i < iterationObject.length; i++) {
        if (!iterationObject[i].required) {
            continue;
        }
        if (!fileList.value[i]) {
            ElMessage.error(`请上传${iterationObject[i].title}`);
            return;
        }
    }
    
    const length = iterationObject.length - 1;
    const formData1 = new FormData();
    for (let i = 0; i < fileList.value.length; i++) {
        if (i === length) {
            break;
        }
        const file = fileList.value[i];
        if (!file) {
            continue;
        }
        formData1.append(iterationObject[i].prop, file[0].raw);

    }
    const res1 = await materialUpload(formData1);
    if (!res1.content) {
        ElMessage.error(res1.message);
        return;
    }
    if (!fileList.value[length]) {
        return;
    }

    // 其他资料上传
    const formData2 = new FormData();
    for (let i = 0; i < fileList.value[length].length; i++) {
        const file = fileList.value[length][i];
        formData2.append('files', file.raw);
    }
    const res2 = await materialOtherUpload(formData2);
    if (!res2.content) {
        ElMessage.error(res2.message);
        return;
    }
}

const handleRemove = (file) => {
    uploadOtherRef.value.handleRemove(file);
}

const handlePreview = (file) => {
    imgUrl.value = file.url
    dialogVisible.value = true
}


</script>

<template>
    <div v-if="windowStatus === 1 || windowStatus === 2" class="upload-container">
        <el-row justify="end">
            <el-col :span="8" :pull="6">
                <el-alert :title="windowStatusMessage" type="info" :closable="false" show-icon />
            </el-col>
            <el-col :span="2">
                <el-button @click="handleUpload" size="large" type="warning">提交</el-button>
            </el-col>
        </el-row>
        <el-divider></el-divider>
        <el-row justify="start">
            <el-col v-for="(item, index) in filteredIO" :key="index" :span="8" :gutter="50" class="upload-item">
                <p class="upload-title">
                    <i v-if="item.required" style="color: #f00;">*</i>
                    {{ item.title }}
                </p>
                <el-upload v-model:file-list="fileList[index]" :accept="item.accept" :multiple="item.multiple" action="#"
                    :on-preview="handlePreview" :on-remove="handleRemove" list-type="picture" :limit="item.limit"
                    :auto-upload="false">
                    <el-button type="primary">点击上传文件</el-button>
                    <template #tip>
                        <span class="el-upload__tip">
                            &emsp;{{ item.tip }}
                        </span>
                    </template>
                </el-upload>
            </el-col>
        </el-row>
        <el-divider></el-divider>
        <!-- 其他资料上传 -->
        <el-row justify="start">
            <el-col class="upload-item">
                <p class="upload-title">
                    <i v-if="iterationObject[iterationObject.length - 1].required" style="color: #f00;">*</i>
                    其他资料
                </p>
                <el-upload v-model:file-list="fileList[iterationObject.length - 1]" ref="uploadOtherRef" action="#"
                    list-type="picture-card" :auto-upload="false">
                    <el-icon>
                        <Plus />
                    </el-icon>
                    <template #file="{ file }">
                        <div>
                            <img class="el-upload-list__item-thumbnail" :src="file.url" alt="" />
                            <span class="el-upload-list__item-actions">
                                <span class="el-upload-list__item-preview" @click="handlePreview(file)">
                                    <el-icon><zoom-in /></el-icon>
                                </span>
                                <span v-if="!disabled" class="el-upload-list__item-delete" @click="handleRemove(file)">
                                    <el-icon>
                                        <Delete />
                                    </el-icon>
                                </span>
                            </span>
                        </div>
                    </template>
                </el-upload>
            </el-col>

        </el-row>

        <!-- 全图预览弹窗 -->
        <el-dialog v-model="dialogVisible">
            <img :src="imgUrl" alt="图片预览" />
        </el-dialog>

    </div>

    <!-- 未到该环节 -->
    <div v-if="windowStatus === -1" class="upload-status">
        <el-result icon="warning" :title="windowStatusMessage"></el-result>
    </div>

    <!-- 已上传，等待审核 -->
    <div v-if="windowStatus === 0" class="upload-status">
        <el-result icon="success" :title="windowStatusMessage"></el-result>
    </div>
</template>

<style lang="scss" scoped>
.upload-container {
    padding: 20px;
}

.upload-item {
    padding: 20px;
}

.upload-title {
    font-size: 20px;
    color: #999;
    margin: 0 0 15px 0;
}

.upload-status {
    width: 100%;
    height: 400px;
    display: flex;
    justify-content: center;
    align-items: center;
}
</style>