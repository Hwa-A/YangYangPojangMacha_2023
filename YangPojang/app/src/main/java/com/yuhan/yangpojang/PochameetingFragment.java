package com.yuhan.yangpojang;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PochameetingFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pochameeting, container, false);

        // 넘어온 데이터(포차 이름, 회원 ID) 변수에 담기
        String pchName = this.getArguments().getString("pchName");
        String uid = this.getArguments().getString("uid");

        // ▼ 번개 작성 activity에 상단 포차 이름과 회원 ID 값을 주기 위한 코드
        // 객체 생성 및 초기화
        FloatingActionButton meetingWriteFabtn;          // 번개 작성 activity 전환 버튼
        meetingWriteFabtn = (FloatingActionButton) view.findViewById(R.id.fabtn_pochameeting_writeButton);

        // 버튼 클릭 시, MeetingwriteActivity(번개 작성 엑티비티)로 전환
        meetingWriteFabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MeetingwriteActivity.class);
                // intent에 MeetingwriteActivity에 전달할 데이터 추가
                intent.putExtra("pchName", pchName);     // 포차 이름(추후 변경)
                intent.putExtra("uid", uid);         // 회원 ID(추후 변경)
                // Activity로 전환 및 데이터 전달
                startActivity(intent);
            }
        });

        return view;
    }
}
