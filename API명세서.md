# 신촌 대학생 외주 플랫폼 API 명세서

**Base URL**

```text
/api/v1
```

**인증 방식**

```text
Authorization: Bearer {accessToken}
```

로그인 사용자의 `memberId`는 요청 Body로 전달하지 않고 JWT 또는 Spring Security의 인증 정보에서 추출한다.

---

# 1. 인증 API

## 1-1. 학생 회원가입

### API 설명

대학생 회원을 생성한다.

현재 MVP에서는 대학 이메일 인증이 완료되었다고 가정한다.

### REQUEST

`POST /api/v1/auth/signup/student`

#### Request Body

| Name | Type | Required | Description |
|---|---|---|---|
| `email` | String | Y | 대학 이메일 |
| `password` | String | Y | 비밀번호 |
| `name` | String | Y | 이름 |
| `major` | String | N | 전공 |
| `introduction` | String | N | 자기소개 |

```json
{
  "email": "student@yonsei.ac.kr",
  "password": "password123!",
  "name": "김학생",
  "major": "컴퓨터과학과",
  "introduction": "웹 개발을 좋아하는 학생입니다."
}
```

### RESPONSE

## 공통 응답 형식

모든 API 응답은 다음 형식을 사용한다.

| Name | Type | Description |
|---|---|---|
| `isSuccess` | Boolean | 요청 성공 여부 |
| `code` | String | 서버 응답 코드 |
| `message` | String | 응답 메시지 |
| `result` | Object, Array, null | 성공 시 반환 데이터 |
| `error` | Object, null | 실패 시 상세 오류 정보 |

### 성공 응답

반환 데이터가 있는 경우:

```json
{
  "isSuccess": true,
  "code": "COMMON_200",
  "message": "요청이 성공적으로 처리되었습니다.",
  "result": {
    "id": 1
  },
  "error": null
}
```

반환 데이터가 없는 경우:

```json
{
  "isSuccess": true,
  "code": "COMMON_200",
  "message": "요청이 성공적으로 처리되었습니다.",
  "result": null,
  "error": null
}
```

리소스 생성 성공:

```json
{
  "isSuccess": true,
  "code": "COMMON_201",
  "message": "리소스가 생성되었습니다.",
  "result": {
    "id": 1
  },
  "error": null
}
```

### 실패 응답

일반적인 실패 응답:

```json
{
  "isSuccess": false,
  "code": "COMMON_404",
  "message": "요청한 리소스를 찾을 수 없습니다.",
  "result": null,
  "error": null
}
```

Validation 실패 응답:

```json
{
  "isSuccess": false,
  "code": "COMMON_400",
  "message": "잘못된 요청입니다.",
  "result": null,
  "error": {
    "message": "지원 메시지는 1000자 이하여야 합니다."
  }
}
```

### 공통 응답 코드

| HTTP Status | Code | Description |
|---|---|---|
| `200 OK` | `COMMON_200` | 요청 처리 성공 |
| `201 CREATED` | `COMMON_201` | 리소스 생성 성공 |
| `400 BAD REQUEST` | `COMMON_400` | 잘못된 요청 또는 입력값 검증 실패 |
| `401 UNAUTHORIZED` | `COMMON_401` | 인증 필요 또는 인증 실패 |
| `403 FORBIDDEN` | `COMMON_403` | 접근 권한 없음 |
| `404 NOT FOUND` | `COMMON_404` | 리소스를 찾을 수 없음 |
| `405 METHOD NOT ALLOWED` | `COMMON_405` | 지원하지 않는 HTTP Method |
| `500 INTERNAL SERVER ERROR` | `COMMON_500` | 서버 내부 오류 |

---

## 1-2. 사장님 회원가입

### API 설명

사장님 회원을 생성하고 매장 프로필을 등록한다.

### REQUEST

`POST /api/v1/auth/signup/owner`

#### Request Body

| Name | Type | Required | Description |
|---|---|---|---|
| `email` | String | Y | 이메일 |
| `password` | String | Y | 비밀번호 |
| `name` | String | Y | 이름 |
| `businessName` | String | Y | 매장명 |
| `address` | String | N | 매장 주소 |
| `introduction` | String | N | 매장 소개 |

