## 서비스 소개
본 서비스는 신촌 상권의 소상공인(사장님)들과 인근 대학 재학생들을 안전하고 효율적으로 연결해 주는 하이퍼로컬 맞춤형 외주 매칭 플랫폼입니다.
- **타겟층:**
    - **사장님:** 디지털 마케팅, 디자인, 웹 개발 지원이 필요한 신촌 소상공인
    - **학생:** 실무 경험과 수익 창출을 원하는 인근 대학생

## 기술 스택

| 분야 | 기술 |
|---|---|
| Frontend | React, TypeScript |
| Backend | Java 21, Spring Boot 4.1.1, Gradle |
| Database | H2 Database, MySQL |
| ORM | Spring Data JPA, Hibernate |
| Security | Spring Security, JWT |
| File Storage | AWS S3 SDK v2 |
| AI | Ollama, EXAONE 3.5 7.8B, Qwen 2.5 7B |
| API Documentation | Swagger UI, Springdoc OpenAPI 3.1.0 |
| Validation | Jakarta Bean Validation |
| Design | Figma |

## 팀원 소개

| 구분 | 담당자 | 담당 기능 | 주요 구현 내용 |
|---|---|---|---|
| Frontend | 박효정 | 학생 사용자 플로우 및 UI 구현 | 학생 온보딩, 홈, 일손 찾기, 공고 지원, 현재 매칭, 마이페이지 UI 구현 |
| Frontend | 이예원 | 점주 사용자 플로우 및 UI 구현 | 점주 온보딩, 공고 등록, 지원자 관리, 매칭 관리, 마이페이지 UI 구현 |
| Design / Planning | 정아인 | 기획 및 디자인 | 서비스 기획, 사용자 플로우 설계, 화면 UI/UX 디자인 |
| Backend | 오창엽 | 로그인 / 회원가입 / 마이페이지 | 학생·점주 회원가입, 대학 이메일 기반 학생 판별, JWT 로그인 및 인증, 회원·프로필 조회 |
| Backend | 조민준 | 공고 / LLM 공고 생성 | 공고 등록·조회·수정·취소, 공고 이미지 관리, 조건별 공고 검색, Ollama 기반 AI 공고 정제 |
| Backend | 주해윤 | 지원 / 매칭 / 결과물 / 수정 요청 | 공고 지원·수락, 매칭 생성, 결과물 제출·수정 요청·승인 |



## 🐳 Docker 실행

Java 21 기반으로 빌드되며, 기본 DB는 H2 인메모리입니다.

```bash
docker compose up --build
```

API는 `http://localhost:8080`, H2 콘솔은 `http://localhost:8080/h2-console`에서 접근할 수 있습니다.
H2 콘솔 JDBC URL은 `jdbc:h2:mem:odyssey`입니다.

외부 SQL DB를 연결할 때는 `.env.example`을 `.env`로 복사한 뒤
`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_DRIVER_CLASS_NAME`,
`SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`를 변경합니다.
Compose 네트워크 내부 DB라면 JDBC URL의 호스트에 `localhost` 대신 DB 서비스명(예: `mysql`)을 사용합니다.

## 🌿 Branch Convention

브랜치 이름은 아래 규칙을 따릅니다.

```text
type/작업내용
```

### Branch Type

| Type | 의미 | 예시 |
|------|------|------|
| feature | 새로운 기능 개발 | `feature/signup` |
| fix | 버그 수정 | `fix/login-error` |
| docs | 문서 수정 | `docs/readme` |
| refactor | 코드 리팩토링 | `refactor/member-service` |
| chore | 설정, 빌드, 기타 작업 | `chore/github-template` |
| test | 테스트 코드 작성 | `test/member-service` |

---

## 💬 Commit Convention

커밋 메시지는 아래 형식을 사용합니다.

```text
Type: 작업 내용
```

### Commit Type

| Type | 의미 |
|------|------|
| Feat | 새로운 기능 추가 |
| Fix | 버그 수정 |
| Docs | 문서 수정 |
| Style | 코드 포맷팅, 세미콜론 수정 등 기능 변화 없는 수정 |
| Refactor | 코드 리팩토링 |
| Test | 테스트 코드 추가 또는 수정 |
| Chore | 빌드 설정, 패키지 설정, 기타 작업 |
| Rename | 파일 또는 폴더명 변경 |
| Remove | 파일 삭제 |
