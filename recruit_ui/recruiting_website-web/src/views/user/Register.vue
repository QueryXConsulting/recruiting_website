<script setup>
import WBForm from "@/components/WBForm.vue";
import { ref, reactive } from "vue";
import { userRegister } from '@/api/user/UserApi';
import { ElMessage } from 'element-plus';
import { Cellphone, User, Message, Lock } from '@element-plus/icons-vue';
import userStore from '@/store/user';
import router from '@/router';


const store = userStore();
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
        ElMessage.success('操作成功');
        if (res?.content?.token) {
            console.log(res.content);
            store.token = res.content.token
            store.role = res.content?.userInfoVO?.userRole
            store.userInfo = res.content.userInfoVO
            router.push('/');
        }
    });


}
// 表单取消函数
// const handleCancel = () => {
//     refForm.value.resetFields();
// }

</script>

<template>
    <div class="register-container">

        <div class="register-form">
            <h2 class="register-title">注册</h2>
            <WBForm :model="formData" ref="refForm" :items="formDisplay" :rules="formRules">
                <template #form-items>
                    <!-- 姓名 -->
                    <el-form-item prop="userName">
                        <el-input v-model="formData.userName" type="text" placeholder="请输入您的姓名" size="large" maxlength="20">
                            <template #prefix>
                                <el-icon>
                                    <User />
                                </el-icon>
                            </template>
                        </el-input>
                    </el-form-item>
                    <!-- 手机号 -->
                    <el-form-item prop="userPhone">
                        <el-input v-model="formData.userPhone" type="tel" placeholder="请输入手机号" size="large" maxlength="20">
                            <template #prefix>
                                <el-icon class="input-icon">
                                    <Cellphone />
                                </el-icon>
                            </template>
                        </el-input>
                    </el-form-item>
                    <!-- 邮箱 -->
                    <el-form-item prop="userEmail">
                        <el-input v-model="formData.userEmail" type="email" placeholder="请输入邮箱" size="large" maxlength="20">
                            <template #prefix>
                                <el-icon>
                                    <Message />
                                </el-icon>
                            </template>
                        </el-input>
                    </el-form-item>
                    <!-- 密码 -->
                    <el-form-item prop="userPassword">
                        <el-input v-model="formData.userPassword" type="password" placeholder="请输入密码" size="large"
                            maxlength="20">
                            <template #prefix>
                                <el-icon class="input-icon">
                                    <Lock />
                                </el-icon>
                            </template>
                        </el-input>
                    </el-form-item>
                </template>
            </WBForm>
            <!-- 按钮 -->
            <div style="display: flex; justify-content: center;margin-bottom: 30px;">
                <el-button size="large" @click="handleSubmit" class="register-btn">注&emsp;册</el-button>
            </div>
            <div style="display: flex; justify-content: space-between;">
                <el-link :underline="false" href="/" type="warning" class="link-text">首页</el-link>
                <el-link :underline="false" href="/auth/login" type="warning" class="link-text">登录</el-link>
            </div>
        </div>
    </div>
</template>


<style lang="scss" scoped>
.register-container {
    padding: 15vh 0px 0px 0px;
}

.back-arrow {
    position: absolute;
    top: 20px;
    left: 20px;
}

.register-form {
    max-width: 600px;
    max-height: 600px;
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

.register-btn {
    background-color: #FF8C00;
    width: 100%;
    height: 44px;
    font-size: 16px;
    border-radius: 6px;
    font-weight: 500;
    letter-spacing: 1px;
    color: #fff;
}

.register-btn:hover {
    background-color: #FFEFD5;
    border-color: #FF8C00;
}

/* 注册表单 */
:deep(.el-input__wrapper) {
    background-color: #f5f7fa;
    box-shadow: none !important;
    border: 2px solid transparent;
    transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
    border-color: #FF8C00;
    background-color: #fff;
}
</style>
