FROM eclipse-temurin:17-jdk-alpine

RUN addgroup appgroup
RUN adduser --ingroup appgroup --disabled-password app
USER app

WORKDIR /home/app

COPY target/*.jar github-microservice.jar
ENTRYPOINT ["java","-jar","/home/app/github-microservice.jar"]