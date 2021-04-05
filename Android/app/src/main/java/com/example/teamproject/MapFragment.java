package com.example.teamproject;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.example.teamproject.HomeFragment.A_dustData;
import static com.example.teamproject.HomeFragment.A_equipLat;
import static com.example.teamproject.HomeFragment.A_equipLng;
import static com.example.teamproject.HomeFragment.A_tempData;
import static com.example.teamproject.HomeFragment.A_uvData;
import static com.example.teamproject.HomeFragment.B_dustData;
import static com.example.teamproject.HomeFragment.B_equipLat;
import static com.example.teamproject.HomeFragment.B_equipLng;
import static com.example.teamproject.HomeFragment.B_tempData;
import static com.example.teamproject.HomeFragment.B_uvData;
import static com.example.teamproject.HomeFragment.gpsLat;
import static com.example.teamproject.HomeFragment.gpsLng;


public class MapFragment extends Fragment implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener {

    GoogleMap map;
    MapView mapView;
    private Context context;
    private static final String TAG = "MapFragment";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = container.getContext();
        return inflater.inflate(R.layout.fragment_map, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.mv_map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        map = googleMap;



            //내 위치 마커 생성
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.user_marker,null);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap user_Marker = Bitmap.createScaledBitmap(b, 150, 150, false);

        MarkerOptions userMarker = new MarkerOptions();
        LatLng userLatLng = new LatLng(gpsLat,gpsLng);
        userMarker.position(userLatLng)
                .title("내 위치")
                .icon(BitmapDescriptorFactory.fromBitmap(user_Marker));
        map.addMarker(userMarker);


        //측정기 마커 생성
        MarkerOptions A_markerOptions = new MarkerOptions();
        LatLng A_deviceLatLng = new LatLng(A_equipLat,A_equipLng);
        A_markerOptions.position(A_deviceLatLng)
                    .title("한밭대");
//                    .snippet("미세먼지 : " + A_dustData + "   기온 : " + A_tempData+"'c"+"  자외선 : "+A_uvData);
            map.addMarker(A_markerOptions);

        MarkerOptions B_markerOptions = new MarkerOptions();
        LatLng B_deviceLatLng = new LatLng(B_equipLat,B_equipLng);
        B_markerOptions.position(B_deviceLatLng)
                .title("우리집");
//                .snippet("미세먼지 : " + B_dustData + "   기온 : " + B_tempData+"'c"+"  자외선 : "+B_uvData);
        map.addMarker(B_markerOptions);

        //카메라 위치 옮기기
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 16));
        map.setOnMarkerClickListener(this);
        map.setOnInfoWindowClickListener(this);
    }


    //마커 클릭 시 이벤트
    @Override
    public boolean onMarkerClick(Marker marker) {
//        Toast.makeText(context, "마커 클릭", Toast.LENGTH_SHORT).show();

        return false;
    }

    //인포창 클릭 시 이벤트
    @Override
    public void onInfoWindowClick(Marker marker) {
//        Toast.makeText(context, "윈도우 클릭", Toast.LENGTH_SHORT).show();

    }

    public void onLocationChanged(Location location) {

    }

    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    public void onProviderEnabled(String s) {

    }

    public void onProviderDisabled(String s) {

    }
}


