FROM docker.io/alpine:latest as builder
RUN apk add --no-cache openjdk21-jdk binutils zstd dos2unix

ENV MODULES="java.base,java.logging,java.management,java.instrument,jdk.unsupported"
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk
RUN jlink \
	--module-path "$JAVA_HOME/jmods" \
	--verbose \
	--strip-debug \
	--no-header-files \
	--compress zip-0 \
	--no-man-pages \
	--add-modules $MODULES \
	--output /opt/jre-minimal

WORKDIR /opt
RUN tar -cf jre-minimal.tar jre-minimal
RUN zstd --ultra -22 /opt/jre-minimal.tar

COPY ./misc/entrypoint.sh /entrypoint.sh
RUN dos2unix /entrypoint.sh

FROM docker.io/alpine:latest
RUN apk add --no-cache nginx zstd

COPY --from=builder /opt/jre-minimal.tar.zst /opt/jre-minimal.tar.zst
ENV JAVA_HOME=/opt/jre-minimal
ENV PATH="$PATH:$JAVA_HOME/bin"

COPY ./backend/api/build/libs/api-all.jar /backend/app.jar
COPY ./frontend/app/dist /serve
RUN mkdir /log

COPY ./misc/nginx.conf /etc/nginx/nginx.conf
COPY --from=builder /entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 80
ENTRYPOINT ["/entrypoint.sh"]
