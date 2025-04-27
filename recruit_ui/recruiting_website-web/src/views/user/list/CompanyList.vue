<script setup>
import WBList from '@/components/WBList.vue'
import { reactive, ref, watchEffect } from 'vue'
import { useSearchStore, SearchCondition } from '@/store/searchStore'
import { companyList } from '@/api/user/UserApi'
import { ElMessage, ElLoading } from 'element-plus';


// 列表数据
const pagination = reactive({}); // 分页数据
const result = ref(useSearchStore().getResult);
const props = defineProps({
    condition: { type: Object, default: {} }
});

// 获取列表数据
const getCompanyList = async (condition) => {
    const loading = null;
    // 分页数据赋值
    try {
        // loading = ElLoading.service({
        //     lock: true,
        //     text: '正在加载中...',
        //     background: 'rgba(0, 0, 0, 0.7)'
        // });
        const data = await companyList(new SearchCondition(condition.keyword, condition.page, condition.size, condition.isAsc));
        pagination.current = data.content.current;
        pagination.pages = data.content.pages;
        pagination.size = data.content.size;
        pagination.total = data.content.total;
        // 列表数据赋值
        result.value = data.content.records;
        useSearchStore().setResult('COMPANY', data.content.records);
    } catch (error) {
        ElMessage.warning('网络繁忙，请稍后再试!');
    } finally {
        loading?.close();
    }
}


// 监听搜索条件变化
watchEffect(() => {
    getCompanyList(props.condition);
});

// 跳转到详情页
const handleCompanyJobs = (item) => {
    props.condition.companyId = item.companyInfoId;
    emits('click-company', item.companyInfoName);
}

/*====================== 分页相关 ======================*/
const emits = defineEmits(['click-company', 'update:page-size', 'update:current-page', 'click', 'update:result']);
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
    <div class="companys-container">
        <!-- 列表 -->
        <WBList v-if="result.length" @click="handleCompanyJobs" :list="result" v-model:current-page="pagination.current"
            v-model:page-size="pagination.size" :background="true" :total="pagination.total"
            @update:page-size="handleSizeChange" @update:current-page="handleCurrentChange">
            <template #item-prepend>
                <el-image style="width: 100px; height: 100px;" :src="result.companyLogo" fit="fill">
                    <template #error="">
                        <img src="/public/company.png" alt="默认公司logo">
                    </template>
                </el-image>
            </template>
            <template #default="item">
                <span class="companys-desc">
                    <h1>{{ item.companyInfoName }}</h1>
                    <p>{{ item.companyInfoProfile }}</p>
                </span>
            </template>
            <template #item-right>
                <p class="companys-create_date">&emsp;</p>
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
    margin: 0 0 15px 0;
}
</style>