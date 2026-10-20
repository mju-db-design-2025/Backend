# 데이터베이스 설계 프로젝트

명지대학교 2025-2 데이터베이스 설계 프로젝트

## 프로젝트 개요

Spring Boot 기반의 RESTful API 프로젝트입니다. 데이터베이스 상태 확인 및 관리 기능을 제공합니다.

프로젝트 등록과 투입 인력 조회, 개발자 추천, 발주처 목록 조회, 동료 평가 작성·조회 기능을 통해 프로젝트와 인력 평가 데이터를 관리합니다. 개발자 추천은 경력, 기술등급, 보유 기술, 직무별 평가 데이터를 바탕으로 적합도를 계산합니다.

## 기술 스택

- **Java**: 17
- **Spring Boot**: 4.0.0
- **Build Tool**: Gradle
- **Database**: MySQL
- **ORM**: Spring Data JPA
- **API Documentation**: Swagger (SpringDoc OpenAPI)
- **Gradle Wrapper**: 8.14
- **SpringDoc OpenAPI**: 2.5.0 (`build.gradle` 선언 기준)
- **기타**: Lombok, MySQL Connector/J, JUnit Platform

## 주요 기능

- 데이터베이스 상태 확인 API
- Swagger UI를 통한 API 문서화 및 테스트
- 프로젝트 등록 및 적합도 상위 5명의 개발자 자동 추천
- 기간·상태별 프로젝트 목록 및 프로젝트별 투입 인력·보유 기술 조회
- 전체 프로젝트 수, 진행·완료 비율, 중복을 제거한 투입 인력 수 조회
- 발주처 목록 조회
- 평가자별 동료 평가 목록 조회, 항목별 점수·서술형 평가 저장 및 상세 조회

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

요청은 `controller → service → repository` 순서로 처리되며, `domain`은 JPA 테이블 매핑, `dto`는 API 요청·응답 형식을 정의합니다.

| 계층 | 주요 클래스 | 역할 |
| --- | --- | --- |
| config | `OpenApiConfig` | OpenAPI 문서 설정 |
| controller | `ProjectController`, `ClientController`, `PeerEvaluationController`, `HealthController` | API 경로와 HTTP 응답 정의 |
| service | `ProjectService`, `DeveloperRecommendationService`, `PeerEvaluationService`, `ClientService`, `HealthService` | 프로젝트·추천·평가·상태 확인 로직 |
| repository | `ProjectRepository`, `DeveloperRepository`, `EvaluationRepository` 등 | JPA 기반 조회·저장 |

공통 설정은 `src/main/resources/application.yml`, 비공개 DB 설정은 `application-secret.yml`에서 관리합니다.

## 실행 방법

### 1. 사전 요구사항

- JDK 17 (Gradle Java Toolchain 기준)
- MySQL 8.0 이상
- Gradle (또는 Gradle Wrapper)

저장소에 Gradle Wrapper가 포함되어 있어 Gradle을 별도로 설치하지 않아도 됩니다. 최초 실행 시 Gradle 배포판과 의존성을 다운로드할 네트워크 연결이 필요합니다. `java -version`으로 JDK 설정을 확인합니다.

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

   > ⚠️ **주의**: `application-secret.yml` 파일은 `.gitignore`에 포함해야 한다. 현재 저장소에는 해당 제외 규칙이 등록되어 있습니다.

기본 연결 대상은 `localhost:3306/project_db`, 사용자 이름은 `root`입니다. 다른 서버나 계정을 사용한다면 위 파일에 `spring.datasource.url`, `spring.datasource.username`도 함께 지정합니다.

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/project_db?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
    username: your_username
    password: your_password
