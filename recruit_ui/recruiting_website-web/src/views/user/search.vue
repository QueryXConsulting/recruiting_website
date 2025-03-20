<script setup>
import { ref, onMounted } from 'vue'
import useSearchStore from '@/store/searchStore'
import { Search } from '@element-plus/icons-vue'
import CompanyList from './list/CompanyList.vue'
import JobList from '@/views/user/list/JobList.vue'


const inputVal = ref(null); // 搜索关键字
const searchType = ref(useSearchStore().getType); // 搜索类型
const searchObj = useSearchStore().getConditions(searchType.value);
const isAsc = ref(searchObj.isAsc); // 排序方式
// 页面数据初始化
onMounted(() => {
    isAsc.value = searchObj.isAsc;
    inputVal.value = searchObj.keyword;
});


// 搜索
const handleSearch = async () => {
    const o = {
        keyword: inputVal.value,
        page: searchObj.page,
        size: searchObj.size,
        isAsc: isAsc.value,
    };
    // 保存搜索条件
    useSearchStore().setConditions(searchType.value, o);
}

// 翻页
const handleCurrentChange = (currentPage, type) => {
    searchObj.page = currentPage;
}

// 切换每页显示条数
const handleSizeChange = (pageSize, type) => {
    searchObj.size = pageSize;
}




</script>

<template>
    <div>
        <section class="search-container">
            <el-input v-model="inputVal" @keyup.enter="handleSearch" type="search" clearable class="search-input"
                :prefix-icon="Search" placeholder="搜索岗位、公司">
                <template #prepend>
                    <el-select v-model="searchType" placeholder="类型" size="large" style="width: 80px;">
                        <el-option label="岗位" value="JOB" />
                        <el-option label="公司" value="COMPANY" />
                    </el-select>
                </template>
                <template #append>
                    <el-button @click="handleSearch">搜&emsp;索</el-button>
                </template>
            </el-input>
            <!-- 其他条件 -->
            <p class="search-other">
                <el-select v-model="isAsc" @change="searchObj.isAsc = isAsc" placeholder="排序方式" style="width: 80px;">
                    <el-option label="倒序" :value="false" />
                    <el-option label="正序" :value="true" />
                </el-select>
            </p>
        </section>
        <!-- 列表 -->
        <JobList v-if="searchType === 'JOB'" @update:page-size="handleSizeChange($event, searchType)"
            @update:current-page="handleCurrentChange($event, searchType)"></JobList>
        <CompanyList v-else-if="searchType === 'COMPANY'" @update:page-size="handleSizeChange($event, searchType)"
            @update:current-page="handleCurrentChange($event, searchType)"></CompanyList>
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
    margin: 0 0 15px 0;
}

.search-container {
    display: flex;
    margin: 10px;
    border-radius: 15px;
    align-items: center;
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
</style>