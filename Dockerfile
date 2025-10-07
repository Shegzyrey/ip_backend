# ---------- Build stage ----------
# Use a JDK 25 Maven image for the build
FROM maven:eclipse-temurin AS build
# Safer defaults
ARG MAVEN_OPTS="-Dmaven.wagon.http.pool=false -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn"
ENV MAVEN_CONFIG=/root/.m2

WORKDIR /workspace

# Leverage Docker layer caching: copy pom first, download deps
COPY pom.xml ./
# Use a cache mount for the Maven repo to speed up builds
RUN --mount=type=cache,target=/root/.m2 mvn -q -e -DskipTests dependency:go-offline

# Now add sources and build
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -q -e -DskipTests package

# ---------- Runtime stage ----------
# Use a JRE 25 image for the runtime
FROM eclipse-temurin:25_36-jre-noble

# Create a non-root user and data dir for SQLite
# -G root so we can set 0750 on dirs; no shell for the user
RUN useradd -r -u 10001 -g root appuser \
    && mkdir -p /app /data \
    && chown -R appuser:root /app /data \
    && chmod 0750 /app /data

WORKDIR /app

# Copy the fat jar from build stage
# (Assumes a single jar in target; adjust if you have a fixed name)
COPY --from=build /workspace/target/*.jar /app/app.jar

# Runtime hardening
USER appuser
ENV SPRING_PROFILES_ACTIVE=prod \
    SPRING_DATASOURCE_URL=jdbc:sqlite:/data/ip_backend.db \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC -Dfile.encoding=UTF-8 -Djava.security.egd=file:/dev/urandom"

# Document the listen port (Spring default 8080)
EXPOSE 8080

# Persist DB outside the image
VOLUME ["/data"]

# No shell, no extra packages
ENTRYPOINT ["java","-jar","/app/app.jar"]
