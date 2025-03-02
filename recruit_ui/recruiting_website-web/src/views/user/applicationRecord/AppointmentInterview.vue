<script setup>
import { ref, reactive, onMounted } from 'vue';
import WBTable from '@/components/table/WBTable.vue';
import WBDialog from '@/components/WBDialog.vue';
import { interviewList, interviewAccept, interviewDate } from '@/api/user/UserApi';
import { ElMessage } from 'element-plus';

const currentPage = ref(1);// 当前页
const pageSize = ref(10);// 每页显示条数
const total = ref(0);// 总条数
const pages = ref(0);// 总页数

// 表格根数据
const rootData = reactive([
    { prop: "companyInfoId", label: "公司信息ID" },
    { prop: "interviewDate", label: "面试日期" },
    { prop: "interviewId", label: "面试ID" },
    { prop: "interviewJob", label: "面试岗位" },
    { prop: "interviewRegion", label: "面试地点" },
    { prop: "interviewResult", label: "面试结果" },
    { prop: "interviewStatus", label: "面试状态" },
    { prop: "interviewType", label: "面试类型" },
    { prop: "userId", label: "用户ID" }
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
    const o = rootData.filter((item) => !item.label.includes('ID'));
    for (let i = 0; i < o.length; i++) {
        tableColumns.push({ prop: o[i].prop, label: o[i].label });
    }
})()

// 表格数据
const tableData = ref([]);
const getInterviewList = async () => {
    const res = await interviewList(currentPage.value, pageSize.value);
    tableData.value = res.content.records;
    currentPage.value = res.content.current;
    pageSize.value = res.content.size;
    total.value = res.content.total;
    pages.value = res.content.pages;
}
onMounted(async () => {
    getInterviewList();

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
// 面试结果选项
const resultOptions = [
    { tag: 'warning', value: '0', label: '待面试' },
    { tag: 'success', value: '1', label: '通过' },
    { tag: 'danger', value: '2', label: '未通过' }
]
// 面试状态选项
const statusOptions = [
    { tag: 'danger', value: '0', label: '拒绝' },
    { tag: 'success', value: '1', label: '接收' },
    { tag: 'warning', value: '2', label: '未选择' },
    { tag: 'info', value: '3', label: '已取消' }
]
// 面试类型选项
const typeOptions = [
    { tag: 'default', value: '0', label: '线上' },
    { tag: 'info', value: '1', label: '线下' },
    // { tag: 'danger', value: '2', label: '视频面试' }
]
// 表格右侧操作栏
const tableOperation = [
    { type: 'warning', text: '取消' },
    { type: 'danger', text: '拒绝' },
    { type: 'success', text: '接受' },
]
let filteredOperation = tableOperation;
// 根据面试状态获取表格右侧操作栏
const getStatusOptionsLabel = (val) => {
    // 表格右侧操作栏根据面试状态过滤
    if (val === "1") {
        filteredOperation = tableOperation.filter((item) => item.text === '取消');
    } else if (val === "0" || val === "3") {
        filteredOperation = [];
    } else if (val === "2") {
        filteredOperation = tableOperation.filter((item) => item.text != '取消');
    }
    return getOptionLabel(statusOptions, val);
}


/*================= 时间选择弹窗 =================*/
const isShowDialog = ref(false);// 时间选择弹窗是否显示
const timeRange = reactive([]);// 时间范围(例如：['4:00', '5:00'])



// 接受面试函数
const isAcceptInterview = async (id, status) => {
    console.log('acceptInterview', id, status)
    // 调用接口
    const res = await interviewAccept(id, status);
    ElMessage.success(res.message);
    // 刷新表格数据
    getInterviewList();
}

// 表格右侧按钮点击事件
const handleClick = async (index, row, $index) => {
    // console.log('operationClick', index, row, $index)
    // console.log('operationClick', index);
    switch (index) {
        case 0:
            // 面试状态为接受后，修改状态为取消
            if (row.interviewStatus === "1") {
                index = 3;
            }
            isAcceptInterview(row.interviewId, index + '');
            break;
        case 1: // 拒绝面试
            isShowDialog.value = true;
            const res = await interviewDate(+row.companyInfoId);
            // isAcceptInterview(row.interviewId, index + '');
            break;
        default:
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
        <WBTable :total="total" @operationClick="handleClick" @update:currentPage="handleCurrentChange"
            @update:page-size="handleSizeChange" :table-columns="tableColumns" v-model:tableData="tableData"
            v-model:currentPage="currentPage" v-model:pageSize="pageSize" border>
            <template #default="scope">
                <el-tag v-if="scope.prop === 'interviewResult'"
                    :type="getTagType(resultOptions, tableData[scope.$index][scope.prop])">
                    {{ getOptionLabel(resultOptions, tableData[scope.$index][scope.prop]) }}
                </el-tag>
                <el-tag v-else-if="scope.prop === 'interviewStatus'"
                    :type="getTagType(statusOptions, tableData[scope.$index][scope.prop])">
                    {{ getStatusOptionsLabel(tableData[scope.$index][scope.prop]) }}
                </el-tag>
                <span v-else-if="scope.prop === 'interviewType'">
                    {{ getOptionLabel(typeOptions, tableData[scope.$index][scope.prop]) }}</span>
                <span v-else>{{ tableData[scope.$index][scope.prop] }}</span>
            </template>
            <!-- 表格右侧操作栏 -->
            <template #operation="scope">
                <el-button v-for="(item, index) in filteredOperation" :type="item.type" :key="index" size="small"
                    @click="handleClick(index, scope.row, scope.$index)">
                    {{ item.text }}
                </el-button>
            </template>
        </WBTable>

        <!-- 面试时间选择窗口 -->
        <WBDialog v-model="isShowDialog" title="请选择面试时间" width="41%" draggable>
            <div class="interview-time-range">
                <el-time-select v-model="timeRange[0]" size="large" style="width: 240px" :max-time="timeRange[1]"
                    class="mr-4" placeholder="Start time" start="08:30" step="00:15" end="18:30" />
                <span>&emsp;-&emsp;</span>
                <el-time-select v-model="timeRange[1]" size="large" style="width: 240px" :min-time="timeRange[0]"
                    placeholder="End time" start="08:30" step="00:15" end="18:30" />
            </div>
        </WBDialog>
    </div>
</template>

<style lang="scss" scoped></style>