```json
{
  "email": "owner@example.com",
  "password": "password123!",
  "name": "박사장",
  "businessName": "신촌 파스타",
  "address": "서울 서대문구 신촌로 00",
  "introduction": "신촌에서 운영 중인 파스타 전문점입니다."
}
```

### RESPONSE

#### 201 CREATED

```json
{
  "success": true,
  "code": 201,
  "message": "사장님 회원가입 성공",
  "data": {
    "memberId": 15,
    "email": "owner@example.com",
    "name": "박사장",
    "role": "OWNER"
  }
}
```

---

## 1-3. 로그인

### API 설명

이메일과 비밀번호를 이용하여 로그인하고 Access Token을 발급한다.

### REQUEST

`POST /api/v1/auth/login`

#### Request Body

| Name | Type | Required | Description |
|---|---|---|---|
| `email` | String | Y | 가입 이메일 |
| `password` | String | Y | 비밀번호 |

```json
{
  "email": "student@yonsei.ac.kr",
  "password": "password123!"
}
```

### RESPONSE

#### 200 OK

```json
{
  "success": true,
  "code": 200,
  "message": "로그인 성공",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "member": {
      "memberId": 12,
      "name": "김학생",
      "role": "STUDENT"
    }
  }
}
```

#### 401 UNAUTHORIZED

```json
{
  "success": false,
  "code": 401,
  "message": "이메일 또는 비밀번호가 올바르지 않습니다.",
  "data": null
}
```

---

# 2. 회원 / 프로필 API

## 2-1. 내 정보 조회

### API 설명

현재 로그인한 사용자의 회원 및 프로필 정보를 조회한다.

### REQUEST

`GET /api/v1/members/me`

### RESPONSE

#### 200 OK - 학생

```json
{
  "success": true,
  "code": 200,
  "message": "내 정보 조회 성공",
  "data": {
    "memberId": 12,
    "email": "student@yonsei.ac.kr",
    "name": "김학생",
    "role": "STUDENT",
    "status": "ACTIVE",
    "profile": {
      "universityId": 1,
      "universityName": "연세대학교",
      "major": "컴퓨터과학과",
      "introduction": "웹 개발을 좋아하는 학생입니다."
    }
  }
}
```

#### 200 OK - 사장님

```json
{
  "success": true,
  "code": 200,
  "message": "내 정보 조회 성공",
  "data": {
    "memberId": 15,
    "email": "owner@example.com",
    "name": "박사장",
    "role": "OWNER",
    "status": "ACTIVE",
    "profile": {
      "businessName": "신촌 파스타",
      "address": "서울 서대문구 신촌로 00",
      "introduction": "신촌 파스타 전문점"
    }
  }
}
```

---

## 2-2. 대학 목록 조회

### API 설명

학생 회원가입 시 선택할 수 있는 대학 목록을 조회한다.

### REQUEST

`GET /api/v1/universities`

### RESPONSE

#### 200 OK

```json
{
  "success": true,
  "code": 200,
  "message": "대학교 목록 조회 성공",
  "data": [
    {
      "universityId": 1,
      "name": "연세대학교"
    },
    {
      "universityId": 2,
      "name": "이화여자대학교"
    },
    {
      "universityId": 3,
      "name": "서강대학교"
    }
  ]
}
```

---

# 3. LLM 공고 마법사 API

## 3-1. AI 공고 정제

### API 설명

사장님이 입력한 자연어 요청을 LLM을 이용하여 구조화된 공고 형태로 정제한다.

OWNER 권한만 요청할 수 있다.

### REQUEST

`POST /api/v1/job-posts/ai-refine`

#### Request Body

| Name | Type | Required | Description |
|---|---|---|---|
| `rawRequest` | String | Y | 사장님이 입력한 원본 요청 |

```json
{
  "rawRequest": "신촌 파스타집인데 인스타에 올릴 메뉴 사진이랑 릴스 찍어줄 학생 구함. 예산 30만원, 다음주까지"
}
```

### RESPONSE

#### 200 OK

```json
{
  "success": true,
  "code": 200,
  "message": "공고 정제 성공",
  "data": {
    "title": "신촌 파스타 매장 SNS 콘텐츠 제작",
    "description": "메뉴 사진 촬영 및 인스타그램 릴스 영상 제작을 요청합니다.",
    "category": "SNS",
    "budget": 300000,
    "deadline": "2026-09-05T23:59:59+09:00",
    "rawRequest": "신촌 파스타집인데 인스타에 올릴 메뉴 사진이랑 릴스 찍어줄 학생 구함. 예산 30만원, 다음주까지"
  }
}
```

