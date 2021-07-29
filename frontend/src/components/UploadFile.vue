<template>
  <div class="upload-wrapper">
    <div class="form-group">
      <label for="newFile" class="form-label"> Upload new file</label>
      <input type="file" id="newFile" class="form-input" ref="file" v-on:change="handleFileUpload()"/>
    </div>
    <p v-if="error" class="form-error"> An upload error occurred</p>
    <button @click="uploadFile" class="form-btn"> Upload</button>
  </div>
</template>

<script lang="ts">
import axios from "axios";
import {defineComponent} from "vue";
import {VueCookieNext as $cookie} from 'vue-cookie-next'

export default defineComponent({
  name: 'UploadFile',
  data() {
    return {
      error: false,
      file: {} as File
    }
  },
  methods: {
    handleFileUpload() {
      let inputElement = this.$refs.file as HTMLInputElement;
      this.file = inputElement.files?.item(0) ?? {} as File;
    },
    uploadFile() {
      let token = localStorage.getItem("vue-token");
      if (token && this.file) {
        let formData = new FormData();
        // Add the form data we need to submit
        formData.append('file', this.file);

        axios.post('filestorage/api/files', formData, {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Access-Control-Allow-Origin': '*',
            'Access-Control-Allow-Methods': 'GET,PUT,POST,DELETE,PATCH,OPTIONS',
            'Content-Type': 'multipart/form-data',
            'X-XSRF-TOKEN': $cookie.getCookie('XSRF-TOKEN')
          }
        })
        .then(() => {
          this.$emit('reload');
        })
        .catch((error) => {
          this.error = true;
          console.log("error: " + error.response.data)
        })
      }
    }
  }
})
</script>

<style scoped>
.upload-wrapper {
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  align-items: center;
  border: #393d40 solid 1px;
  margin-bottom: 1.5rem;
  height: 15rem;
}

.form-label {
  font-size: 2rem;
  margin-right: 1rem;
  margin-left: 1rem;
  font-weight: 450;
  flex: 0 0 auto;
}

.form-input {
  padding: 1rem;
  flex: 0 0 auto;
}

.form-error {
  background-color: #d69764;
  font-size: 2rem;
  font-weight: 400;
  color: #f1edea;
  padding: 1rem;
  flex: 0 0 auto;
}

.form-btn {
  font-size: 2rem;
  text-transform: capitalize;
  padding: 1rem;
  border-radius: 0.5rem;
  box-shadow: 1px 2px #393d40;
  background-color: #d69764;
  cursor: pointer;
  color: #f1edea;
}
</style>