import {createRouter, createWebHashHistory, RouteRecordRaw} from 'vue-router'
import Login from '@/components/Login.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/:login*',
    name: 'Login',
    component: Login
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import(/* webpackChunkName: "about" */ '@/components/Home.vue')
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router