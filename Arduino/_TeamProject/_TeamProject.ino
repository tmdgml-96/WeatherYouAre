#include <FirebaseESP8266.h>
#include <ESP8266WiFi.h>

#include "TimeLib.h"
#include "DHT.h"
#include "SDS011.h"

#define WIFI_SSID "Hanbat_WLAN_Guest"
#define WIFI_PASSWORD ""

#define FIREBASE_HOST "teamproject-aaa50.firebaseio.com"
#define FIREBASE_AUTH "oCXNk9j7WYl2nCZ33tlArZtV12xzZmONBR3zKfQk"

#define FIREBASE_FCM_SERVER_KEY "AAAAfCH3C6g:APA91bG6fRDJc4dGUp39W0J17Xrp48Z0WndxD4UyG032s6VBj-nY6hcq-L10NX8uBT5LYoJGKL7vPvAK7akptLYopL-sPdzYl_E7GkeFEyVIUxSXYw_RWWBGlC1Oo5Zt-LT2WBm4VqrD"

#define DHTPIN D1 // D6
#define DHTTYPE DHT22

FirebaseData firebaseData;
FirebaseJson json;

DHT dht(DHTPIN, DHTTYPE);
SDS011 my_sds;

// date
unsigned long lastTime = 0;
int timezone = 3;
int number = 0;
int mtemp = 0, stemp = 0, monTemp = 0, dTemp = 0, htemp = 0;
String mi, se, mon, da;
String date, DateSecond, FcmDate;

String DayReverse(int &tempDay);

// SDS011
float p10, p25;
// SDS011 - 스위치 FCM
int p10_30, p10_31, p10_81, p10_151;

// DHT
float h, tp = 0.0;
int dst = 0;

void dhtRead();

// UV
int UVsensorIn = A0; //Output from the sensor
int uvLevel;
float outputVoltage;
float uvIntensity;
// UV - 스위치 FCM
int uv_2, uv_5, uv_7, uv_10, uv_11;

// Rain
String rainCheck;
int rain;

int Liquid_level1;
int Liquid_level2;
int Liquid_level3;
int Liquid_level4;

int rainCheckCnt;
int rainStartHour;
int rainStoreCheckHour;
int rainHour = 0;

// WIND_SPEED
int flow_sensor = D0;
float flow_blow;
float blow;

// firebase upload path
String path = "/sensorA";
String TokenPath = "/userToken";

// Get User Token
String UserToken;

// User imformation get
int UserInt1, UserInt2, UserInt3;
String UserValue1, UserValue2, UserValue3;
String UserChoice1, UserChoice2, UserChoice3, UserCheckSensor1, UserCheckSensor2, UserCheckSensor3; // UserChoice = 이상/이하 &  UserCheckSensor = 센서명
String userEmail;

char cTempData1[3], cTempData2[4], cTempData3[3], rainCheckData[5];

String uvOnOff, rainOnOff, dustOnOff;

// Date Setting count
int Dcnt = 0;

void FcmSendMessageCheck();

void setup()
{

  Serial.begin(9600);

  my_sds.begin(2, 6); // D4 + 3v + GND
  dht.begin();

  pinMode(D2, INPUT);
  pinMode(D3, INPUT);
  pinMode(D6, INPUT);
  pinMode(D7, INPUT);


  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);

  firebaseData.setBSSLBufferSize(1024, 1024);
  firebaseData.setResponseSize(1024);

  //UserToken = GetToken(TokenPath);
  //Serial.print("Token : ");
  //Serial.println(UserToken);

  firebaseData.fcm.begin(FIREBASE_FCM_SERVER_KEY);
  //firebaseData.fcm.addDeviceToken(UserToken);
  firebaseData.fcm.setPriority("high");
  firebaseData.fcm.setTimeToLive(1000);

  configTime(9 * 3600, 0, "pool.ntp.org", "time.nist.gov");
  Serial.println("\nWaiting for time");
  while (!time(nullptr))
  {
    Serial.print(".");
    delay(1000);
  }
  Serial.println("");

  FcmSendMessageCheck();
}


