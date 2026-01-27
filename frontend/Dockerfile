
FROM node:18-alpine as build

WORKDIR /app

COPY package*.json ./


RUN npm ci


COPY . .


RUN npm run build


FROM nginx:alpine as production


RUN apk add --no-cache curl


COPY nginx.conf /etc/nginx/nginx.conf


COPY --from=build /app/dist/helpdesk-frontend /usr/share/nginx/html


RUN mkdir -p /var/run/nginx /var/log/nginx && \
    chown -R nginx:nginx /var/cache/nginx /var/log/nginx /etc/nginx/conf.d /var/run/nginx && \
    chown -R nginx:nginx /usr/share/nginx/html && \
    chmod 755 /var/run/nginx

USER nginx

EXPOSE 80


HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:80 || exit 1

CMD ["nginx", "-g", "daemon off;"]
