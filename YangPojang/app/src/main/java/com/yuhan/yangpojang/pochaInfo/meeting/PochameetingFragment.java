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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.model.Shop;
import com.yuhan.yangpojang.mypage.Adapter.MyReportShopAdapter;
import com.yuhan.yangpojang.pochaInfo.meeting.MeetingAdapter;
import com.yuhan.yangpojang.pochaInfo.meeting.PochameetingGetList;
import com.yuhan.yangpojang.mypage.GetList.MyLikeShopGetList;
import com.yuhan.yangpojang.mypage.GetList.MyReportShopGetList;
import com.yuhan.yangpojang.mypage.Model.MyLikeShopModel;
import com.yuhan.yangpojang.mypage.Model.MyReportShopModel;
import com.yuhan.yangpojang.pochaInfo.interfaces.OnFragmentReloadListener;
import com.yuhan.yangpojang.pochaInfo.meeting.MeetingwriteActivity;

import java.util.ArrayList;

public class PochameetingFragment extends Fragment {
    Shop shop;      // 포차 정보를 가진 객체
    String uid;     // 회원 id
    private OnFragmentReloadListener onFrgReloadListener;   // 프래그먼트 재실행하는 인터페이스

    private RecyclerView recyv_pochameeting_meeetingList;
    private RecyclerView.Adapter meetAdapter;

    // ▼ 인터페이스 객체 초기화 코드
    // onAttach(): 프래그먼트가 액티비티에 연결될 때 호출되는 콜백 메서드
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnFragmentReloadListener){    // 호스트 액티비티가 해당 인터페이스를 구현한 액티비티인지 확인
            // 현재 연결된 (호스트)액티비티를 형변환해 onFrgReloadListener에 할당
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
                // Activity로 데이터 전달 및 이동
                startActivity(intent);
            }
        });


        recyv_pochameeting_meeetingList = view.findViewById(R.id.recyv_pochameeting_meeetingList);
        recyv_pochameeting_meeetingList.setHasFixedSize(true);
        recyv_pochameeting_meeetingList.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        PochameetingGetList pochameetingGetList = new PochameetingGetList();
        String pochakey = shop.getPrimaryKey();

        pochameetingGetList.getMeetingList(pochakey, new PochameetingGetList.dataLoadedCallback() {
            @Override
            public void onDataLoaded(ArrayList<PochameetingData> mtlist) {
                if (mtlist != null){
                    meetAdapter = new MeetingAdapter(mtlist,getContext());
                    recyv_pochameeting_meeetingList.setAdapter(meetAdapter);
                }
            }
        });


        return view;
    }
}
