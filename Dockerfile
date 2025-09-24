# Etapa de build (Maven actualizado para estabilidad en Render)
FROM maven:3.9-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests  # Extrae newrelic.jar y newrelic.yml a target/

# Etapa de runtime ((Eclipse Temurin como alternativa a OpenJDK para evitar rate limits)
FROM eclipse-temurin:17-jdk
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Instala dependencias básicas para el CLI (curl, bash ya están en slim)
RUN apt-get update && apt-get install -y curl gnupg && rm -rf /var/lib/apt/lists/*

# Copia el JAR de la app desde build
COPY --from=build /app/target/Agregador-1.0-SNAPSHOT.jar app.jar

# Copia los archivos de New Relic Agent extraídos (ajusta si el plugin usa subdir)
COPY --from=build /app/target/newrelic.jar newrelic.jar
COPY --from=build /app/target/newrelic.yml newrelic.yml  # Opcional; quita si configuras via env

# Instala New Relic CLI y logs-integration (usa env vars para auth)
RUN curl -LsSf https://download.newrelic.com/install/newrelic-cli/scripts/install.sh | bash && \
    newrelic install -n logs-integration --apiKey ${NEW_RELIC_API_KEY} --accountId ${NEW_RELIC_ACCOUNT_ID}

# Env vars para runtime (se sobreescriben en Render)
ENV NEW_RELIC_LICENSE_KEY=""
ENV NEW_RELIC_API_KEY=""
ENV NEW_RELIC_ACCOUNT_ID=""
ENV NEW_RELIC_APP_NAME="Agregador API"

EXPOSE 8080

# ENTRYPOINT: Java Agent para APM + CLI maneja logs/infrastructure
ENTRYPOINT ["java", "-javaagent:/app/newrelic.jar", "-Dnewrelic.config.file=/app/newrelic.yml", "-Dnewrelic.license.key=${NEW_RELIC_LICENSE_KEY}", "-Dnewrelic.app.name=${NEW_RELIC_APP_NAME}", "-Dnewrelic.log_file_name=STDOUT", "-jar", "app.jar"]