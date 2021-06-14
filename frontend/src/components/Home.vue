<template>
  <div class="nav">
    <button name="logout" v-if="isLoggedIn" @click="onLogout" class="logout-btn">Logout</button>
  </div>

  <div>
    <upload-file v-if="showUpload" @reload="loadAllFiles"></upload-file>

    <button @click="showUpload= !showUpload" class="upload-btn"> Upload new File</button>

    <p v-if="errorMessage != null"> {{ errorMessage }}</p>
    <file v-for="file in files" v-bind:key="file" :filename="file.filename" @reload="loadAllFiles"></file>
  </div>
</template>

<script lang="ts">
import axios from "axios";
import {defineComponent} from "vue";
import UploadFile from "./UploadFile.vue";
import File from "./File.vue";

export default defineComponent({
  name: 'Home',
  components: {
    UploadFile,
    File
  },
  data() {
    return {
      files: [],
      showUpload: false,
      errorMessage: null,
      token: '',
      isLoggedIn: false
    }
  },
  methods: {
    loadAllFiles() {
      if (this.token) {
        axios.get('filestorage/api/files', {
          headers: {
            'Authorization': `Bearer ${this.token}`,
            'Access-Control-Allow-Origin': '*',
            'Access-Control-Allow-Methods': 'GET,PUT,POST,DELETE,PATCH,OPTIONS'
          }
        })
        .then((res) => {
          this.files = res.data;
        })
        .catch((error) => {
          this.errorMessage = error.response.data;
          console.log("Error: " + this.errorMessage)
        })
      }
    },
    onLogout() {
      axios.get('auth/realms/cluster/protocol/openid-connect/logout', {
        headers: {
          'Authorization': `Bearer ${this.token}`,
          'Content-Type': 'application/x-www-form-urlencoded',
          'client_id': 'login-app',
          'refresh_token': localStorage.getItem("vue-refresh-token")
        }
      })
      .then(() => {
        localStorage.clear();
        this.$router.push('/');
      })
      .catch((error) => {
        console.log("Error: " + error.response.data)
      })
    }
  },
  created() {
    this.token = localStorage.getItem("vue-token") ?? '';
    if (this.token) this.isLoggedIn = true;
    this.loadAllFiles();
  }
})
</script>

<style>
#app {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin: 8rem auto;
  max-width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

*,
*::after,
*::before {
  margin: 0;
  padding: 0;
  box-sizing: inherit;
}

html {
  box-sizing: border-box;
  font-family: "Roboto", sans-serif;
  font-size: 62.5%;
}

.upload-btn {
  /*grid-row: 2;*/
  margin-bottom: 2rem;
  margin-top: 1rem;
  border-radius: 3rem;
  background-color: #393d40;
  font-size: 2.3rem;
  color: #f1edea;
  cursor: pointer;
  padding: 1rem 1rem;
  text-align: center;
}

.nav {
  width: 100%;
  margin-bottom: 1rem;
  margin-top: -8rem;
  display: flex;
  justify-content: flex-end;
  padding: 2rem;
}

.logout-btn {
  margin: 0 1rem;
  border-radius: 3rem;
  background-color: #95cbdb;
  font-size: 1.5rem;
  color: #393d40;
  cursor: pointer;
  padding: 1rem 1rem;
  text-align: center;
  text-transform: uppercase;
}
</style>
