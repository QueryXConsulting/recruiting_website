<script setup>
import { reactive, ref, watchEffect } from 'vue'
import router from '@/router/index'
import WBList from '@/components/WBList.vue'
import useSearchStore from '@/store/searchStore'
import { jobList } from '@/api/user/UserApi'


// 列表数据
const condition = ref(useSearchStore().getConditions("JOB"));
const result = ref(useSearchStore().getResult);
const pagination = reactive({}); // 分页数据

// 获取列表数据
const getJobList = async (condition) => {
    const data = await jobList(condition.keyword, condition.page, condition.size, condition.isAsc);
    // 分页数据赋值
    pagination.current = data.content.current;
    pagination.pages = data.content.pages;
    pagination.size = data.content.size;
    pagination.total = data.content.total;
    // 列表数据赋值    
    result.value = data.content.records;
}
// 监听搜索条件变化
watchEffect(() => {
    getJobList(condition.value);
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
        <WBList :list="result" @click="viewDetail" v-model:current-page="pagination.current"
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
    </div>
</template>

<style lang="scss" scoped>
h1,
h2,
h3 {
    font: none;
    font-size: 24px;
    font-weight: bold;
    margin: 0 0 15px 0;
}

.search-container {
    display: flex;
    margin: 10px;
    border-radius: 15px;
    align-items: center;
    // flex-direction: column;
    justify-content: center;
    padding: 20px 10px 20px;
    background: #fff;

    .search-input {
        width: 80%;
        margin: 10px;

        &:hover {
            transition: all 0.3s ease-in-out;
            box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
        }
    }

    .search-other {
        display: flex;
        justify-content: start;
    }
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

// }

.jobs-other {
    display: flex;
    align-items: center;
}

.jobs-salary {
    color: #f26d49;
    font-size: 20px;
}
</style>