---

# 4. 공고 API

## 4-1. 공고 등록

### API 설명

사장님이 신규 외주 공고를 등록한다.

OWNER 권한만 요청할 수 있다.

### REQUEST

`POST /api/v1/job-posts`

#### Request Body

| Name | Type | Required | Description |
|---|---|---|---|
| `title` | String | Y | 공고 제목 |
| `description` | String | Y | 작업 상세 설명 |
| `rawRequest` | String | N | LLM 정제 이전 요청 |
| `imageUrls` | String[] | N | 공고 이미지 URL 목록, 최대 10장 |
| `category` | String | Y | `WEB`, `IMAGE`, `SNS` |
| `budget` | Integer | Y | 예산 |
| `deadline` | DateTime | Y | 작업 마감일 |
| `revisionLimit` | Integer | N | 최대 수정 횟수, 기본값 4 |

```json
{
  "title": "신촌 파스타 매장 SNS 콘텐츠 제작",
  "description": "메뉴 사진 촬영 및 릴스 영상 제작을 요청합니다.",
  "rawRequest": "메뉴 사진이랑 릴스 찍어줄 학생 구함",
  "imageUrls": [
    "https://storage.example.com/job-posts/31-1.jpg",
    "https://storage.example.com/job-posts/31-2.jpg"
  ],
  "category": "SNS",
  "budget": 300000,
  "deadline": "2026-09-05T23:59:59+09:00",
  "revisionLimit": 4
}
```

### RESPONSE

#### 201 CREATED

```json
{
  "success": true,
  "code": 201,
  "message": "공고 등록 성공",
  "data": {
    "jobPostId": 31,
    "title": "신촌 파스타 매장 SNS 콘텐츠 제작",
    "images": [
      { "imageId": 101, "imageUrl": "https://storage.example.com/job-posts/31-1.jpg", "sortOrder": 0 },
      { "imageId": 102, "imageUrl": "https://storage.example.com/job-posts/31-2.jpg", "sortOrder": 1 }
    ],
    "category": "SNS",
    "budget": 300000,
    "deadline": "2026-09-05T23:59:59+09:00",
    "status": "OPEN",
    "createdAt": "2026-08-29T15:00:00+09:00"
  }
}
```

---

## 4-2. 공고 목록 조회

### API 설명

등록된 외주 공고 목록을 조회한다.

### REQUEST

`GET /api/v1/job-posts`

#### Query Parameter

| Name | Type | Required | Description |
|---|---|---|---|
| `category` | String | N | `WEB`, `IMAGE`, `SNS` |
| `status` | String | N | 공고 상태 |
| `minBudget` | Integer | N | 최소 예산 |
| `maxBudget` | Integer | N | 최대 예산 |
| `page` | Integer | N | 페이지 번호, 기본값 0 |
| `size` | Integer | N | 페이지 크기, 기본값 20 |
| `sort` | String | N | `LATEST`, `DEADLINE`, `BUDGET_HIGH` |

### RESPONSE

#### 200 OK

```json
{
  "success": true,
  "code": 200,
  "message": "공고 목록 조회 성공",
  "data": {
    "content": [
      {
        "jobPostId": 31,
        "title": "신촌 파스타 매장 SNS 콘텐츠 제작",
        "businessName": "신촌 파스타",
        "thumbnailImageUrl": "https://storage.example.com/job-posts/31-1.jpg",
        "category": "SNS",
        "budget": 300000,
        "deadline": "2026-09-05T23:59:59+09:00",
        "status": "OPEN",
        "createdAt": "2026-08-29T15:00:00+09:00"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 35,
    "totalPages": 2
  }
}
```

---

## 4-3. 공고 상세 조회

### API 설명

특정 공고의 상세 정보를 조회한다.

### REQUEST

`GET /api/v1/job-posts/{jobPostId}`

### RESPONSE

#### 200 OK

