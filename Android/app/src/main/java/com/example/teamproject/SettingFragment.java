package com.example.teamproject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.Hashtable;

import static android.content.ContentValues.TAG;
import static com.example.teamproject.MainActivity.userEmail;

public class SettingFragment extends Fragment {
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseRef = firebaseDatabase.getReference();
    String equip_kind = "", equip_kind2 = "", equip_kind3 = "";
    String sensor_kind = "", sensor_kind2 = "", sensor_kind3 = "";
    String compare_kind = "", compare_kind2 = "", compare_kind3 = "";
    String weather_value = "", weather_value2 = "", weather_value3 = "";
    //        String savedAlarm_uv, savedAlarm_dust, savedAlarm_rain;
    String alarm_rain, alarm_dust, alarm_uv;
    //    String alarm_rain = "off", alarm_dust = "off", alarm_uv = "off";
    //    int count = 0;
    Hashtable<String, String> hashtable_setting = new Hashtable<String, String>();
    Hashtable<String, String> hashtable_setting2 = new Hashtable<String, String>();
    Hashtable<String, String> hashtable_setting3 = new Hashtable<String, String>();
    private Context context;


    //이메일 주소를 사용자 키 값으로 변환하는 함수
    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = container.getContext();
        View root = inflater.inflate(R.layout.fragment_setting, container, false);

        //스피너 들어있는 레이아웃
        final LinearLayout weatherAlert_setting = (LinearLayout) root.findViewById(R.id.weatherAlert_Setting);
        final LinearLayout weatherAlert_setting2 = (LinearLayout) root.findViewById(R.id.weatherAlert_Setting2);
        final LinearLayout weatherAlert_setting3 = (LinearLayout) root.findViewById(R.id.weatherAlert_Setting3);
        //측정기 스피너
        final Spinner Equip_spinner = (Spinner) root.findViewById(R.id.alertEquip_spinner);
        final Spinner Equip_spinner2 = (Spinner) root.findViewById(R.id.alertEquip_spinner2);
        final Spinner Equip_spinner3 = (Spinner) root.findViewById(R.id.alertEquip_spinner3);
        //측정센서 스피너
        final Spinner Sensor_spinner = (Spinner) root.findViewById(R.id.sensorKind_spinner);
        final Spinner Sensor_spinner2 = (Spinner) root.findViewById(R.id.sensorKind_spinner2);
        final Spinner Sensor_spinner3 = (Spinner) root.findViewById(R.id.sensorKind_spinner3);
        //이상,이하 스피너
        final Spinner Compare_spinner = (Spinner) root.findViewById(R.id.weatherCompare_spinner);
        final Spinner Compare_spinner2 = (Spinner) root.findViewById(R.id.weatherCompare_spinner2);
        final Spinner Compare_spinner3 = (Spinner) root.findViewById(R.id.weatherCompare_spinner3);
        //수치 값 텍스트뷰
        final EditText Weather_value = (EditText) root.findViewById(R.id.weather_value);
        final EditText Weather_value2 = (EditText) root.findViewById(R.id.weather_value2);
        final EditText Weather_value3 = (EditText) root.findViewById(R.id.weather_value3);
        //스위치 버튼
        final Switch switch_rain = (Switch) root.findViewById(R.id.switch_rain);
        final Switch switch_dust = (Switch) root.findViewById(R.id.switch_dust);
        final Switch switch_uv = (Switch) root.findViewById(R.id.switch_uv);


//        Button bt_add_setting = (Button) root.findViewById(R.id.bt_add_setting);
        Button bt_update = (Button) root.findViewById(R.id.bt_update);
        final ImageButton bt_delete = (ImageButton) root.findViewById(R.id.bt_delete);
        final ImageButton bt_delete2 = (ImageButton) root.findViewById(R.id.bt_delete2);
        final ImageButton bt_delete3 = (ImageButton) root.findViewById(R.id.bt_delete3);
        final String userEmail_Key = EncodeString(userEmail);

//        파이어베이스에 저장된 알림 설정 값을 저장하는 배열
        final String[] equip_spinner_data = new String[3];
        final String[] sensor_spinner_data = new String[3];
        final String[] weather_value_data = new String[3];
        final String[] compare_spinner_data = new String[3];

