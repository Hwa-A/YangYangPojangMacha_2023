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
//    private String UID;

    private ArrayList<MyReviewModel> reviewDatas = new ArrayList<>(); // 리뷰 정보 전송용

    private ArrayList selectShop = new ArrayList<>(); // 가게 정보 탐색용
    private ArrayList selectReview = new ArrayList<>(); // 리뷰 정보 탐색용

    // myReview테이블연결을 위한 DB선어
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;


    private FirebaseDatabase reviewDB = FirebaseDatabase.getInstance();
    DatabaseReference reviewRef = reviewDB.getInstance().getReference("reviews/");


    private  static int i;

    SecondReviewList(){}
    public SecondReviewList(String UID, final SecondDataLoadedCallback secondDataLoadedCallback) {

        databaseReference = firebaseDatabase.getReference("myReview/" + UID);
        Log.d("프로필reviewSecond", "ZeroReviewList: 테스트report 값 myReviews/" + UID);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewDatas.clear(); // 리스트 초기화
                selectReview.clear(); // 리스트 초기화
                selectShop.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    // myReview테이블에서 목록 뽑기
                    //String RString = (String) snap.getValue() + "/" + snap.getKey();
                    selectReview.add(snap.getValue() + "/" + snap.getKey());  // 경로 : myReview / 가게ID / 리뷰ID
                    selectShop.add(snap.getValue()); // 경로 : shops / 가게ID
                    Log.d("프로필reviewSecond", "onDataChange: selectReview 경로 :" + selectReview);
                    Log.d("프로필reviewSecond", "onDataChange: selectShop 경로 :" + selectShop);
                    Log.d("프로필reviewSecond", "onDataChange: " + selectReview.size());
                }

                reviewRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (i = 0; i < selectShop.size(); i++) {
                            String reviewString = selectReview.get(i).toString();
                            MyReviewModel model = new MyReviewModel();
                            Log.d("프로필reviewFirst", "ZeroReviewList: 리뷰 정보 가져오기 ");

                            String shopID_reviewID = reviewString;
                            String picUrl1 = snapshot.child(reviewString).child( "picUrl1").getValue(String.class);
                            String picUrl2 = snapshot.child(reviewString).child( "picUrl2").getValue(String.class);
                            String picUrl3 = snapshot.child(reviewString).child( "picUrl3").getValue(String.class);
                            Log.d("사진", "onDataChange: " + picUrl1);
                            Float myRating = snapshot.child(reviewString).child("rating").getValue(Float.class);
                            String summary = snapshot.child(reviewString).child( "summary").getValue(String.class);

                            Log.d("프로필reviewFirst", "ZeroReviewList: 리뷰 정보 가져오기 ㅡㅡㅡㅡ " + picUrl1 );
                            Log.d("프로필reviewFirst", "ZeroReviewList: 리뷰 정보 가져오기 ㅡㅡㅡㅡ " + myRating );
                            Log.d("프로필reviewFirst", "ZeroReviewList: 리뷰 정보 가져오기 ㅡㅡㅡㅡ " + summary );

                            model.setMyReviewModel( shopID_reviewID,  picUrl1,  picUrl2,  picUrl3,  myRating,  summary);
                            Log.d("프로필reviewFirst", "ZeroReviewList: selectReview 데이터 확인" + model.getSummary());
                            Log.d("프로필reviewFirst", "i ㅡㅡㅡㅡㅡㅡㅡㅡ " + i);

                            reviewDatas.add(model);
                        }
                        secondDataLoadedCallback.onSecondDataLoaded(reviewDatas, selectShop, UID);
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });
            }
            //                }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }



        });


    }
    public interface SecondDataLoadedCallback {

        void onSecondDataLoaded(ArrayList<MyReviewModel> reviewDatas, ArrayList selectShop, String UID);
    }

}
