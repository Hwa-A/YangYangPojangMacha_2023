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

public class ZeroReviewList extends FirstReviewList {

    private ArrayList<MyReviewModel> shopDatas = new ArrayList<>(); // 가게 정보 전송용

    public void getZeroReviewList(String UID, final ZeroDataLoadedCallback zeroDataLoadedCallback) {
        FirstReviewList firstReviewList = new FirstReviewList();

        firstReviewList.getFirstReviewList(UID, new FirstReviewList.FirstDataLoadedCallback() {

            @Override
            public void onFirstDataLoaded(final ArrayList<MyReviewModel> reviewDatas, final ArrayList selectShop, String UID) {
                // selectShop 목록에서 리뷰 정보들 가져오기
                for (Object shopLink : selectShop){
                    DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference("shops").child(shopLink.toString());

                    shopRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                 MyReviewModel model = new MyReviewModel();
                                Log.d("프로필reviewGetList", "ZeroReviewList: 가게 정보 가져오기 ");
                                String category = snapshot.child("category").getValue(String.class);
                                String addressName = snapshot.child("addressName").getValue(String.class);
                                String shopName = snapshot.child("shopName").getValue(String.class);
                                String exteriorImagePath = snapshot.child("exteriorImagePath").getValue(String.class);
                                String geohash = snapshot.child("geohash").getValue(String.class);
                                boolean hasMeeting = snapshot.child("hasMeeting").getValue(boolean.class);
                                double latitude = snapshot.child("latitude").getValue(double.class);
                                double longitude = snapshot.child("longitude").getValue(double.class);
                                String menuImageUri = snapshot.child("menuImageUri").getValue(String.class);
                                boolean openFri = snapshot.child("openFri").getValue(boolean.class);
                                boolean openMon = snapshot.child("openMon").getValue(boolean.class);
                                boolean openSat = snapshot.child("openSat").getValue(boolean.class);
                                boolean openSun = snapshot.child("openSun").getValue(boolean.class);
                                boolean openThu = snapshot.child("openThu").getValue(boolean.class);
                                boolean openTue = snapshot.child("openTue").getValue(boolean.class);
                                boolean openWed = snapshot.child("openWed").getValue(boolean.class);
                                boolean pwayAccount = snapshot.child("pwayAccount").getValue(boolean.class);
                                boolean pwayCard = snapshot.child("pwayCard").getValue(boolean.class);
                                boolean pwayCash = snapshot.child("pwayCash").getValue(boolean.class);
                                boolean pwayMobile = snapshot.child("pwayMobile").getValue(boolean.class);
                                float rating = snapshot.child("rating").getValue(float.class);
                                String storeImageUri = snapshot.child("storeImageUri").getValue(String.class);
                                String uid = snapshot.child("uid").getValue(String.class);
                                boolean isVerified = snapshot.child("verified").getValue(boolean.class);

                                model.MyReviewModel(uid, shopName, latitude, longitude, addressName, pwayMobile, pwayCard,
                                        pwayAccount, pwayCash, openMon, openTue,
                                        openWed, openThu, openFri, openSat,
                                        openSun, category, storeImageUri, menuImageUri,
                                        isVerified, hasMeeting, rating, geohash, exteriorImagePath);

                                shopDatas.add(model);
                            }
                            zeroDataLoadedCallback.onZeroDataLoaded(reviewDatas, shopDatas);

                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }



            }
        });


    }

    public interface ZeroDataLoadedCallback {
        void onZeroDataLoaded(ArrayList<MyReviewModel> reviewDatas, ArrayList<MyReviewModel> shopDatas);
    }
}



