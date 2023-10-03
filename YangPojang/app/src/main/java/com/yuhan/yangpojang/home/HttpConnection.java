package com.yuhan.yangpojang.home;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

//싱글톤 패턴 : 어떤 클래스가 최초 한 번만 메모리에 할당되고 그 객체를 여러 곳에서 공유하여 사용하는 패턴
public class HttpConnection {

    private final OkHttpClient okClient; //OkHttpClient 인스턴스를 저장하는 변수, HTTP 요청 수행
    private static final HttpConnection instance = new HttpConnection(); // HttpConnection클래스의 Singleton 인스턴스를 저장하는 변수, 이를 통해 앱 전체에서 동일한 HttpConnection 인스턴스에 접근할 수 있음
    public static HttpConnection getInstance(){
        return instance;
    }

    private HttpConnection(){
        this.okClient = new OkHttpClient();
    } // OkHttpClient 인스턴스 초기화

    //웹 서버로 GET요청을 보내는 메서드
    public void requestWebServer(String keyword, String refrnUrl, String onePageRows, String currentPage, String apikey, String target, Callback callback){
        String url = "https://map.ngii.go.kr/openapi/search.json" +
                "?keyword=" + keyword +
                "&refrnUrl=" + refrnUrl +
                "&onePageRows=" + onePageRows +
                "&currentPage=" + currentPage +
                "&apikey=" + apikey +
                "&target=" + target;

        Request request = new Request.Builder() //HTTP요청 객체 생성
                .url(url)
                .get()
                .build();

        okClient.newCall(request).enqueue(callback); // 비동기적으로 요청 전달, 응답 처리하기 위한 콜백 등록
        // callback객체는 HTTP요청이 돤료되었을 때 실행된다(->성공 : onResponse메서드 호출, ->실패 : onFailure메서드 호출)
    }

}



