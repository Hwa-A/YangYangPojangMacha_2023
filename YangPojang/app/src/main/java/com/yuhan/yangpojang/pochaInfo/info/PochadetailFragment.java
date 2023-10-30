package com.yuhan.yangpojang.pochaInfo.info;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.model.Shop;
import com.yuhan.yangpojang.pochaInfo.interfaces.OnFragmentReloadListener;

public class PochadetailFragment extends Fragment {
    Shop shop;      // 포차 정보를 가진 객체
    String uid;     // 회원 id
    private OnFragmentReloadListener onFrgReloadListener;   // 프래그먼트 재실행하는 인터페이스

    // ▼ 인터페이스 객체 초기화 코드
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
        View view = inflater.inflate(R.layout.fragment_pochadetail, container, false);

        // ▼ PochainfoActivity.java에서 전달한 데이터를 받는 코드
        Bundle bundle = getArguments();
        if(bundle != null){
            shop = (Shop)bundle.getSerializable("shopInfo");    // 포차 객체 초기화
            uid = (String) bundle.getString("uid");             // 회원 id 초기화
        }else {
            // bundle이 null인 경우, 프래그먼트 재실행
            onFrgReloadListener.onFragmentReload("pchDatail");
            return view;
        }

        return view;
    }
}
