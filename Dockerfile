# Etapa de build: Usa Maven con Eclipse Temurin 21
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de runtime: Usa Eclipse Temurin 21
FROM eclipse-temurin:21-jdk
COPY --from=build /app/target/Agregador-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]