<script setup>
import { ref, onMounted, reactive } from 'vue';
import { registrationStatus, registrationInfo, registrationSubmit } from '@/api/user/UserApi';
import { ElMessage } from 'element-plus';

const formRootData = reactive([
    { prop: 'hireDate', label: '入职日期', value: null },
    { prop: 'position', label: '职位', value: null },
    { prop: 'userName', label: '姓名', value: null },
    { prop: 'gender', label: '性别', value: null },
    { prop: 'birthDate', label: '出生日期', value: null },
    { prop: 'idCardNumber', label: '身份证号', value: null },
    { prop: 'phoneNumber', label: '手机号码', value: null },
    { prop: 'email', label: '邮箱地址', value: null },
    { prop: 'emergencyContact', label: '紧急联系人号码', value: null },
    { prop: 'bankAccount', label: '银行账号', value: null },
    { prop: 'educationLevel', label: '最高学历', value: null },
    { prop: 'address', label: '家庭地址', value: null },
    { prop: 'schoolName', label: '毕业学校名称', value: null },
])
// 页面状态选项
const windowStatusOptions = [
    { status: 0, description: '待提交' },
    { status: 1, description: '待审核' },
    { status: 2, description: '通过' },
    { status: 3, description: '拒绝' }
];
const windowStatus = ref(null);// 页面状态
const windowStatusMessage = ref('');// 页面状态提示信息
const formData = reactive({});// 表单数据

const position = ref(null);// 表单头部显示职位
// 生日日期最大值
const maxDate = new Date(new Date().setFullYear(new Date().getFullYear())).toISOString().split('T')[0]



onMounted(async () => {
    const res1 = await registrationStatus();
    const resultStatus = +res1.content.registrationStatus;
    windowStatus.value = resultStatus;
    windowStatusMessage.value = windowStatusOptions.find(item => item.status === resultStatus)?.description || '';
    if (windowStatus.value === 0 || windowStatus.value === 3) {
        const res2 = await registrationInfo();
        position.value = res2.content.position;
    }
})

// 对象数据填充
const createObject = (val, data, fn) => {
    const k = formRootData.flatMap((item) => item.prop);
    const v = formRootData.flatMap((item) => item[val]);
    for (let i = 0; i < k.length; i++) {
        data[k[i]] = v[i];
    }
    fn(data);
}
createObject('value', formData, (data) => {
    delete data.hireDate;
    delete data.position;
});


/* 表单行为 */
// 提交表单
const submitForm = async () => {
    // 提交表单
    for (let [key, _] of Object.entries(formData)) {
        if (!formData[key]) {
            ElMessage.error(`请填写${formRootData.find(item => item.prop === key)?.label}`);
            return;
        }
        if (key === 'gender' && formData[key] === '--请选择--') {
            ElMessage.error(`请选择${formRootData.find(item => item.prop === key)?.label}`);
            return;
        }
        if (key === 'educationLevel' && formData[key] === '--请选择--') {
            ElMessage.error(`请选择${formRootData.find(item => item.prop === key)?.label}`);
            return;
        }
    }
    const result = await registrationSubmit(formData);
    if (result.code !== 200) {
        return;
    }
    // 提交成功提示信息
    ElMessage.success(result.message);

    // 提交成功后，更新页面状态
    const res = await registrationStatus();
    const resultStatus = +res.content.registrationStatus;
    // 更新页面状态
    windowStatus.value = resultStatus;
    // 页面状态提示信息
    windowStatusMessage.value = windowStatusOptions.find(item => item.status === resultStatus)?.description || '';
}

// 重置表单
const resetForm = () => {
    for (let [key, _] of Object.entries(formData)) {
        formData[key] = null;
    }
}



</script>

