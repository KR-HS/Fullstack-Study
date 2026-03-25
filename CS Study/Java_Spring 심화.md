# OOP 4대 원칙

## 1. 캡슐화 (Encapsulation)
 + **데이터(필드)와 메서드를 하나로 묶고, 외부에서 직접 접근 못하게 숨기는것**
 + 예시 : `private필드 + getter/setter`
 > ```java
 > public class User {
 >     private String name;  // 외부 직접 접근 불가
 > 
 >     public String getName() { return name; }
 >     public void setName(String name) { this.name = name; }
 > }
 > ```

## 2. 상속 (Inheritance)
 + **부모 클래스의 필드·메서드를 자식 클래스가 물려받는 것**
 + 코드 재사용성이 높아짐
 + `Java는 단일 상속만 지원`
 > ```java
 > public class Animal {
 >     public void eat() { System.out.println("먹는다"); }
 > }
 > public class Dog extends Animal {
 >     public void bark() { System.out.println("짖는다"); }
 > }
 > // Dog는 eat()도 쓸 수 있음
 > ```

## 3. 다형성 (Polymorphism)
 + **같은 타입이지만 실제 동작이 다른 것**
 + 부모 타입으로 자식 객체를 다룰 수 있음
 + 예시 : `오버라이딩(Override)`
 > ```java
 > class Animal {
 >     public void sound() {
 >         System.out.println("...");
 >     }
 > }
 > 
 > class Dog extends Animal {
 >     @Override
 >     public void sound() {         // 부모 메서드를 재정의
 >         System.out.println("멍멍");
 >     }
 > }
 > ```

 + **오버라이딩 / 오버로딩**
     | | **설명** | **관게** |
     |:-----|:-----|:-----|
     | Overriding | 부모 메서드를 자식이 재정의 | 상속 관계 |
     | Overloading | 같은 이름, 다른 매개변수로 메서드 여러 개 | 같은 클래스 |

## 4. 추상화 (Abstraction)
 + **공통적인 특성만 추출하고 세부 구현은 숨기는 것**
 + `Interface`나 `abstract class`로 구현
 + `무엇을 하는지`만 정의하고 `어떻게 하는지`는 구현체에 맡김
 > ```java
 > interface Payment {
 >     void pay(int amount);  // 어떻게 결제하는지는 구현체가 결정
 > }
 > class KakaoPay implements Payment {
 >     public void pay(int amount) { /* 카카오페이 결제 */ }
 > }
 > class NaverPay implements Payment {
 >     public void pay(int amount) { /* 네이버페이 결제 */ }
 > }
 > ```

 + **Abstract 클래스 / Interface**
     | | **abstract 클래스** | **Interface** |
     |:----|:-----|:-----|
     | **필드** | 일반 변수 가능 | 상수(static final)만 |
     | **메서드** | 구현 메서드_ 추상 메서드 혼용 | 추상 메서드만 |
     | **상속/구현** | extends 하나만 | implements 여러 개 가능 |
     | **목적** | 공통 기능 공유 | 기능 구현 강제 |


---
# Java 컬렉션

## List
 + **순서가 있고 중복을 허용**
 + 인덱스로 접근 가능

 | 종류 | 특징 | 사용 이유 |
 |:-----|:-----|:-----|
 | ArrayList | 배열 기반, 조회 빠름 O(1) | 조회가 많을 때 |
 | LinkedList | 노드 연결, 삽입/삭제 빠름 O(1) | 삽입/삭제가 많을 때 |

## Map
 + **Key로 Value를 찾는 구조**
 + Key는 중복 불가, Value는 중복 가능

 | 종류 | 특징 | 사용 이유 |
 |:-----|:-----|:-----|
 | HashMap | 순서 없음, 조회 O(1) | 빠른 검색 |
 | LinkedHashMap | 입력 순서 유지 | 순서 중요할 때 |
 | TreeMap | Key 정렬됨 | 정렬된 Key 필요할 때 |

## Set
 + **중복을 허용하지 않고 순서 없음**
 + 중복 제거할 때 자주 사용

 | 종류 | 특징 | 사용 이유 |
 |:----|:-----|:-----|
 | HashSet | 순서 없음, 조회 O(1) | 중복 제거 |
 | TreeSet | 정렬됨 | 정렬된 중복 제거 |

---
# Spring IoC/DI

