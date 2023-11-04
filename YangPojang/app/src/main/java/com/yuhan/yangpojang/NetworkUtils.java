package com.yuhan.yangpojang;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;

// 네트워크 연결 생태를 확인하는 클래스
public class NetworkUtils {
    // 인터넷에 연결된 경우 true, 연결되지 않은 경우 false 반환
    public static boolean isNetworkAvailable(Context context) {
        // 시스템의 네트워크 연결 관리자를 가져옴
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            // 현재 활성 네트워크 기능을 가져옴(이용해 현재 연결된 네트워크의 특성 확인 가능)
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            // networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            // : 현재 네트워트가 인터넷에 연결된 경우 true 반환, 연결되지 않은 경우  false 반환
            return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        }
        return false;
    }
}
