<script lang="js" setup>
import { ref, defineEmits, defineModel } from 'vue'

const drawer = defineModel()
</script>

<template>
    <div class="drawer-container">
        <el-drawer v-model="drawer" size="80%">
            <template #header>
                <slot name="header"></slot>
            </template>
            <template #default>
                <slot></slot>
            </template>
            <template #footer>
                <slot name="footer"></slot>
            </template>
        </el-drawer>
    </div>
</template>

<!-- <style lang="scss" scoped>

</style> -->
