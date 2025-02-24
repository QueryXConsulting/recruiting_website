<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>菜单管理</span>
          <el-button type="primary" plain @click="handleAdd">
            新增
          </el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryRef" :inline="true">
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="queryParams.menuName" placeholder="请输入菜单名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态" prop="status" style="width: 115px">
          <el-select v-model="queryParams.status" placeholder="" clearable style="width: 100%">
            <el-option v-for="dict in statusOptions" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery(queryParams.status, queryParams.menuName)">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格区域 -->
      <el-table v-loading="loading" :data="menus" row-key="menuId"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }">
        <el-table-column prop="menuName" label="菜单名称" :show-overflow-tooltip="true" />
        <el-table-column prop="menuType" label="菜单类型" align="center" width="80">
          <template #default="scope">
            <el-tag
              :type="scope.row.menuType === 'M' ? 'warning' : scope.row.menuType === 'C' ? 'success' : scope.row.menuType === 'F' ? 'info' : 'primary'">
              {{ scope.row.menuType === 'M' ? '目录' : scope.row.menuType === 'C' ? '菜单' : scope.row.menuType === 'F' ?
                '按钮' : '用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="icon" label="图标" align="center" width="100">
          <template #default="scope">
            <el-icon v-if="scope.row.icon != '#'">
              <component :is="iconMapping[scope.row.icon]" />
            </el-icon>
            <span v-else>{{ scope.row.icon }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderNum" label="排序" width="60" align="center" />
        <el-table-column prop="perms" label="权限标识" :show-overflow-tooltip="true" align="center" />
        <el-table-column prop="component" label="组件路径" :show-overflow-tooltip="true" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === '0' ? 'success' : 'info'">
              {{ scope.row.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
        <el-table-column label="操作" align="center" width="200">
          <template #default="scope">
            <el-button link type="primary" @click="handleUpdate(scope.row)">
              修改
            </el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 添加或修改菜单对话框 -->
      <el-dialog :title="title" v-model="open" width="500px" top="40px">
        <el-form ref="menuRef" :model="form" :rules="rules" label-width="80px">
          <el-form-item label="上级菜单" prop="parentId">
            <el-tree-select v-model="form.parentId" :data="menuOptions"
              :props="{ label: 'menuName', value: 'menuId', children: 'children' }" placeholder="选择上级菜单" check-strictly
              clearable />
          </el-form-item>
          <el-form-item label="菜单类型" prop="menuType">
            <el-radio-group v-model="form.menuType">
              <el-radio value="M">目录</el-radio>
              <el-radio value="C">菜单</el-radio>
              <el-radio value="F">按钮</el-radio>
              <el-radio value="U">用户</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="菜单名称" prop="menuName">
            <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
          </el-form-item>
          <el-form-item label="权限标识" prop="perms">
            <el-input v-model="form.perms" placeholder="请输入权限标识" />
          </el-form-item>
          <el-form-item label="路由地址" prop="path" v-if="form.menuType !== 'F'">
            <el-input v-model="form.path" placeholder="请输入路由地址" />
          </el-form-item>
          <el-form-item label="组件路径" prop="component" v-if="form.menuType === 'C' || form.menuType === 'U'">
            <el-input v-model="form.component" placeholder="请输入组件路径" />
          </el-form-item>
          <el-form-item label="接口地址" prop="api" v-if="form.menuType === 'F'">
            <el-input v-model="form.path" placeholder="请输入接口地址" />
          </el-form-item>
          <el-form-item label="显示排序" prop="orderNum">
            <el-input-number v-model="form.orderNum" controls-position="right" :min="0" />
          </el-form-item>
          <el-form-item label="状态">
            <el-radio-group v-model="form.status">
              <el-radio v-for="dict in statusOptions" :key="dict.value" :label="dict.value">
                {{ dict.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="显示状态">
            <el-radio-group v-model="form.visible">
              <el-radio value="0">显示</el-radio>
              <el-radio value="1">隐藏</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
          </el-form-item>
          <el-form-item label="图标" prop="icon" v-if="form.menuType !== 'F'">
            <el-popover placement="bottom-start" :width="540" trigger="click" v-model:visible="iconSelectVisible">

              <template #reference>
                <el-input v-model="form.icon" readonly>
                  <template #prefix>
                    <el-icon v-if="form.icon">
                      <component :is="iconMapping[form.icon]" />
                    </el-icon>
                  </template>
                  <template #suffix>
                    <el-icon>
                      <arrow-down />
                    </el-icon>
                  </template>
                </el-input>
              </template>

              <div style="height: 300px; overflow-y: auto;">
                <div class="icon-grid">
                  <div v-for="icon in iconList" :key="icon" class="icon-item" @click="selectIcon(icon)">
                    <el-icon>
                      <component :is="iconMapping[icon]" />
                    </el-icon>
                    <span>{{ icon }}</span>
                  </div>
                </div>
              </div>
            </el-popover>
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button type="primary" @click="submitForm">确 定</el-button>
            <el-button @click="cancel">取 消</el-button>
          </div>
        </template>
      </el-dialog>

    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { menuList, menuAdd, menuUpdate, menuDelete } from '@/api/admin/adminApi'
import { iconList, iconMapping } from '@/utils/iconList'
import { ArrowDown } from '@element-plus/icons-vue'

const menus = ref([])

// 查询菜单列表
const getMenuList = async (status, menuName) => {
  try {
    loading.value = true
    const { content } = await menuList(status, menuName)
    menus.value = content
  } catch (error) {
    console.error('获取菜单列表失败:', error)
    ElMessage.error('获取菜单列表失败')
  } finally {
    loading.value = false
  }
}

// 状态数据字典
const statusOptions = [
  { label: '正常', value: '0' },
  { label: '停用', value: '1' }
]

const loading = ref(false)
const open = ref(false)
const title = ref('')


// 查询参数
const queryParams = ref({
  menuName: undefined,
  status: "0"
})

// 表单参数
const form = ref({
  menuId: undefined,
  menuName: undefined,
  menuType: 'M',
  orderNum: 0,
  path: undefined,
  component: undefined,
  perms: undefined,
  icon: undefined,
  status: '0',
  visible: '0',
  remark: undefined,
})

// 表单校验规则
const rules = {
  menuName: [{ required: true, message: '菜单名称不能为空', trigger: 'blur' }],
  orderNum: [{ required: true, message: '显示排序不能为空', trigger: 'blur' }],
  path: [{ required: true, message: '路由地址不能为空', trigger: 'blur' }]
}



// 表单重置
const reset = () => {
  form.value = {
    menuId: undefined,
    menuName: undefined,
    menuType: 'M',
    orderNum: 0,
    path: undefined,
    component: undefined,
    perms: undefined,
    icon: undefined,
    status: '0',
    visible: '0',
    remark: undefined,
  }
}

// 搜索按钮操作
const handleQuery = (status, menuName) => {

  if (status || menuName) {
    const filterMenus = (items) => {
      return items.filter(item => {
        const matchStatus = !status || item.status === status
        const matchName = !menuName || item.menuName.includes(menuName)
        const hasMatchingChildren = item.children && item.children.length > 0 &&
          filterMenus(item.children).length > 0

        if (matchStatus && matchName) {
          return true
        }

        if (hasMatchingChildren) {
          item.children = filterMenus(item.children)
          return true
        }

        return false
      })
    }

    const filteredMenus = filterMenus([...menus.value])
    menus.value = filteredMenus
  } else {
    // 无搜索条件时重新获取完整列表
    getMenuList()
  }
}

// 重置按钮操作
const resetQuery = () => {
  queryParams.value = {
    menuName: undefined,
    status: undefined
  }
  handleQuery()
}

const menuOptions = ref([])

// 获取菜单下拉树列表
const getMenuOptions = async () => {
  try {
    const { content } = await menuList()

    const filterMenus = (menus) => {
      return menus.filter(item => {

        if (item.menuId) {
          item.menuId = item.menuId.toString()
        }

        if (item.menuType == 'F') {
          return false
        }
        if (item.children && item.children.length > 0) {
          const filteredChildren = filterMenus(item.children)
          if (filteredChildren.length > 0) {
            item.children = filteredChildren
          } else {
            delete item.children
          }
        }
        return true
      })
    }

    menuOptions.value = [
      {
        menuId: '0',  // 将menuId改为字符串类型
        menuName: '主菜单',
        children: filterMenus(content)
      }
    ]
  } catch (error) {
    console.error('获取菜单选项失败:', error)
    ElMessage.error('获取菜单选项失败')
  }
}

// 新增按钮操作
const handleAdd = () => {
  reset()
  getMenuOptions()
  open.value = true
  title.value = '添加菜单'
}

// 修改按钮操作
const handleUpdate = (row) => {
  reset()
  getMenuOptions()
  form.value = { ...row }
  open.value = true
  title.value = '修改菜单'
}

// 提交按钮
const submitForm = async () => {
  try {
    if (form.value.menuId) {
      await menuUpdate(form.value)
      ElMessage.success('修改成功')
    } else {
      await menuAdd(form.value)
      ElMessage.success('新增成功')
    }
    open.value = false
    getMenuList()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 删除按钮操作
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('是否确认删除名称为"' + row.menuName + '"的数据项?', '警告', {
      type: 'warning'
    })
    await menuDelete(row.menuId)
    ElMessage.success('删除成功')
    getMenuList()
  } catch (error) {
    console.error('删除失败:', error)
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 取消按钮
const cancel = () => {
  open.value = false
  reset()
}



const iconSelectVisible = ref(false)

// 选择图标
const selectIcon = (icon) => {
  form.value.icon = icon
  iconSelectVisible.value = false
}

onMounted(() => {
  getMenuList()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.icon-grid {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 12px;
  padding: 16px;
}

.icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 12px 8px;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.icon-item:hover {
  background-color: #f5f7fa;
}

.icon-item .el-icon {
  font-size: 20px;
  margin-bottom: 8px;
}

.icon-item span {
  font-size: 12px;
  color: #666;
  text-align: center;
}


::v-deep(.el-input__wrapper) {
  cursor: pointer !important;
}

::v-deep(.el-input__inner) {
  cursor: pointer !important;
}


::v-deep(.el-table__row) {
  height: 50px;
}
</style>
