# ====== Build stage ======
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Leverage Docker layer caching: first copy pom, then src
COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2 mvn -q -e -DskipTests dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -q -e -DskipTests clean package

# ====== Runtime stage ======
FROM eclipse-temurin:17-jre
WORKDIR /app

# Non-root user for better security
RUN useradd -r -u 1001 appuser
USER appuser

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Copy shaded or boot jar
COPY --from=build /app/target/*-SNAPSHOT.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app/app.jar" ]
