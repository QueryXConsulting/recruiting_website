<script setup lang="js">
import { ElMessage } from "element-plus";
import { ref, reactive, defineProps, defineModel, watch } from 'vue';
import { adminUserInfo } from '@/api/admin/adminApi';
const userInfo = ref({});
// 表单字段
const formFields = ref([
    { label: "用户头像", prop: "userAvatar" },
    { label: "用户名", prop: "userName" },
    { label: "手机号", prop: "userPhone" }
]);
// 简历查询参数
// const adminResumeDto = reactive({
//     resumeId: 1,
//     resumeType: 1
// });
const id = defineProps(['userId'])
const getAdminUserInfo = async (userId) => {
    let res = null;
    try {
        res = await adminUserInfo(userId);
    } catch (error) {
        ElMessage.error(error.message);
        return;
    }
    if (res.code !== 200) {
        ElMessage.error(res.message);
        return;
    }
    console.log(res.content);
    userInfo.value = res.content;
};
getAdminUserInfo(id.userId);

// 查询在线简历
const queryResume = async (adminResumeDto) => {
//     const res = await adminUserResume({
//         page: 1,
//         size: 10,
//         userName: '',
//         resumeReview: 1,
//         resumeStatus: 0,
//         resumeType: 0
//     });
//     console.log('查询在线简历', res);
};

// 监听userId变化
watch(() => id.userId, (newVal, oldVal) => {
    if (newVal !== oldVal) {
        getAdminUserInfo(newVal);
    }
});
</script>

<template>
    <div class="drawer-main">
        <div>
            <el-form :model="userInfo.value">
                <el-form-item v-for="(field, index) in formFields" :key="index" :label="`${field.label}：`"
                    :prop="field.prop" label-position="right" label-width="100px" size="large">
                    <el-input v-model="userInfo[field.prop]" />
                </el-form-item>
            </el-form>
        </div>

        <div>
            <el-button type="primary" size="large" @click="queryResume()">在线简历</el-button>
            <el-button type="primary" size="large">投递岗位</el-button>
            <el-button type="primary" size="large">面试信息</el-button>
            <!-- <el-button type="primary" size="large">关闭</el-button> -->
        </div>
    </div>
</template>

<style scoped lang="scss">
.drawer-main {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
}
</style>
