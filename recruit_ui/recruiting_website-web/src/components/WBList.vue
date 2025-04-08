<script setup>

// model
const page = defineModel('page', { type: Number, default: 1 });
const size = defineModel('size', { type: Number, default: 10 });

// prop
const props = defineProps({
    list: { type: Array, default: () => [] },
    total: { type: Number, default: 0 },
    pageSizes: { type: Array, default: () => [10, 20, 30, 40, 50] },
    layout: { type: String, default: 'total, sizes, prev, pager, next, jumper, ->' },
})

// emit
const emits = defineEmits(['update:page-size', 'update:current-page', 'click']);
const handleCurrentChange = (val) => {
    emits('update:current-page', val);
}
const handleSizeChange = (val) => {
    emits('update:page-size', val);
}
const handleClick = (item) => {
    emits('click', item);
}
</script>

<template>
    <div>
        <!-- 列表 -->
        <ul class="list">
            <li @click="handleClick(item)" v-for="(item, index) in props.list" :key="index" class="item">
                    <slot name="item-prepend" v-bind="item"></slot>
                <!-- </div> -->
                <div class="item-left">
                    <slot v-bind="item"></slot>
                </div>
                <div class="item-right" v-bind="item">
                    <slot name="item-right" v-bind="item"></slot>
                </div>
            </li>
        </ul>

        <!-- 分页 -->
        <el-row justify="center">
            <el-pagination v-model:current-page="page" v-model:page-size="size" :page-sizes="props.pageSizes"
                :total="props.total" @update:page-size="handleSizeChange" @update:current-page="handleCurrentChange"
                :layout="props.layout" v-bind="$attrs"/>
        </el-row>
    </div>
</template>

<style lang="scss" scoped>
.list {
    margin: 10px;
}

.item {
    padding: 10px;
    margin: 0px auto 10px;
    border-radius: 20px;
    background: #fff;
    display: flex;
    justify-content: space-between;
    cursor: pointer;
}

.item-left {
    flex-grow: 1;
    padding: 10px;
}

.item-right {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}
</style>