#include <FirebaseESP8266.h>
#include <ESP8266WiFi.h>

#include "TimeLib.h"

#define WIFI_SSID "Hanbat_WLAN_Guest"
#define WIFI_PASSWORD ""

#define FIREBASE_HOST "teamproject-aaa50.firebaseio.com"
#define FIREBASE_AUTH "oCXNk9j7WYl2nCZ33tlArZtV12xzZmONBR3zKfQk"

#define FIREBASE_FCM_SERVER_KEY "AAAAq8kiUiM:APA91bGsydC-Ow-XX71hb3DbELiUWn6Uhsc38piaBFg3UmduIkKENSjkdIqLzLGG_XUFzs8QDiWhrk5RaOtOGmu4BbqjDmxZ0Z5uBMO4HcX5TONRYk2QNo-8taUYm3ny5LFI7xInkD0h"

FirebaseData firebaseData;
FirebaseJson json;

// date
unsigned long lastTime = 0;
int timezone = 3;
int number = 0;
int mtemp = 0, stemp = 0, monTemp = 0, dTemp = 0, htemp = 0;
String mi, se, mon, da;
String date, DateSecond, FcmDate;

String DayReverse(int &tempDay);


// firebase upload path
String path = "/sensorA";
String TokenPath = "/userToken";

// Date Setting count
int Dcnt = 0;


void setup()
{

  Serial.begin(9600);

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


  configTime(9 * 3600, 0, "pool.ntp.org", "time.nist.gov");
  Serial.println("\nWaiting for time");
  while (!time(nullptr))
  {
    Serial.print(".");
    delay(1000);
  }
  Serial.println("");
}

int waterCheck;

void loop()
{

  waterCheck = analogRead(A0); 

  delay(2000);

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

  // 10분마다 데이터 업데이트  && mtemp % 10 == 0
  if (year(t) != 1970)
  {
    if (second(t) == 30 || second(t) == 31 || second(t) == 32 || second(t) == 33 ) {
      Firebase.setString(firebaseData, path + "/rain_check/rainCheck", String(waterCheck));
      //Firebase.setString(firebaseData, "/rain_check/rainCheck", String(level));

      Firebase.setInt(firebaseData, path + "/rain_check/" + date +"/rainCheck", waterCheck);
    } 
  }

  Serial.println("*** Setting ------------------------------------");
  Serial.println("*** Date Count : " + String(Dcnt));
  Serial.println(DateSecond);
  Serial.println();
  Serial.println("*** water : " + String(waterCheck));

  Serial.println();

  //FcmSendMessageCheck();


  if (mtemp >= 0) {

    Dcnt++;

    if (Dcnt > 15) {
      Dcnt = 0;
    }

    delay(2000);

  } else {
    Serial.println();

    Serial.println("........ minute Setting");
    Serial.println();
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
