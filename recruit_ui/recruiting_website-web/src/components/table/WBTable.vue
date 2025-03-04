<script setup>
import { ref, defineEmits, defineProps, defineModel } from 'vue'

// model
const tableData = defineModel('table-data', { type: Array, default: [] });
const currentPage = defineModel('current-page', { type: Number, default: 1 });
const pageSize = defineModel('page-size', { type: Number, default: 10 });

// props
const props = defineProps({
    tableColumns: {
        type: Array,
        default: () => []
    },
    hasOperation: {
        type: Boolean,
        default: true
    },
    operationList: {
        type: Array,
        default: () => []
    },
    operationWidth: {
        type: String,
        default: 'auto'
    },
    pageSizes: {
        type: Array,
        default: () => [10, 20, 30, 40]
    },
    total: {
        type: Number,
        default: 0
    },
    layout: {
        type: String,
        default: 'total, sizes, prev, pager, next, jumper'
    }
})

// emits
const emits = defineEmits(['operationClick', 'update:page-size', 'update:current-page'])
// 表格右侧按钮点击事件
const handleClick = (index, row, $index) => {
    emits('operationClick', index, row, $index)
}
// 一页条数变化时触发
const handleSizeChange = (size) => {
    emits('update:page-size', size)
}
// 当前页码变化时触发
const handleCurrentChange = (page) => {
    emits('update:current-page', page)
}

// methods
const args = (prop, $index) => { return { prop, $index } }
const operationArgs = (row, $index) => { return { row, $index } }
</script>

<template>
    <div class="table-container">
        <slot name="header"></slot>
        <slot name="body">
            <div class="table-content">
                <el-table :data="tableData" v-bind="$attrs">
                    <!-- index：索引， prop：属性， label：标签  -->
                    <el-table-column v-for="(val, index) in tableColumns" :key="index" :prop="val.prop" :label="val.label"
                        align="center">
                        <template #default="scope">
                            <!-- $index：当前行索引 -->
                            <slot v-bind="args(val.prop, scope.$index)"> </slot>
                        </template>
                    </el-table-column>

                    <el-table-column v-if="props.hasOperation" :width="props.operationWidth" align="center" fixed="right"
                        label="操作">
                        <template #default="scope">
                            <slot name="operation" v-bind="operationArgs(scope.row, scope.$index)">
                                <!-- type：按钮类型， text：按钮文字 -->
                                <el-button v-for="(item, index) in props.operationList" :type="item.type" :key="index"
                                    size="small" @click="handleClick(index, scope.row, scope.$index)">
                                    <!-- index：按钮索引（从0开始）， row：当前行数据， $index：当前行索引 -->
                                    {{ item.text }}
                                </el-button>
                            </slot>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
        </slot>
        <slot name="footer">
            <div class="table-footer">
                <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="props.pageSizes"
                    :background="true" :total="props.total" @update:page-size="handleSizeChange"
                    @update:current-page="handleCurrentChange" :layout="props.layout" />
            </div>
        </slot>
    </div>
</template>

<style lang="scss" scoped>
.table-container {
    margin: 5px;
    background: #fff;
    padding: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.table-footer {
    margin-top: 5vh;
    display: flex;
    justify-content: center;
}
</style>