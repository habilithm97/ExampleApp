package com.example.exampleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.example.WeatherResult;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pedro.library.AutoPermissionsListener;
import com.stanfy.gsonxml.GsonXml;
import com.stanfy.gsonxml.GsonXmlBuilder;
import com.stanfy.gsonxml.XmlParserCreator;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// 파스텔톤 색상 링크 :  https://coolors.co/d3f8e2-e4c1f9-f694c1-edd5b2-a9def9

/*
*기상청 날씨를 가져오려면 기상청에서 제공하는 날짜 포맷이 필요함
 -주소 안에 포함된 gridx와 gridy 파라미터는 날씨 정보를 확인하고 싶은 지역을 나타내는데,
 -지역을 나타내는 값이 경위도 좌표가 아닌 격자의 번호로 표시되어 있기 때문에
 -경위도 좌표를 격자 번호로 변환하는 과정이 필요함

*XML Pull Parser 인터페이스
 -XML : 사람과 기계가 모두 읽을 수 있는 형식으로 데이터를 기술할 수 있도록 데이터를 문서화하는 규칙을 정의한 마크업 언어
  -> 인터넷을 통한 데이터 교환 시, 단순함/범용성/사용성을 제공하는 것이 XML을 사용하는 가장 큰 목적임
 -Pull Parser : 특정 위치까지 파싱되어 내용을 처리한 후 계속 파싱할 것인지 멈출 것인지를 개발자가 제어할 수 있는 특징이 있음

*/

public class MainActivity extends AppCompatActivity implements onTabItemSelectedListener, OnRequestListener, AutoPermissionsListener, MyApplication.OnResponseListener {

    private static final String TAG = "MainActivity";

    ListFragment listFragment;
    WriteFragment writeFragment;
    GraphFragment graphFragment;

    BottomNavigationView bottomNavigationView;
    
    Location currentLocation; // 현재 위치를 담고 있음
    GPSListener gpsListener; // 위치 정보를 수신함

    int locationCount = 0; // 위치 정보를 확인한 횟수(위치를 한 번 확인한 후에는 위치 요청을 취소할 수 있도록

    String currentWeather;

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
        String currentDateString = AppConstants.dateFormat3.format(currentDate); // 형식에 맞는 현재 날짜를 변수에 할당한 후에(yyyy년 MM월 dd일)
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
                getCurrentWeather(); // 현재 위치를 이용해서 날씨 확인
                //getCurrentAddress(); // 현재 위치를 이용해서 주소 확인
             }

            gpsListener = new GPSListener(); // 위치 리스너 객체 생성, 요청된 위치를 수신하기 위함
            // 최소 시간으로는 10초, 최소 거리는 0으로 하여 10초마다 위치 정보를 전달받게됨
            long minTime = 10000;
            float minDistance = 0;

            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener); // 현재 위치 갱신
            println("현재 위치가 갱신되었습니다.");

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

    @Override
    public void onDenied(int i, String[] strings) {}

    @Override
    public void onGranted(int i, String[] strings) {}

    class GPSListener implements LocationListener {

        @Override
        public void onLocationChanged(@NonNull Location location) { // 위치가 확인되었을 때 자동으로 호출됨
            currentLocation = location;
            locationCount++;

            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
            String message = "최근 위치 : 위도 : " + latitude + "\n경도:" + longitude;
            println(message);

            getCurrentWeather(); // 현재 위치를 이용해서 날씨 확인
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

    public void getCurrentWeather() { // 현재 날씨 가져오기
        // GridUtill 객체의 getGrid() 메서드로 격자 번호를 확인
        Map<String, Double> gridMap = GridUtil.getGrid(currentLocation.getLatitude(), currentLocation.getLongitude()); // 여기는 String, Double 인데

        double gridx = gridMap.get("x");
        double gridy = gridMap.get("y");
        println("x -> " + gridx + ", " + "y -> " + gridy);

        sendWeatherRequest(gridx, gridy);
    }

    public void sendWeatherRequest(double gridx, double gridy) { // 기상청 날씨 서버로 요청 전송
        String url = "http://www.kma.go.kr/wid/queryDFS.jsp"; // 기상청 API
        // Math.round : 소수점 이하를 반올림 (Math.ceil : 올림, Math.floor : 버림)
        url += "?gridx=" + Math.round(gridx);
        url += "&gridy=" + Math.round(gridy);

        Map<String, String> params = new HashMap<String, String>(); // 여기는 String, String...?
        MyApplication.send(AppConstants.REQ_WEATHER_BY_GRID, Request.Method.GET, url, params, this);
    }

    public void processResponse(int requestCode, int responseCode, String response) { // 기상청 날씨 서버로부터 응답을 받으면 호출됨
        if (responseCode == 200) { // 응답 코드가 200이면
            if (requestCode == AppConstants.REQ_WEATHER_BY_GRID) { // 요청 코드가 102면

                // XML 응답 데이터를 자바 객체로 만듦
                XmlParserCreator parserCreator = new XmlParserCreator() {
                    @Override
                    public XmlPullParser createParser() {
                        try {
                            return XmlPullParserFactory.newInstance().newPullParser();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                };

                GsonXml gsonXml = new GsonXmlBuilder().setXmlParserCreator(parserCreator).setSameNameLists(true).create();
                // 자바 객체는 WeatherResult라는 객체로 정의
                WeatherResult weatherResult = gsonXml.fromXml(response, WeatherResult.class);

                // 현재 기준 시간
                try {
                    // 응답 받은 날씨 데이터는 로그로 출력해서 확인
                    Date tmDate = AppConstants.dateFormat.parse(weatherResult.header.tm);
                    String tmDateText  = AppConstants.dateFormat2.format(tmDate);
                    println("기준 시간 : " + tmDateText);

                    for (int i = 0; i < weatherResult.body.datas.size(); i++) {
                        WeatherItem item = weatherResult.body.datas.get(i);
                        println("#" + i + " 시간 : " + item.hour + "시, " + item.day + "일째");
                        println("  날씨 : " + item.wfKor);
                        println("  기온 : " + item.temp + " C");
                        println("  강수확률 : " + item.pop + "%");

                        println("디버그 1 : " + (int)Math.round(item.ws * 10));
                        float ws = Float.valueOf(String.valueOf((int)Math.round(item.ws * 10))) / 10.0f;
                        println("  풍속 : " + ws + " m/s");
                    }

                    // 작성화면의 좌측 상단에 있는 날씨 아이콘에 현재 날씨 설정
                    WeatherItem item = weatherResult.body.datas.get(0);
                    currentWeather = item.wfKor;
                    if (writeFragment != null) {
                        writeFragment.setWeather(item.wfKor);
                    }

                    // 위치를 한번 확인한 후에는 위치 요청 서비스를 중지함
                    if (locationCount > 1) {
                        stopLocationService();
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                }
            } else {
                println("알 수 없는 요청 코드 : " + requestCode);
            }
        } else {
            println("응답 코드 실패 : " + responseCode);
        }
    }

    private void println(String data) {
        Log.d(TAG, data);
    }
}