package com.yuhan.yangpojang.mypage.GetList.ReviewList;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuhan.yangpojang.mypage.Model.MyReviewModel;

import java.util.ArrayList;

public class SecondReviewList {
    private String UID;

    private ArrayList<MyReviewModel> models = new ArrayList<>(); // 리뷰 정보 전송용


    // myReview테이블연결을 위한 DB선어
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;

    public void getSecondReviewList(String UID, final secondDataLoadedCallback secondDataLoadedCallback) {

        this.UID = UID;

        databaseReference = firebaseDatabase.getReference("myReview/" + UID);
        Log.d("프로필reviewSecond", "ZeroReviewList: 테스트report 값 myReviews/" + UID);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String aaa;
                String bbb;
                ArrayList selectShop = new ArrayList<>(); // 가게 정보 탐색용
                ArrayList selectReview = new ArrayList<>(); // 리뷰 정보 탐색용
                for (DataSnapshot snap : snapshot.getChildren()) {
                    // myReview테이블에서 목록 뽑기
                    aaa = snap.getKey();
                    bbb = snapshot.getKey();
                    selectReview.add(snap.getValue() + "/" + snap.getKey());  // 경로 : myReview / 가게ID / 리뷰ID
                    selectShop.add(snap.getValue()); // 경로 : shops / 가게ID
                    Log.d("프로필reviewSecond", "onDataChange: selectReview 경로 :" + selectReview);
                    Log.d("프로필reviewSecond", "onDataChange: selectShop 경로 :" + selectShop);
                    Log.d("프로필reviewSecond", "onDataChange: " + selectReview.size());
                }
                secondDataLoadedCallback.onSecondDataLoaded(selectReview, selectShop, UID);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface secondDataLoadedCallback{

        void onSecondDataLoaded(ArrayList selectReview, ArrayList selectShop, String UID);
    }
}