```json
{
  "success": true,
  "code": 200,
  "message": "공고 상세 조회 성공",
  "data": {
    "jobPostId": 31,
    "title": "신촌 파스타 매장 SNS 콘텐츠 제작",
    "description": "메뉴 사진 촬영 및 릴스 영상 제작을 요청합니다.",
    "images": [
      { "imageId": 101, "imageUrl": "https://storage.example.com/job-posts/31-1.jpg", "sortOrder": 0 },
      { "imageId": 102, "imageUrl": "https://storage.example.com/job-posts/31-2.jpg", "sortOrder": 1 }
    ],
    "category": "SNS",
    "budget": 300000,
    "deadline": "2026-09-05T23:59:59+09:00",
    "revisionLimit": 4,
    "status": "OPEN",
    "owner": {
      "ownerId": 15,
      "businessName": "신촌 파스타",
      "address": "서울 서대문구 신촌로 00"
    },
    "createdAt": "2026-08-29T15:00:00+09:00"
  }
}
```

---

## 4-4. 내가 등록한 공고 조회

### API 설명

현재 로그인한 사장님이 등록한 공고 목록을 조회한다.

OWNER 권한만 요청할 수 있다.

### REQUEST

`GET /api/v1/job-posts/me`

### RESPONSE

#### 200 OK

```json
{
  "success": true,
  "code": 200,
  "message": "내 공고 목록 조회 성공",
  "data": [
    {
      "jobPostId": 31,
      "title": "신촌 파스타 매장 SNS 콘텐츠 제작",
      "thumbnailImageUrl": "https://storage.example.com/job-posts/31-1.jpg",
      "category": "SNS",
      "budget": 300000,
      "status": "OPEN",
      "applicationCount": 4,
      "deadline": "2026-09-05T23:59:59+09:00"
    }
  ]
}
```

---

## 4-5. 공고 수정

### API 설명

사장님이 자신이 등록한 공고를 수정한다.

매칭이 완료된 공고는 수정할 수 없다.

### REQUEST

`PATCH /api/v1/job-posts/{jobPostId}`

```json
{
  "title": "신촌 파스타 매장 릴스 콘텐츠 제작",
  "description": "메뉴 사진 10장 및 릴스 2건 제작",
  "budget": 350000,
  "deadline": "2026-09-07T23:59:59+09:00"
}
```

### RESPONSE

#### 200 OK

```json
{
  "success": true,
  "code": 200,
  "message": "공고 수정 성공",
  "data": {
    "jobPostId": 31,
    "title": "신촌 파스타 매장 릴스 콘텐츠 제작",
    "budget": 350000,
    "deadline": "2026-09-07T23:59:59+09:00",
    "status": "OPEN"
  }
}
```

---

## 4-6. 공고 취소

### API 설명

사장님이 자신이 작성한 공고를 취소한다.

실제 데이터를 삭제하지 않고 상태를 `CANCELED`로 변경한다.

### REQUEST

`DELETE /api/v1/job-posts/{jobPostId}`

### RESPONSE

#### 204 NO CONTENT

---

## 4-7. 공고 이미지 추가

### API 설명

사장님이 자신이 등록한 공고에 이미지를 추가한다.

매칭이 완료된 공고는 이미지를 추가할 수 없다.

OWNER 권한만 요청할 수 있다.

### REQUEST

`POST /api/v1/job-posts/{jobPostId}/images`

#### Request Body

| Name | Type | Required | Description |
|---|---|---|---|
| `imageUrl` | String | Y | 추가할 이미지 URL |

```json
{
  "imageUrl": "https://storage.example.com/job-posts/31-3.jpg"
}
```

### RESPONSE

#### 201 CREATED

```json
{
  "success": true,
  "code": 201,
  "message": "공고 이미지 추가 성공",
  "data": {
    "imageId": 103,
    "imageUrl": "https://storage.example.com/job-posts/31-3.jpg",
    "sortOrder": 2
  }
}
```

---

## 4-8. 공고 이미지 삭제

### API 설명

사장님이 자신이 등록한 공고의 이미지를 삭제한다.

매칭이 완료된 공고는 이미지를 삭제할 수 없다.

OWNER 권한만 요청할 수 있다.

### REQUEST

`DELETE /api/v1/job-posts/{jobPostId}/images/{imageId}`

### RESPONSE

#### 204 NO CONTENT

---

# 5. 지원 API

## 5-1. 공고 지원

### API 설명

학생이 모집 중인 공고에 지원한다.

