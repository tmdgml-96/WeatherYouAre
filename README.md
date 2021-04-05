# Real-time weather notification service
> 2020 1학기/2학기 캡스톤 디자인(졸업작품) WeatherYouAre팀의 실시간 기상 알림 애플리케이션과 모듈 개발

기상청보다 좁은 범위의 기상 정보를 제공하는 개인화 맞춤 서비스를 제안합니다.   
기상데이터(온도, 습도, 미세먼지 지수, 자외선 제수, 강수 유무, 누적 강수량, 풍속, LSTM 모델을 이용한 기상 예측 값)는 안드로이드 앱에서 실시간으로 확인할 수 있습니다.   또한 FCM(Firebase Cloud Messaging)으로 사용자가 설정한 조건에 따라 변화된 기상 상황에 대비할 수 있도록 알림을 전송합니다.
|팀원        |   기여부분|
|:------------|:------------------------
|Seunghee Park  |  H/W, Arduino(NodeMCU)와 sensor(GUVA-S12SD, SDS011, DHT22, Water Level Sensor, Non-contact Liquid Level Switch)로 모듈 개발|
|Subin Woo  |  AI, Weather learning model|
|Byoung-Jin Choi  |  S/W, Android App|

## Tech/framework used
- H/W : C, Arduino IDE
- S/W : Java, Android Studio
- AI : LSTM Model
- Data storage: Firebase

## About WeatherYouAre
WeatherYouAre는 평소 간과하기 쉬운 날씨를 사용자에게 알림으로서 **사용자의 일상이 더욱 편해지도록** 노렸했습니다.   
App와 Module을 연동하여 날씨를 실시간으로 알립니다.   
우리는 WeatherYouAre를 설계하고, 개발하고, 테스트하며, **온전히 사용자를 위한 서비스를 만들도록 노력했습니다.**   
   
이는 아래의 문제를 해결할 수 있는 발판이 되었습니다.   
- 건물 밀집도, 녹지 비율 등에 따라 발생하는 지역별 온도차에 대한 문제점을 해결할 수 있다.
- 기상청보다 좁은 범위의 기상 데이터를 측정하여 세부 지역별, 시간별 날씨 정보를 제공할 수 있다.
- 실시간 기상 서비스 구현으로 기상 네트워크를 구축할 수 있다.
- 수집한 기상 환경 데이터를 위치, 시간, 센서 단계로 분류하여 관리하기 때문에 여러 분야(농업, 해양 등)에서 필요한 특정 기상 데이터를 간편하게 제공할 수 있다.

## Features
![image](https://user-images.githubusercontent.com/53897151/113578749-42fe8200-965e-11eb-8119-7f2746711a65.png)   

   우리가 집중한 기술은 아래와 같습니다.
- 사용자 친화적인 GUI
- 실시간 측정 값으로 사용자에게 FCM(Firebase Cloud Messaging) 제공(자동/수동)
- Firebase에 저장된 기상 데이터를 활용한 1시간 단위 기온 예측
- 하루 6시간 기준 누적 강수량 측정 후, 제공

스크린 샷과 코드 예제를 통해 사용 방법을 자세히 설명합니다.

_더 많은 예제와 사용법은 [Wiki][wiki]를 참고하세요._

## How to use?


모든 개발 의존성 설치 방법과 자동 테스트 슈트 실행 방법을 운영체제 별로 작성합니다.

```sh
make install
npm test
```

## 업데이트 내역

* 0.2.1
    * 수정: 문서 업데이트 (모듈 코드 동일)
* 0.2.0
    * 수정: `setDefaultXYZ()` 메서드 제거
    * 추가: `init()` 메서드 추가
* 0.1.1
    * 버그 수정: `baz()` 메서드 호출 시 부팅되지 않는 현상 (@컨트리뷰터 감사합니다!)
* 0.1.0
    * 첫 출시
    * 수정: `foo()` 메서드 네이밍을 `bar()`로 수정
* 0.0.1
    * 작업 진행 중

## reference

## License
MIT © Yourname

