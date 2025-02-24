<script setup lang="js">
import { defineProps, defineEmits, ref } from 'vue';
import useResumeListStore from '@/store/resumeListQueryParmesStore'
import { adminUserResumeList, adminUserResumeInfo, adminUserResumeReview } from '@/api/admin/adminApi';
import { ElMessage } from 'element-plus';
import { Search } from '@element-plus/icons-vue';

const isShow = ref(false); // 简历详情抽屉
const isReview = ref(true); // 审核状态
const changeReview = ref(false); // 审核状态切换
const dialogVisible = ref(false); // 请求参数修改弹窗
const isOnlineResume = ref('0'); // 简历类型选择
const isResumeDisabled = ref('0'); // 简历禁用状态

// 简历类型选项
const resumeTypeOptions = [
    { label: '在线简历', value: '0' },
    { label: '附件简历', value: '1' }
]
// 简历启用状态选项
const resumeStatusOptions = [
    { label: '启用', value: '0' },
    { label: '停用', value: '1' }
]

// 数据请求对象
const queryObj = useResumeListStore().getResumeListQueryParams;


// 响应结果
const result = ref({}); // 简历列表
const info = ref({}); // 简历详情

// 请求简历列表
const queryResume = (obj) => {
    adminUserResumeList(obj)
        .then(res => {
            if (res.code !== 200) {
                ElMessage.error(res.message);
                return;
            }
            result.value = res.content;
            result.value.records.forEach(item => {
                item.resumeType = showText(item.resumeType, '在线简历', '附件简历');
                if (item.resumeReview == 0) isReview.value = false;
                item.resumeReview = showText(item.resumeReview, '待审核', '审核通过');
                item.resumeStatus = showText(item.resumeStatus, '启用', '停用');
            });
            ElMessage.success(res.message);
        }).catch(err => {
            ElMessage.error(err.message);
            return;
        });
}
queryResume(queryObj)

// 请求简历详情
const queryResumeInfo = (resumeInfo) => {
    adminUserResumeInfo(resumeInfo)
        .then(res => {
            if (res.code !== 200) {
                ElMessage.error(res.message);
                return;
            }
            info.value = res.content;
            ElMessage.success(res.message);
        }).catch(err => {
            ElMessage.error(err.message);
        });
}

// 处理查看按钮
const handleEdit = (index, row) => {
    row.resumeType = resumeDetailsText(row.resumeType, '在线简历', '附件简历');
    queryResumeInfo({ resumeId: row.resumeId, resumeType: row.resumeType })
    isShow.value = !isShow.value;
}


// 修改审核状态
const changeResumeReview = (val, index, row) => {
    row.resumeReview = val ? '审核通过' : '待审核';
    const reviewCode = val ? 1 : 0;
    const resumeType = resumeDetailsText(row.resumeType, '在线简历', '附件简历');
    changeReview.value = true;
    // 发送请求修改审核状态
    adminUserResumeReview(reviewCode, row.resumeId, resumeType)
        .then((res) => {
            if (res.code !== 200) {
                ElMessage.error(res.message);
                return;
            }
            changeReview.value = false;
            ElMessage.success(res.message);
            queryResume(queryObj);// 更新列表

        }).catch(err => {
            ElMessage.error(err.message);
        });
}

/* 请求参数构建部分 */
const input = ref(''); // 搜索框内容

// 更新简历类型
const updateResumeType = (val) => {
    queryObj.resumeType = val;
}

// 更新简历启用状态
const updateResumeStatus = (val) => {
    queryObj.resumeStatus = val;
}

