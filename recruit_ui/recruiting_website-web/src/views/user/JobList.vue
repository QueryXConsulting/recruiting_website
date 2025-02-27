<script setup>
import { jobList } from '@/api/user/UserApi'
import { reactive, ref } from 'vue'
import router from '@/router/index'

const result = ref([]); // 列表数据
const pagination = reactive({}); // 分页数据

// 获取列表数据
const getJobList = async (keyword, page, pageSize) => {
    const data = await jobList(keyword, page, pageSize)
    // 分页数据赋值
    pagination.current = data.content.current;
    pagination.pages = data.content.pages;
    pagination.size = data.content.size;
    pagination.total = data.content.total;
    // 列表数据赋值
    result.value = data.content.records;
}
getJobList('开发', 1, 10)

// 跳转到详情页
const viewDetail = (id) => {
    router.push({ name: 'JobInfo', query: { jobId: id } })
}

/*====================== 分页相关 ======================*/
// 页码改变
const handleCurrentChange = (currentPage) => {
    // console.log('currentPage', currentPage);
    getJobList(keyword, currentPage, pagination.size)
}
// 页码大小改变
const handleSizeChange = (pageSize) => {
    // console.log('pageSize', pageSize);
    getJobList(keyword, pagination.current, pageSize)
}

</script>

<template>
    <div class="jobs-container">
        <ul class="jobs-list">
            <li @click="viewDetail(item.jobId)" v-for="(item, index) in result" :key="index" class="jobs-item">
                <div class="jobs-item-left">
                    <h1>{{ item.jobPosition }}&emsp;
                        <el-tag size="large" type="warning">{{ item.jobNature }}</el-tag>
                    </h1>
                    <div class="jobs-other">
                        <span class="jobs-salary">{{ item.jobSalary }}</span>
                        &nbsp;&nbsp;|&nbsp;&nbsp;
                        <span class="jobs-education">{{ item.jobEducation }}</span>
                        &nbsp;&nbsp;|&nbsp;&nbsp;
                        <span class="jobs-experience">{{ item.jobExperience }}</span>
                    </div>
                </div>
                <div class="jobs-item-right">
                    <p class="jobs-company">
                        <a href="javascript:;">{{ item.companyName }}</a>
                    </p>
                    <p class="jobs-create_date">创建日期：{{ item.jobTime }}</p>
                </div>
            </li>
        </ul>

        <!-- 分页 -->
        <el-row justify="center">
            <el-pagination v-model:current-page="pagination.current" v-model:page-size="pagination.page"
                :page-sizes="[10, 20, 30, 40, 50]" :background="true" :total="pagination.total"
                @update:page-size="handleSizeChange" @update:current-page="handleCurrentChange"
                layout="total, sizes, prev, pager, next, jumper" />
            <!-- <el-col > -->
            <!-- </el-col> -->
        </el-row>
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

.jobs-list {}

.jobs-item {
    margin: 10px;
    padding: 20px;
    border-radius: 20px;
    background: #fff;
    display: flex;
    justify-content: space-between;
    cursor: pointer;
}

.jobs-item-left {
    // float: left;
}

.jobs-item-right {
    display: flex;
    flex-direction: column;
    justify-content: space-between;

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