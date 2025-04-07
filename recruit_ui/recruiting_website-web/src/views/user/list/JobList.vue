<script setup>
import router from '@/router/index'
import WBList from '@/components/WBList.vue'
import { jobList } from '@/api/user/UserApi'
import { reactive, ref, watchEffect } from 'vue'
import { useSearchStore } from '@/store/searchStore'
import { ElMessage } from 'element-plus'


// 列表数据
const props = defineProps({
    condition: { type: Object, default: {} }
});

const result = ref(useSearchStore().getResult('JOB') || []);
const pagination = reactive({}); // 分页数据

// 获取列表数据
const getJobList = async (condition) => {
    const data = await jobList(condition.keyword, condition.page, condition.size, condition.isAsc, condition.education, condition.nature);
    // 分页数据赋值
    try {
        pagination.current = data.content.current;
        pagination.pages = data.content.pages;
        pagination.size = data.content.size;
        pagination.total = data.content.total;
        // 列表数据赋值
        result.value = data.content.records;
        useSearchStore().setResult('JOB', data.content.records);
    } catch (error) { 
        ElMessage.warning('请输入关键字！');
    }
}
// 监听搜索条件变化
watchEffect(() => {
    getJobList(props.condition);
});

// 跳转到详情页
const viewDetail = (val) => {
    router.push({ name: 'JobInfo', query: { jobId: val.jobId } })
}


/*====================== 分页相关 ======================*/
const emits = defineEmits(['update:page-size', 'update:current-page', 'click', 'update:result']);
// 页码改变
const handleCurrentChange = (currentPage) => {
    emits('update:current-page', currentPage)
}
// 页码大小改变
const handleSizeChange = (pageSize) => {
    emits('update:page-size', pageSize)
}


</script>

<template>
    <div class="jobs-container">
        <!-- 列表 -->
        <WBList v-if="result.length" :list="result" @click="viewDetail" v-model:current-page="pagination.current"
            v-model:page-size="pagination.size" :background="true" :total="pagination.total"
            @update:page-size="handleSizeChange" @update:current-page="handleCurrentChange">
            <template #default="item">
                <h1>{{ item.jobPosition }}&emsp;
                    <el-tag size="large" type="warning">{{ item.jobNature }}</el-tag>
                </h1>
                <div class="jobs-other">
                    <span class="jobs-salary">{{ item.jobSalary }}</span>
                    &emsp;|&emsp;
                    <span class="jobs-education">{{ item.jobEducation }}</span>
                    &emsp;|&emsp;
                    <span class="jobs-experience">{{ item.jobExperience }}</span>
                </div>
            </template>
            <template #item-right="item">
                <p class="jobs-company">
                    <a href="javascript:;">{{ item.companyName }}</a>
                </p>
                <p class="jobs-create_date">创建日期：{{ item.jobTime }}</p>
            </template>
        </WBList>
        <el-empty v-else description="没有内容"></el-empty>
    </div>
</template>

<style lang="scss" scoped>
h1,
h2,
h3 {
    font: none;
    font-size: 24px;
    font-weight: bold;
    margin: 0 0 10px 0;
}

.jobs-company {
    margin: 0 0 10px 0;
    font-size: 18px;

    a {
        color: #000;
        font-size: 19px;
        font-weight: bold;
        text-decoration: none;

        &:hover {
            color: #f26d49;
        }
    }
}

.jobs-create_date {
    text-align: right;
    color: #999;
}


.jobs-other {
    display: flex;
    align-items: center;
}

.jobs-salary {
    color: #f26d49;
    font-size: 20px;
}
</style>