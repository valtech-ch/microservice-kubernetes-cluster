import { createApp } from 'vue'
import App from './App.vue'
import { router } from './routes'
import { VueCookieNext } from 'vue-cookie-next'

const app = createApp(App)
app.use(VueCookieNext)
app.use(router)
app.mount('#app')