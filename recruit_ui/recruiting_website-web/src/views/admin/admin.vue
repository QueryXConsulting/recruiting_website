<script lang="js" setup>
import { roleList } from '@/api/admin/RoleApi'
import { adminList, adminInfoURL, adminUpdate, adminAdd, adminDelete } from '@/api/admin/AdminApi';
import { ref, computed, reactive, defineEmits } from 'vue'
import useAdminStore from '@/store/adminStore'
import { ElMessage } from 'element-plus';
import WBDialog from '@/components/WBDialog.vue';
import WBForm from '@/components/WBForm.vue';
import WBDrawer from '@/components/WBDrawer.vue';
import WBTableHeader from '@/components/table/WBTableHeader.vue';
import WBTable from '@/components/table/WBTable.vue';

const tableData = ref([]); // 表格数据
const responseData = reactive({}); // 请求响应数据
const input = ref('');
let adminRoleOptions = [];// 用户角色选项
const isDisabled = ref(); // 筛选启用状态
const isShowDialogDelete = ref(false); // 删除弹窗
const isShowDialogAdd = ref(false); // 新增弹窗

const adminStatusOptions = [
    { value: '-1', label: '全部' },
    { value: '0', label: '启用', tag: 'success' },
    { value: '1', label: '禁用', tag: 'danger' }
];

const response = reactive([
    { tag: 'table', prop: 'adminId', label: '用户ID', value: '' },
    { tag: 'table, edit', prop: 'adminName', label: '昵称', value: '' },
    { tag: 'table, edit', prop: 'adminUsername', label: '账号', value: '' },
    { tag: 'table, edit', prop: 'adminStatus', label: '状态', value: '', option: adminStatusOptions },
    { tag: 'table', prop: 'roleName', label: '角色', value: '' },
    { tag: 'edit', prop: 'adminAvatar', label: '头像', value: '' },
    { tag: 'edit', prop: 'roleId', label: '角色', value: '' },
    { tag: 'edit', prop: 'adminPassword', label: '密码', value: '' },
]);
const filteredResponse = (tagVal) => {
    return response.filter(item => item.tag.includes(tagVal));
}
// 获得标签颜色
const getTagType = (o, val, tag) => {
    const op = filteredResponse(tag).find((item) => item.prop === o).option;
    return op?.find((item) => item.value === val).tag;
}
// 获得表格列标签
const getColumnLabel = (val, prop, tag) => {
    const op = filteredResponse(tag).find((item) => item.prop === prop).option
    if (!op) { return val[prop]; }
    return op.find((item) => item.value === val[prop]).label;
}
// 根据tag获得所以标签
const getColumnLabels = (tag) => {
    const _item = filteredResponse(tag);
    const keys = _item.flatMap((i) => i.prop);
    const values = _item.flatMap((i) => i.label);
    return keys.reduce((acc, key, index) => {
        acc[key] = values[index];
        return acc;
    }, {})
}
// 根据tag获得所以value
const getColumnValues = (tag) => {
    const _item = filteredResponse(tag);
    const keys = _item.flatMap((i) => i.prop);
    const values = _item.flatMap((i) => i.value);
    return keys.reduce((acc, key, index) => {
        acc[key] = values[index];
        return acc;
    }, {})
}

// 定义防抖函数
const debounce = (func, delay) => {
    let timeoutId;
    return function (...args) {
        const context = this;
        clearTimeout(timeoutId);
        timeoutId = setTimeout(() => func.apply(context, args), delay);
    };
};
// 处理表格右侧操作(形参列表：[按钮编号，行数据，行索引])
// 注：按钮为循环生成，事件会多次触发，因此使用防抖函数处理
const handleOperation = debounce((index, row, i) => {
    switch (index) {
        case 0: // 编辑
            handleEdit(row);
            break;
        case 1: // 详情查看
            handleInspect(row);
            break;
        case 2: // 删除
            handleDelete(row);
            break;
        default:
            break;
    }

}, 100);

// 新增弹窗表单
// const addResponse = computed(() => response.filter(item => item.prop !== 'adminId'));
// 表格列配置
const tableColumns = computed(() => {
    return filteredResponse('table');
});

const searchObj = useAdminStore().getQueryParams;
// 请求用户列表
function getListResult(obj) {
    tableData.length = 0;
    const data = adminList(obj);
    data.then(res => {
        responseData.size = res.content.size;// 每页显示条数
        responseData.total = res.content.total;// 总条数
        responseData.pages = res.content.pages;// 总页数
        responseData.current = res.content.current;// 当前页
        tableData.value = res.content.records;// 表格数据
        ElMessage.success(res.message);
    });
}
getListResult(searchObj);


/* 表格相关函数 */
// 新增用户
const handleUserAdd = (index, row) => {
    isShowDialogAdd.value = true;
}

