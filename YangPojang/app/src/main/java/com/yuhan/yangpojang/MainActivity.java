package com.yuhan.yangpojang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.storage.StorageReference;

import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.util.FusedLocationSource;
import com.yuhan.yangpojang.fragment.ReportShopFragment;
import com.yuhan.yangpojang.fragment.ProfileShowFragment;
import android.Manifest;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback
{
    //reportShopFragemnt 에서 사용할 제보버튼
    private Button reportBtn;


    // 임시
    private StorageReference exteriorImageRef;
    private StorageReference menuImageRef;
    private FusedLocationSource locationSource; //안드로이드의 위치 서비스를 이용하여 현재 위치를 가져올 수 있는 클래스
    private NaverMap mNaverMap; //네이버 지도를 표시하고 조작하기 위한 클래스
    private InfoWindow infoWindow;
    private boolean isMapFragmentVisible=true;

    private static final int PERMISSION_REQUEST_CODE = 100; //위치 권한 요청을 처리하기 위한 요청 코드
    private static final String[] PERMISSIONS = { //위치 권한을 요청할 때 필요한 권한 선언
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };


    // 프로그레스 바
    private ProgressBar progressBar;
    private AlertDialog alertDialog;

    BottomNavigationView bottomNavigationView;
    ReportShopFragment reportShopFragment;
    ProfileShowFragment profileShowFragment;
    MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

//        String naverMapsClientId = BuildConfig.NAVER_CLIENT_ID;

        // 프래그먼트 생성
        reportShopFragment = new ReportShopFragment();
        profileShowFragment = new ProfileShowFragment();
        mapFragment = MapFragment.newInstance();


        // 제일 처음 띄워줄 뷰 alertDialogBuilder.setMessage("로딩 중...").setCancelable(false);
        if(savedInstanceState==null)
        {
            mapFragment=MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mapFragment)
                    .commitAllowingStateLoss();
        }
        else
        {
            mapFragment=(MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        }


        // bottomnavigationview의 아이콘을 선택 했을 때 원하는 프래그먼트가 띄워질 수 있도록 리스너를 추가
        bottomNavigationView.setOnItemSelectedListener(
                menuItem -> {
                    int itemId = menuItem.getItemId();
                    if (itemId == R.id.navigation_map) {
                        mapFragment.getMapAsync(this);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, mapFragment)
                                .commitAllowingStateLoss();
                        return true;
                    } else if (itemId == R.id.navigation_report_shop) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, reportShopFragment)
                                .commitAllowingStateLoss();
                        return true;
                    } else if (itemId == R.id.navigation_profile_show) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, profileShowFragment)
                                .commitAllowingStateLoss();
                        return true;
                    }
                    return false;
                });
        FragmentManager fragmentManager = getSupportFragmentManager();
        // 지도가 준비됐을때 onMapReady 호출
        mapFragment.getMapAsync(this);
        locationSource = new FusedLocationSource(this, PERMISSION_REQUEST_CODE);
    }


    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        mNaverMap = naverMap;
        mNaverMap.setLocationSource(locationSource);

        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);

        if (isMapFragmentVisible) {
            UiSettings uiSettings = naverMap.getUiSettings();
            uiSettings.setLocationButtonEnabled(true);
            uiSettings.setScaleBarEnabled(true);
            uiSettings.setCompassEnabled(true);
        }
    }



}