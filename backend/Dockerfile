# Use an official Java runtime as a base image
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the app will be running on
EXPOSE 8888

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]

## Start with the official MySQL 5.7 image as the base
#FROM mysql:5.7
#
## Copy your SQL script(s) to the container's initialization directory
#COPY ./sql-scripts/internet_banking.sql /docker-entrypoint-initdb.d/
#
## Set environment variables for MySQL
#ENV MYSQL_ROOT_PASSWORD=25072018
#ENV MYSQL_DATABASE=internet_banking
#ENV MYSQL_USER=admin
#ENV MYSQL_PASSWORD=password123

