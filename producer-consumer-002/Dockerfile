# Use the official OpenJDK 21 image as the base image
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
#WORKDIR /app
ARG JAR_FILE=target/*.jar

# Copy the Maven build file and application source code
COPY target/*.jar /app.jar

# Expose the application port (Spring Boot default port is 8080)
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]