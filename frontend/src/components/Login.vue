<template>
  <div class="login">
    <h1 class="login-text">Welcome to Valtech FileStorage</h1>
  </div>
  <div class="login">
    <h1 class="login-text">Login/Register to continue</h1>
    <div>
      <button name="login" @click="onLogin" class="login-btn">Log in</button>
      <button name='register' @click="onRegister" class="login-btn">Register</button>
    </div>
  </div>
</template>

<script lang="ts">
import Keycloak, {KeycloakInitOptions} from "keycloak-js";
import {defineComponent} from "vue";

let initOptions: KeycloakInitOptions = {
  onLoad: 'check-sso',
  silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html'
}

const keycloak = Keycloak({
  url: 'auth',
  realm: 'cluster',
  clientId: 'login-app'
})

export default defineComponent({
  // eslint-disable-next-line
  name: 'Login',
  methods: {
    refreshToken() {
      keycloak.updateToken(70).then((refreshed: boolean) => {
        if (refreshed) {
          console.log('Token refreshed' + refreshed);
        } else {
          if (keycloak.tokenParsed?.exp && keycloak.timeSkew) {
            console.log('Token not refreshed, valid for '
                + Math.round(keycloak.tokenParsed.exp + keycloak.timeSkew - new Date().getTime() / 1000)
                + ' seconds');
          }
        }
      }).catch(() => {
        console.log('Failed to refresh token');
      });
    },
    onLogin() {
      keycloak.login().then(() => {
        this.$router.push("/home");
      }).catch(() => {
        console.log("Authenticated Failed");
      });
    },
    onRegister() {
      keycloak.register().catch(() => {
        console.log("Registration Failed");
      });
      localStorage.clear();
    }
  },
  mounted() {
    keycloak.init(initOptions).then((auth: boolean) => {
      if (auth && keycloak.token && keycloak.refreshToken) {
        localStorage.setItem("vue-token", keycloak.token);
        localStorage.setItem("vue-refresh-token", keycloak.refreshToken);
        this.$router.push("/home");
      }
      this.refreshToken()
    })
  }
})
</script>

<style>
.login {
  width: 50%;
  background-color: #95cbdb;
  margin: 1rem auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-items: center;
  justify-content: center;
  text-align: center;
  padding: 2rem;
}

.login-btn {
  margin: 0 1rem;
  border-radius: 3rem;
  background-color: #393d40;
  font-size: 2.3rem;
  color: #f1edea;
  cursor: pointer;
  padding: 1rem 1rem;
  text-align: center;
  text-transform: uppercase;
}

.login-text {
  font-size: 3rem;
  color: #f1edea;
  margin: 1rem;
}
</style>
