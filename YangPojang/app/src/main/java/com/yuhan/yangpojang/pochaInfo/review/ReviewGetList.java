package com.yuhan.yangpojang.pochaInfo.review;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuhan.yangpojang.pochaInfo.model.ReviewDTO;
import com.yuhan.yangpojang.pochaInfo.model.ReviewListModel;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ReviewGetList {
    private String pchKey;      // 포차 id
    private ArrayList<ReviewListModel> reviewDatas = new ArrayList<>();  // 리뷰 리스트 객체를 담을 리스트
    private ArrayList<ReviewDTO> reviewList = new ArrayList<>();    // 해당 포차의 리뷰 객체를 담을 리스트

    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();  // firebase 참조 객체

    public  void GetReviewList(String pchKey, final ReviewGetList.reviewDataLoadCallback reviewCallback){
        this.pchKey = pchKey;   // 포차 id 연결

        // firebase에서 데이터 가져오기
        ref.child("reviews/"+pchKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // 리스트 초기화
                reviewList.clear();
                reviewDatas.clear();
                Log.e("test1", "getList: 초기화");

                // firebase에서 해당 포차의 리뷰 데이터 가져오기
                if(snapshot.exists()){
                    for (DataSnapshot snap : snapshot.getChildren()){
                        ReviewDTO review = snap.getValue(ReviewDTO.class);
                        reviewList.add(review);     // 리뷰 리스트에 해당 포차의 리뷰들 저장
                        Log.e("test1", "=======================");
                        Log.e("test1", "리뷰id: "+review.getUid());
                        Log.e("test1", "사진: "+review.getPicUrl1());
                    }
                    Collections.reverse(reviewList);    // 역순으로 재정렬
                    Log.e("test1", "getList: 리뷰 데이터 다 가져옴");

                    // reviewList의 uid를 얻어 회원 닉네임 가져오기
                    for(ReviewDTO review : reviewList){
                        String uid = review.getUid();   // 리뷰 작성한 회원 id 얻기
                        ref.child("user-info/"+uid+"/user_Nickname").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    String userNickName = snapshot.getValue(String.class);  // 회원 닉네임 가져오기

                                    ReviewListModel reviewListmodel = new ReviewListModel();

                                    // 회원 id
                                    String uid = review.getUid();
                                    reviewListmodel.setUid(uid);
                                    // 회원 닉네임
                                    reviewListmodel.setUserName(userNickName);
                                    // 별점
                                    float rating = review.getRating();
                                    reviewListmodel.setRating(rating);
                                    // 내용
                                    String summary = review.getSummary();
                                    reviewListmodel.setSummary(summary);
                                    // 등록 날짜(ex. 2023/11/03)
                                    String yearDate = review.getYearDate();
                                    reviewListmodel.setYearDate(yearDate);
                                    // 이미지1
                                    if(review.getPicUrl1() != null){
                                        String picUrl1 = review.getPicUrl1();
                                        reviewListmodel.setPicUrl1(picUrl1);
                                    }
                                    // 이미지2
                                    if(review.getPicUrl2() != null){
                                        String picUrl2 = review.getPicUrl2();
                                        reviewListmodel.setPicUrl2(picUrl2);
                                    }
                                    // 이미지3
                                    if(review.getPicUrl3() != null){
                                        String picUrl3 = review.getPicUrl3();
                                        reviewListmodel.setPicUrl3(picUrl3);
                                    }
                                    // ReviewListModel를 리스트에 추가
                                    reviewDatas.add(reviewListmodel);
                                    Log.e("test1", "리뷰 리스트 추가");
                                }
                                    reviewCallback.onReviewDataLoad(reviewDatas);
                                    Log.e("test1", "추가됨");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // 에러 메시지 출력
                                Log.e("test1", "에러 메시지: "+error.getMessage());
                            }
                        });
                    }
                }
                // 데이터가 변경되었음을 Adapter에 알림
//            carAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface reviewDataLoadCallback{
        void onReviewDataLoad(ArrayList<ReviewListModel> reviewDatas);
    }

}
