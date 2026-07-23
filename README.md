# Board API 📝

Spring Boot와 MyBatis로 만든 간단한 게시판 REST API 프로젝트입니다.  
게시글 목록 조회, 상세 조회, 등록, 수정, 삭제 기능을 제공하며 MySQL 데이터베이스를 사용합니다.

## ✨ 주요 기능

- 📄 게시글 목록 조회
- 🔎 제목/내용 키워드 검색
- 📚 페이지네이션
- 👀 게시글 상세 조회 시 조회수 증가
- ✍️ 게시글 등록
- 🛠️ 게시글 수정
- 🗑️ 게시글 삭제
- 🚨 공통 예외 처리
- ✅ 요청 값 검증

## 🧰 기술 스택

- Java 21
- Spring Boot 3.5.16
- Gradle
- Spring Web
- MyBatis
- MySQL
- Lombok
- JUnit 5

## 📁 프로젝트 구조

```text
src/main/java/com/example/board
├── BoardApplication.java
├── controller
│   └── BoardController.java
├── domain
│   └── Board.java
├── dto
│   ├── BoardCreateRequest.java
│   ├── BoardResponse.java
│   ├── BoardUpdateRequest.java
│   └── PageResponse.java
├── exception
│   ├── BoardNotFoundException.java
│   ├── ErrorResponse.java
│   └── GlobalExceptionHandler.java
├── mapper
│   └── BoardMapper.java
└── service
    └── BoardService.java

src/main/resources
├── application.yaml
├── schema.sql
├── data.sql
└── mapper
    └── BoardMapper.xml
```

## ⚙️ 실행 환경

MySQL에 `board` 데이터베이스가 필요합니다.

```sql
CREATE DATABASE board;
```

기본 DB 접속 설정은 `src/main/resources/application.yaml`에 있습니다.

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/board?serverTimezone=Asia/Seoul
    username: root
    password: 1234
```

테이블은 `schema.sql` 기준으로 생성합니다.

```sql
CREATE TABLE board (
   id          BIGINT AUTO_INCREMENT PRIMARY KEY,
   title       VARCHAR(200)  NOT NULL,
   content     TEXT          NOT NULL,
   writer      VARCHAR(50)   NOT NULL,
   view_count  INT           DEFAULT 0,
   created_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
   updated_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);
```

## 🚀 실행 방법

Windows PowerShell 기준:

```powershell
.\gradlew.bat bootRun
```

macOS/Linux 기준:

```bash
./gradlew bootRun
```

서버는 기본적으로 `http://localhost:8080`에서 실행됩니다.

## 🧪 테스트

```powershell
.\gradlew.bat test
```

## 🌐 API 목록

기본 경로는 `/api/boards`입니다.

| Method | URL | 설명 |
| --- | --- | --- |
| GET | `/api/boards?keyword={keyword}&page=1&size=10` | 게시글 목록 조회, 검색, 페이지네이션 |
| GET | `/api/boards/{id}` | 게시글 상세 조회 및 조회수 증가 |
| POST | `/api/boards` | 게시글 등록 |
| PUT | `/api/boards/{id}` | 게시글 수정 |
| DELETE | `/api/boards/{id}` | 게시글 삭제 |

## 📬 요청 예시

### 게시글 등록

```http
POST /api/boards
Content-Type: application/json
```

```json
{
  "title": "첫 번째 게시글",
  "content": "게시글 내용입니다.",
  "writer": "user01"
}
```

### 게시글 수정

```http
PUT /api/boards/1
Content-Type: application/json
```

```json
{
  "title": "수정된 제목",
  "content": "수정된 내용입니다."
}
```

## 📦 응답 예시

### 게시글 단건 응답

```json
{
  "id": 1,
  "title": "첫 번째 게시글",
  "content": "게시글 내용입니다.",
  "writer": "user01",
  "viewCount": 1,
  "createdAt": "2026-07-01T09:00:00",
  "updatedAt": "2026-07-01T09:00:00"
}
```

### 목록 응답

```json
{
  "content": [
    {
      "id": 1,
      "title": "첫 번째 게시글",
      "content": "게시글 내용입니다.",
      "writer": "user01",
      "viewCount": 1,
      "createdAt": "2026-07-01T09:00:00",
      "updatedAt": "2026-07-01T09:00:00"
    }
  ],
  "page": 1,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

## 🚨 예외 처리

공통 예외는 `GlobalExceptionHandler`에서 처리합니다.

| 상황 | HTTP Status |
| --- | --- |
| 게시글을 찾을 수 없음 | `404 Not Found` |
| 요청 값 검증 실패 | `400 Bad Request` |
| 존재하지 않는 URL 요청 | `404 Not Found` |
| 서버 내부 오류 | `500 Internal Server Error` |

## 📝 참고

- `application.yaml`의 `spring.sql.init.mode`가 `never`로 설정되어 있어 `schema.sql`, `data.sql`은 자동 실행되지 않습니다.
- 초기 데이터가 필요하면 MySQL에서 `schema.sql`과 `data.sql`을 직접 실행하세요.
- API 문서 도구를 사용하려면 `springdoc-openapi` 의존성이 활성화되어 있어야 합니다.
