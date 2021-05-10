<template>
  <upload-file v-if="showUpload" @reload="loadAllFiles"></upload-file>

  <button @click="showUpload= !showUpload" class="upload-btn"> Upload new File</button>
  ----------
  <p v-if="errorMessage != null"> {{ errorMessage }}</p>
  <file v-for="file in files" v-bind:key="file" :filename="file.filename" @reload="loadAllFiles"></file>
</template>

<script>
import axios from "axios";
import UploadFile from "@/components/UploadFile";
import File from "@/components/File";
// import {ConsoleSpanExporter, SimpleSpanProcessor} from '@opentelemetry/tracing';
// import {WebTracerProvider} from '@opentelemetry/web';
// import {XMLHttpRequestInstrumentation} from '@opentelemetry/instrumentation-xml-http-request';
// import {ZoneContextManager} from '@opentelemetry/context-zone';
// import {registerInstrumentations} from '@opentelemetry/instrumentation';

export default {
  name: 'App',
  components: {
    UploadFile,
    File
  },
  data() {
    return {
      files: [],
      showUpload: false,
      errorMessage: null,
      token: ''
    }
  },

  methods: {
    loadAllFiles() {
      if (this.token) {
        // http://localhost:8090/api/files - locally
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
    }
  },
  created() {
    this.token = localStorage.getItem("vue-token");
    this.loadAllFiles();


    //OPEN-TELEMETRY INSTRUMENTATION
    // const providerWithZone = new WebTracerProvider();
    // providerWithZone.addSpanProcessor(new SimpleSpanProcessor(new ConsoleSpanExporter()));
    //
    // providerWithZone.register({
    //   contextManager: new ZoneContextManager(),
    // });
    //
    // registerInstrumentations({
    //   instrumentations: [
    //     new XMLHttpRequestInstrumentation({
    //       propagateTraceHeaderCorsUrls: ['https://vtch-aks-demo.duckdns.org/']
    //     }),
    //   ],
    // });

  }
}
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
</style>
