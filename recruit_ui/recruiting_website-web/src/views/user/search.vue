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


const inputVal = ref(null); // 搜索关键字
const searchType = ref(useSearchStore().getType); // 搜索类型
const searchObj = useSearchStore().getConditions(searchType.value);
const isAsc = ref(searchObj.isAsc); // 排序方式
const natures = ref([]); // 工作性质列表
// 节流函数
function simpleThrottle(func, wait) {
    let timeout = null;
    return function (...args) {
        ElMessage('请不用频繁操作，10秒后再尝试。');
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
    const nav = document.querySelector('.nav-bar');
    const searchBox = document.querySelector('.search-container');
    searchBox.style.marginTop = `${nav.offsetHeight + 10}px`;
});

const condition = ref(useSearchStore().getConditions(searchType.value));
// 搜索
const handleSearch = simpleThrottle(async () => {
    const regex = /[\u3000-\u303F|\uFF00-\uFFEF|~<>～]+/g;
    if (regex.test(inputVal.value)) {
        // 输入符合要求，继续执行搜索逻辑           
        ElMessage.error('搜索关键字只能包含中文汉字、英文') // 这里可以添加你的搜索逻辑 
        return;
    }
    const o = new SearchCondition(inputVal.value, searchObj.page, searchObj.size, isAsc.value, searchObj.education, searchObj.nature);
    condition.value = o;
    // 保存搜索条件
    useSearchStore().setConditions(searchType.value, o);
    useSearchStore().setType(searchType.value);
}, 10000)

// 翻页
const handleCurrentChange = (currentPage, type) => {
    searchObj.page = currentPage;
}

// 切换每页显示条数
const handleSizeChange = (pageSize, type) => {
    searchObj.size = pageSize;
}



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

</script>

<template>
    <div>
        <!-- 顶部导航栏 -->
        <nav class="nav-bar">
            <div class="logo">
                <img src="" alt="问呗" class="logo-img">
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
                    <el-select v-if="searchType === 'JOB'" v-model="searchObj.education" placeholder="学历要求"
                        style="width: 100px;">
                        <el-option label="不限" value="" />
                        <el-option label="博士" value="博士" />
                        <el-option label="硕士" value="硕士" />
                        <el-option label="本科" value="本科" />
                        <el-option label="大专" value="大专" />
                        <el-option label="大学以下" value="大学以下" />
                    </el-select>
                    <!-- 工作性质 -->
                    <el-select v-if="searchType === 'JOB'" v-model="searchObj.nature" placeholder="工作性质"
                        style="width: 100px;">
                        <el-option label="不限" value=""></el-option>
                        <el-option v-for="nature in natures" :label="nature.natureName" :value="nature.natureName"
                            :key="nature.natureId" />
                    </el-select>
                </span>

            </p>
        </section>
        <!-- 列表 -->
        <main style="padding: 0px 200px;">
            <JobList v-if="searchType === 'JOB'" @update:page-size="handleSizeChange($event, searchType)"
                @update:current-page="handleCurrentChange($event, searchType)" :condition="condition"></JobList>
            <CompanyList v-else-if="searchType === 'COMPANY'" @update:page-size="handleSizeChange($event, searchType)"
                @update:current-page="handleCurrentChange($event, searchType)" :condition="condition"></CompanyList>
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

.message-flag {
    position: absolute;
    width: 5px;
    height: 5px;
    right: 1%;
    background: #f00;
    border-radius: 50%;
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

.search-container {
    display: flex;
    margin: 10px;
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