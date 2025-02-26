<script lang="js" setup>
import { adminUserList } from '@/api/admin/adminApi.js'
import { Search } from '@element-plus/icons-vue'
import { ref, reactive, defineEmits } from 'vue'
import useUserListStore from '@/store/userListQueryParmesStore'
import { ElMessage } from 'element-plus';

const emit = defineEmits(['edit-click', 'delete-click']);
let tableData = reactive([]); // 表格数据
const responseData = reactive({}); // 请求响应数据
let input = ref('');
const isUserDisabled = ref('0'); // 启用状态
const userStatusOptions = [
    { value: '0', label: '启用' },
    { value: '1', label: '禁用' }
];

const searchObj = useUserListStore().getUserListQueryParams;
// 请求用户列表
function getUserListResult(obj) {
    tableData.length = 0;
    const data = adminUserList(obj);
    data.then(res => {
        if (res.code !== 200) {
            console.error(res.msg);
            return;
        }
        responseData.total = res.content.total;// 总条数
        responseData.current = res.content.current;// 当前页
        responseData.size = res.content.size;// 每页显示条数
        responseData.pages = res.content.pages;// 总页数
        for (let record of res.content.records) {
            record.userStatus = toStatus(record.userStatus);
            record.userRole = toRole(record.userRole);
            tableData.push(record);
        }
        ElMessage.success(res.message);
    }).catch(err => {
        console.error(err)
    })
}
getUserListResult(searchObj);
function toStatus(status) {
    return status === '0' ? '启用' : '禁用';
}

function toRole(role) {
    switch (role) {
        case '1':
            return '超级管理员';
        case '2':
            return '普通角色';
        case '3':
            return 'test1';
        case '4':
            return '公司用户';
        case '5':
            return '学生用户';
        default:
            return role;
    }
}

/* 表格相关函数 */
// 编辑用户
const handleEdit = (index, row) => {
    emit('edit-click', row);
}

// 删除用户
const handleDelete = (index, row) => {
    emit('delete-click', row);
}

// 用户详情
// const handleDetails = (row) => {
//     emit('row-click', row);
// }

/* 请求参数构建 */
// 搜索
const handleSearch = () => {
    searchObj.userName = input.value;
    getUserListResult(searchObj);
}

// 启用状态筛选
const updateUserStatus = (value) => {
    searchObj.userStatus = value;
    getUserListResult(searchObj);
}

/* 分页相关 */
// 处理显示行数
const handleSizeChange = (val) => {
    searchObj.size = val;
    getUserListResult(searchObj);
}
// 处理翻页
const handleCurrentChange = (val) => {
    searchObj.pages = val;
    getUserListResult(searchObj);
}
</script>

<template>
    <div class="table-container">
        <div class="tableLab">
            <div class="add">
                <el-button type="primary" size="large" @click="handleUserAdd">+ 新增用户</el-button>
            </div>
            <div class="search">
                <!-- @clear="handleClear" -->
                <el-input @keyup.enter="handleSearch" class="search-input" clearable v-model="input" size="large"
                    placeholder="点击搜索" />
                <el-button @click="handleSearch" type="primary" size="large">
                    <el-icon style="vertical-align: middle">
                        <Search />
                    </el-icon>
                    <span style="vertical-align: middle"> 搜索 </span>
                </el-button>
            </div>
            <!-- 筛选部分 -->
            <div class="screen">
                <div class="change-status">
                    <span>启用状态：</span>
                    <el-select @change="updateUserStatus" v-model="isUserDisabled" value-key="value" placeholder="启用状态"
                        size="large" style="width: 140px;">
                        <el-option v-for="item in userStatusOptions" :key="item.value" :label="item.label"
                            :value="item.value" />
                    </el-select>
                </div>
            </div>
        </div>


        <el-table :data="tableData" height="100%" :border=true style="width: 100%">
            <el-table-column align="center" prop="userId" label="用户ID" />
            <el-table-column align="center" prop="userName" label="用户名" />
            <el-table-column align="center" prop="userPhone" label="手机号" />
            <el-table-column align="center" prop="userRole" label="角色" />
            <el-table-column align="center" prop="userStatus" label="状态" />
            <el-table-column align="center" prop="userInterviews" label="面试数" />
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
        <div class="page">
            <el-pagination v-model:current-page="responseData.pages" v-model:page-size="responseData.size"
                :page-sizes="[10, 20, 30, 40]" background :total="responseData.total" @update:page-size="handleSizeChange"
                @update:current-page="handleCurrentChange" layout="total, sizes, prev, pager, next, jumper" />
        </div>
    </div>
</template>

<style lang="scss" scoped>
.table-container {
    .tableLab {
        display: flex;
        align-content: center;

        // cursor: pointer;
        .search {
            position: relative;
            display: flex;
            margin: 1vh 1vw;
            padding-bottom: 2.5vh;

            .search-input {
                margin-right: .5vw;
                width: 80%;
            }
        }

        .screen {
            [class^="change-"] {
                margin-left: 2vw;

                span {
                    color: #7f8184;
                }
            }
        }
    }

}


.page {
    margin: 3vh auto;
    display: flex;
    justify-content: center;
}
</style>
