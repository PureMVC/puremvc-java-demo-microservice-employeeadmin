FROM maven:3.6.0-jdk-11-slim AS build
WORKDIR /service
COPY src src
COPY pom.xml .
RUN mvn -f pom.xml clean package

FROM openjdk:8-jre
COPY --from=build /service/target /service
WORKDIR /service

CMD ["java", "-jar", "Role-1.0-SNAPSHOT-jar-with-dependencies.jar"]