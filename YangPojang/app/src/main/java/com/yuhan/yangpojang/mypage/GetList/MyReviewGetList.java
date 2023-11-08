package com.yuhan.yangpojang.mypage.GetList;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuhan.yangpojang.mypage.Model.MyReviewModel;

import java.util.ArrayList;

public class MyReviewGetList {

    private ArrayList<String> selectReview =new ArrayList<String>();
     ArrayList<String> selectShop = new ArrayList<String>();

//    private
    private ArrayList<MyReviewModel> myShopDatas = new ArrayList<>(); // 가게 정보

    private ArrayList<MyReviewModel> allDatas = new ArrayList<>(); // 전체 정보

    private static ArrayList<MyReviewModel> myReviewDatas = new ArrayList<>(); // 리뷰 정보


    int i = 0;

    // 생성자
    public void setReviewAdapter(String UID) {
        Log.d("테스트MyReviewList", "setReviewAdapter: 시작");
        FirebaseDatabase firstDB = FirebaseDatabase.getInstance();
        DatabaseReference firstRef = firstDB.getReference("myReview/" + UID);
        Log.d("테스트MyReviewList", "setReviewAdapter: firstRef myReview/" + UID );
        firstRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot myLists) {
                // myReview 목록 값 가져오기
                for (DataSnapshot myList : myLists.getChildren()){
                    String selectReviewLink = myList.getValue() + "/" + myList.getKey();
                    String selectShopLink = myList.getValue().toString();
                    selectReview.add(selectReviewLink);  // 경로 : myReview / 가게ID / 리뷰ID
                    selectShop.add(selectShopLink); // 경로 : shops / 가게ID
                    Log.d("테스트MyReviewList", "firstRef onDataChange: add");
                    Log.d("테스트MyReviewList", "firstRef onDataChange: add" + selectReview.size());
                }


                Log.d("테스트MyReviewList", "firstRef onDataChange: 시작");
                Log.d("테스트MyReviewList", "firstRef onDataChange: 시작" + selectReview.size());


                for (Object oneReview : selectReview){
                    // 리뷰데이터 가져오기
                    DatabaseReference secondReviewRef = FirebaseDatabase.getInstance().getReference("reviews/" + oneReview);//.child(oneReview.toString());
                    Log.d("테스트MyReviewList", "OneReview: secondReviewRef " + secondReviewRef);
                    secondReviewRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot oneReviewSnapshot) {
                            Log.d("테스트MyReviewList", "secondReviewRef onDataChange: 시작");
//                                if (oneReviewSnapshot.exists()) {
                                    MyReviewModel model = new MyReviewModel();
                                    String shopID_reviewID = (String)oneReview; //oneReviewSnapshot.getKey();
                                    String picUrl1 = oneReviewSnapshot.child("picUrl1").getValue(String.class);
                                    Float myRating = oneReviewSnapshot.child("rating").getValue(Float.class);
                                    String summary = oneReviewSnapshot.child("summary").getValue(String.class);
                                    model.setMyReviewModel(shopID_reviewID, picUrl1, myRating, summary);

                                    myReviewDatas.add(model);
                            Log.d("테스트MyReviewList", "myReviewDatas.add(model) : " + myReviewDatas.size());
                            Log.d("테스트MyReviewList", "myReviewDatas.add(model) : " + myReviewDatas.get(i).getPicUrl1());
                            Log.d("테스트MyReviewList", "myReviewDatas.add(model) : " + myReviewDatas.get(i).getRating());
                            Log.d("테스트MyReviewList", "myReviewDatas.add(model) : " + myReviewDatas.get(i).getSummary());
                            i++;
                            for (Object oneShop : selectShop){
                                DatabaseReference thirdShopRef = FirebaseDatabase.getInstance().getReference("shops/" + oneShop);
                                thirdShopRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot oneShopSnapshot) {
                                        MyReviewModel model = new MyReviewModel();
                                        Log.d("프로필reviewGetList", "프로필reviewGetList: 가게 정보 가져오기 ");
                                        String category = oneShopSnapshot.child("category").getValue(String.class);
                                        String addressName = oneShopSnapshot.child("addressName").getValue(String.class);
                                        String shopName = oneShopSnapshot.child("shopName").getValue(String.class);
                                        String exteriorImagePath = oneShopSnapshot.child("exteriorImagePath").getValue(String.class);
                                        String geohash = oneShopSnapshot.child("geohash").getValue(String.class);
                                        boolean hasMeeting = oneShopSnapshot.child("hasMeeting").getValue(Boolean.class);
                                        double latitude = oneShopSnapshot.child("latitude").getValue(Double.class);
                                        double longitude = oneShopSnapshot.child("longitude").getValue(Double.class);
                                        String menuImageUri = oneShopSnapshot.child("menuImageUri").getValue(String.class);
                                        boolean openFri = oneShopSnapshot.child("openFri").getValue(boolean.class);
                                        boolean openMon = oneShopSnapshot.child("openMon").getValue(boolean.class);
                                        boolean openSat = oneShopSnapshot.child("openSat").getValue(boolean.class);
                                        boolean openSun = oneShopSnapshot.child("openSun").getValue(boolean.class);
                                        boolean openThu = oneShopSnapshot.child("openThu").getValue(boolean.class);
                                        boolean openTue = oneShopSnapshot.child("openTue").getValue(boolean.class);
                                        boolean openWed = oneShopSnapshot.child("openWed").getValue(boolean.class);
                                        boolean pwayAccount = oneShopSnapshot.child("pwayAccount").getValue(boolean.class);
                                        boolean pwayCard = oneShopSnapshot.child("pwayCard").getValue(boolean.class);
                                        boolean pwayCash = oneShopSnapshot.child("pwayCash").getValue(boolean.class);
                                        boolean pwayMobile = oneShopSnapshot.child("pwayMobile").getValue(boolean.class);
                                        float rating = oneShopSnapshot.child("rating").getValue(float.class);
                                        String storeImageUri = oneShopSnapshot.child("storeImageUri").getValue(String.class);
                                        String uid = oneShopSnapshot.child("uid").getValue(String.class);
                                        boolean isVerified = oneShopSnapshot.child("verified").getValue(boolean.class);

                                        model.setUid(uid);
                                        model.setShopName(shopName);
                                        model.setLatitude(latitude);
                                        model.setLongitude(longitude);
                                        model.setAddressName(addressName);
                                        model.setPwayMobile(pwayMobile);
                                        model.setPwayCard(pwayCard);
                                        model.setPwayAccount(pwayAccount);
                                        model.setPwayCard(pwayCash);
                                        model.setOpenMon(openMon);
                                        model.setOpenTue(openTue);
                                        model.setOpenWed(openWed);
                                        model.setOpenThu(openThu);
                                        model.setOpenFri(openFri);
                                        model.setOpenSat(openSat);
                                        model.setOpenSun(openSun);
                                        model.setCategory(category);
                                        model.setStoreImageUri(storeImageUri);
                                        model.setMenuImageUri(menuImageUri);
                                        model.setVerified(isVerified);
                                        model.setHasMeeting(hasMeeting);
                                        model.setRating(rating);
                                        model.setGeohash(geohash);
                                        model.setExteriorImagePath(exteriorImagePath);

//                    model.setShopID_reviewID(allReviewDatas.get(i).getShopID_reviewID());
//                    model.setPicUrl1(allReviewDatas.get(i).getPicUrl1());
//                    model.setRating(allReviewDatas.get(i).getRating());
//                    model.setSummary(allReviewDatas.get(i).getSummary());

//                                    myShopDatas.add(model);
//                    Log.d("테스트MyReviewList", "myShopDatas.add(model) : " + allDatas.size());
//                    Log.d("테스트MyReviewList", "myShopDatas.add(model) : " + allDatas.get(i).getShopName());
//                    Log.d("테스트MyReviewList", "myReviewDatas.add(model) : " + allDatas.get(i).getSummary());
//                    i++;
                                        Log.d("테스트MyReviewListThread", "onDataChange: Thread" + myShopDatas.size());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                        }
//                        }

                        // secondReviewRef
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }

                    });

                    Log.d("테스트MyReviewList", "secondReviewRef onDataChange: for문 끝");
                } // secondReviewRef for문

                Log.d("테스트MyReviewList", "firstRef onDataChange: for문");
            } //firstRef for문


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });

        Log.d("테스트MyReviewList", "setReviewAdapter: 끝");
    }

}
//
//class MyRunnable implements Runnable {
//
//    ArrayList<MyReviewModel> myShopDatas;
//    ArrayList<String> selectShop;
//
//    ArrayList<MyReviewModel> allReviewDatas = new ArrayList<MyReviewModel>();
//
//    public MyRunnable(ArrayList<String> selectShop) {
////        this.myShopDatas = myShopDatas;
//        this.selectShop = selectShop;
//    }
//
//    @Override
//    public void run() {
//        ArrayList<MyReviewModel> allDatas = new ArrayList<MyReviewModel>();
//        int i = 0;
//        for (Object oneshop : selectShop) {
//            DatabaseReference thirdShopRef = FirebaseDatabase.getInstance().getReference("shops/" + oneshop);//.child(Oneshop.toString());
//            Log.d("테스트MyReviewList", "OneReview: thirdShopRef :" + thirdShopRef);
//            thirdShopRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot oneShopSnapshot) {
//
//                    Log.d("테스트MyReviewList", "thirdShopRef onDataChange: 시작");
////                            if (oneShopSnapshot.exists()) {
//                    MyReviewModel model = new MyReviewModel();
//                    Log.d("프로필reviewGetList", "프로필reviewGetList: 가게 정보 가져오기 ");
//                    String category = oneShopSnapshot.child("category").getValue(String.class);
//                    String addressName = oneShopSnapshot.child("addressName").getValue(String.class);
//                    String shopName = oneShopSnapshot.child("shopName").getValue(String.class);
//                    String exteriorImagePath = oneShopSnapshot.child("exteriorImagePath").getValue(String.class);
//                    String geohash = oneShopSnapshot.child("geohash").getValue(String.class);
//                    boolean hasMeeting = oneShopSnapshot.child("hasMeeting").getValue(Boolean.class);
//                    double latitude = oneShopSnapshot.child("latitude").getValue(Double.class);
//                    double longitude = oneShopSnapshot.child("longitude").getValue(Double.class);
//                    String menuImageUri = oneShopSnapshot.child("menuImageUri").getValue(String.class);
//                    boolean openFri = oneShopSnapshot.child("openFri").getValue(boolean.class);
//                    boolean openMon = oneShopSnapshot.child("openMon").getValue(boolean.class);
//                    boolean openSat = oneShopSnapshot.child("openSat").getValue(boolean.class);
//                    boolean openSun = oneShopSnapshot.child("openSun").getValue(boolean.class);
//                    boolean openThu = oneShopSnapshot.child("openThu").getValue(boolean.class);
//                    boolean openTue = oneShopSnapshot.child("openTue").getValue(boolean.class);
//                    boolean openWed = oneShopSnapshot.child("openWed").getValue(boolean.class);
//                    boolean pwayAccount = oneShopSnapshot.child("pwayAccount").getValue(boolean.class);
//                    boolean pwayCard = oneShopSnapshot.child("pwayCard").getValue(boolean.class);
//                    boolean pwayCash = oneShopSnapshot.child("pwayCash").getValue(boolean.class);
//                    boolean pwayMobile = oneShopSnapshot.child("pwayMobile").getValue(boolean.class);
//                    float rating = oneShopSnapshot.child("rating").getValue(float.class);
//                    String storeImageUri = oneShopSnapshot.child("storeImageUri").getValue(String.class);
//                    String uid = oneShopSnapshot.child("uid").getValue(String.class);
//                    boolean isVerified = oneShopSnapshot.child("verified").getValue(boolean.class);
//
//                    model.setUid(uid);
//                    model.setShopName(shopName);
//                    model.setLatitude(latitude);
//                    model.setLongitude(longitude);
//                    model.setAddressName(addressName);
//                    model.setPwayMobile(pwayMobile);
//                    model.setPwayCard(pwayCard);
//                    model.setPwayAccount(pwayAccount);
//                    model.setPwayCard(pwayCash);
//                    model.setOpenMon(openMon);
//                    model.setOpenTue(openTue);
//                    model.setOpenWed(openWed);
//                    model.setOpenThu(openThu);
//                    model.setOpenFri(openFri);
//                    model.setOpenSat(openSat);
//                    model.setOpenSun(openSun);
//                    model.setCategory(category);
//                    model.setStoreImageUri(storeImageUri);
//                    model.setMenuImageUri(menuImageUri);
//                    model.setVerified(isVerified);
//                    model.setHasMeeting(hasMeeting);
//                    model.setRating(rating);
//                    model.setGeohash(geohash);
//                    model.setExteriorImagePath(exteriorImagePath);
//
////                    model.setShopID_reviewID(allReviewDatas.get(i).getShopID_reviewID());
////                    model.setPicUrl1(allReviewDatas.get(i).getPicUrl1());
////                    model.setRating(allReviewDatas.get(i).getRating());
////                    model.setSummary(allReviewDatas.get(i).getSummary());
//
//                    myShopDatas.add(model);
////                    Log.d("테스트MyReviewList", "myShopDatas.add(model) : " + allDatas.size());
////                    Log.d("테스트MyReviewList", "myShopDatas.add(model) : " + allDatas.get(i).getShopName());
////                    Log.d("테스트MyReviewList", "myReviewDatas.add(model) : " + allDatas.get(i).getSummary());
////                    i++;
//                    Log.d("테스트MyReviewListThread", "onDataChange: Thread" + myShopDatas.size());
//                }
////                        }
//
//                // thirdShopRef
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//            Log.d("테스트MyReviewList", "thirdShopRef onDataChange: for 문");
//            i++;
//        }   //thirdShopRef for문
//
//    }
//}
