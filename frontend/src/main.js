import { createApp } from 'vue'
import App from './App.vue'
import Keycloak from "keycloak-js";

let initOptions = {
  url: 'auth', realm: 'cluster', clientId: 'login-app', onLoad:'login-required'
}
let keycloak = Keycloak(initOptions);

keycloak.init({ onLoad: initOptions.onLoad, checkLoginIframe: false }).success((auth) =>{

  if(!auth) {
    window.location.reload();
  } else {
    console.log("Authenticated");
    localStorage.setItem("vue-token", keycloak.token);
    localStorage.setItem("vue-refresh-token", keycloak.refreshToken);

    const app = createApp(App)
    app.mount('#app')
 }

  //Token Refresh
  setInterval(() => {
    keycloak.updateToken(70).then((refreshed) => {
      if (refreshed) {
        console.log('Token refreshed' + refreshed);
      } else {
        console.log('Token not refreshed, valid for '
          + Math.round(keycloak.tokenParsed.exp + keycloak.timeSkew - new Date().getTime() / 1000) + ' seconds');
      }
    }).catch(() => {
      console.log('Failed to refresh token');
    });
  }, 6000)

}). error(() =>{
  console.log("Authenticated Failed");
});