# Eclipse Temurin JDK 17 as the base image
FROM eclipse-temurin:17-jdk
# Set working directory inside the container
WORKDIR /app
# Copy the built JAR file from the Maven target directory into the container
COPY target/*.jar app.jar
# Expose the port application listens
EXPOSE 5600
# Run the application using the main class org.example.MathHealth
# -cp sets the classpath to the JAR file
ENTRYPOINT ["java", "-cp", "app.jar", "org.example.MathHealth"]
