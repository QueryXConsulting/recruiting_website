<script setup>
import { onMounted, reactive, ref, nextTick } from 'vue';
import { materialStatus, materialUpload, materialOtherUpload, offerSignature } from '@/api/user/UserApi';
import { Delete, Plus, ZoomIn } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus';
import WBDialog from '@/components/WBDialog.vue';
import InformationInput from './InformationInput.vue';

// 文件上传迭代配置对象
const iterationObject = reactive([
    { required: true, status: null, prop: 'setIdentityCard', title: '身份证', multiple: true, limit: 2, accept: '.jpg,.jpeg,.png', tip: '请上传身份证正背面或户口照片' },
    { required: true, status: null, prop: 'setPhysicalExamination', title: '体检报告', multiple: false, limit: 1, accept: '.pdf', tip: '请上传PDF文件' },
    { required: true, status: null, prop: 'setDiploma', title: '学历证书', multiple: false, limit: 1, accept: '.jpg,.jpeg,.png', tip: '请上传学历、学位证书或学生证照片' },
    { required: true, status: null, prop: 'setIdentificationPhoto', title: '证件照', multiple: false, limit: 1, accept: '.jpg,.jpeg,.png', tip: '请上传2寸彩色证件照' },
    { required: true, status: null, prop: 'setBankCard', title: '银行卡', multiple: true, limit: 2, accept: '.jpg,.jpeg,.png', tip: '请上传银行卡正背面照片' },
    { required: false, status: null, prop: 'setResignCertificate', title: '离职证明', multiple: false, limit: 1, accept: '.jpg,.jpeg,.png,.pdf', tip: '请上传照片或PDF文件' },
    { required: false, status: null, prop: 'setQualificationCertificate', title: '岗位资格证书', multiple: false, limit: 1, accept: '.jpg,.jpeg,.png', tip: '请上传照片' },
    { required: false, status: null, prop: 'setSignature', title: 'offer签名', multiple: false, limit: 1, accept: '.jpg,.jpeg,.png', tip: '请完成offer签名照片' },
    { required: false, status: null, prop: 'setOther', title: '其他资料', multiple: true, limit: null, accept: '.jpg,.jpeg,.png,.pdf', tip: '请上传其他资料（如：户口本、结婚证等）' },
]);

const filteredIO = iterationObject.filter(item => item.title !== '其他资料');
const windowStatus = ref(null);// 窗口状态
const windowStatusMessage = ref(null);// 窗口状态信息
const fileList = ref([]);// 上传文件列表
const otherFileList = ref([]);// 其他资料上传文件列表
const uploadOtherRef = ref(null);// 上传组件实例
const imgUrl = ref('');// 图片预览地址
const dialogVisible = ref(false)
const disabled = ref(false);// 上传按钮是否禁用
let offerId = null; // offerId
let isResetUpload = false; // 是否重新上传标志

const resultObject = {
    identityCardStatus: 'setIdentityCard',
    physicalExaminationStatus: 'setPhysicalExamination',
    diplomaStatus: 'setDiploma',
    identificationPhotoStatus: 'setIdentificationPhoto',
    bankCardStatus: 'setBankCard',
    resignCertificateStatus: 'setResignCertificate',
    qualificationCertificateStatus: 'setQualificationCertificate',
    signatureStatus: 'setSignature',
    otherStatus: 'setOther'
};

onMounted(async () => {
    const res = await materialStatus();
    if (res.content.hasOwnProperty('offerId')) {
        offerId = res.content.offerId;

        for (let [k, v] of Object.entries(resultObject)) {
            if (!res.content.hasOwnProperty(k)) {
                continue;
            }
            if (res.content[k] === '2') {
                // 打回提示
                iterationObject.find(item => item.prop === v).status = '该材料已被打回，请重新上传';
                isResetUpload = true;
            }
        }
    }
    windowStatus.value = res.content.status;
    windowStatusMessage.value = res.message;
})