void loop()
{

  UserToken = GetToken(TokenPath);
  Serial.print("Token : ");
  Serial.println(UserToken);

  firebaseData.fcm.addDeviceToken(UserToken);
  //FcmSendMessageCheck();

  dhtRead();
  my_sds.read(&p25, &p10);

  Liquid_level1 = digitalRead(D2);
  Liquid_level2 = digitalRead(D3);
  Liquid_level3 = digitalRead(D6);
  Liquid_level4 = digitalRead(D7);

  uvLevel = averageAnalogRead(UVsensorIn);
  outputVoltage = 3.3 * uvLevel / 1024;
  uvIntensity = mapfloat(outputVoltage, 0.99, 2.9, 0.0, 15.0);

  flow_blow = flow_sensor / 1023.0 * 5.0; //아두이노는 10bit ADC므로 0~1023로 표시되므로 0~5범위로 매핑합니다.

  if (0.4 <= flow_blow) {                  //0m/s일 때 전압이 0.4v므로 정의역(x)의 범위를 0.4이상으로 제한합니다.
    blow = flow_blow * 20.25 - 8.10; //위에서 구한 공식을 대입합니다.
  }
  else {   //전압이 0.4V 미만이면
    blow = 0.0; //Serial.println("0.0m/s");               //0.0m/s로 출력합니다.(풍속이 음수(-)로 나오면 안 되므로)
  }

  // time Setting ------------------------
  time_t t = time(nullptr);

  mtemp = minute(t);
  mi = DayReverse(mtemp);

  stemp = second(t);
  se = DayReverse(stemp);

  monTemp = month(t);
  mon = DayReverse(monTemp);

  dTemp = day(t);
  da = DayReverse(dTemp);

  htemp = hour(t);

  DateSecond = String(year(t)) + "-" + mon + "-" + da + " " + hour(t) + ":" + minute(t) + ":" + second(t);
  date = String(year(t)) + "-" + mon + "-" + da + " " + hour(t) + ":" + mi + ":" + se;
  FcmDate = String(hour(t)) + "시 " + mi + "분";

  if (year(t) == 1970) {
    Serial.println("Year Setting ------------------");
    delay(2000);
  }

  // 10분마다 데이터 업데이트 && mtemp % 10 == 0
  if (year(t) != 1970 )
  {

    Serial.println("*** Setting Time : 10 min ------------------------------------");
    Serial.println("*** Date Count : " + String(Dcnt));
    Serial.println(DateSecond);
    Serial.println();
    Serial.println("*** Humidity : " + String(h));
    Serial.println("*** Temperature : " + String(tp));
    Serial.println("*** P10 : " + String(p10));
    Serial.println("*** UV Intensity: " + String(uvIntensity) + " mW/cm^2");
    Serial.println("*** 접촉 수위 센서 전류 : " + String(rain));

    Serial.print("*** 비접촉 Liquid_level1 = ");
    Serial.println(Liquid_level1, DEC);

    Serial.print("*** 비접촉 Liquid_level2 = ");
    Serial.println(Liquid_level2, DEC);

    Serial.print("*** 비접촉 Liquid_level3 = ");
    Serial.println(Liquid_level3, DEC);

    Serial.print("*** 비접촉 Liquid_level4 = ");
    Serial.println(Liquid_level4, DEC);
    Serial.println();

    if (second(t) == 30 | second(t) == 31 || second(t) == 32 || second(t) == 33 || second(t) == 34 || second(t) == 35) {

      if (Firebase.setString(firebaseData, path + "/weather/" + date, date))
      {
        if (rain >= 100) {
          Serial.println( "   접촉 수위 센서 전류 100 이상" );
          Serial.println();

          rainCheckCnt++;
          Serial.print(" ~~~~ rainCheckCnt : ");
          Serial.println(rainCheckCnt);

          if (rainOnOff == "on") {
            Serial.println();
            Serial.print(" * rainOnOff IS : ON");
            Serial.println();
            if (rainCheckCnt == 1 ) {
              firebaseData.fcm.setNotifyMessage(FcmDate, "☔비가 오고 있으니 우산을 챙기세요.");
              FcmSendMessageCheck();
              rainCheckCnt++;
            }
            rainStartHour = hour(t);
            Serial.println("rainStartHour  :: ");

            Serial.println(rainStartHour);

            rainHour++;

            if (rainHour == 2) {
              rainStoreCheckHour = rainStartHour + 6;
              Serial.print("rainStoreCheckHour  ::  ");

              Serial.println(rainStoreCheckHour);

            }

          } else if (rainOnOff == "off" || rainOnOff == "") {
            rainCheckCnt = 0;
            Serial.println();

            Serial.print(" * rainOnOff IS : OFF");
            Serial.println();
          }

          Firebase.setFloat(firebaseData, path + "/weather/" + date + "/temperature", tp);
          Firebase.setFloat(firebaseData, path + "/weather/" + date + "/humidity", h);
          Firebase.setFloat(firebaseData, path + "/weather/" + date + "/dust", p10);
          Firebase.setFloat(firebaseData, path + "/weather/" + date + "/uvIntensity", uvIntensity);
          Firebase.setFloat(firebaseData , path + "/weather/" +  date + "/wind_speed" , blow);
          Firebase.setFloat(firebaseData , path + "/weather/" +  date + "/rain" , 0.0);

          if (rainStartHour <= rainStoreCheckHour) {

            if (Liquid_level1 > 0 && Liquid_level2 == 0 && Liquid_level3 == 0 && Liquid_level4 == 0) {
              Serial.print("비접촉 Liquid_level = ");
              Serial.println(Liquid_level1, DEC);
              Firebase.setFloat(firebaseData, path + "/weather/" + date + "/temperature", tp);
              Firebase.setFloat(firebaseData, path + "/weather/" + date + "/humidity", h);
              Firebase.setFloat(firebaseData, path + "/weather/" + date + "/dust", p10);
              Firebase.setFloat(firebaseData, path + "/weather/" + date + "/uvIntensity", uvIntensity);
              Firebase.setFloat(firebaseData , path + "/weather/" +  date + "/wind_speed" , blow);
              Firebase.setFloat(firebaseData, path + "/weather/" + date + "/rain", 5.0);

            }
            else if (Liquid_level1 > 0 && Liquid_level2 > 0 && Liquid_level3 == 0 && Liquid_level4 == 0) {
              Serial.print("비접촉 Liquid_level = ");
              Serial.println(Liquid_level2, DEC);

              Firebase.setFloat(firebaseData, path + "/weather/" + date + "/temperature", tp);
              Firebase.setFloat(firebaseData, path + "/weather/" + date + "/humidity", h);
              Firebase.setFloat(firebaseData, path + "/weather/" + date + "/dust", p10);
              Firebase.setFloat(firebaseData, path + "/weather/" + date + "/uvIntensity", uvIntensity);
              Firebase.setFloat(firebaseData, path + "/weather/" +  date + "/wind_speed" , blow);
              Firebase.setFloat(firebaseData, path + "/weather/" + date + "/rain", 10.0);

            }
            else if (Liquid_level1 > 0 && Liquid_level2 > 0 && Liquid_level3 > 0 && Liquid_level4 == 0) {
              Serial.print("비접촉 Liquid_level = ");
              Serial.println(Liquid_level3, DEC);

              Firebase.setFloat(firebaseData, path + "/weather/" + date + "/temperature", tp);
              Firebase.setFloat(firebaseData, path + "/weather/" + date + "/humidity", h);
              Firebase.setFloat(firebaseData, path + "/weather/" + date + "/dust", p10);
              Firebase.setFloat(firebaseData, path + "/weather/" + date + "/uvIntensity", uvIntensity);
              Firebase.setFloat(firebaseData , path + "/weather/" +  date + "/wind_speed" , blow);
              Firebase.setFloat(firebaseData, path + "/weather/" + date + "/rain", 20.0);

            }
            else if (Liquid_level1 > 0 && Liquid_level2 > 0 && Liquid_level3 > 0 && Liquid_level4 > 0) {
              Serial.print("비접촉 Liquid_level = ");
              Serial.println(Liquid_level4, DEC);

              Firebase.setFloat(firebaseData, path + "/weather/" + date + "/temperature", tp);
              Firebase.setFloat(firebaseData, path + "/weather/" + date + "/humidity", h);
              Firebase.setFloat(firebaseData, path + "/weather/" + date + "/dust", p10);
              Firebase.setFloat(firebaseData, path + "/weather/" + date + "/uvIntensity", uvIntensity);
              Firebase.setFloat(firebaseData , path + "/weather/" +  date + "/wind_speed" , blow);
              Firebase.setFloat(firebaseData, path + "/weather/" + date + "/rain", 30.0);

            }

          }
        } else {
          Serial.println();
          Serial.println( "Rain Check Value NULL" );
          Serial.println();

          rainCheckCnt = 0;
          rainHour = 0;

          Firebase.setFloat(firebaseData, path + "/weather/" + date + "/temperature", tp);
          Firebase.setFloat(firebaseData, path + "/weather/" + date + "/humidity", h);
          Firebase.setFloat(firebaseData, path + "/weather/" + date + "/dust", p10);
          Firebase.setFloat(firebaseData, path + "/weather/" + date + "/uvIntensity", uvIntensity);
          Firebase.setFloat(firebaseData , path + "/weather/" +  date + "/wind_speed" , blow);
          Firebase.setFloat(firebaseData , path + "/weather/" +  date + "/rain" , 0.0);

        }
      }
    }
  } else {
    Serial.println();

    Serial.println("........ 1970 and minute Setting");

    Serial.println(DateSecond);
    Serial.println();
    delay(3000);
  }

  // Rain Check
  if (Firebase.getString(firebaseData, "/sensorA/rain_check/rainCheck"))
  {
    if (firebaseData.dataType() == "string")
      rainCheck = (firebaseData.stringData());
  }
  Serial.print("Rain Check : ");

  rainCheck.toCharArray(rainCheckData, 5);

  rain = atoi(rainCheckData);
  Serial.println(rain);
  Serial.println();


  // userEmail
  if (Firebase.getString(firebaseData, "/userEmail"))
  {
    if (firebaseData.dataType() == "string")
      userEmail = (firebaseData.stringData());
  }
  Serial.println();
  Serial.print("userEmail : ");
  Serial.print(userEmail);
  Serial.println();

  // user 설정 1번
  if (Firebase.getString(firebaseData, "/user/" + userEmail + "/1/센서 종류"))
  {

    if (firebaseData.dataType() == "string")
    {
      UserCheckSensor1 = (firebaseData.stringData());
    }
    Serial.print("UserCheckSensor 1 : ");
    Serial.print(UserCheckSensor1);
    Serial.println();


    // User check Value
    if (Firebase.getString(firebaseData, "/user/" + userEmail + "/1/측정 값"))
    {
      if (firebaseData.dataType() == "string")
        UserValue1 = (firebaseData.stringData());
    }
    Serial.print(" 1 - 유저 설정 값 : ");

    // String -> char -> int
    UserValue1.toCharArray(cTempData1, 3);
    UserInt1 = atoi(cTempData1);

    Serial.println(UserInt1);
    Serial.println();


    // User check Choice -> 이상/이하
    if (Firebase.getString(firebaseData, "/user/" + userEmail + "/1/비교 값"))
    {
      if (firebaseData.dataType() == "string")
        UserChoice1 = (firebaseData.stringData());
    }
    Serial.print(" 1 - 이상 / 이하 : ");
    Serial.print(UserChoice1);
    Serial.println();
    Serial.println();

  }

  // user 설정 2번
  if (Firebase.getString(firebaseData, "/user/" + userEmail + "/2/센서 종류"))
  {

    if (firebaseData.dataType() == "string")
    {
      UserCheckSensor2 = (firebaseData.stringData());
    }
    Serial.print("UserCheckSensor 2 : ");
    Serial.print(UserCheckSensor2);
    Serial.println();

    // User check Value
    if (Firebase.getString(firebaseData, "/user/" + userEmail + "/2/측정 값"))
    {
      if (firebaseData.dataType() == "string")
        UserValue2 = (firebaseData.stringData());
    }
    Serial.print(" 2 - 유저 설정 값 : ");

    // String -> char -> int
    UserValue2.toCharArray(cTempData2, 4);
    UserInt2 = atoi(cTempData2);

    Serial.println(UserInt2);

    Serial.println();

    // User check Choice -> 이상/이하
    if (Firebase.getString(firebaseData, "/user/" + userEmail + "/2/비교 값"))
    {
      if (firebaseData.dataType() == "string")
        UserChoice2 = (firebaseData.stringData());
    }
    Serial.print(" 2 - 이상 / 이하 : ");
    Serial.print(UserChoice2);
  }
  Serial.println();
  Serial.println();


  // user 설정 3번
  if (Firebase.getString(firebaseData, "/user/" + userEmail + "/3/센서 종류"))
  {
    if (firebaseData.dataType() == "string")
    {
      UserCheckSensor3 = (firebaseData.stringData());
    }
    Serial.print("UserCheckSensor 3 : ");
    Serial.print(UserCheckSensor3);
    Serial.println();

    // User check Value
    if (Firebase.getString(firebaseData, "/user/" + userEmail + "/3/측정 값"))
    {
      if (firebaseData.dataType() == "string")
        UserValue3 = (firebaseData.stringData());
    }
    Serial.print(" 3 - 유저 설정 값 : ");

    // String -> char -> int
    UserValue3.toCharArray(cTempData3, 3);
    UserInt3 = atoi(cTempData3);

    Serial.println(UserInt3);
    Serial.println();

    // User check Choice -> 이상/이하
    if (Firebase.getString(firebaseData, "/user/" + userEmail + "/3/비교 값"))
    {
      if (firebaseData.dataType() == "string")
        UserChoice3 = (firebaseData.stringData());
    }
    Serial.print(" 3 - 이상 / 이하 : ");
    Serial.print(UserChoice3);
    Serial.println();
    Serial.println();

  }

  //uvOnOff
  if (Firebase.getString(firebaseData, "/user/" + userEmail + "/switch/uv 알림"))
  {
    if (firebaseData.dataType() == "string") {
      uvOnOff = (firebaseData.stringData());
    }

    Serial.print("... uvOnOff State :: ");
    Serial.println(uvOnOff);
    Serial.println();

    if (uvOnOff == "on") {

      Serial.print("  uvOnOff IS  ON");
      Serial.println();

      if ( year(t) != 1970) {
        if (uvIntensity <= 2.0)
        {
          if (uv_2 == 2) {
            firebaseData.fcm.setNotifyMessage(FcmDate, "현재 자외선 지수는 '낮음⛅'입니다.");
            FcmSendMessageCheck();
            uv_2++;

          } else {
            uv_2++;
            uv_5 = 0;
            uv_7 = 0;
            uv_10 = 0;
            uv_11 = 0;
            Serial.println("---------------------------");
            Serial.print("uv_2(자외선 지수 낮음) COUNT : ");
            Serial.println(uv_2);
          }
        }
        else if (uvIntensity >= 3.0 && uvIntensity <= 5.0)
        {
          if (uv_5 == 2) {
            firebaseData.fcm.setNotifyMessage(FcmDate, "현재 자외선 지수는 '보통⛅'입니다.");
            FcmSendMessageCheck();
            uv_5++;

          } else {
            uv_2 = 0;
            uv_5++;
            uv_7 = 0;
            uv_10 = 0;
            uv_11 = 0;
            Serial.println("---------------------------");
            Serial.print("uv_5(자외선 지수 보통) COUNT : ");
            Serial.println(uv_5);
          }
        }
        else if (uvIntensity >= 6.0 && uvIntensity <= 7.0)
        {
          if (uv_7 == 2) {
            firebaseData.fcm.setNotifyMessage(FcmDate, "현재 자외선 지수는 '높음🌞'입니다.");
            FcmSendMessageCheck();
          } else  {
            uv_2 = 0;
            uv_5 = 0;
            uv_7++;
            uv_10 = 0;
            uv_11 = 0;
            Serial.println("---------------------------");
            Serial.print("uv_7(자외선 지수 위험) COUNT : ");
            Serial.println(uv_7);
          }
        }
        else if (uvIntensity >= 8.0 && uvIntensity <= 10.0) {
          if (uv_10 == 2) {
            firebaseData.fcm.setNotifyMessage(FcmDate, "현재 자외선 지수는 '매우 높음🔥'입니다.");
            FcmSendMessageCheck();
            uv_10++;

          } else {
            uv_2 = 0;
            uv_5 = 0;
            uv_7 = 0;
            uv_10++;
            uv_11 = 0;
            Serial.println("---------------------------");
            Serial.print("uv_10(자외선 지수 매우 높음) COUNT : ");
            Serial.println(uv_10);
          }
        } else if (uvIntensity >= 11.0) {
          if (uv_11 == 2) {
            firebaseData.fcm.setNotifyMessage(FcmDate, "현재 자외선 지수는 '위험🔥'입니다.");
            FcmSendMessageCheck();
            uv_11++;

          } else {
            uv_2 = 0;
            uv_5 = 0;
            uv_7 = 0;
            uv_10 = 0;
            uv_11++;
            Serial.println("---------------------------");
            Serial.print("uv_11(자외선 지수 위험) COUNT : ");
            Serial.println(uv_11);
          }
        }
      }
    }
    else if (uvOnOff == "off" || uvOnOff == "") {
      Serial.println("  uvOnOff IS  OFF");
      Serial.println();
      uv_2 = 0;
      uv_5 = 0;
      uv_7 = 0;
      uv_10 = 0;
      uv_11 = 0;
    }
  }

  //rainOnOff
  if (Firebase.getString(firebaseData, "/user/" + userEmail + "/switch/강수 여부 알림"))
  {
    if (firebaseData.dataType() == "string") {
      rainOnOff = (firebaseData.stringData());
    }
  } else if (rainOnOff == "off" || rainOnOff == "") {

    Serial.println("  rainOnOff IS  OFF");
    Serial.println();
  }

  Serial.print("... rainOnOff State :: ");
  Serial.println(rainOnOff);
  Serial.println();

  //dustOnOff
  if (Firebase.getString(firebaseData, "/user/" + userEmail + "/switch/미세먼지 알림"))
  {
    if (firebaseData.dataType() == "string") {
      dustOnOff = (firebaseData.stringData());
    }
    Serial.print("... dustOnOff State :: ");
    Serial.println(dustOnOff);
    Serial.println();

    if (dustOnOff == "on") {
      Serial.print("  dustOnOff IS ON");
      Serial.println();

      if (year(t) != 1970) { // && htemp == 13 && mtemp == 0 && Dcnt == 3
        if (p10 <= 30)
        {
          if (p10_30 == 4) {
            firebaseData.fcm.setNotifyMessage(FcmDate, " 현재 미세먼지는 지수는 '좋음😊'입니다.");
            FcmSendMessageCheck();
            p10_30++;

          } else {
            p10_30++;
            p10_31 = 0;
            p10_81 = 0;
            p10_151 = 0;
            Serial.println("---------------------------");
            Serial.print("p10_30(미세먼지 지수 좋음) COUNT : ");
            Serial.println(p10_30);
          }
        }
        else if (p10 >= 31 && p10 <= 80)
        {
          if (p10_31 == 4) {
            firebaseData.fcm.setNotifyMessage( FcmDate, "현재 미세먼지는 지수는 '보통😞'입니다.");
            FcmSendMessageCheck();
            p10_31++;

          } else {
            p10_31++;
            p10_30 = 0;
            p10_81 = 0;
            p10_151 = 0;
            Serial.println("---------------------------");
            Serial.print("p10_31(미세먼지 지수 보통) COUNT : ");
            Serial.println(p10_31);
          }
        }
        else if (p10 >= 81 && p10 <= 150)
        {
          if (p10_81 == 4) {
            firebaseData.fcm.setNotifyMessage(  FcmDate, "현재 미세먼지는 지수는 '나쁨😷'입니다.");
            FcmSendMessageCheck();
            p10_81++;

          } else {
            p10_81++;
            p10_30 = 0;
            p10_31 = 0;
            p10_151 = 0;
            Serial.println("---------------------------");
            Serial.print("p10_81(미세먼지 지수 나쁨) COUNT : ");
            Serial.println(p10_81);
          }
        }
        else if (p10 >= 151)
        {
          if (p10_151 == 4 ) {
            firebaseData.fcm.setNotifyMessage( FcmDate, "현재 미세먼지는 지수는 '매우 나쁨😡'입니다.");
            FcmSendMessageCheck();
            p10_151++;
          } else {
            p10_151++;
            p10_30 = 0;
            p10_31 = 0;
            p10_81 = 0;
            Serial.println("---------------------------");
            Serial.print("p10_151(미세먼지 지수 매우 나쁨) COUNT : ");
            Serial.println(p10_151);
          }
        }
      }
    }
    else if (dustOnOff == "off" || dustOnOff == "") {
      Serial.println("  dustOnOff IS  OFF ");
      Serial.println();
      p10_151 = 0;
      p10_30 = 0;
      p10_31 = 0;
      p10_81 = 0;
    }
  }

  Firebase.setFloat(firebaseData, path + "/location/latitude", 36.3509);
  Firebase.setFloat(firebaseData, path + "/location/longitude", 127.2990194);

  if (mtemp >= 0) {

    Dcnt++;

    if (Dcnt > 13) {
      Dcnt = 0;
    }

    if (year(t) != 1970)
    {
      if (UserCheckSensor1 != NULL) {
        FcmSendMessage( UserCheckSensor1,  UserChoice1, UserInt1 , tp, p10, uvIntensity, h, htemp, mtemp, Dcnt, 9, 00 ); // 9 0
        FcmSendMessage( UserCheckSensor1,  UserChoice1, UserInt1 , tp, p10, uvIntensity, h, htemp, mtemp, Dcnt, 12, 00);
      }
      if (UserCheckSensor2 != NULL) {
        FcmSendMessage( UserCheckSensor2,  UserChoice2, UserInt2 , tp, p10, uvIntensity, h, htemp, mtemp, Dcnt, 9, 10); // 9 10
        FcmSendMessage( UserCheckSensor2,  UserChoice2, UserInt2 , tp, p10, uvIntensity, h, htemp, mtemp, Dcnt, 12, 10);
      }
      if (UserCheckSensor3 != NULL) {
        FcmSendMessage( UserCheckSensor3,  UserChoice3, UserInt3 , tp, p10, uvIntensity, h, htemp, mtemp, Dcnt, 9, 20); // 9 20
        FcmSendMessage( UserCheckSensor3,  UserChoice3, UserInt3, tp, p10, uvIntensity, h, htemp, mtemp, Dcnt, 12, 20);
      }
    }
  }
}

