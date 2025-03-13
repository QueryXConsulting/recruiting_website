<script setup>
import { ref, onMounted, reactive } from 'vue';
import { registrationStatus, reservationInfo, reservationUpdateStatus } from '@/api/user/UserApi';
import { ElMessage } from 'element-plus';


const reservationStatus = ref(null);// 预约状态
const reservationStatusMessage = ref(null);// 页面状态提示信息
const reservationResult = reactive({});// 报到信息
let date = null;

// 0待发送  1已发送 2已接受 3拒绝
const reservationStatusOptions = [
    { value: 0, label: '待发送' },
    { value: 1, label: '您的申请已发送，请等待审核！' },
    { value: 2, label: '恭喜您，对方已经通过您的申请！' },
    { value: 3, label: '很抱歉，您的申请已被对方拒绝！' }
];


onMounted(async () => {
    const response = await registrationStatus();
    reservationStatus.value = +response.content.reservationStatus;
    reservationStatusMessage.value = reservationStatusOptions.find(item => item.value === +response.content.reservationStatus).label;
    const result = await reservationInfo();
    if (result.code !== 200) {
        ElMessage.error(result.message);
        return;
    }
    date = new Date(result.content.hireDate);
    reservationResult.date = date.toLocaleDateString();
    reservationResult.companyName = result.content.companyInfoName;
    reservationResult.positionName = result.content.position;
    reservationResult.jobArea = result.content.jobArea;
    reservationResult.userName = result.content.userName;

});

// 更新状态
const updateStatus = async (status) => {
    // 修改状态
    const isUpdated = await reservationUpdateStatus(status);
    if (!isUpdated || isUpdated.code !== 200) {
        ElMessage.error('更新状态失败，请稍后再试！');
        return;
    }
    // 更新页面状态
    const response = await registrationStatus(status);
    reservationStatus.value = +response.content.reservationStatus;
    reservationStatusMessage.value = reservationStatusOptions.find(item => item.value === +response.content.reservationStatus).label;
};
</script>

<template>
    <div v-if="!reservationStatus || reservationStatus === 1">
        <!-- <h1>请选择报到时间</h1> -->
        <main class="container">
            <section class="left">
                <el-calendar v-model="date" style="height: 40vh;" />
            </section>
            <el-card class="right" style="max-width: 480px">
                <template #header>
                    <h3>{{ reservationResult.positionName }}报到</h3>
                </template>
                <p class="desc">
                    &emsp;&emsp;您好，<mark>{{ reservationResult.userName }}</mark>，恭喜您通过<mark>{{ reservationResult.companyName
                    }}</mark>关于您就任<mark>{{ reservationResult.positionName }}</mark>这一职位的评估请您于
                    <mark>{{ reservationResult.date }}</mark>报到，请准时到岗。
                </p>
                <template #footer>
                    <el-row class="footer" justify="center">
                        <el-col style="display: flex; justify-content: center; align-items: center;" class="form-item">
                            <el-button type="danger" size="large" @click="updateStatus(3)">拒绝</el-button>
                            <span style="width: 100px;"></span>
                            <el-button type="primary" size="large" @click="updateStatus(2)">接受</el-button>
                        </el-col>
                    </el-row>
                </template>
            </el-card>
            <!-- <section class="right">
                <h3>{{ reservationResult.positionName }}报到</h3>
                <p class="desc">
                    &emsp;&emsp;您好，<mark>{{ reservationResult.userName }}</mark>，恭喜您通过<mark>{{ reservationResult.companyName
                    }}</mark>关于您就任<mark>{{ reservationResult.positionName }}</mark>这一职位的评估请您于
                    <mark>{{ reservationResult.date }}</mark>报到，请准时到岗。
                </p>
                <el-row class="footer" justify="center">
                    <el-col style="display: flex; justify-content: center; align-items: center;" class="form-item">
                        <el-button type="danger" size="large" @click="resetForm">拒绝</el-button>
                        <span style="width: 100px;"></span>
                        <el-button type="primary" size="large" @click="submitForm">接受</el-button>
                    </el-col>
                </el-row>
            </section> -->
        </main>

    </div>

    <!-- 页面状态提示-已发送(待审核) -->
    <!-- <div v-if="reservationStatus === 1" class="reservation-status">
        <el-result icon="warning" :title="reservationStatusMessage"></el-result>
    </div> -->
    <!-- 页面状态提示-接受(通过) -->
    <div v-if="reservationStatus === 2" class="reservation-status">
        <el-result icon="success" :title="reservationStatusMessage"></el-result>
    </div>
    <!-- 页面状态提示-拒绝 -->
    <div v-if="reservationStatus === 3" class="reservation-status">
        <el-result icon="error" :title="reservationStatusMessage"></el-result>
    </div>
</template>

<style lang="scss" scoped>
h1,
h2,
h3 {
    font: none;
    padding: 30px 0 10px 0;
    font-size: 24px;
    text-align: center;
}

.container {
    margin: 0 auto;
    padding: 0 20px;
    min-height: 70vh;
    display: flex;

    .left {
        width: 60%;
        height: 630px;
    }

    .right {
        width: 40%;
        padding: 20px;
        border-left: none;

        h3 {
            font-size: 30px;
            // padding: 0 0 40px 0;
        }

        .desc {
            font-size: 20px;
            line-height: 1.5;
            height: 320px;
            mark {
                background: none;
                color: #E6A23C;
            }
        }
    }

    // .footer {
    //     margin: 0px 0px 10px 0px;
    // }
}

.reservation-status {
    width: 100%;
    height: 400px;
    display: flex;
    justify-content: center;
    align-items: center;
}
</style>