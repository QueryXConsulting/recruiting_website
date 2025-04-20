<script setup>
import WBForm from "@/components/WBForm.vue";
import { ref, reactive } from "vue";
import { userRegister } from '@/api/user/UserApi';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';

const router = useRouter();
// 表单根数据
const formItems = reactive([
    { tag: '', prop: 'userName', label: '姓名', value: '' },
    { tag: '', prop: 'userPhone', label: '手机号', value: '' },
    { tag: '', prop: 'userEmail', label: '邮箱', value: '' },
    { tag: '', prop: 'userPassword', label: '密码', value: '' },
]);

// 表单数据验证规则
const formRules = {
    userName: [
        { required: true, message: '请输入姓名', trigger: 'blur' },
        { min: 2, max: 10, message: '用户名长度在 2 到 10 个字符', trigger: 'blur' }
    ],
    userPassword: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 16, message: '密码长度在 6 到 16 个字符', trigger: 'blur' }
    ],
    userPhone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
    ],
    userEmail: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱', trigger: 'blur' }
    ],
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

// 表单显示对象
const formDisplay = reactive({});
(() => {
    const k = formItems.flatMap((item) => item.prop);
    const v = formItems.flatMap((item) => item.label);
    for (let i = 0; i < k.length; i++) {
        formDisplay[k[i]] = v[i];
    }
})();

// 表单实例
const refForm = ref(null);
// 表单提交函数
const handleSubmit = async () => {
    refForm.value.validate(async (valid) => {
        if (!valid) {
            return;
        }
        const res = await userRegister(formData);
        ElMessage.success(res.message);
        if (res.message === '操作成功') {
            router.push('/auth/login');
        }
    });


}
// 表单取消函数
const handleCancel = () => {
    refForm.value.resetFields();
}

</script>

<template>
    <div class="register-container">

        <div class="register-form">
            <h2 class="register-title">注册</h2>
            <WBForm :model="formData" ref="refForm" :items="formDisplay" :rules="formRules">
                <template #default="scope">
                    <el-input v-if="scope.key === 'userPassword'" v-model="formData[scope.key]" type="password"
                        show-password placeholder="请输入信息"></el-input>
                    <el-input v-else v-model="formData[scope.key]" placeholder="请输入信息"></el-input>
                </template>
            </WBForm>
            <i style="height: 10px;">&nbsp;</i>
            <!-- 按钮 -->
            <el-row justify="center" class="register-btn">
                <el-button size="large" @click="handleCancel">重置</el-button>
                <i style="width: 50px;">&nbsp;</i>
                <el-button size="large" type="primary" @click="handleSubmit">提交</el-button>
            </el-row>
        </div>
    </div>
</template>


<style lang="scss" scoped>
.register-container {
    padding: 15vh 0px 0px 0px;
}

.register-form {
    max-width: 600px;
    max-height: 500px;
    margin: 0px auto 0px auto;
    padding: 60px;
    background-color: #fff;
    border-radius: 10px;
    position: relative;
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
}

.register-title {
    font-size: 32px;
    font-weight: bold;
    text-align: center;
    margin-bottom: 40px;
}
</style>
