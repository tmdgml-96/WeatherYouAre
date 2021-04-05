package com.example.teamproject;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import im.dacer.androidcharts.LineView;

import im.dacer.androidcharts.LineView;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private Context context;
    static float A_dustData, B_dustData;
    static float A_tempData, B_tempData;
    static float A_humidData, B_humidData;
    static float A_uvData, B_uvData;
    static float A_raincheck, B_raincheck;
    static float A_forecastData, B_forecastData;
    static ArrayList<String> X_hour;
    static ArrayList<Float> A_forecastArray, B_forecastArray;
    static ArrayList<Float> A_dustArray, B_dustArray;
    static ArrayList<Float> A_tempArray, B_tempArray;
    static ArrayList<Float> A_humidArray, B_humidArray;
    static ArrayList<Float> A_uvArray, B_uvArray;

    static float A_equipLat, B_equipLat;
    static float A_equipLng, B_equipLng;
    static float gpsLat, gpsLng;
    static Location userLocation;

    ArrayList<Float> result;
    TextView dustCondition, tempCondition, uvCondition;
    TextView currnet_location;
    ImageView rainCondition, iv_dust, iv_uv;
    FirebaseDatabase database;
    private LocationManager locationManager;
    private static final int REQUEST_CODE_LOCATION = 2;
    LineView lineView;
//    LineChart lineChart;
//    DecimalFormat form = new DecimalFormat("#.##");


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

//        lineChart = (LineChart) root.findViewById(R.id.graph_line);
        lineView = (LineView) root.findViewById(R.id.line_view);
        context = container.getContext();
        dustCondition = (TextView) root.findViewById(R.id.tv_dust);
        tempCondition = (TextView) root.findViewById(R.id.tv_temperature);
        uvCondition = (TextView) root.findViewById(R.id.tv_uv);
        iv_dust = (ImageView) root.findViewById(R.id.iv_dust);
        iv_uv = (ImageView) root.findViewById(R.id.iv_uv);
        rainCondition = (ImageView) root.findViewById(R.id.iv_rain);
        currnet_location = (TextView) root.findViewById(R.id.tv_location);
        Spinner spinner = (Spinner) root.findViewById(R.id.bt_spinner);
        database = FirebaseDatabase.getInstance(); //데이터베이스 기상정보 읽기
        X_hour = new ArrayList<String>();


        ////// 데이터베이스 기상정보 쓰기
//        Calendar c = Calendar.getInstance();
//        final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        final String datetime = dateformat.format(c.getTime());


        result = new ArrayList<Float>();