        Equip_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    equip_kind = "";
                } else if (i == 1) {
                    equip_kind = "sensorA";
                } else {
                    equip_kind = "sensorB";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Equip_spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    equip_kind2 = "";
                } else if (i == 1) {
                    equip_kind2 = "sensorA";
                } else {
                    equip_kind2 = "sensorB";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Equip_spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    equip_kind3 = "";
                } else if (i == 1) {
                    equip_kind3 = "sensorA";
                } else {
                    equip_kind3 = "sensorB";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Sensor_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    sensor_kind = "";
                } else if (i == 1) {
                    sensor_kind = "미세먼지";
                } else if (i == 2) {
                    sensor_kind = "온도";
                } else if (i == 3) {
                    sensor_kind = "습도";
                } else if (i == 4) {
                    sensor_kind = "자외선";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Sensor_spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    sensor_kind2 = "";
                } else if (i == 1) {
                    sensor_kind2 = "미세먼지";
                } else if (i == 2) {
                    sensor_kind2 = "온도";
                } else if (i == 3) {
                    sensor_kind2 = "습도";
                } else if (i == 4) {
                    sensor_kind2 = "자외선";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Sensor_spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    sensor_kind3 = "";
                } else if (i == 1) {
                    sensor_kind3 = "미세먼지";
                } else if (i == 2) {
                    sensor_kind3 = "온도";
                } else if (i == 3) {
                    sensor_kind3 = "습도";
                } else if (i == 4) {
                    sensor_kind3 = "자외선";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Compare_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    compare_kind = "";
                } else if (i == 1) {
                    compare_kind = "이상";
                } else {
                    compare_kind = "이하";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Compare_spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    compare_kind2 = "";
                } else if (i == 1) {
                    compare_kind2 = "이상";
                } else {
                    compare_kind2 = "이하";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Compare_spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    compare_kind3 = "";
                } else if (i == 1) {
                    compare_kind3 = "이상";
                } else {
                    compare_kind3 = "이하";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        Log.d(TAG, "처음 알람 값들 : " + alarm_uv + alarm_rain + alarm_dust);
        //스위치 눌렀을 때 설정 값 저장
        switch_rain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    alarm_rain = "on";
                    Log.d(TAG, "강수 여부 알림 수신 : " + alarm_rain);
                } else {
                    alarm_rain = "off";
                    Log.d(TAG, "강수 여부 알림 수신 : " + alarm_rain);
                }
            }
        });
        switch_dust.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    alarm_dust = "on";
                    Log.d(TAG, "미세먼지 알림 수신 : " + alarm_dust);
                } else {
                    alarm_dust = "off";
                    Log.d(TAG, "미세먼지 알림 수신 : " + alarm_dust);
                }
            }
        });
        switch_uv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    alarm_uv = "on";
