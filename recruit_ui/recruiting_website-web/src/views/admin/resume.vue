<script setup lang="js">
import { ref } from 'vue';
import useResumeListStore from '@/store/resumeStore'
import { resumeList, resumeInfo, resumeReview, resumeDelete } from '@/api/admin/ResumeApi';
import { ElMessage } from 'element-plus';
import WBDrawer from '@/components/WBDrawer.vue';
import WBForm from '@/components/WBForm.vue';
import WBDialog from '@/components/WBDialog.vue';
import { reactive } from 'vue';
import WBTable from '@/components/table/WBTable.vue';
import WBTableHeader from '@/components/table/WBTableHeader.vue';

// 数据请求对象
const queryObj = useResumeListStore().getResumeListQueryParams;

// 状态变量
const isShow = ref(false); // 简历详情抽屉
const isResumeDisabled = ref(useResumeListStore().getResumeListScreenParams.resumeStatusOption); // 简历禁用状态
const isShowDeleteDialog = ref(false); // 删除确认弹窗
const isOnlineResume = ref(useResumeListStore().getResumeListScreenParams.resumeTypeOption); // 简历类型选择
let $isOnlineResume = isOnlineResume.value;
const isShowDeleteBtn = ref(isOnlineResume.value); // 删除按钮显示控制

// 简历类型选项
const resumeTypeOptions = [
    { label: '在线简历', value: '0' },
    { label: '附件简历', value: '1' }
];
// 简历启用状态选项
const resumeStatusOptions = [
    { label: '全部', value: '-1' },
    { label: '启用', value: '0', tag: 'success' },
    { label: '停用', value: '1', tag: 'danger' }
];
// 简历审核状态选项
const resumeReviewOptions = [
    { label: '待审核', value: '0', tag: 'info' },
    { label: '审核通过', value: '1', tag: 'success' },
    { label: '打回修改', value: '2', tag: 'warning' }
];
// 表格列定义
const columns = [
    { prop: 'resumeId', label: '简历ID', tag: '0, 1' },
    { prop: 'userName', label: '用户名', tag: '0, 1' },
    { prop: 'resumeType', label: '简历类型', tag: '0, 1', option: resumeTypeOptions },
    { prop: 'resumeReview', label: '审核状态', tag: '0, 1', option: resumeReviewOptions },
    { prop: 'resumeStatus', label: '启用状态', tag: '0', option: resumeStatusOptions },
];
const filteredColumns = (val) => {
    return columns.filter((i) => i.tag.includes(val));
}
// 定义在线简历表单项
const infoItems = reactive({
    resumeReview: "审核状态",
    resumeId: "简历ID",
    resumeBirth: "生日",
    resumeEducation: "学历",
    resumeEmail: "邮箱",
    resumeExperience: "工作经验",
    resumeGender: "性别",
    resumeIntroduction: "个人简介",
    resumeJob: "意向工作",
    resumeMajor: "专业",
    resumeMarriage: "婚姻状况",
    resumePhone: "手机号",
    resumePolitical: "政治面貌",
    resumeSalary: "意向薪资"
});
// 表格审核和启用列label
const getColumnLabel = (val, prop) => {
    const op = columns.find((item) => item.prop === prop).option
    if (!op) { return val[prop]; }
    return op.find((item) => item.value === val[prop]).label;
}


// 响应结果
const result = ref([]); // 简历列表
const info = ref({}); // 简历详情

// 请求简历列表
const queryResume = (obj) => {
    resumeList(obj).then(res => {
        result.value = res.content;
        isShowDeleteBtn.value = isOnlineResume.value;
        ElMessage.success(res.message);
    });
}
queryResume(queryObj)