## IoC (Inversion of Control) - 제어의 역전
 + 객체의 생성 및 관리의 제어권을 Spring 컨테이너에게 넘기는 것
 + 개발자는 `어떤 객체가 필요한지`만 선언하면 Spring이 알아서 만들고 주입
 > ```java
 > // IoC 없을 때 (직접 생성)
 > UserService service = new UserService(new UserRepository());
 > 
 > // IoC 있을 때 (Spring이 알아서)
 > @Service
 > public class UserService { ... }  // Spring이 알아서 생성·관리
 > ```

## DI (Dependency Injection) - 의존성 주입
  + 필요한 객체를 외부(Spring)에서 주입받는 것
  + IoC를 구현하는 방법
  > ```java
  > @Service
  > public class UserService {
  >     private final UserRepository userRepository;
  > 
  >     // 생성자 주입 (가장 권장)
  >     @Autowired
  >     public UserService(UserRepository userRepository) {
  >         this.userRepository = userRepository;
  >     }
  > }
  > ```

## Bean
 + Spring컨테이너가 생성하고 관리하는 객체
 + `@Component`,`@Service`, `@Repository`, `@Controller` 어노테이션을 붙이면 Bean으로 등록
 + 기본적으로 `싱글톤`으로 관리됨
     + **싱글톤(Singleton)** : 객체를 딱 하나만 만들어서 전체에서 공유하는 패턴 
     > ```java
     > public class Singleton {
     >     // 1. 딱 하나만 만들어두기
     >     private static Singleton instance = new Singleton();
     > 
     >     // 2. 외부에서 new 못하게 막기
     >     private Singleton() { }
     > 
     >     // 3. 만들어둔 거 가져다 쓰기
     >     public static Singleton getInstance() {
     >         return instance;
     >     }
     > }
     > 
     > // 사용
     > Singleton a = Singleton.getInstance();
     > Singleton b = Singleton.getInstance();
     > // a == b → true (같은 객체)
     > ```

---
# REST API
 + **HTTP를 기반으로 자원(Reousrce)를 URL로 표현하고, HTTP 메서드로 행위를 나타내는 API 설계 방식**

## HTTP메서드 역할
 | 메서드 | 역할 | 예시 |
 |:-----|:-----|:-----|
 | GET | 조회 | GET/users/1 |
 | POST | 생성 | POST/users |
 | PUT | 전체 수정 | PUT/users/1 |
 | PATCH | 일부 수정 | PATCH/users/1 |
 | DELETE | 삭제 | DELETE/users/1 |
 > ```java
 > @RestController
 > @RequestMapping("/users")
 > public class UserController {
 > 
 >     // GET /users/1 → 조회
 >     @GetMapping("/{id}")
 >     public User getUser(@PathVariable Long id) { }
 > 
 >     // POST /users → 생성
 >     @PostMapping
 >     public User createUser(@RequestBody UserDto dto) { }
 > 
 >     // PUT /users/1 → 전체 수정
 >     @PutMapping("/{id}")
 >     public User updateUser(@PathVariable Long id, @RequestBody UserDto dto) { }
 > 
 >     // PATCH /users/1 → 일부 수정
 >     @PatchMapping("/{id}")
 >     public User patchUser(@PathVariable Long id, @RequestBody UserDto dto) { }
 > 
 >     // DELETE /users/1 → 삭제
 >     @DeleteMapping("/{id}")
 >     public void deleteUser(@PathVariable Long id) { }
 > }
 > ```


## REST API 설계 원칙
 + URL에 동사 쓰지 않기 → /getUser (X) /users/1 (O)
 + 소문자, 하이픈(-) 사용 → /user-profiles
 + 계층 관계는 /로 표현 → /users/1/orders
 + HTTP상태코드 정확히 사용 → 200,201,400,404,500

## 주요 HTTP 상태코드
 | 코드 | 의미 |
 |:-----|:-----|
 | 200 OK | 성공 |
 | 201 Created | 생성 성공 |
 | 400 Bad Request | 잘못된 요청(클라이언트 오류) |
 | 401 Unauthorized | 인증 필요 |
 | 403 Forbidden | 권한 없음 | 
 | 404 Not Found | 자원 없음 | 
 | 500 Internal Server Error | 서버 오류 |

---
# JPA/ORM

## ORM (Object-Relational Mapping)
  + **Java 객체와 DB 테이블을 자동으로 매핑하주는 기술**
  + SQL 직접 안 써도 Java코드로 DB 조작 가능


## JPA
 + Java ORM 표준 인터페이스
 + JPA 구현체 예시 : `Hibernate`
 > ```java
 > // SQL 직접 쓰면
 > String sql = "SELECT * FROM users WHERE id = ?";
 > 
 > // JPA 쓰면
 > User user = userRepository.findById(id);
 > ```

