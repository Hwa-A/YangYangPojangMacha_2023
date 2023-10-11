package com.yuhan.yangpojang.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.MotionEffect;

import com.yuhan.yangpojang.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HttpResponse {
    private static HttpConnection conn = HttpConnection.getInstance();
    private static String keyword;
    private static DataCallback dataCallback;


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
                        Log.d("HttpResponse", "리스폰스된 body : " + body);
                        JSONObject jsonObject = new JSONObject(body); //문자열 형태로 읽어온 body를 JSON형식으로 파싱하여 JSONObject객체로 변환 -> JSON데이터 구조를 유지한 채로 데이터 접근 가능

                        // "contents"객체 안에 있는 "poi" 배열을 가져옴
                        JSONArray poiArray = jsonObject.getJSONObject("search").getJSONObject("contents").getJSONArray("poi"); //하나의 데이터 항목 저장

                        HashMap<String, String> address_hash = new HashMap<>();

                        for (int i = 0; i < poiArray.length(); i++) {
                            JSONObject poiObject = poiArray.getJSONObject(i); //poiArray의 데이터 항목을 JSONObject로 추출 -> 각 데이터 항목의 필드에 접근
                            Log.d("HttpResponse", "리스폰스된 poiObject : ");
                            String typeName = poiObject.getString("typeName"); //분류

                            if(!typeName.contains("버스정류장")){
                                Log.d("HttpResponse", "리스폰스된 poiObject(without버정) : " + poiObject);
                                String name = poiObject.getString("name"); // 지명
                                String roadAdd = poiObject.getString("roadAdres"); // 도로명
                                String jibunAdd = poiObject.getString("jibunAdres"); // 지번
                                String latitude = poiObject.getString("x"); // x좌표
                                String longitude = poiObject.getString("y"); // y좌표

                                if (jibunAdd.endsWith("도") || jibunAdd.endsWith("대")) {
                                    // "도" 글자가 있는 경우, 맨 끝의 "도" 글자를 제외한 부분을 추출
                                    jibunAdd = jibunAdd.substring(0, jibunAdd.length() - 1);
                                }


                                if (name.contains(keyword) || roadAdd.contains(keyword) || jibunAdd.contains(keyword)) {
                                    String address;

                                    if (roadAdd.equals(" ")) {
                                        address = jibunAdd + "/" + latitude + "/" + longitude;
                                    } else {
                                        address = roadAdd + "/" + latitude + "/" + longitude;
                                    }

                                    address_hash.put(name, address);
                                }
                            }


                        }
                        // 데이터가 준비되었으므로 콜백을 호출합니다.
                        if (dataCallback != null) {
                            dataCallback.onDataLoaded(address_hash);
                        }

                        //Log.d(MotionEffect.TAG, "파싱 성공 데이터 name:" + names); //이 위치에서 ConcurrentModificationException에러 발생

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

    public interface DataCallback {
        void onDataLoaded(HashMap<String,String> address_hash);
    }


}