//                    hashtable_setting.put("자외선 알림",alarm_uv);
//                    hashtable_setting3.put("자외선 알림",alarm_uv);
//                    hashtable_setting2.put("자외선 알림",alarm_uv);
                    Log.d(TAG, "자외선 알림 수신 : " + alarm_uv);
                } else {
                    alarm_uv = "off";
//                    hashtable_setting.put("자외선 알림",alarm_uv);
//                    hashtable_setting2.put("자외선 알림",alarm_uv);
//                    hashtable_setting3.put("자외선 알림",alarm_uv);
//                    hashtable_alarm.put("uv 알림",alarm_uv);
                    Log.d(TAG, "자외선 알림 수신 : " + alarm_uv);
                }
            }
        });


        //업데이트 버튼 클릭 시o
        bt_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weather_value = Weather_value.getText().toString();
                weather_value2 = Weather_value2.getText().toString();
                weather_value3 = Weather_value3.getText().toString();

                if (equip_kind != "" && sensor_kind != "" && weather_value != "" && compare_kind != "") {
                    hashtable_setting.put("측정기 종류", equip_kind);
                    hashtable_setting.put("센서 종류", sensor_kind);
                    hashtable_setting.put("측정 값", weather_value);
                    hashtable_setting.put("비교 값", compare_kind);
//                    hashtable_setting.put("강수 여부 알림", alarm_rain);
//                    hashtable_setting.put("미세먼지 알림", alarm_dust);
//                    hashtable_setting.put("uv 알림", alarm_uv);
                    databaseRef.child("user").child(userEmail_Key).child("1").setValue(hashtable_setting);

                    Log.d(TAG, "측정 설정 값 : " + weather_value);
                    Log.d(TAG, "선택 측정기 :" + equip_kind + ", " + sensor_kind + "가 "
                            + weather_value + " " + compare_kind + "일 경우 알림 업데이트");
                }

                if (equip_kind2 != "" && sensor_kind2 != "" && weather_value2 != "" && compare_kind2 != "") {
                    hashtable_setting2.put("측정기 종류", equip_kind2);
                    hashtable_setting2.put("센서 종류", sensor_kind2);
                    hashtable_setting2.put("측정 값", weather_value2);
                    hashtable_setting2.put("비교 값", compare_kind2);

                    databaseRef.child("user").child(userEmail_Key).child("2").setValue(hashtable_setting2);
                    Toast.makeText(context, "업데이트 성공", Toast.LENGTH_SHORT).show();

                    Log.d(TAG, "선택 측정기 :" + equip_kind2 + ", " + sensor_kind2 + "가 "
                            + weather_value2 + " " + compare_kind2 + "일 경우 알림 업데이트");
                }

                if (equip_kind3 != "" && sensor_kind3 != "" && weather_value3 != "" && compare_kind3 != "") {
                    hashtable_setting3.put("측정기 종류", equip_kind3);
                    hashtable_setting3.put("센서 종류", sensor_kind3);
                    hashtable_setting3.put("측정 값", weather_value3);
                    hashtable_setting3.put("비교 값", compare_kind3);

                    databaseRef.child("user").child(userEmail_Key).child("3").setValue(hashtable_setting3);
                    Toast.makeText(context, "업데이트 성공", Toast.LENGTH_SHORT).show();

                    Log.d(TAG, "선택 측정기 :" + equip_kind3 + ", " + sensor_kind3 + "가 "
                            + weather_value3 + " " + compare_kind3 + "일 경우 알림 업데이트");
                }
                databaseRef.child("user").child(userEmail_Key).child("switch").child("강수 여부 알림").setValue(alarm_rain);
                databaseRef.child("user").child(userEmail_Key).child("switch").child("미세먼지 알림").setValue(alarm_dust);
                databaseRef.child("user").child(userEmail_Key).child("switch").child("uv 알림").setValue(alarm_uv);
                Log.d(TAG, "강수 유무 업로드 :" + alarm_rain + ", 미세먼지 업로드 : " + alarm_dust + ", 자외선 업로드 : " + alarm_uv);

            }
        });


