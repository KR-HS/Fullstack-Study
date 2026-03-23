# Index
 + DB에서 특정 컬럼을 빠르게 찾기 위해 DB가 따로 만들어두는 **정렬된 자료구조**
 + 내부 구조는 대부분 B-Tree(Balanced Tree)로 O(log N)으로 탐색 가능
 
 + **생성방법**
     + 명시적으로 만들어야 사용할 수 있음 (PK는 자동으로 index가 생성됨)
     > ```sql
     > -- 직접 생성
     > CREATE INDEX idx_user_email ON users(email);
     > 
     > -- PK는 자동 생성 
     > CREATE TABLE users (
     >   id SERIAL PRIMARY KEY,  -- 자동으로 Index 생성됨
     >   email VARCHAR(100)
     > ); 
     > ```
     
 + **Index를 타지 못하는 경우**
     | 경우 | 예시 | 이유 |
     |:-----|:-----|:-----|
     | 앞에 % 붙은 LIKE | `LIKE '%홍길동'` | 시작점을 특정할 수 없음 |
     | 컬럼에 연산 적용 | `WHERE salary * 2 > 1000` | 정렬된 원본값과 다름 |
     | 전체 조회 | `SELECT * FROM users` | 전부 읽어야 함 |
     | NULL 비교 | `WHERE col IS NULL` | Index에 NULL이 포함 안되는 경우 있음 |


 + **모든 컬럼에 Index를 걸지 않는 이유**
     + Index는 **조회는 빠르게, 쓰기/수정은 느리게** 만듬
     + INSERT / UPDATE / DELETE 할 때마다 Index 자료구조도 같이 갱신해야함
     + **WHERE / JOIN / ORDER BY에 자주 쓰이는 컬럼**에만 최소한으로 설정

---

# RDBMS / NoSQL

## RDBMS
 + 테이블 구조, 스키마 고정
 + 관계(JOIN)로 데이터 연결
 + ACID 트랜잭션 보장
 + 수직 확장(Scale-up)
 + 종류 : PostgreSQL, Oracle, MySQL
 + **데이터 간 관계가 복잡하고 정합성이 중요할 때(금융·주문·회원 정보 등)** 사용


## NoSQL
 + 스키마 유연 (Json / 키-값 등)
 + 관계 없이 독립 저장
 + 일관성보단 가용성 우선
 + 수평 확장(Scale-out)
 + Redis, MongoDB, Cassandra
 + **대용량 트래픽, 빠른 읽기/쓰기, 유연한 구조가 필요할 때(캐시·로그·실시간 데이터 등)** 사용

     ### Redis
     + In-Memory 기반으로 빠르고, 대부분 캐시와 세션 용도로 분리해서 사용
 
     ### MongoDB
     + 스키마리스 Document 구조라 데이터 형태가 유연하고, 구조가 자주 바뀌거나 관계가 단순한 데이터에 적합
     + 구조
         + Database : DB
         + Collection : RDBMS에서 Table역할 (데이터 묶음)
         + Document : RDBMS에서 Row역할 (데이터 한 건)
         + Field : RDBMS에서 Column역할 (데이터 항목)


### 스키마(Schema)
 + DB의 설계도. 테이블 구조, 컬럼명, 자료형, 제약조건을 정의한 것
 + RDBMS는 스키마를 DB가 강제함 → 위반 시 입력 거부
 + NoSQL은 스키마리스(Schemaless) → 정합성을 애플리케이션 코드에서 관리
 > ```sql
 > CREATE TABLE auction (
 >   id          SERIAL PRIMARY KEY,
 >   title       VARCHAR(200) NOT NULL,
 >   status      VARCHAR(20) CHECK (status IN ('OPEN','CLOSED')),
 >   seller_id   INTEGER REFERENCES users(id)
 > );
 > ```

---

# 트랜잭션 / ACID

## 트랜잭션
 + DB 작업의 논리적 단위
 + **전부 성공하거나, 하나라도 실패하면 전부 취소**
 > ```sql
 > BEGIN;  -- 트랜잭션 시작
 >   UPDATE accounts SET balance = balance - 100000 WHERE id = 'A';
 >   UPDATE accounts SET balance = balance + 100000 WHERE id = 'B';
 > COMMIT;  -- 둘 다 성공하면 확정
 > -- 중간에 오류 → ROLLBACK 자동 처리
 > ```

## ACID 특성
 | **속성** | **설명** | **예시** |
 |:-----|:-----|:-----|
 | **Atomicity(원자성)** | 전부 성공 or 전부 실패, 중간 상태 없음 | 이체 중 오류 → 둘 다 취소 |
 | **Consistency(일관성)** | 트랜잭션 전후로 DB 규칙 항상 유지 | 잔액이 음수가 되는 상황 방지 | 
 | **Isolation(격리성)** | 동시 트랜잭션이 서로 영향 안 줌 | 동시에 같은 좌석 예약 시 충돌 방지 |
 | **Durability(지속성)** | 커밋된 데이터는 장애 나도 유지 | 서버 꺼져도 이체 결과 보존 |

---

# PostgreSQL

## Oracle 대비 차이점
 + 오픈소스 (라이선스 무료)
 + 기능은 엔터프라이즈급으로 유사
 + 공공기관 Oracle 대체 전환 사례 증가 중

## MariaDB 대비
 + JSON 타입 지원 강력
 + 동시성 처리(MVCC) 우수
 + 복잡한 쿼리 분석에 더 강함

 + **MVCC란**
     + Multi-Version Concurrency Control
     + 데이터를 **수정할 때 기존 데이터를 바로 덮어쓰지 않고 새 버전을 만듬**
     + 읽기 작업이 쓰기 작업에 의해 블로킹되지 않아서, 동시 접속자가 많아도 성능이 유지됨

## MySQL 대비
 + 객체-관계형(ORDBMS) → 커스텀 타입·함수 추가 가능
 + JSON 연산/인덱싱 더 강력
 + 윈도우 함수, CTE 등 복잡한 쿼리에 강함
 + 동시 접속 많을 때 MVCC 처리 우수
 + 단순 읽기 속도는 MySQL이 더 빠름

---

# Redis
 + **In-Memory 기반 Key-Value 저장소**
 + **데이터를 디스크가 아닌 RAM에 저장**해서 DB보다 읽기/쓰기가 압도적으로 빠름
 + 서버가 꺼지면 데이터가 사라질 수 있음(영속성 옵션으로 보완 가능)

 + **주요 사용 용도**
     | 용도 | 동작 방식 | 예시 |
     |:-----|:-----|:-----|
     | **캐시** | DB 조회 결과를 Redis에 저장 → 다음 요청은 DB 안 거치고 Redis에서 응답 | 상품 목록, 자주 조회되는 데이터 |
     | **세션** | 로그인 세션 정보를 Redis에 저장 → 서버 여러 대여도 세션 공유 가능 | 로그인 상태 유지 |
     | **TTL** | 저장 시 만료 시간 설정 → 시간 지나면 자동 삭제 | 세션 만료, 인증번호 유효 시간 |

 + **캐시 전용 / 세션 전용으로 나누는 이유**
     + TTL 정책이 달라서 캐시는 짧게, 세션은 길게 유지해야 함
     + 장애 시 영향 범위를 분리하기 위함
     > ```sql
     > -- 세션 저장 예시 (TTL 30분)
     > SET session:userId:abc123 "user_data" EX 1800
     > 
     > -- 캐시 저장 예시 (TTL 5분)
     > SET cache:product:list "product_json" EX 300
     > ```