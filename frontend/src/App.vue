<template>
  <router-view></router-view>
</template>

<script>
import {BatchSpanProcessor} from '@opentelemetry/sdk-trace-base';
import {WebTracerProvider} from '@opentelemetry/sdk-trace-web';
import {ZipkinExporter} from '@opentelemetry/exporter-zipkin';
import {XMLHttpRequestInstrumentation} from '@opentelemetry/instrumentation-xml-http-request';
import {ZoneContextManager} from '@opentelemetry/context-zone';
import {registerInstrumentations} from '@opentelemetry/instrumentation';
import {DocumentLoadInstrumentation} from '@opentelemetry/instrumentation-document-load';
import {Resource} from '@opentelemetry/resources';
import {SemanticResourceAttributes} from '@opentelemetry/semantic-conventions';
import {CompositePropagator, W3CTraceContextPropagator} from '@opentelemetry/core';

const zipkinOptions = {
  url: 'https://vtch-aks-demo-monitoring.duckdns.org/api/v2/spans',
  headers: {}
};

const providerWithZone = new WebTracerProvider({
  resource: new Resource({
    [SemanticResourceAttributes.SERVICE_NAME]: 'frontend',
  }),
});
providerWithZone.addSpanProcessor(new BatchSpanProcessor(new ZipkinExporter(zipkinOptions)));
providerWithZone.register({
  contextManager: new ZoneContextManager(),
  propagator: new CompositePropagator({
    propagators: [
      new W3CTraceContextPropagator(),
    ],
  })
});

registerInstrumentations({
  instrumentations: [
    new DocumentLoadInstrumentation(),
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
