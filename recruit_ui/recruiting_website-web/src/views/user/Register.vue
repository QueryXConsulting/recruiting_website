<script setup>
import WBForm from "@/components/WBForm.vue";
import { ref, reactive } from "vue";
import { userRegister } from '@/api/user/UserApi';
import { ElMessage } from 'element-plus';

// 表单根数据
const formItems = reactive([
    { tag: '', prop: 'userName', label: '用户名', value: '' },
    { tag: '', prop: 'userPassword', label: '密码', value: '' },
    { tag: '', prop: 'userRole', label: '角色', value: '' },
    { tag: '', prop: 'userAvatar', label: '头像', value: '' },
    { tag: '', prop: 'resumePhone', label: '手机号', value: '' },
    { tag: '', prop: 'resumeEmail', label: '邮箱', value: '' },
    { tag: '', prop: 'resumeGender', label: '性别', value: '' },
    { tag: '', prop: 'resumeBirth', label: '出生日期', value: '' },
    { tag: '', prop: 'resumeMarriage', label: '婚姻状况', value: '' },
    { tag: '', prop: 'resumePolitical', label: '政治面貌', value: '' },
    { tag: '', prop: 'resumeCollege', label: '毕业院校', value: '' },
    { tag: '', prop: 'resumeMajor', label: '专业', value: '' },
    { tag: '', prop: 'resumeEducation', label: '学历', value: '' },
    { tag: '', prop: 'resumeExperience', label: '工作经验', value: '' },
    { tag: '', prop: 'resumeProject', label: '项目经验', value: '' },
    { tag: '', prop: 'resumeJob', label: '工作职位', value: '' },
    { tag: '', prop: 'resumeSalary', label: '期望薪资', value: '' },
    { tag: '', prop: 'resumeSkills', label: '专业技能', value: '' },
    { tag: '', prop: 'resumeIntroduction', label: '自我介绍', value: '' },
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
    { label: '博士生' },
    { label: '硕士生' },
    { label: '本科生' },
    { label: '专科生' },
    { label: '大学以下' },
];
// 工资选项
const salaryOptions = [
    { range: "面议" }, { range: "1k" }, { range: "2k" }, { range: "3k" },
    { range: "4k" }, { range: "5k" }, { range: "6k" }, { range: "7k" }, { range: "8k" },
    { range: "9k" }, { range: "10k" }, { range: "11k" }, { range: "12k" }, { range: "13k" }
];
const salaryRange = reactive([]); // 工资范围(例如：['1k', '5k'])

// 自定义表单验证规则
const validatePolitical = (rule, val, callback) => {
    if (!val || val === '--请选择--') {
        callback(new Error('请选择您的政治面貌'));
    } else {
        callback();
    }
}
const validateGender = (rule, value, callback) => {
    if (!value) {
        callback(new Error('请选择性别'));
    } else {
        callback();
    }
}
const validateBirth = (rule, value, callback) => {
    if (!value) {
        callback(new Error('请选择出生日期'));
    } else {
        callback();
    }
}
const validateMarriage = (rule, value, callback) => {
    if (!value) {
        callback(new Error('请选择婚姻状况'));
    } else {
        callback();
    }
}
const validateSalary = (rule, value, callback) => {
    if (!salaryRange[0] || !salaryRange[1]) {
        callback(new Error('请选择期望薪资范围'));
    } else if (parseInt(salaryRange[1].replace('k', '')) < parseInt(salaryRange[0].replace('k', ''))) {
        callback(new Error('薪资范围上限不能小于下限'));
    } else {
        callback();
    }
}

// 表单数据验证规则
const formRules = {
    userName: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 10, message: '用户名长度在 3 到 10 个字符', trigger: 'blur' }
    ],
    userPassword: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 16, message: '密码长度在 6 到 16 个字符', trigger: 'blur' }
    ],
    userRole: [
        { required: true, message: '请选择角色', trigger: 'change' }
    ],
    userAvatar: [
        { required: false, message: '请上传头像', trigger: 'change' }
    ],
    resumePhone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
    ],
    resumeEmail: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱', trigger: 'blur' }
    ],
    resumeGender: [
        { validator: validateGender, required: true, message: '请选择性别', trigger: 'change' }
    ],
    resumeBirth: [
        { validator: validateBirth, required: true, message: '请选择出生日期', trigger: 'change' }
    ],
    resumeMarriage: [
        { validator: validateMarriage, required: true, message: '请选择婚姻状况', trigger: 'change' }
    ],
    resumePolitical: [
        { validator: validatePolitical, required: true, message: '请选择政治面貌', trigger: 'change' }
    ],
    resumeCollege: [
        { required: true, message: '请输入毕业院校', trigger: 'blur' },
        { min: 2, max: 50, message: '毕业院校长度在 2 到 50 个字符', trigger: 'blur' }
    ],
    resumeMajor: [
        { required: true, message: '请输入专业', trigger: 'blur' },
        { min: 2, max: 50, message: '专业长度在 2 到 50 个字符', trigger: 'blur' }
    ],
    resumeEducation: [
        { required: true, message: '请选择学历', trigger: 'change' }
    ],
    resumeExperience: [
        { required: false, message: '请输入工作经验', trigger: 'blur' },
        { max: 500, message: '工作经验太长了，最多 500 个字符', trigger: 'blur' }
    ],
    resumeProject: [
        { required: false, message: '请输入项目经验', trigger: 'blur' },
        { max: 500, message: '项目经验太长了，最多 500 个字符', trigger: 'blur' }
    ],
    resumeJob: [
        { required: true, message: '请输入工作职位', trigger: 'blur' },
        { min: 2, max: 50, message: '工作职位长度在 2 到 50 个字符', trigger: 'blur' }
    ],
    resumeSalary: [
        { validator: validateSalary, required: true, message: '请输入期望薪资', trigger: 'blur' },
    ],
    resumeSkills: [
        { required: true, message: '请输入专业技能', trigger: 'blur' },
        { max: 500, message: '专业技能太长了，最多 500 个字符', trigger: 'blur' }
    ],
    resumeIntroduction: [
        { required: true, message: '请输入自我介绍', trigger: 'blur' },
        { min: 5, max: 500, message: '自我介绍长度在 5 到 500 个字符', trigger: 'blur' }
    ]
};