const handleUpload = async () => {
    // 非法校验
    if (fileList === null || fileList.length === 0) {
        ElMessage.error('请选择文件');
        return;
    }
    if (windowStatus.value === 1) {
        for (let i = 0; i < iterationObject.length; i++) {
            if (!iterationObject[i].required) {
                continue;
            }
            if (!fileList.value[i]) {
                ElMessage.error(`请上传${iterationObject[i].title}`);
                return;
            }
        }
    }

    // const length = iterationObject.length - 1;
    const formData1 = new FormData();
    for (let i = 0; i < fileList.value.length; i++) {
        // if (i === length) {
        //     break;
        // }
        const file = fileList.value[i];
        if (!file) {
            continue;
        }
        for (let j = 0; j < file.length; j++) {
            formData1.append(`${iterationObject[i].prop}-${j}`, file[j].raw);
        }
    }
    // 判断是否被打回重新上传
    if (isResetUpload) {
        formData1.append('isResetUpload', true);
    }
    const res1 = await materialUpload(formData1);
    if (!res1.content) {
        ElMessage.error(res1.message);
        return;
    }
    ElMessage.success(`必须材料：${res1.message}`);

    // 其他资料上传
    if (!otherFileList.value) {
        // 刷新页面
        window.location.reload();
        return;
    }

    const formData2 = new FormData();
    for (let i = 0; i < otherFileList.value.length; i++) {
        const file = otherFileList.value[i];
        formData2.append('files', file.raw);
    }
    const res2 = await materialOtherUpload(formData2);
    if (!res2.content) {
        ElMessage.error(res2.message);
        return;
    }
    ElMessage.success(`其它材料：${res2.message}`);
    // 刷新页面
    window.location.reload();
}

const handleRemove = (file) => {
    uploadOtherRef.value.handleRemove(file);
}

const handlePreview = (file) => {
    imgUrl.value = file.url
    dialogVisible.value = true
}


/*================= 签名弹窗 =================*/
const isShowSignature = ref(false);// 签名弹窗是否显示
// cavas绘制相关变量
let canvasRef, ctx, painting;
let lastX = 0;
let lastY = 0;
const signatureUrl = ref(null);// 签名图片地址

// 初始化canvas
const initCanvas = async () => {
    await nextTick();
    canvasRef = document.querySelector('.signature-canvas');
    ctx = canvasRef.getContext('2d');
    painting = false;
    // 设定Canvas尺寸
    canvasRef.width = canvasRef.offsetWidth;
    canvasRef.height = canvasRef.offsetHeight;
    // 填充背景
    ctx.fillStyle = '#fff';
    ctx.fillRect(0, 0, canvasRef.width, canvasRef.height);
}

// 开始绘制
const startPainting = (e) => {
    painting = true;
    [lastX, lastY] = [e.offsetX, e.offsetY];
    ctx.beginPath();
    ctx.moveTo(lastX, lastY);
}
// 结束绘制
const stopPainting = () => {
    // 结束绘制
    painting = false;
    ctx.closePath();
}
// 绘制
const draw = (e) => {
    if (!painting) return;
    ctx.lineWidth = 2;
    ctx.lineTo(lastX, lastY);
    ctx.stroke();
    [lastX, lastY] = [e.offsetX, e.offsetY];
}

// 提交签名
const signatureSubmit = async () => {
    if (!offerId) {
        ElMessage.error('未知错误，请联系管理员');
        return;
    }
    const dataURL = canvasRef.toDataURL('image/png');
    const byteString = atob(dataURL.split(',')[1]);
    const mimeType = dataURL.split(',')[0].split(':')[1].split(';')[0];
    const ab = new ArrayBuffer(byteString.length);
    const ia = new Uint8Array(ab);
    for (let i = 0; i < byteString.length; i++) {
        ia[i] = byteString.charCodeAt(i);
    }
    const blob = new Blob([ab], { type: mimeType });
    const formData = new FormData();
    const imgName = Date.now() + '.' + mimeType.split('/')[1];
    formData.append('image', blob, imgName);
    signatureUrl.value = URL.createObjectURL(blob);
    const _res = await offerSignature(offerId, formData, false);
    if (_res) {
        // 关闭弹窗
        isShowSignature.value = false;
        if (_res.code !== 200) {
            return ElMessage.error(_res.message);
        }
    }
}

// 清空画布
const clearCanvas = () => {
    ctx.clearRect(0, 0, canvasRef.width, canvasRef.height);
}

</script>

