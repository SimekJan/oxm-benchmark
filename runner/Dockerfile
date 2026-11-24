FROM eclipse-temurin:25-jdk

WORKDIR /app
COPY target/oxm-benchmark-runner-1.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
