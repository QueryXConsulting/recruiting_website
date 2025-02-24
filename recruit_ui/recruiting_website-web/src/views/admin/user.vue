<script setup lang="js">
import { ref, provide, reactive } from 'vue';
import {
} from '@element-plus/icons-vue'
import TopNav from '@/components/top.vue';
import Table from '@/components/admin/home/Table.vue';
import Drawer from '@/components/admin/home/Drawer.vue';
import DrawerMain from '@/components/admin/home/DrawerMain.vue';

const opened = ref(false);
// const userInfo = reactive({})
const userId = ref(null)
const handleUserInfo = (row) => {
  console.log('row', row);
  userId.value = row.userId;
  opened.value = !opened.value;
}


</script>

<template>
  <!-- 主体 -->
  <main>

    <Table @edit-click="handleUserInfo"></Table>
    <Drawer v-model="opened">
      <!-- 头部 -->
      <template #header>
        <div>
          用户信息
        </div>
      </template>
      <!-- 内容 -->
      <template #default>
        <DrawerMain :userId="userId"></DrawerMain>
        <!-- <DrawerMain v-model="userId"></DrawerMain> -->
      </template>
      <!-- 底部 -->
      <template #footer>
        476576
      </template>
    </Drawer>
  </main>
  <!-- <div class="content">
        <router-view></router-view>

    </div> -->

  <!-- 底部 -->
  <footer>

  </footer>
</template>

<style scoped lang="scss">
main {
  background: white;
  height: 100vh;
}
</style>