STUDENT 권한만 요청할 수 있다.

### REQUEST

`POST /api/v1/job-posts/{jobPostId}/applications`

#### Request Body

| Name | Type | Required | Description |
|---|---|---|---|
| `message` | String | N | 사장님에게 전달할 지원 메시지 |

```json
{
  "message": "인스타그램 콘텐츠 제작 경험이 있습니다. 포스터와 릴스 제작 모두 가능합니다."
}
```

### RESPONSE

#### 201 CREATED

```json
{
  "success": true,
  "code": 201,
  "message": "공고 지원 성공",
  "data": {
    "applicationId": 51,
    "jobPostId": 31,
    "status": "PENDING",
    "appliedAt": "2026-08-29T15:20:00+09:00"
  }
}
```

#### 409 CONFLICT

```json
{
  "success": false,
  "code": 409,
  "message": "이미 지원한 공고입니다.",
  "data": null
}
```

---

## 5-2. 내가 지원한 공고 조회

### API 설명

현재 로그인한 학생의 지원 내역을 조회한다.

### REQUEST

`GET /api/v1/applications/me`

### RESPONSE

#### 200 OK

```json
{
  "success": true,
  "code": 200,
  "message": "지원 목록 조회 성공",
  "data": [
    {
      "applicationId": 51,
      "jobPost": {
        "jobPostId": 31,
        "title": "신촌 파스타 매장 SNS 콘텐츠 제작",
        "businessName": "신촌 파스타",
        "budget": 300000,
        "deadline": "2026-09-05T23:59:59+09:00"
      },
      "status": "PENDING",
      "appliedAt": "2026-08-29T15:20:00+09:00"
    }
  ]
}
```

---

## 5-3. 공고 지원자 목록 조회

### API 설명

사장님이 자신이 작성한 특정 공고의 지원자 목록을 조회한다.

OWNER 권한만 요청할 수 있다.

### REQUEST

`GET /api/v1/job-posts/{jobPostId}/applications`

### RESPONSE

#### 200 OK

```json
{
  "success": true,
  "code": 200,
  "message": "지원자 목록 조회 성공",
  "data": [
    {
      "applicationId": 51,
      "student": {
        "memberId": 12,
        "name": "김학생",
        "university": "연세대학교",
        "major": "컴퓨터과학과",
        "introduction": "웹 개발과 디자인에 관심이 있습니다."
      },
      "message": "SNS 콘텐츠 제작 경험이 있습니다.",
      "status": "PENDING",
      "appliedAt": "2026-08-29T15:20:00+09:00"
    }
  ]
}
```

---

## 5-4. 지원 취소

### API 설명

학생이 자신이 제출한 지원을 취소한다.

이미 매칭이 완료된 지원은 취소할 수 없다.

### REQUEST

`POST /api/v1/applications/{applicationId}/cancel`

### RESPONSE

#### 200 OK

```json
{
  "success": true,
  "code": 200,
  "message": "지원 취소 성공",
  "data": {
    "applicationId": 51,
    "status": "CANCELED"
  }
}
```

---

# 6. 매칭 API

## 6-1. 지원 수락 및 매칭 생성

### API 설명

사장님이 지원자를 선택하여 매칭을 생성한다.

OWNER 권한만 요청할 수 있다.

매칭 성공 시 선택된 지원은 `ACCEPTED`, 나머지 지원은 `REJECTED`, 공고 상태는 `MATCHED`로 변경한다.

### REQUEST

`POST /api/v1/applications/{applicationId}/accept`

#### Request Body

| Name | Type | Required | Description |
|---|---|---|---|
| `agreedAmount` | Integer | Y | 최종 합의 금액 |
| `deadline` | DateTime | Y | 최종 작업 기한 |

```json
{
  "agreedAmount": 300000,
  "deadline": "2026-09-05T23:59:59+09:00"
}
```

### RESPONSE

#### 201 CREATED

```json
{
  "success": true,
  "code": 201,
  "message": "매칭 성공",
  "data": {
    "matchingId": 71,
    "jobPostId": 31,
    "applicationId": 51,
    "agreedAmount": 300000,
    "deadline": "2026-09-05T23:59:59+09:00",
    "revisionCount": 0,
    "revisionLimit": 4,
    "status": "IN_PROGRESS",
    "matchedAt": "2026-08-29T16:00:00+09:00"
  }
}
```

