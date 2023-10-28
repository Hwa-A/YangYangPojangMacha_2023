package com.yuhan.yangpojang.pochaInfo.info;

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

import com.yuhan.yangpojang.R;

public class PochadetailFragment extends Fragment {
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
        View view = inflater.inflate(R.layout.fragment_pochadetail, container, false);


        return view;
    }
}
