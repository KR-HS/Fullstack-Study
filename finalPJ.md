## 🎥 실시간 방송(WebRTC)을 활용한 온라인 경매 플랫폼
**BIDCAST**는 누구나 경매를 개설하고 입찰에 참여할 수 있는 실시간 영상 기반 경매 시스템입니다.  
실시간 채팅, 영상 공유, 입찰 기능을 통해 생동감 있는 경매 경험을 제공합니다.

🔗 [BIDCAST 서버 GitHub](https://github.com/KR-HS/BidCast_Server)
🔗 [BIDCAST 클라이언트 GitHub](https://github.com/KR-HS/BidCast) 
🔗 [AWS세팅하기](AWS세팅.md) 
🌐 ~~[BIDCAST 홈페이지(운영종료)](https://bidcast.kro.kr)~~

---

### 🔧 주요 기능
- **경매 탐색**: 경매 일정과 검색 기능을 통해 원하는 경매를 손쉽게 찾을 수 있습니다.
- **관심 경매 등록**: 마이페이지에서 관심 경매로 바로 이동할 수 있습니다.
- **경매 개설**: 누구나 경매사가 되어 경매를 개설할 수 있습니다.
- **경매 진행 기능**:
  - 경매사는 실시간 방송을 통해 입찰을 진행하고, 물품을 낙찰/유찰 처리할 수 있습니다.
  - 경매 단위 조정 및 입찰 종료 기능을 제공합니다.
- **입찰자 기능**:
  - 실시간 입찰, 낙찰/유찰 결과를 모달 및 영상을 통해 확인할 수 있습니다. 
  - 실시간 채팅기능을 구현하였습니다. (최근 40개 메시지 유지)
- **마이페이지**:
  - 경매 이력, 낙찰 내역, 등록 상품 상태 등을 확인할 수 있습니다.

---
### 📦 프로젝트 구성 및 배포
- **Vite 기반 멀티 페이지 구성**
- **EC2 배포**, **Nginx + certbot으로 HTTPS** 설정
- **Jenkins**를 통한 **CI/CD 구축**
- **AWS S3**를 통한 파일 저장
- **PostgreSQL (RDS)** 기반 데이터베이스 사용
- **JWT**를 통한 인증 및 **비밀번호 암호화** 적용
> ⚠️ **주의**: `application-custom.properties` 파일은 Git에 포함되지 않습니다.
> *직접 `src/main/resources` 경로에 아래 내용을 포함한 파일을 추가해주세요:*

<details>
<summary><code>application-custom.properties</code> 예시 보기</summary>

```bash
properties
spring.datasource.url=jdbc:postgresql://<DB주소>:5432/bidcast
spring.datasource.username=<DB유저>
spring.datasource.password=<DB비밀번호>

jwt.secret=<JWT 비밀 키>
jwt.expiration=3600000

aws.s3.access-key=<AccessKey>
aws.s3.secret-key=<SecretKey>
aws.s3.region=ap-northeast-2
aws.s3.bucket=<버킷명>
aws.s3.folder=uploads
```
</details> 

---

### 🛠 사용 기술 스택

> ### 1. Server
> #### 1-1. App Server (유저 서비스)
> - **Build Tools**: `Vite`, `Gradle`
> - **Front-End**: `React`, `HTML`, `CSS`, `Thymeleaf`
> - **Back-End**: `Spring Boot`, `WebRTC`, `WebSocket`
> - **Persistence Layer**: `MyBatis`, `JPA`
> - **Authorization**: `JWT`, `Spring Security`
>
> #### 1-2. SFU Server (중계 서버)
> - `Node.js`, `WebRTC`, `mediasoup`, `WebSocket`
>
> #### 1-3. Database
> - `Amazon RDS`, `PostgreSQL`
>
> #### 1-4. Cloud Storage
> - `Amazon S3`

> ### 2. Infrastructure
> - `Nginx`, `certbot`, `Let's Encrypt`

> ### 3. Dev Tools
> - `IntelliJ IDEA`, `Figma`

> ### 4. Collaboration
> - `Git`, `GitHub`

> ### 5. CI/CD
> - `Jenkins`


---

# 서버 코드분석

## 1.WebSocket & WebRTC
<details>
<summary>내용 보기</summary>

### 종류

- **`socket`**  
  **클라이언트 하나하나의 연결**을 나타내는 객체
  해당 클라이언트와 **1:1 통신**하거나 이벤트를 처리할 때 사용

- **`io`**  
  **전체 소켓 서버 객체**로, **모든 클라이언트에 메시지를 보내거나 특정 방(room)에 메시지를 전달**할 때 사용
  예: `io.to(roomId).emit('event',data)`

- **`broadcast`**  
  **자신을 제외한 같은 방 또는 전체 클라이언트에게 메시지 전송**
  예: `socket.broadcast.emit()` — 자기 자신을 제외한 모두에게 전송

### 방식

- **`on`**  
  클라이언트로부터 **이벤트가 왔을 때** 실행할 콜백을 등록  
  예: `socket.on('produce', callback)`

- **`emit`**  
  **이벤트와 데이터를 보내는 방식**  
  서버 ↔ 클라이언트 양방향으로 사용
  예: `socket.emit('new-producer', data)`

---

## WebRTC & mediasoup

### WebRTC

- **실시간 음성, 영상, 데이터 통신을 위한 표준 기술**
- 브라우저 또는 네이티브 앱 간 **P2P 연결을 지원**
- 직접 연결이 어려운 경우 **SFU 방식** 사용 (Selective Forwarding Unit)

#### ICE, STUN, TURN

| 용어   | 설명 |
|--------|------|
| **ICE**  | 연결 가능한 후보 주소들을 수집해 최적 경로를 선택하는 프레임워크 |
| **STUN** | 공인 IP 및 포트 정보를 알아내기 위한 서버 |
| **TURN** | P2P 연결이 불가한 경우 미디어를 **중계**해주는 서버 (대역폭 소모 ↑) |

### mediasoup

- **WebRTC SFU 서버 구현 라이브러리** 
- 미디어 흐름 제어를 위한 객체 기반 구조

### mediasoup 주요 개념

| 용어       | 설명 |
|------------|-----|
| **Worker**     | 미디어 처리를 담당하는 **백그라운드 프로세스** |
| **Router**     | 한 방(room)에 하나씩 존재, **미디어 경로 제어** 역할 |
| **Transport**  | **클라이언트와 서버 간 미디어 연결 통로** (DTLS/ICE 등 포함) |
| **Producer**   | 클라이언트가 **미디어를 송출할 때** 생성되는 객체 |
| **Consumer**   | 클라이언트가 **미디어를 수신할 때** 생성되는 객체 |

<details>
<summary>상세 설명</summary>

- **Worker** : 미디어 처리를 담당하는 **엔진** 같은 프로세스, 실제 미디어 데이터(음성, 영상)를 다룸 
- **Router** : producer가 보내는 미디어 스트림을 받아서 어떤 consumer에게 보낼지 정함. 하나의 방에는 하나의 Router가 있음
- **Transport** : ICE 연결, DTLS 핸드셰이크 등을 포함한 통신 경로, consumer/producer 각각 쓰는 tranport가 다름
</details>


## rtpCapabilities

- **WebRTC 연결 시 사용할 수 있는 미디어 형식 (코덱, 해상도 등) 목록**
- 송신자와 수신자의 **미디어 호환 여부를 판단**하기 위해 필수
- 예시:
  ```
  {
    "codecs": [
      { "kind": "audio", "mimeType": "audio/opus", ... },
      { "kind": "video", "mimeType": "video/VP8", ... }
    ]
  }
  ```

## DTLS
- **UDP 기반** 통신에서 **보안 연결을 위한 SSL/TLS 계층**
- WebRTC에서 **미디어 데이터 암호화 및 인증** 역할 수행

---

## 서버 미디어 처리 흐름
<details>
<summary>접기/펼치기</summary>

### 1) rtpCapabilities 요청

```
socket.on('create-router', (_, callback) => {
  callback({ rtpCapabilities: router.rtpCapabilities });
});
```

### 2) Transport 생성
<details>
<summary>create-transport 코드 보기</summary>

```bash
socket.on('create-transport', async ({ direction }, callback) => {
  const transport = await router.createWebRtcTransport({
    listenIps: [{ ip: '0.0.0.0', announcedIp: 'bidcastserver.kro.kr' }],
    enableUdp: true,
    enableTcp: true,
    preferUdp: true,
    portRange: { min: 40000, max: 40010 },
    iceServers: [
      { urls: 'stun:stun.l.google.com:19302' },
      { urls: 'turn:bidcastserver.kro.kr:3478', username: 'webrtc', credential: '1234' }
    ],
  });
  transports.set(transport.id, { transport, socketId: socket.id, direction });

  callback({
    id: transport.id,
    iceParameters: transport.iceParameters,
    iceCandidates: transport.iceCandidates,
    dtlsParameters: transport.dtlsParameters,
  });
});
```
</details>

### 3) Transport 연결 (DTLS 핸드쉐이크)
```
socket.on('connect-transport', async ({ dtlsParameters, transportId }, callback) => {
  const data = transports.get(transportId);
  if (!data) throw new Error('Transport not found');
  await data.transport.connect({ dtlsParameters });
  callback();
});
```

### 4) Producer 생성 (미디어 송출 시작)
<details> 
<summary>produce 요청 처리</summary>

```
socket.on('produce', async ({ kind, rtpParameters, transportId, roomId }, callback) => {
  const data = transports.get(transportId);
  if (!data) throw new Error('Transport not found');
  const producer = await data.transport.produce({ kind, rtpParameters });

  if (!producers.has(roomId)) producers.set(roomId, new Map());
  producers.get(roomId).set(producer.id, { producer, socketId: socket.id, kind });

  socket.broadcast.to(roomId).emit('new-producer', {
    producerId: producer.id,
    socketId: socket.id,
    kind,
  });

  callback({ id: producer.id });
});
```
</details>

### 5) Consumer 생성 (미디어 수신 요청)
<details> <summary>consume 요청 처리</summary>

```
socket.on('consume', async ({ producerId, rtpCapabilities, transportId, roomId }, callback) => {
  const data = transports.get(transportId);
  if (!data) throw new Error('Transport not found');

  const roomProducers = producers.get(roomId);
  if (!roomProducers || !roomProducers.has(producerId)) throw new Error('Producer not found');

  if (!router.canConsume({ producerId, rtpCapabilities })) throw new Error('Cannot consume');

  const consumer = await data.transport.consume({
    producerId,
    rtpCapabilities,
    paused: false,
  });

  consumers.set(consumer.id, { consumer, socketId: socket.id });

  callback({
    id: consumer.id,
    producerId,
    kind: consumer.kind,
    rtpParameters: consumer.rtpParameters,
  });
});
```
</details>

### 6) Consumer 재생 시작
```
socket.on('consumer-resume', async ({ consumerId }) => {
  const data = consumers.get(consumerId);
  if (!data) return;
  await data.consumer.resume();
});
```

### 7) 연결 종료 시 자원 정리
<details> <summary>disconnect 처리 전체 코드</summary>

```bash
socket.on('disconnect', () => {
  for (const [transportId, data] of transports) {
    if (data.socketId === socket.id) {
      data.transport.close();
      transports.delete(transportId);
    }
  }
  for (const [roomId, roomProducers] of producers) {
    for (const [producerId, data] of roomProducers) {
      if (data.socketId === socket.id) {
        data.producer.close();
        roomProducers.delete(producerId);
        io.emit('user-disconnected', {
          socketId: socket.id,
          producerId,
        });
      }
    }
    if (roomProducers.size === 0) producers.delete(roomId);
  }
  for (const [consumerId, data] of consumers) {
    if (data.socketId === socket.id) {
      data.consumer.close();
      consumers.delete(consumerId);
    }
  }
});
```
</details>

</details>

</details>


## 2. 서버 주요 전역 변수 및 함수 정리

<details>
<summary>내용 보기</summary>

---

### 1. mediasoup 관련 변수

- `transports` (Map)  
  transportId를 key로, `{ transport, socketId, direction }` 객체를 value로 저장  
  - mediasoup WebRTC 연결용 Transport 객체 관리용

- `producers` (Map)  
  roomId를 key로, value는 또 다른 Map  
  내부 Map: producerId를 key로 `{ producer, socketId, kind }`  
  - 각 방(room)별 미디어 송출자(Producer) 객체 저장

- `consumers` (Map)  
  consumerId를 key로, `{ consumer, socketId }` 저장  
  - 미디어 수신자(Consumer) 객체 관리


### 2. 소켓 & 방 관리 변수

- `socketRoomMap` (Map)  
  socketId를 key로, 현재 사용자가 입장한 roomId 저장  
  - 한 사용자가 여러 방에 동시에 접속하지 못하도록 제한하는 역할

- `socketIdMap` (Map)  
  loginId를 key로, 소켓 ID 저장  
  - 로그인 유저와 소켓 연결 관리

- `auctionHostMap` (Map)  
  auctionId를 key로, 해당 경매의 호스트 소켓 ID 저장  
  - 경매별 호스트 소켓 관리

- `auctionStates` (객체)  
  auctionId를 key로, 경매 진행 상태(선택된 상품 등) 저장  
  - 경매 상태 및 진행 정보 관리

- `auctionUserStatus` (객체)  
  socketId를 key로, 입찰자 닉네임, 마지막 입찰가 등 상태 저장  
  - 입찰자 개인별 현재 상태 정보를 저장


### 3. 상품 데이터 정규화 함수

```
function normalizeProduct(raw) {
  return {
    prodKey: raw.prod_key,
    aucKey: raw.auc_key,
    prodName: raw.prod_name,
    prodDetail: raw.prod_detail,
    unitValue: raw.unit_value,
    initPrice: raw.init_price,
    currentPrice: raw.current_price,
    finalPrice: raw.final_price,
    winnerId: raw.winner_id,
    prodStatus: raw.prod_status,
    fileUrl: raw.file_url
  };
}
```
</details>

## 3. 클라이언트-서버 통신 흐름

<details>
<summary>내용 보기</summary>

### 1. 경매장 입장/퇴장

#### 입장
- 요청: `join-room(roomId, userInfo)`
- 사용 변수:
  - `socketRoomMap[socket.id] = roomId`
  - `socketIdMap[userInfo.loginId] = socket.id`
  - `auctionUserStatus[socket.id] = { nickname, lastBid: 0 }`
- 응답:
  - 입장자 목록 반환
  - 기존 유저에게 `user-status-update` 브로드캐스트

#### 퇴장
- 발생: 소켓 종료 시 자동
- 처리:
  - `transports`, `producers`, `consumers`에서 socket 자원 제거
  - `socketRoomMap`, `socketIdMap`, `auctionUserStatus`에서 제거
- 브로드캐스트: `user-disconnected`


### 2. 영상 송출 (mediasoup)

#### 미디어 스트림 수신 발송
- 요청: `produce(kind, rtpParameters, transportId, roomId)`
- 사용 변수:
  - `transports[transportId]` 확인
  - `producers[roomId][producerId] = { producer, socketId, kind }`
- 응답: `producerId`
- 브로드캐스트: `new-producer`

#### 기존 Producer 리스트 요청
- 요청: `get-existing-producers(roomId)`
- 사용 변수:
  - `producers` Map(roomId → Map(producerId → { producer, socketId, kind }))  
  - `auctionHostMap` (roomId → hostSocketId)
- 응답: 콜백으로 `{ existingProducers, hostSocketId }` 반환

#### Producer 삭제
- 요청: `close-producer(roomId)`
- 사용 변수:
  - `producers` Map에서 socket.id에 해당하는 Producer 객체 제거 및 close 호출
- 브로드캐스트:
  - 해당 경매방에 `user-disconnected` 이벤트 (해당 socketId, producerId) 전송
- 추가:
  - 방 내 Producer가 없으면 `producers` Map에서 해당 roomId 삭제

#### 미디어 스트림 수신 요청
- 요청: `consume(producerId, rtpCapabilities, transportId, roomId)`
- 사용 변수:
  - `transports[transportId]` 확인
  - `consumers[consumerId] = { consumer, socketId }`
- 응답: `{ consumerId, producerId, kind, rtpParameters }`

#### 재생시작
- 요청: `consumer-resume(consumerId)`
- 처리: `consumers[consumerId].consumer.resume()`


### 3. 채팅

#### 채팅
- 요청: `chat-message(roomId, userId, message)`
- 사용 변수:
  - `socketRoomMap[socket.id]` → roomId 추출
- 브로드캐스트: `chat-message`


### 4. 입찰

#### 입찰시도
- 요청: `bid-attempt(roomId, productId, bidAmount,userLoginId)`
- 사용 변수:
  - `auctionStates[auctionId]` → 선택된 상품 상태 갱신  
  - `auctionUserStatus[auctionId][socket.id]` → 입찰자별 입찰 기록 저장  
  - `pool.query()` → 사용자 정보 조회, 상품 가격 갱신, 입찰 기록 저장
- 브로드캐스트:
  - `user-status-update` → 입찰자 상태 갱신 전체 전송  
  - `bid-update` → 입찰 결과(상품, 입찰자 정보 포함) 전송  
  - `bid-rejected` → 입찰 실패 시 개별 응답


### 5. 경매 관리 (호스트)

#### 상품 선택
- 요청: `host-selected-product(auctionId, product)`
- 사용 변수:
  - `auctionStates[auctionId].selectedProduct = product`
  - DB `product.prod_status = 'P'` 업데이트
- 브로드캐스트:
  - 해당 경매방에 `host-selected-product` (선택된 상품 정보) 전송

#### 낙찰 / 유찰 상태 변경
- 요청: `bid-status(auctionId, prodKey, winner_id, status)`
- 사용 변수:
  - DB `product.prod_status` 업데이트 (`'C'` or `'F'`)
  - `auctionUserStatus[auctionId]`에서 해당 상품 입찰 기록 삭제
- 브로드캐스트:
  - 해당 방에 `bid-status` (상품 상태, 낙찰자 정보) 전송 (호스트 제외)
  - 이후 `user-status-update` (전체 유저 상태 갱신)

#### 최고 입찰자 되돌리기
- 요청: `revert-bidder(auctionId, prodKey, winnerId, finalPrice)`
- 사용 변수:
  - DB `product.final_price`, `winner_id` 업데이트
  - `auctionStates[auctionId].selectedProduct` 업데이트
  - DB에서 낙찰자 닉네임 조회
- 브로드캐스트:
  - 해당 방에 `bid-update` (업데이트된 상품·낙찰자 정보) 전송

#### 입찰 단위 변경
- 요청: `change-bid-unit(roomId, newUnit)`
- 사용 변수:
  - `auctionStates[roomId].selectedProduct.unitValue = newUnit`
- 브로드캐스트: `bid-unit-changed`

#### 경매 종료
- 요청: `auction-end(auctionId)`
- 사용 변수:
  - DB `auction` 테이블 status='종료', end_time=NOW()로 업데이트
- 브로드캐스트: `auction-ended`


### 6. 기타

#### 시청자 수 조회
- 요청: `get-guest-counts(auctionIds[])`
- 사용 변수:
  - `io.sockets.adapter.rooms` (각 경매방(room) 인원 수 조회)
- 응답:
  - 콜백 함수로 `{ auctionId: 인원수 }` 객체 반환

</details>

---

# 클라이언트 코드분석

## 1. 빌드 및 배포 관련 설정
<details>
  <summary><strong>내용 보기</strong></summary>

- **Vite**를 사용해 React 앱을 **멀티 페이지(entry)** 구조로 빌드함
- `vite.config.js`의 `rollupOptions.input`에 각 진입 JSX 파일들이 정의되어 있으며, 빌드시 결과물은 `resources/static/bundle` 경로에 JS/CSS 파일로 출력됨.
- 각 페이지는 Thymeleaf 템플릿(.html)에서 `${pageName}` 변수를 통해 해당 JS/CSS를 동적으로 로드하고, `<div id="root">`에 React 컴포넌트가 마운트됨. → SPA가 아닌 **멀티 페이지 기반 구조**임.
- 
- 정적 리소스는 **Spring Boot**에서 서빙하며, HTTPS 및 프록시는 **Nginx + Certbot**을 통해 처리함.
- `vite.config.js`의 `server.https` 설정은 **로컬 개발용(테스트용 HTTPS)**이며, 배포 환경에서는 사용되지 않음.
- Jenkins를 통한 자동 배포 스크립트 및 EC2/Nginx 설정 등 인프라 관련 내용은 [AWS세팅.md](./AWS세팅.md) 문서에 별도로 정리되어 있음.

<details>
<summary><strong>vite.config.js 주요 설정</strong></summary>

```js
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import fs from 'fs';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  root: 'src/main/react',
  build: {
    outDir: '../resources/static/bundle',
    emptyOutDir: true,
    cssCodeSplit: true,
    rollupOptions: {
      input: {
        home: path.resolve(__dirname, 'src/home/home.jsx'),
        login: path.resolve(__dirname, 'src/auth/login/login.jsx'),
        bidGuest: path.resolve(__dirname, 'src/bidGuest/bidGuest.jsx'),
        // ... 생략 가능
      },
      output: {
        entryFileNames: 'js/[name].bundle.js',
        chunkFileNames: 'chunk/[name].chunk.js',
        assetFileNames: `css/[name].css`,
      },
    },
  },
  server: {
    https: {
      key: fs.readFileSync('certs/key.pem'),
      cert: fs.readFileSync('certs/cert.pem'),
    },
    host: '0.0.0.0',
    port: 3200,
  },
});
```
</details>

<details>
  <summary><strong>Thymeleaf 템플릿 예시</strong></summary>

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
  <title>[[${pageName}]]</title>
  <link rel="stylesheet" th:href="@{'/bundle/css/' + ${pageName} + '.css'}" onerror="this.remove()" />
</head>
<body>
  <div id="root"></div>
  <script type="module" th:src="@{'/bundle/js/' + ${pageName} + '.bundle.js'}"></script>
</body>
</html>
```
</details>

<details>
  <summary><strong>Spring Boot Controller — 멀티 페이지 매핑</strong></summary>

```java
package com.project.bidcast.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;

@Controller
public class MainController {

    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home.do";
    }

    @GetMapping("/{pageName}.do") // 모든 페이지 요청을 처리
    public String page(HttpSession session , @PathVariable String pageName, Model model) {
        model.addAttribute("pageName", pageName); // Thymeleaf에 pageName 전달
        System.out.println("뷰이름:" + pageName);

        if(session.getAttribute("id") != null) 
            System.out.println(session.getAttribute("id"));

        return "view"; // 항상 동일한 view.html 템플릿으로 이동
    }
}
```
</details>

<details>
  <summary><strong>vite.config.js의 로컬 개발용 HTTPS 설정 (배포 시 미사용)</strong></summary>

```js
server: {
  https: {
    key: fs.readFileSync('certs/key.pem'),
    cert: fs.readFileSync('certs/cert.pem'),
  },
  host: '0.0.0.0',
  port: 3200,
}
```
</details>

</details>

## 2. WebSocket & WebRTC

<details>
  <summary><strong>내용 보기</strong></summary>

### 1.1 Host (호스트) 측 구현

- 호스트 측은 경매 방송을 송출하고 참가자들의 상태를 관리하는 핵심 역할을 수행
- WebSocket과 WebRTC를 통해 실시간 영상 송출과 입찰 정보 교환이 이루어짐

#### 1.1.1 주요 변수 및 상태

| 변수명           | 설명                                         |
|------------------|----------------------------------------------|
| `peers`          | 전체 참가자의 소켓 ID를 키로 하는 객체<br>예: `{ socketId: { stream } }` |
| `hostSocketId`   | 호스트(방송 송출자)의 소켓 ID                 |
| `mySocketId`     | 현재 클라이언트(호스트)의 소켓 ID              |
| `userInfoMap`    | 참가자별 닉네임, 입찰가 등이 저장된 객체        |
| `selectedProductIdx` | 현재 경매 중인 상품의 인덱스                   |
| `products`       | 경매 상품 리스트 배열                           |
| `prevHighestBidder` | 이전 최고 입찰자 정보 (재입찰 반영 시 사용)      |

#### 1.1.2 주요 WebSocket 이벤트 및 요청

| 이벤트명               | 목적 및 설명                                               | 발신자  | 수신자       |
|------------------------|-----------------------------------------------------------|---------|--------------|
| `host-selected-product` | 호스트가 특정 경매 상품을 선택했음을 서버에 알림          | 호스트  | 서버, 클라이언트 |
| `bid-status`            | 낙찰(완료), 유찰(실패) 상태를 서버에 전달                  | 호스트  | 서버, 클라이언트 |
| `revert-bidder`         | 최고 입찰자를 이전 입찰자로 되돌릴 때 서버에 요청           | 호스트  | 서버, 클라이언트 |

#### 1.1.3 WebRTC 미디어 스트림 관리

- **MediaStream 송출**: 호스트는 자신의 카메라/마이크 스트림을 `peers[hostSocketId].stream` 형태로 관리하며, 이를 메인 비디오 컴포넌트(`MainVideo.jsx`)에 전달해 송출함
- **참가자 스트림 수신**: 호스트는 참가자들의 스트림도 `peers` 객체를 통해 수신, `VideoGrid.jsx`를 통해 화면에 표시
- **음소거 및 볼륨 제어**: 각 비디오 스트림에 대해 음소거, 볼륨 조절 기능이 구현되어 있으며 말하는 사람 감지도 가능

--- 

### 1.2 Client (게스트) 측 구현

#### 1.2.1 주요 변수 및 상태

| 변수명              | 설명                                                  |
|---------------------|-------------------------------------------------------|
| `peers`             | 전체 참가자의 소켓 ID를 키로 하는 객체<br>예: `{ socketId: { stream } }` |
| `hostSocketId`      | 호스트 소켓 ID                                        |
| `mySocketId`        | 현재 클라이언트(게스트)의 소켓 ID                     |
| `userInfoMap`       | 참가자별 닉네임, 입찰가 등이 저장된 객체               |
| `product`           | 현재 경매 중인 상품 정보                               |

#### 1.2.2 주요 WebSocket 이벤트 및 요청

| 이벤트명           | 목적 및 설명                                         | 발신자  | 수신자            |
|--------------------|-----------------------------------------------------|---------|-------------------|
| `bid-attempt`      | 입찰 시도 요청                                       | 게스트  | 서버, 클라이언트  |
| `bid-update`       | 입찰가 변경 알림                                     | 서버    | 모든 클라이언트   |
| `host-selected-product` | 호스트가 상품을 선택했음을 알림                   | 서버    | 클라이언트 전원   |
| `bid-status`       | 낙찰, 유찰 상태 업데이트                             | 서버    | 클라이언트 전원   |

#### 1.2.3 WebRTC 미디어 스트림 관리

- 게스트는 자신의 카메라/마이크 스트림을 `peers[mySocketId].stream` 으로 관리
- 호스트 및 다른 참가자들의 스트림은 `peers` 객체를 통해 받아 `VideoGrid` 컴포넌트에 표시
- 음소거, 볼륨 조절 기능이 각 비디오 스트림별로 존재하며, 음성 발화 상태도 감지 가능

</details>

## 3. 클라이언트 - 서버 통신 흐름

| 기능               | 시작 주체    | 이벤트 이름              | 서버 처리 내용                         | 클라이언트 처리 내용                  |
|--------------------|--------------|-------------------------|--------------------------------------|-------------------------------------|
| 입찰 시도          | 게스트       | `bid-attempt`           | 입찰가 유효성 검사 및 최고 입찰자 갱신 후 `bid-update` 방송 | `bid-update` 수신 후 UI 및 상태 갱신 |
| 경매 상품 선택     | 호스트       | `host-selected-product` | 상품 변경 저장 및 모든 클라이언트에 방송 | 상품 정보 갱신 및 UI 변경            |
| 낙찰/유찰 상태 변경| 호스트       | `bid-status`             | 경매 상태 저장 및 방송                | 상태 메시지 및 UI 갱신               |
| 최고 입찰자 되돌리기| 호스트      | `revert-bidder`          | 최고 입찰자 정보 재설정 및 방송      | 입찰자 정보 재갱신                   |
| WebRTC 미디어 송출 | 호스트/게스트| WebRTC 시그널링          | SFU 역할로 미디어 스트림 중계        | 스트림 생성 및 수신, 비디오 컴포넌트에 출력 |
| 실시간 채팅        | 호스트/게스트| `chat-message`     | 메시지 중계 및 브로드캐스트           | 메시지 수신 및 UI 업데이트           |

## 4. 인증 및 권한 관리 흐름 (Spring Security 관련 포함)

<details>
  <summary><strong>내용 보기</strong></summary>

### 4.1 주요 기능 개요

- **비밀번호 암호화**: `BCryptPasswordEncoder`를 사용해 비밀번호를 안전하게 암호화
- **CSRF 비활성화**: API 서버 환경에 맞게 CSRF 보호는 비활성화 상태
- **접근 권한 설정**:
  - 특정 경로(`/css/**`, `/js/**`, `/img/**`, `/home.do`, `/login.do` 등)는 인증 없이 접근 허용
  - 나머지 경로는 인증 필요
- **세션 관리**: 필요 시에만 세션 생성 (`SessionCreationPolicy.IF_REQUIRED`)
- **인증 실패 처리**: 인증이 필요한 요청에 인증되지 않은 경우 `/login.do` 페이지로 리다이렉트
- **로그인 설정**:
  - 로그인 페이지는 `/login.do`
  - 로그인 처리 URL은 `/login`
  - 로그인 성공 시 `/home.do`로 이동
  - 로그인 실패 시 HTTP 401 Unauthorized 상태 반환
- **로그아웃 설정**:
  - 로그아웃 URL은 `/logout`
  - 로그아웃 성공 시 `/home.do`로 이동
  - 세션 무효화 및 `JSESSIONID` 쿠키 삭제

---

### 4.2 설정 주요 코드 설명

| 설정 항목            | 내용 및 역할                                         |
|---------------------|-----------------------------------------------------|
| `passwordEncoder()` | 비밀번호를 BCrypt 방식으로 암호화                      |
| `csrf().disable()`  | CSRF 공격 방어 기능 비활성화 (API 환경에 적합)          |
| `authorizeHttpRequests()` | 인증 없이 접근 가능한 URL 경로 지정                    |
| `.anyRequest().authenticated()` | 나머지 모든 요청은 인증 필요                            |
| `sessionManagement()` | 세션 생성 정책 설정 (`IF_REQUIRED`: 필요시만 생성)     |
| `exceptionHandling()` | 인증 실패 시 로그인 페이지로 리다이렉트 처리             |
| `formLogin()`       | 로그인 페이지, 처리 URL, 성공 및 실패 핸들러 설정       |
| `logout()`          | 로그아웃 URL, 성공 후 이동 페이지, 세션 무효화, 쿠키 삭제 |

</details>