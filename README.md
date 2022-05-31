# 계약관리 시스템

## 1.업무요건
계약관리 API 시스템 구축
- 계약 생성 API
- 계약정보 조회 API 
- 계약정보 수정 API
- 예상 총 보험료 계산 API

## 2.개발환경
- macOS Big Sur
- IntelliJ IDEA 2021.3.2 (Community Edition)
- OpenJDK 11.0.13

## 3.프로젝트구조
### 계약관리 API 와 안내장발송 Batch 모듈로 분리
![프로젝트 구조](https://user-images.githubusercontent.com/11697119/161024384-a8c96613-e512-42e7-a788-429742a8385c.png)

## 4.Framework
- Gradle 7.4.1
- Spring-boot 2.6.5
- JPA 2.6.5
- Lombok 1.18.22
- H2Database 1.4.2
- Querydsl 5.0.0
- Swagger 3.0.0

## 5.Table Schema
![Table Schema](https://user-images.githubusercontent.com/11697119/160968076-062a112f-51c8-4bc7-b820-c21e08358c53.png)

## 6.테스트 실행(API)
### Intellij -> Run -> ContractControllerTest
com/pmguda/contract/api/ContractControllerTest.java
![API 테스트 코드](https://user-images.githubusercontent.com/11697119/161032062-e40f515a-6d8f-4bba-b0ff-fedd76a2cb04.png)

## 7.프로젝트 실행(API)
### Intellij -> Run -> ContractApiApplication
com/pmguda/contract/api/ContractApiApplication.java
- curl 계약정보 조회
```bash
curl -v -G 'localhost:8080/contract/get/test1' | json
```
- curl 계약정보 생성
```bash
curl -v -H 'Content-Type: application/json' -d  \
 '{
  "cntrPrd": 3,
  "insDto": [
    {"grntCd": "I000201"}
    ,{"grntCd": "I000202"}
  ],
  "prodCd": "P0002"
}' \
'localhost:8080/contract/create' | json
```
- curl 예상 총 보험료 계산

```bash
curl -v -H 'Content-Type: application/json' -d  \
 '{
"prodCd":"P0002",
"cntrPrd": 7,
"grntCd": [ "I000201", "I000202"]
}' \
'localhost:8080/contract/calc' | json
```

- curl 계약정보 수정

```bash
curl -v -H 'Content-Type: application/json' -x PUT 'localhost:8080/contract/update/test1' \
'{
"cntrPrd": "12"
,"grntCd": [ "I000201","I000202"]
,"cntrScd": "NORMAL"

}'| json
```
## 8.API 명세
### POSTMAN을 활용한 API명세
- https://documenter.getpostman.com/view/19792554/UVyrTGLh
- api/계약관리 API.postman_collection.json
![POSTMAN API 명세](https://user-images.githubusercontent.com/11697119/161042529-18114bb5-6ea7-4f87-8163-4befe7eda4ac.png)

### Swagger를 활용한 API명세
- 계약관리 API 프로젝트 실행
- 링크 접속 : http://localhost:8080/swagger-ui/index.html#/
  ![swagger 명세](https://user-images.githubusercontent.com/11697119/161027375-ea67e40d-9e0a-4c0a-8416-d196fae6f72f.png)

  
## 안내장 발송 기능
만기가 도래한 계약에 대해 일주일 전 안내장 발송
- 시스템 출력이나 로그 메세지로 대체

### Framework
- Gradle 7.4.1
- Spring-boot-batch 2.6.5
- JPA 2.6.5
- Lombok 1.18.22
- H2Database 1.4.2

### 테스트 실행
### Intellij -> Run -> BatchApplication
batch/src/main/java/com/pmguda/contract/api/BatchApplication.java
- H2 메모리 방식이라 재기동시 이전 데이터 초기화됨(자동적재 데이터는 생성)
- 기동시 테스트 데이터 자동 적재로 배치 테스트 결과 확인 가능
![안내장 배치 테스트 결과](https://user-images.githubusercontent.com/11697119/160975570-ed6c6d07-c974-41fa-8a33-0521130c4468.png)


[블로그](https://www.pmguda.com/)



