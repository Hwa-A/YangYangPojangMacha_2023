package com.yuhan.yangpojang;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;


import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;


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

    public BottomNavigationView bottomNavigationView;
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

        // 액티비티가 처음 실행됐을 때
        if (savedInstanceState == null) {
            // 프래그먼트 생성
            reportShopFragment = new ReportShopFragment();
            homeFragment = new HomeFragment();

            // 앱을 처음 켰을 때 보여지는 화면 -> HomeFragment
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, homeFragment, "homeFragmentTag")
                    .add(R.id.fragment_container, reportShopFragment, "reportShopFragmentTag")
                    .hide(reportShopFragment)  // 리포트 샵 프래그먼트도 처음에 추가하고 숨김
                    .commitAllowingStateLoss(); //트랜잭션 실행
            currentFragment = homeFragment;
        }
        // 액티비티가 재생성되는 경우
        else {
            homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("homeFragmentTag");
            reportShopFragment = (ReportShopFragment) getSupportFragmentManager().findFragmentByTag("reportShopFragmentTag");
            currentFragment = homeFragment;
        }


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

    private long backBtnTime = 0;

    // 홈 프레그먼트의 뒤로가기 버튼 구현
    @Override
    public void onBackPressed() {
        if (currentFragment.equals(homeFragment)) {
            long curTime = System.currentTimeMillis();
            long gapTime = curTime - backBtnTime;
            ConstraintLayout pocha_info = findViewById(R.id.pocha_info);
            RecyclerView pochalist_view = findViewById(R.id.pocha_list);
            pocha_info.setVisibility(View.INVISIBLE);
            pochalist_view.setVisibility(View.INVISIBLE);
            homeFragment.basemap();
            AppCompatButton close_pochalist = findViewById(R.id.close_pochalist);
            close_pochalist.setVisibility(View.INVISIBLE);

            // 뒤로가기 버튼을 두 번 클릭한 경우(2초 내)
            if (0 <= gapTime && 2000 >= gapTime) {
                super.onBackPressed();
            } else {
                backBtnTime = curTime;
                Toast.makeText(this, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            bottomNavigationView.setSelectedItemId(R.id.navigation_map);  // HomeFragment로 이동
        }
    }


}