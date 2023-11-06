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

public class FirstReviewList {
    private String UID;

    private ArrayList<MyReviewModel> reviewDatas = new ArrayList<>(); // 리뷰 정보 전송용
    private ArrayList selectShop = new ArrayList<>(); // 가게 정보 탐색용
    private ArrayList selectReview = new ArrayList<>(); // 리뷰 정보 탐색용


    public FirstReviewList() {
        Log.d("프로필reviewFirst", "MyMeetingGetList: 진입");
    }

    public void getFirstReviewList(String UID, final FirstDataLoadedCallback firstDataLoadedCallback) {
        SecondReviewList secondReviewList = new SecondReviewList();

        secondReviewList.getSecondReviewList(UID, new SecondReviewList.secondDataLoadedCallback() {
            @Override
            public void onSecondDataLoaded(ArrayList selectReview, ArrayList selectShop, String UID) {
                // selectReview 목록에서 리뷰 정보들 가져오기
                for (int i = 0; i < selectReview.size(); i++) {
                    Object reviewLink = selectReview.get(i);
                    DatabaseReference reviewRef = FirebaseDatabase.getInstance().getReference("reviews").child(reviewLink.toString());

                    reviewRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Log.d("프로필reviewFirst", "ZeroReviewList: 리뷰 정보 가져오기 ");
                                String shopID_reviewID = snapshot.getKey();
                                String picUrl1 = snapshot.child("picUrl1").getValue(String.class);
                                Float myRating = snapshot.child("rating").getValue(Float.class);
                                String summary = snapshot.child("summary").getValue(String.class);
                                MyReviewModel model = new MyReviewModel(shopID_reviewID, picUrl1, myRating, summary);
                                Log.d("프로필reviewFirst", "ZeroReviewList: selectReview 데이터 확인" + model.getSummary());
                                Log.d("프로필reviewFirst", "summary : " + summary);

                                reviewDatas.add(model);
                                Log.d("프로필reviewFirst", "model in summary : " + model.getSummary());
                            }
                            firstDataLoadedCallback.onFirstDataLoaded(reviewDatas, selectShop, UID);
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });


    }

    public interface FirstDataLoadedCallback {

        void onFirstDataLoaded(ArrayList<MyReviewModel> reviewDatas, ArrayList selectShop, String UID);


    }
}