# Use the official Gradle image as build environment
FROM gradle:jdk17 as build

# Copy the source code into the container
COPY . /home/gradle/src

# Set working directory
WORKDIR /home/gradle/src

# Build the application
RUN gradle clean build --no-watch-fs

# Start a new stage and use the OpenJDK image as base image
FROM openjdk:17-slim

# Create a user and a group named "appuser"
RUN groupadd appuser && useradd -g appuser appuser && \
   mkdir -p /app/access-refresh-token-keys && \
   chown -R appuser:appuser /app/access-refresh-token-keys && \
   chmod 700 /app/access-refresh-token-keys

# Copy the jar file from the build stage into this new stage
COPY --from=build --chown=appuser:appuser /home/gradle/src/build/libs/*.jar /app/app.jar

# Remove the source code
RUN rm -rf /home/gradle/src

# Change to non-root privilege
USER appuser

WORKDIR /app

# Run the jar file
ENTRYPOINT ["java","-jar","./app.jar"]
