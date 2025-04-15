<template>
  <div class="app-container">
    <!-- 操作按钮区域 -->
    <el-card class="table-wrapper">
      <template #header>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" @click="handleAdd">
              <el-icon>
                <Plus />
              </el-icon>新增
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" :disabled="multiple" @click="handleDelete">
              <el-icon>
                <Delete />
              </el-icon>删除
            </el-button>
          </el-col>
        </el-row>
      </template>

      <!-- 表格区域 -->
      <el-table v-loading="loading" :data="roleList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" />
        <el-table-column label="序号" width="200">
          <template #default="scope">
            {{ scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="角色名称" prop="roleName" width="200" :show-overflow-tooltip="true" />
        <el-table-column label="角色类型" width="100" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.roleType === '1'" type="success">系统角色</el-tag>
            <el-tag v-else type="info">用户角色</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="显示顺序" prop="roleSort" width="200" align="center" />
        <el-table-column label="状态" width="120">
          <template #default="scope">
            <el-switch v-model="scope.row.status" active-value="0" inactive-value="1"
              :disabled="scope.row.roleId === '1'" @change="handleStatusChange(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="200" />
        <el-table-column label="操作" width="200" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button type="primary" link :disabled="scope.row.roleId === '1'" @click="handleUpdate(scope.row)">
              <el-icon>
                <Edit />
              </el-icon>修改
            </el-button>
            <el-button type="primary" link :disabled="scope.row.roleId === '1'" @click="handleDelete(scope.row)">
              <el-icon>
                <Delete />
              </el-icon>删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>


    </el-card>

    <!-- 添加或修改角色弹窗 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色类型" prop="roleType">
          <el-radio-group v-model="form.roleType">
            <el-radio :value="'1'">系统角色</el-radio>
            <el-radio :value="'0'">用户角色</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色顺序" prop="roleSort">
          <el-input-number v-model="form.roleSort" controls-position="right" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="'0'">正常</el-radio>
            <el-radio :value="'1'">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单权限">
          <el-tree ref="menuRef" :data="menuTree" :props="treeProps" show-checkbox node-key="menuId"
            :check-strictly="true" @check="handleCheck" @check-change="handleCheckChange">
            <template #default="{ node, data }">
              <span>
                {{ node.label }}
                <el-tag v-if="data.menuType === 'U'" size="small" type="success" style="margin-left: 8px">用户</el-tag>
              </span>
            </template>
          </el-tree>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>




<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus, Delete, Edit } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { roleInfo, roleMenus, roleUpdate, selectRoleList, roleAdd, roleDelete, roleStatus } from '@/api/admin/adminApi'

// 状态定义
const loading = ref(false)
const roleList = ref([])

const open = ref(false)
const title = ref('')
const multiple = ref(true)
const roleMenusId = ref([])
const menuRef = ref(null)
const formRef = ref(null)
const menuTree = ref([])

// 表单数据
const form = reactive({
  roleId: undefined,
  roleName: '',
  roleSort: 0,
  status: '0',
  roleType: '0', // 默认为用户角色
  menuIds: [],
})

// 表单校验规则
const rules = {
  roleName: [
    { required: true, message: '角色名称不能为空', trigger: 'blur' }
  ],
  roleSort: [
    { required: true, message: '角色顺序不能为空', trigger: 'blur' }
  ]
}

// 树形配置
const defaultProps = {
  children: 'children',
  label: 'menuName'
}

// 修改树形控件配置
const treeProps = {
  ...defaultProps,
  checkStrictly: true,
  class: (data) => {
    // 为用户类型菜单添加特殊样式类
    return data.menuType === 'U' ? 'user-menu-node' : ''
  }
}

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const result = await selectRoleList()
    roleList.value = result.content
  } catch (error) {
    console.error('获取角色列表失败:', error)
  } finally {
    loading.value = false
  }
}



// 多选框选中数据
const ids = ref([])
const handleSelectionChange = (selection) => {
  multiple.value = selection.length === 0
  ids.value = selection.map(item => item.roleId)
}

// 新增按钮操作
const handleAdd = async () => {
  title.value = '添加角色'
  open.value = true

  // 重置表单数据
  Object.assign(form, {
    roleId: undefined,
    roleName: '',
    roleSort: 0,
    status: '0',
    roleType: '2', // 重置为默认用户角色
    menuIds: [],
  })

  // 获取菜单树数据
  await getMenuTree()

  // 清空已选菜单
  if (menuRef.value) {
    menuRef.value.setCheckedKeys([])
  }
}

// 请求角色菜单数据
const getMenuTreeId = async (roleId) => {
  const result = await roleInfo(roleId)

  if (result.content) {
    roleMenusId.value = result.content.menusListId
    return true
  }
  ElMessage.error('获取角色菜单数据失败')
  return false
}



// 请求菜单数据
const getMenuTree = async () => {
  const result = await roleMenus()

  if (result.content) {
    menuTree.value = result.content
    return true
  }
  ElMessage.error('获取菜单数据失败')
  return false
}




