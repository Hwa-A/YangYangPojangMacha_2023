package com.yuhan.yangpojang;


import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;



import com.naver.maps.map.MapFragment;

import com.yuhan.yangpojang.fragment.HomeFragment;
import com.yuhan.yangpojang.fragment.ReportShopFragment;
import com.yuhan.yangpojang.fragment.ProfileShowFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

// description : 하단 버튼 바를 구성 및 클릭시 각 페이지 Home, Profile, Report로 이동

public class MainActivity extends AppCompatActivity {
    //reportShopFragemnt 에서 사용할 제보버튼
    private Button reportBtn;

    // 프로그레스 바
    private ProgressBar progressBar;
    private AlertDialog alertDialog;

    BottomNavigationView bottomNavigationView;
    ReportShopFragment reportShopFragment;
    ProfileShowFragment profileShowFragment;
    HomeFragment homeFragment;

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
        homeFragment = new HomeFragment();

        // 앱을 처음 켰을 때 보여지는 화면 -> HomeFragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .commitAllowingStateLoss();


        // bottomnavigationview의 아이콘을 선택 했을 때 원하는 프래그먼트가 띄워질 수 있도록 리스너를 추가
        bottomNavigationView.setOnItemSelectedListener(
                menuItem -> {
                    int itemId = menuItem.getItemId();
                    if (itemId == R.id.navigation_map) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, homeFragment)
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

    }


}