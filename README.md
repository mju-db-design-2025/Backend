# 데이터베이스 설계 프로젝트

명지대학교 2025-2 데이터베이스 설계 프로젝트

## 프로젝트 개요

Spring Boot 기반의 RESTful API 프로젝트입니다. 데이터베이스 상태 확인 및 관리 기능을 제공합니다.

## 기술 스택

- **Java**: 17
- **Spring Boot**: 4.0.0
- **Build Tool**: Gradle
- **Database**: MySQL
- **ORM**: Spring Data JPA
- **API Documentation**: Swagger (SpringDoc OpenAPI)

## 주요 기능

- 데이터베이스 상태 확인 API
- Swagger UI를 통한 API 문서화 및 테스트

## 프로젝트 구조

```
src/main/java/com/mju/db_project/
├── config/          # 설정 클래스
├── controller/      # REST 컨트롤러
├── service/         # 비즈니스 로직
├── repository/      # 데이터 접근 계층
├── domain/          # 엔티티 클래스
└── dto/             # 데이터 전송 객체
    ├── request/     # 요청 DTO
    └── response/    # 응답 DTO
```

## 실행 방법

### 1. 사전 요구사항

- Java 17 이상
- MySQL 8.0 이상
- Gradle (또는 Gradle Wrapper)

### 2. 데이터베이스 설정

1. MySQL에 데이터베이스 생성:
```sql
CREATE DATABASE project_db;
```

2. `src/main/resources/application-secret.yml` 파일 생성:
```yaml
spring:
  datasource:
    password: your_password
```

   > ⚠️ **주의**: `application-secret.yml` 파일은 `.gitignore`에 포함해야 한다.

### 3. 애플리케이션 실행

```bash
# Gradle Wrapper 사용
./gradlew bootRun

# 또는 빌드 후 실행
./gradlew build
java -jar build/libs/db-project-0.0.1-SNAPSHOT.jar
```

### 4. 접속 정보

- **애플리케이션**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

