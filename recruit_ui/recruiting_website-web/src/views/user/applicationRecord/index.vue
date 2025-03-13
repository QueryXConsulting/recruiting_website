<script setup>
import { reactive, ref } from 'vue';
import { defineStore } from 'pinia';
import ApplicationRecord from './ApplicationRecord.vue';
import AppointmentInterview from './AppointmentInterview.vue';
import ConfirmOffer from './ConfirmOffer.vue';
import UploadMaterials from './UploadMaterials.vue';
import InformationInput from './InformationInput.vue';
import AppointmentRegistration from './AppointmentRegistration.vue';


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
            <!-- 使用component 动态渲染，实现页面懒加载 -->
            <el-tab-pane name="0" label="应聘记录">
                <component :is="activeName === '0'? ApplicationRecord : null"></component>
            </el-tab-pane>

            <el-tab-pane name="1" label="预约面试">
                <component :is="activeName === '1'? AppointmentInterview : null"></component>
            </el-tab-pane>

            <el-tab-pane name="2" label="确认offer">
                <component :is="activeName === '2'? ConfirmOffer : null"></component>
            </el-tab-pane>

            <el-tab-pane name="3" label="上传材料">
                <component :is="activeName === '3'? UploadMaterials : null"></component>
            </el-tab-pane>
            
            <el-tab-pane name="4" label="信息录入">
                <component :is="activeName === '4'? InformationInput : null"></component>
            </el-tab-pane>
            <el-tab-pane name="5" label="预约报到">
                <component :is="activeName === '5'? AppointmentRegistration : null"></component>
            </el-tab-pane>
        </el-tabs>
    </div>
</template>

<style lang="scss" scoped></style>