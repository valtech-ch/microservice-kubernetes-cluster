import Vue, { PluginObject } from 'vue';
import Keycloak, { KeycloakConfig } from 'keycloak-js';

const initOptions: KeycloakConfig = {
  // url: process.env.VUE_APP_KEYCLOAK_URL,
  // realm: process.env.VUE_APP_KEYCLOAK_REALM,
  // clientId: process.env.VUE_APP_KEYCLOAK_CLIENT_ID,
  url: 'https://vtch-aks-demo.duckdns.org/auth',
  realm: 'cluster',
  clientId: 'login-app',
  onLoad: 'login-required'

};

const _keycloak = Keycloak(initOptions);

const Plugin: PluginObject<any> = {
  install: Vue => {
    Vue.$keycloak = _keycloak;
  },
};
Plugin.install = Vue => {
  Vue.$keycloak = _keycloak;
  Object.defineProperties(Vue.prototype, {
    $keycloak: {
      get() {
        return _keycloak;
      },
    },
  });
};

Vue.use(Plugin);

export default Plugin;