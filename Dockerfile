FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# 빌드된 Spring Boot fat jar 파일 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 타임존 설정 및 기본 포트 노출
ENV TZ=Asia/Seoul
EXPOSE 8080

# prod 프로필로 애플리케이션 실행
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-Duser.timezone=Asia/Seoul", "-jar", "/app/app.jar"]
