import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { title: '登录', guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { title: '注册', guest: true }
  },
  {
    path: '/polls',
    name: 'PollList',
    component: () => import('@/views/polls/PollList.vue'),
    meta: { title: '投票列表' }
  },
  {
    path: '/polls/:id',
    name: 'PollDetail',
    component: () => import('@/views/polls/PollDetail.vue'),
    meta: { title: '投票详情' }
  },
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('@/views/admin/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: '',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '管理后台' }
      },
      {
        path: 'polls',
        name: 'AdminPolls',
        component: () => import('@/views/admin/PollManage.vue'),
        meta: { title: '投票管理' }
      },
      {
        path: 'polls/create',
        name: 'CreatePoll',
        component: () => import('@/views/admin/PollForm.vue'),
        meta: { title: '创建投票' }
      },
      {
        path: 'polls/:id/edit',
        name: 'EditPoll',
        component: () => import('@/views/admin/PollForm.vue'),
        meta: { title: '编辑投票' }
      }
    ]
  },
  {
    path: '/my-polls',
    name: 'MyPolls',
    component: () => import('@/views/polls/MyPolls.vue'),
    meta: { title: '我的投票', requiresAuth: true }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title || '在线投票系统'} - 在线投票系统`
  
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else if (to.meta.requiresAdmin && authStore.user?.role !== 'ADMIN') {
    next({ name: 'Home' })
  } else if (to.meta.guest && authStore.isAuthenticated) {
    next({ name: 'Home' })
  } else {
    next()
  }
})

export default router
