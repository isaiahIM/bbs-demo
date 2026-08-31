# 🚀 Spring Boot 4 + JPA Rich Domain Model BBS Demo

Spring Boot 4.1.1, Java 25, Spring Data JPA, H2/PostgreSQL Multi-Profile DB를 기반으로 **Rich Domain Model(풍부한 도메인 모델)** 패턴, **Java Record DTO**, **Spring REST Docs + OpenAPI 3.0 자동 추출 아키텍처**를 적용한 레퍼런스 프로젝트입니다.

---

## 📌 목차
1. [프로젝트 아키텍처 핵심 특징](#-프로젝트-아키텍처-핵심-특징)
2. [기술 스택 (Tech Stack)](#-기술-스택-tech-stack)
3. [프로필별 DB 환경 구성 (Multi-Profile Database)](#-프로필별-db-환경-구성-multi-profile-database)
4. [패키지 구조 (Package Structure)](#-패키지-구조-package-structure)
5. [주요 설계 패턴 (Design Patterns)](#-주요-설계-패턴-design-patterns)
6. [REST API 명세 (API Specification)](#-rest-api-명세-api-specification)
7. [BDD 테스트 수트 구성 (BDD Test Suites)](#-bdd-테스트-수트-구성-bdd-test-suites)
8. [실행 및 테스트 방법 (Getting Started)](#-실행-및-테스트-방법-getting-started)

---

## 🌟 프로젝트 아키텍처 핵심 특징

### 1. 🟢 비즈니스 코드와 문서화 코드의 완전한 분리 (Zero Swagger Annotations in Production)
- 컨트롤러, 서비스, DTO 등 **비즈니스(프로덕션) 코드에는 문서화 목적의 코드나 어노테이션(`@Operation`, `@Schema`, `@Tag` 등)을 단 1줄도 작성하지 않았습니다.**
- 모든 API 문서화 코드(`summary`, `description`, `pathParameters`, `queryParameters` 등)는 **오직 테스트 코드(`src/test/.../controller/`)에만 작성**되어 비즈니스 로직과 철저히 격리됩니다.
- BDD 기반 컨트롤러 슬라이스 테스트(MockMvc) 실행 시 `com.epages.restdocs-api-spec`을 통해 OpenAPI 3.0 명세([`openapi3.yaml`](src/main/resources/static/docs/openapi3.yaml))를 자동 추출합니다.
- **장점**: 
  - 비즈니스/프로덕션 코드가 문서화용 부가 코드로 오염되지 않고 본래의 비즈니스 로직에만 집중할 수 있습니다.
  - 테스트를 반드시 통과해야만 문서가 생성되므로 **코드 변경 시 문서 불일치(Drift)가 100% 원천 차단**됩니다.

### 2. ⚡ Spring Boot 4 & Java 25 최신화
- **Spring Boot 4.1.1** 및 **Java 25** 툴체인을 적용하여 최신 런타임 최적화와 향상된 언어 기능을 활용합니다.
- Spring Boot 4의 모듈화된 테스트 스타터(`spring-boot-starter-webmvc-test`, `spring-boot-restdocs`)를 구성했습니다.
- 레거시 `@MockBean` 대신 Spring Framework 6.2+ / Spring Boot 4 표준인 **`@MockitoBean`**을 전면 적용하여 안전하고 유연한 Mock 의존성 주입을 수행합니다.

### 3. 🏛️ Rich Domain Model (풍부한 도메인 모델 패턴)
- 비즈니스 유효성 검증(필수값 체크, 작성자 권한 검증 등)과 상태 변경 로직이 `Board`, `Post`, `Comment` 엔티티 내부에 캡슐화되어 있습니다.
- 서비스 계층(`BoardService`, `PostService`, `CommentService`)은 트랜잭션 관리와 영속화 조율만 담당하는 **Lean Service(얇은 서비스 계층)** 구조입니다.

### 4. 📦 Java `record` DTO 표준 적용
- DTO 객체에 최신 Java 표준인 **Java `record`**를 전면 도입하여 데이터 불변성(Immutability)을 보장하고 불필요한 보일러플레이트 코드를 제거했습니다.
- 엔티티 ↔ DTO 변환 시 콤팩트 생성자(Compact Constructor)와 Java Stream `.toList()`를 활용합니다.

### 5. 🔍 화면 중복 방지 및 직관적인 API 명세 (Swagger UI Optimization)
- 동일 API가 여러 태그 섹션에 중복 렌더링되는 문제를 방지하기 위해 태그를 **주 도메인(`1. 게시판 API`, `2. 게시글 API`, `3. 댓글 API`) 1개로 고정**했습니다.
- `springdoc.swagger-ui.displayOperationId: false` 옵션을 지정하여 깔끔한 화면을 유지하고, 각 API의 `summary`와 `description` 및 파라미터 설명을 충실히 작성하여 Swagger UI에서 직관적인 문서 탐색이 가능합니다.

### 6. 🔀 프로필별 다중 DB 환경 구성 (Multi-Profile Database)
- **Local**: H2 In-Memory 데이터베이스 (외부 DB 설치 없이 즉시 개발 및 테스트 가능)
- **Dev / Prod**: PostgreSQL 데이터베이스 (환경 변수를 통한 주입 지원)

---

## 🛠️ 기술 스택 (Tech Stack)

| 구분 | 기술 / 라이브러리 | 설명 |
| :--- | :--- | :--- |
| **Language** | Java 25 | Java 25 Toolchain |
| **Framework** | Spring Boot 4.1.1 | Web, Data JPA, `spring-boot-starter-webmvc-test`, `spring-boot-restdocs` |
| **DTO Standard** | Java `record` | 불변(Immutable) 객체 보장 및 보일러플레이트 제거 |
| **Database** | H2 (Local/Test), PostgreSQL (Dev/Prod) | 프로필별 분리 적용 |
| **ORM** | Spring Data JPA / Hibernate | Entity, Repository, JPA Auditing (`createdAt`, `updatedAt`) |
| **API Docs** | Spring REST Docs + OpenAPI 3.0 | `restdocs-api-spec:0.20.1` & `springdoc-openapi-starter-webmvc-ui:3.1.0` |
| **Testing** | JUnit 5, AssertJ, BDDMockito, `@MockitoBean`, MockMvc | BDD 스타일 4단계 계층형 테스트 |
| **Tool / Utility**| Lombok, Gradle 9.x | 코드 간소화 및 빌드 최적화 |

---

## 🔀 프로필별 DB 환경 구성 (Multi-Profile Database)

환경별로 독립된 설정 파일(`yml`)을 분리하여 유지보수성을 높였습니다.

| 프로필 (Profile) | 대상 DB | 설정 파일 | 비고 |
| :--- | :--- | :--- | :--- |
| **`local` (기본값)** | H2 In-Memory | [`application-local.yml`](src/main/resources/application-local.yml) | H2 Web Console 지원 (`/h2-console`) |
| **`dev`** | PostgreSQL | [`application-dev.yml`](src/main/resources/application-dev.yml) | 개발 환경 DB (`ddl-auto: update`) |
| **`prod`** | PostgreSQL | [`application-prod.yml`](src/main/resources/application-prod.yml) | 운영 환경 DB (`ddl-auto: validate`) |

---

## 📂 패키지 구조 (Package Structure)

```
src/main/
├── java/org/study/bbsdemo/
│   ├── config/
│   │   └── JpaConfig.java                       # @EnableJpaAuditing 활성화 설정
│   ├── domain/                                  # Rich Domain Layer (비즈니스 검증 및 상태 관리)
│   │   ├── BaseTimeEntity.java                  # 생성/수정 일시 자동 감사(Auditing)
│   │   ├── Board.java                           # 게시판 엔티티 (게시판 이름 불변식 검증)
│   │   ├── Post.java                            # 게시글 엔티티 (작성자 권한 검증, 양방향 연관관계 관리)
│   │   └── Comment.java                         # 댓글 엔티티 (내용 및 작성자 검증)
│   ├── repository/                              # Data Access Layer (Spring Data JPA)
│   │   ├── BoardRepository.java
│   │   ├── PostRepository.java
│   │   └── CommentRepository.java
│   ├── dto/                                     # Data Transfer Object Layer (Java record)
│   │   ├── BoardDto.java                        # Request / Response record
│   │   ├── PostDto.java                         # CreateRequest / UpdateRequest / SummaryResponse / DetailResponse record
│   │   └── CommentDto.java                      # CreateRequest / UpdateRequest / Response record
│   ├── service/                                 # Application Service Layer (트랜잭션 및 유스케이스 조율)
│   │   ├── BoardService.java
│   │   ├── PostService.java
│   │   └── CommentService.java
│   ├── controller/                              # Web REST Controller Layer (순수 REST API)
│   │   ├── BoardController.java                 # /api/boards
│   │   ├── PostController.java                  # /api/boards/{boardId}/posts, /api/posts/{postId}
│   │   └── CommentController.java               # /api/posts/{postId}/comments, /api/comments/{commentId}
│   └── exception/                               # Global REST Exception Handler
│       └── GlobalExceptionHandler.java          # REST API 표준 JSON 에러 응답 처리
└── resources/
    ├── application.yml                          # 공통 기본 설정 (default active: local)
    ├── application-local.yml                    # Local (H2 In-Memory)
    ├── application-dev.yml                      # Dev (PostgreSQL)
    ├── application-prod.yml                     # Prod (PostgreSQL)
    └── static/docs/
        └── openapi3.yaml                        # REST Docs 자동 추출 OpenAPI 3.0 명세
```

---

## 📐 주요 설계 패턴 (Design Patterns)

### 1. Java `record` 기반 DTO 예시 (`PostDto.java`)
```java
public class PostDto {

    public record CreateRequest(
            String title,
            String content,
            String writer
    ) {}

    public record DetailResponse(
            Long id,
            Long boardId,
            String title,
            String content,
            String writer,
            List<CommentDto.Response> comments,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public DetailResponse(Post post) {
            this(
                    post.getId(),
                    post.getBoard().getId(),
                    post.getTitle(),
                    post.getContent(),
                    post.getWriter(),
                    post.getComments().stream().map(CommentDto.Response::new).toList(),
                    post.getCreatedAt(),
                    post.getUpdatedAt()
            );
        }
    }
}
```

### 2. Rich Domain Model 예시 (`Post.java`)
```java
// 정적 팩토리 메서드 기반 도메인 생성
public static Post create(Board board, String title, String content, String writer) {
    return new Post(board, title, content, writer);
}

// 작성자 권한 검증 및 수정 로직을 엔티티 내부에서 수행
public void update(String title, String content, String requestWriter) {
    validateWriterPermission(requestWriter);
    validateTitle(title);
    validateContent(content);

    this.title = title;
    this.content = content;
}

public void validateWriterPermission(String requestWriter) {
    if (requestWriter == null || !this.writer.equals(requestWriter.trim())) {
        throw new IllegalArgumentException("작성자만 수정 및 삭제할 수 있습니다.");
    }
}
```

### 3. Application Service 예시 (`PostService.java`)
```java
@Transactional
public PostDto.DetailResponse updatePost(Long postId, PostDto.UpdateRequest request) {
    Post post = findPostById(postId);
    // 엔티티 내부 도메인 메서드 호출 및 record 접근자 사용
    post.update(request.title(), request.content(), request.writer());
    return new PostDto.DetailResponse(post);
}
```

### 4. 테스트 코드 기반 API 문서화 예시 (`BoardControllerTest.java`)
> **컨트롤러(비즈니스 코드)는 순수하게 유지**하고, API 명세 정보는 **테스트 코드에서 선언**합니다.
```java
mockMvc.perform(delete("/api/boards/{boardId}", boardId))
        .andExpect(status().isNoContent())
        .andDo(document("board-delete",
                resource(builder()
                        .tag("1. 게시판 API")
                        .summary("게시판 삭제")
                        .description("게시판 및 하위 게시글을 삭제합니다.")
                        .pathParameters(
                                parameterWithName("boardId").description("게시판 ID")
                        )
                        .build())));
```

---

## 📡 REST API 명세 (API Specification)

| 분류 | HTTP Method | Endpoint | 설명 |
| :--- | :--- | :--- | :--- |
| **게시판** | `POST` | `/api/boards` | 게시판 생성 |
| | `GET` | `/api/boards` | 게시판 전체 목록 조회 |
| | `GET` | `/api/boards/{boardId}` | 게시판 단건 조회 |
| | `PUT` | `/api/boards/{boardId}` | 게시판 수정 |
| | `DELETE` | `/api/boards/{boardId}` | 게시판 삭제 |
| **게시글** | `POST` | `/api/boards/{boardId}/posts` | 특정 게시판에 게시글 작성 |
| | `GET` | `/api/boards/{boardId}/posts` | 특정 게시판의 게시글 목록 조회 |
| | `GET` | `/api/posts/{postId}` | 게시글 상세 조회 (댓글 목록 포함) |
| | `PUT` | `/api/posts/{postId}` | 게시글 수정 (작성자 본인 검증) |
| | `DELETE` | `/api/posts/{postId}?writer={writer}` | 게시글 삭제 (작성자 본인 검증) |
| **댓글** | `POST` | `/api/posts/{postId}/comments` | 특정 게시글에 댓글 작성 |
| | `GET` | `/api/posts/{postId}/comments` | 특정 게시글의 댓글 목록 조회 |
| | `PUT` | `/api/comments/{commentId}` | 댓글 수정 (작성자 본인 검증) |
| | `DELETE` | `/api/comments/{commentId}?writer={writer}` | 댓글 삭제 (작성자 본인 검증) |

---

## 🧪 BDD 테스트 수트 구성 (BDD Test Suites)

`Given - When - Then` 패턴에 따라 작성된 **5가지 테스트 레이어**로 100% 빈틈없이 검증됩니다.

1. **도메인 단위 테스트 (`domain/`)**: `BoardDomainTest`, `PostDomainTest`, `CommentDomainTest` (엔티티 불변식, 필수값 및 작성자 권한 비즈니스 규칙 검증)
2. **서비스 단위 테스트 (`service/`)**: `BoardServiceBddTest`, `PostServiceBddTest`, `CommentServiceBddTest` (BDDMockito 기반 격리 단위 테스트)
3. **컨트롤러 REST Docs 테스트 (`controller/`)**: `BoardControllerTest`, `PostControllerTest`, `CommentControllerTest` (Spring Boot 4 `@WebMvcTest` + `@MockitoBean` + `spring-boot-restdocs` 기반 12개 API 전체 검증 및 OpenAPI 3.0 추출)
4. **예외 핸들러 단위 테스트 (`exception/`)**: `GlobalExceptionHandlerTest` (REST API 표준 JSON 에러 응답 및 HTTP 상태 코드 검증)
5. **전체 시나리오 통합 테스트 (`integration/`)**: `BbsBddIntegrationTest` (게시판/게시글/댓글 전체 생애주기 통합 시나리오 테스트)

---

## 🚀 실행 및 테스트 방법 (Getting Started)

### 1. 테스트 실행 및 OpenAPI 명세(`openapi3.yaml`) 자동 추출
```powershell
.\gradlew.bat clean openapi3
```
> 실행 완료 시 [`src/main/resources/static/docs/openapi3.yaml`](src/main/resources/static/docs/openapi3.yaml) 파일이 최신 상태로 갱신됩니다.

### 2. 전체 단위 및 통합 테스트 실행
```powershell
.\gradlew.bat test
```

### 3. 프로필별 애플리케이션 실행

#### 🔹 Local 환경 (H2 In-Memory DB)
```powershell
.\gradlew.bat bootRun
```

#### 🔹 Dev 환경 (PostgreSQL)
```powershell
.\gradlew.bat bootRun --args='--spring.profiles.active=dev'
```

#### 🔹 Prod 환경 (PostgreSQL Jar 실행)
```powershell
java -jar -Dspring.profiles.active=prod build/libs/bbs-demo-0.0.1-SNAPSHOT.jar
```

### 4. 주요 접속 주소
- **Swagger UI (대화형 API 문서)**: `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI 3.0 YAML 파일**: `http://localhost:8080/docs/openapi3.yaml`
- **H2 DB 웹 콘솔 (Local 프로필)**: `http://localhost:8080/h2-console`
  - `JDBC URL`: `jdbc:h2:mem:testdb`
  - `User Name`: `sa` (비밀번호 없음)
