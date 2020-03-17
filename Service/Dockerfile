FROM maven:3.6.0-jdk-11-slim AS build
WORKDIR /service
COPY src src
COPY pom.xml .
RUN mvn -f pom.xml clean package

FROM jboss/wildfly:18.0.0.Final
COPY --from=build /service/target/Service.war /opt/jboss/wildfly/standalone/deployments/Service.war
ENV _JAVA_OPTIONS "-agentlib:jdwp=transport=dt_socket,address=0.0.0.0:65193,suspend=n,server=y -Djava.net.preferIPv4Stack=true"
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]