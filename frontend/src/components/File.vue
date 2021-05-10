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
import {SpanStatusCode, trace} from '@opentelemetry/api';
export default {
  name: 'File',
  props: ["filename"],
  methods: {
    deleteFile() {
      let token = localStorage.getItem("vue-token");
      const tracer = trace.getTracer("frontend", "0.1.0");
      const span = tracer.startSpan("deleteFile");
      axios.delete('filestorage/api/files/' + this.filename, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Access-Control-Allow-Origin': '*',
          'Access-Control-Allow-Methods': 'GET,PUT,POST,DELETE,PATCH,OPTIONS'
        }
      })
      .then(() => {
        this.$emit('reload');
        span.setStatus({ code: SpanStatusCode.OK });
      })
      .catch((error) => {
        this.error = true;
        span.setStatus({
          code: SpanStatusCode.ERROR,
          message: error.response.data,
        });
      }).finally( () => {
        // Every span must be ended or it will not be exported
        span.end();
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