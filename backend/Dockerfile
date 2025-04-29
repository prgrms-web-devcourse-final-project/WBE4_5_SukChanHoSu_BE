# 첫 번째 스테이지: 빌드 스테이지
FROM gradle:jdk-21-and-23-graal-jammy AS builder

# 작업 디렉토리 설정
WORKDIR /app

# 소스 코드와 Gradle 래퍼 복사
COPY build.gradle .
COPY settings.gradle .

# Gradle 래퍼에 실행 권한 부여
RUN gradle wrapper

# 종속성 설치
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY src src

# 'ARG' 예약어를 통해 인자로 전달 받은 값을 환경 변수로 설정
ARG DATABASE_URL
ARG DATABASE_USERNAME
ARG DATABASE_PASSWORD
ARG JWT_SECRET
ARG GOOGLE_CLIENT_ID
ARG GOOGLE_SECURITY_PASSWORD

# 'ENV' 예약어를 통해 전달받은 값을 실제 값과 매칭시켜야 한다.
ENV DATABASE_URL=${DATABASE_URL} \
    DATABASE_USERNAME=${DATABASE_USERNAME} \
    DATABASE_PASSWORD=${DATABASE_PASSWORD} \
    JWT_SECRET=${JWT_SECRET} \
    GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID} \
    GOOGLE_SECURITY_PASSWORD=${GOOGLE_SECURITY_PASSWORD}

# 애플리케이션 빌드
RUN ./gradlew build --no-daemon

# 이후 명령어가 편하도록 불필요한 파일 삭제
RUN rm -rf /app/build/libs/*-plain.jar

# 두 번째 스테이지: 실행 스테이지
FROM container-registry.oracle.com/graalvm/jdk:21

# 작업 디렉토리 설정
WORKDIR /app

# 첫 번째 스테이지에서 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 실행할 JAR 파일 지정
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]