//        알림 버튼 추가 시
//        bt_add_setting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                count++;
//                if (count == 1)
//                    weatherAlert_setting2.setVisibility(View.VISIBLE);
//
//                if (count == 2)
//                    weatherAlert_setting3.setVisibility(View.VISIBLE);
//
//            }
//        });

        //사용자 설정 값 가져오기

        //사용자 키 값 확인하고 가져오기

        //사용자 정보 확인, 설정 값 가져오기
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("user").child(userEmail_Key) != null) {
                    int i = 0;
                    for (DataSnapshot ds : dataSnapshot.child("user").child(userEmail_Key).getChildren()) {
                        try {

                            equip_spinner_data[i] = ds.child("측정기 종류").getValue().toString();
                            sensor_spinner_data[i] = ds.child("센서 종류").getValue().toString();
                            weather_value_data[i] = ds.child("측정 값").getValue().toString();
                            compare_spinner_data[i] = ds.child("비교 값").getValue().toString();
//                            alarm_rain =  ds.child("강수 여부 알림").getValue().toString();
//                            alarm_dust = ds.child("미세먼지 알림").getValue().toString();
//                            alarm_uv = ds.child("자외선 알림").getValue().toString();

                            Log.d(TAG, "알림 설정된 측정기 설정 값 " + i + " : " + equip_spinner_data[i]);
                            Log.d(TAG, "알림 설정된 센서 설정 값 " + i + " : " + sensor_spinner_data[i]);
                            Log.d(TAG, "알림 설정된 기상측정 값 " + i + " : " + weather_value_data[i]);
                            Log.d(TAG, "알림 설정된 조건 설정 값 " + i + " : " + compare_spinner_data[i]);
//                            Log.d(TAG, "저장된 강수 여부 알림 설정: " + alarm_rain);
//                            Log.d(TAG, "저장된 미세먼지 알림 설정: " + alarm_dust);
//                            Log.d(TAG, "저장된 자외선 알림 설정: " + alarm_uv);

                            if (i == 0) {
                                if (equip_spinner_data[i] != "" || equip_spinner_data[i] != null) {
                                    if (equip_spinner_data[i].equals("sensorA")) {
                                        Equip_spinner.setSelection(1);
                                    } else if (equip_spinner_data[i].equals("sensorB")) {
                                        Equip_spinner.setSelection(2);
                                    } else {
                                        Equip_spinner.setSelection(0);
                                    }

                                    if (sensor_spinner_data[i].equals("미세먼지")) {
                                        Sensor_spinner.setSelection(1);
                                    } else if (sensor_spinner_data[i].equals("온도")) {
                                        Sensor_spinner.setSelection(2);
                                    } else if (sensor_spinner_data[i].equals("습도")) {
                                        Sensor_spinner.setSelection(3);
                                    } else if (sensor_spinner_data[i].equals("자외선")) {
                                        Sensor_spinner.setSelection(4);
                                    } else {
                                        Sensor_spinner.setSelection(0);
                                    }

                                    if (compare_spinner_data[i].equals("이상")) {
                                        Compare_spinner.setSelection(1);
                                    } else if (compare_spinner_data[i].equals("이하")) {
                                        Compare_spinner.setSelection(2);
                                    } else {
                                        Compare_spinner.setSelection(0);
                                    }

                                    Weather_value.setText(weather_value_data[i]);
                                }
                            } else if (i == 1) {
                                if (equip_spinner_data[i] != "" || equip_spinner_data[i] != null) {

                                    if (equip_spinner_data[i].equals("sensorA")) {
                                        Equip_spinner2.setSelection(1);
                                    } else if (equip_spinner_data[i].equals("sensorB")) {
                                        Equip_spinner2.setSelection(2);
                                    } else {
                                        Equip_spinner2.setSelection(0);

                                    }

                                    if (sensor_spinner_data[i].equals("미세먼지")) {
                                        Sensor_spinner2.setSelection(1);
                                    } else if (sensor_spinner_data[i].equals("온도")) {
                                        Sensor_spinner2.setSelection(2);
                                    } else if (sensor_spinner_data[i].equals("습도")) {
                                        Sensor_spinner2.setSelection(3);
                                    } else if (sensor_spinner_data[i].equals("자외선")) {
                                        Sensor_spinner2.setSelection(4);
                                    } else {
                                        Sensor_spinner2.setSelection(0);
                                    }

                                    if (compare_spinner_data[i].equals("이상")) {
                                        Compare_spinner2.setSelection(1);
                                    } else if (compare_spinner_data[i].equals("이하")) {
                                        Compare_spinner2.setSelection(2);
                                    } else {
                                        Compare_spinner2.setSelection(0);

                                    }

                                    Weather_value2.setText(weather_value_data[i]);
                                }
                            } else if (i == 2) {
                                if (equip_spinner_data[i] != "" || equip_spinner_data[i] != null) {

                                    if (equip_spinner_data[i].equals("sensorA")) {
                                        Equip_spinner3.setSelection(1);
                                    } else if (equip_spinner_data[i].equals("sensorB")) {
                                        Equip_spinner3.setSelection(2);
                                    } else {
                                        Equip_spinner3.setSelection(0);
                                    }

                                    if (sensor_spinner_data[i].equals("미세먼지")) {
                                        Sensor_spinner3.setSelection(1);
                                    } else if (sensor_spinner_data[i].equals("온도")) {
                                        Sensor_spinner3.setSelection(2);
                                    } else if (sensor_spinner_data[i].equals("습도")) {
                                        Sensor_spinner3.setSelection(3);
                                    } else if (sensor_spinner_data[i].equals("자외선")) {
                                        Sensor_spinner3.setSelection(4);
                                    } else {
                                        Sensor_spinner3.setSelection(0);
                                    }

                                    if (compare_spinner_data[i].equals("이상")) {
                                        Compare_spinner3.setSelection(1);
                                    } else if (compare_spinner_data[i].equals("이하")) {
                                        Compare_spinner3.setSelection(2);
                                    } else {
                                        Compare_spinner3.setSelection(0);
                                    }

                                    Weather_value3.setText(weather_value_data[i]);
                                }
                            }


//
                            i++;
                        } catch (Exception e) {
                        }
                        String savedAlarm_uv = dataSnapshot.child("user").child(userEmail_Key).child("switch").child("uv 알림").getValue(String.class);
                        String savedAlarm_rain = dataSnapshot.child("user").child(userEmail_Key).child("switch").child("강수 여부 알림").getValue(String.class);
                        String savedAlarm_dust = dataSnapshot.child("user").child(userEmail_Key).child("switch").child("미세먼지 알림").getValue(String.class);
                        Log.d(TAG, "가져오자아아~~ : " + savedAlarm_uv + ", " + savedAlarm_rain + ", " + savedAlarm_dust);

                        if (savedAlarm_rain.equals("on")) {
                            Log.d(TAG, "저장>>alarm_rain if문 실행  :");
                            alarm_rain = "on";
                            switch_rain.setChecked(true);
                            Log.d(TAG, "저장>>alarm_rain :" + alarm_rain);
                        } else {
                            alarm_rain = "off";
                            switch_rain.setChecked(false);

                            Log.d(TAG, "저장>>alarm_rain :" + alarm_rain);
                        }

                        if (savedAlarm_dust.equals("on")) {
                            alarm_dust = "on";
                            switch_dust.setChecked(true);
                            Log.d(TAG, "저장>>alarm_dust :" + alarm_dust);
                        } else {
                            alarm_dust = "off";
                            switch_dust.setChecked(false);
                            Log.d(TAG, "저장>>alarm_dust :" + alarm_dust);
                        }

                        if (savedAlarm_uv.equals("on")) {
                            alarm_uv = "on";
                            switch_uv.setChecked(true);
                            Log.d(TAG, "저장>>alarm_uv :" + alarm_uv);
                        } else {
                            alarm_uv = "off";
                            switch_uv.setChecked(false);
                            Log.d(TAG, "저장>>alarm_uv :" + alarm_uv);
                        }

                    }//DataSnapShot for문 끝


                    bt_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Equip_spinner.setSelection(0);
                            Sensor_spinner.setSelection(0);
                            Compare_spinner.setSelection(0);
                            Weather_value.setText("");

//                        equip_spinner_data[0] = "";
//                        weather_value_data[0] = "";
//                        compare_spinner_data[0] = "";
//                        weather_value_data[0] = "";

                            hashtable_setting.put("측정기 종류", "");
                            hashtable_setting.put("센서 종류", "");
                            hashtable_setting.put("측정 값", "");
                            hashtable_setting.put("비교 값", "");

                            databaseRef.child("user").child(userEmail_Key).child("1").setValue(hashtable_setting);

                        }
                    });

                    bt_delete2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Equip_spinner2.setSelection(0);
                            Sensor_spinner2.setSelection(0);
                            Compare_spinner2.setSelection(0);
                            Weather_value2.setText("");

