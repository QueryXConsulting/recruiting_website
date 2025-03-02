<script lang="js" setup>
import { roleList } from '@/api/admin/RoleApi'
import { userDelete, userList, userAdd, userInfo, userUpdate } from '@/api/admin/UserApi';
import { ref, computed, reactive, defineEmits } from 'vue'
import useUserListStore from '@/store/userListQueryParmesStore'
import { ElMessage } from 'element-plus';
import WBDialog from '@/components/WBDialog.vue';
import WBForm from '@/components/WBForm.vue';
import WBDrawer from '@/components/WBDrawer.vue';

const tableData = reactive([]); // 表格数据
const responseData = reactive({}); // 请求响应数据
const info = reactive({}); // 用户详情信息
const input = ref('');
const isUserDisabled = ref(); // 启用状态
const isShowDialogDelete = ref(false); // 删除弹窗
const isShowDialogAdd = ref(false); // 新增弹窗
const isShowDialogInfo = ref(false); // 详情弹窗
let roles = [];// 用户角色列表
const opened = ref(false);// 详情抽屉是否打开

// 请求角色列表
const getRoleList = async () => {
    const d = await roleList();
    roles = d.content;
}
getRoleList();

// 用户状态选项
const userStatusOptions = [
    { value: '-1', label: '全部' },
    { value: '0', label: '启用', tag: 'success' },
    { value: '1', label: '禁用', tag: 'danger' },
];
const getStatusLabel = (status) => {
    const option = userStatusOptions.find(item => item.value === status);
    if (option) {
        return option.label;
    }
    return status;
}

// 用户角色选项
const getRoleLabel = (_roleId) => {
    const roleO = roles.find(item => item.roleId === (+_roleId));
    if (roleO) {
        return roleO.roleName;
    }
    return _roleId;
}

//
const response = reactive([
    { tag: 'edit, table', prop: 'userId', label: '用户ID', value: '' },
    { tag: 'add, edit, info, table', prop: 'userName', label: '用户名', value: '' },
    { tag: 'add, edit, info, table', prop: 'userPhone', label: '手机号', value: '' },
    { tag: 'add, edit, info', prop: 'userPassword', label: '密码', value: '' },
    { tag: 'add, edit', prop: 'userEmail', label: '邮箱', value: '' },
    { tag: 'add, edit, table', prop: 'userRole', label: '角色', value: '' },
    { tag: 'add, edit, table', prop: 'userStatus', label: '状态', value: '', option: userStatusOptions },
    { tag: 'table', prop: 'userInterviews', label: '面试数', value: '' },
    { tag: 'edit, info', prop: 'resumeId', label: '简历ID', value: '' },
    { tag: 'add, edit, info', prop: 'userAvatar', label: '头像', value: '' },

]);
// 新增弹窗表单
const filteredResponse = computed(() => response.filter(item => item.prop !== 'userId'));
// 表格列配置
const tableColumns = computed(() => {
    return response.filter(item => item.tag.includes('table'));
});
// 详情配置对象
const getFormConfig = (val) => {
    const arr = response.filter(item => item.tag.includes(val));
    if (!arr) { return val; }
    const k = arr.flatMap(item => item.prop);
    const v = arr.flatMap(item => item.label);
    return k.reduce((acc, key, index) => {
        acc[key] = v[index];
        return acc;
    }, {})
}


const searchObj = useUserListStore().getUserListQueryParams;
// 请求用户列表
const getUserListResult = (obj) => {
    tableData.length = 0;
    const data = userList(obj);
    data.then(res => {
        responseData.size = res.content.size;// 每页显示条数
        responseData.total = res.content.total;// 总条数
        responseData.pages = res.content.pages;// 总页数
        responseData.current = res.content.current;// 当前页
        for (let record of res.content.records) {
            tableData.push(record);
        }
        ElMessage.success(res.message);
    });
}
getUserListResult(searchObj);

/* 表格相关函数 */
// 操作按钮
const operationButtons = [
    { text: '编辑', type: 'primary' },
    { text: '查看', type: 'default' },
    { text: '删除', type: 'danger' },
];
// 操作按钮点击事件
const handleOperationClick = (buttonIndex, row, rowIndex) => {
    switch (buttonIndex) {
        case 0:
            handleEdit(rowIndex, row);
            break;
        case 1:
            handleInfo(rowIndex, row);
            break;
        case 2:
            handleDeleteUser(rowIndex, row);
            break;
        default:
            break;
    }
}

// 新增用户
const handleUserAdd = (index, row) => {
    isShowDialogAdd.value = true;
    // 初始化新增表单数据
    const d = response.filter((item) => item.tag.includes('add'));
    const k = d.flatMap(item => item.prop);
    const v = d.flatMap(item => item.value);
    for (let i = 0; i < k.length; i++) {
        userAddForm[k[i]] = v[i];
    }
}

