# BucketRoulette Server



버킷룰렛은 사용자가 버킷리스트를 등록하고, 룰렛으로 하나를 뽑아 도전한 뒤 완료 인증글을 친구 피드에 공유할 수 있는 해커톤용 백엔드 서비스입니다.



## 서비스 개요



사용자는 최대 8개의 버킷을 등록할 수 있습니다. 룰렛은 아직 시작하지 않은 TODO 상태의 버킷만 대상으로 하며, 이미 도전 중인 버킷이 있으면 새 룰렛을 돌릴 수 없습니다. 도전을 완료하면 인증글을 작성할 수 있고, 공개 설정이 PUBLIC인 인증글은 친구 피드에 노출됩니다.



## 주요 기능



- 회원가입 / 로그인

- JWT 기반 인증

- 내 정보 조회, 닉네임 수정, 프로필 이모지/색상 수정

- 버킷 등록, 목록 조회, 삭제

- 룰렛으로 도전할 버킷 선택

- 버킷 도전 시작, 완료, 미완료 처리

- 완료된 버킷에 대한 인증글 작성

- 인증글 공개 범위 설정: PUBLIC, PRIVATE

- 인증글 친구 태그

- 친구 요청, 받은 요청 조회, 수락, 거절, 친구 목록 조회

- 친구의 공개 인증글 피드 조회

- 인증글 좋아요 등록/취소

- AWS S3 Presigned URL 기반 이미지 업로드



## 기술 스택



| 구분 | 기술 |

| --- | --- |

| Language | Java 17 |

| Framework | Spring Boot 4.1.0 |

| Web | Spring Web MVC |

| Persistence | Spring Data JPA |

| Database | MySQL |

| Security | Spring Security, JWT |

| Validation | Bean Validation |

| Image Storage | AWS S3, AWS SDK for Java |

| Build Tool | Gradle |

| 기타 | Lombok |



## 서버 아키텍처

![server architecture](./docs/server-architecture.png)

기본 흐름은 Controller -> Service -> Repository -> MySQL 구조입니다. 인증은 Spring Security 필터에서 JWT를 검증하고, 이미지 업로드는 서버가 S3 Presigned URL을 발급한 뒤 클라이언트가 S3로 직접 업로드하는 방식입니다.



    Client

      -> Spring Security / JWT Filter

      -> Controller

      -> Service

      -> Repository

      -> MySQL



    Image Upload:

    Client -> /api/images/presigned-url -> S3 Presigned URL 발급

    Client -> S3 PUT uploadUrl

    Client -> /api/posts/{bucketId} with imageUrl



## 패키지 구조



    com.youthroulette.server

    ├── auth       # 회원가입, 로그인

    ├── bucket     # 버킷 등록, 룰렛, 도전 상태 관리

    ├── common     # 공통 예외, 에러 응답

    ├── config     # AWS 설정

    ├── friend     # 친구 요청, 친구 목록

    ├── image      # S3 Presigned URL 발급

    ├── post       # 인증글, 좋아요, 친구 태그

    ├── security   # JWT, Spring Security 설정

    └── user       # 내 정보, 닉네임, 프로필 수정



## 핵심 도메인 규칙



- 회원가입은 loginId, password, nickname만 사용합니다.

- 사용자는 버킷을 최대 8개까지 등록할 수 있습니다.

- 버킷 상태는 TODO, IN_PROGRESS, COMPLETED입니다.

- 룰렛은 TODO 상태 버킷만 대상으로 합니다.

- IN_PROGRESS 상태 버킷이 하나라도 있으면 룰렛을 돌릴 수 없습니다.

- 룰렛 결과에서 도전 시작을 누르면 해당 버킷은 IN_PROGRESS가 됩니다.

- 도전 완료 시 버킷은 COMPLETED가 됩니다.

- COMPLETED 상태 버킷만 인증글 작성이 가능합니다.

- 인증글은 imageUrl, reviewText, visibility를 가집니다.

- PUBLIC 인증글은 친구 피드에 노출되고, PRIVATE 인증글은 본인만 조회합니다.

- 인증글 작성 시 친구 태그를 등록할 수 있습니다.

- 연속도전 기능은 제외했습니다.



## 주요 API



### Auth



| Method | Path | 설명 |

