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

router.beforeEach(function (to, _, next) {
  if (to.name != 'Home' && localStorage.getItem("vue-token") !== null) {
    next("/home");
  } else {
    next();
  }
})