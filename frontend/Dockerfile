FROM node:21 as build-stage
COPY ./ /app
WORKDIR /app
RUN npm install && npm run build

FROM nginx:1.26.0-bookworm
RUN mkdir /app
COPY --from=build-stage /app/dist /app
COPY nginx.conf /etc/nginx/nginx.conf
