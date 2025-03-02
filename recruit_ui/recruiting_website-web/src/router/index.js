import userStore from '@/store/user'
import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

// 定义白名单路由
const whiteList = [
  '/auth/login',
  '/404',
  // '/users/index',
  // '/registerCompany',
  // '/registerCompany/**',
  // '/registerCompany/account',
]

// 路由配置
const routes = [
  {
    path: '/404',
    component: () => import('../views/404.vue'),
  },
  {
    path: '/auth/login',
    component: () => import('../views/Login.vue'),
  },
  {
    path: '/users/index',
    component: () => import('../views/home/index.vue'),
  },
  {
    path: '/users/registerCompany',
    component: () => import('../views/company/registerCompany.vue'),
  },
  {
    path: '/user/register',
    component: () => import('../views/user/Register.vue'),
  },
  {
    name: 'JobInfo',
    path: '/user/jobInfo',
    component: () => import('../views/user/JobInfo.vue'),
  },
  {
    path: '/user/jobList',
    component: () => import('../views/user/JobList.vue'),
  },
  {
    path: '/user/userInfo',
    component: () => import('../views/user/UserInfo.vue'),
  },
  {
    path: '/user/application',
    component: () => import('../views/user/applicationRecord/index.vue'),
  },
  {
    path: '/home',
    name: 'home',
    component: () => import('../views/admin/home.vue'),
    children: [
      {
        path: 'jobResume',
        component: () => import('../views/company/jobResume.vue'),
      },
      {
        path: 'resumeDetail',
        component: () => import('../views/company/resumeDetail.vue'),
      },
      {
        path: 'userInfo',
        component: () => import('../views/company/userInfo.vue'),
      },
      {
        path: 'interviewTime',
        component: () => import('../views/company/interviewDate.vue'),
      },
    ],
  },
  {
    path: '/users/registerCompany/contactInfo',
    component: () => import('@/views/company/contactInfo.vue'),
  },
  {
    path: '/users/registerCompany/account',
    component: () => import('../views/company/account.vue'),
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ left: 0, top: 0 }),
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const store = userStore()

  if (to.path.startsWith('/users/')) {
    return next()
  }

  if (store.token && to.path === '/auth/login') {
    return next('/users/index')
  }

  if (!store.token && !whiteList.includes(to.path)) {
    return next('/auth/login')
  }

  if (store.token && store.userInfo.isFirstLogin == 0 && to.path != '/home') {
    ElMessage.warning('首次登录请先完善个人信息')
    return next('/home')
  }
  if (store.role != '5' && store.role != null) {
    const needsReview =
      store.token &&
      (store.userInfo.companyInfoReview != 1 || store.userInfo.enterpriseReview != 1) &&
      to.path != '/info'
    if (needsReview) {
      ElMessage.error('信息审核中，请耐心等待')
      return next('/info')
    }
  }

  if (to.matched.length === 0) {
    return next('/404')
  }

  next()
})

export default router
