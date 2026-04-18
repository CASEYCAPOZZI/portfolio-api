# Use Amazon Corretto 26 (The standard AWS Java distribution)
FROM amazoncorretto:26-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file you just built into the container
COPY target/*.jar app.jar

# Tell AWS/Docker that this container uses port 8080
EXPOSE 8080

# The command to start the application
ENTRYPOINT ["java", "-jar", "app.jar"]