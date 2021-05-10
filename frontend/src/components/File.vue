<template>
  <div class="file">
    <h3 class="file-name">{{ filename }}</h3>
    <svg class="file-delete" v-on:click="deleteFile">
      <use xlink:href="../assets/images/sprite.svg#icon-trash"></use>
    </svg>
  </div>
</template>
<script>
import axios from 'axios';
 export default {
  name: 'File',
  props: ["filename"],
  methods: {
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
  width: 30%;
  /*width: 100%;*/
  height: 6rem;
  background-color: #95cbdb;
  margin: 1rem auto;
  display: grid;
  grid-template-columns: 65% 35%;
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
</style>