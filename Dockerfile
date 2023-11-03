FROM amazoncorretto:17-alpine
WORKDIR /app
COPY target/users-api.war users-api.war
CMD ["java", "-jar", "users-api.war"]