/* 用户信息 */
const info = reactive({});
const isShowDialogInfo = ref(false);
const handleInspect = async (row) => {
    // 请求用户信息
    await adminInfoURL(row.adminId).then(res => { info.content = res.content; });
    // 打开弹窗
    isShowDialogInfo.value = true;
}


/* 删除相关函数 */
let deleteId = '';
// 删除用户
const handleDelete = (row) => {
    isShowDialogDelete.value = true;
    deleteId = row.adminId;
}
// 弹出删除弹窗
const handleDeleteRequest = () => {
    adminDelete(deleteId).then(res => {
        getListResult(searchObj);
        isShowDialogDelete.value = false;
    });
}


// 用户新增和修改信息对象
const editInfo = reactive(getColumnValues('edit'));
// 角色列表请求
const roleListRequest = async () => {
    const data = await roleList();
    adminRoleOptions = data.content;
}

// editinfo表单验证规则
const editRules = {
    adminAvatar: [
        { required: false, message: '请上传头像', trigger: 'change' }
    ],
    roleId: [
        { required: true, message: '请选择角色', trigger: 'change' }
    ],
    adminName: [
        { required: true, message: '请输入昵称', trigger: 'change' }
    ],
    adminUsername: [
        { required: true, message: '请输入账号', trigger: 'change' }
    ],
    adminPassword: [
        { required: true, min: 6, max: 20, message: '密码长度在6到20个字符之间', trigger: 'blur' }
    ],
    adminStatus: [
        { required: true, message: '请选择状态', trigger: 'change' }
    ],
}
/* 编辑相关函数 */
const isShowDialogEdit = ref(false);// 编辑弹窗控制
const roleSelectValue = ref('');// 角色选择值
const editForm = ref(null);// 编辑表单实例
let editId = '';
// 编辑用户信息
const handleEdit = async (row) => {
    // 请求用户信息
    const data = await adminInfoURL(row.adminId);
    const keys = Object.keys(data.content);
    // const values = Object.values(data.content);
    for (let i = 0; i < keys.length; i++) {
        editInfo[keys[i]] = null;
        // editInfo[keys[i]] = values[i];
    }
    await roleListRequest();// 请求角色列表
    const role = adminRoleOptions.find((item) => item.roleName === row.roleName)
    // 表单赋值
    editInfo.roleId = role.roleId;
    // 打开弹窗
    isShowDialogEdit.value = true;
    editId = row.adminId;// 记录编辑用户id
}

// 用户信息修改提交
const updateInfo = () => {
    // 表单验证
    editForm.value.validate(async (valid) => {
        if (!valid) {
            return;
        }
        isShowDialogEdit.value = false;
        // 构建参数对象
        editInfo.adminId = editId;
        // 发送请求
        await adminUpdate(editInfo);
        getListResult(searchObj);// 刷新用户列表
    });
}
// 新增抽屉打开回调
const handleDrawerOpen = () => {
    editForm.value.resetFields();
}
// 抽屉关闭回调
const resetUpdateValidate = () => {
    isShowDialogEdit.value = false; // 关闭弹窗
    resetEditInfo(); // 重置表单数据对象
    editForm.value.resetFields();// 重置表单验证
}

/* 新增相关函数 */
const filteredStatusOptions = computed(() => adminStatusOptions.filter(item => item.value !== '-1'));// 用户状态选项
// 新增表单实例
const addForm = ref(null);
// 提交新增表单
const handleAddRequest = () => {
    // 表单验证
    addForm.value.validate((valid) => {
        if (!valid) {
            return;
        }
        isShowDialogAdd.value = false;// 关闭弹窗
        console.log('add', editInfo);
        adminAdd(editInfo).then(res => {
            getListResult(searchObj);// 刷新用户列表
        });
    });

}
// 新增弹窗打开回调
const handleDialogOpen = async () => {
    await roleListRequest();// 请求角色列表
    resetEditInfo();
    addForm.value.resetFields();
};
// 弹窗关闭回调
const handleDialogClose = () => {
    isShowDialogAdd.value = false;// 关闭弹窗
    addForm.value.resetFields();// 重置表单验证
    resetEditInfo();// 重置表单数据对象
};

// 重置表单数据对象
const resetEditInfo = () => {
    Object.entries(editInfo).forEach(([key, _]) => {
        editInfo[key] = null;
    });
}

/* 请求参数构建 */
// 搜索
const handleSearch = () => {
    searchObj.adminName = input.value;
    getListResult(searchObj);
}

// 启用状态筛选
const updateStatus = () => {
    if (isDisabled.value === '-1') {
        delete searchObj.adminStatus;
        return;
    }
    searchObj.adminStatus = isDisabled.value;
}

/* 分页相关 */
// 处理显示行数
const handleSizeChange = (val) => {
    console.log('handleSizeChange', val);
    // searchObj.size = val;
    // getListResult(searchObj);
}
// 处理翻页
const handleCurrentChange = (val) => {
    console.log('handleCurrentChange', val);
    // searchObj.page = val;
    // getListResult(searchObj);
}

