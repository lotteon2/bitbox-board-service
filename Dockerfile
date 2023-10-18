FROM openjdk:11-jre-slim-buster

ARG PROFILE
ARG CONFIG

ENV USE_PROFILE=$PROFILE
ENV CONFIG_SERVER=$CONFIG
ENV ENCRYPT_KEY ""

COPY app.jar /app.jar

ENTRYPOINT ["/bin/sh","-c","java -Dspring.profiles.active=${USE_PROFILE} -jar ./app.jar"]