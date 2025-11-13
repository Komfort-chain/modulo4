FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/*.jar app.jar
COPY src/main/resources/logback-spring.xml /app/logback-spring.xml
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]