// 搜索
const handleSearch = () => {
    queryObj.userName = input.value;
    useResumeListStore().setResumeListQueryParams({
        page: result.value.current,
        size: result.value.size,
        userName: queryObj.userName,
        resumeReview: queryObj.resumeReview,
        resumeStatus: queryObj.resumeStatus,
        resumeType: queryObj.resumeType
    });
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

// 表格展示文字
function showText(val, val0, val1) {
    val = parseInt(val);
    switch (val) {
        case 1:
            return val1;
        case 0:
            return val0;
        default:
            console.log('未知状态', val);
            return val;
    }
}
// 查看简历详情文字处理
function resumeDetailsText(val, val0, val1) {
    switch (val) {
        case val0:
            return '0';
        case val1:
            return '1';
        default:
            console.log('未知状态', val);
            return val;
    }
}


</script>

<template>
    <div>
        <div class="container">
            <div class="tableBar">
                <div class="tableLab">
                    <!-- 搜索部分 -->
                    <div class="search">
                        <el-input @keyup.enter="handleSearch" class="search-input" clearable v-model="input"
                            size="large" placeholder="点击搜索" />
                        <el-button @click="handleSearch" type="primary" size="large">
                            <el-icon style="vertical-align: middle">
                                <Search />
                            </el-icon>
                            <span style="vertical-align: middle"> 搜索 </span>
                        </el-button>
                    </div>
                    <!-- 筛选部分 -->
                    <div class="screen">
                        <div class="change-type">
                            <span>简历类型：</span>
                            <el-select @change="updateResumeType" v-model="isOnlineResume" value-key="value"
                                placeholder="简历类型" size="large" style="width: 140px;">
                                <el-option v-for="item in resumeTypeOptions" :key="item.value" :label="item.label"
                                    :value="item.value" />
                            </el-select>
                        </div>
                        <div class="change-status">
                            <span>启用状态：</span>
                            <el-select @change="updateResumeStatus" v-model="isResumeDisabled" value-key="value"
                                placeholder="启用状态" size="large" style="width: 140px;">
                                <el-option v-for="item in resumeStatusOptions" :key="item.value" :label="item.label"
                                    :value="item.value" />
                            </el-select>
                        </div>
                    </div>
                </div>
            </div>
            <div class="tableBox">
                <el-table :data="result.records" border>
                    <el-table-column prop="resumeId" label="简历ID" align="center"></el-table-column>
                    <el-table-column prop="userName" label="用户名" align="center"></el-table-column>
                    <el-table-column prop="resumeType" label="简历类型" align="center">
                    </el-table-column>
                    <el-table-column prop="resumeReview" label="审核状态" align="center">
                        <template #default="scope">
                            <el-tooltip :content="result.records[scope.$index].resumeReview" placement="top">
                                <el-switch :loading="changeReview" v-model="isReview"
                                    @change="changeResumeReview(isReview, scope.$index, scope.row)" />
                            </el-tooltip>
                        </template>
                    </el-table-column>
                    <el-table-column prop="resumeStatus" label="启用状态" align="center">
                    </el-table-column>
                    <el-table-column fixed="right" label="操作" align="center">
                        <template #default="scope">
                            <el-button size="small" @click="handleEdit(scope.$index, scope.row)">
                                查看
                            </el-button>
                            <!-- TODO: 简历删除功能暂时隐藏 -->
                            <!-- <el-button size="small" type="danger" @click="handleDelete(scope.$index, scope.row)">
                                删除
                            </el-button> -->
                        </template>
                    </el-table-column>
                </el-table>
                <!-- 分页 -->
                <div class="page">
                    <el-pagination v-model:current-page="result.page" v-model:page-size="result.size"
                        :page-sizes="[10, 20, 30, 40]" background :total="result.total"
                        layout="total, sizes, prev, pager, next, jumper" @update:page-size="handleSizeChange"
                        @update:current-page="handleCurrentChange" />
                </div>
            </div>
        </div>

        <!-- 简历详情抽屉 -->
        <el-drawer v-model="isShow" title="简历信息" direction="rtl" size="50%">
            <template #default>
                <div class="drawer-main-form">
                    <el-form :model="info">
                        <el-form-item v-for="(_, index) in info" :key="index" :label="`${index}：`"
                            label-position="right" label-width="100px" size="large">
                            <el-input v-model="info[index]" size="large" />
                        </el-form-item>
                    </el-form>
                </div>
            </template>
        </el-drawer>
    </div>
</template>

<style scoped lang="scss">
.container {
    height: 100vh;
    box-sizing: border-box;

    .tableLab {
        width: 100%;
        margin-top: 2vh;
        display: flex;
        flex-direction: row;
        justify-content: flex-start;

        .search {
            display: flex;

            .search-input {
                width: 80%;
                margin-right: .5vw;
            }
        }

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

    }

    .tableBox {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        display: flex;
        flex-direction: column;
        align-items: center;
        margin: 2vh auto;

        .page {
            margin-top: 4vh;
        }
    }
}

.drawer-main-form {
    display: flex;
    justify-content: center;
}
</style>