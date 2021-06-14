import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import Login from '@/components/Login.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'Login',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: Login
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import(/* webpackChunkName: "about" */ '@/components/Home.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router

router.beforeEach(function(to, _, next) {
  console.log(to);
  if (to.fullPath === "/" && localStorage.getItem("vue-token") !== null) {
    next("/home");
  } else {
    next();
  }
});