# ==========================================
# Stage 1: Build the Java JAR (The Builder)
# ==========================================
FROM amazoncorretto:26-alpine AS builder
WORKDIR /app

# Copy the Maven wrapper and configuration files first
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Grant execution rights to the Maven wrapper
RUN chmod +x ./mvnw

# Download dependencies (this caches them so future builds are faster)
RUN ./mvnw dependency:go-offline

# Copy the actual source code
COPY src ./src

# Compile the code and build the JAR file (skipping tests for speed)
RUN ./mvnw clean package -DskipTests

# ==========================================
# Stage 2: Run the Java App (The Runner)
# ==========================================
FROM amazoncorretto:26-alpine
WORKDIR /app

# Copy ONLY the compiled JAR from Stage 1 into this final image
COPY --from=builder /app/target/*.jar app.jar

# Expose the port
EXPOSE 8080

# Boot the application
ENTRYPOINT ["java", "-jar", "app.jar"]