```

`application-secret.yml`은 `spring.config.import`의 `optional` 설정으로 읽습니다. 파일이 없어도 설정 파일 로딩 자체는 진행되지만, 실제 DB 접속에 필요한 비밀번호는 별도로 제공해야 합니다. 환경 변수 `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`로도 연결 정보를 지정할 수 있습니다.

JPA의 `ddl-auto`는 `update`, SQL 출력은 `show-sql: true`로 설정되어 있습니다. 애플리케이션 시작 시 엔티티 매핑에 따라 스키마를 갱신하므로, 접속 계정에 필요한 테이블 생성·변경 권한이 있어야 합니다.

### 초기 데이터 준비

저장소에는 초기 데이터 SQL이나 샘플 데이터 로더가 포함되어 있지 않습니다. 테이블 생성과 실제 업무 데이터 준비는 별도입니다.

- 프로젝트 등록: `client`에 등록된 발주처의 `clientId`를 사용합니다. 발주처 목록은 `GET /api/clients`로 확인합니다.
- 개발자 추천: `employee`, `developer`, `job`, `technology`, `developer_skill` 데이터를 준비합니다. 직무별 평가를 반영하려면 프로젝트 참여 및 평가 데이터도 필요합니다.
- 동료 평가 작성: 프로젝트, 평가자·평가 대상 직원, `evaluation_item`의 평가 항목이 먼저 존재해야 합니다.

현재 API에는 발주처·직원·직무·기술·평가 항목을 등록하는 경로가 없으므로, 해당 기준 데이터는 DB에서 별도로 준비합니다.

### 3. 애플리케이션 실행

```bash
# Gradle Wrapper 사용
./gradlew bootRun

# 또는 빌드 후 실행
./gradlew build
java -jar build/libs/db-project-0.0.1-SNAPSHOT.jar
```

Windows PowerShell에서는 저장소 루트에서 다음 명령을 사용합니다.

```powershell
.\gradlew.bat bootRun

# 또는 빌드 후 실행
.\gradlew.bat build
java -jar build/libs/db-project-0.0.1-SNAPSHOT.jar
```

포트는 `PORT` 환경 변수로 변경할 수 있으며, 기본값은 `8080`입니다.

```powershell
$env:PORT = "8081"
.\gradlew.bat bootRun
```

### 4. 접속 정보

- **애플리케이션**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

애플리케이션 주소는 기본 서버 주소이며, `/`에 별도의 화면이나 API는 구현되어 있지 않습니다. 실행 확인은 `/api/health` 또는 Swagger UI에서 진행합니다. 포트를 변경했다면 접속 URL도 함께 변경합니다.

## API 사용 방법

모든 경로는 기본 주소 `http://localhost:8080`을 기준으로 합니다. POST 요청 본문은 `Content-Type: application/json`으로 전송합니다. 아래 예시의 ID와 직무·기술 이름은 실제 DB 데이터에 맞게 변경해야 합니다.

| 메서드 | 경로 | 설명 | 정상 응답 |
| --- | --- | --- | --- |
| GET | `/api/health` | DB 연결 상태 확인 | 200 |
| GET | `/api/clients` | 발주처 ID·이름 목록 | 200 |
| POST | `/api/projects` | 프로젝트 등록 및 개발자 추천 | 201 |
| GET | `/api/projects` | 기간·상태별 프로젝트 목록 | 200 |
| GET | `/api/projects/{projectId}` | 프로젝트 상세 및 투입 인력 | 200 |
| GET | `/api/projects/dashboard` | 프로젝트 통계 및 총 투입 인력 | 200 |
| GET | `/api/peer-evaluations/list?evaluatorId={id}` | 평가자가 작성한 동료 평가 목록 | 200 |
| POST | `/api/peer-evaluations?evaluatorId={id}` | 동료 평가 저장 | 201 |
| GET | `/api/peer-evaluations/{evaluationId}` | 동료 평가 상세 | 200 |

### 1. 데이터베이스 상태 확인

```bash
curl http://localhost:8080/api/health
```

정상 응답 예시입니다. 시간과 응답 속도는 실행 시점에 따라 달라집니다.

```json
{
  "status": "UP",
  "database": "healthy",
  "message": "Database connection is healthy",
  "timestamp": "2025-12-01T10:00:00",
  "responseTime": "3ms"
}
```

연결 확인에 실패하면 본문의 `status`는 `DOWN`, `database`는 `unhealthy`가 됩니다. 현재 컨트롤러는 이 경우에도 HTTP 200을 반환하므로, 상태 확인 시 응답 본문을 함께 확인합니다. 애플리케이션 자체가 DB 연결 문제로 시작하지 못하면 이 API에도 접속할 수 없습니다.

### 2. 프로젝트 등록 및 개발자 추천

`POST /api/projects` 요청 본문 예시:

