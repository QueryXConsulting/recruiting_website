<template>
  <div class="job-type-container">
    <!-- 顶部操作栏 -->
    <div class="operation-bar">
      <el-button type="primary" @click="handleAdd">新增工作性质</el-button>
    </div>

    <!-- 数据表格 -->
    <el-table :data="tableData" style="width: 100%" :cell-style="{ padding: '8px 0', textAlign: 'center' }"
      :header-cell-style="{ background: 'transparent', textAlign: 'center', padding: '8px 0' }">
      <el-table-column label="序号" width="80">
        <template #default="scope">
          {{ scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column prop="jobNatureName" label="工作性质名称" />
      <el-table-column prop="natureStatus" label="状态">
        <template #default="scope">
          <el-switch v-model="scope.row.natureStatus" :active-value="'0'" :inactive-value="'1'"
            @change="handleStatusChange(scope.row)" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button type="primary" link :icon="Edit" @click="handleEdit(scope.row)">
            编辑
          </el-button>
          <el-button type="danger" link :icon="Delete" @click="handleDelete(scope.row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="性质名称" prop="jobNatureName">
          <el-input v-model="form.jobNatureName" placeholder="请输入工作性质名称" />
        </el-form-item>
        <el-form-item label="状态" prop="natureStatus">
          <el-radio-group v-model="form.natureStatus">
            <el-radio label="0">启用</el-radio>
            <el-radio label="1">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
        <el-button @click="dialogVisible = false">取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Delete } from '@element-plus/icons-vue'
import {
  jobNatureList,
  jobNatureStatus,
  jobNatureUpdate,
  jobNatureAdd,
  jobNatureDelete
} from '@/api/admin/adminApi'

// 表格数据
const tableData = ref([])

// 对话框控制
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

// 表单数据
const form = reactive({
  natureId: '',
  jobNatureName: '',
  natureStatus: '0'
})

// 表单验证规则
const rules = {
  jobNatureName: [
    { required: true, message: '请输入工作性质名称', trigger: 'blur' },
    { max: 20, message: '长度不能超过20个字符', trigger: 'blur' }
  ],
  natureStatus: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 获取数据列表
const getList = async () => {
  try {
    const { content } = await jobNatureList(undefined)
    tableData.value = content
  } catch (error) {
    ElMessage.error('获取数据失败')
  }
}

// 新增按钮处理
const handleAdd = () => {
  dialogTitle.value = '新增工作性质'
  form.natureId = ''
  form.jobNatureName = ''
  form.natureStatus = '0'
  dialogVisible.value = true
}

// 编辑按钮处理
const handleEdit = (row) => {
  dialogTitle.value = '编辑工作性质'
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除按钮处理
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该工作性质吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await jobNatureDelete(row.natureId)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.natureId) {

          await jobNatureUpdate({
            natureId: form.natureId,
            jobNatureName: form.jobNatureName,
            natureStatus: form.natureStatus
          })
        } else {
          debugger
          await jobNatureAdd(form.jobNatureName, form.natureStatus)
        }
        ElMessage.success(form.natureId ? '更新成功' : '添加成功')
        dialogVisible.value = false
        getList()
      } catch (error) {
        ElMessage.error(form.natureId ? '更新失败' : '添加失败')
      }
    }
  })
}

// 添加状态切换处理函数
const handleStatusChange = async (row) => {
  const originalStatus = row.natureStatus
  try {
    await jobNatureStatus(row.natureId, row.natureStatus)
    ElMessage.success('状态更新成功')
  } catch (error) {

    row.natureStatus = originalStatus === '0' ? '1' : '0'
    ElMessage.error('状态更新失败')
  }
}

onMounted(() => {
  getList()
})
</script>

<style scoped lang="scss">
.job-type-container {
  padding: 24px;
  background: #fff;
  border-radius: 4px;
}

.operation-bar {
  margin-bottom: 24px;
}
</style>
