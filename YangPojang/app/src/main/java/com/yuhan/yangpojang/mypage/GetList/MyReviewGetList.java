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
//    // MyReviewModel 값 전달을 위해 전역 변수 선언
//    MyReviewModel model = new MyReviewModel();
//
//
//    private FirebaseDatabase shopDB, reviewDB = FirebaseDatabase.getInstance();
//    private DatabaseReference shopRef,  reviewRef;
//
//
//    public MyReviewGetList() {       Log.d("프로필review", "MyMeetingGetList: ");    }
//
//    public void getMyReviewModel(String UID, final dataLoadCallback callback){
//        // 여기의 콜백은 전체 객체를 가져가는 콜백
//
//        this.UID = UID;
//
//        //리뷰 정보를 가져올 DB연결
//        reviewRef = reviewDB.getReference("myReview/" + UID);
//
//        // 해당 가게와 리뷰 정보를 리스트 출력
//        reviewRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot snap : snapshot.getChildren()) {
//                    selectReview.add(snap.getValue() + "/" + snap.getKey());  // 경로 : myReview / 가게ID / 리뷰ID
//                    selectShop.add(snap.getValue()); // 경로 : shops / 가게ID
//                    callback.reviewLoad(selectReview, selectShop, shopDatas);
//                    Log.d("프로필review", "onDataChange: selectReview 경로 :" + selectReview);
//                    Log.d("프로필review", "onDataChange: selectShop 경로 :" + selectShop);
//                }
//
//
//
//                for (int i = 0; i < selectReview.size() ; i++) {
//                    MyReviewModel model = new MyReviewModel();
//                    Object review = selectReview.get(i);
//                    Object shopKey = selectShop.get(i);
//
//                    Log.d("프로필review", "model shopName: " + model.getShopName());
//                    Log.d("프로필review", "model summary: " + model.getSummary());
//                    shopDatas.add(settingShopInfo(settingReviews(model,review),shopKey));
//                    Log.d("프로필review", "shopDatas : " + shopDatas.get(i).getShopName());
//                    Log.d("프로필review", "shopDatas : " + shopDatas.get(i).getSummary());
//                }
//                Log.d("프로필review", "model shopName: " + model.getShopName());
//                Log.d("프로필review", "model summary: " + model.getSummary());
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//
////    public MyReviewModel settingReviews(MyReviewModel model, Object review, final dataLoadCallback callback) {
////        // 리뷰 정보 얻기
////        DatabaseReference obReviewRef = FirebaseDatabase.getInstance().getReference("reviews").child(review.toString());
////        obReviewRef.addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                if (snapshot.exists()) {
////                    Log.d("프로필review", "onDataChange: 리뷰 정보 불러오기");
////                    model.setRating(snapshot.child("rating").getValue(Integer.class));
////                    model.setSummary(snapshot.child("summary").getValue(String.class));
////                    model.setPicUrl1(snapshot.child("picUrl1").getValue(String.class));
////                    Log.d("프로필review 가게 정보 얻기", "model summary: " + model.getSummary());
////                    callback.reviewLoad(model);
////
////                }
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError error) {
////
////            }
////        });
////        return model;
////    }
//
//    public MyReviewModel settingShopInfo(){
//        // 가게 정보 얻기
//        DatabaseReference obShopRef = FirebaseDatabase.getInstance().getReference("shop").child(shopKey.toString());
//        obShopRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    Log.d("프로필review", "onDataChange: 가게 정보 불러오기");
//                    String shopName = snapshot.child("shopName").getValue(String.class);
//                    Log.d("프로필review", "model shopName: String" + shopName);
//                    model.setShopName(shopName);
//                    model.setAddressName((snapshot.child("addressName").getValue(String.class)));
//                    model.setCategory(snapshot.child("category").getValue(String.class));
//                    Log.d("프로필review 가게 정보 얻기", "model shopName: " + model.getShopName());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        return model;
//    }
//
//    // callback
//    public interface dataLoadCallback{
//        void listLoad (ArrayList<MyReviewModel> reviewDatas);
//        void reviewLoad(ArrayList selectReview, ArrayList selectShop, ArrayList<MyReviewModel> shopDatas);
//        void shopLoad(MyReviewModel model, ArrayList selectShop, ArrayList<MyReviewModel> shopDatas);
//    }
//
//    // 내 리뷰 목록의 가게, 리뷰 리스트 불러오기 getMyReviewModel -> reviewLoad
//    public void reviewLoad(ArrayList selectReview, ArrayList selectShop, final dataLoadCallback callback){
//
//        if(selectReview != null){
//            for(Object review : selectReview) {
//                // 리뷰 정보 얻기
//                DatabaseReference obReviewRef = FirebaseDatabase.getInstance().getReference("reviews").child(review.toString());
//                obReviewRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            Log.d("프로필review", "onDataChange: 리뷰 정보 불러오기");
//                            model.setRating(snapshot.child("rating").getValue(Integer.class));
//                            model.setSummary(snapshot.child("summary").getValue(String.class));
//                            model.setPicUrl1(snapshot.child("picUrl1").getValue(String.class));
//                            Log.d("프로필review 가게 정보 얻기", "model summary: " + model.getSummary());
//                            callback.shopLoad(model,selectShop, shopDatas);
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//            }
//
//    }
//        }
//
//
//        public void shopLoad (MyReviewModel model, ArrayList selectShop, ArrayList<MyReviewModel> shopDatas){
//
//        for(Object shopKey : selectShop) {
//            // 가게 정보 얻기
//            DatabaseReference obShopRef = FirebaseDatabase.getInstance().getReference("shop").child(shopKey.toString());
//            obShopRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if (snapshot.exists()) {
//                        Log.d("프로필review", "onDataChange: 가게 정보 불러오기");
//                        String shopName = snapshot.child("shopName").getValue(String.class);
//                        Log.d("프로필review", "model shopName: String" + shopName);
//                        model.setShopName(shopName);
//                        model.setAddressName((snapshot.child("addressName").getValue(String.class)));
//                        model.setCategory(snapshot.child("category").getValue(String.class));
//                        Log.d("프로필review 가게 정보 얻기", "model shopName: " + model.getShopName());
//                        shopDatas.add(model);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//        }
//        }
//    }
//
//
//
//
//
//
//
//