package com.yuhan.yangpojang.pochaInfo.meeting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.model.Shop;
import com.yuhan.yangpojang.pochaInfo.interfaces.OnFragmentReloadListener;
import com.yuhan.yangpojang.pochaInfo.meeting.MeetingwriteActivity;

public class PochameetingFragment extends Fragment {
    Shop shop;      // 포차 정보를 가진 객체
    String uid;     // 회원 id
    private OnFragmentReloadListener onFrgReloadListener;   // 프래그먼트 재실행하는 인터페이스

    // ▼ 인터페이스 객체 초기화 코드
    // onAttach(): 프래그먼트가 액티비티에 연결될 때 호출되는 콜백 메서드
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnFragmentReloadListener){
            onFrgReloadListener = (OnFragmentReloadListener) context;   // 초기화
        }else {
            // 에러 처리
            throw new RuntimeException(context.toString() + "must implement OnFragmentReloadListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pochameeting, container, false);

        // ▼ PochainfoActivity.java에서 전달한 데이터를 받는 코드
        Bundle bundle = getArguments();
        if(bundle != null){
            shop = (Shop)bundle.getSerializable("shopInfo");    // 포차 객체 초기화
            uid = (String)bundle.getString("uid");             // 회원 id 초기화
        }else {
            // bundle이 null인 경우, 프래그먼트 재실행
            onFrgReloadListener.onFragmentReload("pchMeeting");
            return view;
        }

        // ▼ 번개 작성 페이지(MeetingwriteActivity)로 데이터 전달 및 이동 코드
        // 객체 생성 및 초기화
        FloatingActionButton meetingWriteFabtn;          // 번개 작성 버튼
        meetingWriteFabtn = (FloatingActionButton) view.findViewById(R.id.fabtn_pochameeting_writeButton);
        // 버튼 클릭한 경우
        meetingWriteFabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MeetingwriteActivity.class);
                // intent에 MeetingwriteActivity에 전달할 데이터 추가
                intent.putExtra("pchKey", shop.getPrimaryKey());    // 포차 고유키
                intent.putExtra("pchName", shop.getShopName());     // 포차 이름
                intent.putExtra("uid", uid);        // 회원 id
                // Activity로 데이터 전달 및 이동
                startActivity(intent);
            }
        });


        return view;
    }
}
