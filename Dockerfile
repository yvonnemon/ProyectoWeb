FROM openjdk:8-jre-alpine3.9
COPY /back/target/proyecto-0.0.1-SNAPSHOT.jar proyecto-1.0.0.jar
ENTRYPOINT ["java","-jar","/proyecto-1.0.0.jar"]