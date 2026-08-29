# 신촌 대학생 외주 플랫폼 DB 명세서

## 1. `members`

### 테이블 설명

플랫폼의 모든 회원 정보를 저장한다.

학생과 사장님은 공통으로 `members` 테이블에 저장하며, `role` 값에 따라 `student_profiles`, `owner_profiles` 테이블과 연결된다.

### Columns

| Column | Type | Null | Default | Description |
|---|---|---|---|---|
| `id` | BIGINT | N | - | 회원 PK |
| `email` | VARCHAR(255) | N | - | 로그인 이메일 |
| `password` | VARCHAR(255) | N | - | 암호화된 비밀번호 |
| `name` | VARCHAR(50) | N | - | 회원 이름 |
| `role` | VARCHAR(20) | N | - | 회원 역할 (`STUDENT`, `OWNER`) |
| `status` | VARCHAR(20) | N | `ACTIVE` | 회원 상태 (`ACTIVE`, `WITHDRAWN`) |

### Primary Key

```text
PK_MEMBERS (id)
```

### 관계

```text
members 1 : 0..1 student_profiles
members 1 : 0..1 owner_profiles
```

---

# 2. `student_profiles`

### 테이블 설명

학생 회원의 추가 프로필 정보를 저장한다.

`members.role = STUDENT`인 회원만 해당 프로필을 가진다.

### Columns

| Column | Type | Null | Default | Description |
|---|---|---|---|---|
| `member_id` | BIGINT | N | - | 학생 회원 ID |
| `university_id` | BIGINT | N | - | 소속 대학교 ID |
| `major` | VARCHAR(100) | Y | - | 전공 |
| `introduction` | VARCHAR(500) | Y | - | 자기소개 |
| `created_at` | DATETIME | N | `CURRENT_TIMESTAMP` | 프로필 생성일 |
| `updated_at` | DATETIME | N | `CURRENT_TIMESTAMP` | 프로필 수정일 |

### Primary Key

```text
PK_STUDENT_PROFILES (member_id)
```

### Foreign Key

```text
member_id → members.id
```

추가 권장 FK:

```text
university_id → universities.id
```

### 관계

```text
members 1 : 1 student_profiles

universities 1 : N student_profiles
```

---

# 3. `owner_profiles`

### 테이블 설명

사장님 회원의 매장 및 사업 관련 프로필 정보를 저장한다.

`members.role = OWNER`인 회원만 해당 프로필을 가진다.

### Columns

| Column | Type | Null | Default | Description |
|---|---|---|---|---|
| `member_id` | BIGINT | N | - | 사장님 회원 ID |
| `business_name` | VARCHAR(100) | N | - | 매장명 |
| `address` | VARCHAR(255) | Y | - | 매장 주소 |
| `introduction` | VARCHAR(500) | Y | - | 매장 소개 |

### Primary Key

```text
PK_OWNER_PROFILES (member_id)
```

### Foreign Key

```text
member_id → members.id
```

### 관계

```text
members 1 : 1 owner_profiles
```

---

# 4. `universities`

### 테이블 설명

플랫폼에서 지원하는 대학 정보를 저장한다.

학생 회원가입 시 소속 대학교를 선택할 때 사용한다.

### Columns

| Column | Type | Null | Default | Description |
|---|---|---|---|---|
| `id` | BIGINT | N | - | 대학 PK |
| `name` | VARCHAR(100) | N | - | 대학명 |
| `created_at` | DATETIME | N | `CURRENT_TIMESTAMP` | 등록일 |

### Primary Key

```text
PK_UNIVERSITIES (id)
```

### 관계

```text
universities 1 : N student_profiles
```

---

# 5. `job_posts`

### 테이블 설명

사장님이 등록한 외주 공고 정보를 저장한다.

LLM 공고 마법사를 사용할 경우 사장님의 원본 입력과 정제된 공고 내용을 함께 저장한다.

### Columns

| Column | Type | Null | Default | Description |
|---|---|---|---|---|
| `id` | BIGINT | N | - | 공고 PK |
| `owner_id` | BIGINT | N | - | 공고를 등록한 사장님 회원 ID |
| `title` | VARCHAR(150) | N | - | 공고 제목 |
| `description` | TEXT | N | - | 공고 상세 내용 |
| `raw_request` | TEXT | Y | - | LLM 정제 전 사장님 원본 요청 |
| `category` | VARCHAR(30) | N | - | 카테고리 (`WEB`, `IMAGE`, `SNS`) |
| `budget` | INT | N | - | 공고 예산 |
| `deadline` | DATETIME | N | - | 작업 마감일 |
| `revision_limit` | INT | N | `4` | 최대 수정 요청 가능 횟수 |
| `status` | VARCHAR(30) | N | `OPEN` | 공고 상태 |
| `created_at` | DATETIME | N | `CURRENT_TIMESTAMP` | 공고 생성일 |
| `updated_at` | DATETIME | N | `CURRENT_TIMESTAMP` | 공고 수정일 |

