FROM docker.io/alpine:latest
RUN apk add --no-cache openjdk21-jre-headless caddy supervisor


COPY ./backend/api/build/libs/api-all.jar /backend/app.jar
COPY ./frontend/app/dist /serve
RUN mkdir /log

COPY ./misc/supervisor.conf /etc/supervisor/conf.d/supervisor.conf
COPY ./misc/Caddyfile /etc/caddy/Caddyfile

EXPOSE 80
ENTRYPOINT ["supervisord", "--configuration", "/etc/supervisor/conf.d/supervisor.conf"]
