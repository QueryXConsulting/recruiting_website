<template>
  <div class="app-container">
    <el-card class="box-card">
      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryRef" :inline="true">
        <el-form-item label="工种名称" prop="categoryName">
          <el-input v-model="queryParams.categoryName" placeholder="请输入工种名称" clearable />
        </el-form-item>
        <el-form-item label="状态" prop="categoryStatus" style="width: 120px;">
          <el-select v-model="queryParams.categoryStatus" placeholder="请选择状态" clearable>
            <el-option label="正常" value="0" default />
            <el-option label="停用" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">搜索</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮区域 -->
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" @click="handleAdd">新增</el-button>
        </el-col>
      </el-row>

      <!-- 表格区域 -->
      <el-table v-loading="loading" :data="categoryData">
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="categoryName" label="工种名称" width="200" align="center" />
        <el-table-column prop="categoryDescription" label="描述" min-width="300" :show-overflow-tooltip="true"
          align="center" />
        <el-table-column prop="categoryStatus" label="状态" width="120" align="center">
          <template #default="scope">
            <el-switch v-model="scope.row.categoryStatus" :active-value="'0'" :inactive-value="'1'"
              @change="handleStatusChange(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="scope">
            <el-button type="primary" link @click="handleUpdate(scope.row)">修改</el-button>
            <el-button type="primary" link @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页区域 -->
      <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize" :total="total"
        :page-sizes="[10, 20, 30, 40]" layout="total, sizes, prev, pager, next" @size-change="handleSizeChange"
        @current-change="handleCurrentChange" class="pagination-container" />

      <!-- 添加或修改工种对话框 -->
      <el-dialog :title="title" v-model="open" width="500px" append-to-body>
        <el-form ref="worktypeRef" :model="form" :rules="rules" label-width="80px">
          <el-form-item label="工种名称" prop="categoryName">
            <el-input v-model="form.categoryName" placeholder="请输入工种名称" />
          </el-form-item>
          <el-form-item label="描述" prop="categoryDescription">
            <el-input v-model="form.categoryDescription" type="textarea" placeholder="请输入描述" />
          </el-form-item>
          <el-form-item label="状态" prop="categoryStatus">
            <el-radio-group v-model="form.categoryStatus">
              <el-radio label="0">正常</el-radio>
              <el-radio label="1">停用</el-radio>
            </el-radio-group>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { categoryList, categoryAdd, categoryUpdate, categoryDelete, categoryStatus } from '@/api/admin/adminApi'

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  categoryName: undefined,
  categoryStatus: '0'
})

// 总条数
const total = ref(0)
const categoryData = ref([])
const title = ref('')
const open = ref(false)
const loading = ref(false)

const worktypeRef = ref(null)

const form = reactive({
  categoryId: undefined,
  categoryName: undefined,
  categoryDescription: undefined,
  categoryStatus: '0'
})

// 表单校验规则
const rules = {
  categoryName: [
    { required: true, message: '工种名称不能为空', trigger: 'blur' }
  ]
}

// 查询工种列表
const getList = async () => {
  loading.value = true
  try {
    const res = await categoryList(queryParams.pageNum, queryParams.pageSize, queryParams.categoryName, queryParams.categoryStatus)
    categoryData.value = res.content.records
    total.value = res.content.total
  } catch (error) {
    ElMessage.error('获取工种列表失败')
  } finally {
    loading.value = false
  }
}

// 表单重置
const reset = () => {
  form.categoryId = undefined
  form.categoryName = undefined
  form.categoryDescription = undefined
  form.categoryStatus = '0'
}

// 搜索按钮操作
const handleQuery = () => {
  queryParams.pageNum = 1
  // debugger
  getList()
}

// 新增按钮操作
const handleAdd = () => {
  reset()
  open.value = true
  title.value = '添加工种'
}

// 修改按钮操作
const handleUpdate = (row) => {
  reset()
  const { categoryId, categoryName, categoryDescription, categoryStatus } = row
  Object.assign(form, { categoryId, categoryName, categoryDescription, categoryStatus })
  open.value = true
  title.value = '修改工种'
}

// 提交按钮
const submitForm = async () => {
  if (!worktypeRef.value) return
  await worktypeRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.categoryId) {
          await categoryUpdate(form)
          ElMessage.success('修改成功')
        } else {
          await categoryAdd(form)
          ElMessage.success('新增成功')
        }
        open.value = false
        getList()
      } catch (error) {
        ElMessage.error(form.categoryId ? '修改失败' : '新增失败')
      }
    }
  })
}

// 删除按钮操作
const handleDelete = (row) => {
  ElMessageBox.confirm('确认要删除该工种吗？', '警告', {
    type: 'warning'
  }).then(async () => {
    try {
      await categoryDelete(row.categoryId)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 分页大小改变
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getList()
}

// 页码改变
const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getList()
}

// 取消按钮
const cancel = () => {
  open.value = false
  reset()
}

// 状态切换处理函数
const handleStatusChange = async (row) => {
  try {

    await categoryStatus(
      row.categoryStatus,
      row.categoryId
    )
    ElMessage.success('状态修改成功')
  } catch (error) {
    // 如果更新失败，回滚状态
    row.categoryStatus = row.categoryStatus === '0' ? '1' : '0'
    ElMessage.error('状态修改失败')
  }
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.app-container {
  /* padding: 10px; */
  height: 700px;
}

.mb8 {
  margin-bottom: 8px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
