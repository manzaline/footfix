FROM openjdk:17-jdk-slim

WORKDIR /app
COPY . .

RUN sed -i 's/\r$//' mvnw
RUN chmod +x ./mvnw
RUN ./mvnw clean package

ENV JAR_PATH=/app/target
RUN mv ${JAR_PATH}/*.jar /app/app.jar

ENTRYPOINT [ "java", "-jar", "app.jar" ]
