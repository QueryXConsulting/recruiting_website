<script setup>
import { ref, defineProps, defineModel, defineEmits } from 'vue'

const isShowDialog = defineModel({ default: false, type: Boolean });
const props = defineProps({
    confirmText: { type: String, default: '确定' },
    cancelText: { type: String, default: '取消' }
})

const emit = defineEmits(['submit', 'cancel'])


// 取消按钮点击事件
const handleCancel = () => {
    emit('cancel')
}

// 确定按钮点击事件
const handleSubmit = () => {
    emit('submit')
}
</script>

<template>
    <el-dialog v-model="isShowDialog" v-bind="$attrs">
        <template #default>
            <slot></slot>
        </template>

        <template #footer>
            <slot name="footer">
                <div class="dialog-footer">
                    <el-button size="large" @click="handleCancel">{{ props.cancelText }}</el-button>
                    <el-button size="large" type="primary" @click="handleSubmit">{{ props.confirmText }}</el-button>
                </div>
            </slot>

        </template>
    </el-dialog>
</template>

<style scoped></style>
