<script setup>
import { ref, onMounted, nextTick } from 'vue'
import userStore from '@/store/user';
import { Bell } from '@element-plus/icons-vue';
import { iconMapping } from '@/utils/iconList';
import { ElMessageBox } from 'element-plus';
import { userLogout } from '@/api/company/companyApi';
import { Search } from '@element-plus/icons-vue'
import CompanyList from './list/CompanyList.vue'
import JobList from '@/views/user/list/JobList.vue'
import { jobNature } from '@/api/user/UserApi'
import { SearchCondition, useSearchStore } from '@/store/searchStore'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router';

const router = useRouter();
const inputVal = ref(null); // 搜索关键字
const searchType = ref(useSearchStore().getType); // 搜索类型
const searchObj = useSearchStore().getConditions(searchType.value);
const isAsc = ref(searchObj.isAsc); // 排序方式
const natures = ref([]); // 工作性质列表
// 节流函数
function simpleThrottle(func, wait) {
  let timeout = null;
  return function (...args) {
    if (!timeout) {
      func.apply(this, args);
      timeout = setTimeout(() => {
        timeout = null;
      }, wait);
    }
  };
}


// 页面数据初始化
onMounted(async () => {
  isAsc.value = searchObj.isAsc;
  inputVal.value = searchObj.keyword;
  natures.value = await jobNature().then((r) => r.content);
  await nextTick();
  // 调整布局
  // const nav = document.querySelector('.nav-bar');
  // const searchBox = document.querySelector('.search-container');
  // searchBox.style.marginTop = `${nav.offsetHeight + 10}px`;
});

const condition = ref(useSearchStore().getConditions(searchType.value));
// 搜索
const handleSearch = simpleThrottle(async () => {
  // const handleSearch = async () => {
  const regex = /[\u3000-\u303F|\uFF00-\uFFEF|~<>～]+/g;
  if (regex.test(inputVal.value)) {
    // 输入符合要求，继续执行搜索逻辑
    ElMessage.error('搜索关键字只能包含中文汉字、英文和数字。') // 这里可以添加你的搜索逻辑
    return;
  }
  const o = new SearchCondition(inputVal.value, searchObj.page, searchObj.size, isAsc.value);
  if (searchType.value === 'JOB') {
    if (searchObj.nature === 'null') {
      o.nature = null;
    } else {
      o.nature = searchObj.nature;
    }
    o.education = searchObj.education;
    if (searchObj.area === '不限') {
      o.area = null;
    } else {
      o.area = searchObj.area;
    }
    o.salary = searchObj.salary;
  }
  condition.value = o;
  // 保存搜索条件
  useSearchStore().setConditions(searchType.value, o);
  useSearchStore().setType(searchType.value);
}, 1000);
// }

// 城市筛选选项
const cities = [
  '不限', '北京', '上海', '广州', '深圳', '杭州',
  '南京', '武汉', '成都', '重庆', '西安',
  '天津', '苏州', '长沙', '郑州', '青岛',
  '厦门', '大连', '宁波', '济南', '合肥',
  '昆明', '南宁', '福州', '贵阳', '哈尔滨',
  '石家庄', '太原', '南昌', '长春', '兰州',
  '海口', '乌鲁木齐', '西宁', '银川', '呼和浩特',
  '拉萨', '沈阳', '成都', '无锡', '佛山',
  '东莞', '珠海', '中山', '惠州', '南通'
];


// 翻页
const handleCurrentChange = (currentPage, type) => {
  searchObj.page = currentPage;
}

// 切换每页显示条数
const handleSizeChange = (pageSize, type) => {
  searchObj.size = pageSize;
}

// 
const companyName = ref(null);
const handleCompanyJobs = (name) => {
  companyName.value = `正在查看 ${name} 发布的职位`;
}

// 关闭查看公司发布职位列表
const handleCloseCompanyJobs = () => {
  companyName.value = null;
  searchObj.companyId = null;
  // 重置搜索条件
  useSearchStore().resetCompanyId();
  // handleSearch();
}

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
  } else if (command === 'index') {
    router.push('/home');
  }
}

</script>

