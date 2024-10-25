#!/bin/bash
nohup caddy run --config /etc/caddy/Caddyfile --watch > /log/caddy.log 2&>1 &
java -jar /backend/app.jar
