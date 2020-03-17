FROM maven:3.6.0-jdk-11-slim AS build
WORKDIR /service
COPY src src
COPY pom.xml .
RUN mvn -f pom.xml install

FROM openjdk:8-jre
COPY --from=build /service/target /service
WORKDIR /service
ENV _JAVA_OPTIONS "-agentlib:jdwp=transport=dt_socket,address=0.0.0.0:65193,suspend=n,server=y -Djava.net.preferIPv4Stack=true"
CMD ["java", "-jar", "Employee-1.0-SNAPSHOT-jar-with-dependencies.jar"]