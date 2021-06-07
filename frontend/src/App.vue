<template>
  <router-view></router-view>
</template>

<script>
import {BatchSpanProcessor} from '@opentelemetry/tracing';
import {WebTracerProvider} from '@opentelemetry/web';
import {ZipkinExporter} from '@opentelemetry/exporter-zipkin';
import {XMLHttpRequestInstrumentation} from '@opentelemetry/instrumentation-xml-http-request';
import {ZoneContextManager} from '@opentelemetry/context-zone';
import {registerInstrumentations} from '@opentelemetry/instrumentation';

const zipkinOptions = {
  serviceName: 'frontend',
  url: 'https://vtch-aks-demo-monitoring.duckdns.org/api/v2/spans',
  headers: {}
};

const providerWithZone = new WebTracerProvider();
providerWithZone.addSpanProcessor(new BatchSpanProcessor(new ZipkinExporter(zipkinOptions)));
providerWithZone.register({
  contextManager: new ZoneContextManager()
});

registerInstrumentations({
  instrumentations: [
    new XMLHttpRequestInstrumentation({
      propagateTraceHeaderCorsUrls: ['https://vtch-aks-demo.duckdns.org/']
    })
  ],
});

export default {
  name: 'App'
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
</style>