//        spinner.setSelection(2);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, final int position, long id) {

                DatabaseReference weatherRef = database.getReference();

                weatherRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (position == 0) {
                            X_hour.clear();
                            A_equipLat = dataSnapshot.child("sensorA").child("location").child("latitude").getValue(float.class);
                            A_equipLng = dataSnapshot.child("sensorA").child("location").child("longitude").getValue(float.class);
                            result.add((float) Math.sqrt(Math.pow(gpsLat - A_equipLat, 2) + Math.pow(gpsLng - A_equipLng, 2)));

                            B_equipLat = dataSnapshot.child("측정기B").child("location").child("latitude").getValue(float.class);
                            B_equipLng = dataSnapshot.child("측정기B").child("location").child("longitude").getValue(float.class);
                            result.add((float) Math.sqrt(Math.pow(gpsLat - B_equipLat, 2) + Math.pow(gpsLng - B_equipLng, 2)));

                            Log.d(TAG, "sensorA와의 거리 : " + result.get(0));
                            Log.d(TAG, "sensorB와의 거리 : " + result.get(1));

                            currnet_location.setText("위도 : " + gpsLat + "  경도 : " + gpsLng);
                            if (result.get(0) < result.get(1)) {
                                Log.d(TAG, "가장 가까운 측정기 : seonsorA");
                                A_dustArray = new ArrayList<Float>();
                                A_tempArray = new ArrayList<Float>();
                                A_humidArray = new ArrayList<Float>();
                                A_uvArray = new ArrayList<Float>();
                                A_forecastArray = new ArrayList<Float>();

                                //예측 데이터 가져오기
                                for (DataSnapshot ds : dataSnapshot.child("sensorA").child("forecast").getChildren()) {
                                    try {
                                        A_forecastData = ds.child("temp").getValue(float.class);
                                        A_forecastArray.add(A_forecastData);

//                                        String X_index = ds.getKey().substring(11, 16);
//                                        X_hour.add(X_index);
//                                        Log.d(TAG, "X축 인덱스 : " + X_hour);
                                        X_hour.clear();
                                        X_hour.add("+0.5H");
                                        X_hour.add("+1H");
                                        X_hour.add("+1.5H");
                                        X_hour.add("+2H");
                                        X_hour.add("+2.5H");
                                        X_hour.add("+3H");
                                        X_hour.add("+3.5H");
                                        X_hour.add("+4H");

                                    } catch (Exception e) {
                                    }
                                }

                                //강수 유무 데이터 가져오기
                                for (DataSnapshot ds : dataSnapshot.child("sensorA").child("rain_check").getChildren()) {
                                    try {
                                        A_raincheck = ds.child("rainCheck").getValue(float.class);
                                    } catch (Exception e) {
                                    }
                                }

                                //기상 데이터 가져오기
                                for (DataSnapshot ds : dataSnapshot.child("sensorA").child("weather").getChildren()) {
                                    try {
                                        A_dustData = ds.child("dust").getValue(float.class);
                                        A_tempData = ds.child("temperature").getValue(float.class);
                                        A_humidData = ds.child("humidity").getValue(float.class);
                                        A_uvData = ds.child("uvIntensity").getValue(float.class);

                                        //리스트에 기상 수치 저장
                                        A_dustArray.add(A_dustData);
                                        A_tempArray.add(A_tempData);
                                        A_humidArray.add(A_humidData);
                                        A_uvArray.add(A_uvData);

                                        Log.d(TAG, "날짜 값:" + ds);

                                    } catch (Exception e) {
                                    }

                                }

//                                Handler delayHandler = new Handler();
//                                delayHandler.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
                                        ArrayList<ArrayList<Float>> dataLists = new ArrayList<>();
                                        dataLists.clear();


                                        dataLists.add(A_forecastArray);
                                        Log.d(TAG, "@@@@@" + dataLists);

                                        lineView.setDrawDotLine(true);
                                        lineView.setShowPopup(LineView.SHOW_POPUPS_All);
                                        lineView.setBottomTextList(X_hour);
                                        lineView.setFloatDataList(dataLists, true);
                                        lineView.setColorArray(new int[]{Color.parseColor("#e74c3c")});
//                                    }
//                                }, 1000);

                                Log.d(TAG, "A측정기의 dustData: " + A_dustData);
                                Log.d(TAG, "dustArray 갯수 : " + A_dustArray.size() + ", tempArray 갯수 : " + A_tempArray.size());
                                Log.d(TAG, "미세먼지 마지막 값 :" + A_dustArray.get(A_dustArray.size() - 1) +
                                        ", 온도 마지막 값 :" + A_tempArray.get(A_tempArray.size() - 1) +
                                        "자외선 마지막 값 :" + A_uvArray.get(A_uvArray.size() - 1)+ "습도 마지막 값 :" + A_humidArray.get(A_humidArray.size() - 1));


                                if (A_raincheck < 100) {
                                    rainCondition.setImageResource(R.drawable.norain);
                                } else {
                                    rainCondition.setImageResource(R.drawable.rain);
                                }

                                //미세먼지 상태
                                if (A_dustData <= 30) {
                                    iv_dust.setImageResource(R.drawable.ic_baseline_blur_on_good);
                                    dustCondition.setText("좋음(0~30)   " + A_dustData + " ㎍/㎥");
                                } else if (30 < A_dustData && A_dustData <= 80) {
                                    iv_dust.setImageResource(R.drawable.ic_baseline_blur_on_24);
                                    dustCondition.setText("보통(30~80) " + A_dustData + " ㎍/㎥");
                                } else {
                                    iv_dust.setImageResource(R.drawable.ic_baseline_blur_on_dangerous);
                                    dustCondition.setText("나쁨(81~ ) " + A_dustData + " ㎍/㎥");
                                }

                                String uv_dataString = Float.toString(A_uvData);
                                if (0 <= A_uvData && A_uvData < 6) {
                                    iv_uv.setImageResource(R.drawable.ic_baseline_brightness_5_24);
                                    uvCondition.setText("보통 (0~5) " + uv_dataString.substring(0, 3) + " ㎼/㎠");
                                } else if (6 <= A_uvData && A_uvData < 10) {
                                    iv_uv.setImageResource(R.drawable.ic_baseline_brightness_hot);
                                    uvCondition.setText("높음 (6~10) " + uv_dataString.substring(0, 3) + " ㎼/㎠");
                                } else {
                                    iv_uv.setImageResource(R.drawable.ic_baseline_brightness_dangerous);
                                    uvCondition.setText("나쁨 (6~10) " + uv_dataString.substring(0, 3) + " ㎼/㎠");
                                }


                                tempCondition.setText(A_tempData + " ℃  "+ A_humidData+"%");


                            } else {

                                Log.d(TAG, "가장 가까운 측정기 : seonsorB");

                                B_dustArray = new ArrayList<Float>();
                                B_tempArray = new ArrayList<Float>();
                                B_humidArray = new ArrayList<Float>();
                                B_uvArray = new ArrayList<Float>();
                                B_forecastArray = new ArrayList<Float>();

                                for (DataSnapshot ds : dataSnapshot.child("측정기B").child("forecast").getChildren()) {
                                    try {
                                        B_forecastData = ds.child("temp").getValue(float.class);
                                        B_forecastArray.add(B_forecastData);
                                        X_hour.clear();
//                                        String X_index = ds.getKey().substring(11, 16);
//                                        X_hour.add(X_index);
//                                        Log.d(TAG, "X축 인덱스 : " + X_hour);
                                        X_hour.add("+0.5H");
                                        X_hour.add("+1H");
                                        X_hour.add("+1.5H");
                                        X_hour.add("+2H");
                                        X_hour.add("+2.5H");
                                        X_hour.add("+3H");
                                        X_hour.add("+3.5H");
                                        X_hour.add("+4H");

                                    } catch (Exception e) {
                                    }
                                }


                                for (DataSnapshot ds : dataSnapshot.child("측정기B").child("rain_check").getChildren()) {
                                    try {
                                        B_raincheck = ds.child("rainCheck").getValue(float.class);
                                    } catch (Exception e) {
                                    }
                                }

                                for (DataSnapshot ds : dataSnapshot.child("측정기B").child("weather").getChildren()) {
                                    try {
                                        B_dustData = ds.child("dust").getValue(float.class);
                                        B_tempData = ds.child("temperature").getValue(float.class);
                                        B_humidData = ds.child("humidity").getValue(float.class);
                                        B_uvData = ds.child("uvIntensity").getValue(float.class);

                                        //리스트에 기상 수치 저장
                                        B_dustArray.add(B_dustData);
                                        B_tempArray.add(B_tempData);
                                        B_humidArray.add(B_humidData);
                                        B_uvArray.add(B_uvData);
                                        Log.d(TAG, "날짜 값:" + ds);
                                    } catch (Exception e) {
                                    }
                                }


//                                Handler delayHandler = new Handler();
//                                delayHandler.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {

                                        ArrayList<ArrayList<Float>> dataLists = new ArrayList<>();
                                        dataLists.clear();


                                        dataLists.add(B_forecastArray);

                                        lineView.setDrawDotLine(true);
                                        lineView.setShowPopup(LineView.SHOW_POPUPS_All);
                                        lineView.setBottomTextList(X_hour);
                                        lineView.setFloatDataList(dataLists, true);
                                        lineView.setColorArray(new int[]{Color.parseColor("#e74c3c")});
//                                    }
//                                }, 1000);


                                Log.d(TAG, "B측정기의 dustData: " + B_dustData);
                                Log.d(TAG, "dustArray 갯수 : " + B_dustArray.size() + ", tempArray 갯수 : " + B_tempArray.size());
                                Log.d(TAG, "미세먼지 마지막 값 :" + B_dustArray.get(B_dustArray.size() - 1) +
                                        ", 온도 마지막 값 :" + B_tempArray.get(B_tempArray.size() - 1) +
                                        "자외선 마지막 값 :" + B_uvArray.get(B_uvArray.size() - 1)+"습도 마지막 값 :" + B_humidArray.get(B_humidArray.size() - 1));

                                if (B_raincheck < 100) {
                                    rainCondition.setImageResource(R.drawable.norain);
                                } else {
                                    rainCondition.setImageResource(R.drawable.rain);
                                }


                                //미세먼지 상태
                                if (B_dustData <= 30) {
                                    iv_dust.setImageResource(R.drawable.ic_baseline_blur_on_good);
                                    dustCondition.setText("좋음(0~30)   " + B_dustData + " ㎍/㎥");
                                } else if (30 < B_dustData && B_dustData <= 80) {
                                    iv_dust.setImageResource(R.drawable.ic_baseline_blur_on_24);
                                    dustCondition.setText("보통(30~80) " + B_dustData + " ㎍/㎥");
                                } else {
                                    iv_dust.setImageResource(R.drawable.ic_baseline_blur_on_dangerous);
                                    dustCondition.setText("나쁨(81~ ) " + B_dustData + " ㎍/㎥");
                                }

                                String uv_dataString = Float.toString(B_uvData);
                                if (0 <= B_uvData && B_uvData < 6) {
                                    iv_uv.setImageResource(R.drawable.ic_baseline_brightness_5_24);
                                    uvCondition.setText("보통 (0~5) " + uv_dataString.substring(0, 3) + " ㎼/㎠");
                                } else if (6 <= B_uvData && B_uvData < 10) {
                                    iv_uv.setImageResource(R.drawable.ic_baseline_brightness_hot);
                                    uvCondition.setText("높음 (6~10) " + uv_dataString.substring(0, 3) + " ㎼/㎠");
                                } else {
                                    iv_uv.setImageResource(R.drawable.ic_baseline_brightness_dangerous);
                                    uvCondition.setText("나쁨 (6~10) " + uv_dataString.substring(0, 3) + " ㎼/㎠");
                                }
                                tempCondition.setText(B_tempData + " ℃  "+B_humidData+" %");

                            }//else
                        }
                        if (position == 1) {
                            X_hour.clear();
                            Log.d(TAG, "position1조건 A 들어옴");

                            A_equipLat = dataSnapshot.child("sensorA").child("location").child("latitude").getValue(float.class);
                            A_equipLng = dataSnapshot.child("sensorA").child("location").child("longitude").getValue(float.class);
                            A_dustArray = new ArrayList<Float>();
                            A_humidArray = new ArrayList<Float>();
                            A_tempArray = new ArrayList<Float>();
                            A_uvArray = new ArrayList<Float>();
                            A_forecastArray = new ArrayList<Float>();

                            //예측 데이터 가져오기
                            for (DataSnapshot ds : dataSnapshot.child("sensorA").child("forecast").getChildren()) {
                                try {
                                    A_forecastData = ds.child("temp").getValue(float.class);
                                    A_forecastArray.add(A_forecastData);
                                    X_hour.clear();
//                                    String X_index = ds.getKey().substring(11, 16);
//                                    X_hour.add(X_index);
//                                    Log.d(TAG, "X축 인덱스 : " + X_hour);
                                    X_hour.add("+0.5H");
                                    X_hour.add("+1H");
                                    X_hour.add("+1.5H");
                                    X_hour.add("+2H");
                                    X_hour.add("+2.5H");
                                    X_hour.add("+3H");
                                    X_hour.add("+3.5H");
                                    X_hour.add("+4H");

                                } catch (Exception e) {
                                }
                            }

                            //강수 유무 데이터 가져오기
                            for (DataSnapshot ds : dataSnapshot.child("sensorA").child("rain_check").getChildren()) {
                                try {
                                    A_raincheck = ds.child("rainCheck").getValue(float.class);
                                } catch (Exception e) {
                                }
                            }

                            //기상 데이터 가져오기
                            for (DataSnapshot ds : dataSnapshot.child("sensorA").child("weather").getChildren()) {
                                try {
                                    A_dustData = ds.child("dust").getValue(float.class);
                                    A_tempData = ds.child("temperature").getValue(float.class);
                                    A_humidData = ds.child("humidity").getValue(float.class);
                                    A_uvData = ds.child("uvIntensity").getValue(float.class);

                                    //리스트에 기상 수치 저장
                                    A_dustArray.add(A_dustData);
                                    A_tempArray.add(A_tempData);
                                    A_humidArray.add(A_humidData);
                                    A_uvArray.add(A_uvData);

                                    Log.d(TAG, "날짜 값:" + ds);

                                } catch (Exception e) {
                                }

                            }

//                            Handler delayHandler = new Handler();
//                            delayHandler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
                                    ArrayList<ArrayList<Float>> dataLists = new ArrayList<>();
                                    dataLists.clear();


                                    dataLists.add(A_forecastArray);
                                    Log.d(TAG, "@@@@@" + dataLists);

                                    lineView.setDrawDotLine(true);
                                    lineView.setShowPopup(LineView.SHOW_POPUPS_All);
                                    lineView.setBottomTextList(X_hour);
                                    lineView.setFloatDataList(dataLists, true);
                                    lineView.setColorArray(new int[]{Color.parseColor("#e74c3c")});
//                                }
//                            }, 1000);

                            Log.d(TAG, "A측정기의 dustData: " + A_dustData);
                            Log.d(TAG, "dustArray 갯수 : " + A_dustArray.size() + ", tempArray 갯수 : " + A_tempArray.size());
                            Log.d(TAG, "미세먼지 마지막 값 :" + A_dustArray.get(A_dustArray.size() - 1) +
                                    ", 온도 마지막 값 :" + A_tempArray.get(A_tempArray.size() - 1) +
                                    "자외선 마지막 값 :" + A_uvArray.get(A_uvArray.size() - 1)+"습도 마지막 값 :" + A_humidArray.get(A_humidArray.size() - 1));


                            if (A_raincheck < 100) {
                                rainCondition.setImageResource(R.drawable.norain);
                            } else {
                                rainCondition.setImageResource(R.drawable.rain);
                            }

//미세먼지 상태
                            if (A_dustData <= 30) {
                                iv_dust.setImageResource(R.drawable.ic_baseline_blur_on_good);
                                dustCondition.setText("좋음(0~30)   " + A_dustData + " ㎍/㎥");
                            } else if (30 < A_dustData && A_dustData <= 80) {
                                iv_dust.setImageResource(R.drawable.ic_baseline_blur_on_24);
                                dustCondition.setText("보통(30~80) " + A_dustData + " ㎍/㎥");
                            } else {
                                iv_dust.setImageResource(R.drawable.ic_baseline_blur_on_dangerous);
                                dustCondition.setText("나쁨(81~ ) " + A_dustData + " ㎍/㎥");
                            }

                            String uv_dataString = Float.toString(A_uvData);
                            if (0 <= A_uvData && A_uvData < 6) {
                                iv_uv.setImageResource(R.drawable.ic_baseline_brightness_5_24);
                                uvCondition.setText("보통 (0~5) " + uv_dataString.substring(0, 3) + " ㎼/㎠");
                            } else if (6 <= A_uvData && A_uvData < 10) {
                                iv_uv.setImageResource(R.drawable.ic_baseline_brightness_hot);
                                uvCondition.setText("높음 (6~10) " + uv_dataString.substring(0, 3) + " ㎼/㎠");
                            } else {
                                iv_uv.setImageResource(R.drawable.ic_baseline_brightness_dangerous);
                                uvCondition.setText("나쁨 (6~10) " + uv_dataString.substring(0, 3) + " ㎼/㎠");
                            }
                            tempCondition.setText(A_tempData + " ℃  "+A_humidData+" %");
                        }

                        if (position == 2) {
                            X_hour.clear();
                            Log.d(TAG, "position2조건 B 들어옴");
                            B_dustArray = new ArrayList<Float>();
                            B_tempArray = new ArrayList<Float>();
                            B_humidArray = new ArrayList<Float>();
                            B_uvArray = new ArrayList<Float>();
                            B_forecastArray = new ArrayList<Float>();

                            for (DataSnapshot ds : dataSnapshot.child("측정기B").child("forecast").getChildren()) {
                                try {
                                    B_forecastData = ds.child("temp").getValue(float.class);
                                    B_forecastArray.add(B_forecastData);

//                                    String X_index = ds.getKey().substring(11, 16);
//                                    X_hour.add(X_index);
//                                    Log.d(TAG, "X축 인덱스 : " + X_hour);
                                    X_hour.clear();
                                    X_hour.add("+0.5H");
                                    X_hour.add("+1H");
                                    X_hour.add("+1.5H");
                                    X_hour.add("+2H");
                                    X_hour.add("+2.5H");
                                    X_hour.add("+3H");
                                    X_hour.add("+3.5H");
                                    X_hour.add("+4H");

                                } catch (Exception e) {
                                }
                            }


                            for (DataSnapshot ds : dataSnapshot.child("측정기B").child("rain_check").getChildren()) {
                                try {
                                    B_raincheck = ds.child("rainCheck").getValue(float.class);
                                } catch (Exception e) {
                                }
                            }

                            for (DataSnapshot ds : dataSnapshot.child("측정기B").child("weather").getChildren()) {
                                try {
                                    B_dustData = ds.child("dust").getValue(float.class);
                                    B_tempData = ds.child("temperature").getValue(float.class);
                                    B_humidData = ds.child("humidity").getValue(float.class);
                                    B_uvData = ds.child("uvIntensity").getValue(float.class);

                                    //리스트에 기상 수치 저장
                                    B_dustArray.add(B_dustData);
                                    B_tempArray.add(B_tempData);
                                    B_humidArray.add(B_humidData);
                                    B_uvArray.add(B_uvData);
                                    Log.d(TAG, "날짜 값:" + ds);
                                } catch (Exception e) {
                                }
                            }


//                            Handler delayHandler = new Handler();
//                            delayHandler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {

                                    ArrayList<ArrayList<Float>> dataLists = new ArrayList<>();
                                    dataLists.clear();


                                    dataLists.add(B_forecastArray);

                                    lineView.setDrawDotLine(true);
                                    lineView.setShowPopup(LineView.SHOW_POPUPS_All);
                                    lineView.setBottomTextList(X_hour);
                                    lineView.setFloatDataList(dataLists, true);
                                    lineView.setColorArray(new int[]{Color.parseColor("#e74c3c")});
//                                }
//                            }, 1000);


                            Log.d(TAG, "B측정기의 dustData: " + B_dustData);
                            Log.d(TAG, "dustArray 갯수 : " + B_dustArray.size() + ", tempArray 갯수 : " + B_tempArray.size());
                            Log.d(TAG, "미세먼지 마지막 값 :" + B_dustArray.get(B_dustArray.size() - 1) +
                                    ", 온도 마지막 값 :" + B_tempArray.get(B_tempArray.size() - 1) + "자외선 마지막 값 :" + B_uvArray.get(B_uvArray.size() - 1));

                            if (B_raincheck < 100) {
                                rainCondition.setImageResource(R.drawable.norain);
                            } else {
                                rainCondition.setImageResource(R.drawable.rain);
                            }


                            //미세먼지 상태
                            if (B_dustData <= 30) {
                                iv_dust.setImageResource(R.drawable.ic_baseline_blur_on_good);
                                dustCondition.setText("좋음(0~30)   " + B_dustData + " ㎍/㎥");
                            } else if (30 < B_dustData && B_dustData <= 80) {
                                iv_dust.setImageResource(R.drawable.ic_baseline_blur_on_24);
                                dustCondition.setText("보통(30~80) " + B_dustData + " ㎍/㎥");
                            } else {
                                iv_dust.setImageResource(R.drawable.ic_baseline_blur_on_dangerous);
                                dustCondition.setText("나쁨(81~ ) " + B_dustData + " ㎍/㎥");
                            }

                            String uv_dataString = Float.toString(B_uvData);
                            if (0 <= B_uvData && B_uvData < 6) {
                                iv_uv.setImageResource(R.drawable.ic_baseline_brightness_5_24);
                                uvCondition.setText("보통 (0~5) " + uv_dataString.substring(0, 3) + " ㎼/㎠");
                            } else if (6 <= B_uvData && B_uvData < 10) {
                                iv_uv.setImageResource(R.drawable.ic_baseline_brightness_hot);
                                uvCondition.setText("높음 (6~10) " + uv_dataString.substring(0, 3) + " ㎼/㎠");
                            } else {
                                iv_uv.setImageResource(R.drawable.ic_baseline_brightness_dangerous);
                                uvCondition.setText("나쁨 (6~10) " + uv_dataString.substring(0, 3) + " ㎼/㎠");
                            }
                            tempCondition.setText(B_tempData + " ℃  "+B_humidData+" %");

                        }

                    }//onDataChanged

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }

                });//weatherRef

            }//onItemselected

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

        });


        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        //사용자의 현재 위치
        userLocation = getMyLocation();
        if (userLocation != null) {
            gpsLat = (float) userLocation.getLatitude();
            gpsLng = (float) userLocation.getLongitude();
        }
//        gpsLocation.setText("현재 위치 : " + form.format(gpsLat) + ", " + form.format(gpsLng));
//        Log.d(TAG, "위도 : " + gpsLat + "경도 : " + gpsLng);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                gpsLat = (float) userLocation.getLatitude();
                gpsLng = (float) userLocation.getLongitude();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return root;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


        return root;
    }//onCreateView


    /////사용자의 위치를 수신 메소드
    private Location getMyLocation() {
        Location currentLocation = null;
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("////////////사용자에게 권한을 요청해야함");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, this.REQUEST_CODE_LOCATION);
            getMyLocation(); //권한 승인하면 즉시 위치값 받아옴
        } else {
            System.out.println("////////////권한요청 안해도됨");

            // 수동으로 위치 구하기
            String locationProvider = LocationManager.GPS_PROVIDER;
            currentLocation = locationManager.getLastKnownLocation(locationProvider);
            if (currentLocation != null) {
                double lng = currentLocation.getLongitude();
                double lat = currentLocation.getLatitude();
            }
        }

        return currentLocation;
    }


}






