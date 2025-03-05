<script setup>
import { ref, reactive, onMounted } from 'vue';
import WBTable from '@/components/table/WBTable.vue';
import WBDialog from '@/components/WBDialog.vue';
import WBForm from '@/components/WBForm.vue';
import { offer, offerFilePath, offerList, offerStatus } from '@/api/user/UserApi';
import { ElMessage } from 'element-plus';

const currentPage = ref(1);// 当前页
const pageSize = ref(10);// 每页显示条数
const total = ref(0);// 总条数
const pages = ref(0);// 总页数

// 表格根数据
const rootData = reactive([
    { tag: '', prop: "jobId", label: "职位ID" },
    { tag: '', prop: "userId", label: "用户ID" },
    { tag: '', prop: "offerId", label: "offerID" },
    { tag: '', prop: "jobPosition", label: "面试岗位" },
    { tag: '', prop: "companyInfoName", label: "公司名" },
    { tag: '', prop: "offersDate", label: "发送时间" },
    { tag: '', prop: "offersStatus", label: "offer状态" },
    // { tag: 'info', prop: "resumeName", label: "简历名" },
    // { tag: 'table, info', prop: "deliverDate", label: "投递日期" },
    // { tag: 'info', prop: "resumeType", label: "简历类型" },
]);
// 对象数据填充
const createObject = (val, data, fn) => {
    const k = rootData.flatMap((item) => item.prop);
    const v = rootData.flatMap((item) => item[val]);
    for (let i = 0; i < k.length; i++) {
        data[k[i]] = v[i];
    }
    fn(data);
    return data;
}

// 表格列显示
const tableColumns = reactive([]);
(() => {
    const o = rootData.filter((item) => !item.prop.includes('Id'));
    for (let i = 0; i < o.length; i++) {
        tableColumns.push({ prop: o[i].prop, label: o[i].label });
    }
})()

// 表格数据
const tableData = ref([]);
const getOfferList = async () => {
    const res = await offerList(currentPage.value, pageSize.value);
    tableData.value = res.content.records;
    currentPage.value = res.content.current;
    pageSize.value = res.content.size;
    total.value = res.content.total;
    pages.value = res.content.pages;
    ElMessage.success(res.message);
}
onMounted(async () => {
    getOfferList();
})

// 获取选项label
const getOptionLabel = (options, value) => {
    const option = options.find((item) => item.value === value);
    return option ? option.label : value;
}
// 获取tag类型
const getTagType = (options, value) => {
    const option = options.find((item) => item.value === value);
    return option ? option.tag : 'default';
}


// 应聘状态选项  0待发送 1是已接受 2拒绝 3撤销
const statusOptions = [
    { tag: 'warning', value: '0', label: '待发送' },
    { tag: 'success', value: '1', label: '已接受' },
    { tag: 'danger', value: '2', label: '拒绝' },
    // { tag: '', value: '3', label: '撤销' },
]
// 表格右侧操作栏
const hasOperation = ref(true);// 是否有操作栏
const tableOperation = [
    { type: 'default', text: '查看' },
    { type: 'danger', text: '拒绝' },
    { type: 'success', text: '接受' },
]
// const filteredOperation = tableOperation;
const getStatusOptionsLabel = (value) => {
    if (value !== '0') {
        // filteredOperation = [];
        hasOperation.value = false;
    }
    return getOptionLabel(statusOptions, value);
}
// 面试类型选项
const typeOptions = [
    { tag: 'default', value: '0', label: '线上' },
    { tag: 'info', value: '1', label: '线下' },
    // { tag: 'danger', value: '2', label: '视频面试' }
]



/*================= 详情弹窗 =================*/
const formData = ref({});
const formItems = reactive({});
createObject('label', formItems, (data) => {
    delete data.userId;
    delete data.resumeId;
});
const isShowDialog = ref(false);// 详情弹窗是否显示

// 表格点击事件
const handleOperationClick = async (btnIndex, row, text) => {
    // console.log(btnIndex, row, text);
    switch (btnIndex) {
        case 0:
            // 查看详情
            const _filePath = await offerFilePath(row.offerId);
            console.log(_filePath);
            break;
        case 1:
            // 拒绝
            const _updateResult = await offerStatus(row.offerId, 2);
            console.log('拒绝', _updateResult);
            if (_updateResult) {
                getOfferList();
            }
            break;
        case 2:
            // 接受
            // const res = await acceptOffer(row.offerId);

            // getOfferList();
            break;
        default:
            break;
    }

}


// 一页条数变化时触发
const handleSizeChange = (size) => {
    // console.log('update:page-size', size)
    pageSize.value = size
}
// 当前页码变化时触发
const handleCurrentChange = (page) => {
    // console.log('update:current-page', page)
    currentPage.value = page
}


</script>

<template>
    <div>
        <WBTable :total="total" @update:currentPage="handleCurrentChange" @update:page-size="handleSizeChange"
            :table-columns="tableColumns" v-model:tableData="tableData" v-model:currentPage="currentPage"
            :operation-list="tableOperation" @operation-click="handleOperationClick" :has-operation="hasOperation"
            v-model:pageSize="pageSize" border>
            <template #default="scope">
                <el-tag v-if="scope.prop === 'offersStatus'"
                    :type="getTagType(statusOptions, tableData[scope.$index][scope.prop])">
                    {{ getStatusOptionsLabel(tableData[scope.$index][scope.prop]) }}
                </el-tag>
                <span v-else>{{ tableData[scope.$index][scope.prop] }}</span>
            </template>
            <!-- 表格右侧操作栏 -->
            <!-- <template #operation="scope">
                <el-button v-for="(item, index) in tableOperation" :type="item.type" :key="index" size="small"
                    @click="handleOperationClick(index, scope.row, item.text)">
                    {{ item.text }}
                </el-button>
            </template> -->
        </WBTable>

        <!-- 面试时间选择窗口 -->
        <WBDialog v-model="isShowDialog" @submit="isShowDialog = false" @cancel="isShowDialog = false" title="详情信息"
            width="40%" draggable>
            <WBForm v-model="formData" :items="formItems" class="form-detail">
                <template #default="scope">
                    <span v-if="scope.key === 'resumeStatus'">
                        {{ getOptionLabel(statusOptions, formData[scope.key]) }}
                    </span>
                    <span v-else-if="scope.key === 'resumeType'">
                        {{ getOptionLabel(typeOptions, formData[scope.key]) }}
                    </span>
                    <span v-else-if="scope.key === 'resumeDelete'">{{ formData[scope.key] === '0' ? '不合适' : '已通过' }}</span>
                    <span v-else>{{ formData[scope.key] }}</span>
                </template>
            </WBForm>
        </WBDialog>
    </div>
</template>

<style lang="scss" scoped>
.form-detail {
    display: flex;
    justify-content: center;
}


.step-icon {
    border-radius: 50%;
    border: 2px solid rgb(168, 171, 178);
    width: 24px;
    height: 24px;
    background: transparent;
}
</style>