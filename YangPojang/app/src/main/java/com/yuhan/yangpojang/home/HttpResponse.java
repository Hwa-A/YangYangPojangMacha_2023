package com.yuhan.yangpojang.home;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.MotionEffect;

import com.yuhan.yangpojang.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HttpResponse {
    private static HttpConnection conn = HttpConnection.getInstance();
    private static String keyword;
    private static DataCallback dataCallback;

    // 현재 사용자 위치 정보
    static double currentLatitude;
    static double currentLongitude;
    static String addressName ="";


    //새로운 스레드에 네트워크 요청 보내는 메서드 -> 네트워크 작업이 백그라운드에서 수행
    public static void sendData(Context context, String newText, DataCallback callback1){
        dataCallback = callback1; //SearchActivity.class로 데이터를 보내기 위한 콜백
        keyword = newText; //검색어 업데이트

        new Thread(){
            public void run(){
                conn.requestWebServer(keyword, "http://localhost:8080", "40", "1", "0E895A6037E6B2A5478DFD072465A01B", "poi", callback);
                //HttpConnection 클래스의 requestWebServer 메서드를 호출하여 서버로 데이터 전달
            }
        }.start();
    }

    // 사용자의 현재 위치
    public static void setCurrentLocation(Context context, double latitude, double longitude) {
        currentLatitude = latitude;
        currentLongitude = longitude;

        addressName = getAddressFromLocation(context,currentLatitude, currentLongitude); //받아온 위도 경도로 부터 행정구역 추출
        Log.d("HttpResponse", "현재 위치 : " + addressName);

    }

    //서버로부터의 응답 처리
    private static final Callback callback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            Log.e(MotionEffect.TAG, "오류:"+e.getMessage());
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            try {
                if (response.isSuccessful()) {
                    try {
                        String body = response.body().string(); //HTTP응답의 본문을 문자열로 읽어옴
                        Log.d(MotionEffect.TAG, "응답한 Body:" + body);

                        JSONObject jsonObject = new JSONObject(body); //문자열 형태로 읽어온 body를 JSON형식으로 파싱하여 JSONObject객체로 변환 -> JSON데이터 구조를 유지한 채로 데이터 접근 가능
                        JSONArray poiArray = jsonObject.getJSONObject("search").getJSONObject("contents").getJSONArray("poi"); // "contents"객체 안에 있는 "poi" 배열을 가져옴, 하나의 데이터 항목 저장

                        int placeInfoCnt = 0; // address_hash용량 제한을 위한 변수

                        Log.d(MotionEffect.TAG, "placeInfo 값(키워드) :  :" + keyword);

                        LinkedHashMap<String, ArrayList<String>> placeInfo0 = new LinkedHashMap<>();
                        // 0순위 : 같은 지역에 있는 데이터 우선
                        for(int i = 0; i < poiArray.length(); i++){

                            JSONObject poiObject = poiArray.getJSONObject(i); //poiArray의 데이터 항목을 JSONObject로 추출 -> 각 데이터 항목의 필드에 접근
                            Log.d(MotionEffect.TAG, "poi오브젝트 :" + poiObject);

                            String typeName = poiObject.optString("typeName", ""); // 버스 정류장 제외
                            if(!typeName.endsWith("정류장")) {
                                Log.d(MotionEffect.TAG, "poi오브젝트(필터링) :" + poiObject);

                                String placeName = poiObject.getString("name"); //지명
                                String roadAdd = poiObject.getString("roadAdres"); //도로명
                                String jibunAdd = poiObject.getString("jibunAdres"); //지번
                                double xCoord = poiObject.getDouble("x"); //x좌표
                                double yCoord = poiObject.getDouble("y"); //y좌표
                                double[] Coord = coordinateTransformation(xCoord, yCoord); // x,y좌표 -> 위도, 경도

                                // 주소 끝에 "도", "대", "철" 글자가 출력되는 데이터들에 해당 글자 삭제함
                                if (jibunAdd.endsWith("도") || jibunAdd.endsWith("대") || jibunAdd.endsWith("철")) {
                                    // "도" 글자가 있는 경우, 맨 끝의 "도" 글자를 제외한 부분을 추출
                                    jibunAdd = jibunAdd.substring(0, jibunAdd.length() - 1);
                                }


                                if (roadAdd.startsWith(addressName) || jibunAdd.startsWith(addressName)) {
                                    String address;

                                    // 도로명이 비어있는 데이터들이 있으면 지번으로
                                    if (roadAdd.equals(" ")) {
                                        address = jibunAdd;
                                    } else {
                                        address = roadAdd;
                                    }
                                    String[] address1 = jibunAdd.split(" ");
                                    placeName = placeName + "(" + address1[2] + ")";

                                    // 키 : 지명, 값 : 주소, 위도, 경도
                                    ArrayList<String> values = new ArrayList<>();
                                    values.add(address);
                                    values.add(String.valueOf(Coord[0]));
                                    values.add(String.valueOf(Coord[1]));
                                    placeInfo0.put(placeName, values);

                                    placeInfoCnt++;

                                    Log.d(MotionEffect.TAG, "placeInfo 값(0순위) : " + placeInfo0);
                                }
                            }

                        }


                        LinkedHashMap<String, ArrayList<String>> placeInfo1 = new LinkedHashMap<>();
                        // 1순위 : keyword == name
                        for (int i = 0; i < poiArray.length(); i++) {
                            JSONObject poiObject = poiArray.getJSONObject(i); //poiArray의 데이터 항목을 JSONObject로 추출 -> 각 데이터 항목의 필드에 접근
                            Log.d(MotionEffect.TAG, "poi오브젝트 :" + poiObject);

                            String typeName = poiObject.optString("typeName", ""); // 버스 정류장 제외
                            if(!typeName.endsWith("정류장")){
                                Log.d(MotionEffect.TAG, "poi오브젝트(필터링) :" + poiObject);

                                String placeName = poiObject.getString("name"); //지명
                                String roadAdd = poiObject.getString("roadAdres"); //도로명
                                String jibunAdd = poiObject.getString("jibunAdres"); //지번
                                double xCoord = poiObject.getDouble("x"); //x좌표
                                double yCoord = poiObject.getDouble("y"); //y좌표
                                double[] Coord = coordinateTransformation(xCoord, yCoord); // x,y좌표 -> 위도, 경도

                                // 주소 끝에 "도", "대", "철" 글자가 출력되는 데이터들에 해당 글자 삭제함
                                if (jibunAdd.endsWith("도") || jibunAdd.endsWith("대") || jibunAdd.endsWith("철")) {
                                    // "도" 글자가 있는 경우, 맨 끝의 "도" 글자를 제외한 부분을 추출
                                    jibunAdd = jibunAdd.substring(0, jibunAdd.length() - 1);
                                }

                                // 키워드, 주소에 띄어쓰기 제거 - 사용자 입력 시 띄어쓰기에 상관없이 데이터 검색 가능하도록 함
                                keyword = keyword.replace(" ", "");

                                if (placeName.equals(keyword)) {
                                    String address;

                                    // 도로명이 비어있는 데이터들이 있으면 지번으로
                                    if (roadAdd.equals(" ")) {
                                        address = jibunAdd;
                                    } else {
                                        address = roadAdd;
                                    }

                                    // 키 : 지명, 값 : 주소, 위도, 경도
                                    ArrayList<String> values = new ArrayList<>();
                                    values.add(address);
                                    values.add(String.valueOf(Coord[0]));
                                    values.add(String.valueOf(Coord[1]));
                                    placeInfo1.put(placeName, values);

                                    placeInfoCnt++;

                                    Log.d(MotionEffect.TAG, "placeInfo 값(1순위) :  :" + placeInfo1);
                                }
                            }
                        }


                        LinkedHashMap<String, ArrayList<String>> placeInfo2 = new LinkedHashMap<>();
                        // 2순위 : name으로 시작하는 keyword
                        for (int i = 0; i < poiArray.length(); i++) {

                            JSONObject poiObject = poiArray.getJSONObject(i); //poiArray의 데이터 항목을 JSONObject로 추출 -> 각 데이터 항목의 필드에 접근
                            Log.d(MotionEffect.TAG, "poi오브젝트 :" + poiObject);

                            String typeName = poiObject.optString("typeName", ""); // 버스 정류장 제외
                            if(!typeName.endsWith("정류장")){
                                Log.d(MotionEffect.TAG, "poi오브젝트(필터링) :" + poiObject);

                                String placeName = poiObject.getString("name"); //지명
                                String roadAdd = poiObject.getString("roadAdres"); //도로명
                                String jibunAdd = poiObject.getString("jibunAdres"); //지번
                                double xCoord = poiObject.getDouble("x"); //x좌표
                                double yCoord = poiObject.getDouble("y"); //y좌표
                                double[] Coord = coordinateTransformation(xCoord, yCoord); // x,y좌표 -> 위도, 경도

                                // 주소 끝에 "도", "대", "철" 글자가 출력되는 데이터들에 해당 글자 삭제함
                                if (jibunAdd.endsWith("도") || jibunAdd.endsWith("대") || jibunAdd.endsWith("철")) {
                                    // "도" 글자가 있는 경우, 맨 끝의 "도" 글자를 제외한 부분을 추출
                                    jibunAdd = jibunAdd.substring(0, jibunAdd.length() - 1);
                                }

                                // 키워드, 주소에 띄어쓰기 제거 - 사용자 입력 시 띄어쓰기에 상관없이 데이터 검색 가능하도록 함
                                keyword = keyword.replace(" ", "");

                                if (placeName.startsWith(keyword)) {
                                    String address;

                                    // 도로명이 비어있는 데이터들이 있으면 지번으로
                                    if (roadAdd.equals(" ")) {
                                        address = jibunAdd;
                                    } else {
                                        address = roadAdd;
                                    }

                                    // 키 : 지명, 값 : 주소, 위도, 경도
                                    ArrayList<String> values = new ArrayList<>();
                                    values.add(address);
                                    values.add(String.valueOf(Coord[0]));
                                    values.add(String.valueOf(Coord[1]));
                                    placeInfo2.put(placeName, values);

                                    placeInfoCnt++;



                                    Log.d(MotionEffect.TAG, "placeInfo 값(2순위) : " + placeInfo2);
                                }
                            }
                        }


                        LinkedHashMap<String, ArrayList<String>> placeInfo3 = new LinkedHashMap<>();
                        // 3순위 : keyword가 주소에 포함
                        for (int i = 0; i < poiArray.length(); i++) {
                            JSONObject poiObject = poiArray.getJSONObject(i); //poiArray의 데이터 항목을 JSONObject로 추출 -> 각 데이터 항목의 필드에 접근
                            Log.d(MotionEffect.TAG, "poi오브젝트 :" + poiObject);

                            String typeName = poiObject.optString("typeName", ""); // 버스 정류장 제외
                            if(!typeName.endsWith("정류장")){
                                Log.d(MotionEffect.TAG, "poi오브젝트(필터링) :" + poiObject);

                                String placeName = poiObject.getString("name"); //지명
                                String roadAdd = poiObject.getString("roadAdres"); //도로명
                                String jibunAdd = poiObject.getString("jibunAdres"); //지번
                                double xCoord = poiObject.getDouble("x"); //x좌표
                                double yCoord = poiObject.getDouble("y"); //y좌표
                                double[] Coord = coordinateTransformation(xCoord, yCoord); // x,y좌표 -> 위도, 경도

                                // 주소 끝에 "도", "대", "철" 글자가 출력되는 데이터들에 해당 글자 삭제함
                                if (jibunAdd.endsWith("도") || jibunAdd.endsWith("대") || jibunAdd.endsWith("철")) {
                                    // "도" 글자가 있는 경우, 맨 끝의 "도" 글자를 제외한 부분을 추출
                                    jibunAdd = jibunAdd.substring(0, jibunAdd.length() - 1);
                                }

                                // 키워드, 주소에 띄어쓰기 제거 - 사용자 입력 시 띄어쓰기에 상관없이 데이터 검색 가능하도록 함
                                String road = roadAdd.replace(" ", "");
                                String jibun = jibunAdd.replace(" ", "");
                                keyword = keyword.replace(" ", "");

                                if (road.contains(keyword) || jibun.contains(keyword)) {
                                    String address;

                                    // 도로명이 비어있는 데이터들이 있으면 지번으로
                                    if (roadAdd.equals(" ")) {
                                        address = jibunAdd;
                                    } else {
                                        address = roadAdd;
                                    }

                                    // 키 : 지명, 값 : 주소, 위도, 경도
                                    ArrayList<String> values = new ArrayList<>();
                                    values.add(address);
                                    values.add(String.valueOf(Coord[0]));
                                    values.add(String.valueOf(Coord[1]));
                                    placeInfo3.put(placeName, values);

                                    placeInfoCnt++;



                                    Log.d(MotionEffect.TAG, "placeInfo 값(3순위) :  :" + placeInfo3);
                                }
                            }
                        }

                        LinkedHashMap<String, ArrayList<String>> placeInfo = new LinkedHashMap<>(); // 메인으로 데이터를 보낼 hashMap
                        placeInfo.putAll(placeInfo0);
                        placeInfo.putAll(placeInfo1);
                        placeInfo.putAll(placeInfo2);
                        placeInfo.putAll(placeInfo3);
                        Log.d(MotionEffect.TAG, "placeInfo 값(최종) :  :" + placeInfo);


                        // 데이터가 준비되었으므로 콜백을 호출합니다.
                        if (dataCallback != null) {
                            Log.d(MotionEffect.TAG, "보낼 값(도로명) :" + placeInfo);
                            dataCallback.onDataLoaded(placeInfo);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(MotionEffect.TAG, "JSON 파싱 오류" + e.getMessage());
                    }

                } else {
                    Log.e(MotionEffect.TAG, "HTTP 요청 실패 - 응답 코드: " + response.code() + " 메시지: " + response.message());
                }
            }finally { //응답 객체의 본문을 안전하게 닫고 메모리 누수를 방지
                if(response.body() != null){
                    response.body().close();
                }
            }

        }
    };

    // 위도, 경도 -> 주소
    public static String getAddressFromLocation(Context context, double latitude, double longitude){
        Geocoder geocoder = new Geocoder(context);
        String result = "서울특별시"; // 기본값

        try{
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if(addresses != null & addresses.size() > 0){
                Address address = addresses.get(0);
                result = address.getAddressLine(0); // 주소 추출
                Log.d("HttpResponse", "주소 : " + result);

                String[] parts = result.split(" ");
                result = parts[1]; // 주소에서 행정구역 추출

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    // 좌표계 변환
    public static double[] coordinateTransformation(double xCoord, double yCoord){
        // CRS(좌표 참조 시스템) 객체 생성
        CRSFactory crsFactory = new CRSFactory();

        // WGS84 system 정의
        String wgsName = "WGS84"; // 좌표 참조 시스템 이름
        String wgsProj = "+proj=longlat +datum=WGS84 +no_defs"; // 좌표 참조 시스템 세부 정보 정의( +proj=longlat : 위도, 경도 사용함을 의미 / +datum=WGS84 : 데이터텀을 WGS84로 설정함 / +no_defs : 정의 파일 사용 x )
        CoordinateReferenceSystem wgsSystem = crsFactory.createFromParameters(wgsName, wgsProj); // WGS84 좌표 참조 시스템 객체 생성

        // UTMK system 정의
        String utmName = "UTMk";
        String utmProj = "+proj=tmerc +lat_0=38 +lon_0=127.5 +k=0.9996 +x_0=1000000 +y_0=2000000 +ellps=GRS80 +units=m +no_defs";
        CoordinateReferenceSystem utmkSystem = crsFactory.createFromParameters(utmName, utmProj);

        // 변환할 좌표계 정보 생성
        ProjCoordinate p = new ProjCoordinate(); // PrijCoorinate : 좌표를 저장하고 관리하기 위한 클래스
        p.x = xCoord; // 변환할 좌표 입력
        p.y = yCoord;

        // 변환된 좌표를 담을 객체 생성
        ProjCoordinate p2 = new ProjCoordinate(); // 변환된 좌표를 담을 객체 생성

        // 좌표 변환
        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory(); // CoordinateTransformFactory : 좌표변환 수행
        CoordinateTransform coodinateTransform = ctFactory.createTransform(utmkSystem, wgsSystem); // UTMK -> WGS 좌표 변환 정의
        ProjCoordinate projCoordinate = coodinateTransform.transform(p, p2); // 좌표변환 수행

        // 변환된 좌표
        double[] result = {projCoordinate.y, projCoordinate.x}; // 변환된 위도, 경도
        double longitude = projCoordinate.x; // 변환된 경도
        double latitude = projCoordinate.y; // 변환된 위도
        Log.d("SearchActivity", "변환된 위도 : " + latitude + ", " + "경도 : " + longitude);

        return result;
    }

    public interface DataCallback {
        void onDataLoaded(LinkedHashMap<String,ArrayList<String>> address_hash);
    }


}