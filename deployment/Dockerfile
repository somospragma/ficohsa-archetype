FROM amazoncorretto:21-alpine-jdk AS builder

RUN apk add --no-cache tzdata && cp /usr/share/zoneinfo/America/Bogota /localtime

RUN jlink --compress=2 --module-path $JAVA_HOME/jmods \
 --add-modules java.base,java.logging,java.xml,jdk.unsupported,java.sql,java.naming,java.desktop,java.management,java.security.jgss,java.instrument,jdk.management,jdk.crypto.cryptoki \
 --no-header-files --no-man-pages --output /jlinked

WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle main.gradle ./
COPY applications applications
COPY domain domain
COPY infrastructure infrastructure
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon

FROM alpine:3.17

COPY --from=builder /localtime /etc/localtime
RUN echo "America/Bogota" > /etc/timezone && addgroup -S user && adduser -S user -G user

USER user

ENV JAVA_HOME /opt/jdk
ENV PATH $JAVA_HOME/bin:$PATH

COPY --from=builder /jlinked /opt/jdk
WORKDIR /app

COPY --from=builder /app/applications/app-service/build/libs/ScaffoldFicohsa.jar /app/ScaffoldFicohsa.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/ScaffoldFicohsa.jar"]
