<template>
    <el-menu @select="handleSelect" :default-openeds="[1]" :default-active="routerIndex" class="menu-vertical"
        :collapse="isCollapse">
        <el-sub-menu index="1">
            <template #title>
                <el-icon>
                    <User />
                </el-icon>
                <span>用户管理</span>
            </template>
            <el-menu-item-group>
                <el-menu-item index="1-1">用户信息</el-menu-item>
                <el-menu-item index="1-2">简历信息</el-menu-item>
            </el-menu-item-group>
        </el-sub-menu>
        <!-- 折叠按钮 -->
        <button type="plain" class="collapse-btn" @click="handleChange">
            <el-icon size="20">
                <!-- 动态切换折叠状态 -->
                <component :is="isCollapse ? Expand : Fold"></component>
            </el-icon>
        </button>
    </el-menu>
</template>

<script lang="js" setup>
import { ref } from 'vue'
import {
    User,
    Fold,
    Expand
} from '@element-plus/icons-vue'
import router from '@/router/index';

// 侧边导航栏折叠状态
const isCollapse = ref(false)
const handleChange = () => { isCollapse.value = !isCollapse.value }
const handleSelect = (index) => {
    console.log('点击了导航栏，index=', index);
    switch (index) {
        case '1-1':
            router.push('/');
            break;
        case '1-2':
            router.push('/resume');
            break;
        default:
            break;
    }
}

</script>

<style lang="scss" scoped>
.menu-vertical {
    height: 100%;
    min-height: 400px;
    position: relative;
    // --el-menu-vertical-height: 100%;
}

.collapse-btn {
    position: absolute;
    bottom: 1vh;
    left: .3vw;
    width: 4vw;
    height: 4vw;
    border: none;
}
</style>
