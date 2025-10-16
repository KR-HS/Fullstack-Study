# 운영체제(OS)

## 1. 프로세스(Process)
 + 프로그램이 메모리에 올라가 실행 중인 상태

 + **구성요소**
     1. **Text Segment** : 실행 코드
     2. **Data Segment** : 전역 변수, static 변수
     3. **Heap** : 동적 메모리
     4. **Stack** : 함수 호출, 지역 변수
     5. **레지스터(Register)** : CPU 연산용, 주소, 상태 저장
     6. **PC(Program Counter)** : 다음 명령어 주소

 + **프로세스 상태**
     + `Ready` : CPU 할당 대기
     + `Running` : CPU에서 명령어 실행 중
     + `Waiting / Blocked` : I/O등 이벤트 대기
     + `Terminated` : 실행 종료

 + **메모리 구조**
     + **Stack** : LIFO, 함수 호출/리턴, 지역 변수 저장
     + **Heap** : 동적 메모리, 객체 저장, GC(Java) 관리
     + **Data Segment** : 전역/Static 변수
     + **Text Segment** : 코드 저장

 + **레지스터 & PC**
     + **레지스터** : CPU 내부 초고속 저장소
         + 데이터 레지스터, 주소 레지스터, 상태 레지스터
     + **PC** : 다음 명령어 주소, context switch 시 저장/복원

---

