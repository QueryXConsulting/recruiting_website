<!--
 * new page
 * @author: 深藏Blued蓝先生
 * @since: 2024-10-23
 * 404.vue
-->
<template>
    <el-result
      icon="warning"
      title="404提示"
      sub-title="你找的页面走丢了"
    >
      <template #extra>
        <el-button
          type="primary"
          @click="$router.push('/home')"
        >
          回到首页
        </el-button>
      </template>
    </el-result>
  </template>

  <script setup lang='ts'>

  </script>

  <style>
  </style>


