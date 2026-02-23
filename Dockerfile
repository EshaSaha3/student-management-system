FROM eclipse-temurin:17-jre-alpine

# Create app directory
WORKDIR /app

# Copy jar file
COPY target/*.jar app.jar

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]