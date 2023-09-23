package com.yuhan.yangpojang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.database.FirebaseDatabase;


import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    FragmentManager frgManager;       // Fragment 관리자
    FragmentTransaction frgTransaction;        // Fragment 트랜잭션 : Fragment 작업을 처리
    PochareviewFragment pchReviewFrg;      // 포차 리뷰 Fragment
    PochadetailFragment pchReviewFrg2;      // 포차 리뷰 Fragment
Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pchReviewFrg2 = new PochadetailFragment();
        pchReviewFrg = new PochareviewFragment();

        frgManager = getSupportFragmentManager();
        if(frgManager != null) {

            Toast.makeText(this, "DB 오류 발생", Toast.LENGTH_SHORT).show();
        }
        btn = findViewById(R.id.testBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 객체 생성 및 초기화
                frgTransaction = frgManager.beginTransaction();
//        // 포차 리뷰 Fragment로 화면 전환
                frgTransaction.replace(R.id.frg_pochainfo_mainFragment22, pchReviewFrg).commit();

            }
        });

    }
}
