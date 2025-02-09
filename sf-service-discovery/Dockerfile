FROM gradle:jdk-21-and-23-alpine AS GRADLE_TOOL_CHAIN
#FROM eclipse-temurin:23-jdk-alpine AS GRADLE_TOOL_CHAIN

WORKDIR /
COPY . .

RUN ./gradlew build


FROM eclipse-temurin:23-jdk-alpine

WORKDIR /app

COPY --from=GRADLE_TOOL_CHAIN build/libs/*.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]