// 编辑用户
// 编辑表单验证规则
const editRules = {
    userName: [
        { required: true, message: '请输入用户名', trigger: 'blur' }
    ],
    userPhone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3456789]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
    ],
    userPassword: [
        { required: true, min: 6, max: 20, message: '密码长度在6到20个字符之间', trigger: 'blur' }
    ],
    userEmail: [
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
    ],
    userRole: [
        { required: true, message: '请选择用户角色', trigger: 'change' }
    ],
    userStatus: [
        { required: true, message: '请选择用户状态', trigger: 'change' }
    ],
    userAvatar: [
        { required: false, message: '请输入头像地址', trigger: 'blur' }
    ]
};
// 编辑表单实例
const editForm = ref(null);
const editModel = reactive({});// 编辑表单模型
const handleEdit = async (index, row) => {
    const _info = await userInfo(row.userId);
    opened.value = true;
    const d = response.filter(item => item.tag.includes('edit'));
    const _k = d.flatMap(item => item.prop);
    const _v = d.flatMap(item => item.value);
    const _role = roles.find(item => item.roleId === +row.userRole);
    for (let i = 0; i < _k.length; i++) {
        editModel[_k[i]] = _v[i];
    }
    // 表单赋值
    editModel.userRole = _role?.roleName;
    editModel.userId = row.userId;
    editModel.userPhone = row.userPhone;
    editModel.userName = row.userName;
    editModel.userStatus = row.userStatus;
    editModel.resumeId = _info.content.resumeId;
}
// 编辑表单提交
const confirmEditForm = () => {
    editForm.value.validate(async (valid) => {
        if (!valid) {
            return;
        }
        opened.value = false;
        console.log(editModel);
        await userUpdate(editModel);
        getUserListResult(searchObj);
    });
}
// 详情抽屉关闭事件
const handleDrawerCancel = () => {
    opened.value = false;
    editForm.value.resetFields();
}
// 查看用户详情
const handleInfo = async (index, row) => {
    const d = await userInfo(row.userId);
    const _k = Object.keys(d.content);
    const _v = Object.values(d.content);
    for (let i = 0; i < _k.length; i++) {
        info[_k[i]] = _v[i];
    }
    isShowDialogInfo.value = true;
}

/* 删除相关函数 */
let deleteId = '';
// 删除用户
const handleDeleteUser = (index, row) => {
    isShowDialogDelete.value = true;
    deleteId = row.userId;
}
// 弹出删除弹窗
const handleDeleteRequest = () => {
    userDelete(deleteId).then(res => {
        getUserListResult(searchObj);
        isShowDialogDelete.value = false;
    });
}

/* 新增相关函数 */
const addUserStatusOptions = computed(() => userStatusOptions.filter(item => item.value !== '-1'));// 用户状态选项

// 新增表单实例
const addForm = ref(null);
const userAddForm = reactive({});// 新增表单模型
// 提交新增表单
const handleAddRequest = () => {
    // 表单验证
    addForm.value.validate((valid) => {
        if (!valid) return;
        isShowDialogAdd.value = false;// 关闭弹窗
        userAdd(userAddForm).then(res => {
            getUserListResult(searchObj);// 刷新用户列表
        });
    });

}
// 弹窗打开回调
const handleDialogOpen = () => {
    addForm.value.resetFields();
};
// 弹窗关闭回调
const handleDialogClose = () => {
    isShowDialogAdd.value = false;// 关闭弹窗
    addForm.value.resetFields();// 重置表单验证
};


/* 请求参数构建 */
// 搜索
const handleSearch = () => {
    searchObj.userName = input.value;
    getUserListResult(searchObj);
}

