<template>
  <div class="home-container">

    <div class="main-content">

      <!-- 右侧内容区 -->
      <div class="content">
        <div class="welcome-section">
          <h2>欢迎回来，{{ getUserName() }}</h2>
          <p>{{ getCurrentDateTime() }}</p>
        </div>

        <!-- 数据概览卡片 -->
        <div class="stats-cards">
          <el-row :gutter="20">
            <el-col :span="6">
              <el-card shadow="hover" class="stats-card">
                <template #header>
                  <div class="card-header">
                    <span>今日访问量</span>
                    <el-icon><View /></el-icon>
                  </div>
                </template>
                <div class="card-content">
                  <div class="card-value">{{ stats.visits }}</div>
                  <div class="card-change">
                    <span :class="stats.visitsChange >= 0 ? 'positive' : 'negative'">
                      {{ stats.visitsChange >= 0 ? '+' : '' }}{{ stats.visitsChange }}%
                    </span>
                    较昨日
                  </div>
                </div>
              </el-card>
            </el-col>

            <el-col :span="6">
              <el-card shadow="hover" class="stats-card">
                <template #header>
                  <div class="card-header">
                    <span>{{ isAdmin ? '用户总数' : '简历投递' }}</span>
                    <el-icon><User /></el-icon>
                  </div>
                </template>
                <div class="card-content">
                  <div class="card-value">{{ stats.users }}</div>
                  <div class="card-change">
                    <span :class="stats.usersChange >= 0 ? 'positive' : 'negative'">
                      {{ stats.usersChange >= 0 ? '+' : '' }}{{ stats.usersChange }}%
                    </span>
                    较昨日
                  </div>
                </div>
              </el-card>
            </el-col>



          </el-row>
        </div>



        <!-- 图表区域 -->
        <div class="charts-section">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-card shadow="hover">
                <template #header>
                  <div class="card-header">
                    <span>{{ isAdmin ? '系统访问量趋势' : '简历处理情况' }}</span>
                  </div>
                </template>
                <div class="chart-container">
                  <!-- 在实际项目中，这里可以使用 ECharts 等图表组件 -->
                  <div class="chart-placeholder">图表区域</div>
                </div>
              </el-card>
            </el-col>

            <el-col :span="12">
              <el-card shadow="hover">
                <template #header>
                  <div class="card-header">
                    <span>{{ isAdmin ? '用户分布' : '职位申请情况' }}</span>
                  </div>
                </template>
                <div class="chart-container">
                  <!-- 在实际项目中，这里可以使用 ECharts 等图表组件 -->
                  <div class="chart-placeholder">图表区域</div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import userStore from '@/store/user';
import { ElMessageBox, ElMessage } from 'element-plus';
import { userLogout } from '@/api/company/companyApi';
import {
  House, User, SwitchButton, Setting, PieChart, DataBoard,
  OfficeBuilding, UserFilled, ChatDotRound, Tools,
  View, DataLine, Message
} from '@element-plus/icons-vue';

const router = useRouter();
const store = userStore();
const activeMenu = ref('/dashboard');
const defaultAvatar = '/public/default_user.png';

// 判断用户角色
const isAdmin = computed(() => {
  return store.role === '5'; // 根据实际项目的角色配置调整
});

const isCompany = computed(() => {
  return ['1', '2', '3', '4'].includes(store.role); // 根据实际项目的角色配置调整
});

// 模拟统计数据
const stats = ref({
  visits: 1024,
  visitsChange: 12.5,
  users: 256,
  usersChange: 8.3,
  data: 512,
  dataChange: -3.6,
  messages: 18
});

// 快捷操作项
const quickActions = computed(() => {
  const commonActions = [
    { title: '消息中心', icon: 'ChatDotRound', path: '/messages' },
    { title: '个人设置', icon: 'Setting', path: '/settings' }
  ];

  // 根据角色显示不同的快捷操作
  if (isAdmin.value) {
    return [
      { title: '用户管理', icon: 'User', path: '/admin/users' },
      { title: '系统设置', icon: 'Tools', path: '/admin/settings' },
      ...commonActions
    ];
  } else {
    return [
      { title: '发布职位', icon: 'Document', path: '/company/jobs/add' },
      { title: '简历筛选', icon: 'Files', path: '/candidates/resumes' },
      ...commonActions
    ];
  }
});

// 获取用户姓名
const getUserName = () => {
  if (isAdmin.value) {
    return store.userInfo.user?.adminName || '管理员';
  } else {
    return store.userInfo?.userName || '用户';
  }
};

// 获取当前日期时间
const getCurrentDateTime = () => {
  const now = new Date();
  const options = {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long',
    hour: '2-digit',
    minute: '2-digit'
  };
  return now.toLocaleDateString('zh-CN', options);
};

// 页面导航
const handleSelect = (index) => {
  router.push(index);
};

const navigateTo = (path) => {
  router.push(path);
};

// 下拉菜单命令处理
const handleCommand = (command) => {
  if (command === 'logout') {
    handleLogout();
  } else if (command === 'userInfo') {
    router.push('/home/userInfo');
  } else if (command === 'index') {
    router.push('/users/index');
  }
};

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userLogout().then(() => {
      store.$reset();
      router.push('/auth/login');
      ElMessage.success('已安全退出登录');
    });
  }).catch(() => {});
};

onMounted(() => {
  // 这里可以添加页面加载时需要获取的数据
  // 例如获取统计数据、用户信息等
});
</script>

<style scoped lang="scss">
.home-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.header {
  height: 60px;
  background-color: #424242;
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.logo {
  font-size: 20px;
  font-weight: bold;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #ffffff;

  .username {
    margin-left: 8px;
    font-size: 14px;
  }
}

.main-content {
  display: flex;
  padding-top: 60px;
  min-height: calc(100vh - 60px);
}

.sidebar {
  width: 220px;
  background-color: #424242;
  position: fixed;
  top: 60px;
  bottom: 0;
  overflow-y: auto;
  z-index: 900;
  box-shadow: 2px 0 6px rgba(0, 0, 0, 0.1);
}

.el-menu-vertical {
  border-right: none;
  width: 100%;
}

.content {
  flex: 1;
  margin-left: 0;
  padding: 20px;
}

.welcome-section {
  margin-bottom: 20px;

  h2 {
    margin-bottom: 8px;
    font-size: 24px;
    font-weight: 500;
    color: #333;
  }

  p {
    color: #666;
    margin: 0;
  }
}

.stats-cards {
  margin-bottom: 30px;
}

.stats-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 16px;
  }

  .card-content {
    text-align: center;
    padding: 10px 0;
  }

  .card-value {
    font-size: 28px;
    font-weight: bold;
    margin-bottom: 8px;
  }

  .card-change {
    font-size: 12px;
    color: #666;

    .positive {
      color: #67c23a;
    }

    .negative {
      color: #f56c6c;
    }
  }
}

.quick-actions {
  margin-bottom: 30px;

  h3 {
    margin-bottom: 16px;
    font-size: 18px;
    font-weight: 500;
    color: #333;
  }
}

.action-card {
  height: 100px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
  }

  .el-icon {
    margin-bottom: 10px;
    color: #409EFF;
  }

  .action-title {
    font-size: 14px;
  }
}

.charts-section {
  .chart-container {
    height: 300px;
  }

  .chart-placeholder {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    background-color: #f5f7fa;
    border-radius: 4px;
    color: #909399;
    font-size: 14px;
  }
}
</style>