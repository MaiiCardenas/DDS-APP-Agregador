# Importing JDK and copying required files
#FROM maven:3.8.6-openjdk-18 AS build
#COPY . .
#run mvn clean package -DskipTests

#FROM openjdk:17-jdk-slim
#copy --from=build /target/Agregador-1.0-SNAPSHOT.jar app.jar
#EXPOSE 8080
#ENTRYPOINT ["java","-jar","/app.jar"]
##########
# Etapa de build
FROM maven:3.8.6-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests  # Extrae newrelic.jar y newrelic.yml a target/

# Etapa de runtime
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copia el JAR de la app
COPY --from=build /app/target/Agregador-1.0-SNAPSHOT.jar app.jar

# Copia los archivos de New Relic extraídos (directamente desde target/)
COPY --from=build /app/target/newrelic.jar newrelic.jar
COPY --from=build /app/target/newrelic.yml newrelic.yml  # Opcional, si usas el YML

EXPOSE 8080

# ENTRYPOINT: Usa los archivos copiados, con env vars
ENTRYPOINT ["java", "-javaagent:/app/newrelic.jar", "-Dnewrelic.config.file=/app/newrelic.yml", "-Dnewrelic.license.key=${NEW_RELIC_LICENSE_KEY}", "-Dnewrelic.app.name=${NEW_RELIC_APP_NAME:-Agregador API}", "-jar", "app.jar"]

RUN curl -LsSf https://download.newrelic.com/install/newrelic-cli/scripts/install.sh | bash && \
    newrelic install -n logs-integration --apiKey ${NEW_RELIC_API_KEY} --accountId ${NEW_RELIC_ACCOUNT_ID}
ENV NEW_RELIC_API_KEY=4667fb5ad9096e45e1cb45f98af902e3FFFFNRAL
ENV NEW_RELIC_ACCOUNT_ID=7163391