### Status

| Value | Description |
|---|---|
| `OPEN` | 지원 가능한 공고 |
| `MATCHED` | 학생과 매칭 완료 |
| `COMPLETED` | 작업 완료 |
| `CANCELED` | 공고 취소 |

### Primary Key

```text
PK_JOB_POSTS (id)
```

### 권장 Foreign Key

```text
owner_id → members.id
```

### 관계

```text
members(OWNER) 1 : N job_posts

job_posts 1 : N job_applications

job_posts 1 : 0..1 matchings
```

---

# 6. `job_applications`

### 테이블 설명

학생이 외주 공고에 지원한 내역을 저장한다.

한 학생이 여러 공고에 지원할 수 있으며 하나의 공고에도 여러 학생이 지원할 수 있다.

### Columns

| Column | Type | Null | Default | Description |
|---|---|---|---|---|
| `id` | BIGINT | N | - | 지원 PK |
| `job_post_id` | BIGINT | N | - | 지원 대상 공고 ID |
| `student_id` | BIGINT | N | - | 지원한 학생 회원 ID |
| `message` | VARCHAR(1000) | Y | - | 지원 메시지 |
| `status` | VARCHAR(30) | N | `PENDING` | 지원 상태 |
| `applied_at` | DATETIME | N | `CURRENT_TIMESTAMP` | 지원 시각 |
| `decided_at` | DATETIME | Y | - | 수락 또는 거절 처리 시각 |

### Status

| Value | Description |
|---|---|
| `PENDING` | 사장님 결정 대기 |
| `ACCEPTED` | 지원 수락 |
| `REJECTED` | 지원 거절 |
| `CANCELED` | 학생이 지원 취소 |

### Primary Key

```text
PK_JOB_APPLICATIONS (id)
```

### 권장 Foreign Key

```text
job_post_id → job_posts.id
student_id → members.id
```

### 권장 Unique Constraint

동일 학생이 동일 공고에 중복 지원하는 것을 방지한다.

```text
UNIQUE (job_post_id, student_id)
```

### 관계

```text
job_posts 1 : N job_applications

members(STUDENT) 1 : N job_applications

job_applications 1 : 0..1 matchings
```

---

# 7. `matchings`

### 테이블 설명

사장님이 특정 학생의 지원을 수락하여 실제 작업이 시작된 거래 정보를 저장한다.

공고의 원래 예산 및 마감일과 별도로 최종 합의 금액과 마감일을 저장한다.

### Columns

| Column | Type | Null | Default | Description |
|---|---|---|---|---|
| `id` | BIGINT | N | - | 매칭 PK |
| `job_post_id` | BIGINT | N | - | 매칭된 공고 ID |
| `application_id` | BIGINT | N | - | 수락된 지원 ID |
| `agreed_amount` | INT | N | - | 최종 합의 금액 |
| `deadline` | DATETIME | N | - | 최종 합의 작업 기한 |
| `revision_count` | INT | N | `0` | 현재까지 사용한 수정 요청 횟수 |
| `status` | VARCHAR(30) | N | `IN_PROGRESS` | 매칭 상태 |
| `matched_at` | DATETIME | N | `CURRENT_TIMESTAMP` | 매칭 성사 시각 |
| `completed_at` | DATETIME | Y | - | 작업 최종 완료 시각 |

### Status

| Value | Description |
|---|---|
| `IN_PROGRESS` | 작업 진행 중 |
| `SUBMITTED` | 학생 결과물 제출 |
| `REVISION_REQUESTED` | 사장님 수정 요청 |
| `COMPLETED` | 작업 완료 |
| `CANCELED` | 거래 취소 |

### Primary Key

```text
PK_MATCHINGS (id)
```

### 권장 Foreign Key

```text
job_post_id → job_posts.id

application_id → job_applications.id
```

### 권장 Unique Constraint

하나의 공고는 하나의 최종 매칭만 생성하도록 제한할 경우:

```text
UNIQUE (job_post_id)
```

하나의 지원이 중복 매칭되는 것을 방지하기 위해:

```text
UNIQUE (application_id)
```

### 관계

```text
job_posts 1 : 0..1 matchings

job_applications 1 : 0..1 matchings

matchings 1 : N submissions
```

---

# 8. `submissions`

### 테이블 설명

학생이 특정 매칭에 대해 제출한 결과물을 회차별로 저장한다.

수정 요청 이후 새 결과물을 제출할 때마다 새로운 `submission`이 생성된다.

### Columns