## 영속성 컨텍스트
 + **JPA가 엔티티를 관리하는 공간**
 + 한 번 조회한 엔티티를 캐시처럼 저장해두고, 같은 트랜잭션 안에서 같은 데이터 조회하면 DB 안 가고 여기서 꺼내줌
 + 트랜잭션 커밋 시점에 변경 감지해서 자동으로 UPDATE 요청

## N+1 문제
 + 연관 엔티티 조회 시 쿼리가 N+1번 나가는 문제
 + 예시 : 유저 10명 조회 → 유저 조회 1번 + 각 유저 조회 10번 = 11번
 + 해결 방법 : `fetch join` 또는 `@EntityGraph` 사용


---
# @Transactional
 + 메서드에 붙이면 **해당 메서드 실행 전 트랜잭션 시작, 정상 종료 시 커밋, 예외 발생 시 롤백을 자동**으로 함
 > ```java
 > @Transactional
 > public void transfer(Long fromId, Long toId, int amount) {
 >     Account from = accountRepo.findById(fromId);
 >     Account to = accountRepo.findById(toId);
 >     from.minus(amount);
 >     to.plus(amount);
 >     // 정상이면 자동 커밋, 예외 나면 자동 롤백
 > }
 > ```

## 동작원리 - AOP 프록시
 + @Transactional은 AOP(관점 지향 프로그래밍) 기반으로 동작
 + Spring이 프록시 객체를 만들어서 메서드 앞뒤로 트랜잭션 로직을 끼워넣는 방식

## 주의할 점
 + 같은 클래스 내에서 @Transactional 메서드를 호출하면 프록시를 안 거쳐서 트랜잭션이 안걸림
 + 기본적으로 RuntimeException만 롤백, CheckedException은 롤백 안함
 + rollbackFor = Exception.class 옵션으로 모든 예외 롤백 가능
 > ```java
 > // 모든 예외에서 롤백하려면
 > @Transactional(rollbackFor = Exception.class)
 > ```

# AOP(Aspect-Oriented Programming) - 관점지향 프로그래밍
 + **핵심 로직(회원가입, 주문 등)**과 **부가 기능(트랜잭션, 로깅, 보안)을 분리해서 관리**하는 방식
 + 부가 기능은 여러 곳에서 공통으로 쓰이는데, 이걸 핵심 로직에 섞어두면 코드가 지저분해지고 중복이 생김
 > ```java
 > // AOP 없을 때 → 모든 메서드에 반복
 > public void createUser() {
 >     log.info("시작");          // 로깅
 >     checkAuth();               // 보안
 >     connection.begin();        // 트랜잭션
 >     
 >     // 핵심 로직
 >     userRepository.save(user);
 >     
 >     connection.commit();       // 트랜잭션
 >     log.info("종료");          // 로깅
 > }
 > 
 > // AOP 있을 때 → 핵심 로직만
 > @Transactional
 > @LogExecutionTime
 > public void createUser() {
 >     userRepository.save(user); // 핵심 로직만
 > }
 > ```

## AOP 용어
 | 용어 | 설명 | 예시 | 
 |:----|:----|:----|
 | Aspect | 부가 기능 모듈 전체 | 트랜잭션 관리 모듈 |
 | Advice | 실제 부가 기능 코드 | 트랜잭션 시작/커밋/롤백 코드 |
 | Pointcut | Advice를 어디에 적용할지 | @Transactional 붙은 메서드 전부 |
 | JoinPoint | Advice가 실제 적용된 지점 | createUser() 실행 시점 |
 | Weaving | Advice를 핵심 로직에 끼워넣는 과정 | 프록시 객체 생성 |

## Advice 실행 시점
 | 종류 | 실행 시점 | 용도 |
 |:-----|:-----|:-----|
 | @Before | 메서드 실행 전 | 권한 체크, 파라미터 검증 |
 | @After | 메서드 실행 후 (성공/실패 무관) | 리소스 해제, 로그 기록 |
 | @AfterReturning | 메서드 정상 종료 후 | 변환값 로깅 |
 | @AfterThrowing | 예외 발생 후 | 예외 로깅, 알림
 | @Around | 메서드 앞뒤 전부 | 트랜잭션, 실행시간 측정 |

 +  @Transactional은 @Around임
    + 트랜잭션은 시작(앞)과 커밋/롤백(뒤) 둘 다 처리해야 하기때문에 @Around로 구현되어 있음