```json
{
  "projectName": "프로젝트 관리 시스템 구축",
  "startDate": "2025-12-01",
  "endDate": "2026-02-28",
  "clientId": 1,
  "requiredJobNames": ["백엔드", "프론트엔드"],
  "requiredTechNames": ["Java", "Spring", "React"]
}
```

| 필드 | 형식 | 설명 |
| --- | --- | --- |
| projectName | 문자열 | 프로젝트명, DB에서 NULL 허용 안 함 |
| startDate | `YYYY-MM-DD` | 시작일, DB에서 NULL 허용 안 함 |
| endDate | `YYYY-MM-DD` 또는 null | 종료일, 미정이면 null |
| clientId | 정수 | 기존 발주처 ID, DB에서 NULL 허용 안 함 |
| requiredJobNames | 문자열 배열 | 추천에 사용할 직무 이름 |
| requiredTechNames | 문자열 배열 | 추천에 사용할 기술 이름 |

응답에는 생성된 `projectId`, 프로젝트 기본 정보, `recommendedDevelopers` 배열이 포함됩니다. 각 추천 항목은 직원 ID·이름, 직무 코드·이름, 적합도(`fitScore`), 추천 근거(`recommendReasons`), 보유 기술(`skills`), 경력과 기술등급을 제공합니다.

- 직무·기술 이름은 DB에서 이름으로 조회하며, 찾지 못한 이름은 코드 변환 결과에서 제외됩니다.
- 변환된 직무 코드가 없으면 전체 직무를 대상으로 추천합니다. 기술 코드가 없으면 보유 기술 수를 기준으로 점수를 계산합니다.
- 개발자 또는 대상 직무 데이터가 없으면 추천 목록은 빈 배열이 될 수 있습니다.
- 추천 결과가 실제 프로젝트 투입 인력으로 자동 등록되지는 않습니다.

### 3. 프로젝트 조회 및 대시보드

```bash
curl "http://localhost:8080/api/projects?period=THIS_MONTH&status=ING"
curl http://localhost:8080/api/projects/1
curl http://localhost:8080/api/projects/dashboard
```

| 파라미터 | 값 | 동작 |
| --- | --- | --- |
| period | `ALL`, `THIS_MONTH`, `THIS_QUARTER` | 전체 범위, 이번 달, 이번 분기 |
| status | `ALL`, `ING`, `DONE` | 전체, 진행중, 완료 |

필터는 생략할 수 있습니다. 기간 필터는 조회 기간과 프로젝트 수행 기간이 겹치는 항목을 찾으며, 전체 범위는 코드상 `1900-01-01`부터 `2099-12-31`까지입니다. 현재 월·분기와 상태 계산에는 서버의 현재 날짜를 사용합니다.

목록·상세의 `projectStatus`는 시작일 전이면 `준비중`, 종료일이 지났으면 `완료`, 그 외에는 `진행중`입니다. 종료일 당일은 진행중으로 처리합니다. 목록에는 인력 수(`totalPeople`), 상세에는 직원명·직무명·투입 기간·보유 기술을 담은 `members`가 포함됩니다.

대시보드는 `totalProjects`, `ingProjectPercent`, `doneProjectPercent`, `totalDevelopers`를 반환합니다. 비율은 소수점 첫째 자리까지 계산하며, `totalDevelopers`는 프로젝트 참여 데이터에서 중복을 제거한 직원 수입니다. 현재 대시보드의 진행 집계는 종료일이 오늘 이후이거나 미정인 프로젝트를 포함하므로, 목록에서 `준비중`인 프로젝트도 진행 비율에 포함될 수 있습니다.

### 4. 동료 평가 작성 및 조회

`POST /api/peer-evaluations?evaluatorId=1` 요청 본문 예시:

```json
{
  "targetEmpId": 2,
  "projectId": 1,
  "details": [
    {
      "itemId": 1,
      "score": 85.5,
      "narrative": "요구사항을 정확하게 이해하고 일정에 맞춰 구현했습니다."
    },
    {
      "itemId": 2,
      "score": 90,
      "narrative": "진행 상황과 이슈를 팀에 적극적으로 공유했습니다."
    }
  ]
}
```