| Column | Type | Null | Default | Description |
|---|---|---|---|---|
| `id` | BIGINT | N | - | 결과물 제출 PK |
| `matching_id` | BIGINT | N | - | 매칭 ID |
| `round_number` | INT | N | - | 제출 회차 |
| `description` | VARCHAR(1000) | Y | - | 결과물 설명 |
| `status` | VARCHAR(30) | N | `SUBMITTED` | 제출 상태 |
| `submitted_at` | DATETIME | N | `CURRENT_TIMESTAMP` | 제출 시각 |

### Status

| Value | Description |
|---|---|
| `SUBMITTED` | 학생이 제출한 상태 |
| `APPROVED` | 사장님 최종 승인 |
| `REVISION_REQUESTED` | 수정 요청된 상태 |

### Primary Key

```text
PK_SUBMISSIONS (id)
```

### 권장 Foreign Key

```text
matching_id → matchings.id
```

### 권장 Unique Constraint

하나의 매칭에서 동일한 제출 회차가 중복 생성되는 것을 방지한다.

```text
UNIQUE (matching_id, round_number)
```

### 관계

```text
matchings 1 : N submissions

submissions 1 : N submission_files

submissions 1 : 0..1 revision_requests
```

---

# 9. `submission_files`

### 테이블 설명

학생이 제출한 결과물의 실제 파일 정보를 저장한다.

파일 자체는 S3 등의 Object Storage에 저장하고 DB에는 파일 메타데이터와 접근 URL을 저장한다.

### Columns

| Column | Type | Null | Default | Description |
|---|---|---|---|---|
| `id` | BIGINT | N | - | 파일 PK |
| `submission_id` | BIGINT | N | - | 결과물 제출 ID |
| `original_name` | VARCHAR(255) | N | - | 업로드 당시 원본 파일명 |
| `file_url` | VARCHAR(1000) | N | - | 저장된 파일 접근 URL |
| `content_type` | VARCHAR(100) | Y | - | MIME Type |
| `file_size` | BIGINT | Y | - | 파일 크기(Byte) |
| `created_at` | DATETIME | N | `CURRENT_TIMESTAMP` | 파일 등록일 |

### Primary Key

```text
PK_SUBMISSION_FILES (id)
```

### 권장 Foreign Key

```text
submission_id → submissions.id
```

### 관계

```text
submissions 1 : N submission_files
```

---

# 10. `revision_requests`

### 테이블 설명

사장님이 학생의 제출 결과물에 대해 요청한 수정 내용을 저장한다.

수정 요청은 특정 `submission`을 기준으로 생성된다.

### Columns

| Column | Type | Null | Default | Description |
|---|---|---|---|---|
| `id` | BIGINT | N | - | 수정 요청 PK |
| `submission_id` | BIGINT | N | - | 수정 요청 대상 제출물 ID |
| `reason` | VARCHAR(1000) | N | - | 수정 요청 사유 및 내용 |
| `requested_at` | DATETIME | N | `CURRENT_TIMESTAMP` | 수정 요청 시각 |

### Primary Key

```text
PK_REVISION_REQUESTS (id)
```

### 권장 Foreign Key

```text
submission_id → submissions.id
```

### 권장 Unique Constraint

현재 구조에서는 하나의 제출물에 한 번의 수정 요청만 존재하는 것이 자연스럽다.

```text
UNIQUE (submission_id)
```

### 관계

```text
submissions 1 : 0..1 revision_requests
```

---

# 11. 전체 테이블 관계

```text
members
 ├─ 1 : 1 ─ owner_profiles
 │
 └─ 1 : 1 ─ student_profiles
                │
                N : 1
                │
           universities


members (OWNER)
     │
     │ 1 : N
     ▼
 job_posts
     │
     │ 1 : N
     ▼
job_applications ◀──── members (STUDENT)
     │
     │ 1 : 0..1
     ▼
  matchings
     │
     │ 1 : N
     ▼
 submissions
    ├───────────────┐
    │               │
  1 : N           1 : 0..1
    │               │
    ▼               ▼
submission_files  revision_requests
```

---

# 12. 테이블 요약

| Table | Description |
|---|---|
| `members` | 공통 회원 정보 |
| `student_profiles` | 학생 프로필 |
| `owner_profiles` | 사장님 및 매장 프로필 |
| `universities` | 대학 정보 |
| `job_posts` | 사장님 외주 공고 |
| `job_applications` | 학생 공고 지원 |
| `matchings` | 사장님-학생 최종 매칭 |
| `submissions` | 학생 결과물 제출 |
| `submission_files` | 제출 결과물 파일 |
| `revision_requests` | 사장님 수정 요청 |

---

# 13. 주요 업무 흐름과 DB 변화

## 공고 등록

