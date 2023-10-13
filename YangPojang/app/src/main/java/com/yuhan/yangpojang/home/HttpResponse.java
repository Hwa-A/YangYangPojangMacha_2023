package com.yuhan.yangpojang.home;

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

    private static double currentLatitude;
    private static double currentLongitude;

    public static void sendData(String newText, DataCallback callback1){ //새로운 스레드에 네트워크 요청 보내는 메서드 -> 네트워크 작업이 백그라운드에서 수행
        dataCallback = callback1; //SearchActivity.class로 데이터를 보내기 위한 콜백
        keyword = newText; //검색어 업데이트
        new Thread(){
            public void run(){
                conn.requestWebServer(keyword, "http://localhost:8080", "30", "1", BuildConfig.POI_API_KEY, "poi", callback);
                //HttpConnection 클래스의 requestWebServer 메서드를 호출하여 서버로 데이터 전달
            }
        }.start();
    }

    private static final Callback callback = new Callback() { //서버로부터의 응답 처리
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
                        // "contents"객체 안에 있는 "poi" 배열을 가져옴
                        JSONArray poiArray = jsonObject.getJSONObject("search").getJSONObject("contents").getJSONArray("poi"); //하나의 데이터 항목 저장

                        LinkedHashMap<String, ArrayList<String>> placeInfo = new LinkedHashMap<>(); // 메인으로 데이터를 보낼 hashMap
                        int placeInfoCnt = 0; // address_hash용량 제한을 위한 변수

                        // 1순위 : keyword == name
                        for (int i = 0; i < poiArray.length(); i++) {
                            if( placeInfoCnt >= 15){ // 용량 15개로 제한
                                break;
                            }

                            JSONObject poiObject = poiArray.getJSONObject(i); //poiArray의 데이터 항목을 JSONObject로 추출 -> 각 데이터 항목의 필드에 접근
                            Log.d(MotionEffect.TAG, "poi오브젝트 :" + poiObject);

                            String typeName = poiObject.optString("typeName", ""); // 버스 정류장 제외
                            if(!typeName.endsWith("정류장")){
                                Log.d(MotionEffect.TAG, "poi오브젝트(필터링) :" + poiObject);

                                String Placename = poiObject.getString("name"); //지명
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

                                if (Placename.equals(keyword)) {
                                    String address;

                                    // 도로명이 비어있는 데이터들이 있으면 지번으로
                                    if (roadAdd.equals(" ")) {
                                        address = jibunAdd;
                                    } else {
                                        address = roadAdd;
                                    }

                                    // 키 : 지명, 값 : 주소, 위도, 경도
                                    placeInfo.put(Placename, new ArrayList<>());
                                    placeInfo.get(Placename).add(address);
                                    placeInfo.get(Placename).add(String.valueOf(Coord[0]));
                                    placeInfo.get(Placename).add(String.valueOf(Coord[1]));
                                    placeInfoCnt++;
                                }
                            }
                        }

                        // 2순위 : name으로 시작하는 keyword
                        for (int i = 0; i < poiArray.length(); i++) {
                            if( placeInfoCnt >= 15){ // 용량 15개로 제한
                                break;
                            }

                            JSONObject poiObject = poiArray.getJSONObject(i); //poiArray의 데이터 항목을 JSONObject로 추출 -> 각 데이터 항목의 필드에 접근
                            Log.d(MotionEffect.TAG, "poi오브젝트 :" + poiObject);

                            String typeName = poiObject.optString("typeName", ""); // 버스 정류장 제외
                            if(!typeName.endsWith("정류장")){
                                Log.d(MotionEffect.TAG, "poi오브젝트(필터링) :" + poiObject);

                                String Placename = poiObject.getString("name"); //지명
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

                                if (Placename.startsWith(keyword)) {
                                    String address;

                                    // 도로명이 비어있는 데이터들이 있으면 지번으로
                                    if (roadAdd.equals(" ")) {
                                        address = jibunAdd;
                                    } else {
                                        address = roadAdd;
                                    }

                                    // 키 : 지명, 값 : 주소, 위도, 경도
                                    placeInfo.put(Placename, new ArrayList<>());
                                    placeInfo.get(Placename).add(address);
                                    placeInfo.get(Placename).add(String.valueOf(Coord[0]));
                                    placeInfo.get(Placename).add(String.valueOf(Coord[1]));
                                    placeInfoCnt++;
                                }
                            }
                        }


                        // 3순위 : keyword가 주소에 포함
                        for (int i = 0; i < poiArray.length(); i++) {
                            if( placeInfoCnt >= 15){ // 용량 15개로 제한
                                break;
                            }

                            JSONObject poiObject = poiArray.getJSONObject(i); //poiArray의 데이터 항목을 JSONObject로 추출 -> 각 데이터 항목의 필드에 접근
                            Log.d(MotionEffect.TAG, "poi오브젝트 :" + poiObject);

                            String typeName = poiObject.optString("typeName", ""); // 버스 정류장 제외
                            if(!typeName.endsWith("정류장")){
                                Log.d(MotionEffect.TAG, "poi오브젝트(필터링) :" + poiObject);

                                String Placename = poiObject.getString("name"); //지명
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
                                    placeInfo.put(Placename, new ArrayList<>());
                                    placeInfo.get(Placename).add(address);
                                    placeInfo.get(Placename).add(String.valueOf(Coord[0]));
                                    placeInfo.get(Placename).add(String.valueOf(Coord[1]));
                                    placeInfoCnt++;
                                }
                            }
                        }
                        // 이제 정렬 작업을 수행합니다.
                        List<HashMap<String, String>> sortedPlaceInfo = sortPlaceInfoByDistance(placeInfo);
                        Log.d("fdsfasf", sortedPlaceInfo.toString());


                        // 데이터가 준비되었으므로 콜백을 호출합니다.
                        if (dataCallback != null) {
                            Log.d(MotionEffect.TAG, "보낼 값(도로명) :" + placeInfo);
                            // sortedPlaceInfo를 LinkedHashMap<String, ArrayList<String>> 형식으로 변환
                            LinkedHashMap<String, ArrayList<String>> convertedData = new LinkedHashMap<>();
                            for (HashMap<String, String> placeData : sortedPlaceInfo) {
                                String name = placeData.get("name");
                                String address = placeData.get("address");
                                ArrayList<String> info = new ArrayList<>();
                                info.add(address);
                                info.add(placeData.get("latitude"));
                                info.add(placeData.get("longitude"));
                                convertedData.put(name, info);
                            }
                            dataCallback.onDataLoaded(convertedData);
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



    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // 거리 계산 함수 (예: Haversine Formula)
        // 두 지점 사이의 거리를 반환합니다.
        // 구체적인 구현은 사용 중인 지도 API에 따라 달라질 수 있습니다.
        // 이 예시에서는 간단한 구현을 사용합니다.
        // 이 함수를 필요에 맞게 수정하세요.
        double R = 6371; // 지구 반경 (단위: 킬로미터)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public static List<HashMap<String, String>> sortPlaceInfoByDistance(LinkedHashMap<String, ArrayList<String>> placeInfo) {
        List<HashMap<String, String>> sortedPlaceInfoList = new ArrayList<>();

        for (String placeName : placeInfo.keySet()) {
            HashMap<String, String> placeData = new HashMap<>();
            placeData.put("name", placeName);
            placeData.put("address", placeInfo.get(placeName).get(0));
            placeData.put("latitude", placeInfo.get(placeName).get(1));
            placeData.put("longitude", placeInfo.get(placeName).get(2));

            double placeLatitude = Double.parseDouble(placeInfo.get(placeName).get(1));
            double placeLongitude = Double.parseDouble(placeInfo.get(placeName).get(2));
            double distance = calculateDistance(currentLatitude, currentLongitude, placeLatitude, placeLongitude);

            placeData.put("distance", String.valueOf(distance));

            sortedPlaceInfoList.add(placeData);
        }

        sortedPlaceInfoList.sort((place1, place2) -> {
            double distance1 = Double.parseDouble(place1.get("distance"));
            double distance2 = Double.parseDouble(place2.get("distance"));
            return Double.compare(distance1, distance2);
        });

        return sortedPlaceInfoList;
    }

    public static void setCurrentLocation(double latitude, double longitude) {
        currentLatitude = latitude;
        currentLongitude = longitude;
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