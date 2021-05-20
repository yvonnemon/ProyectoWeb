FROM openjdk:8-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
RUN mvn -f /back/pom.xml clean install -DskipTests
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]