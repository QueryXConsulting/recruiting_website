<script setup>
import { ref, computed, defineModel, defineProps, defineExpose } from 'vue';

// model
const formData = defineModel({ type: Object, default: {} });

// props
const props = defineProps({
    items: { type: Object, default: () => { } },
});

// computed


// methods
const args = (key, value) => { return { key, value }; }

// expose
const formRef = ref(null);
// 暴露子组件示例，给父组件
defineExpose(new Proxy({}, {
    get(_target, prop) {
        return formRef.value?.[prop]
    },
    // set(_target, prop, value) {
    //     formRef.value?.[prop] = value;
    // },
    has(_target, prop) {
        return prop in formRef.value
    }
}));
</script>

<template>
    <div>
        <el-form :model="formData" ref="formRef" v-bind="$attrs">
            <template #default="scope">
                <slot name="form-items" v-bind="scope">
                    <!-- index: 属性名，val: 显示名称（对应中文名） -->
                    <el-form-item v-for="(val, index) in props.items" :key="index" :label="val + '：'" label-width="100px"
                        size="large" :prop="index">
                        <slot v-bind="args(index, val)"></slot>
                    </el-form-item>
                </slot>
            </template>

            <!-- <template v-for="(_, name) in $slots" #[name]="slotData">
                <slot :name="name" v-bind="slotData || {}">
                </slot>

            </template> -->
        </el-form>
    </div>
</template>

<style lang="scss" scoped></style>
