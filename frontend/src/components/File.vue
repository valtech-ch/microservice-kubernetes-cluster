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
<script>
import axios from 'axios';
export default {
  name: 'File',
  props: ["filename"],
  data() {
    return {
      messages: [],
    }
  },
  methods: {
    downloadFile () {
      let token = localStorage.getItem("vue-token");
      axios.get('filestorage/api/files/' + this.filename, {
        responseType: 'blob',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Access-Control-Allow-Origin': '*',
          'Access-Control-Allow-Methods': 'GET,PUT,POST,DELETE,PATCH,OPTIONS'
        }
      })
      .then(res => {
        const blob = new Blob([res.data])
        const link = document.createElement('a')
        link.href = URL.createObjectURL(blob)
        link.download = this.filename
        link.click()
        URL.revokeObjectURL(link.href)
      }).catch((error) => {
        this.error = true;
        console.log("Error: " + error.response.data)
      })
    },
    listChanges () {
      let token = localStorage.getItem("vue-token");
      axios.get('filestorage/api/files/changes/' + this.filename, {
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
        this.error = true;
        console.log("Error: " + error.response.data)
      })
    },
    deleteFile() {
      let token = localStorage.getItem("vue-token");
      axios.delete('filestorage/api/files/' + this.filename, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Access-Control-Allow-Origin': '*',
          'Access-Control-Allow-Methods': 'GET,PUT,POST,DELETE,PATCH,OPTIONS'
        }
      })
      .then(() => {
        this.$emit('reload');
      })
      .catch((error) => {
        this.error = true;
        console.log("Error: " + error.response.data)
      })
    }
  }
}
</script>

<style>
.file {
  width: 50%;
  /*width: 100%;*/
  height: 6rem;
  background-color: #95cbdb;
  margin: 1rem auto;
  display: grid;
  grid-template-columns: 80% 10% 10%;
  align-items: center;
  justify-items: center;
}

.file-name {
  font-size: 3rem;
  text-transform: capitalize;
  color: #f1edea;
  display: inline;
  grid-column: 1;
}

.file-delete {
  width: 4rem;
  height: 4rem;
  fill: #393d40;
  grid-column: 2;
  cursor: pointer;
}

.file-changes {
  width: 4rem;
  height: 4rem;
  fill: #393d40;
  grid-column: 3;
  cursor: pointer;
}

.messages {
  width: 50rem;
  fill: #393d40;
  grid-column: 4;
}
</style>