| --- | --- | --- |

| POST | /api/auth/signup | 회원가입 |

| POST | /api/auth/login | 로그인 |



### User



| Method | Path | 설명 |

| --- | --- | --- |

| GET | /api/users/me | 내 정보 조회 |

| PATCH | /api/users/nickname | 닉네임 수정 |

| PATCH | /api/users/profile | 프로필 이모지/색상 수정 |



### Bucket



| Method | Path | 설명 |

| --- | --- | --- |

| POST | /api/buckets | 버킷 등록 |

| GET | /api/buckets | 내 버킷 목록 조회 |

| DELETE | /api/buckets/{bucketId} | 버킷 삭제 |

| POST | /api/buckets/roulette | 룰렛 돌리기 |

| PATCH | /api/buckets/{bucketId}/start | 도전 시작 |

| PATCH | /api/buckets/{bucketId}/complete | 버킷 완료 처리 |

| PATCH | /api/buckets/{bucketId}/incomplete | 버킷 미완료 처리 |



### Post



| Method | Path | 설명 |

| --- | --- | --- |

| POST | /api/posts/{bucketId} | 인증글 작성 |

| GET | /api/posts/feed | 친구 피드 조회 |

| GET | /api/posts/me | 내 인증글 조회 |

| DELETE | /api/posts/{postId} | 인증글 삭제 |

| POST | /api/posts/{postId}/likes | 좋아요 등록 |

| DELETE | /api/posts/{postId}/likes | 좋아요 취소 |



### Friend



| Method | Path | 설명 |

| --- | --- | --- |

| POST | /api/friends/request | 친구 요청 보내기 |

| GET | /api/friends/requests/received | 받은 친구 요청 조회 |

| PATCH | /api/friends/{friendId}/accept | 친구 요청 수락 |

| PATCH | /api/friends/{friendId}/reject | 친구 요청 거절 |

| GET | /api/friends | 친구 목록 조회 |



### Image



| Method | Path | 설명 |

| --- | --- | --- |

| POST | /api/images/presigned-url | 이미지 업로드용 Presigned URL 발급 |



## 실행 방법



### 1. MySQL 데이터베이스 생성



    CREATE DATABASE bucketroulette CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;



### 2. 환경 변수 설정



    DB_URL=jdbc:mysql://localhost:3306/bucketroulette?serverTimezone=Asia/Seoul&characterEncoding=UTF-8

    DB_USERNAME=root

    DB_PASSWORD=1234

    JWT_SECRET=your-jwt-secret

    JWT_ACCESS_TOKEN_EXPIRATION=3600000

    AWS_REGION=ap-northeast-2

    S3_BUCKET_NAME=your-s3-bucket

    AWS_ACCESS_KEY=your-access-key

    AWS_SECRET_KEY=your-secret-key



### 3. 서버 실행



    ./gradlew bootRun



Windows 환경에서는 다음 명령을 사용할 수 있습니다.



    gradlew.bat bootRun



기본 포트는 8080입니다.



## 인증 방식



로그인 성공 시 JWT access token을 반환합니다.



    {

      "accessToken": "...",

      "tokenType": "Bearer"

    }


인증이 필요한 API는 아래 헤더를 포함해야 합니다.



    Authorization: Bearer {accessToken}



## 이미지 업로드 흐름



1. 클라이언트가 /api/images/presigned-url로 fileName, contentType을 보냅니다.

2. 서버는 S3 Presigned uploadUrl과 최종 접근용 imageUrl을 반환합니다.

3. 클라이언트는 uploadUrl로 S3에 직접 PUT 업로드합니다.

4. 인증글 작성 시 반환받은 imageUrl을 /api/posts/{bucketId} 요청에 포함합니다.



예시 응답:



    {

      "uploadUrl": "https://...",

      "imageUrl": "https://bucket-name.s3.ap-northeast-2.amazonaws.com/images/..."

    }



## 에러 응답 형식



공통 에러 응답은 code, message, status, errors 형식을 사용합니다.



    {

      "code": "VALIDATION_ERROR",

      "message": "요청 값이 올바르지 않습니다.",

      "status": 400,

      "errors": [

        {

          "field": "title",

          "reason": "버킷 제목은 1~100자여야 합니다."

        }

      ]

    }