const btnObj = [
    { text: '编辑', type: 'default' },
    { text: '查看', type: 'default' },
    { text: '删除', type: 'danger' },
]

</script>

<template>
    <main>
        <!-- 表格 -->
        <WBTable v-model:tableData="tableData" v-model:current-page="responseData.current"
            v-model:page-size="responseData.size" :total="responseData.total" :table-columns="filteredResponse('table')"
            @update:current-page="handleCurrentChange" @update:page-size="handleSizeChange" @operation-click="handleOperation" :operation-list="btnObj" border>
            <!-- 顶部搜索栏 -->
            <template #header>
                <WBTableHeader v-model:input="input" v-model:select="isDisabled" :options="adminStatusOptions"
                    @click-add="handleUserAdd" @click-search="handleSearch" @update-status="updateStatus">
                </WBTableHeader>
            </template>
            <!-- 表格内容 -->
            <template #default="scope">
                <el-tag v-if="scope.prop === 'adminStatus'"
                    :type="getTagType(scope.prop, tableData[scope.$index][scope.prop], 'table')">
                    {{ getColumnLabel(tableData[scope.$index], scope.prop, 'table') }}
                </el-tag>
                <span v-else>{{ tableData[scope.$index][scope.prop] }}</span>
            </template>
        </WBTable>
    </main>


    <!-- 新增弹窗 -->
    <WBDialog v-model="isShowDialogAdd" title="新增用户" :draggable="true" @submit="handleAddRequest" @close="handleDialogClose"
        @cancel="handleDialogClose" @open="handleDialogOpen" width="40%">
        <WBForm :model="editInfo" :rules="editRules" ref="addForm" :items="getColumnLabels('edit')">
            <template #default="scope">
                <!-- 用户密码 -->
                <el-input v-if="scope.key === 'adminPassword'" type="password" show-password v-model="editInfo[scope.key]"
                    placeholder="请输入" />
                <!-- 用户角色选择 -->
                <el-select v-else-if="scope.key === 'roleId'" v-model="editInfo[scope.key]" placeholder="用户角色" size="large">
                    <el-option v-for="(val, index) in adminRoleOptions" :key="index" :label="val.roleName"
                        :value="val.roleId" />
                </el-select>
                <!-- 用户状态选择 -->
                <el-select v-else-if="scope.key === 'adminStatus'" v-model="editInfo[scope.key]" placeholder="用户状态"
                    size="large">
                    <el-option v-for="(val, index) in filteredStatusOptions" :key="index" :label="val.label"
                        :value="val.value" />
                </el-select>
                <!-- 其他 -->
                <el-input v-else v-model="editInfo[scope.key]" placeholder="请输入" />
            </template>
        </WBForm>
    </WBDialog>

    <!-- 删除弹窗 -->
    <WBDialog v-model="isShowDialogDelete" @submit="handleDeleteRequest" @cancel="isShowDialogDelete = false" title="删除用户"
        width="500" align-center>
        <span>确定要删除吗？</span>
    </WBDialog>

    <!-- 用户信息弹窗 -->
    <WBDialog v-model="isShowDialogInfo" class="info-dialog" title="用户信息" width="300" align-center draggable>
        <WBForm :model="info" class="info-form" :items="getColumnLabels('table')">
            <template #default="scope">
                <span>{{ info.content[scope.key] }}</span>
            </template>
        </WBForm>
        <template #footer><br></template>
    </WBDialog>

    <!-- 用户编辑抽屉 -->
    <WBDrawer v-model="isShowDialogEdit" @confirm="updateInfo" @cancel="resetUpdateValidate" @close="resetUpdateValidate"
        @open="handleDrawerOpen" title="编辑用户" size="30%">
        <WBForm :model="editInfo" :rules="editRules" :items="getColumnLabels('edit')" ref="editForm" class="edit-form">
            <template #default="scope">
                <!-- 角色选择 -->
                <el-select v-if="scope.key === 'roleId'" v-model="editInfo[scope.key]">
                    <el-option v-for="(item, i) in adminRoleOptions" :key="i" :label="item.roleName" :value="item.roleId" />
                </el-select>
                <!-- 用户状态选择 -->
                <el-select v-else-if="scope.key === 'adminStatus'" v-model="editInfo[scope.key]" placeholder="用户状态"
                    size="large">
                    <el-option v-for="(val, i) in filteredStatusOptions" :key="i" :label="val.label" :value="val.value" />
                </el-select>
                <el-input v-else-if="scope.key === 'adminPassword'" show-password type="password"
                    v-model="editInfo[scope.key]" placeholder="请输入" />
                <el-input v-else v-model="editInfo[scope.key]" placeholder="请输入" />
            </template>
        </WBForm>
    </WBDrawer>
</template>

<style lang="scss" scoped>
main {
    // padding: 10px;
    background: #fff;
}

// .info-form {
//     display: flex;
//     justify-content: center;
// }
</style>
