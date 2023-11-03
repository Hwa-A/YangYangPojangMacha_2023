//package com.yuhan.yangpojang.mypage.GetList;
//
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.yuhan.yangpojang.mypage.Model.MyReviewModel;
//
//import java.util.ArrayList;
//
//public class MyReviewGetList {
//    private String UID;
//
//    private ArrayList<MyReviewModel> shopDatas = new ArrayList<>(); // 리뷰 정보 전송용
//    private ArrayList selectShop = new ArrayList<>(); // 가게 정보 탐색용
//    private ArrayList selectReview = new ArrayList<>(); // 리뷰 정보 탐색용
//
//    // MyReviewModel 값 전달을 위해 전엽 변수 선언
//    MyReviewModel model = new MyReviewModel();
//
//    int i = 0;
//
//
//    private FirebaseDatabase shopDB, reviewDB = FirebaseDatabase.getInstance();
//    private DatabaseReference shopRef,  reviewRef;
//
//    public MyReviewGetList() {       Log.d("프로필review", "MyMeetingGetList: ");    }
//
//    public void GetMyReviewModel(String UID){
//
//        this.UID = UID;
//
//        //리뷰 정보를 가져올 DB연결
//        reviewRef = reviewDB.getReference("myReview/" + UID);
//
//        reviewRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot snap : snapshot.getChildren()){
//                    selectReview.add(snap.getValue() + "/" + snap.getKey());  // 경로 : myReview / 가게ID / 리뷰ID
//                    selectShop.add(snap.getValue()); // 경로 : shops / 가게ID
//                    Log.d("프로필review", "onDataChange: selectReview 경로 :" + selectReview);
//                    Log.d("프로필review", "onDataChange: selectShop 경로 :" + selectShop);
//                }
//
//
//                for (Object review : selectReview) {
//
//                    // 리뷰 정보 얻기
//                    DatabaseReference obReviewRef = FirebaseDatabase.getInstance().getReference("reviews").child(review.toString());
//                    obReviewRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if(snapshot.exists()){
//                                Log.d("프로필review", "onDataChange: 리뷰 정보 불러오기");
//                                model.setRating(snapshot.child("rating").getValue(Integer.class));
//                                model.setSummary(snapshot.child("summary").getValue(String.class));
//                                model.setPic1(snapshot.child("picUrl1").getValue(String.class));
//                            }
//                        }
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//
//                    // 가게 정보 얻기
//                    DatabaseReference obShopRef = FirebaseDatabase.getInstance().getReference("shop").child(review.toString());
//                    obShopRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if(snapshot.exists()){
//                                Log.d("프로필review", "onDataChange: 가게 정보 불러오기");
//                                String shopName = snapshot.child("shopName").getValue(String.class);
//                                Log.d("프로필review", "model shopName: String" + shopName);
//                                model.setShopName(shopName);
//                                model.setShopAdderss(snapshot.child("addressName").getValue(String.class));
//                                model.setCategory(snapshot.child("category").getValue(String.class));
//                            }
//                        }
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//                    Log.d("프로필review", "model shopName: " + model.getShopName());
//                    Log.d("프로필review", "model summary: " + model.getSummary());
//                    shopDatas.add(model);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }
//
//}
