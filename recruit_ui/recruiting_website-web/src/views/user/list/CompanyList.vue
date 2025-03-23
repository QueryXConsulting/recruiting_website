<script setup>
import WBList from '@/components/WBList.vue'
import { reactive, ref, watchEffect } from 'vue'
import useSearchStore from '@/store/searchStore'
import { companyList } from '@/api/user/UserApi'


// 列表数据
const pagination = reactive({}); // 分页数据
const result = ref(useSearchStore().getResult);
const condition = ref(useSearchStore().getConditions("COMPANY"));

// 获取列表数据
const getCompanyList = async (condition) => {
    const data = await companyList(condition.keyword, condition.page, condition.size, condition.isAsc);
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
    console.log('监听搜索条件变化');
    getCompanyList(condition.value);
});

// 跳转到详情页


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
    <div class="companys-container">
        <!-- 列表 -->
        <WBList :list="result" v-model:current-page="pagination.current"
            v-model:page-size="pagination.size" :background="true" :total="pagination.total"
            @update:page-size="handleSizeChange" @update:current-page="handleCurrentChange">
            <template #item-prepend>
                <el-image style="width: 100px; height: 100px;" :src="result.companyLogo" fit="fill" >
                    <template #error="">
                        <img src="../../../../public/company.png" alt="">
                    </template>
                </el-image>
            </template>
            <template #default="item">
                <span class="companys-desc">
                    <h1>{{ item.companyInfoName }}</h1>
                    <p>{{ item.companyInfoScope }}</p>
                </span>
            </template>
            <template #item-right>
                <p class="companys-create_date">&emsp;</p>
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


.companys-desc {
    // margin: 10px;
    // border: 1px solid #ccc;
}

</style>