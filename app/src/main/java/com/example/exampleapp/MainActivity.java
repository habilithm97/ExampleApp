package com.example.exampleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Date;

// 파스텔톤 색상 링크 :  https://coolors.co/d3f8e2-e4c1f9-f694c1-edd5b2-a9def9

/*
*기상청 날씨를 가져오려면 기상청에서 제공하는 날짜 포맷이 필요함
 -주소 안에 포함된 gridx와 gridy 파라미터는 날씨 정보를 확인하고 싶은 지역을 나타내는데,
 -지역을 나타내는 값이 경위도 좌표가 아닌 격자의 번호로 표시되어 있기 때문에
 -경위도 좌표를 격자 번호로 변환하는 과정이 필요함
*/

public class MainActivity extends AppCompatActivity implements onTabItemSelectedListener, OnRequestListener{

    private static final String TAG = "MainActivity";

    ListFragment listFragment;
    WriteFragment writeFragment;
    GraphFragment graphFragment;

    BottomNavigationView bottomNavigationView;
    
    Location currentLocation; // 현재 위치를 담고 있음
    GPSListener gpsListener; // 위치 정보를 수신함

    int locationCount = 0; // 위치 정보를 확인한 횟수(위치를 한 번 확인한 후에는 위치 요청을 취소할 수 있도록

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listFragment = new ListFragment();
        writeFragment = new WriteFragment();
        graphFragment = new GraphFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, listFragment).commit();

        bottomNavigationView = findViewById(R.id.bottomNavi);
        // 하단 탭에 들어 있는 각각의 버튼을 눌렀을 때
        // onNavigationItemSelected()가 자동으로 호출되므로 그 안에서 메뉴 아이템의 id 값으로 버튼을 구분한 후 그에 맞는 기능을 구현함
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.tab1:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, listFragment).commit();
                                return true;

                            case R.id.tab2:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, writeFragment).commit();
                                return true;

                            case R.id.tab3:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, graphFragment).commit();
                                return true;
                        }
                        return false;
                    }
                });
    }

    public void onRequest(String command) { // 두 번째 프래그먼트에서 호출됨
        if(command != null) {
            if(command.equals("getCurrentLocation")) {
                getCurrentLocation(); // 위치 확인이 시작됨
            }
        }
    }

    public void getCurrentLocation() { // 현재 위치 요청
        Date currentDate = new Date(); // 현재 날짜를 가져와서
        String currentDateString = AppConstants.dateFormat3.format(currentDate); // 형식에 맞는 현재 날짜를 변수에 할당한 후에
        if (writeFragment != null) {
            writeFragment.setDateString(currentDateString); // 두 번째 프래그먼트 상단에 현재 날짜를 표시함
        }
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            currentLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER); // 위치 관리자의 위치 제공자로 최근 위치 정보를 확인해서 현재 위치 변수에 할당함
            if (currentLocation != null) {
                double latitude = currentLocation.getLatitude();
                double longitude = currentLocation.getLongitude();
                String message = "최근 위치 : 위도 : " + latitude + "\n경도:" + longitude;
                println(message);

                // 현재 위치가 확인되면 호출됨
                //getCurrentWeather(); // 현재 위치를 이용해서 날씨 확인
                //getCurrentAddress(); // 현재 위치를 이용해서 주소 확인
             }

            gpsListener = new GPSListener(); // 위치 리스너 객체 생성, 요청된 위치를 수신하기 위함
            // 최소 시간으로는 10초, 최소 거리는 0으로 하여 10초마다 위치 정보를 전달받게됨
            long minTime = 10000;
            float minDistance = 0;

            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener); // 위치 요청
            println("현재 위치가 요청되었습니다.");

        } catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    public void stopLocationService() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            manager.removeUpdates(gpsListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    class GPSListener implements LocationListener {

        @Override
        public void onLocationChanged(@NonNull Location location) { // 위치가 확인되었을 때 자동으로 호출됨
            currentLocation = location;
            locationCount++;

            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
            String message = "최근 위치 : 위도 : " + latitude + "\n경도:" + longitude;
            println(message);

            //getCurrentWeather(); // 현재 위치를 이용해서 날씨 확인
            //getCurrentAddress(); // 현재 위치를 이용해서 주소 확인
        }
    }

    public void onTabSelected(int position) {
        if(position == 0) {
            bottomNavigationView.setSelectedItemId(R.id.tab1);
        } else if(position == 2) {
            bottomNavigationView.setSelectedItemId(R.id.tab2);
        } else if(position == 3) {
            bottomNavigationView.setSelectedItemId(R.id.tab3);
        }
    }

    private void println(String data) {
        Log.d(TAG, data);
    }
}