// 启用状态筛选
const updateUserStatus = () => {
    if (isUserDisabled.value === '-1') {
        delete searchObj.userStatus;
        return;
    }
    searchObj.userStatus = isUserDisabled.value;
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
    <WBTable :table-data="tableData" :table-columns="tableColumns" :operation-list="operationButtons"
        @operation-click="handleOperationClick" :total="responseData.total" v-model:current-page="responseData.pages"
        v-model:page-size="responseData.size" @update:page-size="handleSizeChange"
        @update:current-page="handleCurrentChange" height="100%" :border=true style="width: 100%">
        <!-- 表格头 -->
        <template #header>
            <WBTableHeader v-model:input="input" v-model:select="isUserDisabled" @click-add="handleUserAdd"
                @update-status="updateUserStatus" @click-search="handleSearch" :options="userStatusOptions">
            </WBTableHeader>
        </template>
        <!-- 表格内容 -->
        <template #default="scope">
            <el-tag v-if="scope.prop === 'userStatus'"
                :type="tableData[scope.$index][scope.prop] === '0' ? 'success' : 'danger'">
                {{ getStatusLabel(tableData[scope.$index][scope.prop]) }}
            </el-tag>
            <span v-else-if="scope.prop === 'userRole'">{{ getRoleLabel(tableData[scope.$index][scope.prop]) }}</span>
            <span v-else>{{ tableData[scope.$index][scope.prop] }}</span>
        </template>
    </WBTable>

    <!-- 新增弹窗 -->
    <WBDialog v-model="isShowDialogAdd" title="新增用户" :draggable="true" @submit="handleAddRequest" @close="handleDialogClose"
        @cancel="handleDialogClose" @open="handleDialogOpen">
        <WBForm :model="userAddForm" :rules="editRules" ref="addForm" :items="getFormConfig('add')">
            <!-- 内容 -->
            <template #default="scope">
                <el-input v-if="scope.key === 'userPassword'" type="password" show-password v-model="userAddForm[scope.key]"
                    placeholder="请输入" />
                <!-- 启用状态选择 -->
                <el-select v-else-if="scope.key === 'userStatus'" v-model="userAddForm[scope.key]" placeholder="启用状态">
                    <el-option v-for="(val, index) in addUserStatusOptions" :key="index" :label="val.label"
                        :value="val.value" />
                </el-select>
                <!-- 角色选择 -->
                <el-select v-else-if="scope.key === 'userRole'" v-model="userAddForm[scope.key]" placeholder="用户角色">
                    <el-option v-for="(val, index) in roles" :key="index" :label="val.roleName" :value="val.roleId" />
                </el-select>
                <el-input v-else v-model="userAddForm[scope.key]" placeholder="请输入"></el-input>
            </template>
            <!-- <el-form-item v-for="(val, index) in filteredResponse" :prop="val.prop" :key="index" :label="`${val.label}：`"
                label-position="right" label-width="100px" size="large"> -->
            <!-- 用户密码 -->
            <!-- <el-input v-if="val.prop === 'userPassword'" type="password" show-password v-model="val.value"
                    placeholder="请输入" /> -->
            <!-- 用户邮箱 -->
            <!-- <el-input v-else-if="val.prop === 'userEmail'" type="email" v-model="val.value" placeholder="请输入" /> -->

            <!-- 用户角色选择  @change="addUserRole"-->
            <!-- <el-select v-else-if="val.prop === 'userRole'" v-model="val.value" placeholder="用户角色" size="large"
                    style="width: 240px;">
                    <el-option v-for="(val, index) in roles" :key="index" :label="val" :value="val" />
                </el-select> -->

            <!-- 用户状态选择  @change="addUserStatus"-->
            <!-- <el-select v-else-if="val.prop === 'userStatus'" v-model="val.value" placeholder="用户状态" size="large"
                    style="width: 240px;">
                    <el-option v-for="(val, index) in addUserStatusOptions" :key="index" :label="val.label"
                        :value="val.value" />
                </el-select> -->

            <!-- 用户面试数 -->
            <!-- <el-input v-else-if="val.prop === 'userInterviews'" v-model.number="val.value" placeholder="请输入" /> -->

            <!-- 其他 -->
            <!-- <el-input v-else v-model="val.value" placeholder="请输入" /> -->
            <!-- </el-form-item> -->
        </WBForm>
    </WBDialog>

    <!-- 删除弹窗 -->
    <WBDialog v-model="isShowDialogDelete" @submit="handleDeleteRequest" draggable @cancel="isShowDialogDelete = false"
        title="删除用户" width="500" align-center>
        <span>确定要删除吗？</span>
    </WBDialog>

    <!-- 编辑抽屉 -->
    <WBDrawer v-model="opened" @confirm="confirmEditForm" @cancel="handleDrawerCancel" title="编辑用户">
        <WBForm v-model="editModel" ref="editForm" :rules="editRules" :items="getFormConfig('edit')">
            <!-- 内容 -->
            <template #default="scope">
                <el-input v-if="scope.key === 'userPassword'" type="password" show-password v-model="editModel[scope.key]"
                    placeholder="请输入" />
                <!-- 启用状态选择 -->
                <el-select v-else-if="scope.key === 'userStatus'" v-model="editModel[scope.key]" placeholder="启用状态">
                    <el-option v-for="(val, index) in addUserStatusOptions" :key="index" :label="val.label"
                        :value="val.value" />
                </el-select>
                <!-- 角色选择 -->
                <el-select v-else-if="scope.key === 'userRole'" v-model="editModel[scope.key]" placeholder="用户角色">
                    <el-option v-for="(val, index) in roles" :key="index" :label="val.roleName" :value="val.roleId" />
                </el-select>
                <el-input v-else v-model="editModel[scope.key]" placeholder="请输入"></el-input>
            </template>
        </WBForm>
    </WBDrawer>

    <!-- 详情弹窗 -->
    <WBDialog v-model="isShowDialogInfo" title="用户信息" :draggable="true" width="30%" align-center>
        <WBForm v-model="info" :items="getFormConfig('info')" class="info-form">
            <template #default="scope">
                <el-avatar v-if="scope.key === 'userAvatar'" :src="info[scope.key]" size="large"></el-avatar>
                <span v-else-if="scope.key === 'userPassword'">不能看哦＞︿＜</span>
                <span v-else>{{ info[scope.key] }}</span>
            </template>
            <template #footer><a></a></template>
        </WBForm>
    </WBDialog>
</template>

<style lang="scss" scoped>
.table-container {
    .tableLab {
        display: flex;
        align-content: center;

        // flex-direction: column;
        // cursor: pointer;
        .search {
            position: relative;
            display: flex;
            margin: 0 1vw;
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

.info-form {
    display: flex;
    justify-content: center;
}

.page {
    margin: 3vh auto;
    display: flex;
    justify-content: center;
}
</style>
