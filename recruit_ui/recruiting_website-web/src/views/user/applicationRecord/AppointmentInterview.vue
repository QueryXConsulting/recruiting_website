<script setup>
import { ref, reactive, onMounted } from 'vue';
import WBTable from '@/components/table/WBTable.vue';
import WBDialog from '@/components/WBDialog.vue';
import { interviewList, interviewAccept, interviewDate } from '@/api/user/UserApi';
import { ElMessage } from 'element-plus';
import { ArrowRight, ArrowDown } from '@element-plus/icons-vue'

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
    { prop: "interviewTime", label: "面试时长" },
    { prop: "jobId", label: "职位id" },
    { prop: "jobResumeId", label: "简历投递id" },
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
    const o = rootData.filter((item) => !item.prop.includes('Id'));
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
    { tag: 'success', value: '1', label: '接受' },
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
const isAcceptInterview = async (id, status, date) => {
    // 调用接口
    const res = await interviewAccept(id, status, date);
    ElMessage.success(res.message);
    // 刷新表格数据
    getInterviewList();
}

let $interviewId = '';
// 表格右侧按钮点击事件
const handleClick = async (index, row, $index) => {
    $interviewId = row.interviewId;
    switch (index) {
        case 0:
            // 面试状态为接受后，修改状态为取消
            if (row.interviewStatus === "1") {
                index = 3;
            }
            isAcceptInterview(row.interviewId, index + '');
            break;
        case 1: // 接受面试
            isShowDialog.value = true;
            const res = await interviewDate(+row.companyInfoId);
            for (let i = 0; i < res.content.length; i++) {
                timeRange[i] = res.content[i];
            }
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

/*================= 面试时间选择 =================*/
const interviewTime = ref('');// 用户选择的面试时间
const itemIndex = ref(null);// 时间选项索引
const selected = ref(0);// item是否被选中
// 时间转换函数 
const parseTime = (time) => {
    const date = new Date(time);
    const h = date.getHours();
    const m = String(date.getMinutes()).padStart(2, '0');
    return `${h}:${m}`;
}
// 选择时间
const selectTime = (index) => {
    itemIndex.value = index;// 记录当前选择项索引
    selected.value = timeRange[index].interviewDateId;// 记录当前选择项id
    interviewTime.value = null;// 重置面试时间
}
// 确认选择时间
const submitTime = async () => {
    if (!interviewTime.value) {
        ElMessage.error('请选择面试时间');
        return;
    }
    // 获取面试时间（年月日）
    let time = timeRange.find((item) => item.interviewDateId === selected.value).interviewDateEnd;
    time = new Date(time);
    const y = time.getFullYear();
    const m = String(time.getMonth() + 1).padStart(2, '0');
    const d = String(time.getDate()).padStart(2, '0');
    time = new Date(`${y}-${m}-${d} ${interviewTime.value}`);
    await isAcceptInterview($interviewId, 1, time);
    cancelTime();// 重置弹窗数据
}
// 取消选择时间
const cancelTime = () => {
    isShowDialog.value = false;// 关闭时间选择弹窗
    itemIndex.value = null;// 重置选择项索引
    selected.value = 0;// 重置选择项id
    interviewTime.value = '';// 重置面试时间
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
        <WBDialog v-model="isShowDialog" @submit="submitTime" @cancel="cancelTime" title="请选择面试时间" width="40%" draggable
            style="background: #f0f0f0;">
            <ul>
                <li v-for="(item, index) in timeRange" @click="selectTime(index)" :key="index" class="interview-time-item">
                    <span class="interview-time-label">
                        <label>
                            <input type="radio" v-model="selected" :value="item.interviewDateId" />
                        </label>
                        <el-icon>
                            <component :is="itemIndex === index ? ArrowDown : ArrowRight" />
                        </el-icon>
                        {{ item.interviewDateStart }} - {{ item.interviewDateEnd }}
                    </span>

                    <p v-if="index === itemIndex" style=" margin: 10px 0 0 50px;">
                        <el-time-select v-model="interviewTime" size="large" style="width: 200px"
                            :end="parseTime(item.interviewDateEnd)" placeholder="请选择面试时间"
                            :start="parseTime(item.interviewDateStart)" :editable="false" step="00:15" />
                    </p>
                </li>
            </ul>
        </WBDialog>
    </div>
</template>

<style lang="scss" scoped>
.interview-time-item {
    margin: 5px;
    padding: 5px 0 5px 20px;
    background: #fff;
    border-radius: 10px;
    min-height: 50px;
    cursor: pointer;
    display: flex;
    flex-direction: column;
    justify-content: center;

    .interview-time-label {
        gap: 10px;
        display: flex;
        align-items: center;
    }

    // gap: 10px;
    font-size: 17px;
}
</style>