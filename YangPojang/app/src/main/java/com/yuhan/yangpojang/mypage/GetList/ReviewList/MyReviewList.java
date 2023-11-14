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

public class MyReviewList {
    private ArrayList<MyReviewModel> shopDatas = new ArrayList<>(); // 가게 정보 전송용


    private String UID;
//    int i = 0;

    public void getReviewItemInfo(String UID, final MyReviewList.dataLoadedCallback callback) {

        new SecondReviewList(UID, new SecondReviewList.SecondDataLoadedCallback() {
            @Override
            public void onSecondDataLoaded(ArrayList<MyReviewModel> reviewDatas, ArrayList selectShop, String UID) {
                Log.d("프로필reviewGetList", "onDataChange: selectShop 경로 :" + selectShop);
//
//                        Log.d("프로필reviewGetList", "shopLink : " + shopLink.toString());
//                        Log.d("프로필reviewGetList", "selectShop.size() : " + selectShop.size());
//                        Log.d("프로필reviewGetList", "selectShop.size() : " + selectShop.get(i) + " / " + i);

                DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference("shops/");//.child(shopLink.toString());
                Log.d("프로필reviewGetList", "selectShop.size() : " + shopRef);
                shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        shopDatas.clear();

                        for (int i = 0; i < reviewDatas.size(); i++) {
                            String shopLink = selectShop.get(i).toString();

                            MyReviewModel model = new MyReviewModel();
                            Log.d("프로필reviewGetList", "ZeroReviewList: 가게 정보 가져오기 ");
                            String category = snapshot.child(shopLink).child("category").getValue(String.class);
                            String addressName = snapshot.child(shopLink).child("addressName").getValue(String.class);
                            String shopName = snapshot.child(shopLink).child("shopName").getValue(String.class);
                            String exteriorImagePath = snapshot.child(shopLink).child("exteriorImagePath").getValue(String.class);
                            String geohash = snapshot.child(shopLink).child("geohash").getValue(String.class);
                            boolean hasMeeting = snapshot.child(shopLink).child("hasMeeting").getValue(boolean.class);
                            double latitude = snapshot.child(shopLink).child("latitude").getValue(Double.class);
                            double longitude = snapshot.child(shopLink).child("longitude").getValue(Double.class);
                            String menuImageUri = snapshot.child(shopLink).child("menuImageUri").getValue(String.class);
                            boolean openFri = snapshot.child(shopLink).child("openFri").getValue(Boolean.class);
                            boolean openMon = snapshot.child(shopLink).child("openMon").getValue(Boolean.class);
                            boolean openSat = snapshot.child(shopLink).child("openSat").getValue(Boolean.class);
                            boolean openSun = snapshot.child(shopLink).child("openSun").getValue(Boolean.class);
                            boolean openThu = snapshot.child(shopLink).child("openThu").getValue(Boolean.class);
                            boolean openTue = snapshot.child(shopLink).child("openTue").getValue(Boolean.class);
                            boolean openWed = snapshot.child(shopLink).child("openWed").getValue(Boolean.class);
                            boolean pwayAccount = snapshot.child(shopLink).child("pwayAccount").getValue(Boolean.class);
                            boolean pwayCard = snapshot.child(shopLink).child("pwayCard").getValue(Boolean.class);
                            boolean pwayCash = snapshot.child(shopLink).child("pwayCash").getValue(Boolean.class);
                            boolean pwayMobile = snapshot.child(shopLink).child("pwayMobile").getValue(Boolean.class);
                            float rating = snapshot.child(shopLink).child("rating").getValue(Float.class);
                            String storeImageUri = snapshot.child(shopLink).child("storeImageUri").getValue(String.class);
                            String uid = snapshot.child(shopLink).child("uid").getValue(String.class);
                            boolean isVerified = snapshot.child(shopLink).child("verified").getValue(Boolean.class);
                            String primaryKey = shopLink;
                            String shopKey = shopLink;
                            String shopID_reviewID = reviewDatas.get(i).getShopID_reviewID();


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
                            model.setPrimaryKey(primaryKey);
                            model.setShopkey(shopKey);
                            model.setShopID_reviewID(shopID_reviewID);

                            model.setShopID_reviewID(reviewDatas.get(i).getShopID_reviewID());

                            model.setPicUrl1(reviewDatas.get(i).getPicUrl1());
                            model.setPicUrl2(reviewDatas.get(i).getPicUrl2());
                            model.setPicUrl3(reviewDatas.get(i).getPicUrl3());
                            model.setMyRating(reviewDatas.get(i).getMyRating());
                            model.setSummary(reviewDatas.get(i).getSummary());


                            Log.d("프로필reviewGetList", "reviewDatas: " + reviewDatas.size());
                            Log.d("사진 프로필reviewGetList", " model.setPicUrl1(reviewDatas.get(i).getPicUrl1()); " + model.getPicUrl1());
                            Log.d("사진 프로필reviewGetList", " model.setPicUrl1(reviewDatas.get(i).getPicUrl2()); " + model.getPicUrl2());

                            Log.d("프로필reviewGetList", "shopDatas: " + shopDatas.size());
                            //Log.d("프로필reviewGetList", "shopDatas: " + shopDatas.get(0).getShopName());
                            //Log.d("프로필reviewGetList", "shopDatas: " + shopDatas.get(1).getShopName());
                            shopDatas.add(model);
                            Log.d("프로필reviewGetList", "ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ" + model.getSummary());
                            Log.d("프로필reviewGetList", "ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ" + model.getShopName());
                            Log.d("프로필reviewGetList", "onSecondDataLoaded: 사이즈" + shopDatas.size());
                            callback.onDataLoaded(shopDatas);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

    }
    public interface dataLoadedCallback {

        void onDataLoaded(ArrayList<MyReviewModel> shopDatas);
    }
}


