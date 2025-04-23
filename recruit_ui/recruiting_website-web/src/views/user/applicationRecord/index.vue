<script setup>
import { defineStore } from 'pinia';
import userStore from '@/store/user';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import ApplicationRecord from './ApplicationRecord.vue';
import AppointmentInterview from './AppointmentInterview.vue';
import ConfirmOffer from './ConfirmOffer.vue';
import UploadMaterials from './UploadMaterials.vue';
import InformationInput from './InformationInput.vue';
import AppointmentRegistration from './AppointmentRegistration.vue';

// const store = userStore();
// const router = useRouter();
// if (!store.userInfo.resumeId){
//     ElMessage.error('请先补充简历');
//     router.push('/users/userInfo');
// }
const useTabStore = defineStore('tab', {
    state: () => {
        return {
            tabIndex: '0',
        }
    },
    actions: {
        setTabIndex(index) {
            this.tabIndex = index;
        },
    },
    getters: {
        getTabIndex() {
            return this.tabIndex;
        },
    },
    persist: {
        key: 'tab',
        storage: sessionStorage,
        paths: ['tabIndex'],
    },
});

const activeName = useTabStore().getTabIndex; // tab 切换

const clickTab = (pane, e) => {
    useTabStore().setTabIndex(pane.index);
}
</script>

<template>
    <div style="padding: 20px;">
        <el-tabs v-model="activeName" @tab-click="clickTab" type="border-card">
            <el-tab-pane name="0" label="应聘记录" lazy>
                <ApplicationRecord></ApplicationRecord>
            </el-tab-pane>
            <el-tab-pane name="1" label="预约面试" lazy>
                <AppointmentInterview></AppointmentInterview>
            </el-tab-pane>
            <el-tab-pane name="2" label="确认offer" lazy>
                <ConfirmOffer></ConfirmOffer>
            </el-tab-pane>
            <el-tab-pane name="3" label="上传材料" lazy>
                <UploadMaterials></UploadMaterials>
            </el-tab-pane>
            <el-tab-pane name="4" label="信息录入" lazy>
                <InformationInput></InformationInput>
            </el-tab-pane>
            <el-tab-pane name="5" label="预约报到" lazy>
                <AppointmentRegistration></AppointmentRegistration>
            </el-tab-pane>
        </el-tabs>
    </div>
</template>

<style lang="scss" scoped></style>
