FROM docker.io/alpine:latest as builder
RUN apk add --no-cache openjdk21-jdk binutils

ENV MODULES="java.base,java.logging,java.management,java.instrument,jdk.unsupported"
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk
RUN jlink \
	--module-path "$JAVA_HOME/jmods" \
	--verbose \
	--strip-debug \
	--no-header-files \
	--compress zip-9 \
	--no-man-pages \
	--add-modules $MODULES \
	--output /opt/jre-minimal

FROM docker.io/alpine:latest
RUN apk add --no-cache nginx

COPY --from=builder /opt/jre-minimal /opt/jre-minimal
ENV JAVA_HOME=/opt/jre-minimal
ENV PATH="$PATH:$JAVA_HOME/bin"

COPY ./backend/api/build/libs/api-all.jar /backend/app.jar
COPY ./frontend/app/dist /serve
RUN mkdir /log

COPY ./misc/nginx.conf /etc/nginx/nginx.conf
COPY ./misc/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 80
ENTRYPOINT ["/entrypoint.sh"]
