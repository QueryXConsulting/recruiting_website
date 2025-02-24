<template>
  <div class="company-container">

    <div class="menu">
      <el-menu :default-active="activeMenu" class="el-menu-vertical" background-color="#304156" text-color="#bfcbd9"
        active-text-color="#409EFF" @select="handleSelect">
        <el-sub-menu index="1">
          <template #title>
            <el-icon>
              <OfficeBuilding />
            </el-icon>
            <span>企业管理</span>
          </template>
          <el-menu-item index="/company/info">
            <el-icon>
              <InfoFilled />
            </el-icon>
            <span>企业信息管理</span>
          </el-menu-item>
          <el-menu-item index="/company/jobs">
            <el-icon>
              <Briefcase />
            </el-icon>
            <span>公司工作管理</span>
          </el-menu-item>
          <el-menu-item index="/company/userCompany">
            <el-icon>
              <User />
            </el-icon>
            <span>员工账号管理</span>
          </el-menu-item>
          <el-menu-item index="/company/website">
            <el-icon>
              <Monitor />
            </el-icon>
            <span>官网管理</span>
          </el-menu-item>
          <el-menu-item index="/company/messageBoard">
            <el-icon>
              <ChatDotRound />
            </el-icon>
            <span>留言板</span>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </div>

    <div class="content">
      <RouterView></RouterView>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'

import { OfficeBuilding, InfoFilled, Briefcase, Monitor, ChatDotRound, User } from '@element-plus/icons-vue'


const router = useRouter()
const route = useRoute()

const activeMenu = ref(route.path)




const handleSelect = (index) => {
  router.push(index)
}
</script>

<style scoped lang="scss">
.company-container {
  user-select: none;
  display: flex;
  min-height: 100vh;
}

.menu {
  width: 200px;
  background-color: #304156;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  overflow-y: auto;
}

.el-menu-vertical {
  border-right: none;
}

.content {
  flex: 1;
  margin-left: 200px;
  padding: 10px;
  background-color: #f0f2f5;
  min-height: 80vh;
  box-sizing: border-box;
}
</style>
