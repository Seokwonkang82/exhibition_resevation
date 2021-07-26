
# 전시회_예약시스템
![gallary](https://user-images.githubusercontent.com/86943781/126889014-7a965a01-9d65-4070-95f0-790d9afe4f52.png)

# Table of contents
- [전시회_예약시스템](#---)
  - [서비스 시나리오](#서비스-시나리오)
  - [체크포인트](#체크포인트)
  - [분석/설계](#분석설계)
  - [구현:](#구현-)
    - [DDD 의 적용](#ddd-의-적용)
    - [폴리글랏 퍼시스턴스](#폴리글랏-퍼시스턴스)
    - [폴리글랏 프로그래밍](#폴리글랏-프로그래밍)
    - [동기식 호출 과 Fallback 처리](#동기식-호출-과-Fallback-처리)
    - [비동기식 호출 과 Eventual Consistency](#비동기식-호출-과-Eventual-Consistency)
  - [운영](#운영)
    - [CI/CD 설정](#cicd설정)
    - [동기식 호출 / 서킷 브레이킹 / 장애격리](#동기식-호출-서킷-브레이킹-장애격리)
    - [오토스케일 아웃](#오토스케일-아웃)
    - [무정지 재배포](#무정지-재배포)

# 서비스 시나리오
- 기능적 요구사항
1. 전시회 관리자는 전시회를 등록한다.
2. 고객이 전시회를 선택하여 예약한다.
3. 예약이 확정되면 바우처가 활성화 된다.
4. 고객이 삭제된 전시회를 예약할 경우, 예약이 불가능하다.
5. 고객이 확정된 예약을 취소할 수 있다.
6. 고객이 예약을 취소되면 바우처가 삭제된다.
7. 고객은 전시회 예약 정보를 확인 할 수 있다.

- 비기능적 요구사항
1. 트랜잭션
    1. 전시회 상태가 예약 가능상태가 아니면 아예 예약이 성립되지 않아야 한다  Sync 호출 
1. 장애격리
    1. 바우처/마이페이지 기능이 수행되지 않더라도 예약은 365일 24시간 받을 수 있어야 한다.  Async (event-driven), Eventual Consistency
    1. 예약시스템이 과중되면 사용자를 잠시동안 받지 않고 잠시후에 하도록 유도한다.  Circuit breaker, fallback
1. 성능
    1. 고객이 자신의 예약 상태를 확인할 수 있도록 마이페이지가 제공 되어야 한다.  CQRS

# 분석/설계

## AS-IS 조직 (Horizontally-Aligned)

  ![image](https://user-images.githubusercontent.com/487999/79684144-2a893200-826a-11ea-9a01-79927d3a0107.png)

## TO-BE 조직 (Vertically-Aligned)

  ![조직구조](https://user-images.githubusercontent.com/86943781/126889313-3e66dbd6-5ae3-4d30-8f17-268d0e8f528c.png)



## Event Storming 결과
* MSAEz 로 모델링한 이벤트스토밍 결과: http://www.msaez.io/#/storming/4ipqChDLXyZzO1bWVcgkF55hJj12/678d2148590ce4171f0789607d49a84a
                                     


### 이벤트 도출
![event1](https://user-images.githubusercontent.com/86943781/126898598-4e0e4259-d7ea-4340-bd12-cca5da5087e7.png)


### 이벤트 도출-부적격삭제
![event2](https://user-images.githubusercontent.com/86943781/126898617-d1a0da2d-4cc4-47df-847c-4d68fc0a7606.png)


### 액터, 커맨드, 폴리시 부착
![event3](https://user-images.githubusercontent.com/86943781/126899173-1e89b9d7-80bf-4329-8671-7dbe086dc1e2.png)


### 어그리게잇과 바운디드 컨텍스트로 묶기
![event4](https://user-images.githubusercontent.com/86943781/126899016-f6061c84-f4d1-45d1-93dd-8f6b697356e1.png)


### 폴리시의 이동과 컨텍스트 매핑 (점선은 Pub/Sub, 실선은 Req/Resp)

![event5](https://user-images.githubusercontent.com/86943781/126899024-98eed36a-7768-44af-b193-421c4566b16c.png)


### 완성된 모형
![diagram](https://user-images.githubusercontent.com/86943781/126899261-0ecb33b1-7017-4cbc-b17f-18816a67781c.png)




- View Model 추가
- 도메인 서열
  - Core : reservation
  - Supporting : exhibition, mypage
  - General : voucher

## 헥사고날 아키텍처 다이어그램 도출

![hexa](https://user-images.githubusercontent.com/86943781/126890207-f36343eb-0c12-4b11-955a-9f9e2e66df5f.png)




    - Chris Richardson, MSA Patterns 참고하여 Inbound adaptor와 Outbound adaptor를 구분함
    - 호출관계에서 PubSub 과 Req/Resp 를 구분함
    - 서브 도메인과 바운디드 컨텍스트의 분리:  각 팀의 KPI 별로 아래와 같이 관심 구현 스토리를 나눠가짐


# 구현:

분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라, 각 BC별로 대변되는 마이크로 서비스들을 스프링부트로 구현하였다. 구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 808n 이다)

## 시나리오에 따른 처리
1. 전시회 관리자는 전시회를 등록한다.
```sh
http a349aca93201b443cb8931e6193e56cb-252808345.ap-northeast-2.elb.amazonaws.com:8080/exhibitions exhibitionName="Monet" exhibitionDate="20210725" exhibitionStatus="Available" exhibitionType="gallary"
http a349aca93201b443cb8931e6193e56cb-252808345.ap-northeast-2.elb.amazonaws.com:8080/exhibitions exhibitionName="Picasso" exhibitionDate="20210812" exhibitionStatus="Available" exhibitionType="gallary"
http a349aca93201b443cb8931e6193e56cb-252808345.ap-northeast-2.elb.amazonaws.com:8080/exhibitions
```
![exhibition1](https://user-images.githubusercontent.com/86943781/126931572-ffd13acd-bae2-4db6-ab28-f800145f1b44.png)

2. 고객은 전시회를 선택하여 예약한다.
```sh
http aab08da4631a24878a5de445cefc53cc-516544677.ap-northeast-2.elb.amazonaws.com:8080/reservations exhibitionId=1 memberName="seokwon"

```
![예약](https://user-images.githubusercontent.com/86943781/126958496-c71f423e-e165-4911-82bb-798f60cfaa54.png)


3. 예약이 확정되면 바우처가 활성화 된다
```sh
http aab08da4631a24878a5de445cefc53cc-516544677.ap-northeast-2.elb.amazonaws.com:8080/vouchers
```
![vouchercre](https://user-images.githubusercontent.com/86943781/126958697-f9d6a033-fdd3-441f-9641-a8317445b279.png)

4. 고객이 삭제된 전시회를 예약할 경우, 예약이 불가능하다.
```sh
http aab08da4631a24878a5de445cefc53cc-516544677.ap-northeast-2.elb.amazonaws.com:8080/exhibitions
```
![cancelledex](https://user-images.githubusercontent.com/86943781/126960239-1a3f7552-d566-4ca8-803c-74ef5860ccad.png)

```sh
http aab08da4631a24878a5de445cefc53cc-516544677.ap-northeast-2.elb.amazonaws.com:8080/reservations exhibitionId=3 memberName="seokwon"
```
![fail](https://user-images.githubusercontent.com/86943781/126960492-9a1b29d9-a62d-465f-8237-f51c01f61d98.png)


5. 고객이 확정된 예약을 취소할 수 있다.
```sh
http PATCH aab08da4631a24878a5de445cefc53cc-516544677.ap-northeast-2.elb.amazonaws.com:8080/reservations/2 exhibitionStatus="Deleted"
```
![cancel](https://user-images.githubusercontent.com/86943781/126958950-9065d66d-942d-45dc-9f2e-535fc2918dd2.png)


6. 예약이 취소되면 바우처가 비활성화 된다.
```sh
http aab08da4631a24878a5de445cefc53cc-516544677.ap-northeast-2.elb.amazonaws.com:8080/vouchers
```
![invalid](https://user-images.githubusercontent.com/86943781/126959144-a6aca9d9-4d76-4d27-8d50-52149dd4125e.png)


7. 고객은 전시회 예약 정보를 확인 할 수 있다.
```sh
http aab08da4631a24878a5de445cefc53cc-516544677.ap-northeast-2.elb.amazonaws.com:8080/mypages
```
![mypages](https://user-images.githubusercontent.com/86943781/126960884-784432a3-05f4-42c9-bc02-5493f5bdaa04.png)


## DDD 의 적용
- 위 이벤트 스토밍을 통해 식별된 Micro Service 전체 4개를 모두 구현하였으며 그 중 mypage는 CQRS를 위한 서비스이다.

|MSA|기능|port|URL|
| :--: | :--: | :--: | :--: |
|resort| 전시회 관리 |8081|http://localhost:8081/exhibitions|
|reservation| 예약정보 관리 |8082|http://localhost:8082/reservations|
|mypage| 예약내역 조회 |8083|http://localhost:8083/mypages|
|voucher| 바우처 관리 |8084|http://localhost:8084/vouchers|
|gateway| gateway |8088|http://localhost:8088|

## Gateway 적용
- API GateWay를 통하여 마이크로 서비스들의 진입점을 통일할 수 있다. 
다음과 같이 GateWay를 적용하였다.

```yaml
- gateway 서비스의 application.yml

server:
  port: 8088

---

spring:
  profiles: default
  cloud:
    gateway:
      routes:
        - id: exhibition
          uri: http://localhost:8081
          predicates:
            - Path=/exhibitions/** 
        - id: reservation
          uri: http://localhost:8082
          predicates:
            - Path=/reservations/** 
        - id: mypage
          uri: http://localhost:8083
          predicates:
            - Path= /mypages/**
        - id: voucher
          uri: http://localhost:8084
          predicates:
            - Path=/vouchers/** 
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "*"
            allowedMethods:
              - "*"
            allowedHeaders:
              - "*"
            allowCredentials: true


---

spring:
  profiles: docker
  cloud:
    gateway:
      routes:
        - id: exhibition
          uri: http://exhibition:8080
          predicates:
            - Path=/exhibitions/** 
        - id: reservation
          uri: http://reservation:8080
          predicates:
            - Path=/reservations/** 
        - id: mypage
          uri: http://mypage:8080
          predicates:
            - Path= /mypages/**
        - id: voucher
          uri: http://voucher:8080
          predicates:
            - Path=/vouchers/** 
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "*"
            allowedMethods:
              - "*"
            allowedHeaders:
              - "*"
            allowCredentials: true

server:
  port: 8080
  
``` 
## 폴리글랏 퍼시스턴스
- CQRS 를 위한 mypage 서비스만 DB를 구분하여 적용함. 인메모리 DB인 hsqldb 사용.
```
- maypage 서비스의 pom.xml
<!-- 
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
-->
    <dependency>
        <groupId>org.hsqldb</groupId>
        <artifactId>hsqldb</artifactId>
        <version>2.4.0</version>
        <scope>runtime</scope>
    </dependency>
```
## CQRS & Kafka
- 타 마이크로서비스의 데이터 원본에 접근없이 내 서비스의 화면 구성과 잦은 조회가 가능하게 mypage에 CQRS 구현하였다.
- 모든 정보는 비동기 방식으로 발행된 이벤트(예약, 예약취소, 가능상태변경)를 수신하여 처리된다.

예약 실행

<img width="993" alt="image" src="https://user-images.githubusercontent.com/85722851/125231135-2769de00-e315-11eb-8b6e-f0e4711c2760.png">

카프카 메시지
<img width="962" alt="image" src="https://user-images.githubusercontent.com/85722851/125224363-73625600-e308-11eb-9cd2-2dfccf0aa78f.png">
```bash
{"eventType":"ReservationRegistered","timestamp":"20210712022656","id":1,"resortId":2,"resortName":"Seoul","resortStatus":"Confirmed","resortType":"Hotel","resortPeriod":"7/23~25","resortPrice":100000.0,"memberName":"sim sang joon"}
{"eventType":"ResortStatusChanged","timestamp":"20210712022656","id":2,"resortName":"Seoul","resortStatus":"Not Available","resortType":"Hotel","resortPeriod":"7/23~25","resortPrice":100000.0}
{"eventType":"ReservationCanceled","timestamp":"20210712022719","id":1,"resortId":2,"resortName":"Seoul","resortStatus":"Cancelled","resortType":"Hotel","resortPeriod":"7/23~25","resortPrice":100000.0,"memberName":"sim sang joon"}
{"eventType":"ResortStatusChanged","timestamp":"20210712022719","id":2,"resortName":"Seoul","resortStatus":"Available","resortType":"Hotel","resortPeriod":"7/23~25","resortPrice":100000.0}
```

예약/예약취소 후 mypage 화면

<img width="992" alt="image" src="https://user-images.githubusercontent.com/85722851/125231312-7c0d5900-e315-11eb-93bf-af4f025fc3d3.png">


## 동기식 호출 과 Fallback 처리

- 분석단계에서의 조건 중 하나로 예약(reservation)->전시상태확인(exhibition) 간의 호출은 동기식 일관성을 유지하는 트랜잭션으로 처리하기로 하였다. 호출 프로토콜은 
  FeignClient를 이용하여 호출하였다

- 전시서비스를 호출하기 위하여 Stub과 (FeignClient) 를 이용하여 Service 대행 인터페이스 (Proxy) 를 구현 

```java
# (reservation) ExhibitionService.java
```

![exhibitionservice](https://user-images.githubusercontent.com/86943781/126901422-51d02f4c-d2d4-4e9d-b0d0-39c86ada2eed.png)

- 예약을 처리 하기 직전(@PrePersist)에 ResortSevice를 호출하여 서비스 상태와 Exhibition 세부정보도 가져온다.
 
```java
# Reservation.java (Entity)
```

![reservation](https://user-images.githubusercontent.com/86943781/126901556-35cba206-e16c-44e1-a8cf-a94cf6baef5e.png)


- 동기식 호출에서는 호출 시간에 따른 타임 커플링이 발생하며, 시스템이 장애로 예약을 못받는다는 것을 확인
<img width="1019" alt="image" src="https://user-images.githubusercontent.com/85722851/125232225-2174fc80-e317-11eb-9186-98995cf27f97.png">


- 또한 과도한 요청시에 서비스 장애가 도미노 처럼 벌어질 수 있다. (서킷브레이커, 폴백 처리는 운영단계에서 설명한다.)




## 비동기식 호출 / 시간적 디커플링 / 장애격리 / 최종 (Eventual) 일관성 테스트
- 예약이 이루어진 후에 결제시스템에 결제요청과 마이페이지시스템에 이력을 보내는 행위는 동기식이 아니라 비 동기식으로 처리하여 예약이 블로킹 되지 않아도록 처리한다.
- 이를 위하여 예약기록을 남긴 후에 곧바로 예약완료가 되었다는 도메인 이벤트를 카프카로 송출한다(Publish)
 
```java
@Entity
@Table(name="Reservation_table")
public class Reservation {
 ...
    @PostPersist
    public void onPostPersist() throws Exception {
        ...
        ReservationRegistered reservationRegistered = new ReservationRegistered();
        BeanUtils.copyProperties(this, reservationRegistered);
        reservationRegistered.publishAfterCommit();
    }
}
```
- 결제시스템과 마이페이지시스템에서는 예약완료 이벤트에 대해서 이를 수신하여 자신의 정책을 처리하도록 PolicyHandler 를 구현한다

결제시스템(팀과제에서는 미구현)
```java

@Service
public class PolicyHandler{
    @Autowired PaymentRepository paymentRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReservationRegistered_PaymentRequestPolicy(@Payload ReservationRegistered reservationRegistered){

        if(!reservationRegistered.validate()) return;
        System.out.println("\n\n##### listener PaymentRequestPolicy : " + reservationRegistered.toJson() + "\n\n");
        // Logic 구성 // 
    }
}
```
마이페이지시스템
```java
@Service
public class MyPageViewHandler {

    @Autowired
    private MyPageRepository myPageRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenReservationRegistered_then_CREATE_1 (@Payload ReservationRegistered reservationRegistered) {
        try {

            if (!reservationRegistered.validate()) return;

            // view 객체 생성
            MyPage myPage = new MyPage();
            // view 객체에 이벤트의 Value 를 set 함
            myPage.setId(reservationRegistered.getId());
            myPage.setMemberName(reservationRegistered.getMemberName());
            myPage.setResortId(reservationRegistered.getResortId());
            myPage.setResortName(reservationRegistered.getResortName());
            myPage.setResortStatus(reservationRegistered.getResortStatus());
            myPage.setResortType(reservationRegistered.getResortType());
            myPage.setResortPeriod(reservationRegistered.getResortPeriod());
            myPage.setResortPrice(reservationRegistered.getResortPrice());
            // view 레파지 토리에 save
            myPageRepository.save(myPage);
        
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
```
- 예약 시스템은 결제시스템/마이페이지 시스템과 완전히 분리되어있으며, 이벤트 수신에 따라 처리되기 때문에, 결제시스템/마이시스템이 유지보수로 인해 잠시 내려간 상태라도 예야을 받는데 문제가 없다:
```bash
# 마이페이지 서비스는 잠시 셧다운 시키고 결제시스템은 현재 미구현

1.리조트입력
http localhost:8082/resorts resortName="Jeju" resortType="Hotel" resortPrice=100000 resortStatus="Available" resortPeriod="7/23~25"
http localhost:8082/resorts resortName="Seoul" resortType="Hotel" resortPrice=100000 resortStatus="Available" resortPeriod="7/23~25"

2.예약입력
http localhost:8081/reservations resortId=2 memberName="sim sang joon" 
http localhost:8081/reservations #예약 정상 처리 확인

3.마이페이지서비스 기동

4.마이페이지확인
http localhost:8083/myPages #정상적으로 마이페이지에서 예약 이력이 확인 됨

```


# 운영

## CI/CD 설정

각 구현체들은 각자의 source repository 에 구성되었고, 각 서비스별로 Docker로 빌드를 하여, Docker Hub에 등록 후 deployment.yaml, service.yml을 통해 EKS에 배포함.
- git에서 소스 가져오기
```bash
git clone https://github.com/simair77/resort_reservation.git
```
- 각서비스별 packege, build, github push 실행
```bash
cd resort #서비스별 폴더로 이동
mvn package -B -Dmaven.test.skip=true #패키지

docker build -t simair/resort:latest . #docker build
docker push simair/resort:latest       #docker push

kubectl apply -f resort/kubernetes/deployment.yml #AWS deploy 수행
kubectl apply -f resort/kubernetes/service.yaml.  #AWS service 등록

```
- Docker Hub Image
![dockerimage](https://user-images.githubusercontent.com/86943781/126926356-44821b33-23bc-4e28-ad72-bb765589a3f3.png)



- 최종 Deploy완료
<img width="895" alt="image" src="https://user-images.githubusercontent.com/85722851/125232490-b4ae3200-e317-11eb-998c-88e5833866c7.png">

![deploy](https://user-images.githubusercontent.com/86943781/126927286-a0a959f8-505d-4a97-afaf-bc11739c8099.png)


## 동기식 호출 / 서킷 브레이킹 / 장애격리

* 서킷 브레이크 프레임워크 : Spring FeignClient + Hystrix 옵션을 사용

- 시나리오 : 예약(reservation) -> 전시(exhibition) 예약 시 Request/Response 로 구현이 하였고, 예약 요청이 과도할 경우 circuit breaker 를 통하여 장애격리.
- Hystrix 설정: 요청처리 쓰레드에서 처리시간이 610 밀리초가 넘어서기 시작하여 어느정도 유지되면 circuit breaker 수행됨
  ![hystix 선언](https://user-images.githubusercontent.com/86943781/126892940-fdb18554-e07a-4d97-b3c6-ec01d90162f3.png)

- 피호출 서비스(휴양소:resort) 의 임의 부하 처리 - 400 밀리초 ~ 620밀리초의 지연시간 부여
  ![delay](https://user-images.githubusercontent.com/86943781/126892958-6000010b-3f36-4351-abe6-ec5ceb4b45f8.png)


* 부하테스터 siege 툴을 통한 서킷 브레이커 동작 확인:
- 동시사용자 5명
- 10초 동안 실시

```
siege -c5 -t10S -v --content-type "application/json" 'http://localhost:8088/reservations/ POST {"exhibitionId":2, "memberName":"seokwon"}'

```
- siege 수행 결과

![break1](https://user-images.githubusercontent.com/86943781/126893053-c1c7f6da-b01f-4f87-b267-62bfac98eec1.png)
![break2](https://user-images.githubusercontent.com/86943781/126893057-6daf99d1-1d6e-4ed6-ac69-75d3a7169324.png)


## 오토스케일 아웃
- 앞서 CB 는 시스템을 안정되게 운영할 수 있게 해줬지만 사용자의 요청을 100% 받아들여주지 못했기 때문에 이에 대한 보완책으로 자동화된 확장 기능을 적용하고자 한다. 
- 리조트서비스에 대한 replica 를 동적으로 늘려주도록 HPA 를 설정한다. 설정은 CPU 사용량이 20프로를 넘어서면 replica 를 10개까지 늘려준다:
```bash
kubectl autoscale deployment resort --cpu-percent=20 --min=1 --max=10
```
- CB 에서 했던 방식대로 워크로드를 100초 동안 걸어준다.
```bash
siege -c20 -t100S -v http://resort:8080/resorts 
```
<img width="533" alt="image" src="https://user-images.githubusercontent.com/85722851/125200066-20ef4e00-e2a4-11eb-893e-7407615daa18.png">

- 오토스케일이 어떻게 되고 있는지 모니터링을 해보면 어느정도 시간이 흐른 후 스케일 아웃이 벌어지는 것을 확인할 수 있다:
<img width="704" alt="image" src="https://user-images.githubusercontent.com/85722851/125234907-926ae300-e31c-11eb-8be4-377f595f9a24.png">


## Zero-Downtime deploy (Readiness Probe)
- 먼저 무정지 재배포가 100% 되는 것인지 확인하기 위해서 Autoscaler 이나 CB 설정을 제거하고 테스트함
- seige로 배포중에 부하를 발생과 재배포 실행
```bash
root@siege:/# siege -c1 -t30S -v http://resort:8080/resorts 
kubectl apply -f  kubernetes/deployment.yml 
```
- seige 의 화면으로 넘어가서 Availability 가 100% 미만으로 떨어졌는지 확인

<img width="552" alt="image" src="https://user-images.githubusercontent.com/85722851/125045082-922dd600-e0d7-11eb-9128-4c9eff39654c.png">
배포기간중 Availability 가 평소 100%에서 80% 대로 떨어지는 것을 확인. 원인은 쿠버네티스가 성급하게 새로 올려진 서비스를 READY 상태로 인식하여 서비스 유입을 진행한 것이기 때문. 

- 이를 막기위해 Readiness Probe 를 설정함: deployment.yaml 의 readiness probe 추가
```yml
          readinessProbe:
            httpGet:
              path: '/actuator/health'
              port: 8080
            initialDelaySeconds: 10
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 10
```

- 동일한 시나리오로 재배포 한 후 Availability 확인
<img width="503" alt="image" src="https://user-images.githubusercontent.com/85722851/125044747-3cf1c480-e0d7-11eb-9c35-1091547bb099.png">
배포기간 동안 Availability 가 100%를 유지하기 때문에 무정지 재배포가 성공한 것으로 확인됨.

## Self-healing (Liveness Probe)
- Pod는 정상적으로 작동하지만 내부의 어플리케이션이 반응이 없다면, 컨테이너는 의미가 없다.
- 위와 같은 경우는 어플리케이션의 Liveness probe는 Pod의 상태를 체크하다가, Pod의 상태가 비정상인 경우 kubelet을 통해서 재시작한다.
- 임의대로 Liveness probe에서 path를 잘못된 값으로 변경 후, retry 시도 확인
```yml
          livenessProbe:
            httpGet:
              path: '/actuator/fakehealth' <-- path를 잘못된 값으로 변경
              port: 8080
            initialDelaySeconds: 120
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 5
```
- resort Pod가 여러차례 재시작 한것을 확인할 수 있다.
<img width="757" alt="image" src="https://user-images.githubusercontent.com/85722851/125048777-3cf3c380-e0db-11eb-99cd-97c7ebead85f.png">

## ConfigMap 사용
- 시스템별로 또는 운영중에 동적으로 변경 가능성이 있는 설정들을 ConfigMap을 사용하여 관리합니다. Application에서 특정 도메일 URL을 ConfigMap 으로 설정하여 운영/개발등 목적에 맞게 변경가능합니다.
configMap 생성
```bash
kubectl apply -f - <<EOF
apiVersion: v1
kind: ConfigMap
metadata:
  name: resort-cm
data:
    api.resort.url: resort:8080
EOF
```
configmap 생성 후 조회
<img width="881" alt="image" src="https://user-images.githubusercontent.com/85722851/125245232-470c0100-e32b-11eb-9db1-54f35d1b2e4c.png">
deployment.yml 변경
```yml
      containers:
          ...
          env:
            - name: feign.resort.url
              valueFrom:
                configMapKeyRef:
                  name: resort-cm
                  key: api.resort.url
```
ResortService.java내용
```java
@FeignClient(name="resort", url="${feign.resort.url}")
public interface ResortService {

    @RequestMapping(method= RequestMethod.GET, value="/resorts/{id}", consumes = "application/json")
    public Resort getResortStatus(@PathVariable("id") Long id);

}
```
생성된 Pod 상세 내용 확인
<img width="1036" alt="image" src="https://user-images.githubusercontent.com/85722851/125245075-162bcc00-e32b-11eb-80ab-81fa57e774d8.png">

