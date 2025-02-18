FROM maven:3.6.3-jdk-11-slim as build-nsp-2g
WORKDIR /tmp/nsp-2g

COPY pom.xml .
COPY src/ /tmp/nsp-2g/src/
COPY libs/ /tmp/nsp-2g/libs/

RUN mvn validate
RUN mvn clean package -DskipTests

FROM gcr.io/distroless/java:11

COPY --from=build-nsp-2g /tmp/nsp-2g/target/ROOT.war /app/main.war

EXPOSE 8080

WORKDIR /app
CMD ["main.war"]
