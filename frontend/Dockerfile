FROM node:19 as build-stage
COPY ./ /app
WORKDIR /app
RUN npm install && npm run build

FROM nginx:1.22.1
RUN mkdir /app
COPY --from=build-stage /app/dist /app
COPY nginx.conf /etc/nginx/nginx.conf