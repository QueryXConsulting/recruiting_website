import userStore from '@/store/user'
import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

// 定义白名单路由
const whiteList = [
  '/auth/login',
  '/404',
  '/users/search',
  '/users/register',
  '/users/index',
  '/users/register',
  '/users/registerCompany',
  '/users/registerCompany/contactInfo',
  '/users/registerCompany/account',
  '/users/jobInfo'
]

// 路由配置
const routes = [
  {
    path: '/',
    redirect: '/users/index',
  },
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
    path: '/users/register',
    component: () => import('../views/user/Register.vue'),
  },
  {
    name: 'JobInfo',
    path: '/users/jobInfo',
    component: () => import('../views/user/JobInfo.vue'),
  },
  {
    path: '/users/jobList',
    component: () => import('../views/user/list/JobList.vue'),
  },
  {
    path: '/users/companyList',
    component: () => import('../views/user/list/CompanyList.vue'),
  },
  {
    path: '/users/userInfo',
    component: () => import('../views/user/UserInfo.vue'),
  },
  {
    path: '/users/application',
    component: () => import('../views/user/applicationRecord/index.vue'),
  },
  {
    path: '/users/search',
    component: () => import('../views/user/search.vue'),
  },
  {
    path: '/users/message',
    component: () => import('../views/user/MessageBoard.vue'),
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
        path: 'homePage',
        component: () => import('../views/homePage.vue'),
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
      {
        path: 'interviewRoom',
        component: () => import('../views/company/interviewRoom.vue'),
        meta: {
          hideNavbar: true,
        },
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

  // TODO 暂时注释
  // if (to.path.startsWith('/users/')) {
  //   return next()
  // }


  if (store.token && to.path === '/auth/login') {
    return next('/users/index')
  }

  if (!store.token && !whiteList.includes(to.path)) {
    return next('/auth/login')
  }

  if (store.token && store.userInfo?.isFirstLogin == 0 && to.path != '/home' && store.role != '5') {
    ElMessage.warning('首次登录请先完善个人信息')
    return next('/home')
  }
  if (store.role != '5' && store.role != null) {
    const needsReview =
      store.token &&
      (store.userInfo.companyInfoReview != 1 || store.userInfo.enterpriseReview != 1) &&
      to.path != '/info'
      // TODO: 使用添加动态路由
      store.addRouters();
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
