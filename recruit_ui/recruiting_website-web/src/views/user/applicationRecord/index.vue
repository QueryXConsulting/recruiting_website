<script setup>
import { ref, onMounted } from 'vue';
import router from '@/router';
import { defineStore } from 'pinia';
import userStore from '@/store/user';
import { iconMapping } from '@/utils/iconList';
import { Bell } from '@element-plus/icons-vue';
import { userLogout } from '@/api/company/companyApi';
import { ElMessage, ElMessageBox } from 'element-plus';
import ApplicationRecord from './ApplicationRecord.vue';
import AppointmentInterview from './AppointmentInterview.vue';
import ConfirmOffer from './ConfirmOffer.vue';
import Questionnaires from './Questionnaires.vue';
import UploadMaterials from './UploadMaterials.vue';
import InformationInput from './InformationInput.vue';
import AppointmentRegistration from './AppointmentRegistration.vue';
import { materialStatus, registrationStatus } from '@/api/user/UserApi';


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
    } else if (command === 'index') {
        router.push('/home');
    }
}

// const useTabStore = defineStore('tab', {
//     state: () => {
//         return {
//             tabIndex: '0',
//             tabName: '投递记录'
//         }
//     },
//     actions: {
//         setTabIndex(index) {
//             this.tabIndex = index;
//         },
//         setTabName(name) {
//             this.tabName = name;
//         }
//     },
//     getters: {
//         getTabIndex() {
//             return this.tabIndex;
//         },
//         getTabName() {
//             return this.tabName;
//         }
//     },
//     persist: {
//         key: 'tab',
//         storage: sessionStorage,
//         paths: ['tabIndex', 'tabName'],
//     },
// });

const activeName = ref('0'); // tab 切换
const activeLabel = ref('投递记录'); // tab 名称
const isDisabled1 = ref(false); // 上传材料按钮是否禁用
const isDisabled2 = ref(false); // 信息录入按钮是否禁用
const isDisabled3 = ref(false); // 预约报到按钮是否禁用
// 投递流程对象
const tabPanes = [
    {
        index: '0',
        label: '投递记录',
    },
    {
        index: '1',
        label: '预约面试',
    },
    {
        index: '2',
        label: '确认offer',
    },
    {
        index: '3',
        label: '上传材料',
    },
    // {
    //     index: '4',
    //     label: '信息录入',
    // },
    {
        index: '5',
        label: '预约报到',
    },
];


const clickTab = (pane, e) => {
    // useTabStore().setTabIndex(pane.index);
    // useTabStore().setTabName(pane.props.label);
    activeLabel.value = pane.props.label;
}

// const setActiveName = async (index, status) => {
//     const res = await materialStatus();
//     if (res.content === status) {
//         ElMessage.error('未到该环节，无法使用该功能，请返回！');
//         return false;
//     }
//     const name = tabPanes[index].label;
//     useTabStore().setTabIndex(index);
//     return true;
// }

// const beforeLeave = async (to, from) => {
//     switch (to) {
//         case '3':
//             return setActiveName(+to, -1);
//         case '5':
//             return setActiveName(+to, 1);
//         case '6':
//             return setActiveName(+to, 0);
//     }
//     const name = tabPanes[+to].label;
//     activeLabel.value = name;
//     useTabStore().setTabName(name);
//     useTabStore().setTabIndex(+to);
//     return true;
// }

onMounted(() => {
    materialStatus().then((res) => {
        if (Number.isNaN(+res.content.status) || Number(res.content.status) === -1) {
            isDisabled1.value = true;
        } else {
            isDisabled1.value = false;
        }
    });
    registrationStatus().then((res) => {
        if (res?.content || Object.keys(res.content).length > 0) {
            isDisabled2.value = false;
            isDisabled3.value = false;
        } else {
            isDisabled2.value = true;
            isDisabled3.value = true;
        }
    });
});
</script>

<template>
    <div style="padding: 20px;">
        <!-- 顶部导航栏 -->
        <div style="height: 73px;width: 100%;">
            <nav class="nav-bar">
                <div class="logo">
                    <!-- <img src="/public/logo.png" alt="问呗" class="logo-img">
            <span class="divider">|</span>
            <span class="recruit-text">招聘</span> -->
                </div>
                <div class="nav-items">
                    <a href="/users/index" class="nav-item">首页</a>
                    <a href="/users/search" class="nav-item">校园招聘</a>
                    <a href="#" class="nav-item">社会招聘</a>
                    <a href="/users/application" class="nav-item active" v-if="userStore().role == '5'">投递历史</a>
                    <a href="/users/message" class="nav-item" v-if="userStore().role == '5'" style="padding-top: 15px;"
                        alt="留言板">
                        <el-icon>
                            <component :is="Bell"></component>
                        </el-icon>
                        <!-- 未读消息提示(小圆点) -->
                        <i v-if="hasMessage" class="message-flag"></i>
                    </a>
                    <!-- 用户头像 -->
                    <el-dropdown v-if="userStore().token != null" @command="handleCommand">
                        <span class="user-dropdown">
                            <el-avatar size="large" :src="userStore().userInfo.userAvatar" @error="() => { }">
                                <!-- 头像加载失败时显示默认头像 -->
                                <img src="/public/default_user.png" alt="用户头像">
                            </el-avatar>
                        </span>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item command="index"
                                    v-if="userStore().role != '5' && userStore().role != null">
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
        </div>

        <div class="tabs-container">
            <h1>{{ activeLabel }}</h1>
            <!-- :before-leave="beforeLeave"  -->
            <el-tabs v-model="activeName" @tab-click="clickTab">
                <el-tab-pane name="0" label="投递记录" lazy>
                    <ApplicationRecord></ApplicationRecord>
                </el-tab-pane>
                <el-tab-pane name="1" label="问卷调查" lazy>
                    <Questionnaires></Questionnaires>
                </el-tab-pane>
                <el-tab-pane name="2" label="预约面试" lazy>
                    <AppointmentInterview></AppointmentInterview>
                </el-tab-pane>
                <el-tab-pane name="3" label="确认offer" lazy>
                    <ConfirmOffer></ConfirmOffer>
                </el-tab-pane>
                <el-tab-pane name="4" label="上传材料" :disabled="isDisabled1" lazy>
                    <UploadMaterials></UploadMaterials>
                </el-tab-pane>
                <el-tab-pane name="5" label="信息录入" :disabled="isDisabled2" lazy>
                    <InformationInput></InformationInput>
                </el-tab-pane>
                <el-tab-pane name="6" label="预约报到" :disabled="isDisabled3" lazy>
                    <AppointmentRegistration></AppointmentRegistration>
                </el-tab-pane>
            </el-tabs>
        </div>
    </div>
</template>

<style lang="scss" scoped>
h1,
h2,
h3 {
    font: none;
    padding: 30px 0 10px 0;
    font-size: 24px;
    margin-bottom: 20px;
}

.tabs-container {
    padding: 15px;
    background-color: #fff;
}

/* 头部导航栏 */
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

// .logo-img {
//   height: 32px;
//   width: auto;
// }

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
</style>
