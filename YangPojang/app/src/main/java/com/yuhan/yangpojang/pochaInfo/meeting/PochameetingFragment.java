package com.yuhan.yangpojang.pochaInfo.meeting;

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
import com.yuhan.yangpojang.pochaInfo.meeting.MeetingwriteActivity;

public class PochameetingFragment extends Fragment {
    String pchName;          // 포차 이름
    String uid;             // 회원 id

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ▼ PochainfoActivity.java에서 전달한 데이터(포차 이름, 회원id) 받는 코드
        Bundle bundle = getArguments();
        if(bundle != null){
            pchName = bundle.getString("pchName"); // 포차 이름
            uid = bundle.getString("uid"); // 회원 id
            Log.e("test1", pchName);

        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pochameeting, container, false);

;
//        // ▼ 번개 작성 activity에 상단 포차 이름과 회원 ID 값을 주기 위한 코드
//        // 객체 생성 및 초기화
//        FloatingActionButton meetingWriteFabtn;          // 번개 작성 activity 전환 버튼
//        meetingWriteFabtn = (FloatingActionButton) view.findViewById(R.id.fabtn_pochameeting_writeButton);
//
//        // 버튼 클릭 시, MeetingwriteActivity(번개 작성 엑티비티)로 전환
//        meetingWriteFabtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), MeetingwriteActivity.class);
//                // intent에 MeetingwriteActivity에 전달할 데이터 추가
//                intent.putExtra("pchName", pchName);     // 포차 이름(추후 변경)
//                // intent.putExtra("uid", uid);         // 회원 ID(추후 변경)
//                // Activity로 전환 및 데이터 전달
//                startActivity(intent);
//            }
//        });

        return view;
    }
}