---

## 6-2. 내 매칭 목록 조회

### API 설명

현재 로그인한 사용자가 참여하고 있는 매칭 목록을 조회한다.

학생과 사장님 모두 요청할 수 있다.

### REQUEST

`GET /api/v1/matchings/me`

#### Query Parameter

| Name | Type | Required | Description |
|---|---|---|---|
| `status` | String | N | 매칭 상태 필터 |

### RESPONSE

#### 200 OK

```json
{
  "success": true,
  "code": 200,
  "message": "매칭 목록 조회 성공",
  "data": [
    {
      "matchingId": 71,
      "jobPost": {
        "jobPostId": 31,
        "title": "신촌 파스타 매장 SNS 콘텐츠 제작"
      },
      "agreedAmount": 300000,
      "deadline": "2026-09-05T23:59:59+09:00",
      "revisionCount": 1,
      "revisionLimit": 4,
      "status": "REVISION_REQUESTED"
    }
  ]
}
```

---

## 6-3. 매칭 상세 조회

### API 설명

특정 매칭의 상세 정보를 조회한다.

해당 매칭에 참여 중인 학생 또는 사장님만 조회할 수 있다.

### REQUEST

`GET /api/v1/matchings/{matchingId}`

### RESPONSE

#### 200 OK

```json
{
  "success": true,
  "code": 200,
  "message": "매칭 상세 조회 성공",
  "data": {
    "matchingId": 71,
    "jobPost": {
      "jobPostId": 31,
      "title": "신촌 파스타 매장 SNS 콘텐츠 제작",
      "description": "메뉴 사진 및 릴스 제작"
    },
    "owner": {
      "memberId": 15,
      "name": "박사장",
      "businessName": "신촌 파스타"
    },
    "student": {
      "memberId": 12,
      "name": "김학생",
      "university": "연세대학교",
      "major": "컴퓨터과학과"
    },
    "agreedAmount": 300000,
    "deadline": "2026-09-05T23:59:59+09:00",
    "revisionCount": 1,
    "revisionLimit": 4,
    "status": "REVISION_REQUESTED"
  }
}
```

---

# 7. 결과물 제출 API

## 7-1. 결과물 제출

### API 설명

학생이 작업 결과물 또는 수정 결과물을 제출한다.

`roundNumber`는 클라이언트에서 전달하지 않고 서버에서 자동 계산한다.

STUDENT 권한만 요청할 수 있다.

### REQUEST

`POST /api/v1/matchings/{matchingId}/submissions`

#### Request Body

`multipart/form-data`

| Name | Type | Required | Description |
|---|---|---|---|
| `description` | String | N | 결과물 설명 |
| `files` | File[] | Y | 결과물 파일 |

### RESPONSE

#### 201 CREATED

```json
{
  "success": true,
  "code": 201,
  "message": "결과물 제출 성공",
  "data": {
    "submissionId": 91,
    "matchingId": 71,
    "roundNumber": 1,
    "description": "1차 메뉴 사진 및 릴스 시안입니다.",
    "status": "SUBMITTED",
    "files": [
      {
        "fileId": 101,
        "originalName": "menu1.jpg",
        "fileUrl": "https://storage.example.com/submissions/menu1.jpg"
      },
      {
        "fileId": 102,
        "originalName": "reel.mp4",
        "fileUrl": "https://storage.example.com/submissions/reel.mp4"
      }
    ],
    "submittedAt": "2026-09-03T13:00:00+09:00"
  }
}
```

---

## 7-2. 제출 내역 조회

### API 설명

특정 매칭에서 주고받은 결과물 제출 및 수정 요청 이력을 조회한다.

### REQUEST

`GET /api/v1/matchings/{matchingId}/submissions`

### RESPONSE

#### 200 OK

```json
{
  "success": true,
  "code": 200,
  "message": "제출 내역 조회 성공",
  "data": [
    {
      "submissionId": 91,
      "roundNumber": 1,
      "description": "1차 시안입니다.",
      "status": "REVISION_REQUESTED",
      "submittedAt": "2026-09-03T13:00:00+09:00",
      "files": [
        {
          "fileId": 101,
          "originalName": "menu1.jpg",
          "fileUrl": "https://storage.example.com/submissions/menu1.jpg",
          "contentType": "image/jpeg",
          "fileSize": 1534120
        }
      ],
      "revisionRequest": {
        "revisionRequestId": 111,
        "reason": "메뉴명이 조금 더 크게 보이도록 수정해주세요.",
        "requestedAt": "2026-09-03T15:00:00+09:00"
      }
    }
  ]
}
```

