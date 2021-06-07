import { createRouter, createWebHistory } from "vue-router";
import Login from "./Login"
import Home from "@/components/Home";

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    {path: "/", component: Login},
    {path: "/home", component: Home}
    ]
})

router.beforeEach(function(to, _, next) {
  console.log(to);
  if (to.fullPath === "/" && localStorage.getItem("vue-token") !== null) {
    next("/home");
  } else {
    next();
  }
});