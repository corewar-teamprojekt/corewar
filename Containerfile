FROM docker.io/openjdk:23-jdk-bookworm
RUN apt update && apt upgrade -y

RUN apt install -y debian-keyring debian-archive-keyring apt-transport-https curl supervisor

RUN bash -c "curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/gpg.key' | gpg --dearmor -o /usr/share/keyrings/caddy-stable-archive-keyring.gpg"
RUN bash -c "curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/debian.deb.txt' > /etc/apt/sources.list.d/caddy-stable.list"
RUN apt update
RUN apt install -y caddy

RUN echo ":80 {\n\
    root * /serve\n\
    file_server\n\
    reverse_proxy /api/* localhost:8080\n\
}" > /etc/caddy/Caddyfile

EXPOSE 80

RUN mkdir /backend
COPY ./backend/api/build/libs/api-all.jar /backend/app.jar
RUN mkdir /serve
COPY ./frontend/app/dist /serve
RUN mkdir /log

COPY ./misc/supervisor.conf /etc/supervisor/conf.d/supervisor.conf

ENTRYPOINT ["supervisord", "--configuration", "/etc/supervisor/conf.d/supervisor.conf"]
