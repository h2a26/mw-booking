# ---------- Build Stage ----------

# Use a lightweight Maven image with OpenJDK 21 (Alpine-based) for compiling the application
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

# Set the working directory inside the container for build context
WORKDIR /build

# Copy the Maven project file first to take advantage of Docker layer caching
COPY pom.xml .

# Pre-download dependencies to speed up builds and enable better caching
RUN mvn dependency:go-offline -B

# Copy the entire source code (after dependencies) to avoid unnecessary rebuilds if only source changes
COPY src ./src

# Compile and package the application into a JAR file, skipping tests to save time in the image build
RUN mvn clean package -DskipTests


# ---------- Runtime Stage ----------

# Use a minimal JRE base image (Alpine-based) with OpenJDK 21 to run the application, not build it
FROM eclipse-temurin:21.0.7_6-jre-alpine-3.21

# Set the timezone to Asia/Yangon for correct time-based operations in the app
ENV TZ=Asia/Yangon

# Set the working directory where the app will be placed and run
WORKDIR /app

# Copy the packaged JAR from the previous build stage into the runtime image
COPY --from=build /build/target/*.jar app.jar

# Add a non-root user and group for improved container security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Switch to the non-root user to follow the least privilege principle
USER appuser

# Expose the application port (adjust if your app listens on a different port)
EXPOSE 8080

# Run the JAR file using the java -jar command
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
