<script setup>
import { ref, reactive, onMounted } from 'vue';
import WBTable from '@/components/table/WBTable.vue';
import WBDialog from '@/components/WBDialog.vue';
import WBForm from '@/components/WBForm.vue';
import { deliverList, resumeDeliver } from '@/api/user/UserApi';
import { ElMessage } from 'element-plus';

const currentPage = ref(1);// 当前页
const pageSize = ref(10);// 每页显示条数
const total = ref(0);// 总条数
const pages = ref(0);// 总页数

// 表格根数据
const rootData = reactive([
    { tag: 'table, info', prop: "companyInfoName", label: "公司名" },
    { tag: 'table, info', prop: "deliverDate", label: "投递日期" },
    { tag: '', prop: "resumeId", label: "简历ID" },
    { tag: 'table, info', prop: "jobPosition", label: "面试岗位" },
    { tag: 'info', prop: "resumeName", label: "简历名" },
    { tag: 'info', prop: "resumeStatus", label: "简历状态" },
    { tag: 'info', prop: "resumeDelete", label: "流程状态" },
    { tag: 'info', prop: "resumeType", label: "简历类型" },
    { tag: '', prop: "userId", label: "用户ID" }
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
    const o = rootData.filter((item) => item.tag.includes('table'));
    for (let i = 0; i < o.length; i++) {
        tableColumns.push({ prop: o[i].prop, label: o[i].label });
    }
})()

// 表格数据
const tableData = ref([]);
const getDeliverList = async () => {
    const res = await deliverList(currentPage.value, pageSize.value);
    tableData.value = res.content.records;
    currentPage.value = res.content.current;
    pageSize.value = res.content.size;
    total.value = res.content.total;
    pages.value = res.content.pages;
    ElMessage.success(res.message);
}
onMounted(async () => {
    getDeliverList();
})

// 获取选项label
const getOptionLabel = (options, value) => {
    const option = options.find((item) => item.value === value);
    return option ? option.label : value;
}

// 应聘状态选项
const statusOptions = [
    { status: null, value: '0', label: '已投递' },
    { status: null, value: '1', label: '已查看' },
    { status: null, value: '2', label: '待面试' },
    { status: null, value: '3', label: 'offer发放' },
    { status: null, value: '4', label: '上传材料' },
    { status: null, value: '5', label: '录入信息' },
    { status: null, value: '6', label: '预约报道' },
    // { status: null, value: '7', label: '用户已撤销简历' }
]

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
// const handleClick = async (row, _) => {
//     formData.value = row;
//     isShowDialog.value = true;

// }

const revokeDeliver = async (row) => {
    console.log(row);
    const dto = {
        jobResumeId: row.jobResumeId,
        resumeStatus: 7
    }
    await resumeDeliver(dto).then((res) => {
        ElMessage.success(res.message);
    });
    // 刷新页面
    window.location.reload();
}

// 一页条数变化时触发
const handleSizeChange = (size) => {
    pageSize.value = size
}
// 当前页码变化时触发
const handleCurrentChange = (page) => {
    currentPage.value = page
}
</script>

<template>
    <div>
        <el-table :data="tableData" border style="width: 100%">
            <el-table-column prop="companyInfoName" label="公司名" />
            <el-table-column prop="deliverDate" label="投递日期" />
            <el-table-column prop="jobPosition" label="面试岗位" />
            <el-table-column prop="" label="流程" width="610px">
                <template #default="scope">
                    <el-steps :process-status="scope.row.resumeDelete === '0' ? 'error' : 'finish'"
                        style="max-width: 600px" :active="+scope.row.resumeStatus === 7 ? 0 : +scope.row.resumeStatus"
                        finish-status="success" align-center>
                        <el-step class="step-item" v-for="(item, index) in statusOptions" :description="item.label"
                            :key="index">
                        </el-step>
                    </el-steps>
                </template>
            </el-table-column>
            <el-table-column prop="operation" label="操作" width="115px">
                <template #default="scope">
                    <el-button v-if="scope.row.resumeDelete !== '0'" type="warning"
                        @click="revokeDeliver(scope.row)">撤销投递</el-button>
                </template>
            </el-table-column>
        </el-table>
        <div class="table-footer">
            <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 30, 40]"
                :background="true" :total="total" @update:page-size="handleSizeChange"
                @update:current-page="handleCurrentChange" :layout="'total, sizes, prev, pager, next, jumper'" />
        </div>
        <!--  @row-click="handleClick" -->
        <!-- <WBTable :total="total" @update:currentPage="handleCurrentChange" @update:page-size="handleSizeChange"
            :table-columns="tableColumns" v-model:tableData="tableData" v-model:currentPage="currentPage"
            operation-width="620px" v-model:pageSize="pageSize" border>
            <template #default="scope">
                <span>{{ tableData[scope.$index][scope.prop] }}</span>
            </template>
            <template #operation="scope">
                <el-steps :process-status="scope.row.resumeDelete === '0' ? 'error' : 'success'" style="max-width: 600px"
                    :active="+scope.row.resumeStatus" finish-status="success" align-center>
                    <el-step class="step-item" v-for="(item, index) in statusOptions" :description="item.label"
                        :key="index">
                    </el-step>
                </el-steps>
            </template>
        </WBTable> -->

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

.table-footer {
    margin-top: 5vh;
    display: flex;
    justify-content: center;
}
</style>