<template>
    <!-- TODO -->
    <div v-if="windowStatus === 1 || windowStatus === 2 || windowStatus === 3" class="upload-container">
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
                <div class="uploadSignature" v-if="item.title === 'offer签名' && offerId">
                    <p class="upload-title">
                        <i v-if="item.required" style="color: #f00;">*</i>
                        {{ item.title }}
                        <span style="color: #ff7427;font-size: 12px;margin-top: 7px;"> &emsp;{{ item.status }}</span>
                    </p>
                    <p>
                        <el-button type="primary"
                            @click="() => { isShowSignature = true; initCanvas(); }">点击开始签名</el-button>
                        <span style="color: #606266;font-size: 12px;margin-top: 7px;"> &emsp;{{ item.tip }}</span>
                    </p>
                    <ul v-if="signatureUrl" class="el-upload-list el-upload-list--picture">
                        <li class="el-upload-list__item is-ready" tabindex="0">
                            <img class="el-upload-list__item-thumbnail" :src="signatureUrl" alt="">
                            <div class="el-upload-list__item-info">
                                <a class="el-upload-list__item-name">
                                    <i class="el-icon el-icon--document">
                                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024">
                                            <path fill="currentColor"
                                                d="M832 384H576V128H192v768h640zm-26.496-64L640 154.496V320zM160 64h480l256 256v608a32 32 0 0 1-32 32H160a32 32 0 0 1-32-32V96a32 32 0 0 1 32-32m160 448h384v64H320zm0-192h160v64H320zm0 384h384v64H320z">
                                            </path>
                                        </svg>
                                    </i>
                                    <span class="el-upload-list__item-file-name" title="c.png">c.png</span>
                                </a>
                            </div><label class="el-upload-list__item-status-label">
                                <i class="el-icon el-icon--upload-success el-icon--check">
                                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024">
                                        <path fill="currentColor"
                                            d="M406.656 706.944 195.84 496.256a32 32 0 1 0-45.248 45.248l256 256 512-512a32 32 0 0 0-45.248-45.248L406.592 706.944z">
                                        </path>
                                    </svg>
                                </i>
                            </label>
                            <i class="el-icon el-icon--close">
                                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024">
                                    <path fill="currentColor"
                                        d="M764.288 214.592 512 466.88 259.712 214.592a31.936 31.936 0 0 0-45.12 45.12L466.752 512 214.528 764.224a31.936 31.936 0 1 0 45.12 45.184L512 557.184l252.288 252.288a31.936 31.936 0 0 0 45.12-45.12L557.12 512.064l252.288-252.352a31.936 31.936 0 1 0-45.12-45.184z">
                                    </path>
                                </svg>
                            </i>
                            <i class="el-icon--close-tip">按 delete 键可删除</i>
                        </li>
                    </ul>
                </div>

                <div v-else-if="item.title !== 'offer签名'">
                    <p class="upload-title">
                        <i v-if="item.required" style="color: #f00;">*</i>
                        {{ item.title }}
                        <span style="color: #ff7427;font-size: 12px;margin-top: 7px;"> &emsp;{{ item.status }}</span>
                    </p>
                    <el-upload v-model:file-list="fileList[index]" :accept="item.accept" :multiple="item.multiple"
                        action="#" :on-preview="handlePreview" :on-remove="handleRemove" list-type="picture"
                        :limit="item.limit" :auto-upload="false">
                        <el-button type="primary">点击上传文件</el-button>
                        <template #tip>
                            <span class="el-upload__tip">
                                &emsp;{{ item.tip }}
                            </span>
                        </template>
                    </el-upload>
                </div>
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
                <el-upload v-model:file-list="otherFileList" ref="uploadOtherRef" action="#" list-type="picture-card"
                    :auto-upload="false">
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


        <!-- 签名弹窗 -->
        <WBDialog v-model="isShowSignature" @cancel="clearCanvas" @submit="signatureSubmit" cancel-text="清空" width="50%"
            title="offer签名" draggable>
            <fieldset style="display: flex; justify-content: center; align-items: center; height: 300px;">
                <canvas @mousedown="startPainting" @mousemove="draw" @mouseleave="stopPainting" @mouseup="stopPainting"
                    class="signature-canvas"></canvas>
            </fieldset>
        </WBDialog>


        <!-- 全图预览弹窗 -->
        <el-dialog v-model="dialogVisible">
            <img :src="imgUrl" alt="图片预览" />
        </el-dialog>

    </div>

    <!-- 未到该环节 -->
    <div v-if="windowStatus === -1" class="upload-status">
        <el-result icon="warning" :title="windowStatusMessage"></el-result>
    </div>

    <!-- 审核通过 -->
    <div v-if="windowStatus === 0" class="upload-status">
        <el-result icon="success" :title="windowStatusMessage"></el-result>
        <!-- <InformationInput></InformationInput> -->
    </div>

    <!-- 已上传，等待审核 -->
    <!-- <div v-if="windowStatus === 3" class="upload-status">
        <el-result icon="warning" :title="windowStatusMessage"></el-result>
    </div> -->
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

.signature-canvas {
    width: 100%;
    height: 100%;
    border: 2px solid #FF7427;
    border-radius: 10px;
}
</style>