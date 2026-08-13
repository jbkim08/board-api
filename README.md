# Board API

Spring Boot와 MyBatis로 구현한 게시판 REST API입니다.
JWT 기반 회원 인증, 게시글 검색과 페이징, 댓글, 첨부파일 업로드 기능을 제공합니다.

## 기술 스택

- Java 21
- Spring Boot 3.5.16
- Gradle 9.5.1
- Spring Web / Validation / Security
- MyBatis 3.0.5
- MySQL
- JJWT 0.12.5
- springdoc-openapi 2.8.16
- Lombok
- JUnit 5 / Mockito

## 주요 기능

- 회원가입, 로그인, Access Token 및 Refresh Token 발급
- Refresh Token Rotation 방식의 토큰 재발급
- 게시글 목록 조회, 제목·내용 검색, 페이징
- 게시글 상세 조회 및 조회수 증가
- 인증 사용자 게시글 등록·수정·삭제
- 댓글 등록·조회·삭제
- 다중 첨부파일 업로드 및 정적 파일 조회
- 작성자 본인만 게시글과 댓글 변경 가능
- 공통 예외 응답 및 요청값 검증
- Swagger UI를 통한 API 문서 확인

## 프로젝트 구조

```text
src/main/java/com/example/board
├── config       # Spring Security, CORS, 정적 리소스 설정
├── controller   # 인증, 게시글, 댓글 API
├── domain       # User, Board, Comment, Attachment
├── dto          # 요청 및 응답 객체
├── exception    # 공통 예외 처리
├── mapper       # MyBatis 매퍼 인터페이스
├── security     # JWT 발급·검증 및 인증 필터
└── service      # 인증, 게시글, 댓글, 파일 비즈니스 로직

src/main/resources
├── application.yaml
├── schema.sql
├── data.sql
└── mapper       # MyBatis XML 매퍼
```

## 실행 환경

MySQL에 `board` 데이터베이스를 생성합니다.

```sql
CREATE DATABASE board
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

최초 구성 시 다음 파일을 순서대로 실행합니다.

1. `src/main/resources/schema.sql`
2. `src/main/resources/data.sql` (샘플 데이터가 필요한 경우)

`schema.sql`은 기존 테이블을 삭제한 뒤 다시 생성하므로 운영 데이터가 있는 환경에서는 그대로 실행하면 안 됩니다.
현재 `spring.sql.init.mode`가 `never`이므로 애플리케이션 시작 시 SQL 파일이 자동 실행되지 않습니다.

기본 개발 설정은 다음과 같습니다.

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/board?serverTimezone=Asia/Seoul
    username: root
    password: 1234
```

DB 접속 정보와 파일 저장 경로는 환경에 맞게 `application.yaml`에서 변경해야 합니다.
운영 환경에서는 반드시 `JWT_SECRET` 환경 변수를 충분히 긴 무작위 값으로 설정해야 합니다.

```powershell
$env:JWT_SECRET = "replace-with-a-secure-random-secret"
```

첨부파일은 기본적으로 다음 경로에 저장됩니다.

```text
C:/java/springboot/board/uploads/
```

## 실행

Windows PowerShell:

```powershell
.\gradlew.bat bootRun
```

macOS/Linux:

```bash
./gradlew bootRun
```

서버는 기본적으로 `http://localhost:8080`에서 실행됩니다.

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

## API

### 인증

| Method | URL | 인증 | 설명 |
| --- | --- | --- | --- |
| POST | `/api/auth/signup` | 불필요 | 회원가입 |
| POST | `/api/auth/login` | 불필요 | 로그인 및 토큰 발급 |
| POST | `/api/auth/refresh` | 불필요 | Access/Refresh Token 재발급 |

### 게시글

| Method | URL | 인증 | 설명 |
| --- | --- | --- | --- |
| GET | `/api/boards?keyword={keyword}&page=1&size=10` | 불필요 | 게시글 검색 및 목록 조회 |
| GET | `/api/boards/{id}` | 불필요 | 게시글 상세 조회, 조회수 증가 |
| POST | `/api/boards` | 필요 | 게시글 및 첨부파일 등록 |
| PUT | `/api/boards/{id}` | 필요 | 본인 게시글 수정 |
| DELETE | `/api/boards/{id}` | 필요 | 본인 게시글 삭제 |