const isShowPreview = ref(false);// 详情弹窗是否显示
const pdfUrl = ref(''); // PDF预览地址
// 处理查看按钮
const handleEdit = (index, row) => {
    // 请求简历详情
    resumeInfo({ resumeId: row.resumeId, resumeType: row.resumeType }).then(res => {
        console.log(res);
        if (typeof res.content === 'object') {
            // 在线简历
            res.content.resumeReview = row.resumeReview;
            info.value = res.content;
            isShow.value = !isShow.value;
        } else {
            // TODO PDF简历预览
            const raw = window.atob(res.content);
            const rawLength = raw.length;
            const uInt8Array = new Uint8Array(rawLength);
            for (let i = 0; i < rawLength; ++i) {
                uInt8Array[i] = raw.charCodeAt(i);
            }

            const blob = new Blob([uInt8Array], { type: 'application/pdf' });
            pdfUrl.value = URL.createObjectURL(blob);
            isShowPreview.value = true;
        }
    });
}

/* 处理删除按钮 */
let deleteId = null;
// 处理删除确认弹窗
const handleDelete = (row) => {
    // TODO 附件简历删除
    deleteId = row.resumeId;
    isShowDeleteDialog.value = true;
}
// 处理删除确认弹窗提交
const submitDelete = async () => {
    await resumeDelete({ id: deleteId });
    isShowDeleteDialog.value = false;
    queryResume(queryObj);
}

/* 请求参数构建部分 */
const input = ref(''); // 搜索框内容

// 更新简历类型
const updateResumeType = (val) => {
    queryObj.resumeType = val;
}

// 更新简历启用状态
const updateResumeStatus = (val) => {
    if (val === '-1') {
        delete queryObj.resumeStatus;
    }
    queryObj.resumeStatus = val;
}

// 搜索
const handleSearch = () => {
    queryObj.userName = input.value;
    // 保存查询参数到 store
    useResumeListStore().setResumeListQueryParams({
        page: result.value?.current,
        size: result.value?.size,
        userName: queryObj.userName,
        resumeReview: queryObj.resumeReview,
        resumeStatus: queryObj.resumeStatus,
        resumeType: queryObj.resumeType
    });
    // 保存筛选参数到 store
    useResumeListStore().setResumeListScreenParams({
        resumeTypeOption: queryObj.resumeType,
        resumeStatusOption: queryObj.resumeStatus
    });
    $isOnlineResume = isOnlineResume.value;
    queryResume(queryObj);
}

/* 分页相关函数 */
// 处理显示行数
const handleSizeChange = (val) => {
    queryObj.size = val;
    queryResume(queryObj);
}
// 处理翻页
const handleCurrentChange = (val) => {
    queryObj.page = val;
    queryResume(queryObj);
}

/* tag颜色处理 */
// 获得标签颜色
const getTagType = (o, val) => {
    if (!val) {
        return;
    }
    const op = columns.find((item) => item.prop === o).option;
    return op.find((item) => item.value === val).tag;
}


/* 表单验证 */
const infoForm = ref(); // 详情表单实例
// 表单校验规则
const rules = {
    resumeReview: [{ required: true, message: '请选择审核状态', trigger: 'blur' }]
};
// 详情抽屉确认
const handleDrawerConfirm = async () => {
    isShow.value = false;
    // 发送请求修改审核状态
    if (queryObj.resumeType === '0') { // 在线简历
        // 表单校验方法
        await resumeReview(info.value.resumeReview, info.value.resumeId, queryObj.resumeType)
        queryResume(queryObj);// 更新列表
        return;
    }
}


</script>