## 2. 스레드(Thread) & 동기화
 + **스레드(Thread)**
     + `프로세스 내 실행 단위`
     + 스택은 독립적, Heap/Data 공유 -> 빠른 통신 가능, Race Condition 발생 가능

 + **동기화**
     + **문제** : 여러 스레드가 공유 데이터 동시에 접근 -> 일관성 문제
     + **방법** 
         1. Mutex / Lock : 상호 배제, 하나만 접근 가능
             + **Mutex(Mutual Exclusion)**
                 + 한 번에 하나의 스레드만 임계 영역에 접근할 수 있도록 하는 배타적 잠금
                 
                 + **특징**
                     + 공유 자원에 대해 단일 스레드 접근 보장
                     + 일반적으로 1개의 Lock만 사용
                     + Deadlock 가능성 있음 -> 사용시 주의
                 
                 + 예시
                 > ```java
                 > class SharedResource {
                 >     private int value = 0;
                 >     private final Object mutex = new Object();
                 > 
                 >     public void increment() {
                 >         synchronized(mutex) { // mutex를 이용한 배타적 접근
                 >             value++;
                 >         }
                 >     }
                 > }
                 > ``` 

             + **Lock**
                 + 스레드 간 공유 자원 접근 제어 객체
                 + Java에서는 **ReentrantLock** 같은 구현체 사용
                 
                 + **특징**
                     + `lock()` -> 임계 영역 접근 시작
                     + `unlock()` -> 임계 영역 종료
                     + synchronized보다 유연성 높음(tryLock, 공정성 설정 가능)
                 
                 + 예시
                 > ```java
                 > class SharedCounter {
                 >     private int count = 0;
                 >     private ReentrantLock lock = new ReentrantLock();
                 > 
                 >     public void increment() {
                 >         lock.lock();
                 >         try {
                 >             count++;
                 >         } finally {
                 >             lock.unlock();
                 >         }
                 >     }
                 > }
                 > ```

         2. Semaphore : N개까지 동시 접근 허용
             + 특정 자원에 **동시에 접근할 수 있는 스레드 수**를 제한
             
             + **특징** 
                 + 카운팅 개념 : `Semaphore(n)` -> 최대 n개 스레드 동시 접근 가능
                 + Mutex는 Semaphore(1)와 동일
            
             + 예시
             > ```java
             > Semaphore semaphore = new Semaphore(3); // 최대 3개 스레드 허용
             > 
             > semaphore.acquire(); // 접근 허용 전 대기
             > try {
             >     // 임계 영역
             > } finally {
             >     semaphore.release(); // 사용 후 반환
             > }
             > ```

         3. Synchronized(java), ReentrantLock
             + **synchronized**
                 + Java 키워드, **객체 단위로 임계 영역 보호**

                 + **특징**
                     + 블록 단위(`synchronized(obj){})` 또는 메서드 단위(`synchronized void method())` 사용
                     + 자동으로 Lock 획득/해제 -> Deadlock 가능성 낮음

                 + 예시
                 > ```java
                 > public synchronized void increment() {
                 >     count++;
                 > }
                 > ``` 
            
             + **ReentrantLock**
                 + Lock 인터페이스 구현체, **재진입 가능**
                 
                 + **특징**
                     + 같은 스레드가 여러 번 lock() 해도 데드락 없이 통과 가능
                     + 공정성(fairness) 설정 가능 -> 먼저 요청한 스레드가 먼저 Lock 획득
                     + synchronized보다 세밀한 제어 가능

                 + 예시
                 > ```java
                 > ReentrantLock lock = new ReentrantLock(true); // 공정성 true
                 > lock.lock();
                 > try {
                 >     // 임계 영역
                 > } finally {
                 >     lock.unlock();
                 > }
                 > ```

 + **스레드 풀(Thread Pool)**
     + 미리 N개의 스레드 생성, 재사용
     + 작업 요청 시 스레드 할당 -> 생성/삭제 비용 절감
     > ```java
     > ExecutorService pool = Executors.newFixedThreadPool(5);
     > pool.submit(() -> System.out.println("작업 수행"));
     > ```



---

# 네트워크(Network)

## 0. OSI 7계층
 + **1. 물리(Physical) 계층**
     + 실제 신호 전송, 비트 스트림 전달
     + 예시
         + 케이블, 무선, 비트 전기적 신호/광 신호.
         + 0과 1을 물리적으로 전달

 + **2. 데이터 링크(Data Link)계층**
     + 프레임 전송, 오류 감지, 물리적 주소 관리
     + 예시
         + Ethernet, MAC, 스위치
         + 물리적 네트워크에서 오류 없는 데이터 전달

 + **3. 네트워크(Network) 계층**
     + 패킷 전달, 경로 선택, 주소 지정
     + 예시
         + IP, ICMP, 라우터
         + 목적지 IP를 기준으로 패킷 라우팅

 + **4. 전송(Transport) 계층**
     + 종단 간 통신, 신뢰성 제공, 흐름 제어
     + 예시
         + TCP(신뢰성, 연결형), UDP(비연결형)
         + 포트번호 관리, 데이터 재전송, 오류 검출

 + **5. 세션(Session) 계층**
     + 세션 관리, 연결 설정/유지/종료, 동기화
     + 예시
         + API 세션, RPC 세션
         + 로그인 세션 관리, 클라이언트와 서버 간 통신 연결 유지

 + **6. 표현(Presentation) 계층**
     + 데이터 표현, 인코딩/복호화, 암호화/압축
     + 예시
         + JPEG, GIF, SSL/TLS, ASCII
         + 데이터를 네트워크에 맞게 포맷

 + **7. 응용(Application) 계층**
     + 사용자와 직접 상호작용, 애플리케이션 간 통신 제공
     + 예시
         + HTTP, FTP, SMTP, DNS
         + 사용자가 웹 브라우저로 요청하면 이 계층에서 처리

---

## 1. TCP/IP
 + **계층** : Application -> (Presentation -> Session ->) Transport(TCP/UDP) -> Network(IP) -> Data Link -> Physical

 + **TCP 특징**
     + 연결형(Connection-Oriented)
     + 신뢰성 보장, 순서 보장, 오류 검출 및 재전송
     + 3-way Handshake : SYN -> SYN/ACK -> ACK
         + **1. SYN(Synchronize)**
             + 클라이언트가 서버로 연결 요청
             + "서버에 연결 요청, 클라이언트 초기 시퀀스 번호(ISN)=x"
             + 패킷 플래그 : `SYN=1`

         + **2. SYN-ACK**
             + 서버가 요청 수신 후 승인
             + "서버도 연결준비 완료, 서버 ISN=y, 클라이언트 ISN=x"
             + 패킷 플래그 : `SYN=1, ACK=1`

         + **3. ACK**
             + 클라이언트가 서버 응답 확인
             + "확인 후 통신 시작"
             + 패킷 플래그 : `ACK=1`


## 2. UDP
 + 비연결형(Connectionless)
 + 빠르지만 순서/신뢰성 보장 없음
 + 예 : 실시간 스트리밍, 게임


## 3. HTTP/HTTPS
 + **HTTP**
     + **무상태(stateless)** : 요청 간 서버 상태 저장 X
     + 요청 구조
         + **Header** : 메타데이터, 인증 정보, 콘텐츠 타입
         + **Body** : 실제 데이터

 + **HTTPS**
     + **TLS/SSL** 기반 암호화
     + **동작**
         1. 클라이언트 -> 서버 요청
         2. 서버 -> 인증서 + 공개키 전달
         3. 클라이언트 인증서 검증
         4. 공개키로 비밀키 교환
         5. 이후 비밀키로 암호화/복호화


## 4. REST API
 + HTTP + URI를 이용한 자원(Resource) 접근 규칙
 + **JSON 형태** 데이터 송수신

 + **HTTP 메서드**
     + GET : 조회(Idempotent)
     + POST : 생성
     + PUT : 갱신(Idempotent)
     + DELETE : 삭제(Idempotent)

 + 예시
 > ```java
 > @PostMapping("/users")
 > public User create(@RequestBody User user) { ... }
 > 
 > @GetMapping("/users/{id}")
 > public User read(@PathVariable int id) { ... }
 > ```

 + `form`의 경우 GET, POST만 지원
 + `Ajax, fetch`를 사용하는 경우 RESTful 규칙에 따라 사용


## 5. 클라이언트-서버 상태 관리
 + **쿠키** : 클라이언트 저장
 + **세션** : 서버에 저장, 세션 ID로 조회
 + **JWT(Json Web Token)** : 토큰 기반 인증 방식, 서버가 세션을 유지하지 않고도 사용자 인증(stateless 인증)을 관리
     + **구조** : `Header.Payload.Signature`
         + **1. Header**
             + 토큰 타입, 서명 알고리즘
             > ```json
             > { "alg": "HS256", "typ": "JWT" }
             > ```

         + **2. Payload**
             + 사용자 정보와 토큰 유효기간 포함
             > ```json
             > { "sub": "user123", "exp": 1700000000 }
             > ```

         + **3. Signature**
             + 서버 비밀키로 Header + Payload를 서명
             + 변조 방지

     + **동작 방식**
         1. 로그인 성공 -> 서버 JWT 발급
             + 서버가 자동으로 발급 : 
                 1. 로그인 정보 검증 -> 유효
                 2. JWT 생성(Header, Payload, Signature)
                 3. 클라이언트에게 반환(Authorization 헤더나 body)

         2. 클라이언트 저장 -> HTTP Header(`Authorization: Bearer <token>`)로 요청 시 전달
             + **JWT 저장 위치**
                 + `LocalStorage`
                     + 브라우저에 영구 저장
                     + 새로고침, 브라우저 재시작 시 유지
                     + XSS 공격 시 탈취 가능
                         + **XSS (Cross-Site Scripting)**
                             + 공격자가 악성 스크립트를 웹 페이지에 삽입하여, 다른 사용자의 브라우저에서 실행되도록 하는 공격

                 + `SessionStorage`
                     + 브라우저 탭/세션에 저장
                     + 탭 별로 데이터 격리
                     + 브라우저 닫으면 사라짐
                     + XSS 공격 시 탈취 가능

                 + `Cookie`
                     + 브라우저 자체에 저장
                     + HttpOnly + Secure 옵션 -> JS에서 접근 불가, HTTPS 통신에서만 전송
                     + 서버에 자동 전송 -> CSRF 위험
                     + 브라우저에 따라 용량 제한
                     
                     + 예시 
                     > ```java
                     > ResponseCookie cookie = ResponseCookie.from("accessToken", token)
                     >         .httpOnly(true)
                     >         .secure(true)
                     >         .path("/")
                     >         .maxAge(3600)
                     >         .build();
                     > response.addHeader("Set-Cookie", cookie.toString());
                     >
                     > // Spring에서 쿠키 받을 때 
                     > @GetMapping("/data")
                     > public String getData(@CookieValue("accessToken") String token) {
                     >     // HttpOnly 쿠키지만 서버에서는 읽을 수 있음
                     >     return "Token received: " + token;
                     > }
                     > ```

         3. 서버는 서버 DB 세션 없이 JWT 서명 검증만으로 사용자 인증
         4. 유효기간 만료 -> 토큰 사용 불가 -> 다시 로그인 필요