예시 에러 코드:



- UNAUTHORIZED

- LOGINID_DUPLICATED

- INVALID_CREDENTIALS

- USER_NOT_FOUND

- BUCKET_NOT_FOUND

- NO_BUCKET_ITEMS

- ALREADY_IN_PROGRESS

- BUCKET_ALREADY_STARTED

- BUCKET_ALREADY_VERIFIED

- INVALID_BUCKET_STATUS

- POST_NOT_FOUND

- ALREADY_LIKED

- LIKE_NOT_FOUND

- FRIEND_REQUEST_DUPLICATED

- SELF_FRIEND_REQUEST

- FRIEND_REQUEST_NOT_FOUND

- INVALID_IMAGE_TYPE



## 고민했던 부분



### 1. 룰렛 도전 상태를 어떻게 제한할 것인가



룰렛은 단순히 랜덤으로 버킷을 뽑는 기능처럼 보이지만, 서비스 규칙상 동시에 여러 도전을 진행하면 흐름이 복잡해집니다. 그래서 IN_PROGRESS 상태의 버킷이 하나라도 있으면 룰렛을 돌릴 수 없도록 제한했습니다. 이 규칙 덕분에 사용자는 항상 하나의 도전에 집중하고, 버킷 상태 전환도 명확해집니다.



### 2. 인증글 작성 가능 시점



인증글은 도전 완료를 증명하는 기능이므로 COMPLETED 상태의 버킷에서만 작성할 수 있도록 했습니다. 아직 도전 중이거나 시작하지 않은 버킷에 인증글을 작성하면 서비스 의미가 흐려지기 때문에 INVALID_BUCKET_STATUS 에러로 차단했습니다.



### 3. 친구 피드 공개 범위



인증글에는 PUBLIC, PRIVATE 공개 범위가 있습니다. 친구 피드에는 친구가 작성한 PUBLIC 인증글만 노출되도록 했습니다. 이를 통해 사용자는 본인만 볼 인증글과 친구에게 공유할 인증글을 구분할 수 있습니다.



### 4. 에러 코드를 세분화한 이유



처음에는 HTTP status와 message만으로 에러를 표현할 수도 있었지만, 프론트엔드에서 안정적으로 분기하기 어렵다는 문제가 있었습니다. 예를 들어 같은 409라도 아이디 중복, 이미 도전 중인 버킷, 이미 좋아요한 게시글은 서로 다른 처리가 필요합니다. 그래서 LOGINID_DUPLICATED, ALREADY_IN_PROGRESS, ALREADY_LIKED처럼 구체적인 ErrorCode를 정의했습니다.



### 5. 이미지 업로드 방식



이미지를 서버가 직접 받아 S3에 업로드할 수도 있지만, 서버 부하와 구현 복잡도를 줄이기 위해 Presigned URL 방식을 선택했습니다. 서버는 업로드 권한이 포함된 URL만 발급하고, 클라이언트가 S3에 직접 업로드합니다. 인증글에는 최종 imageUrl만 저장하므로 게시글 도메인과 이미지 저장소가 느슨하게 연결됩니다.



### 6. 친구 태그 모델링



인증글에 친구를 태그할 수 있어야 했기 때문에 posts_friends_tag 연결 테이블을 두었습니다. 단순히 사용자 ID를 직접 저장하는 대신, 이미 수락된 친구 관계인 friends를 참조하도록 설계해서 유효하지 않은 친구 태그를 막을 수 있게 했습니다.



### 7. 삭제 정책



버킷과 인증글은 연관 데이터가 많습니다. 버킷을 삭제하면 연결된 인증글, 좋아요, 친구 태그도 함께 삭제될 수 있도록 JPA cascade와 orphan removal을 사용했습니다. 덕분에 고아 데이터가 남는 문제를 줄일 수 있지만, 완료된 버킷 삭제 시 인증글도 함께 사라질 수 있으므로 서비스 정책상 주의가 필요합니다.



## ERD 요약



    users 1 : N bucket_items

    users 1 : N posts

    bucket_items 1 : N posts

    users 1 : N post_likes

    posts 1 : N post_likes

    users 1 : N friends as requester

    users 1 : N friends as receiver

    posts 1 : N posts_friends_tag

    friends 1 : N posts_friends_tag