// 修改按钮操作
const handleUpdate = async (row) => {
  title.value = '修改角色'
  open.value = true

  // 先重置表单数据
  resetForm()

  // 重置树形控件的选中状态
  if (menuRef.value) {
    menuRef.value.setCheckedKeys([])
  }

  // 获取最新的菜单树数据
  await getMenuTree()

  // 更新表单数据
  Object.assign(form, row)

  // 获取角色的菜单权限并设置选中状态
  await getMenuTreeId(row.roleId)
  if (menuRef.value && roleMenusId.value) {
    menuRef.value.setCheckedKeys(roleMenusId.value, false)
  }
}

// 删除按钮操作
const handleDelete = async (row) => {
  try {

    await ElMessageBox.confirm('确认要删除选中的角色吗？', '警告', {
      type: 'warning',
      cancelButtonText: '取消',
      confirmButtonText: '确定',
    })

    if (ids.value.length === 0) {
      ids.value.push(row.roleId)
    }
    const result = await roleDelete(ids.value)

    if (result.code === 200) {
      ElMessage.success('删除成功')
      await getList()
    } else {
      ElMessage.error('删除失败')
    }
  } catch (error) {
    console.error('删除角色失败:', error)
  }
}

// 状态修改
const handleStatusChange = async (row) => {
  try {
    await roleStatus(row.roleId, row.status)
    ElMessage.success(row.status === '0' ? '启用成功' : '停用成功')
  } catch (error) {
    row.status = row.status === '0' ? '1' : '0'
    ElMessage.error('操作失败')
  }
}

// 处理节点选中
const handleCheck = (data, node) => {
  const isChecked = menuRef.value.getNode(data).checked

  // 为用户类型菜单添加特殊标识
  if (data.menuType === 'U') {
    const parentNode = menuRef.value.getNode(data.parentId)
    if (parentNode && isChecked) {
      ElMessage.info(`${data.menuName}是用户菜单`)
    }
  }

  if (isChecked) {
    data.children?.length > 0 && setChildrenChecked(data.children, true)
  } else {
    data.children?.length > 0 && setChildrenChecked(data.children, false)
  }
}

// 递归设置子节点选中状态
const setChildrenChecked = (nodes, checked) => {
  nodes.forEach(item => {
    item.children?.length > 0 && setChildrenChecked(item.children, checked)
    menuRef.value.setChecked(item.menuId, checked, false)
  })
}


const handleCheckChange = (data, checked, indeterminate) => {
  const currentNode = menuRef.value.getNode(data)

  // 处理父节点状态
  if (currentNode.parent && currentNode.parent.key !== 0) {
    // 如果有子节点被选中,设置父节点为半选状态
    if (currentNode.parent.childNodes.some(node => node.checked)) {
      currentNode.parent.indeterminate = true
    }

    // 如果所有子节点都被选中,设置父节点为全选状态
    if (currentNode.parent.childNodes.every(node => node.checked)) {
      currentNode.parent.checked = true
      currentNode.parent.indeterminate = false
    }

    // 如果所有子节点都未选中,取消父节点选中状态
    if (currentNode.parent.childNodes.every(node => !node.checked)) {
      currentNode.parent.checked = false
      currentNode.parent.indeterminate = false
    }
  }
}

// 表单提交
const submitForm = async () => {
  try {
    await formRef.value.validate()

    // 获取选中的节点和半选中的节点
    const checkedKeys = menuRef.value?.getCheckedKeys(false) || []
    const halfCheckedKeys = menuRef.value?.getHalfCheckedKeys() || []

    // 合并选中的节点ID
    form.menuIds = [...new Set([...checkedKeys, ...halfCheckedKeys])]

    let result
    if (form.roleId) {

      result = await roleUpdate(form)
    } else {
      result = await roleAdd(form)
    }

    if (result.code === 200) {
      ElMessage.success(form.roleId ? '修改成功' : '新增成功')
      open.value = false
      await getList()
    } else {
      if (form.roleId === '1') {
        ElMessage.error('超级管理员不能进行修改')
      } else {
        ElMessage.error('操作失败')
      }
    }
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

// 取消按钮
const cancel = () => {
  open.value = false
  resetForm()
  // 重置菜单选中状态
  if (menuRef.value) {
    menuRef.value.setCheckedKeys([])
  }
}

const resetForm = () => {
  formRef.value?.resetFields()
}

onMounted(() => {
  getList()
})
</script>

<style scoped lang="scss">
:deep(.el-table) {
  width: 100%;
  table-layout: fixed;
}

:deep(.el-table .cell) {
  padding: 0 8px;
}

:deep(.el-button--link) {
  padding: 4px 8px;
  margin: 0 4px;
}

:deep(.el-button--link .el-icon) {
  margin-right: 4px;
}

/* 确保表格列均匀分布 */
:deep(.el-table__header-wrapper),
:deep(.el-table__body-wrapper) {
  width: 100%;
}

:deep(.el-table td),
:deep(.el-table th) {
  text-align: center;
}
</style>
