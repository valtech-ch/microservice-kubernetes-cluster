import { createApp } from 'vue'
import App from './App.vue'
import { router } from './routes';
import VueCookies from 'vue3-cookies';


const app = createApp(App);
app.use(router);
app.use(VueCookies, {
  expireTimes: "30d",
  path: "/",
  domain: "",
  secure: true,
  sameSite: "None"
});
app.mount('#app')