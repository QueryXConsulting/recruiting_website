<template>
  <div class="layout-container">
    <CardLogin v-if="store.userInfo.isFirstLogin == 0" class="centered-card" />
    <!-- 添加顶部栏 -->
    <div class="header">
      <div class="header-right">
        <el-dropdown @command="handleCommand">
          <span class="user-dropdown">
            <el-avatar :size="32" :src="store.userInfo.userAvatar || defaultAvatar" />
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="userInfo" v-if="store.role != null && store.role != 5">
                <el-icon>
                  <component :is="iconMapping[
                    'user']" />
                </el-icon>
                个人信息
              </el-dropdown-item>
              <el-dropdown-item command="logout" divided>
                <el-icon>
                  <component :is="iconMapping['switch']" />
                </el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <div class="menu">
      <el-menu :default-active="activeMenu" class="el-menu-vertical" background-color="#304156" text-color="#bfcbd9"
        active-text-color="#409EFF" :router="false" @select="handleSelect">
        <template v-for="menu in menus" :key="menu.menuId">
          <!-- 一级菜单 -->
          <el-sub-menu v-if="menu.children && menu.children.length" :index="'/' + menu.path"
            v-show="menu.visible === '0'">
            <template #title>
              <el-icon v-if="menu.icon !== '#'">
                <component :is="iconMapping[menu.icon]" />
              </el-icon>
              <span>{{ menu.menuName }}</span>
            </template>

            <!-- 二级菜单 -->
            <template v-for="subMenu in menu.children" :key="subMenu.menuId">
              <el-sub-menu v-if="subMenu.children && subMenu.children.length" :index="'/' + subMenu.path"
                v-show="subMenu.visible == '0'">
                <template #title>
                  <el-icon v-if="subMenu.icon !== '#'">
                    <component :is="iconMapping[subMenu.icon]" />
                  </el-icon>
                  <span>{{ subMenu.menuName }}</span>
                </template>

                <!-- 三级菜单 -->
                <el-menu-item v-for="item in subMenu.children" :key="item.menuId" :index="'/' + item.path"
                  v-show="item.visible == '0'">
                  <el-icon v-if="item.icon !== '#'">
                    <component :is="iconMapping[item.icon]" />
                  </el-icon>
                  <span>{{ item.menuName }}</span>
                </el-menu-item>
              </el-sub-menu>

              <!-- 没有子菜单的二级菜单 -->
              <el-menu-item v-else :index="'/' + subMenu.path" v-show="subMenu.visible == '0'">
                <el-icon v-if="subMenu.icon !== '#'">
                  <component :is="iconMapping[subMenu.icon]" />
                </el-icon>
                <span>{{ subMenu.menuName }}</span>
              </el-menu-item>
            </template>
          </el-sub-menu>

          <!-- 没有子菜单的一级菜单 -->
          <el-menu-item v-else :index="'/' + menu.path" v-show="menu.visible == '0'">
            <el-icon v-if="menu.icon !== '#'">
              <component :is="iconMapping[menu.icon]" />
            </el-icon>
            <span>{{ menu.menuName }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </div>

    <div class="content">
      <RouterView></RouterView>
    </div>
  </div>
</template>

<script setup lang="js">
import { onMounted, ref, watch } from 'vue'
import userStore from '@/store/user';
import { RouterView, useRouter, useRoute } from 'vue-router'
import { iconMapping } from '@/utils/iconList';
import { ElMessageBox, ElMessage } from 'element-plus';
import { userLogout } from '@/api/company/companyApi';
import CardLogin from '@/components/company/cardLogin.vue';

const router = useRouter()
const route = useRoute()


const store = userStore()
let menus = ref();
onMounted(() => {
  menus.value = filterMenus(store.menuTree);
})

watch(() => store.menuTree, (newVal) => {
  menus.value = filterMenus(newVal);
}, { deep: true })

const activeMenu = ref(route.path)

watch(() => route.path, (newPath) => {
  console.log(newPath)
  activeMenu.value = newPath
})

const handleSelect = (index) => {
  router.push(index)
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userLogout().then(() => {
      store.$reset()
      window.location.reload()
      // 跳转到登录页
      router.push('/auth/login')
      ElMessage.success('已安全退出登录')
    })
  }).catch(() => { })
}

const filterMenus = (menus) => {

  if (store.role !== null) {
    return menus;
  }

  return menus.map(menu => {
    const filteredMenu = { ...menu };

    if (filteredMenu.children && filteredMenu.children.length) {
      filteredMenu.children = filteredMenu.children.filter(child => child.menuType !== 'U');
      filteredMenu.children = filterMenus(filteredMenu.children);
    }

    return filteredMenu;
  });
}

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const handleCommand = (command) => {
  if (command === 'logout') {
    handleLogout()
  } else if (command === 'userInfo') {
    router.push('/home/userInfo')
  }
}

</script>

<style scoped lang="scss">
.layout-container {
  user-select: none;
  display: flex;
  min-height: 100vh;
}

.header {
  position: fixed;
  top: 0;
  right: 0;
  height: 50px;
  padding: 0 20px;
  background: white;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  z-index: 100;
  width: calc(100% - 200px);
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #333;

  .username {
    margin-left: 8px;
    font-size: 14px;
  }
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
  margin-top: 30px;
  padding: 20px;
  background-color: #f0f2f5;
  min-height: 100vh;
  box-sizing: border-box;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;

  .el-icon {
    margin-right: 8px;
  }
}

.centered-card {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%; // 确保 CardLogin 在父容器中垂直居中
}
</style>
