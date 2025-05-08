# 첫 번째 스테이지: 빌드 스테이지
FROM gradle:jdk-21-and-23-graal-jammy AS builder

WORKDIR /app

COPY build.gradle .
COPY settings.gradle .

RUN gradle wrapper

RUN ./gradlew dependencies --no-daemon

COPY src src

COPY src/main/resources/application-secret.yml src/main/resources/

RUN ./gradlew build -x test --no-daemon

RUN rm -rf /app/build/libs/*-plain.jar

# 두 번째 스테이지: 실행 스테이지
FROM container-registry.oracle.com/graalvm/jdk:21

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]