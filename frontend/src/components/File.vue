<template>
  <div class="file">
    <h3 class="file-name" @click.prevent="downloadFile">{{ filename }}</h3>
    <svg class="file-delete" v-on:click="deleteFile">
      <use xlink:href="../assets/images/sprite.svg#icon-trash"></use>
    </svg>
    <svg class="file-changes" v-on:click="listChanges">
      <use xlink:href="../assets/images/log-file.svg#icon-logs"></use>
    </svg>
    <ul class="messages">
      <li v-for="item in messages" :key="item.message">
        {{ item.message }}
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import {defineComponent} from 'vue'
import axios from 'axios';
import {VueCookieNext as $cookie} from 'vue-cookie-next'

export default defineComponent({
  // eslint-disable-next-line
  name: 'File',
  props: {
    filename: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      messages: [],
    }
  },
  methods: {
    downloadFile() {
      const token = localStorage.getItem("vue-token");
      axios.get<Blob>('filestorage/api/files/' + this.filename, {
        responseType: 'blob',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Access-Control-Allow-Origin': '*',
          'Access-Control-Allow-Methods': 'GET,PUT,POST,DELETE,PATCH,OPTIONS'
        }
      })
      .then(res => {
        const link = document.createElement('a')
        link.href = URL.createObjectURL(res.data)
        link.download = this.filename
        link.click()
        URL.revokeObjectURL(link.href)
      }).catch((error) => {
        console.log("Error: " + error.response.data)
      })
    },
    listChanges() {
      const token = localStorage.getItem("vue-token");
      axios.get<[]>('filestorage/api/files/changes/' + this.filename, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Access-Control-Allow-Origin': '*',
          'Access-Control-Allow-Methods': 'GET,PUT,POST,DELETE,PATCH,OPTIONS'
        }
      })
      .then(res => {
        this.messages = res.data;
      })
      .catch((error) => {
        console.log("Error: " + error.response.data)
      })
    },
    deleteFile() {
      const token = localStorage.getItem("vue-token");
      axios.delete('filestorage/api/files/' + this.filename, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Access-Control-Allow-Origin': '*',
          'Access-Control-Allow-Methods': 'GET,PUT,POST,DELETE,PATCH,OPTIONS',
          'X-XSRF-TOKEN': $cookie.getCookie('XSRF-TOKEN')
        }
      })
      .then(() => {
        this.$emit('reload');
      })
      .catch((error) => {
        console.log("Error: " + error.response.data)
      })
    }
  }
})
</script>

<style>
.file {
  width: 100%;
  height: fit-content;
  padding: 1rem;
  background-color: #95cbdb;
  margin: 1rem auto;
  display: flex;
  flex-direction: row;
  flex-grow: 2;
  align-items: center;
  justify-items: center;
}

.file-name {
  font-size: 3rem;
  text-transform: capitalize;
  color: #f1edea;
  display: inline;
  flex-grow: 2;
}

.file-delete {
  width: 4rem;
  height: 4rem;
  fill: #393d40;
  cursor: pointer;
}

.file-changes {
  width: 4rem;
  height: 4rem;
  fill: #393d40;
  cursor: pointer;
}

.messages {
  width: fit-content;
  fill: #393d40;
}
</style>