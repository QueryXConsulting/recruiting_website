<template>
  <div class="home-container">
    <!-- 顶部导航栏 -->
    <nav class="nav-bar">
      <div class="logo">
        <img src="/public/logo.png" alt="问呗" class="logo-img">
        <span class="divider">|</span>
        <span class="recruit-text">招聘</span>
      </div>
      <div class="nav-items">
        <a href="#" class="nav-item active">首页</a>
        <a href="/users/search" class="nav-item">校园招聘</a>
        <a href="#" class="nav-item">社会招聘</a>
        <a href="/users/application" class="nav-item" v-if="userStore().token">应聘历史</a>
        <a href="/users/message" class="nav-item" v-if="userStore().token" style="padding-top: 15px;" alt="留言板">
          <el-icon>
            <component :is="Bell"></component>
          </el-icon>
          <!-- 未读消息提示(小圆点) -->
          <i v-if="hasMessage" class="message-flag"></i>
        </a>
        <!-- 用户头像 -->
        <el-dropdown v-if="userStore().token" @command="handleCommand">
          <span class="user-dropdown">
            <el-avatar size="large" :src="userStore().userInfo.userAvatar" @error="() => { }">
              <!-- 头像加载失败时显示默认头像 -->
              <img src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" alt="用户头像">
            </el-avatar>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="userInfo">
                <el-icon>
                  <component :is="iconMapping['user']" />
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
        <router-link to="/auth/login" class="login-btn" v-else-if="userStore().token == null">登录</router-link>
      </div>
    </nav>

      <!-- 主横幅区域 -->
      <div class="banner">
      <div class="banner-content">
        <!-- <div class="banner-image"></div> -->
        <div class="banner-buttons">
          <button class="primary-btn" @click="router.push('/users/search')">校园招聘</button>
          <button class="primary-btn" @click="router.push('/users/registerCompany')">企业入驻</button>
        </div>
      </div>
    </div>

    <!-- 文化区域 -->
    <section class="culture-section">
      <h2>文化</h2>
      <div class="culture-grid">
        <div class="culture-item" v-for="(item, index) in cultureItems" :key="index">
          <div class="icon-placeholder">{{ item.title[0] }}</div>
          <h3>{{ item.title }}</h3>
          <p>{{ item.subtitle }}</p>
        </div>
      </div>
    </section>



    <!-- 公司优势区域 -->
    <section class="advantages-section">
      <h2>公司优势</h2>
      <div class="advantages-grid">
        <div class="advantage-item" v-for="(item, index) in advantageItems" :key="index">
          <div class="advantage-icon">
            <el-icon size="32px">
              <component :is="item.icon" />
            </el-icon>
          </div>
          <h3>{{ item.title }}</h3>
          <p>{{ item.description }}</p>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import router from '@/router';
import userStore from '@/store/user';
import { ref } from 'vue';
import { Bell, Medal, Opportunity, Place, School } from '@element-plus/icons-vue';
import { iconMapping } from '@/utils/iconList';
import { ElMessage, ElMessageBox } from 'element-plus';
import { userLogout } from '@/api/company/companyApi';


const hasMessage = ref(false); // 是否有未读消息


const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userLogout().then(() => {
        userStore().$reset();
        window.location.reload();
        // 跳转到登录页
        router.push('/auth/login');
        ElMessage.success('已安全退出登录');
      })
    }).catch(() => { })
  } else if (command === 'userInfo') {
    router.push('/users/userInfo');
  }
}

const cultureItems = ref([
  {
    icon: '',
    title: '拼搏奉献',
    subtitle: '团队文化'
  },
  {
    icon: '',
    title: '因你而变',
    subtitle: '服务文化'
  },
  {
    icon: '',
    title: '敢为天下先',
    subtitle: '创新文化'
  },
  {
    icon: '',
    title: '稳健审慎',
    subtitle: '风险文化'
  },


])

const advantageItems = ref([
  {
    icon: Opportunity,
    title: '发展空间',
    description: '提供广阔的职业发展平台和晋升机会'
  },
  {
    icon: Medal,
    title: '具有竞争力的薪资',
    description: '提供行业内具有竞争力的薪资待遇'
  },
  {
    icon: School,
    title: '培训体系',
    description: '完善的培训体系，助力个人成长'
  },
  {
    icon: Place,
    title: '工作环境',
    description: '舒适的办公环境，良好的团队氛围'
  }
]);


