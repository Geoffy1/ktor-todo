# Use a base image with a JDK
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the Gradle wrapper files
COPY gradlew .
COPY gradle gradle

# Copy the project files
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src src

# Make the Gradle wrapper executable
RUN chmod +x gradlew

# Expose the port your Ktor app listens on
EXPOSE 8080