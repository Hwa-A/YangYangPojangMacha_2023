package com.yuhan.yangpojang.pochaInfo.info;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.model.Shop;
import com.yuhan.yangpojang.model.Store;
import com.yuhan.yangpojang.pochaInfo.interfaces.OnFragmentReloadListener;
import com.yuhan.yangpojang.pochaInfo.meeting.PochameetingFragment;
import com.yuhan.yangpojang.pochaInfo.review.PochareviewFragment;

import java.io.Serializable;

// pch: pojangmacha
// frg: fragment
// tv: TextView
public class PochainfoActivity extends AppCompatActivity implements Serializable, OnFragmentReloadListener {
    PochadetailFragment pchDetailFrg;      // 포차 상세정보 Fragment
    PochareviewFragment pchReviewFrg;      // 포차 리뷰 Fragment
    PochameetingFragment pchMeetingFrg;        // 포차 번개 Fragment
    Button pchDetailBtn;                   // 포차 상세정보 Button
    Button pchReviewBtn;                   // 포차 리뷰 Button
    Button pchMeetingBtn;                   // 포차 번개 Button
    FragmentManager frgManager;             // Fragment 관리자
    FragmentTransaction frgTransaction;     // Fragment 트랜잭션 : Fragment 작업을 처리
    private Shop shop;          // 포차 정보를 담을 객체
//    FirebaseDatabase ref = FirebaseDatabase.getInstance();
//    DatabaseReference shops = ref.getReference("shops");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pochainfo);

        // 객체 생성 및 초기화
        pchDetailBtn = findViewById(R.id.btn_pochainfo_detailTab);
        pchReviewBtn = findViewById(R.id.btn_pochainfo_reviewTab);
        pchMeetingBtn = findViewById(R.id.btn_pochainfo_meetingTab);
        pchDetailFrg = new PochadetailFragment();
        pchReviewFrg = new PochareviewFragment();
        pchMeetingFrg = new PochameetingFragment();
        frgManager = getSupportFragmentManager();
        TextView pchNameTv = findViewById(R.id.tv_pochainfo_pochaname);  // 포차 이름 TextView

        // ▼ HomeFragment에서 전달 받은 포차 객체 받아 처리
        Intent intent = getIntent();
        if(intent != null){  // Serializable(객체 직렬화): 객체를 바이트로 저장하는 자바의 인터페이스
            shop = (Shop) intent.getSerializableExtra("shopInfo");  // 직렬화된 객체 수신
            String pchName = shop.getShopName(); // 포차 이름 얻기
            // 포차 이름 변경
            pchNameTv.setText(pchName);
        } else {
            Toast.makeText(this, "해당 가게를 찾을 수 없습니다.", Toast.LENGTH_LONG).show();
        }

        // uid 임의 값 넣어 테스트(추후 삭제 예정)
        String uid = "롤로";

        // ▼ fragment에 데이터 전달 코드       // Bundle: Map형태로 여러가지의 타입의 값을 저장하는 클래스
        Bundle bundle = new Bundle();               // 전달하기 위해 포차 객체와 회원ID 담을 객체
        bundle.putSerializable("shopInfo", shop);   // 포차 객체
        bundle.putString("uid", uid);               // 회원 id
        // 프래그먼트에 포차 객체 넘기기
        pchDetailFrg.setArguments(bundle);          // 포차 상세 정보 프래그먼트에 전달
        pchReviewFrg.setArguments(bundle);          // 포차 리뷰 프래그먼트에 전달
        pchMeetingFrg.setArguments(bundle);         // 포차 번개 프래그먼트에 전달

        // 탭 버튼 클릭 시, 화면 전환 - 포차 상세 정보, 리뷰 리스트, 번개 리스트
        pchDetailBtn.setOnClickListener(onClickListener);       // 포차 상세 정보
        pchReviewBtn.setOnClickListener(onClickListener);       // 포차 리뷰 리스트
        pchMeetingBtn.setOnClickListener(onClickListener);      // 포차 번개 리스트
//
//        // ▼ 처음에 포차 상세 정보 Fragment를 보여주기 위한 코드
//        // Fragment 트랜잭션 객체 생성 및 초기화
//        frgTransaction = frgManager.beginTransaction();
//        // 포차 상세정보 Fragment로 화면 전환
//        frgTransaction.replace(R.id.frg_pochainfo_mainFragment, pchDetailFrg).commitNow();

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // v.getResources().getResourceEntryName(v.getId()) : id의 이름(문자)을 반환
            switch (v.getResources().getResourceEntryName(v.getId())) {
                case "btn_pochainfo_detailTab":
                    // 객체 생성 및 초기화
                    frgTransaction = frgManager.beginTransaction();
                    // 포차 상세정보 Fragment로 화면 전환
                    frgTransaction.replace(R.id.frg_pochainfo_mainFragment, pchDetailFrg).commit();
                    // 버튼 탭의 색 변경
                    pchDetailBtn.setTextColor(Color.WHITE);
                    pchDetailBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_selected);
                    pchReviewBtn.setTextColor(Color.BLACK);
                    pchReviewBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_unselected);
                    pchMeetingBtn.setTextColor(Color.BLACK);
                    pchMeetingBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_unselected);
                    break;
                case "btn_pochainfo_reviewTab":
                    // 객체 생성 및 초기화
                    frgTransaction = frgManager.beginTransaction();
                    // 포차 리뷰 Fragment로 화면 전환
                    frgTransaction.replace(R.id.frg_pochainfo_mainFragment, pchReviewFrg).commit();
                    // 버튼 탭의 색 변경
                    pchDetailBtn.setTextColor(Color.BLACK);
                    pchDetailBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_unselected);
                    pchReviewBtn.setTextColor(Color.WHITE);
                    pchReviewBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_selected);
                    pchMeetingBtn.setTextColor(Color.BLACK);
                    pchMeetingBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_unselected);
                    break;
                case "btn_pochainfo_meetingTab":
                    // 객체 생성 및 초기화
                    frgTransaction = frgManager.beginTransaction();
                    // 포차 번개 Fragment로 화면 전환
                    frgTransaction.replace(R.id.frg_pochainfo_mainFragment, pchMeetingFrg).commit();
                    // 버튼 탭의 색 변경
                    pchDetailBtn.setTextColor(Color.BLACK);
                    pchDetailBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_unselected);
                    pchReviewBtn.setTextColor(Color.BLACK);
                    pchReviewBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_unselected);
                    pchMeetingBtn.setTextColor(Color.WHITE);
                    pchMeetingBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_selected);
                    break;
            }
        }
    };

    @Override
    public void onFragmentReload(String frgName) {
        if(frgManager != null) {
            switch (frgName){
                case "pchDatail":
                    // fragment transaction 객체 생성 및 초기화
                    frgTransaction = frgManager.beginTransaction();
                    // 포차 상세정보 Fragment로 화면 전환
                    frgTransaction.replace(R.id.frg_pochainfo_mainFragment, pchDetailFrg).commit();
                    break;
                case "pchReview":
                    // fragment transaction 객체 생성 및 초기화
                    frgTransaction = frgManager.beginTransaction();
                    // 포차 리뷰 Fragment로 화면 전환
                    frgTransaction.replace(R.id.frg_pochainfo_mainFragment, pchReviewFrg).commit();
                    break;
                case "pchMeeting":
                    // fragment transaction 객체 생성 및 초기화
                    frgTransaction = frgManager.beginTransaction();
                    // 포차 번개 Fragment로 화면 전환
                    frgTransaction.replace(R.id.frg_pochainfo_mainFragment, pchMeetingFrg).commit();
                    break;
            }

        }
    }
}