//                            equip_spinner_data[1] = "";
//                            weather_value_data[1] = "";
//                            compare_spinner_data[1] = "";
//                            weather_value_data[1] = "";

                            hashtable_setting2.put("측정기 종류", "");
                            hashtable_setting2.put("센서 종류", "");
                            hashtable_setting2.put("측정 값", "");
                            hashtable_setting2.put("비교 값", "");

                            databaseRef.child("user").child(userEmail_Key).child("2").setValue(hashtable_setting2);
//                            weatherAlert_setting2.setVisibility(INVISIBLE);
                        }
                    });

                    bt_delete3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Equip_spinner3.setSelection(0);
                            Sensor_spinner3.setSelection(0);
                            Compare_spinner3.setSelection(0);
                            Weather_value3.setText("");

//                            equip_spinner_data[2] = "";
//                            weather_value_data[2] = "";
//                            compare_spinner_data[2] = "";
//                            weather_value_data[2] = "";

                            hashtable_setting3.put("측정기 종류", "");
                            hashtable_setting3.put("센서 종류", "");
                            hashtable_setting3.put("측정 값", "");
                            hashtable_setting3.put("비교 값", "");

                            databaseRef.child("user").child(userEmail_Key).child("3").setValue(hashtable_setting3);
//                            weatherAlert_setting3.setVisibility(INVISIBLE);
                        }
                    });


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return root;

    }//onCreateView
}