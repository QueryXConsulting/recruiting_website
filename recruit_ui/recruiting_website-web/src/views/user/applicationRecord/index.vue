<script setup>
import { reactive, ref } from 'vue';
import { defineStore } from 'pinia';
import ApplicationRecord from './ApplicationRecord.vue';
import AppointmentInterview from './AppointmentInterview.vue';
import ConfirmOffer from './ConfirmOffer.vue'

const useTabStore = defineStore('tab', {
    state: () => ({
        tabIndex: '0',
    }),
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


const activeName = useTabStore().getTabIndex;// tab 切换


const cilckTab = (pane, e) => {
    useTabStore().setTabIndex(pane.index);
}

</script>

<template>
    <div style="padding: 20px;">
        <el-tabs v-model="activeName" @tab-click="cilckTab" type="border-card">
            <el-tab-pane name="0" label="应聘记录">
                <component :is="activeName === '0'? ApplicationRecord : null"></component>
                <!-- <ApplicationRecord >
                </ApplicationRecord> -->
            </el-tab-pane>

            <el-tab-pane name="1" label="预约面试">
                <component :is="activeName === '1'? AppointmentInterview : null"></component>
                <!-- <AppointmentInterview></AppointmentInterview> -->
            </el-tab-pane>
            <el-tab-pane name="2" label="确认offer">
                <component :is="activeName === '2'? ConfirmOffer : null"></component>
            </el-tab-pane>
            <el-tab-pane name="3" label="上传材料">Task</el-tab-pane>
            <el-tab-pane name="4" label="录入信息">Task</el-tab-pane>
            <el-tab-pane name="5" label="预约报到">Task</el-tab-pane>
        </el-tabs>
    </div>
</template>

<style lang="scss" scoped></style>