</script>

<style lang="scss" scoped>
.home-container {
  width: 100%;
  min-height: 100vh;
}

.nav-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 4rem;
  background: rgba(255, 255, 255, 0.95);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  backdrop-filter: blur(8px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.logo {
  display: flex;
  align-items: center;
}

.logo-img {
  height: 32px;
  width: auto;
}

.divider {
  margin: 0 10px;
  color: #FF7427;
}

.recruit-text {
  font-size: 16px;
  color: #FF7427;
}

.nav-items {
  display: flex;
  align-items: center;
  gap: 2rem;
}

.nav-item {
  color: #333;
  text-decoration: none;
  font-size: 16px;
  padding: 0.5rem 0;
  position: relative;
}

.nav-item:hover,
.nav-item.active {
  color: #FF7427;
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #FF7427;
}

.message-flag {
  position: absolute;
  width: 5px;
  height: 5px;
  right: 1%;
  background: #FF7427;
  border-radius: 50%;
}

.login-btn {
  background: #FF7427;
  color: white;
  padding: 0.5rem 1.5rem;
  border-radius: 4px;
  text-decoration: none;
  transition: background-color 0.3s;
}

.login-btn:hover {
  background: #FFAA66;
}


.culture-section {
  padding: 6rem 0;
  text-align: center;
  background: rgba(255, 255, 255, 0.98);

  h2 {
    font-size: 32px;
    font-weight: bold;
    margin-bottom: 3rem;
    color: #333;
  }
}

.culture-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 2rem;
  padding: 2rem 4rem;
}

.culture-item {
  background: rgba(245, 245, 245, 0.9);
  padding: 2.5rem;
  border-radius: 12px;
  transition: all 0.3s ease;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.culture-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.stories-section {
  padding: 4rem 0;
  text-align: center;
}

.stories-slider {
  position: relative;
  padding: 0 4rem;
}

.story-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.primary-btn {
  background: rgba(255, 116, 39, 0.9);
  backdrop-filter: blur(4px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  padding: 1rem 2.5rem;
  font-size: 1.1rem;
  color: white;
  border-radius: 4px;
  margin: 0 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.primary-btn:hover {
  background: rgba(255, 170, 102, 0.95);
  transform: translateY(-2px);
}

.icon-placeholder {
  width: 64px;
  height: 64px;
  background: #e6e6e6;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1rem;
  font-size: 24px;
  color: #666;
}

.image-placeholder {
  width: 100%;
  height: 200px;
  background: #e6e6e6;
  margin-bottom: 1rem;
}

.avatar-placeholder {
  width: 40px;
  height: 40px;
  background: #e6e6e6;
  border-radius: 50%;
  display: inline-block;
  vertical-align: middle;
  margin-right: 8px;
}

.banner {
  height: 80vh;
  position: relative;
  display: flex;
  align-items: center;
  background-image: url('/public/index.png');
  background-position: top;
  background-size: cover;
  background-repeat: no-repeat;
  justify-content: center;
  text-align: center;
  color: white;


}

.banner-content {
  position: relative;
  z-index: 2;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.banner-buttons {
  margin-top: 120px;
  margin-right: 160px;
  /* position: relative;
  z-index: 2;
  margin-top: 20px; */
}


.advantages-section {
  padding: 6rem 0;
  background-color: rgba(248, 249, 250, 0.97);
  text-align: center;

  h2 {
    font-size: 32px;
    font-weight: bold;
    margin-bottom: 3rem;
    color: #333;
  }
}

.advantages-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 2rem;
  padding: 2rem 4rem;
  max-width: 1200px;
  margin: 0 auto;
}

.advantage-item {
  background: rgba(255, 255, 255, 0.95);
  padding: 2.5rem;
  border-radius: 12px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 28px rgba(0, 0, 0, 0.15);
  }

  h3 {
    margin: 1rem 0;
    color: #333;
  }

  p {
    color: #666;
    line-height: 1.6;
  }
}

.advantage-icon {
  width: 72px;
  height: 72px;
  margin: 0 auto 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 245, 240, 0.9);
  border-radius: 50%;
  color: #FF7427;
}


</style>