`evaluatorId`는 평가자 직원 ID, `targetEmpId`는 평가 대상 직원 ID입니다. `details`에는 기존 평가 항목의 `itemId`, 숫자 점수 `score`, 서술 내용 `narrative`를 전달합니다. 저장 성공 시 HTTP 201과 생성된 평가 ID(정수)를 반환하며, 평가 유형은 `동료`, 제출 시각은 저장 시 자동 설정됩니다.

```bash
curl "http://localhost:8080/api/peer-evaluations/list?evaluatorId=1"
curl http://localhost:8080/api/peer-evaluations/1
```

목록에는 평가 ID, 프로젝트명, 대상 직원명, 평가 유형, 제출 시각이 포함됩니다. 상세에는 프로젝트 ID, 평가자 이름, 항목별 점수와 서술 내용이 추가됩니다. 현재 별도의 로그인·인증 API는 구현되어 있지 않으며, `evaluatorId`는 요청 파라미터로 직접 전달합니다.

## 개발자 추천 기준

`DeveloperRecommendationService`는 각 개발자에 대해 대상 직무별 점수를 비교하고 가장 적합한 직무를 선택한 뒤, 점수 내림차순으로 최대 5명을 반환합니다.

```text
적합도 = 기본 점수 × 0.3 + 기술 점수 × 0.3 + 직무별 평가 점수 × 0.4
```

- 기본 점수: 경력 1년당 5점(최대 50점)과 기술등급 점수를 합산합니다. 등급 점수는 S/시니어 50점, A/시니어+ 45점, B/미들 35점, C/주니어+ 25점, D/주니어 15점이며, 그 외 등급은 10점, 등급 정보가 없으면 0점입니다.
- 기술 점수: 요구 기술이 있으면 매칭률 기반 점수와 보유 기술 수 기반 보정값을 비교합니다. 요구 기술이 없으면 보유 기술당 10점, 최대 50점입니다.
- 직무별 평가 점수: 해당 직무의 평가 데이터를 조회하며, 평가가 없거나 조회에 실패하면 기본값 50점을 사용합니다.

추천 내역은 해당 프로젝트에 평가 데이터가 있을 때 DB 저장을 시도합니다. 신규 프로젝트에 평가가 없으면 추천 응답은 반환하되 추천 내역 저장은 건너뜁니다.

## 빌드 및 테스트

```bash
./gradlew build
./gradlew test
```

Windows에서는 각각 `.\gradlew.bat build`, `.\gradlew.bat test`를 사용합니다. 빌드 산출물은 `build/libs/db-project-0.0.1-SNAPSHOT.jar`, 테스트 HTML 보고서는 `build/reports/tests/test/index.html`입니다.

현재 테스트는 `@SpringBootTest` 기반의 `contextLoads()`로 애플리케이션 컨텍스트 로딩을 확인합니다. 별도의 테스트용 DB 설정이 없으므로, 테스트 및 테스트를 포함하는 `build` 실행 시 접근 가능한 MySQL과 연결 설정이 필요합니다. API별 동작은 Swagger UI 또는 위 요청 예시로 별도로 확인합니다.

## 실행 시 확인 사항

| 증상 | 확인할 내용 |
| --- | --- |
| DB 연결 거부 또는 통신 오류 | MySQL 실행 여부, 호스트·3306 포트, DB URL |
| `Unknown database 'project_db'` | DB 생성 여부와 연결 URL의 DB 이름 |
| `Access denied` | 사용자 이름, 비밀번호, DB 접근 권한 |
| 기본 포트 사용 중 | 기존 프로세스를 확인하거나 `PORT` 환경 변수 변경 |
| 프로젝트 등록·평가 저장 실패 | 참조하는 발주처·프로젝트·직원·평가 항목 존재 여부와 필수 값 |
| 추천 목록이 비어 있음 | 개발자·직무 기준 데이터와 추천 처리 로그 |

프로젝트와 동료 평가 ID는 현재 최대 ID에 1을 더하는 방식으로 생성합니다. 동시 등록 시 ID 충돌 가능성이 있으며, 요청 값 검증과 예외의 HTTP 상태 매핑도 별도로 구현되어 있지 않습니다. 따라서 잘못된 ID가 반드시 404로 반환되거나 점수 범위가 자동 검증된다고 가정하지 않아야 합니다.