<template>
  <div>
    <!-- 顶部导航栏 -->
    <nav class="nav-bar">
      <div class="logo">
        <!-- <img src="/public/logo.png" alt="问呗" class="logo-img">
        <span class="divider">|</span>
        <span class="recruit-text">招聘</span> -->
      </div>
      <div class="nav-items">
        <a href="/users/index" class="nav-item">首页</a>
        <a href="/users/search" class="nav-item active">校园招聘</a>
        <a href="#" class="nav-item">社会招聘</a>
        <a href="/users/application" class="nav-item" v-if="userStore().token">投递历史</a>
        <a href="/users/message" class="nav-item" v-if="userStore().token" style="padding-top: 15px;" alt="留言板">
          <el-icon>
            <component :is="Bell"></component>
          </el-icon>
        </a>
        <!-- 用户头像 -->
        <el-dropdown v-if="userStore().token" @command="handleCommand">
          <span class="user-dropdown">
            <el-avatar size="large" :src="userStore().userInfo.userAvatar" @error="() => { }">
              <!-- 头像加载失败时显示默认头像 -->
              <img src="/public/default_user.png" alt="用户头像">
            </el-avatar>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="index" v-if="userStore().role != '5' && userStore().role != null">
                <el-icon>
                  <component :is="iconMapping['house']" />
                </el-icon>
                公司端
              </el-dropdown-item>

              <el-dropdown-item command="userInfo" v-if="userStore().role == '5'">
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

    <section class="search-container">
      <p class="search-main">
        <el-input v-model="inputVal" @keyup.enter="handleSearch" maxlength="20" type="search" clearable
          class="search-input" :prefix-icon="Search" placeholder="搜索岗位、公司">
          <template #prepend>
            <el-select v-model="searchType" placeholder="类型" size="large" style="width: 80px;">
              <el-option label="岗位" value="JOB" />
              <el-option label="公司" value="COMPANY" />
            </el-select>
          </template>
          <template #append>
            <el-button @click="handleSearch">搜&emsp;索</el-button>
          </template>
        </el-input>
      </p>

      <!-- 其他条件 -->
      <p class="search-other">
        <span class="search-other-filter">
          <!-- 排序方式 -->
          <el-select v-model="isAsc" placeholder="排序方式" style="width: 100px;">
            <el-option label="倒序" :value="false" />
            <el-option label="正序" :value="true" />
          </el-select>
          <!-- 学历要求 -->
          <el-select v-if="searchType === 'JOB'" v-model="searchObj.education" placeholder="学历要求" style="width: 100px;">
            <el-option label="不限" value="" />
            <el-option label="博士" value="博士" />
            <el-option label="硕士" value="硕士" />
            <el-option label="本科" value="本科" />
            <el-option label="大专" value="大专" />
            <el-option label="大学以下" value="大学以下" />
          </el-select>
          <!-- 工作性质 -->
          <el-select v-if="searchType === 'JOB'" v-model="searchObj.nature" placeholder="工作性质" style="width: 100px;">
            <el-option label="不限" value="null"></el-option>
            <el-option v-for="nature in natures" :label="nature.natureName" :value="nature.natureName"
              :key="nature.natureId" />
          </el-select>
          <!-- 工作地点 -->
          <el-select v-if="searchType === 'JOB'" v-model="searchObj.area" placeholder="工作地点" style="width: 100px;">
            <el-option v-for="city in cities" :label="city" :value="city" :key="city" />
          </el-select>
          <!-- 薪资范围 -->
          <el-select v-if="searchType === 'JOB'" v-model="searchObj.salary" placeholder="薪资范围" style="width: 100px;">
            <el-option label="1k-2k" value="1k-2k"></el-option>
            <el-option label="3k-5k" value="3k-5k"></el-option>
            <el-option label="5k-8k" value="5k-8k"></el-option>
            <el-option label="8k-10k" value="8k-10k"></el-option>
            <el-option label="10k-15k" value="10k-15k"></el-option>
            <el-option label="15k-20k" value="15k-20k"></el-option>
            <el-option label="20k-30k" value="20k-30k"></el-option>
            <el-option label="30k以上" value="30k+"></el-option>
            <el-option label="面议" value="面议"></el-option>
          </el-select>
        </span>

      </p>
      <p v-if="companyName" style="margin-top: 5px;width: 50%;">
        <el-alert :title="companyName" type="info" show-icon @close="handleCloseCompanyJobs" />
      </p>
    </section>
    <!-- 列表 -->
    <main style="padding: 0px 200px;">
      <JobList v-if="searchType === 'JOB'" @update:page-size="handleSizeChange($event, searchType)"
        @click-company="handleCompanyJobs" @update:current-page="handleCurrentChange($event, searchType)"
        :condition="condition"></JobList>
      <CompanyList v-else-if="searchType === 'COMPANY'" @update:page-size="handleSizeChange($event, searchType)"
        @update:current-page="handleCurrentChange($event, searchType)" @click-company="handleCompanyJobs"
        :condition="condition"></CompanyList>
    </main>
  </div>
</template>

<style lang="scss" scoped>
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

.login-btn {
  background: #FF7427;
  color: white;
  padding: 0.5rem 1.5rem;
  border-radius: 4px;
  text-decoration: none;
  transition: background-color 0.3s;
}

.login-btn:hover {
  background: #FF7427;
}

.search-container {
  display: flex;
  margin: 90px 10px 10px 10px;
  border-radius: 15px;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px 10px 20px;
  background: #fff;

  .search-main {
    width: 80%;
    margin: 10px;

    .search-input {

      &:hover {
        transition: all 0.3s ease-in-out;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
      }
    }
  }


  .search-other {
    display: flex;
    justify-content: start;
  }
}
</style>