<script setup>
import { reactive, ref } from 'vue';
import applicationRecord from '@/views/user/applicationRecord/ApplicationRecord.vue';
import AppointmentInterview from './AppointmentInterview.vue';

const activeName = ref('0');// tab 切换

</script>

<template>
    <div style="padding: 20px;">
        <el-tabs v-model="activeName" type="border-card">
            <el-tab-pane name="0" label="应聘历史">
                <applicationRecord >
                </applicationRecord>
            </el-tab-pane>

            <el-tab-pane name="1" label="预约面试">
                <AppointmentInterview></AppointmentInterview>
            </el-tab-pane>
            <el-tab-pane name="2" label="确认offer">Role</el-tab-pane>
            <el-tab-pane name="3" label="上传材料">Task</el-tab-pane>
            <el-tab-pane name="4" label="录入信息">Task</el-tab-pane>
            <el-tab-pane name="5" label="预约报到">Task</el-tab-pane>
        </el-tabs>
    </div>
</template>

<style lang="scss" scoped></style>