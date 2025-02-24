<template>
  <div class="home-container">
    <!-- 顶部导航栏 -->
    <nav class="nav-bar">
      <div class="logo">
        <img src="" alt="问呗" class="logo-img">
        <span class="divider">|</span>
        <span class="recruit-text">招聘</span>
      </div>
      <div class="nav-items">
        <a href="#" class="nav-item active">首页</a>
        <a href="#" class="nav-item">校园招聘</a>
        <a href="#" class="nav-item">社会招聘</a>
        <router-link to="/auth/login" class="login-btn" v-show="userStore().token == null">登录</router-link>
      </div>
    </nav>

    <!-- 主横幅区域 -->
    <div class="banner">
      <div class="banner-content">
        <div class="banner-buttons">
          <button class="primary-btn">校园招聘</button>
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
  </div>
</template>

<script setup>
import router from '@/router';
import userStore from '@/store/user';
import { ref } from 'vue'



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


</script>

<style scoped>
.home-container {
  width: 100%;
  min-height: 100vh;
}

.nav-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 4rem;
  background: rgba(255, 255, 255, 0.98);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
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
  color: #666;
}

.recruit-text {
  font-size: 16px;
  color: #333;
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
  color: #e60012;
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #e60012;
}

.login-btn {
  background: #e60012;
  color: white;
  padding: 0.5rem 1.5rem;
  border-radius: 4px;
  text-decoration: none;
  transition: background-color 0.3s;
}

.login-btn:hover {
  background: #cc0000;
}

.banner {
  height: 100vh;
  background: linear-gradient(45deg, #e60012, #ff4d4d);
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: white;
}

.culture-section {
  padding: 4rem 0;
  text-align: center;

  h2 {
    font-size: 30px;
    font-weight: bold;
  }
}

.culture-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 2rem;
  padding: 2rem 4rem;
}

.culture-item {
  background: #f5f5f5;
  padding: 2rem;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.culture-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
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
  background: #e60012;
  color: white;
  border: none;
  padding: 0.8rem 2rem;
  border-radius: 4px;
  margin: 0 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.primary-btn:hover {
  background: #cc0000;
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
</style>