---

# 8. 수정 요청 API

## 8-1. 결과물 수정 요청

### API 설명

사장님이 학생이 제출한 결과물에 대해 수정 요청을 등록한다.

수정 요청 시 매칭의 `revisionCount`가 1 증가한다.

OWNER 권한만 요청할 수 있다.

### REQUEST

`POST /api/v1/submissions/{submissionId}/revision-requests`

#### Request Body

| Name | Type | Required | Description |
|---|---|---|---|
| `reason` | String | Y | 수정 요청 내용 |

```json
{
  "reason": "메뉴명이 잘 보이지 않습니다. 글자 크기를 키우고 매장 로고를 우측 상단에 추가해주세요."
}
```

### RESPONSE

#### 201 CREATED

```json
{
  "success": true,
  "code": 201,
  "message": "수정 요청 성공",
  "data": {
    "revisionRequestId": 111,
    "submissionId": 91,
    "reason": "메뉴명이 잘 보이지 않습니다. 글자 크기를 키우고 매장 로고를 우측 상단에 추가해주세요.",
    "revisionCount": 1,
    "revisionLimit": 4,
    "remainingRevisionCount": 3,
    "requestedAt": "2026-09-03T15:00:00+09:00"
  }
}
```

#### 409 CONFLICT

```json
{
  "success": false,
  "code": 409,
  "message": "수정 요청 가능 횟수를 모두 사용했습니다.",
  "data": {
    "revisionCount": 4,
    "revisionLimit": 4
  }
}
```

---

# 9. 결과물 승인 API

## 9-1. 결과물 최종 승인

### API 설명

사장님이 제출된 결과물을 최종 승인한다.

승인 완료 시 제출 상태는 `APPROVED`, 매칭 및 공고 상태는 `COMPLETED`로 변경한다.

OWNER 권한만 요청할 수 있다.

### REQUEST

`POST /api/v1/submissions/{submissionId}/approve`

Request Body 없음.

### RESPONSE

#### 200 OK

```json
{
  "success": true,
  "code": 200,
  "message": "결과물 승인 성공",
  "data": {
    "submissionId": 93,
    "submissionStatus": "APPROVED",
    "matchingId": 71,
    "matchingStatus": "COMPLETED",
    "completedAt": "2026-09-05T17:30:00+09:00"
  }
}
```

---

# 10. 상태값 정의

## Member Role

| Value | Description |
|---|---|
| `STUDENT` | 학생 |
| `OWNER` | 사장님 |

## Member Status

| Value | Description |
|---|---|
| `ACTIVE` | 정상 회원 |
| `WITHDRAWN` | 탈퇴 회원 |

## JobPost Category

| Value | Description |
|---|---|
| `WEB` | 웹 개발 |
| `IMAGE` | 이미지 제작 |
| `SNS` | SNS 운영 및 콘텐츠 |

## JobPost Status

| Value | Description |
|---|---|
| `OPEN` | 지원 가능 |
| `MATCHED` | 매칭 완료 |
| `COMPLETED` | 거래 완료 |
| `CANCELED` | 공고 취소 |

## Application Status

| Value | Description |
|---|---|
| `PENDING` | 지원 대기 |
| `ACCEPTED` | 지원 수락 |
| `REJECTED` | 지원 거절 |
| `CANCELED` | 지원 취소 |

## Matching Status

| Value | Description |
|---|---|
| `IN_PROGRESS` | 작업 진행 중 |
| `SUBMITTED` | 결과물 제출 |
| `REVISION_REQUESTED` | 수정 요청 |
| `COMPLETED` | 거래 완료 |
| `CANCELED` | 거래 취소 |

## Submission Status

| Value | Description |
|---|---|
| `SUBMITTED` | 결과물 제출 |
| `REVISION_REQUESTED` | 수정 요청됨 |
| `APPROVED` | 최종 승인 |

---

# 11. API 목록