<template>
    <div v-if="windowStatus === 3">
        <main class="form-container">
            <h1 class="form-title">个人信息录入</h1>
            <el-alert v-if="windowStatus === 3" title="信息为通过审核，请重新提交" type="warning" center show-icon  />
            <el-row justify="center">
                <el-col :span="12" class="form-subtitle">
                    职位：{{ position }}
                </el-col>
            </el-row>
            <!-- 表单主体 -->
            <form class="form-section">
                <el-row>
                    <el-col :span="8" class="form-item">
                        <label class="w-50" for="">姓名</label>
                        <input class="form-input" type="text" v-model="formData.userName" required="true" alt="姓名"
                            maxlength="10" minlength="2">
                    </el-col>
                    <el-col :span="8" class="form-item">
                        <label class="w-50" for="">性别</label>
                        <select v-model="formData.gender" name="gender" class="form-input w-50">
                            <option value="">--请选择--</option>
                            <option value="男">男</option>
                            <option value="女">女</option>
                        </select>

                    </el-col>
                    <el-col :span="8" class="form-item">
                        <label class="w-50" for="">出生日期</label>
                        <input class="form-input" type="date" v-model="formData.birthDate" required="true" alt="出生日期"
                            :max="maxDate">
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="12" class="form-item">
                        <label class="w-50" for="">身份证号</label>
                        <input class="form-input" type="text" v-model="formData.idCardNumber" required="true" alt="身份证号"
                            maxlength="18" minlength="18">
                    </el-col>
                    <el-col :span="12" class="form-item">
                        <label class="w-50" for="">手机号码</label>
                        <input class="form-input" type="number" v-model="formData.phoneNumber" required="true" alt="手机号码">
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="12" class="form-item">
                        <label class="w-50" for="">邮箱地址</label>
                        <input class="form-input" type="email" v-model="formData.email" required="true" alt="邮箱地址"
                            maxlength="50" minlength="5">
                    </el-col>
                    <el-col :span="12" class="form-item">
                        <label class="w-50" for="">紧急联系人号码</label>
                        <input class="form-input" type="number" v-model="formData.emergencyContact" required="true"
                            alt="紧急联系人号码">
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="12" class="form-item">
                        <label class="w-50" for="">银行账号</label>
                        <input class="form-input" type="text" v-model="formData.bankAccount" required="true" alt="银行账号"
                            maxlength="20" minlength="10">
                    </el-col>
                    <el-col :span="12" class="form-item">
                        <label class="w-50" for="">最高学历</label>
                       <select v-model="formData.educationLevel" name="educationLevel" class="form-input w-50">
                            <option value="">--请选择--</option>
                            <option value="高中">高中</option>
                            <option value="大专">大专</option>
                            <option value="本科">本科</option>
                            <option value="硕士">硕士</option>
                            <option value="博士">博士</option>
                        </select>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col class="form-item">
                        <label class="w-25" for="">家庭地址</label>
                        <input class="form-input" type="text" v-model="formData.address" required="true" alt="家庭地址"
                            maxlength="100" minlength="4">
                    </el-col>
                </el-row>
                <el-row>
                    <el-col class="form-item">
                        <label class="w-25" for="">毕业学校名称</label>
                        <input class="form-input" type="text" v-model="formData.schoolName" required="true" alt="毕业学校名称"
                            minlength="4" maxlength="50">
                    </el-col>
                </el-row>
                <el-row justify="center">
                    <el-col style="display: flex; justify-content: center; align-items: center;" class="form-item">
                        <el-button type="default" size="large" @click="resetForm">重置</el-button>
                        <span style="width: 50px;"></span>
                        <el-button type="primary" size="large" @click="submitForm">提交</el-button>
                    </el-col>
                </el-row>
            </form>
        </main>

    </div>

    <!-- 页面状态提示-待审核 -->
    <div v-if="windowStatus === 1" class="info-status">
        <el-result icon="warning" :title="windowStatusMessage"></el-result>
    </div>
    <!-- 页面状态提示-通过 -->
    <div v-if="windowStatus === 2" class="info-status">
        <el-result icon="success" :title="windowStatusMessage"></el-result>
    </div>
    <!-- 页面状态提示-拒绝 -->
    <div v-if="!windowStatus" class="info-status">
         <el-result icon="error" title="错误！请先投递简历"></el-result>
    </div>
</template>

<style lang="scss" scoped>
.form-container {
    .form-title {
        text-align: center;
        font-size: 30px;
        margin: 10px 0 20px 0;
    }

    .form-subtitle {
        text-align: center;
        font-size: 20px;
        margin: 5px 0 10px 0;
    }

    width: 80vw;
    min-height: 80vh;
    margin: auto;
    padding: 5px;

    .form-section {
        border: 1px solid #E6A23C;
    }

    .form-item {
        height: 60px;
        display: flex;
        border: 2px solid #E6A23C;
        align-items: center;

        label {
            display: flex;
            align-items: center;
            justify-content: center;
            text-align: center;
            font-size: 20px;
            height: 100%;
        }

        .form-input {
            width: 50%;
            height: 100%;
            border: none;
            font-size: 20px;
            border-left: 2px solid #E6A23C;
            padding: 0 10px;

        }

        select {
            color: #999;
            /* 鼠标悬停时显示为指针 */
            cursor: pointer;
        }
    }
}

.info-status {
    width: 100%;
    height: 400px;
    display: flex;
    justify-content: center;
    align-items: center;
}

.w-50 {
    width: 50%;
}

.w-25 {
    width: 25%;
}

// 去掉input type="number"右侧的上下箭头
input[type="number"]::-webkit-inner-spin-button,
input[type="number"]::-webkit-outer-spin-button {
    /* 针对Webkit内核浏览器，如Chrome, Safari */
    -webkit-appearance: none;
}

input[type="number"] {
    /* 针对Firefox */
    -moz-appearance: textfield;
}

</style>
