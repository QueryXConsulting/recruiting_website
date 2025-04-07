<script setup>
import { Search } from '@element-plus/icons-vue';

// model
const inputModel = defineModel('input', { type: String, default: '' });
const selectModel = defineModel('select', { type: String, default: '' });
// 事件
const emits = defineEmits(['clickAdd', 'clickSearch', 'updateStatus']);
const handleClick = () => { emits('clickAdd'); }
const handleSearch = () => { emits('clickSearch'); }
const updateStatus = () => { emits('updateStatus'); }

// props
const props = defineProps({
    addButtonText: {
        type: String,
        default: '+ 新增用户'
    },
    searchButtonText: {
        type: String,
        default: '搜索'
    },
    placeholder: {
        type: String,
        default: '请输入关键字搜索'
    },
    screenText: {
        type: String,
        default: '启用状态'
    },
    options: {
        type: Array,
        default: () => []
    }
});

//

</script>

<template>
    <slot name="body">
        <div class="searchLab">
            <slot name="add">
                <el-button type="primary" size="large" @click="handleClick">{{ props.addButtonText }}</el-button>
            </slot>
            <slot name="search">
                <!-- 搜索部分 -->
                <div class="search">
                    <el-input @keyup.enter="handleSearch" class="search-input" clearable v-model="inputModel" size="large"
                        type="search" :placeholder="props.placeholder" />
                    <el-button @click="handleSearch" type="primary" size="large">
                        <el-icon style="vertical-align: middle">
                            <Search />
                        </el-icon>
                        <span style="vertical-align: middle"> {{ props.searchButtonText }} </span>
                    </el-button>
                </div>
            </slot>

            <!-- 筛选部分 -->
            <slot name="screen">
                <div class="screen">
                    <span>{{ props.screenText }}：</span>
                    <el-select @change="updateStatus" v-model="selectModel" value-key="value"
                        :placeholder="props.screenText" size="large" style="width: 140px;">
                        <!-- value：选项值，label：选项显示值 -->
                        <el-option v-for="item in props.options" :key="item.value" :label="item.label"
                            :value="item.value" />
                    </el-select>
                </div>

            </slot>

            <slot></slot>
        </div>
    </slot>
</template>

<style lang="scss" scoped>
.searchLab {
    display: flex;
    align-content: center;

    .search {
        position: relative;
        display: flex;
        margin: 0 1vw;
        padding-bottom: 1.5vh;

        .search-input {
            margin-right: .5vw;
            width: 80%;
        }
    }

    .screen {
        span {
            color: #7f8184;
        }

    }
}
</style>