package com.yuhan.yangpojang;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


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
    Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);


//        String naverMapsClientId = BuildConfig.NAVER_CLIENT_ID;

        // 프래그먼트 생성
        reportShopFragment = new ReportShopFragment();
        homeFragment = new HomeFragment();

        // 앱을 처음 켰을 때 보여지는 화면 -> HomeFragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, homeFragment)
                .add(R.id.fragment_container, reportShopFragment).hide(reportShopFragment)  // 리포트 샵 프래그먼트도 처음에 추가하고 숨김
                .commitAllowingStateLoss();
        currentFragment = homeFragment;


        // bottomnavigationview의 아이콘을 선택 했을 때 원하는 프래그먼트가 띄워질 수 있도록 리스너를 추가
        bottomNavigationView.setOnItemSelectedListener(
                menuItem -> {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.hide(currentFragment);
                    int itemId = menuItem.getItemId();
                    if (itemId == R.id.navigation_map) {
                        transaction.show(homeFragment);
                        currentFragment = homeFragment;
                    } else if (itemId == R.id.navigation_report_shop) {
                        transaction.show(reportShopFragment);
                        currentFragment = reportShopFragment;
                    } else if (itemId == R.id.navigation_profile_show) {
                        profileShowFragment = new ProfileShowFragment(); // 프로필 쇼 프래그먼트를 매번 새로 생성
                        transaction.add(R.id.fragment_container, profileShowFragment).hide(currentFragment);  // 프로필 쇼 프래그먼트를 추가하고 현재 프래그먼트를 숨김
                        currentFragment = profileShowFragment;
                    }
                    transaction.commit();
                    return true;
                });
    }



}