<template>
  <div class="app-container">
    <el-card class="box-card">
      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryRef" :inline="true">
        <el-form-item label="操作名称" prop="operateName">
          <el-input v-model="queryParams.operateName" placeholder="请输入操作名称" clearable />
        </el-form-item>
        <el-form-item label="操作时间">
          <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
            end-placeholder="结束日期" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格区域 -->
      <el-table v-loading="loading" :data="filteredData">
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="operateUser" label="操作人id" width="120" align="center" />
        <el-table-column prop="operateName" label="操作名称" width="150" align="center" />
        <el-table-column prop="methodName" label="接口地址" width="180" align="center" />
        <el-table-column prop="params" label="请求参数" min-width="200" :show-overflow-tooltip="true" align="center" />
        <el-table-column prop="returnValue" label="返回值" min-width="200" :show-overflow-tooltip="true" align="center" />
        <el-table-column prop="gmtCreate" label="操作时间" width="180" align="center">
          <template #default="scope">
            {{ scope.row.gmtCreate }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template #default="scope">
            <el-button type="primary" link @click="handleView(scope.row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页区域 -->
      <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize" :total="total"
        :page-sizes="[10, 20, 30, 50]" layout="total, sizes, prev, pager, next" @size-change="handleSizeChange"
        @current-change="handleCurrentChange" class="pagination-container" />

      <!-- 添加查看详情对话框 -->
      <el-dialog title="日志详情" v-model="dialogVisible" width="700px" append-to-body>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="操作人ID">{{ viewData.operateUser }}</el-descriptions-item>
          <el-descriptions-item label="操作名称">{{ viewData.operateName }}</el-descriptions-item>
          <el-descriptions-item label="接口地址">{{ viewData.methodName }}</el-descriptions-item>
          <el-descriptions-item label="操作时间">{{ viewData.gmtCreate }}</el-descriptions-item>
          <el-descriptions-item label="请求参数">
            <div style="white-space: pre-wrap;">{{ viewData.params }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="返回值">
            <div style="white-space: pre-wrap;">{{ viewData.returnValue }}</div>
          </el-descriptions-item>
        </el-descriptions>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="dialogVisible = false">关 闭</el-button>
          </div>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

import { getOperateLog } from '@/api/admin/adminApi'

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  operateName: '',
  startTime: '',
  endTime: ''
})

// 日期范围
const dateRange = ref([])

// 数据相关
const total = ref(0)
const loading = ref(false)
const logData = ref([])

// 过滤和分页数据
const filteredData = ref([])

// 查看详情相关
const dialogVisible = ref(false)
const viewData = ref({})

// 查询日志列表
const getList = async () => {
  loading.value = true
  try {

    if (dateRange.value && dateRange.value.length === 2) {
      queryParams.startTime = dateRange.value[0]
      queryParams.endTime = dateRange.value[1]
    } else {
      queryParams.startTime = undefined
      queryParams.endTime = undefined
    }

    const res = await getOperateLog(queryParams)

    if (res.code === 200) {
      logData.value = res.content.records
      total.value = res.content.total
      filteredData.value = res.content.records
    } else {
      ElMessage.error(res.message || '获取日志列表失败')
    }
  } catch (error) {
    ElMessage.error('获取日志列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索按钮操作
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置按钮操作
const resetQuery = () => {
  dateRange.value = []
  Object.assign(queryParams, {
    pageNum: 1,
    pageSize: 10,
    operateName: '',
    startTime: '',
    endTime: ''
  })
  handleQuery()
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


// 查看按钮点击事件
const handleView = (row) => {
  viewData.value = { ...row }
  dialogVisible.value = true
}



onMounted(() => {
  getList()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

:deep(.el-descriptions__cell) {
  padding: 12px !important;
}

:deep(.el-descriptions__label) {
  width: 120px;
  background-color: #f5f7fa;
}
</style>
