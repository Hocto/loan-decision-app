FROM openjdk:17-jdk-slim
COPY build/libs/loan-app-0.0.1-SNAPSHOT.jar application.jar
ENTRYPOINT ["java","-jar","application.jar"]