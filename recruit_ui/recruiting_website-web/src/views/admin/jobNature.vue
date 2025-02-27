<script setup lang="js">
import { reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import {
    jobNatureList, jobNatureAdd, jobNatureDelete,
    jobNatureInfo, jobNatureUpdate, jobNatureUpdateStatus
} from '@/api/admin/JobNatureApi';
import WBDialog from '@/components/WBDialog.vue';
import WBDrawer from '@/components/WBDrawer.vue';

// 状态变量
const isShowDrawer = ref(false); // 简历详情抽屉
const isShowDialogAdd = ref(false); // 新增职位性质弹窗
const isShowDialogDelete = ref(false); // 删除职位性质弹窗
let deleteId = ''; // 待删除的职位性质id

// 启用状态选项
const jobNatureStatusOptions = [
    { label: '启用', value: '0', bool: true },
    { label: '禁用', value: '1', bool: false }
];

// 简历禁用状态
const changeStatus = (status) => {
    let s = '';
    let bool = null;
    jobNatureStatusOptions.forEach((item) => {
        if (item.value === status) {
            bool = item.bool;
            s = item.label;
        }
    });
    return { statusValue: s, statusBool: bool };
};

// 新增职位性质
const addForm = reactive({
    natureId: '',
    jobNatureName: '',
    natureStatus: ''
});


// 响应结果
const result = ref([]); // 列表
const info = ref({}); // 详情

// 请求列表
const queryList = () => {
    jobNatureList()
        .then(res => {
            if (res.code !== 200) {
                ElMessage.error(res.message);
                return;
            }
            result.value = res.content;
            ElMessage.success(res.message);
        }).catch(err => {
            ElMessage.error(err.message);
        });
};
queryList();

/* 新增职位性质相关函数 */
// 新增职位性质
const handleJobNatureAdd = () => {
    // 打开弹窗
    isShowDialogAdd.value = !isShowDialogAdd.value;

}
// 处理新增职位性质
const handleAdd = () => {
    if (!addForm.jobNatureName) {
        ElMessage.error('请输入职位性质名');
        return;
    }
    // 发送请求,只要不是空参或路径参数，否则参数必须是对象
    jobNatureAdd({ jobNatureName: addForm.jobNatureName }).then(res => {
        ElMessage.success(res.message);
        // 关闭弹窗
        isShowDialogAdd.value = !isShowDialogAdd.value;
        // 刷新列表
        queryList();
    });
}

/* 编辑职位性质逻辑 */
// 请求简历详情
const queryInfo = (id) => {
    return new Promise((getFilePath) => {
        jobNatureInfo(id).then(res => {
            info.value = res.content;
        });
    });
}
// 处理查看按钮
const handleEdit = (index, row) => {
    // 发送请求
    queryInfo(row.natureId);
    // 打开抽屉
    isShowDrawer.value = !isShowDrawer.value;
}
// 更新职位性质
const handleUpdateRequest = () => {
    jobNatureUpdate(info.value).then(res => {
        // 关闭抽屉
        isShowDrawer.value = !isShowDrawer.value;
        // 刷新列表
        queryList();
    }).catch(err => {
        ElMessage.error(err.message);
    });
}

/* 删除逻辑 */
// 处理删除按钮
const handleDelete = (index, row) => {
    isShowDialogDelete.value = !isShowDialogDelete.value;
    deleteId = row.natureId;
}
// 发送删除请求
const handleDeleteRequest = () => {
    jobNatureDelete(deleteId).then(res => {
        // 关闭弹窗
        isShowDialogDelete.value = !isShowDialogDelete.value;
        // 刷新列表
        queryList();
    });
}

/* 启用状态更新逻辑 */
const handleStatusChange = (row) => {
    // 遍历选项，找到当前值，更新状态
    jobNatureStatusOptions.forEach((item) => {
        if (item.value !== row.natureStatus) {
            row.natureStatus = item.value;
        }
    });
    jobNatureUpdateStatus(row.natureStatus, row.natureId)
        .then(res => {
            if (res.code !== 200) {
                ElMessage.error(res.message);
                return;
            }
            // 刷新列表
            queryList();
        }).catch(err => {
            ElMessage.error(err.message);
        });
}

</script>

<template>
    <div>
        <div class="container">
            <div class="tableBar">
                <div class="tableLab">
                    <!-- 新增部分 -->
                    <div class="add">
                        <el-button type="primary" size="large" @click="handleJobNatureAdd">+ 新增</el-button>
                    </div>
                </div>
            </div>
            <div class="tableBox">
                <el-table :data="result" border>
                    <el-table-column prop="natureId" label="ID" align="center"></el-table-column>
                    <el-table-column prop="jobNatureName" label="职位性质名" align="center"></el-table-column>
                    <el-table-column label="启用状态" align="center">
                        <template #default="scope">
                            <el-tooltip :content="changeStatus(scope.row.natureStatus).statusValue" placement="right">
                                <el-switch @change="handleStatusChange(scope.row)"
                                    v-model="changeStatus(scope.row.natureStatus).statusBool"></el-switch>
                            </el-tooltip>
                        </template>
                    </el-table-column>
                    <el-table-column fixed="right" label="操作" align="center">
                        <template #default="scope">
                            <el-button size="small" @click="handleEdit(scope.$index, scope.row)">
                                编辑
                            </el-button>
                            <el-button size="small" type="danger" @click="handleDelete(scope.$index, scope.row)">
                                删除
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <!-- 分页 -->
                <!-- <div class="page">
                    <el-pagination v-model:current-page="result.page" v-model:page-size="result.size"
                        :page-sizes="[10, 20, 30, 40]" background :total="result.total"
                        layout="total, sizes, prev, pager, next, jumper" @update:page-size="handleSizeChange"
                        @update:current-page="handleCurrentChange" />
                </div> -->
            </div>
        </div>

        <!-- 新增弹窗 -->
        <WBDialog v-model="isShowDialogAdd" @cancel="isShowDialogAdd = false" @submit="handleAdd" title="新增职位性质"
            width="500" align-center>
            <template #default>
                <div class="dialog-main-form">
                    <el-form @submit.prevent="e.preventDefault();" :model="addForm">
                        <el-form-item label="jobNatureName:" label-position="right" label-width="100px" size="large">
                            <el-input v-model="addForm.jobNatureName" size="large" />
                        </el-form-item>
                    </el-form>
                </div>
            </template>
        </WBDialog>

        <!-- 删除弹窗 -->
        <WBDialog v-model="isShowDialogDelete" @cancel="isShowDialogDelete = false" @submit="handleDeleteRequest"
            title="删除职位性质" width="500" align-center>
            <span>确定要删除吗？</span>
        </WBDialog>

        <!-- 详情抽屉 -->
        <WBDrawer v-model="isShowDrawer" @cancel="isShowDrawer = false" @confirm="handleUpdateRequest" title="求职性质信息"
            direction="rtl" size="50%">
            <!-- 抽屉内容部分 -->
            <template #default>
                <div class="drawer-main-form">
                    <el-form :model="info">
                        <el-form-item v-for="( _, index ) in  info " :key="index" :label="`${index}：`"
                            label-position="right" label-width="100px" size="large">
                            <el-input v-model="info[index]" size="large" />
                        </el-form-item>
                    </el-form>
                </div>
            </template>
            <!-- 抽屉页脚部分 -->
            <!-- <template #footer>
                <div class="drawer-footer">
                    <el-button size="large" @click="isShowDrawer = false">取消</el-button>
                    <el-button size="large" type="danger" @click="handleUpdateRequest">
                        确定
                    </el-button>
                </div>
            </template> -->
        </WBDrawer>
    </div>
</template>

<style scoped lang="scss">
.container {
    height: 100vh;
    // box-sizing: border-box;

    .tableLab {
        width: 100%;
        margin-top: 2vh;
        display: flex;
        flex-direction: row;
        justify-content: flex-start;

        .add {
            display: flex;
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

.dialog-main-form {
    margin: 0 3vw;
}
</style>