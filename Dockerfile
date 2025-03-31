# Base image with Java 21
FROM amazoncorretto:21-alpine

# Working directory
WORKDIR /app

# Copy the JAR file
COPY EntryApp/target/*.jar app.jar

# Expose port
EXPOSE 9090

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
