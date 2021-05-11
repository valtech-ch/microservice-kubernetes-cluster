// this will be needed to get a tracer
import opentelemetry from '@opentelemetry/api';
// tracer provider for web
import { WebTracerProvider } from '@opentelemetry/web';
// and an exporter with span processor
import {
  SimpleSpanProcessor,
} from '@opentelemetry/tracing';
import { CollectorTraceExporter } from '@opentelemetry/exporter-collector';
import {JaegerExporter} from '@opentelemetry/exporter-jaeger';
// Create a provider for activating and tracking spans
const tracerProvider = new WebTracerProvider();

const serviceName = 'frontend';
let exporter = new JaegerExporter({
  serviceName
});

// Connect to Lightstep by configuring the exporter with your endpoint and access token.
tracerProvider.addSpanProcessor(new SimpleSpanProcessor(exporter));

// Register the tracer
tracerProvider.register();