String DayReverse(int &tempDay)
{

  String resultTemp;

  if (tempDay >= 10)
  {
    resultTemp = String(tempDay);
  }
  else
  {
    resultTemp = "0" + String(tempDay);
  }

  return resultTemp;
}

void dhtRead()
{
  h = dht.readHumidity();
  tp = dht.readTemperature();

  if (isnan(h) || isnan(tp))
  {
    Serial.println();

    Serial.println("Failed to read DHT!");
    return;
  }
}

int averageAnalogRead(int pinToRead)
{
  byte numberOfReadings = 8;
  unsigned int runningValue = 0;

  for (int x = 0; x < numberOfReadings; x++)
    runningValue += analogRead(pinToRead);
  runningValue /= numberOfReadings;

  return (runningValue);
}

float mapfloat(float x, float in_min, float in_max, float out_min, float out_max)
{
  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
}

String GetToken(String path)
{
  if (Firebase.getString(firebaseData, path))
  {
    if (firebaseData.dataType() == "string")
      UserToken = (firebaseData.stringData());
  }
  return (UserToken);
}

void FcmSendMessage(String SensorName, String check, int value, float temp, float dust, float uv, float hu, int hour, int minute, int cnt, int Hvalue, int Mvalue) {

  if ( hour == Hvalue && minute == Mvalue && cnt == 2)
  {
    if (SensorName == "온도" )
    {
      if (check == "이상" && temp >= value)
      {
        Serial.println("사용자 설정 값 : 온도 & 이상");
        Serial.println();

        firebaseData.fcm.setNotifyMessage(FcmDate, "현재 기온은 " + String(temp) + "입니다.");
        FcmSendMessageCheck();
      }
      else if (check == "이하" && temp <= value)
      {
        Serial.println("사용자 설정 값 : 온도 & 이하");
        Serial.println();
        firebaseData.fcm.setNotifyMessage( FcmDate, "현재 기온은b " + String(temp) + "입니다.");
        FcmSendMessageCheck();
      }
    }
    else if (SensorName == "미세먼지")
    {
      if (check == "이상" && dust >= value)
      {
        Serial.println("사용자 설정 값 : 미세먼지 & 이상");
        Serial.println();

        firebaseData.fcm.setNotifyMessage( FcmDate, "현재 미세먼지 지수는 " + String(dust) + "입니다.");
        FcmSendMessageCheck();
      }
      else if (check == "이하" && dust <= value)
      {
        Serial.println("사용자 설정 값 : 미세먼지 & 이하");
        Serial.println();
        firebaseData.fcm.setNotifyMessage( FcmDate, "현재 미세먼지 지수는 " + String(dust) + "입니다.");
        FcmSendMessageCheck();
      }
    } else if (SensorName == "습도") {
      if (check == "이상" && hu >= value)
      {
        Serial.println("사용자 설정 값 : 습도 & 이상");
        Serial.println();
        firebaseData.fcm.setNotifyMessage(FcmDate, "현재 습도는 " + String(hu) + "입니다.");
        FcmSendMessageCheck();
      }
      else if (check == "이하" && hu <= value)
      {
        Serial.println("사용자 설정 값 : 습도 & 이하");
        Serial.println();
        firebaseData.fcm.setNotifyMessage( FcmDate, "현재 습도는 " + String(hu) + "입니다.");
        FcmSendMessageCheck();
      }
    } else if (SensorName == "자외선") {
      if (check == "이상" && uv >= value)
      {
        Serial.println("사용자 설정 값 : 자외선 & 이상");
        Serial.println();
        firebaseData.fcm.setNotifyMessage(FcmDate, "현재 자외선 지수는 " + String(uv) + "입니다.");
        FcmSendMessageCheck();
      }
      else if (check == "이하" && uv <= value)
      {
        Serial.println("사용자 설정 값 : 자외선 & 이하");
        Serial.println();
        firebaseData.fcm.setNotifyMessage( FcmDate, "현재 자외선 지수는 " + String(uv) + "입니다.");
        FcmSendMessageCheck();
      }
    }
  }
  else  {
    Serial.println();
    Serial.println("User Sensor Setting is NULL");
  }
}

void FcmSendMessageCheck() {
  if (Firebase.sendMessage(firebaseData))
  {
    Serial.println("PASSED");
    Serial.println(firebaseData.fcm.getSendResult());
    Serial.println("------------------------------------");
    Serial.println();
  }
  else
  {
    Serial.println("FAILED");
    Serial.println("REASON: " + firebaseData.errorReason());
    Serial.println("------------------------------------");
    Serial.println();
  }
}
