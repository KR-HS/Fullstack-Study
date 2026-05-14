##### (강의 수강링크)[https://www.inflearn.com/courses/lecture?courseId=337067&tab=curriculum&type=LECTURE&unitId=284426&subtitleLanguage=ko]

---

# Spring AI
 + Spring 개발자들을 위한 LLM 통합 도구
 + 다양한 모델을 일관되게 요청할 수 있음
 

 ## Spring AI 추상화 방식 및 구조
 + `**Prompt 클래스**`
     + Spring AI에서 모델에 보낼 메시지와 모델 파라미터 옵션 `ChatOptions`을 감싸는 역할
     + **어떤 메시지**를 **어떤 옵션**으로 보낼지를 함께 담고 있는 구조
     

 + `**ChatOptions**`
     + **LLM호출시 사용할 다양한 파라미터를 정의한 인터페이스**로 대부분의 LLM에서 공통으로 사용될 수 있는 옵션들만 포함
     + ChatOptions가 제공하는 속성은 벤더(AI모델)간 자동 변환
     + 정의되지 않은 추가 옵션은 직접 매핑이 필요

 + `**ChatModel**`
     + LLM과의 기본적인 상호작용을 담당하는 인터페이스
     + `ChatModel` 인터페이스를 구현한 클래스의 결과는 `ChatResponse`라는 공통된 응답 객체로 리턴
     + `ChatResponse`에는 **모델의 출력 메시지**, **메타정보**(사용된 프롬프트, 모델 파라미터, 응답 시간 등)가 포함

 ### 내부 동작 순서
 1. 입력으로 받은 `Prompt`를 벤더의 API 형식에 맞게 변환
 2. 변환된 메시지를 사용하여 벤더의 API 호출
 3. 벤더로부터 받은 응답을 `ChatResponse`형식으로 변환하여 반환
 