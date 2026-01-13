import Vue from 'vue'
import VueRouter from 'vue-router'
import HomeView from '../views/HomeView.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/news/:nid',
    name: 'newsDetail',
    component: () => import('../views/NewsDetail.vue')
  },
  {
    path: '/life',
    name: 'life',
    component: () => import('../views/LifeView.vue')
  },
  {
    path: '/traffic',
    name: 'traffic',
    component: () => import('../views/TrafficView.vue')
  },
  {
    path: '/my',
    name: 'my',
    component: () => import('../views/MyView.vue')
  },
  {
    path: '/about',
    name: 'about',
    component: () => import('../views/AboutView.vue')
  }
]

const router = new VueRouter({
  mode: 'hash',  // 5+App 需要使用 hash 模式
  base: process.env.BASE_URL,
  routes
})

export default router