| Method | Endpoint | 권한 | Description |
|---|---|---|---|
| POST | `/api/v1/auth/signup/student` | Public | 학생 회원가입 |
| POST | `/api/v1/auth/signup/owner` | Public | 사장님 회원가입 |
| POST | `/api/v1/auth/login` | Public | 로그인 |
| GET | `/api/v1/members/me` | 로그인 | 내 정보 조회 |
| GET | `/api/v1/universities` | Public | 대학 목록 조회 |
| POST | `/api/v1/job-posts/ai-refine` | OWNER | AI 공고 정제 |
| POST | `/api/v1/job-posts` | OWNER | 공고 등록 |
| GET | `/api/v1/job-posts` | 로그인 | 공고 목록 조회 |
| GET | `/api/v1/job-posts/{jobPostId}` | 로그인 | 공고 상세 조회 |
| GET | `/api/v1/job-posts/me` | OWNER | 내 공고 조회 |
| PATCH | `/api/v1/job-posts/{jobPostId}` | OWNER | 공고 수정 |
| DELETE | `/api/v1/job-posts/{jobPostId}` | OWNER | 공고 취소 |
| POST | `/api/v1/job-posts/{jobPostId}/images` | OWNER | 공고 이미지 추가 |
| DELETE | `/api/v1/job-posts/{jobPostId}/images/{imageId}` | OWNER | 공고 이미지 삭제 |
| POST | `/api/v1/job-posts/{jobPostId}/applications` | STUDENT | 공고 지원 |
| GET | `/api/v1/applications/me` | STUDENT | 내 지원 조회 |
| GET | `/api/v1/job-posts/{jobPostId}/applications` | OWNER | 지원자 조회 |
| POST | `/api/v1/applications/{applicationId}/cancel` | STUDENT | 지원 취소 |
| POST | `/api/v1/applications/{applicationId}/accept` | OWNER | 지원 수락 및 매칭 |
| GET | `/api/v1/matchings/me` | 로그인 | 내 매칭 조회 |
| GET | `/api/v1/matchings/{matchingId}` | 참여자 | 매칭 상세 조회 |
| POST | `/api/v1/matchings/{matchingId}/submissions` | STUDENT | 결과물 제출 |
| GET | `/api/v1/matchings/{matchingId}/submissions` | 참여자 | 결과물 제출 이력 |
| POST | `/api/v1/submissions/{submissionId}/revision-requests` | OWNER | 수정 요청 |
| POST | `/api/v1/submissions/{submissionId}/approve` | OWNER | 결과물 최종 승인 |

---

# 12. 핵심 거래 상태 흐름

```text
공고 등록
OPEN

        ↓ 학생 지원

Application
PENDING

        ↓ 사장님 지원 수락

JobPost
OPEN → MATCHED

Application
PENDING → ACCEPTED

Matching
IN_PROGRESS

        ↓ 학생 결과물 제출

Matching
IN_PROGRESS → SUBMITTED

Submission
SUBMITTED

        ↓ 사장님 수정 요청

Matching
SUBMITTED → REVISION_REQUESTED

Submission
SUBMITTED → REVISION_REQUESTED

        ↓ 학생 수정본 제출

Matching
REVISION_REQUESTED → SUBMITTED

새 Submission 생성

        ↓ 반복 가능
        최대 4회

        ↓ 사장님 최종 승인

Submission
SUBMITTED → APPROVED

Matching
SUBMITTED → COMPLETED

JobPost
MATCHED → COMPLETED
```

---

# 13. 공통 에러 응답

## 400 BAD REQUEST

```json
{
  "success": false,
  "code": 400,
  "message": "잘못된 요청입니다.",
  "data": null
}
```

## 401 UNAUTHORIZED

```json
{
  "success": false,
  "code": 401,
  "message": "로그인이 필요합니다.",
  "data": null
}
```

## 403 FORBIDDEN

```json
{
  "success": false,
  "code": 403,
  "message": "요청에 대한 권한이 없습니다.",
  "data": null
}
```

## 404 NOT FOUND

```json
{
  "success": false,
  "code": 404,
  "message": "요청한 데이터를 찾을 수 없습니다.",
  "data": null
}
```

## 409 CONFLICT

```json
{
  "success": false,
  "code": 409,
  "message": "현재 상태에서는 요청을 처리할 수 없습니다.",
  "data": null
}
```