게시글을 삭제하면 연결된 댓글과 첨부파일 DB 정보도 함께 삭제됩니다.
서버에 저장된 실제 첨부파일도 삭제됩니다.

### 댓글

| Method | URL | 인증 | 설명 |
| --- | --- | --- | --- |
| GET | `/api/boards/{boardId}/comments` | 불필요 | 게시글 댓글 조회 |
| POST | `/api/boards/{boardId}/comments` | 필요 | 댓글 등록 |
| DELETE | `/api/comments/{id}` | 필요 | 본인 댓글 삭제 |

### 첨부파일

| Method | URL | 인증 | 설명 |
| --- | --- | --- | --- |
| GET | `/uploads/{storedName}` | 불필요 | 업로드된 파일 조회 |

## 요청 예시

### 회원가입

```http
POST /api/auth/signup
Content-Type: application/json

{
  "username": "user01",
  "password": "password1234",
  "nickname": "사용자1"
}
```

### 로그인

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "user01",
  "password": "password1234"
}
```

로그인 응답의 `accessToken`을 인증이 필요한 요청에 전달합니다.

```http
Authorization: Bearer {accessToken}
```

### 게시글 등록

게시글 등록은 JSON 단독 요청이 아니라 `multipart/form-data`를 사용합니다.
`board` 파트에는 JSON을, `files` 파트에는 첨부파일을 전달합니다.

```powershell
curl.exe -X POST "http://localhost:8080/api/boards" `
  -H "Authorization: Bearer {accessToken}" `
  -F 'board={"title":"첫 번째 게시글","content":"게시글 내용입니다."};type=application/json' `
  -F "files=@C:\images\sample.jpg"
```

파일이 없으면 `files` 파트를 생략할 수 있습니다.
현재 파일 한 개의 최대 크기는 10MB, 요청 전체 최대 크기는 30MB입니다.

### 게시글 수정

```http
PUT /api/boards/1
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "title": "수정된 제목",
  "content": "수정된 게시글 내용입니다."
}
```

### 댓글 등록

```http
POST /api/boards/1/comments
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "content": "댓글 내용입니다."
}
```

### 토큰 재발급

```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "{refreshToken}"
}
```

## 오류 응답

오류는 다음과 같은 공통 형식으로 반환됩니다.

```json
{
  "timestamp": "2026-08-13T17:00:00",
  "status": 404,
  "message": "게시글을 찾을 수 없습니다. id=1",
  "errors": null
}
```

| 상황 | HTTP Status |
| --- | --- |
| 요청값 검증 실패 | `400 Bad Request` |
| 로그인 또는 토큰 재발급 실패 | `401 Unauthorized` |
| 본인 소유가 아닌 게시글·댓글 변경 | `403 Forbidden` |
| 게시글·댓글·URL을 찾을 수 없음 | `404 Not Found` |
| 중복 아이디로 회원가입 | `409 Conflict` |
| 처리되지 않은 서버 오류 | `500 Internal Server Error` |

## 테스트

```powershell
.\gradlew.bat test
```

전체 컨텍스트 테스트는 설정된 MySQL에 연결할 수 있어야 합니다.

## 개발 시 참고사항

- `application.yaml`의 DB 비밀번호와 기본 JWT Secret은 로컬 개발용입니다.
- CORS는 현재 모든 Origin을 허용하므로 운영 배포 전에 프론트엔드 도메인으로 제한해야 합니다.
- `uploads/` 디렉터리의 파일은 Git에서 자동 제외되지 않습니다.
- 게시글과 댓글의 작성자 권한은 현재 닉네임을 기준으로 판단합니다. 닉네임 중복에 따른 권한 문제를 막으려면 사용자 ID 기반으로 변경해야 합니다.
