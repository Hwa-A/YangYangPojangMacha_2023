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

public class MyReviewList implements SecondDataLoadedCallback {
    private ArrayList<MyReviewModel> shopDatas = new ArrayList<>(); // 가게 정보 전송용

    DataLoadedCallback dataLoadedCallback;

    private String UID;
    int i = 0;


    @Override
    public void onSecondDataLoaded(ArrayList<MyReviewModel> reviewDatas, ArrayList selectShop, String UID) {

        for (Object shopLink : selectShop) {
            Log.d("프로필reviewGetList", "selectShop.size() : " + selectShop.size());
            DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference("shops/" + shopLink);//.child(shopLink.toString());

            shopRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if (snapshot.exists()) {
                    MyReviewModel model = new MyReviewModel();
                    Log.d("프로필reviewGetList", "ZeroReviewList: 가게 정보 가져오기 ");
                    String category = snapshot.child("category").getValue(String.class);
                    String addressName = snapshot.child("addressName").getValue(String.class);
                    String shopName = snapshot.child("shopName").getValue(String.class);
                    String exteriorImagePath = snapshot.child("exteriorImagePath").getValue(String.class);
                    String geohash = snapshot.child("geohash").getValue(String.class);
                    boolean hasMeeting = snapshot.child("hasMeeting").getValue(boolean.class);
                    double latitude = snapshot.child("latitude").getValue(Double.class);
                    double longitude = snapshot.child("longitude").getValue(Double.class);
                    String menuImageUri = snapshot.child("menuImageUri").getValue(String.class);
                    boolean openFri = snapshot.child("openFri").getValue(Boolean.class);
                    boolean openMon = snapshot.child("openMon").getValue(Boolean.class);
                    boolean openSat = snapshot.child("openSat").getValue(Boolean.class);
                    boolean openSun = snapshot.child("openSun").getValue(Boolean.class);
                    boolean openThu = snapshot.child("openThu").getValue(Boolean.class);
                    boolean openTue = snapshot.child("openTue").getValue(Boolean.class);
                    boolean openWed = snapshot.child("openWed").getValue(Boolean.class);
                    boolean pwayAccount = snapshot.child("pwayAccount").getValue(Boolean.class);
                    boolean pwayCard = snapshot.child("pwayCard").getValue(Boolean.class);
                    boolean pwayCash = snapshot.child("pwayCash").getValue(Boolean.class);
                    boolean pwayMobile = snapshot.child("pwayMobile").getValue(Boolean.class);
                    float rating = snapshot.child("rating").getValue(Float.class);
                    String storeImageUri = snapshot.child("storeImageUri").getValue(String.class);
                    String uid = snapshot.child("uid").getValue(String.class);
                    boolean isVerified = snapshot.child("verified").getValue(Boolean.class);

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

                    model.setShopID_reviewID(reviewDatas.get(i).getShopID_reviewID());
                    model.setPicUrl1(reviewDatas.get(i).getPicUrl1());
                    model.setRating(reviewDatas.get(i).getRating());
                    model.setSummary(reviewDatas.get(i).getSummary());


                    Log.d("프로필reviewGetList", "reviewDatas: " + reviewDatas.size());


                    Log.d("프로필reviewGetList", "shopDatas: " + shopDatas.size());
                    //Log.d("프로필reviewGetList", "shopDatas: " + shopDatas.get(0).getShopName());
                    //Log.d("프로필reviewGetList", "shopDatas: " + shopDatas.get(1).getShopName());
                    shopDatas.add(model);
                    Log.d("프로필reviewGetList", "ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ" + model.getSummary());
                    Log.d("프로필reviewGetList", "ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ" + model.getShopName());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
gi t

        }

        


    }

}
interface DataLoadedCallback {

    void onDataLoaded(ArrayList<MyReviewModel> shopDatas);
}