<template>
    <div>
        <div class="container">
            <WBTable v-model:table-data="result.records" :table-columns="filteredColumns($isOnlineResume)"
                v-model:current-page="result.current" v-model:page-size="result.size" :total="result.total"
                @update:page-size="handleSizeChange" @update:current-page="handleCurrentChange" border>
                <!-- 头部 -->
                <template #header>
                    <WBTableHeader v-model:input="input" @click-search="handleSearch">
                        <template #add><span></span></template>
                        <template #screen>
                            <div class="screen">
                                <div class="change-type">
                                    <span>简历类型：</span>
                                    <el-select @change="updateResumeType" v-model="isOnlineResume" value-key="value"
                                        placeholder="简历类型" size="large" style="width: 140px;">
                                        <el-option v-for="item in resumeTypeOptions" :key="item.value" :label="item.label"
                                            :value="item.value" />
                                    </el-select>
                                </div>
                                <div v-if="isOnlineResume !== '1'" class="change-status">
                                    <span>启用状态：</span>
                                    <el-select @change="updateResumeStatus" v-model="isResumeDisabled" value-key="value"
                                        placeholder="启用状态" size="large" style="width: 140px;">
                                        <el-option v-for="item in resumeStatusOptions" :key="item.value" :label="item.label"
                                            :value="item.value" />
                                    </el-select>
                                </div>
                            </div>
                        </template>
                    </WBTableHeader>
                </template>
                <!-- 表格内容 -->
                <template #default="scope">
                    <!-- 简历启用状态 -->
                    <el-tag v-if="scope.prop === 'resumeStatus' || scope.prop === 'resumeReview'"
                        :type="getTagType(scope.prop, result.records[scope.$index][scope.prop])">
                        {{ getColumnLabel(result.records[scope.$index], scope.prop) }}
                    </el-tag>
                    <span v-else>{{ getColumnLabel(result.records[scope.$index], scope.prop) }}</span>
                </template>
                <!-- 操作按钮 -->
                <template #operation="scope">
                    <el-button size="small" @click="handleEdit(scope.$index, scope.row)">
                        查看
                    </el-button>
                    <el-button v-if="isShowDeleteBtn === '1'" size="small" type="danger"
                        @click="handleDelete(scope.row, scope.$index)">
                        删除
                    </el-button>
                </template>
            </WBTable>
        </div>

        <!-- 简历详情抽屉 -->
        <WBDrawer v-model="isShow" @cancel="isShow = false" @confirm="handleDrawerConfirm" title="简历信息" direction="rtl"
            size="40%">
            <WBForm :model="info" :rules="rules" :items="infoItems" ref="infoForm">
                <template #default="scope">
                    <el-select v-if="scope.key === 'resumeReview'" v-model="info[scope.key]" size="large"
                        placeholder="审核状态">
                        <el-option v-for="item in resumeReviewOptions" :key="item.value" :label="item.label"
                            :value="item.value" />
                    </el-select>
                    <el-input v-else :value="info[scope.key]" size="large" readonly />
                </template>
            </WBForm>
        </WBDrawer>

        <!-- 简历删除确认框 -->
        <WBDialog draggable v-model="isShowDeleteDialog" @submit="submitDelete" @cancel="isShowDeleteDialog = false"
            title="删除附件简历">
            <span>确认删除该简历？</span>
        </WBDialog>

        <!-- pdf预览弹窗 -->
        <WBDialog v-model="isShowPreview" fullscreen title="offer预览">
            <iframe :src="pdfUrl" class="pdf-preview" frameborder="0"></iframe>
            <template #footer><i></i></template>
        </WBDialog>
    </div>
</template>

<style scoped lang="scss">
.screen {
    display: flex;

    // 选择部分
    [class^="change-"] {
        margin-left: 2vw;

        // 文字部分
        span {
            color: #7f8184;
        }
    }
}

.container {
    height: 100vh;
}

/* 为iframe设置一些基本样式 */
.pdf-preview {
    width: 100%;
    /* 以适应其父容器 */
    height: 100vh;
    /* 高度可以根据需要调整 */
    border: none;
    /* 去掉边框 */
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    /* 添加阴影效果 */
    margin: 5px auto;
    /* 上下外边距为20px，左右自动居中 */
    display: block;
    /* 将iframe显示为块级元素 */
    background-color: #f5f5f5;
    /* 背景颜色，当PDF加载失败时会显示 */

}
</style>
