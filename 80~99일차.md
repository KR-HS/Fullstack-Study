## 커리큘럼(12-30/변경)
```
01. Java
02. git
03. Database 
04. Jsp [Server]

05. HTML,CSS 
07. JS
06. 미니프로젝트-2W

08. SpringFramework , SrpingBoot 
19. 중간프로젝트 (1M) (v)
10. Linux 명령어 
11. AWS 클라우드 
12. React JS [Front-end]
13. DevOps - Docker
14. App - Android
15. 최종프로젝트 (1M)
```
---
# 팀프로젝트 SWS(04/08 ~ 05/04)
## [우리 아이 안심 귀가 서비스](https://github.com/KR-HS/SafeWaySeoul)
+ 깃허브 링크는 로컬에서 테스트하기 위한 코드로만 이루어져있어 실행시 실행이 되지않을 수 있습니다.
+ 실제 동작은 AWS에 업로드하여 서버로 구축하였음
+ 민감정보는 gitignore파일에 등록하여 깃허브에 등록되지 않게 설정하였음 (env파일, 커스텀 properties파일 등)
+ ~~[기사서버 (운영종료)](https://swsdriver.kro.kr)~~
+ ~~[유저서버 (운영종료)](https://swsuser.kro.kr)~~
+ ~~[웹소켓서버 (운영종료)](https://swssocket.kro.kr)~~
> + 웹소켓 서버는 서버 사이에 데이터를 실시간으로 주고받기 위한 서버로 실제 화면 구현은 하지 않음

## 사용기술
### 프론트엔드(Frontend)
+ HTML
+ CSS
+ JavaScript
+ JQuery
+ Thymeleaf
+ SockJS

### 백엔드(Backend)
#### Spring Boot
##### 1. SockJS
+ WebSocket을 지원하지 않는 브라우저에서도 WebSocket과 유사한 기능을 제공하기 위해 사용하는 웹소켓 대체 기술
+ ``spring-websocket``모듈을 통해 WebSocket을 지원

+ 사용예시
> ```
> @Configuration
> @EnableWebSocketMessageBroker
> public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
>     @Override
>     public void registerStompEndpoints(StompEndpointRegistry registry) {
>         registry.addEndpoint("/wss").setAllowedOriginPatterns("*").withSockJS();
>     }
> 
>     @Override
>     public void configureMessageBroker(MessageBrokerRegistry registry) {
>         registry.enableSimpleBroker("/topic");
>         registry.setApplicationDestinationPrefixes("/app");
>     }
> }
> ```
 
##### 2. Scheduler
+ ``@Scheduled`` 어노테이션을 사용하여 주기적으로 작업을 수행

+ 사용예시
> ```
> // 매일 새벽 0시에 실행
> @Scheduled(cron = "0 0 0 * * *")
> @Transactional
> public void scheduledUpdate() {
>     int result = 0;
>     // 오전, 오후에 맞는 driveInfoKey받기
>     List<Integer> am = getDriveInfoKey("오전");
>     List<Integer> pm = getDriveInfoKey("오후");
>     // record테이블에 추가
>     registRecordDailyAM(am);
>     registRecordDailyPM(pm);
>     // recordMatch테이블에 필요한 데이터 가져오기(km_key, record_key)
>     List<RecordMatchVO> list = getRecordMatachInfo();
>     // recordMatch에도 추가
>     registRecordMatchDaily(list);
>     System.out.println("기사 운행정보 추가");
> }
> ```
 
##### 3. Interceptor
+ 요청이 컨트롤러에 도달하기 전/후에 공통 처리를 위한 필터링/로깅/인증 등의 역할
+ ``HandlerInterceptor``를 구현하고, ``WebMvcConfigurer``에서 등록

+ 사용예시
> ```
> // # Interceptor(세부 조건 설정)
> @Component // webConfig에서 bean으로 등록안해도됨 (자동 빈 등록)
> public class FirstVisitInterceptor implements HandlerInterceptor {
>     // 첫 화면시 무조건 로딩화면
>     @Override
>     public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
>             throws Exception {
> 
>         HttpSession session = request.getSession();
>         String uri = request.getRequestURI();
> 
>         System.out.println("firstVisit: " + session.getAttribute("firstVisit"));
> 
>         // 세션에 방문 여부가 없다면
>         if (session.getAttribute("firstVisit") == null) {
>             session.setAttribute("firstVisit", true);
> 
>             // 로딩 페이지 자체는 리다이렉트하지 않도록 예외 처리
>             if (!uri.equals("/loading")) {
>                 response.sendRedirect("/loading");
>                 return false;
>             }
>         }
> 
>         return true; // 이미 방문했으면 계속 진행
>     }
> 
>     @Override
>     public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
>         System.out.println("컨트롤러 실행 후 인터셉터 동작");
>     }
> }
> 
> // # WebConfig파일(경로 설정)
> @Configuration
> public class WebConfig implements WebMvcConfigurer {
> 
>     @Autowired
>     private FirstVisitInterceptor firstVisitInterceptor;
> 
> 
>     @Autowired
>     private LoginInterceptor loginInterceptor;
> 
>     @Override
>     public void addInterceptors(InterceptorRegistry registry) {
>         registry.addInterceptor(firstVisitInterceptor)
>                 .addPathPatterns("/**")// 모든 경로에 적용
>                 .excludePathPatterns(
>                 "/loading","/css/**", "/js/**", "/img/**", "/favicon.ico")
>                 .excludePathPatterns("/.well-known/**");
> 
> 
> 
>         registry.addInterceptor(loginInterceptor)
>                 .addPathPatterns("/**") // 모든 경로에 적용
>                 .excludePathPatterns(
>                         "/loading",            // 첫 방문 페이지
>                     "/user/*", // 유저로그인관련
>                     "/css/**", "/js/**", "/img/**", "/favicon.ico") // 정적 리소스 제외); // 리다이렉트 대상은 제외!
>                 .excludePathPatterns("/.well-known/acme-challenge/**");
> 
>     }
> }
> ```

### API
+ [서울시 공공 API](https://data.seoul.go.kr/dataList/OA-20300/S/1/datasetView.do)
+ **kakao map API**
> + 실시간 위치좌표를 찍고 해당 좌표로 경로를 그려줌
> + 서울시 공공 API에서 얻은 유치원 좌표를 맵상에 표현하고 마커를 통해 해당 유치원의 정보를 띄워줌
> + 기본 설정된 좌표들의 최적경로를 찍어줌


### 데이터베이스(DataBase)
+ **Mysql**
+ **Amazon RDS**

+ 서울시 공공데이터를 실시간으로 받아서 schedular를 통해 실시간으로 유치원정보를 받아 데이터 정제 후 DB에 인서트함
> ```
> @Override
> public void registKinderAPI() {
>     int pageSize = 1000;
>     int startIndex = 1;
>     // 1. 총 개수 확인
>     String countUrl = String.format(
>             "http://openapi.seoul.go.kr:8088/%s/json/ChildCareInfo/1/1/",
>             apiKey
>     );
>     JsonNode countRoot = restTemplate.getForObject(countUrl, JsonNode.class);
>     int totalCount = countRoot.path("ChildCareInfo").path("list_total_count").asInt();
>     System.out.println("총 어린이집 수: " + totalCount);
>     // 2. 페이징 돌면서 가져오기
>     while (startIndex <= totalCount) {
>         int endIndex = Math.min(startIndex + pageSize - 1, totalCount);
>         String url = String.format(
>                 "http://openapi.seoul.go.kr:8088/%s/json/ChildCareInfo/%d/%d/",
>                 apiKey, startIndex, endIndex
>         );
>         System.out.println("요청: " + url);
>         JsonNode root = restTemplate.getForObject(url, JsonNode.class);
>         JsonNode rows = root.path("ChildCareInfo").path("row");
>         System.out.println("row 수: " + rows.size());
>         for (JsonNode node : rows) {
>             KinderVO vo = KinderVO.builder()
>                     .kinderName(node.path("CRNAME").asText())
>                     .kinderPhone(node.path("CRTELNO").asText())
>                     .kinderPostcode(node.path("ZIPCODE").asText())
>                     .kinderAddress(node.path("CRADDR").asText())
>                     .kinderCapacity(node.path("CRCHCNT").asInt())
>                     .kinderWeekendOpen(node.path("CRSPEC").asText().contains("휴일") ? "Y" : "N")
>                     .kinderNightOpen(node.path("CRSPEC").asText().contains("야간") ? "Y" : "N")
>                     .build();
>             String closed = node.path("CRABLDT").asText();
>             String zipcode = node.path("ZIPCODE").asText();
>             String phone = node.path("CRTELNO").asText();
>             String latitude = node.path("LA").asText();
>             String longitude = node.path("LO").asText();
>             if (!closed.isBlank()|| zipcode.isBlank()|| phone.isBlank() )
>                 continue;
>             if(kinderMapper.existsByLaAndLo(latitude,longitude)>0) continue;
>             System.out.println("중복검사");
>             if (!(kinderMapper.existsByNameAndPhone(vo.getKinderName(), vo.getKinderPhone())>0)){
>                 System.out.println("인서트전");
>                 kinderMapper.insertKinder(vo);
>                 LocationVO locationVO = LocationVO.builder().
>                         latitude(latitude).
>                         longitude(longitude).
>                         kinderKey(vo.getKinderKey())
>                         .build();
>                 System.out.println("인서트후");
>                 locationMapper.registLocation(locationVO);
>                 System.out.println("위치정보인서트");
>             }
>         }
>         startIndex += pageSize;
>     }
> }
> ```

### 인프라 & 배포(Infrastructure/ DevOps)
#### 1. AWS
> #### 1-1. EC2(Server)
+ 프로젝트를 AWS상의 Ubuntu기반의 리눅스서버에 올리면서 윈도우서버상에서 개발하던 프로젝트를 리눅스 환경에 맞게 수정하였음
+ EC2서버를 생성하며 ``키페어``를 생성하였음
> + **키페어의 경우 웹상에 올리지 않게 주의 필요**
> + 키페어 생성 후 키페어 파일의 이름을 변경해도 상관은 없음
> + EC2생성 시 보안그룹을 통해 특정 포트에 대해서만 방화벽을 열어주도록 설정 (**EC2생성 후 수정 가능**)
>> + 외부에서 EC2에 접속하는 규칙인 ``인바운드규칙``과 EC2에서 나가는 규칙인 ``아웃바운드규칙``이 있으며 기본적으로 모든 포트를 아웃바운드함
>> + 인바운드 포트 
>>> | 포트번호 | 통신규약 | 텍스트 |
>>> |:-----|:-----|:-----|
>>> | 22 | SSH | 리눅스 인스턴스 접속에 사용 |
>>> | 80 | HTTP | 비보안 웹서버 포트 |
>>> | 443 | HTTPS | 보안 웹서버 포트 |
>>> | 21 | FTP | 파일 전송 프로토콜 |
>>> | 22 | SFTP | 보안 파일 전송 프로토콜 |
>>> | 3389 | RDP | 윈도우 인스턴스용 포트 |
 
+ EC2에 할당된 ip를 도메인에 등록시켜 해당 도메인주소로 접속하면 프로젝트가 실행되게 하였음
+ ``https로 리다이렉트`` 시켜주기 위해 **nginx**를 사용하였음
> + 실시간 위치 정보는 http에서는 불가능하기 때문에 https로 리다이렉트 하였음
> + https로 리다이렉트 시킬경우 요청을 보내거나 받는 주소 또한 https로 변경필요

+ 가상 서버의 파일을 좀더 쉽게 사용하기 위해 ``FileZilla``를 사용하였음
+ **고정 IP**를 사용하기 위해 ``Elastic IP``를 등록하였음
> + **엘라스틱IP의 경우 EC2인스턴스에 연결하지 않거나 연결한 EC2를 중단한 후 생성한 채로 두면 요금이 부과되니 주의 필요**

+ 프리티어로 인스턴스를 생성 후 사용하면 메모리가 1GB로 매우 작아 ``Swap 메모리``를 설정하였음 
> + ``스왑메모리``란 실제 메모리 Ram이 가득 찼지만 더 많은 메모리가 필요할 때 **디스크 공간을 이용하여 부족한 메모리를 대체할 수 있는 공간**
> 1. 스왑파일 생성
>> + dd 명령을 사용해 루트파일 시스템에 스왑 파일 생성
>> + bx는 블록의 크기이고, count는 블록 수 , **스왑 파일의 크기**는 dd명령의 **블록 크기 옵션에 블록 수 옵션을 곱한 값**
>> ``sudo dd if=/dev/zero of=/swapfile bs=128M count=32``
> 2. 스왑 파일의 읽기 및 쓰기 권한 업데이트
>> + ``sudo chmod 600 /swapfile``
> 3. Linux 스왑 영역 설정
>> + ``sudo mkswap /swapfile``
> 4. 스왑 공간에 스왑 파일 추가
>> + ``sudo swapon / swapfile``
> 5. 프로시저 확인
>> + ``sudo swapon -s``
> 6. /etc/fstab 파일을 편집하여 부팅 시 스왑 파일 시작
>> ```
>> // 편집기에서 파일을 열어준다.
>> $ sudo vi /etc/fstab
>> 
>> // 파일 끝에 아래의 내용을 추가하고 파일을 저장한다.(/포함)
>> /swapfile swap swap defaults 0 0
>> ```
> 7. 메모리 확인명령어로 결과 호가인
>> ``free -m``

> #### 1-2. S3(Simple Storage Service)
+ 유저 서버내 프로필 사진, 게시판 업로드 사진을 저장하기 위해 사용
+ 외부에서 접근 시 ``access Key``와 ``secret Key``가 필요하며 추가적으로 버킷을 만든 ``지역``과 ``버킷의 이름``이 필요
> + 해당 내용들은 민감 정보이므로 깃허브에 올릴 수 없어 env파일로 만들어 따로 이용하고 있었고, EC2서버에 업로드시 해당 서버에 같이 업로드 한 후 해당 파일을 이용할 수 있도록 코드를 수정하였음
>> ```
>> // # application.properties
>> ## AWS
>> cloud.aws.credentials.accessKey=${aws.accessKey}
>> cloud.aws.credentials.secretKey=${aws.secretKey}
>> cloud.aws.region.static=${aws.region}
>> cloud.aws.s3.bucket=${aws.bucket}
>> 
>> // # env파일을 불러오는 세부옵션 설정
>> public class EnvConfig implements EnvironmentPostProcessor {
>>     @Override
>>     public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
>>         Dotenv dotenv = Dotenv.configure()
>>                 .directory("./")
>>                 .ignoreIfMissing()
>>                 .load();
>> 
>>         Map<String, Object> dotenvProperties = new HashMap<>();
>>         dotenvProperties.put("aws.accessKey", dotenv.get("AWS_ACCESS_KEY_ID"));
>>         dotenvProperties.put("aws.secretKey", dotenv.get("AWS_SECRET_ACCESS_KEY"));
>>         dotenvProperties.put("aws.region", dotenv.get("AWS_REGION"));
>>         dotenvProperties.put("aws.bucket", dotenv.get("AWS_S3_BUCKET_NAME"));
>> 
>>         environment.getPropertySources().addFirst(new MapPropertySource("dotenv", dotenvProperties));
>>     }
>> }
>> ```

+ 프로젝트에서는 먼저 S3에 업로드 한 후 DB상에 해당파일의 URL를 저장한 후, 해당 URL로 파일을 불러와 사용하였음
> ```
> if (!file.isEmpty()) {
>     try {
>         String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
>         String originalFileName = file.getOriginalFilename();
>         String uuid = UUID.randomUUID().toString();
>         String saveName = uuid + "_" + originalFileName;
>         String s3FilePath = today + "/" + saveName;
>         System.out.println("saveName: " + saveName);
>         try (InputStream inputStream = file.getInputStream()) {
>             ObjectMetadata metadata = new ObjectMetadata();
>             metadata.setContentLength(file.getSize());
>             metadata.setContentType(file.getContentType());
>             amazonS3.putObject(
>                     new PutObjectRequest(bucketName, s3FilePath, inputStream, metadata)
>             );
>             System.out.println("S3 업로드 성공: " + s3FilePath);
>         } catch (Exception e) {
>             System.err.println("S3 업로드 실패: " + e.getMessage());
>             e.printStackTrace();
>             throw e;
>         }
>         String fileUrl = "https://" + bucketName + ".s3." + amazonS3.getRegionName() + ".amazonaws.com/" + s3FilePath;
>         FileVO fileVO = FileVO.builder()
>                 .fileName(originalFileName)   // 원래의 한글 파일명 (화면 표시용)
>                 .filePath(fileUrl)            // 실제 접근할 S3 URL (한글 없는 safe 버전)
>                 .fileUuid(uuid)               // uuid (S3 키 매칭용)
>                 .postKey(postKey)
>                 .build();
>         System.out.println("********" + fileVO.toString());
>         filesMapper.insertFile(fileVO);
>     } catch (Exception e) {
>         e.printStackTrace();
>     }
> }
> ```

> #### 1-3. Aurora and RDS(DB)
+ 프로젝트 DB와 동일한 ``MySQL Community``로 엔진을 선택하여 DB인스턴스 생성함
+ 마스터 사용자이름 및 비밀번호 설정
+ 보안그룹에서 포트번호 3306(Mysql)을 허용하는 인바운드 규칙을 설정
> + 프로젝트상 기사, 유저, 웹소켓 서버에서 모두 DB를 사용하기 떄문에 세 서버의 IP를 인바운드 규칙에 등록하였음
+ DB인스턴스 생성 후 정상적으로 스키마를 생성하여 이용

#### 2. Github(깃허브)
+ 프로젝트 공유 및 관리에에 사용

### 개발도구
> #### 1. IntelliJ IDEA
> #### 2. VIsual Studio Code
> #### 3. Git

### 협업 및 문서화
> #### 1. Notion
> #### 2. Github


## 프로젝트 상세 내용
### 0. 공통 기능
+ 회원가입
> + 중복체크
> + daum API를 이용한 주소지 찾기
> + AirDatepicker를 이용한 생년월일 선택
+ 로그인/로그아웃
+ 아이디/비밀번호 찾기
+ ``Interceptor를 이용``한 첫 로그인 시 ``로딩화면 구현`` 및 로그인시 ``쿠키 설정`` 구현
> + 쿠키 설정을 통한 자동로그인 기능 구현
+ 상단 로고바와 하단 메뉴바를 통해 빠른 화면 이동이 가능

### 1. 기사 서버
+ 회원가입 시 거주지가 아닌 유치원 등록
> + 유치원 등록 시 등록된 유치원 목록 중 검색을 통해 선택 가능
+ schedular를 통해 회원 가입 후 하루가 지난 시점 00시 00분 00초 부터 오전,오후 2건 배차 자동 등록
 
> #### 1-1. 홈
+ 본인의 배차를 확인 할 수 있음
> + 운행대기, 운행중, 운행완료 세가지 상태가 있음
+ 운행대기의 경우 해당 배차를 클릭시 운행시작, 운행종료 안내창이 뜨고 운행시작을 누를 경우 상세 운행창으로 이동
 
> #### 1-2. 운행정보
+ 기본적으로 아이들의 부모(유저)의 거주지로 자녀들이 내릴 장소로 경로가 설정
+ 운행화면에 들어온 시점부터 실시간 위치가 웹소켓으로 전송
+ 아이들의 배차를 관리할 수 있음
> + 하차 버튼을 눌러 하차완료상태로 변경
> + 모든 아이들이 하차완료상태가 되면 운행종료 버튼이 생성되고 버튼 클릭시 배차 상태가 운행종료 상태로 변경
> + 운행종료 상태가 되면 실시간 위치 좌표전송기능도 중단

### 2. 유저 서버
+ 회원가입 시 거주지 등록
+ schedular를 통해 매일 서울시 공공데이터로 유치원 데이터를 새롭게 등록시킴(이미 존재하는 데이터는 등록되지 않음)
 
> #### 2-1. 홈
+ 본인이 등록한 자녀들의 하차상태, 운행상태창이 보임
> + 가장 마지막 카드로 자녀 추가 카드가 있고 해당 카드를 눌러 자녀를 등록할 수 있음
> + 자녀 카드 클릭시 해당 자녀의 실시간 위치 정보를 볼수 있는 화면으로 이동
 
+ 유치원을 검색하거나 유치원 운영형태, 지역에 따라 유치원의 위치를 맵상을 통해 볼 수 있음
> + 페이지네이션을 통해 10건의 유치원 정보씩 보이며 해당 유치원 선택시 화면상에 마커가 추가
> + 마커를 클릭 시 해당 유치원의 정보가 맵상에 표시
 
> #### 2-2. 자녀관리
+ 가장 상단에 부모의 정보가 보이며 수정아이콘을 클릭하여 부모(유저)의 정보를 수정하고 프로필 사진을 등록할 수 있음
+ 하단에는 등록한 자녀들의 정보가 표시되며 자녀카드 클릭시 자녀의 실시간 위치를 볼 수 있는 화면으로 이동
> + 새로고침 시 DB상에 저장된 위치를 불러와 먼저 화면에 이동한 경로를 찍고 추가적으로 웹소켓을 통해 실시간으로 받은 정보를 뒤에 이어붙여 경로를 그림
> + 자녀 정보를 자녀카드의 우측 상단의 아이콘을 통해 삭제하거나 수정할 수 있음
> + 가장 하단의 내 아이등록하기 카드를 클릭하여 자녀를 추가할 수 있음
 
> #### 2-3. 게시판
+ 간단한 부모끼리의 소통 게시판으로 글을 쓰거나 댓글을 작성할 수 있음
+ 검색 기능을 통해 특정 게시물을 검색하거나 아이콘을 통해 본인의 게시물만 볼 수 있음
+ 한 게시물당 최대 5개의 사진을 업로드 가능하며 해당 사진은 AWS의 RDS에 저장됨

### 3. 웹소켓 서버
> + 기사 앱에서 보낸 실시간 위치정보를 DB상에 저장
> + 기사 앱에서 보낸 실시간 위치정보를 유저앱으로 전송