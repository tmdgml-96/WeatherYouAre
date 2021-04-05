# Real-time weather notification service
> 2020 1학기/2학기 캡스톤 디자인(졸업작품) WeatherYouAre팀의 실시간 기상 알림 애플리케이션과 모듈 개발

기상청보다 **좁은 범위의 기상 정보를 제공하는 개인화 맞춤 서비스를 제안**합니다.   

|팀원        |   기여부분|
|:------------|:------------------------
|Seunghee Park  |  H/W, Arduino(NodeMCU)와 sensor(GUVA-S12SD, SDS011, DHT22, Water Level Sensor, Non-contact Liquid Level Switch)로 모듈 개발|
|Subin Woo  |  AI, Weather learning model|
|Byoung-Jin Choi  |  S/W, Android App|

## Tech/framework used
- H/W : C, Arduino IDE
- S/W : Java, Android Studio
- AI : Python, Google Colab, LSTM Model
- Data storage: Firebase

## About WeatherYouAre
WeatherYouAre는 평소 간과하기 쉬운 날씨를 사용자에게 알림으로서 **사용자의 일상이 더욱 편해지도록** 노렸했습니다.   
App와 Module을 연동하여 날씨를 실시간으로 알립니다.   
우리는 WeatherYouAre를 설계하고, 개발하고, 테스트하며, **온전히 사용자를 위한 서비스를 만들도록 노력했습니다.**   
   
이는 아래의 문제를 해결할 수 있는 발판이 되었습니다.   
- 건물 밀집도, 녹지 비율 등에 따라 발생하는 지역별 온도차 문제점을 해결할 수 있다.
- 기상청보다 좁은 범위의 기상 데이터를 측정하여 세부 지역별, 시간별 날씨 정보를 제공할 수 있다.
- 실시간 기상 서비스 구현으로 기상 네트워크를 구축할 수 있다.
- 수집한 기상 환경 데이터를 위치, 시간, 센서 단계로 분류하여 관리하기 때문에 여러 분야(농업, 해양 등)에서 필요한 특정 기상 데이터를 간편하게 제공할 수 있다.

## Features
- 실시간 기상 정보를 이용한 개인화 서비스 구성도
<img width="371" alt="그림1" src="https://user-images.githubusercontent.com/53897151/113578887-7b05c500-965e-11eb-932e-9b730b97bec1.png">

    우리가 집중한 기술은 아래와 같습니다.
    - 사용자 친화적인 GUI
    - 실시간 측정 값으로 사용자에게 FCM(Firebase Cloud Messaging) 제공(자동/수동)
    - Firebase에 저장된 기상 데이터를 활용한 1시간 단위 기온 예측
    - 하루 6시간 기준 누적 강수량 측정 후, 제공


## 어떤 모습으로 개발했냐면..

- 예시 앱/모듈 사진

|Application        |   Module  |
|:------------:|:------------------------:|
|<img src="https://user-images.githubusercontent.com/53897151/113579855-d4babf00-965f-11eb-8c4f-bf05c1c70260.png" width="150"> | <img src="https://user-images.githubusercontent.com/53897151/113579809-c8366680-965f-11eb-9d8f-e0999c20649a.jpg" width="250"> |      

#### "Application은 이렇게 구현했습니다."
<img src="https://user-images.githubusercontent.com/53897151/113580577-e6509680-9660-11eb-9b4f-25b13c2f6960.png" width="750">
<img src="https://user-images.githubusercontent.com/53897151/113580591-ebade100-9660-11eb-8edd-d39c1272282a.png" width="750">
<img src="https://user-images.githubusercontent.com/53897151/113580596-ecdf0e00-9660-11eb-9b1c-8a3a9113812c.png" width="750">
<img src="https://user-images.githubusercontent.com/53897151/113580600-ee103b00-9660-11eb-81d4-686162f47974.png" width="750">   

#### "Module은 이렇게 구현했습니다."
<img src="https://user-images.githubusercontent.com/53897151/113580639-f8cad000-9660-11eb-8747-17efb4d9f041.png" width="750">
<img src="https://user-images.githubusercontent.com/53897151/113580626-f5374900-9660-11eb-84c1-1d9c3c3b25cf.png" width="750">


## 업데이트 내역
* 0.2.1
    * 최종: 문서 업데이트 및 졸업 작품 마무리
* 0.2.0
    * 수정(Arduino): Water Level Sensor, Non-contact Liquid Level Switch 추가 연결
    * 보완(Arduino): 누적 강수량 측정 - NodeMcu 2개 사용 & 실시간 데이터 이용
    * 수정(APP): firebase 실시간 데이터 오류 해결
* 0.1.1
    * 버그 수정(Arduino): firebase library error check 및 수정 (@mobizt 제공 library 사용)
    * 수정(APP): GUI 구성 수정
    * 회의] 아이디어 수정: 사용 제공 기능 회의
* 0.1.0
    * 수정(Arduino): Arduino Sensor 3개 연결(SDS011, DHT22, GUVA-S12SD) 후, firebase에 데이터 저장
    * 회의] 아이디어 수정: 사용 제공 기능 회의
* 0.0.1
    * 작업 진행 중.. 아이디어 구체화와 설계 방법 고안

## reference
    [1] 김성진, 여현 “과수 기상환경 파악을 위한 저가형 아두이노 기반 AWS 모니터링 시스템 설계” 한국통신학회 하계종합학술발표회 논문집, 2017.   
    [2] 조동혁 “로라 통신 기반의 클라우드 시스템을 구축된 스마트팜 플랫폼”, 한국정보통신학회 춘계학술대회 논문집, pp 917∼919, 2019.     
    [3] 허경용, 김광훈 “날씨 정보와 아두이노를 이용한 스마트 알람 시계”, 한국정보통신학회논문지, 제23권, 제8호, pp. 889∼895, 2019.    
    [4] 장현준, 이아론, 박현수, 황경호 “무선 멀티홉 네트워크 기반 실시간 기상환경 모니터링 시스템 개발” 한국통신학회 추계종합학술발표회 논문집, 2019. 