// 表单数据对象填充函数
const createObject = (data, fn) => {
    const k = formItems.flatMap((item) => item.prop);
    for (let i = 0; i < k.length; i++) {
        data[k[i]] = null;
    }
    fn(data);
    return data;
}



// 表单数据对象
const formData = reactive({});
createObject(formData, (data) => {
    data.resumePolitical = politicalOptions[0].label;
    data.resumeEducation = educationOptions[0].label;
    data.userRole = 5;
    delete data.userAvatar;
})

// 表单显示对象
const formDisplay = reactive({});
(() => {
    const k = formItems.flatMap((item) => item.prop);
    const v = formItems.flatMap((item) => item.label);
    for (let i = 0; i < k.length; i++) {
        formDisplay[k[i]] = v[i];
    }
    delete formDisplay.userAvatar;
    delete formDisplay.userRole;
})();

// 表单实例
const refForm = ref(null);
// 表单提交函数
const handleSubmit = async () => {
    refForm.value.validate(async (valid) => {
        if (!valid) {
            return;
        }
        formData.resumeSalary = salaryRange.join('-');
        console.log(formData);
        const res = await userRegister(formData);
        ElMessage.success(res.message);
    });
}
// 表单取消函数
const handleCancel = () => {
    refForm.value.resetFields();
}

</script>

<template>
    <div class="register-container">
        <WBForm :model="formData" ref="refForm" :items="formDisplay" :rules="formRules" inline class="register-form">
            <template #default="scope">
                <!-- 个人介绍 -->
                <el-input v-if="scope.key === 'resumeIntroduction' ||
                    scope.key === 'resumeExperience' ||
                    scope.key === 'resumeProject' || scope.key === 'resumeSkills'" v-model="formData[scope.key]"
                    type="textarea"></el-input>
                <!-- 政治面貌 -->
                <el-select v-else-if="scope.key === 'resumePolitical'" v-model="formData[scope.key]" placeholder="请选择政治面貌"
                    size="large" :style="{ width: '190px' }">
                    <el-option v-for="(item, index) in politicalOptions" :key="index" :label="item.label"
                        :value="item.label"></el-option>
                </el-select>
                <!-- 性别 -->
                <el-select v-else-if="scope.key === 'resumeGender'" :style="{ width: '190px' }"
                    v-model="formData.resumeGender" placeholder="请选择性别">
                    <el-option label="男" value="男"></el-option>
                    <el-option label="女" value="女"></el-option>
                </el-select>
                <!-- 密码 -->
                <el-input v-else-if="scope.key === 'userPassword'" v-model="formData[scope.key]" type="password"
                    show-password placeholder="请输入密码"></el-input>
                <!-- 生日 -->
                <el-date-picker v-else-if="scope.key === 'resumeBirth'" v-model="formData[scope.key]" type="date"
                    placeholder="选择出生日期" :style="{ width: '190px' }" />
                <!-- 婚姻状况 -->
                <el-select v-else-if="scope.key === 'resumeMarriage'" v-model="formData[scope.key]"
                    placeholder="选择婚姻状况" :style="{ width: '190px' }">
                    <el-option label="未婚" value="未婚"></el-option>
                    <el-option label="已婚" value="已婚"></el-option>
                </el-select>
                <!-- 学历 -->
                <el-select v-else-if="scope.key === 'resumeEducation'" v-model="formData[scope.key]"
                    placeholder="选择学历" :style="{ width: '190px' }">
                    <el-option v-for="(item, index) in educationOptions" :key="index" :label="item.label"
                        :value="item.label"></el-option>
                </el-select>
                <!-- 期待薪资 -->
                <span v-else-if="scope.key === 'resumeSalary'">
                    <el-select v-model="salaryRange[0]" placeholder="请选择" size="large" :style="{ width: '100px' }">
                        <el-option v-for="(item, i) in salaryOptions" :key="i" :label="item.range"
                            :value="item.range"></el-option>
                    </el-select>
                    <em style="color: #999;margin: 0 15px;vertical-align: sub;">至</em>
                    <el-select v-model="salaryRange[1]" placeholder="请选择" size="large" :style="{ width: '100px' }">
                        <el-option v-for="(item, i) in salaryOptions" :key="i" :label="item.range"
                            :value="item.range"></el-option>
                    </el-select>
                </span>
                <!-- 其他输入框 -->
                <el-input v-else v-model="formData[scope.key]"></el-input>
            </template>
        </WBForm>
        <div class="register-btn">
            <el-button size="large" @click="handleCancel">重置</el-button>
            <el-button size="large" type="primary" @click="handleSubmit">提交</el-button>
        </div>
    </div>
</template>


<style lang="scss" scoped>
.register-container {
    max-width: 800px;
    margin: 5vh auto;
    padding: 20px 0 0 60px;
    background-color: #fff;
    border-radius: 10px;
    position: relative;
}

</style>
