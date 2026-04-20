# ============================================================
# STAGE 1: Build the application
# ============================================================
# We use a full JDK + Maven image so we can compile the project.
# "AS build" names this stage so we can reference it later.
FROM maven:3.9-eclipse-temurin-21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy ONLY the pom.xml first and download dependencies.
# Why? Docker caches each layer. If pom.xml hasn't changed,
# Docker reuses the cached dependencies layer — saves minutes!
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Now copy the actual source code
COPY src ./src

# Build the app (skip tests — they run in CI separately)
# This produces: target/job-match-0.0.1-SNAPSHOT.jar
RUN mvn package -DskipTests -B

# ============================================================
# STAGE 2: Run the application
# ============================================================
# Use a slim JRE image (no compiler, no Maven — much smaller)
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy ONLY the built jar from stage 1 (not the source code!)
COPY --from=build /app/target/*.jar app.jar

# Create a directory for file uploads (resumes)
RUN mkdir -p /app/uploads/resumes

# The app listens on port 8080
EXPOSE 8080

# Start the application.
# spring.profiles.active=docker tells Spring to use
# application-docker.properties (we'll create this next).
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
