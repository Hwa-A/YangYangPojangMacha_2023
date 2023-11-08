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

    private ArrayList<MyReviewModel> reviewDatas = new ArrayList<>(); // 리뷰 정보 전송용

    private ArrayList selectShop = new ArrayList<>(); // 가게 정보 탐색용
    private ArrayList selectReview = new ArrayList<>(); // 리뷰 정보 탐색용
    // myReview테이블연결을 위한 DB선어
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;

    public SecondReviewList(String UID, final SecondDataLoadedCallback secondDataLoadedCallback) {

        this.UID = UID;

        databaseReference = firebaseDatabase.getReference("myReview/" + UID);
        Log.d("프로필reviewSecond", "ZeroReviewList: 테스트report 값 myReviews/" + UID);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    // myReview테이블에서 목록 뽑기
                    selectReview.add(snap.getValue() + "/" + snap.getKey());  // 경로 : myReview / 가게ID / 리뷰ID
                    selectShop.add(snap.getValue()); // 경로 : shops / 가게ID
                    Log.d("프로필reviewSecond", "onDataChange: selectReview 경로 :" + selectReview);
                    Log.d("프로필reviewSecond", "onDataChange: selectShop 경로 :" + selectShop);
                    Log.d("프로필reviewSecond", "onDataChange: " + selectReview.size());
                }

                // selectReview 목록에서 리뷰 정보들 가져오기
                for (Object reviewLink : selectReview) {
                    DatabaseReference reviewRef = FirebaseDatabase.getInstance().getReference("reviews/" + reviewLink.toString());//.child(reviewLink.toString());
                    reviewRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if (snapshot.exists()) {
                            MyReviewModel model = new MyReviewModel();
                            Log.d("프로필reviewFirst", "ZeroReviewList: 리뷰 정보 가져오기 ");
                            String shopID_reviewID = (String) reviewLink;
                            String picUrl1 = snapshot.child("picUrl1").getValue(String.class);
                            Float myRating = snapshot.child("rating").getValue(Float.class);
                            String summary = snapshot.child("summary").getValue(String.class);
                            model.setMyReviewModel(shopID_reviewID, picUrl1, myRating, summary);
                            Log.d("프로필reviewFirst", "ZeroReviewList: selectReview 데이터 확인" + model.getSummary());
                            Log.d("프로필reviewFirst", "summary : " + summary);

                            reviewDatas.add(model);
                            Log.d("프로필reviewFirst", "model in summary : " + model.getSummary());
                            Log.d("프로필reviewFirst", "model in summary : 안" + reviewDatas.size());
                            Log.d("프로필reviewFirst", "model in selectShop : 안" + selectShop.size());
                            secondDataLoadedCallback.onSecondDataLoaded(reviewDatas, selectShop, UID);                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }

                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

    }}


 interface SecondDataLoadedCallback {

    void onSecondDataLoaded(ArrayList<MyReviewModel> reviewDatas, ArrayList selectShop, String UID);
}