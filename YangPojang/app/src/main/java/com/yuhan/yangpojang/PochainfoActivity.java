package com.yuhan.yangpojang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

// pch: pojangmacha
// frg: fragment
public class PochainfoActivity extends AppCompatActivity {
    PochadetailFragment pchDetailFrg;      // 포차 상세정보 Fragment
    PochareviewFragment pchReviewFrg;      // 포차 리뷰 Fragment
    Button pchDetailBtn;                   // 포차 상세정보 Button
    Button pchReviewBtn;                   // 포차 리뷰 Button
    FragmentManager frgManager;       // Fragment 관리자
    FragmentTransaction frgTransaction;        // Fragment 트랜잭션 : Fragment 작업을 처리
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pochainfo);

        // 객체 생성 및 초기화
        pchDetailBtn = (Button) findViewById(R.id.btn_pochainfo_detailTab);
        pchReviewBtn = (Button) findViewById(R.id.btn_pochainfo_reviewTab);
        pchDetailFrg = new PochadetailFragment();
        pchReviewFrg = new PochareviewFragment();
        frgManager = getSupportFragmentManager();

        pchDetailBtn.setOnClickListener(onClickListener);
        pchReviewBtn.setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // v.getResources().getResourceEntryName(v.getId()) : id의 이름(문자)을 반환
            switch (v.getResources().getResourceEntryName(v.getId())) {
                case "btn_pochainfo_detailTab":
                    // 객체 생성 및 초기화
                    frgTransaction = frgManager.beginTransaction();
                    // 해당 Fragment로 화면 전환
                    frgTransaction.replace(R.id.frg_pochainfo_mainFragment, pchDetailFrg).commit();
                    break;
                case "btn_pochainfo_reviewTab":
                    // 객체 생성 및 초기화
                    frgTransaction = frgManager.beginTransaction();
                    // 해당 Fragment로 화면 전환
                    frgTransaction.replace(R.id.frg_pochainfo_mainFragment, pchReviewFrg).commit();
                    break;
            }
        }
    };
}