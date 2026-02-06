FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY build/libs/server.jar /app/server.jar
EXPOSE 8080
ENV PORT=8080
ENTRYPOINT ["java", "-jar", "/app/server.jar"]
