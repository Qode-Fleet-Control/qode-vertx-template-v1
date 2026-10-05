# Built by .github/workflows/deploy.yml and pushed to Artifact Registry.
#
# Two stages: Maven builds the shaded fat jar (start.vertx.io's shade setup), a
# JRE-only image runs it as a non-root user. MainVerticle reads $PORT at
# RUNTIME, never baked in.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY pom.xml ./
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q package -DskipTests && cp target/app-1.0.0-SNAPSHOT-fat.jar /app.jar

FROM eclipse-temurin:21-jre AS runtime
ARG BUILD_ID=""
WORKDIR /app
ENV PORT=8888 BUILD_ID=$BUILD_ID
RUN useradd -r -u 10001 app
COPY --from=build /app.jar /app/app.jar
USER app
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
