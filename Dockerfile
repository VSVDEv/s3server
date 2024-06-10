# Use an official OpenJDK 17 runtime as a parent image
FROM amazoncorretto:17.0.7-alpine3.14

# Set the working directory in the container
WORKDIR /app

USER 0


# Copy the application's jar file into the container
COPY target/s3server-1.0.1.jar /app/s3server-1.0.1.jar

# Set default environment variables (optional)
ENV AWS_PORT=7777
ENV AWS_ACCESS_KEY=2
ENV AWS_SECRET_ACCESS_KEY=1
ENV AWS_REGION=us-east-1
ENV AWS_FOLDER=/app/s3


USER 0

RUN mkdir -p ${AWS_FOLDER}

USER $CONTAINER_USER_ID

# Make port available to the world outside this container
EXPOSE ${AWS_PORT}

# Run the jar file
ENTRYPOINT ["java", "-jar", "/app/s3server-1.0.1.jar"]
