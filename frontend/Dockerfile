FROM node:21 as build-stage
COPY ./ /app
WORKDIR /app
RUN npm install && npm run build

FROM nginx:stable
RUN mkdir /app
COPY --from=build-stage /app/dist /app
COPY nginx.conf /etc/nginx/nginx.conf
