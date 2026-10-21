
# Build stage
FROM eclipse-temurin:21.0.12_8-jdk-noble AS build

WORKDIR /restaurante

COPY . .

RUN sed -i 's/\r$//' ./mvnw && chmod +x ./mvnw

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -f /restaurante/pom.xml clean package -DskipTests

# Execution stage
FROM eclipse-temurin:21.0.12_8-jre-noble

ARG JAR_FILE=/restaurante/target/*.jar

COPY --from=build ${JAR_FILE} /restaurante/runner.jar

# Non-root user
RUN groupadd -r owneruser && useradd -r -g owneruser owneruser

USER owneruser

ENTRYPOINT ["java", "-jar", "/restaurante/runner.jar"]