```text
OWNER
 ↓
job_posts 생성
status = OPEN
```

## 학생 지원

```text
STUDENT
 ↓
job_applications 생성
status = PENDING
```

## 사장님 지원 수락

```text
job_applications
PENDING → ACCEPTED

job_posts
OPEN → MATCHED

matchings 생성
status = IN_PROGRESS
revision_count = 0
```

선택되지 않은 다른 지원:

```text
job_applications
PENDING → REJECTED
```

## 학생 1차 결과물 제출

```text
submissions 생성

round_number = 1
status = SUBMITTED

submission_files 생성

matchings
IN_PROGRESS → SUBMITTED
```

## 사장님 수정 요청

```text
revision_requests 생성

submissions
SUBMITTED → REVISION_REQUESTED

matchings
SUBMITTED → REVISION_REQUESTED

revision_count
0 → 1
```

## 학생 수정본 제출

기존 Submission을 수정하지 않고 새로운 Submission을 생성한다.

```text
submissions 생성

round_number = 2
status = SUBMITTED

matchings
REVISION_REQUESTED → SUBMITTED
```

## 사장님 최종 승인

```text
submissions
SUBMITTED → APPROVED

matchings
SUBMITTED → COMPLETED

job_posts
MATCHED → COMPLETED

completed_at 기록
```

---

# 14. 현재 DB 기준 추가 권장 Constraint

현재 SQL에는 일부 FK만 정의되어 있으므로 아래 FK 추가를 권장한다.

```sql
ALTER TABLE job_posts
ADD CONSTRAINT FK_JOB_POST_OWNER
FOREIGN KEY (owner_id)
REFERENCES members(id);

ALTER TABLE job_applications
ADD CONSTRAINT FK_APPLICATION_JOB_POST
FOREIGN KEY (job_post_id)
REFERENCES job_posts(id);

ALTER TABLE job_applications
ADD CONSTRAINT FK_APPLICATION_STUDENT
FOREIGN KEY (student_id)
REFERENCES members(id);

ALTER TABLE matchings
ADD CONSTRAINT FK_MATCHING_JOB_POST
FOREIGN KEY (job_post_id)
REFERENCES job_posts(id);

ALTER TABLE matchings
ADD CONSTRAINT FK_MATCHING_APPLICATION
FOREIGN KEY (application_id)
REFERENCES job_applications(id);

ALTER TABLE submissions
ADD CONSTRAINT FK_SUBMISSION_MATCHING
FOREIGN KEY (matching_id)
REFERENCES matchings(id);

ALTER TABLE submission_files
ADD CONSTRAINT FK_SUBMISSION_FILE
FOREIGN KEY (submission_id)
REFERENCES submissions(id);

ALTER TABLE revision_requests
ADD CONSTRAINT FK_REVISION_SUBMISSION
FOREIGN KEY (submission_id)
REFERENCES submissions(id);

ALTER TABLE student_profiles
ADD CONSTRAINT FK_STUDENT_UNIVERSITY
FOREIGN KEY (university_id)
REFERENCES universities(id);
```

중복 데이터 방지를 위해 다음 Unique Constraint도 권장한다.

```sql
ALTER TABLE job_applications
ADD CONSTRAINT UK_JOB_APPLICATION
UNIQUE (job_post_id, student_id);

ALTER TABLE matchings
ADD CONSTRAINT UK_MATCHING_JOB_POST
UNIQUE (job_post_id);

ALTER TABLE matchings
ADD CONSTRAINT UK_MATCHING_APPLICATION
UNIQUE (application_id);

ALTER TABLE submissions
ADD CONSTRAINT UK_SUBMISSION_ROUND
UNIQUE (matching_id, round_number);

ALTER TABLE revision_requests
ADD CONSTRAINT UK_REVISION_SUBMISSION
UNIQUE (submission_id);
```

---

# 15. 현재 기획 대비 미구현 DB 영역

현재 DB는 MVP의 **공고 → 지원 → 매칭 → 결과물 제출 → 수정 요청 → 완료** 흐름을 표현한다.

다음 기능은 현재 테이블에 포함되어 있지 않으며 추후 별도 설계가 필요하다.

| 기능 | 필요한 데이터 |
|---|---|
| 대학 이메일 인증 | 이메일 인증 코드, 인증 상태, 이메일 도메인 |
| 학생 스킬 태그 | 스킬 및 학생-스킬 매핑 |
| 에스크로 결제 | 결제 금액, 결제 상태, PG 거래 ID |
| 7일 클레임 | 클레임 내용, 접수일, 처리 상태 |
| 학생 정산 | 정산 가능일, 정산 상태, 지급일 |
| 만족도 평가 | 별점, 리뷰 내용 |
| 사업자 인증 | 사업자등록번호, 인증 상태 |