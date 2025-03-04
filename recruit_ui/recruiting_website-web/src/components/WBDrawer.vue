<script lang="js" setup>
import { ref, defineEmits, defineExpose, defineModel, defineProps } from 'vue';

const drawer = defineModel();
const props = defineProps({});
const emits = defineEmits(['cancel', 'confirm']);

const handleCancel = () => {
    emits('cancel');
}
const handleConfirm = () => {
    emits('confirm');
}

const drawerRef = ref(null);
defineExpose(new Proxy({}, {
    get(_target, prop) {
        return drawerRef.value?.[prop]
    },
    // set(_target, prop, value) {
    //     drawerRef.value?.[prop] = value;
    // },
    has(_target, prop) {
        return prop in drawerRef.value
    }
}));
</script>

<template>
    <div class="drawer-container">
        <el-drawer v-model="drawer" v-bind="$attrs" ref="drawerRef">
            <!-- <template #header>
                <slot name="header"></slot>
            </template> -->
            <template v-for="(_, name) in $slots" #[name]="slotData">
                <slot :name="name" v-bind="slotData || {}"></slot>
            </template>
            <template #footer>
                <slot name="footer">
                    <div class="drawer-footer">
                        <el-button size="large" @click="handleCancel">取消</el-button>
                        <el-button size="large" type="danger" @click="handleConfirm">
                            确定
                        </el-button>
                    </div>
                </slot>
            </template>
        </el-drawer>
    </div>
</template>

<style lang="scss" scoped></style>